from pydantic import BaseModel, Field, validator
from typing import Optional, List
from datetime import datetime
from config import settings

class NotionConfig(BaseModel):
    notion_token: Optional[str] = Field(None, description="Notion 통합 토큰 (없으면 환경변수 사용)")
    parent_page_id: Optional[str] = Field(None, description="상위 페이지 ID (없으면 환경변수 사용)")
    
    @validator('notion_token', pre=True, always=True)
    def set_notion_token(cls, v):
        if isinstance(v, tuple):
            v = v[0] if v else None
        # 값이 없거나 빈 문자열이면 환경변수 사용
        if not v:
            return settings.notion_token
        return str(v)
    
    @validator('parent_page_id', pre=True, always=True)
    def set_parent_page_id(cls, v):
        if isinstance(v, tuple):
            v = v[0] if v else None
        # 값이 없거나 빈 문자열이면 환경변수 사용
        if not v:
            return settings.parent_page_id
        return str(v) if v else None

class NotionConfigResponse(BaseModel):
    configured: bool
    parent_page_id: Optional[str] = None
    message: str

class NotionPage(BaseModel):
    title: str
    problem: str
    user_solution: str
    ai_solution: str
    comparison: str
    summary: List[str]
    user_complexity: str
    ai_complexity: str
    complexity_analysis: str
    language: str
    created_at: datetime
    tags: List[str] = []

class NotionSaveResponse(BaseModel):
    notion_page_id: str
    notion_url: str
    saved_at: datetime

class NotionApiRequest(BaseModel):
    """Notion API 요청 베이스 모델 (환경변수 fallback 지원)"""
    notion_token: Optional[str] = Field(None, description="Notion 통합 토큰 (없으면 환경변수 사용)")
    parent_page_id: Optional[str] = Field(None, description="상위 페이지 ID (없으면 환경변수 사용)")
    
    @validator('notion_token', pre=True, always=True)
    def set_notion_token(cls, v):
        if isinstance(v, tuple):
            v = v[0] if v else None
        # 값이 없거나 빈 문자열이면 환경변수 사용
        if not v:
            return settings.notion_token
        return str(v) if v else None
    
    @validator('parent_page_id', pre=True, always=True)
    def set_parent_page_id(cls, v):
        if isinstance(v, tuple):
            v = v[0] if v else None
        # 값이 없거나 빈 문자열이면 환경변수 사용
        if not v:
            return settings.parent_page_id
        return str(v) if v else None

class NotionSaveRequest(NotionApiRequest):
    """Notion 페이지 저장 요청"""
    problem: str
    user_solution: str
    ai_solution: str
    comparison: str
    summary: List[str]
    user_complexity: str
    ai_complexity: str
    complexity_analysis: str
    language: str = "python"
    title: Optional[str] = None
    tags: List[str] = []

class NotionQuickSaveRequest(NotionApiRequest):
    """빠른 저장 요청 (AI가 솔루션 생성)"""
    problem: str
    user_solution: str
    language: str = "python"
    title: Optional[str] = None
    tags: List[str] = []

