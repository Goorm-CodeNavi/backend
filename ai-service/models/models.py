from pydantic import BaseModel
from typing import Optional

class ProblemSolveRequest(BaseModel):
    problem: str
    user_solution: str
    language: Optional[str] = "python"

class TimeComplexity(BaseModel):
    ai_solution_complexity: str
    user_solution_complexity: str
    complexity_analysis: str

class ProblemSolveResponse(BaseModel):
    ai_solution: str
    comparison: str
    summary: list[str]
    time_complexity: TimeComplexity
