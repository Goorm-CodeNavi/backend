from pydantic_settings import BaseSettings
from typing import Optional
import os

class Settings(BaseSettings):
    groq_api_key: str = os.environ.get("GROQ_API_KEY")
    host: str = "0.0.0.0"
    port: int = 8000
    debug: bool = True
    
    # class Config:
    #     env_file = ".env"

settings = Settings()