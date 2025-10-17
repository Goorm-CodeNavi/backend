from pydantic_settings import BaseSettings
from typing import Optional

class Settings(BaseSettings):
    groq_api_key: str = "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"
    host: str = "0.0.0.0"
    port: int = 8000
    debug: bool = True
    
    # class Config:
    #     env_file = ".env"

settings = Settings()