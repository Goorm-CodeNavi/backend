from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from api import router
from api.notion_routers import router as notion_router
from config import settings

app = FastAPI(
    title="CodeNavi's Algorithm Problem Solver API",
    description="알고리즘 문제 풀이 및 비교 분석 API",
    version="1.0.0",
    debug=settings.debug
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(router)
app.include_router(notion_router)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app", 
        host=settings.host, 
        port=settings.port, 
        reload=settings.debug
    )