from fastapi import APIRouter, HTTPException
from models.models import ProblemSolveRequest, ProblemSolveResponse
from services import groq_service

router = APIRouter()

@router.get("/", response_model=dict)
async def root():
    return {"message": "CodeNavi's Algorithm Problem Solver API", "status": "running"}

@router.post("/solve-problem", response_model=ProblemSolveResponse)
async def solve_problem(request: ProblemSolveRequest):
    """
    알고리즘 문제를 분석하고 AI 솔루션을 제공하며 사용자 답안과 비교합니다.
    """
    try:
        # 1. AI 솔루션 생성
        ai_solution = await groq_service.generate_ai_solution(request.problem, request.language)
        
        # 2. 사용자 솔루션과 비교 분석
        comparison = await groq_service.compare_solutions(
            request.problem, 
            request.user_solution, 
            ai_solution, 
            request.language
        )
        
        # 3. 풀이 요약 생성
        summary = await groq_service.generate_summary(request.problem, ai_solution)
        
        # 4. 시간복잡도 분석
        time_complexity = await groq_service.analyze_time_complexity(
            request.problem,
            request.user_solution,
            ai_solution,
            request.language
        )
        
        return ProblemSolveResponse(
            ai_solution=ai_solution,
            comparison=comparison,
            summary=summary,
            time_complexity=time_complexity
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"처리 중 오류가 발생했습니다: {str(e)}")
