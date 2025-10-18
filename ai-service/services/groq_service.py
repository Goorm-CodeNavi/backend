from groq import Groq
from config import settings
import json
from models.models import TimeComplexity

class GroqService:
    def __init__(self):
        self.client = Groq(api_key=settings.groq_api_key)
        self.model = "openai/gpt-oss-20b"
    
    async def generate_ai_solution(self, problem: str, language: str) -> str:
        """AI를 이용해 문제에 대한 솔루션을 생성합니다."""
        
        prompt = f"""
        다음 알고리즘 문제를 {language} 언어로 해결해주세요.
        
        문제:
        {problem}
        
        요구사항:
        1. 효율적이고 최적화된 코드를 작성해주세요
        2. 시간복잡도와 공간복잡도를 고려해주세요
        3. 코드에 주석을 포함해주세요
        4. 코드만 반환하고 추가 설명은 제외해주세요
        """
        
        try:
            chat_completion = self.client.chat.completions.create(
                messages=[
                    {"role": "system", "content": "당신은 알고리즘 문제 해결 전문가입니다. 효율적이고 깔끔한 코드를 작성해주세요."},
                    {"role": "user", "content": prompt}
                ],
                model=self.model,
                temperature=0.3,
                max_tokens=2000
            )
            
            return chat_completion.choices[0].message.content.strip()
            
        except Exception as e:
            raise Exception(f"AI 솔루션 생성 중 오류: {str(e)}")

    async def compare_solutions(self, problem: str, user_solution: str, ai_solution: str, language: str) -> str:
        """사용자 솔루션과 AI 솔루션을 비교 분석합니다."""
        
        prompt = f"""
        다음 알고리즘 문제에 대한 두 가지 솔루션을 비교 분석해주세요.

        문제:
        {problem}

        사용자 솔루션:
        {user_solution}

        AI 솔루션:
        {ai_solution}

        다음 관점에서 비교 분석해주세요:
        1. 코드의 정확성 (올바른 결과를 출력하는지)
        2. 코드의 가독성과 구조
        3. 개선점이나 최적화 방안
        4. 장단점 비교

        분석 결과를 명확하고 구체적으로 설명해주세요.
        (시간복잡도 관련 내용은 제외하고 분석해주세요)
        """
        
        try:
            chat_completion = self.client.chat.completions.create(
                messages=[
                    {"role": "system", "content": "당신은 코드 리뷰 전문가입니다. 객관적이고 건설적인 피드백을 제공해주세요."},
                    {"role": "user", "content": prompt}
                ],
                model=self.model,
                temperature=0.4,
                max_tokens=1500
            )
            
            return chat_completion.choices[0].message.content.strip()
            
        except Exception as e:
            raise Exception(f"솔루션 비교 중 오류: {str(e)}")

    async def analyze_time_complexity(self, problem: str, user_solution: str, ai_solution: str, language: str) -> TimeComplexity:
        """사용자 솔루션과 AI 솔루션의 시간복잡도를 분석합니다."""
        
        prompt = f"""
        다음 알고리즘 문제와 두 솔루션의 시간복잡도를 분석해주세요.

        문제:
        {problem}

        사용자 솔루션:
        {user_solution}

        AI 솔루션:
        {ai_solution}

        다음 JSON 형식으로 정확히 반환해주세요:
        {{
            "ai_solution_complexity": "AI 솔루션의 시간복잡도 (예: O(n log n))",
            "user_solution_complexity": "사용자 솔루션의 시간복잡도 (예: O(n²))",
            "complexity_analysis": "두 솔루션의 시간복잡도 비교 및 분석 설명"
        }}

        요구사항:
        - 시간복잡도는 Big O 표기법 사용
        - complexity_analysis에는 어떤 솔루션이 더 효율적인지와 그 이유를 포함
        - 최악의 경우 시간복잡도를 기준으로 분석
        """
        
        try:
            chat_completion = self.client.chat.completions.create(
                messages=[
                    {"role": "system", "content": "당신은 알고리즘 복잡도 분석 전문가입니다. 정확한 시간복잡도를 분석해주세요."},
                    {"role": "user", "content": prompt}
                ],
                model=self.model,
                temperature=0.2,
                max_tokens=800
            )
            
            response_text = chat_completion.choices[0].message.content.strip()
            
            # JSON 파싱 시도
            try:
                complexity_data = json.loads(response_text)
                return TimeComplexity(
                    ai_solution_complexity=complexity_data.get("ai_solution_complexity", "분석 불가"),
                    user_solution_complexity=complexity_data.get("user_solution_complexity", "분석 불가"),
                    complexity_analysis=complexity_data.get("complexity_analysis", "분석 중 오류가 발생했습니다.")
                )
            except json.JSONDecodeError:
                # JSON 파싱 실패 시 기본값 반환
                return TimeComplexity(
                    ai_solution_complexity="분석 불가",
                    user_solution_complexity="분석 불가",
                    complexity_analysis="시간복잡도 분석 중 오류가 발생했습니다. 코드를 수동으로 확인해주세요."
                )
                
        except Exception as e:
            raise Exception(f"시간복잡도 분석 중 오류: {str(e)}")

    async def generate_summary(self, problem: str, ai_solution: str) -> list[str]:
        """문제 풀이에 대한 세 줄 요약을 생성합니다."""
        
        prompt = f"""
        다음 알고리즘 문제와 솔루션에 대해 정확히 3줄로 요약해주세요.

        문제:
        {problem}

        솔루션:
        {ai_solution}

        요구사항:
        - 각 줄은 한 문장으로 작성
        - 1줄: 문제의 핵심과 해결 방법
        - 2줄: 주요 알고리즘이나 자료구조
        - 3줄: 핵심 포인트나 최적화 방법

        JSON 형식으로 반환: ["첫번째 줄", "두번째 줄", "세번째 줄"]
        """
        
        try:
            chat_completion = self.client.chat.completions.create(
                messages=[
                    {"role": "system", "content": "당신은 알고리즘 문제 해설 전문가입니다. 간결하고 명확한 요약을 제공해주세요."},
                    {"role": "user", "content": prompt}
                ],
                model=self.model,
                temperature=0.2,
                max_tokens=500
            )
            
            response_text = chat_completion.choices[0].message.content.strip()
            
            # JSON 파싱 시도
            try:
                summary_list = json.loads(response_text)
                if isinstance(summary_list, list) and len(summary_list) == 3:
                    return summary_list
            except:
                pass
            
            # JSON 파싱에 실패한 경우 텍스트를 줄 단위로 분할
            lines = response_text.split('\n')
            clean_lines = [line.strip().strip('"-') for line in lines if line.strip()]
            
            if len(clean_lines) >= 3:
                return clean_lines[:3]
            else:
                return clean_lines + [""] * (3 - len(clean_lines))
                
        except Exception as e:
            raise Exception(f"요약 생성 중 오류: {str(e)}")

groq_service = GroqService()