from pydantic_settings import BaseSettings
from typing import Optional
import os

class Settings(BaseSettings):
    groq_api_key: str = os.environ.get("GROQ_API_KEY")
    notion_token: str = os.environ.get("NOTION_TOKEN")
    parent_page_id: str = os.environ.get("PARENT_PAGE_ID")
    
    host: str = "0.0.0.0"
    port: int = 8000
    debug: bool = True

    mcp_port: int = 8001
    mcp_enabled: bool = True
    
    # class Config:
    #     env_file = ".env"

settings = Settings()