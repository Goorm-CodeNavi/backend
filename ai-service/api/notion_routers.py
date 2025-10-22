from fastapi import APIRouter, HTTPException
from models.notion_models import (
    NotionConfig, NotionConfigResponse, NotionSaveResponse, 
    NotionSaveRequest, NotionQuickSaveRequest, NotionPage
)
from services.notion_service import notion_service
from services.groq_service import groq_service
from datetime import datetime

router = APIRouter(prefix="/notion", tags=["Notion Integration"])

@router.post("/test-connection", response_model=NotionConfigResponse)
async def test_connection(config: NotionConfig):
    """Notion 연결을 테스트합니다."""
    try:
        result = await notion_service.test_connection(
            config.notion_token, 
            config.parent_page_id
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"연결 테스트 실패: {str(e)}")

@router.post("/save", response_model=NotionSaveResponse)
async def save_to_notion(request: NotionSaveRequest):
    """문제와 솔루션을 Notion 페이지로 저장합니다."""
    try:
        notion_page = NotionPage(
            title=request.title or f"Algorithm - {datetime.now().strftime('%Y%m%d_%H%M%S')}",
            problem=request.problem,
            user_solution=request.user_solution,
            ai_solution=request.ai_solution,
            comparison=request.comparison,
            summary=request.summary,
            user_complexity=request.user_complexity,
            ai_complexity=request.ai_complexity,
            complexity_analysis=request.complexity_analysis,
            language=request.language,
            created_at=datetime.now(),
            tags=request.tags
        )
        
        result = await notion_service.save_problem_solution(
            notion_page,
            request.notion_token,
            request.parent_page_id
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"저장 실패: {str(e)}")

@router.post("/quick-save")
async def quick_save(request: NotionQuickSaveRequest):
    """문제를 해결하고 바로 Notion에 저장합니다."""
    try:
        # 1. AI로 문제 해결
        ai_solution = await groq_service.generate_ai_solution(request.problem, request.language)
        comparison = await groq_service.compare_solutions(request.problem, request.user_solution, ai_solution, request.language)
        summary = await groq_service.generate_summary(request.problem, ai_solution)
        time_complexity = await groq_service.analyze_time_complexity(request.problem, request.user_solution, ai_solution, request.language)
        
        # 2. Notion 페이지 생성
        notion_page = NotionPage(
            title=request.title or f"Quick Save - {datetime.now().strftime('%Y%m%d_%H%M%S')}",
            problem=request.problem,
            user_solution=request.user_solution,
            ai_solution=ai_solution,
            comparison=comparison,
            summary=summary,
            user_complexity=time_complexity.user_solution_complexity,
            ai_complexity=time_complexity.ai_solution_complexity,
            complexity_analysis=time_complexity.complexity_analysis,
            language=request.language,
            created_at=datetime.now(),
            tags=request.tags
        )
        
        # 3. Notion에 저장
        notion_result = await notion_service.save_problem_solution(
            notion_page,
            request.notion_token,
            request.parent_page_id
        )
        
        return {
            "success": True,
            "solution": {
                "ai_solution": ai_solution,
                "comparison": comparison,
                "summary": summary,
                "time_complexity": {
                    "user": time_complexity.user_solution_complexity,
                    "ai": time_complexity.ai_solution_complexity,
                    "analysis": time_complexity.complexity_analysis
                }
            },
            "notion": notion_result,
            "message": "해결 및 저장 완료"
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"처리 실패: {str(e)}")