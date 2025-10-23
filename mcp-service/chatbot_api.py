import os
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import httpx
from dotenv import load_dotenv
from typing import Optional, Dict, Any, List
import uuid
from datetime import datetime
import re

load_dotenv()

app = FastAPI(title="CodeNavi ChatBot API", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


MCP_API = "http://mcp-tools:8001"
AI_API = "http://ai-service:8000"

# 세션 저장소
sessions: Dict[str, Dict[str, Any]] = {}

class ChatRequest(BaseModel):
    message: str
    session_id: Optional[str] = None

class ChatResponse(BaseModel):
    session_id: str
    step: str
    response: str
    status: str
    data: Optional[Dict[str, Any]] = None
    options: Optional[List[str]] = None
    require_input: bool = True
    notion_saved: bool = False
    notion_url: Optional[str] = None
    analysis_result: Optional[Dict[str, Any]] = None

# MCP 도구 호출 
async def call_mcp_tool(tool_name: str, parameters: Dict[str, Any]) -> Dict[str, Any]:
    """MCP 도구 서버 호출"""
    try:
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{MCP_API}/api/tools/call",
                json={
                    "tool_name": tool_name,
                    "parameters": parameters
                }
            )
            result = response.json()
            if result.get("success"):
                return result.get("result", {})
            else:
                raise Exception(result.get("error", "Tool call failed"))
    except Exception as e:
        raise Exception(f"MCP tool error: {str(e)}")

# 챗봇 엔드포인트
@app.post("/api/chat", response_model=ChatResponse)
async def chat_endpoint(request: ChatRequest):
    """프론트엔드와 대화하는 메인 엔드포인트"""
    session_id = request.session_id or str(uuid.uuid4())
    message = request.message.strip()
    
    # 세션이 없으면 새로 시작
    if session_id not in sessions:
        sessions[session_id] = {
            "step": "idle",
            "data": {},
            "created_at": datetime.now().isoformat()
        }
    
    session = sessions[session_id]
    current_step = session["step"]
    
    # 명령어 처리
    if message.lower() in ["시작", "start", "새로시작", "new", "/start"]:
        session["step"] = "waiting_problem"
        session["data"] = {}
        return ChatResponse(
            session_id=session_id,
            step="waiting_problem",
            response="""🚀 **코딩 문제 분석을 시작합니다!**

            📝 먼저 해결하려는 **문제를 설명**해주세요.

            예시:
            - "문자열을 거꾸로 뒤집어서 출력하는 문제"
            - "정수 배열에서 두 수의 합이 타겟이 되는 인덱스 찾기"
            - "주어진 이진트리의 깊이를 구하는 문제"

            💡 문제를 구체적으로 설명할수록 더 정확한 분석이 가능합니다!""",
            status="success",
            require_input=True
        )
    
    # 단계별 처리
    if current_step == "idle":
        try:
            examples = await call_mcp_tool("get_problem_examples", {"difficulty": "easy"})
        except:
            examples = None
            
        return ChatResponse(
            session_id=session_id,
            step="idle",
            response="""👋 안녕하세요! **CodeNavi AI Assistant**입니다.

            저는 여러분의 코딩 문제를 분석하고, 
            자동으로 Notion에 정리해드리는 똑똑한 도우미예요! 🤖

            **🎯 제가 도와드릴 수 있는 것:**
            - 💬 코드 정확성 검증
            - 🔍 시간/공간 복잡도 분석  
            - 📝 개선점 제안
            - 📑 Notion에 자동 문서화
            - 🎨 깔끔한 비교 분석표

            시작하려면 **'시작'**이라고 입력해주세요!""",
            status="waiting",
            options=["시작", "도움말", "예제 보기"],
            require_input=True
        )
    
    elif current_step == "waiting_problem":
        if len(message) < 10:
            return ChatResponse(
                session_id=session_id,
                step="waiting_problem",
                response="❌ 문제 설명이 너무 짧습니다. 좀 더 자세히 설명해주세요.",
                status="error",
                require_input=True
            )
        
        session["data"]["problem"] = message
        session["step"] = "waiting_code"
        
        return ChatResponse(
            session_id=session_id,
            step="waiting_code",
            response=f"""✅ **문제를 저장했습니다!**

            📋 입력된 문제:
            > {message[:150]}{'...' if len(message) > 150 else ''}

            💻 이제 **작성한 솔루션 코드**를 입력해주세요.

            **입력 방법**: 
            - 코드를 그대로 붙여넣으세요
            - 여러 줄 코드도 한 번에 입력 가능합니다
            - 코드 블록(```)은 자동으로 처리됩니다""",
            status="success",
            data={"problem": message},
            require_input=True
        )
    
    elif current_step == "waiting_code":
        # 코드 블록 정리
        code = clean_code_block(message)
        
        if len(code.strip()) < 10:
            return ChatResponse(
                session_id=session_id,
                step="waiting_code",
                response="❌ 코드가 너무 짧습니다. 실제 솔루션 코드를 입력해주세요.",
                status="error",
                require_input=True
            )
        
        session["data"]["user_solution"] = code
        session["step"] = "waiting_language"
        
        # MCP 도구로 언어 감지
        try:
            lang_result = await call_mcp_tool("detect_language", {"code": code})
            detected_lang = lang_result.get("detected_language", "unknown")
            code_stats = lang_result.get("code_stats", {"lines": 0})
        except:
            detected_lang = "unknown"
            code_stats = {"lines": len(code.splitlines())}
        
        return ChatResponse(
            session_id=session_id,
            step="waiting_language",
            response=f"""✅ **코드를 저장했습니다!**

            📄 코드 정보:
            - 줄 수: {code_stats['lines']}줄
            - 문자 수: {len(code)}자
            - 예상 언어: {detected_lang.upper() if detected_lang != 'unknown' else '감지 실패'}

            🔤 **프로그래밍 언어를 선택해주세요:**

            1️⃣ Python
            2️⃣ Java  
            3️⃣ C++
            4️⃣ JavaScript

            번호나 언어명을 입력하세요.""",
            status="success",
            data={
                "code_lines": code_stats['lines'],
                "detected_language": detected_lang,
                "code_preview": code[:200] + "..." if len(code) > 200 else code
            },
            options=["Python", "Java", "C++", "JavaScript"],
            require_input=True
        )
    
    elif current_step == "waiting_language":
        language_map = {
            "python": "python", "1": "python", "파이썬": "python",
            "java": "java", "2": "java", "자바": "java",
            "c++": "cpp", "cpp": "cpp", "3": "cpp", "씨플플": "cpp",
            "javascript": "javascript", "js": "javascript", "4": "javascript", "자바스크립트": "javascript"  
        }
        
        language = language_map.get(message.lower())
        if not language:
            return ChatResponse(
                session_id=session_id,
                step="waiting_language",
                response="❌ 올바른 언어를 선택해주세요. (1-4 번호 또는 언어명)",
                status="error",
                options=["Python", "Java", "C++", "JavaScript"],
                require_input=True
            )
        
        session["data"]["language"] = language
        session["step"] = "confirm"
        
        # 요약 생성
        summary = generate_summary(session["data"])
        
        return ChatResponse(
            session_id=session_id,
            step="confirm",
            response=f"""📊 **입력 내용 확인**

            ✅ 모든 정보를 받았습니다! 다음 내용으로 분석을 진행합니다:

            **📝 문제:**
            {summary['problem']}

            **💻 언어:** {summary['language_display']}

            **📄 코드:** ({summary['code_lines']}줄)
            ```{language}
            {summary['code_preview']}
            ```

            **🤖 AI 분석을 시작하면:**
            1. 코드 정확성 검증
            2. AI 솔루션 생성 및 비교
            3. 시간/공간 복잡도 분석
            4. 개선점 제안
            5. **Notion에 자동 저장** 📑

            **분석을 시작할까요?** (약 10-30초 소요)""",
            status="ready",
            data=summary,
            options=["분석 시작", "취소"],
            require_input=True
        )
    
    elif current_step == "confirm":
        if message.lower() in ["분석", "분석 시작", "시작", "yes", "y", "네", "예", "확인"]:
            session["step"] = "analyzing"
            
            # MCP 도구로 분석 실행
            try:
                result = await call_mcp_tool("analyze_problem", {
                    "problem": session["data"]["problem"],
                    "user_solution": session["data"]["user_solution"],
                    "language": session["data"]["language"]
                })
                
                # 세션 정리
                del sessions[session_id]
                
                if result.get("success"):
                    # 분석 결과 포맷팅
                    analysis_summary = format_analysis_result(result)
                    
                    return ChatResponse(
                        session_id=session_id,
                        step="completed",
                        response=f"""🎉 **분석이 완료되었습니다!**

                        {analysis_summary}

                        📑 **Notion 페이지가 생성되었습니다!**
                        🔗 **링크**: {result.get('notion_url', 'URL을 가져올 수 없습니다')}

                        **💡 Notion 페이지에서 확인할 수 있는 내용:**
                        - 📊 코드 정확성 비교표
                        - ⏱️ 시간/공간 복잡도 분석
                        - 🔍 상세한 코드 비교
                        - 💡 개선점 및 최적화 제안
                        - 📚 관련 알고리즘 설명

                        새로운 문제를 분석하려면 **'시작'**을 입력하세요!""",
                        status="completed",
                        data={
                            "saved_at": result.get("saved_at"),
                            "page_id": result.get("page_id")
                        },
                        notion_saved=True,
                        notion_url=result.get("notion_url"),
                        analysis_result=result,
                        require_input=False
                    )
                else:
                    return ChatResponse(
                        session_id=session_id,
                        step="error",
                        response=f"❌ 분석 실패: {result.get('error', '알 수 없는 오류')}",
                        status="error",
                        require_input=True
                    )
            except Exception as e:
                del sessions[session_id]
                return ChatResponse(
                    session_id=session_id,
                    step="error",
                    response=f"❌ 분석 중 오류가 발생했습니다: {str(e)}",
                    status="error",
                    require_input=True
                )
        else:
            del sessions[session_id]
            return ChatResponse(
                session_id=session_id,
                step="cancelled",
                response="❌ 분석을 취소했습니다. L로 시작하려면 **'시작'**을 입력하세요.",
                status="cancelled",
                require_input=False
            )
    
    # 도움말 처리
    if message.lower() in ["도움말", "help", "/help", "?"]:
        return await show_help(session_id, current_step)
    
    # 예제 보기
    if message.lower() in ["예제", "예제 보기", "examples"]:
        return await show_examples(session_id, current_step)
    
    return ChatResponse(
        session_id=session_id,
        step="error",
        response="🤔 이해하지 못했습니다. **'시작'** 또는 **'도움말'**을 입력해주세요.",
        status="error",
        options=["시작", "도움말"],
        require_input=True
    )

# 헬퍼 함수들
def clean_code_block(code: str) -> str:
    """코드 블록 정리"""
    # 코드 블록 마커 제거
    code = re.sub(r'^```\w*\n?', '', code.strip())
    code = re.sub(r'\n?```$', '', code)
    return code.strip()

def generate_summary(data: dict) -> dict:
    """입력 데이터 요약 생성"""
    problem_preview = data["problem"][:200] + "..." if len(data["problem"]) > 200 else data["problem"]
    code_lines = data["user_solution"].strip().split('\n')
    
    # 코드 미리보기 (처음 10줄)
    if len(code_lines) > 10:
        code_preview = '\n'.join(code_lines[:10]) + f"\n... (총 {len(code_lines)}줄)"
    else:
        code_preview = data["user_solution"]
    
    language_display_map = {
        "python": "Python 🐍",
        "java": "Java ☕",
        "cpp": "C++ 🔧",
        "javascript": "JavaScript 🌐"
    }
    
    return {
        "problem": problem_preview,
        "language": data["language"],
        "language_display": language_display_map.get(data["language"], data["language"]),
        "code_preview": code_preview,
        "code_lines": len(code_lines),
        "code_size": len(data["user_solution"])
    }

def format_analysis_result(result: dict) -> str:
    """분석 결과를 읽기 쉬운 형태로 포맷팅"""
    summary_points = result.get("summary", [])
    time_complexity = result.get("time_complexity", {})
    
    # 요약 포인트 포맷팅
    summary_text = ""
    if summary_points:
        summary_text = "\n".join([f"• {point}" for point in summary_points[:3]])
    
    # 시간 복잡도 정보
    complexity_text = ""
    if time_complexity:
        if isinstance(time_complexity, dict) and time_complexity.get("ai"):
            complexity_text = f"\n\n⏱️ **시간 복잡도**: {time_complexity['ai']}"
    
    return f"""📊 **분석 요약**

        {summary_text}{complexity_text}

        ✅ **주요 특징**:
        • 코드 정확성 검증 완료
        • AI 솔루션과 비교 분석 완료
        • 개선점 및 최적화 방안 제시"""

async def show_help(session_id: str, current_step: str) -> ChatResponse:
    """도움말 표시"""
    return ChatResponse(
        session_id=session_id,
        step=current_step,
        response="""📚 **CodeNavi 사용 가이드**

        **🎯 주요 명령어:**
        - `시작` - 새 분석 시작
        - `도움말` - 이 메시지 표시
        - `예제 보기` - 예제 문제 확인

        **📝 분석 프로세스:**
        1. 문제 설명 입력
        2. 솔루션 코드 입력  
        3. 프로그래밍 언어 선택
        4. AI 분석 실행
        5. Notion 자동 저장

        **✨ 분석 내용:**
        - 🎯 코드 정확성 검증
        - 🤖 AI 솔루션과 비교
        - ⏱️ 시간/공간 복잡도
        - 💡 개선점 제안
        - 📊 상세 비교표

        **💾 Notion 저장:**
        - 자동으로 페이지 생성
        - 체계적인 문서 구조
        - 공유 가능한 링크
        - 영구 보관

        **💡 팁:**
        - 문제는 구체적으로 설명할수록 좋아요
        - 코드는 전체를 붙여넣으세요
        - 분석은 10-30초 정도 걸려요""",
        status="info",
        require_input=True
    )

async def show_examples(session_id: str, current_step: str) -> ChatResponse:
    """예제 문제 표시"""
    try:
        # MCP 도구 호출
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{MCP_API}/api/tools/call",
                json={
                    "tool_name": "get_problem_examples",
                    "parameters": {"difficulty": "easy"}
                }
            )
            result = response.json()
            
            if result.get("success"):
                examples = result["result"]["problems"]
                examples_text = "\n\n".join([
                    f"**{i+1}. {ex['title']}**\n{ex['problem']}\n예시: {ex['example']}"
                    for i, ex in enumerate(examples)
                ])
                
                return ChatResponse(
                    session_id=session_id,
                    step=current_step,
                    response=f"""📚 **예제 문제들**

                    {examples_text}

                    이 문제들로 연습해보세요! 
                    분석을 시작하려면 **'시작'**을 입력하세요.""",
                    status="info",
                    require_input=True
                )
    except:
        pass
    
    return ChatResponse(
        session_id=session_id,
        step=current_step,
        response="❌ 예제를 가져올 수 없습니다. **'시작'**을 입력해 바로 시작하세요!",
        status="error",
        require_input=True
    )

# 추가 API 엔드포인트들
@app.get("/api/chat/status/{session_id}")
async def get_session_status(session_id: str):
    """세션 상태 확인"""
    if session_id in sessions:
        session = sessions[session_id]
        return {
            "exists": True,
            "step": session["step"],
            "has_data": bool(session["data"]),
            "created_at": session.get("created_at"),
            "data_collected": {
                "problem": "problem" in session["data"],
                "code": "user_solution" in session["data"],
                "language": "language" in session["data"]
            }
        }
    return {"exists": False}

@app.delete("/api/chat/session/{session_id}")
async def clear_session(session_id: str):
    """세션 삭제"""
    if session_id in sessions:
        del sessions[session_id]
        return {"message": "세션이 삭제되었습니다.", "success": True}
    return {"message": "세션을 찾을 수 없습니다.", "success": False}

@app.get("/api/health")
async def health_check():
    """헬스 체크"""
    # MCP 서비스 상태 확인
    mcp_status = "unknown"
    try:
        async with httpx.AsyncClient() as client:
            response = await client.get(f"{MCP_API}/api/health", timeout=5.0)
            if response.status_code == 200:
                mcp_status = "healthy"
            else:
                mcp_status = "unhealthy"
    except:
        mcp_status = "unreachable"
    
    return {
        "status": "healthy",
        "service": "CodeNavi ChatBot",
        "sessions_active": len(sessions),
        "mcp_service_status": mcp_status,
        "version": "1.0.0"
    }

@app.get("/api/chat/sessions")
async def list_sessions():
    """활성 세션 목록 (디버깅용)"""
    return {
        "count": len(sessions),
        "sessions": [
            {
                "session_id": sid,
                "step": session["step"],
                "created_at": session.get("created_at"),
                "has_problem": "problem" in session["data"],
                "has_code": "user_solution" in session["data"]
            }
            for sid, session in sessions.items()
        ]
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8002)