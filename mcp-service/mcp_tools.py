import os
from fastmcp import FastMCP
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import httpx
from dotenv import load_dotenv
from typing import Optional, Dict, Any
from datetime import datetime
import uvicorn

load_dotenv()

mcp = FastMCP("codenavi-assistant")

app = FastAPI(title="CodeNavi MCP API", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

AI_API = "http://ai-service:8000"

# MCP 도구들 정의
@mcp.tool()
async def analyze_problem(
    problem: str,
    user_solution: str,
    language: str
) -> Dict[str, Any]:
    """
    코딩 문제와 솔루션을 분석하고 Notion에 저장합니다
    
    Args:
        problem: 해결하려는 코딩 문제
        user_solution: 사용자가 작성한 솔루션 코드
        language: 프로그래밍 언어 (python, java, cpp, javascript)
    
    Returns:
        분석 결과와 Notion URL
    """
    try:
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{AI_API}/notion/quick-save",
                json={
                    "problem": problem,
                    "user_solution": user_solution,
                    "language": language
                },
                timeout=60.0
            )
            
            if response.status_code == 200:
                result = response.json()
                
                if result.get("success"):
                    solution_data = result.get("solution", {})
                    notion_data = result.get("notion", {})
                    
                    return {
                        "success": True,
                        "notion_url": notion_data.get("notion_url", ""),
                        "summary": solution_data.get("summary", []),
                        "ai_solution": solution_data.get("ai_solution", ""),
                        "time_complexity": solution_data.get("time_complexity", {}),
                        "saved_at": notion_data.get("saved_at", "")
                    }
                else:
                    return {
                        "success": False,
                        "error": result.get("message", "분석 실패")
                    }
            else:
                return {
                    "success": False,
                    "error": f"HTTP {response.status_code}"
                }
                
    except Exception as e:
        return {
            "success": False,
            "error": str(e)
        }

@mcp.tool()
async def detect_language(code: str) -> Dict[str, Any]:
    """
    코드에서 프로그래밍 언어를 자동으로 감지합니다
    
    Args:
        code: 분석할 코드
    
    Returns:
        감지된 언어와 신뢰도
    """
    code_lower = code.lower()
    
    # 언어별 키워드 매칭
    if any(kw in code for kw in ["def ", "import ", "print(", "elif ", "from "]):
        language = "python"
        confidence = 0.9
    elif any(kw in code for kw in ["public class", "System.out.println", "public static"]):
        language = "java"
        confidence = 0.9
    elif any(kw in code for kw in ["#include", "cout <<", "std::", "using namespace"]):
        language = "cpp"
        confidence = 0.9
    elif any(kw in code for kw in ["function ", "const ", "=>", "console.log", "let ", "var "]):
        language = "javascript"
        confidence = 0.9
    else:
        language = "unknown"
        confidence = 0.0
    
    return {
        "detected_language": language,
        "confidence": confidence,
        "code_stats": {
            "lines": len(code.splitlines()),
            "characters": len(code)
        }
    }

@mcp.tool()
async def get_problem_examples(difficulty: str = "easy") -> Dict[str, Any]:
    """
    코딩 문제 예시를 제공합니다
    
    Args:
        difficulty: 난이도 (easy, medium, hard)
    
    Returns:
        난이도별 문제 예시 목록
    """
    examples = {
        "easy": [
            {
                "title": "Two Sum",
                "problem": "정수 배열에서 두 수의 합이 타겟이 되는 인덱스를 찾으세요.",
                "example": "nums = [2,7,11,15], target = 9 → [0,1]"
            },
            {
                "title": "Reverse String", 
                "problem": "문자열을 뒤집어서 반환하세요.",
                "example": "hello → olleh"
            }
        ],
        "medium": [
            {
                "title": "Longest Palindrome",
                "problem": "가장 긴 회문 부분 문자열을 찾으세요.",
                "example": "babad → bab 또는 aba"
            }
        ],
        "hard": [
            {
                "title": "Regular Expression Matching",
                "problem": "정규표현식 매칭을 구현하세요.",
                "example": "s = 'aa', p = 'a*' → true"
            }
        ]
    }
    
    return {
        "difficulty": difficulty,
        "problems": examples.get(difficulty, []),
        "count": len(examples.get(difficulty, []))
    }

# REST API 모델
class ToolRequest(BaseModel):
    tool_name: str
    parameters: Dict[str, Any]

class ToolResponse(BaseModel):
    success: bool
    result: Optional[Dict[str, Any]] = None
    error: Optional[str] = None

# MCP 도구들을 직접 참조하는 맵
TOOL_FUNCTIONS = {
    "analyze_problem": analyze_problem,
    "detect_language": detect_language,
    "get_problem_examples": get_problem_examples
}

# REST API 엔드포인트
@app.post("/api/tools/call", response_model=ToolResponse)
async def call_tool(request: ToolRequest):
    """React에서 MCP 도구를 호출하는 엔드포인트"""
    try:
        if request.tool_name not in TOOL_FUNCTIONS:
            return ToolResponse(
                success=False,
                error=f"Unknown tool: {request.tool_name}"
            )
        
        # 도구 실행
        tool_func = TOOL_FUNCTIONS[request.tool_name]
        result = await tool_func(**request.parameters)
        
        # 도구가 자체적으로 success 플래그를 반환하는 경우
        if isinstance(result, dict) and "success" in result and not result["success"]:
            return ToolResponse(
                success=False,
                error=result.get("error", "Tool execution failed")
            )
        
        return ToolResponse(
            success=True,
            result=result
        )
        
    except Exception as e:
        return ToolResponse(
            success=False,
            error=str(e)
        )

@app.get("/api/health")
async def health_check():
    """헬스 체크"""
    # AI 서비스 상태 확인
    ai_status = "unknown"
    try:
        async with httpx.AsyncClient() as client:
            response = await client.get(f"{AI_API}/health", timeout=5.0)
            ai_status = "healthy" if response.status_code == 200 else "unhealthy"
    except:
        ai_status = "unreachable"
    
    return {
        "status": "healthy",
        "service": "CodeNavi MCP Server",
        "ai_service_status": ai_status,
        "available_tools": list(TOOL_FUNCTIONS.keys()),
        "timestamp": datetime.now().isoformat()
    }

@app.get("/api/tools")
async def list_tools():
    """사용 가능한 도구 목록"""
    return {
        "tools": [
            {
                "name": "analyze_problem",
                "description": "코딩 문제와 솔루션을 분석하고 Notion에 저장",
                "parameters": ["problem", "user_solution", "language"]
            },
            {
                "name": "detect_language",
                "description": "코드에서 프로그래밍 언어를 자동으로 감지",
                "parameters": ["code"]
            },
            {
                "name": "get_problem_examples",
                "description": "난이도별 코딩 문제 예시 제공",
                "parameters": ["difficulty"]
            }
        ]
    }

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8001)
    

