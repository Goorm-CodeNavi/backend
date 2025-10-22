from notion_client import Client
from models.notion_models import NotionPage, NotionSaveResponse, NotionConfigResponse
from datetime import datetime
from typing import List, Dict, Optional
import logging
import re

logger = logging.getLogger(__name__)

class NotionService:
    def __init__(self):
        pass
    
    async def test_connection(
        self, 
        notion_token: str, 
        parent_page_id: Optional[str] = None
    ) -> NotionConfigResponse:
        """Notion 연결을 테스트합니다."""
        try:
            client = Client(auth=notion_token)
            
            # 기본 사용자 정보 조회로 토큰 유효성 확인
            user_info = client.users.me()
            
            # 상위 페이지 ID가 있으면 해당 페이지 접근 테스트
            if parent_page_id:
                try:
                    page_info = client.pages.retrieve(parent_page_id)
                    page_title = self._extract_page_title(page_info)
                    
                    return NotionConfigResponse(
                        configured=True,
                        parent_page_id=parent_page_id,
                        message=f"연결 성공: 상위 페이지 '{page_title}'"
                    )
                except Exception as e:
                    return NotionConfigResponse(
                        configured=False,
                        message=f"상위 페이지 접근 실패: {str(e)}"
                    )
            else:
                return NotionConfigResponse(
                    configured=True,
                    parent_page_id=None,
                    message=f"연결 성공: 사용자 {user_info.get('name', 'Unknown')}"
                )
                
        except Exception as e:
            logger.error(f"Notion 연결 테스트 실패: {str(e)}")
            return NotionConfigResponse(
                configured=False,
                message=f"연결 실패: {str(e)}"
            )
    
    async def save_problem_solution(
        self, 
        notion_page: NotionPage,
        notion_token: str,
        parent_page_id: Optional[str] = None
    ) -> NotionSaveResponse:
        """알고리즘 문제와 솔루션을 Notion 페이지로 저장합니다."""
        
        try:
            client = Client(auth=notion_token)
            
            # 페이지 부모 설정
            if parent_page_id:
                parent = {"type": "page_id", "page_id": parent_page_id}
            else:
                parent = {"type": "workspace", "workspace": True}
            
            # 페이지 속성 (제목만 설정)
            properties = {
                "title": [
                    {
                        "type": "text",
                        "text": {"content": notion_page.title}
                    }
                ]
            }
            
            # 페이지 내용 구성
            children = self._build_page_content(notion_page)
            
            # Notion 페이지 생성
            response = client.pages.create(
                parent=parent,
                properties=properties,
                children=children
            )
            
            return NotionSaveResponse(
                notion_page_id=response["id"],
                notion_url=response["url"],
                saved_at=datetime.now()
            )
            
        except Exception as e:
            logger.error(f"Notion 페이지 저장 중 오류: {str(e)}")
            raise Exception(f"Notion 페이지 저장 실패: {str(e)}")
    
    def _extract_page_title(self, page_info: Dict) -> str:
        """페이지 정보에서 제목을 추출합니다."""
        try:
            if page_info.get("properties") and page_info["properties"].get("title"):
                title_prop = page_info["properties"]["title"]
                if title_prop.get("title") and len(title_prop["title"]) > 0:
                    return title_prop["title"][0]["plain_text"]
            return "Untitled"
        except:
            return "Untitled"
    
    def _split_text(self, text: str, max_length: int = 1900) -> List[str]:
        """긴 텍스트를 여러 부분으로 나눕니다."""
        if len(text) <= max_length:
            return [text]
        
        parts = []
        current_pos = 0
        
        while current_pos < len(text):
            # 최대 길이만큼 자르되, 문장이나 줄바꿈에서 자르도록 시도
            end_pos = min(current_pos + max_length, len(text))
            
            if end_pos == len(text):
                # 마지막 부분
                parts.append(text[current_pos:end_pos])
                break
            
            # 적절한 자르는 지점 찾기 (줄바꿈, 마침표, 공백 순서로)
            cut_pos = end_pos
            for delimiter in ['\n', '. ', ' ']:
                last_delimiter = text.rfind(delimiter, current_pos, end_pos)
                if last_delimiter > current_pos:
                    cut_pos = last_delimiter + len(delimiter)
                    break
            
            parts.append(text[current_pos:cut_pos])
            current_pos = cut_pos
        
        return parts
    
    def _parse_markdown_blocks(self, text: str) -> List[Dict]:
        """마크다운 텍스트를 Notion 블록으로 파싱합니다."""
        blocks = []
        lines = text.split('\n')
        i = 0
        
        while i < len(lines):
            line = lines[i].strip()
            
            # 빈 줄 처리
            if not line:
                i += 1
                continue
            
            # 헤딩 처리 (## 헤딩)
            if line.startswith('## '):
                blocks.append({
                    "object": "block",
                    "type": "heading_2",
                    "heading_2": {
                        "rich_text": self._parse_inline_markdown(line[3:])
                    }
                })
                i += 1
            
            # 헤딩 처리 (### 헤딩)
            elif line.startswith('### '):
                blocks.append({
                    "object": "block", 
                    "type": "heading_3",
                    "heading_3": {
                        "rich_text": self._parse_inline_markdown(line[4:])
                    }
                })
                i += 1
            
            # 테이블 처리
            elif '|' in line and '|' in lines[i+1] if i+1 < len(lines) else False:
                table_lines = []
                j = i
                while j < len(lines) and '|' in lines[j]:
                    table_lines.append(lines[j])
                    j += 1
                
                if len(table_lines) >= 2:  # 헤더 + 구분선 최소
                    table_block = self._create_table_block(table_lines)
                    if table_block:
                        blocks.append(table_block)
                    i = j
                else:
                    # 일반 텍스트로 처리
                    blocks.extend(self._create_paragraph_blocks(line))
                    i += 1
            
            # 코드 블록 처리 (```)
            elif line.startswith('```'):
                language = line[3:].strip() or 'plain_text'
                code_lines = []
                i += 1
                
                while i < len(lines) and not lines[i].strip().startswith('```'):
                    code_lines.append(lines[i])
                    i += 1
                
                if i < len(lines):  # 닫는 ``` 찾음
                    i += 1
                
                code_content = '\n'.join(code_lines)
                blocks.extend(self._create_code_blocks(code_content, language))
            
            # 인용 처리 (>)
            elif line.startswith('> '):
                quote_lines = []
                j = i
                while j < len(lines) and lines[j].strip().startswith('> '):
                    quote_lines.append(lines[j][2:])  # '> ' 제거
                    j += 1
                
                quote_content = ' '.join(quote_lines)
                blocks.append({
                    "object": "block",
                    "type": "quote",
                    "quote": {
                        "rich_text": self._parse_inline_markdown(quote_content)
                    }
                })
                i = j
            
            # 불릿 리스트 처리 (-)
            elif line.startswith('- '):
                blocks.append({
                    "object": "block",
                    "type": "bulleted_list_item",
                    "bulleted_list_item": {
                        "rich_text": self._parse_inline_markdown(line[2:])
                    }
                })
                i += 1
            
            # 넘버링 리스트 처리 (1. )
            elif re.match(r'^\d+\. ', line):
                content = re.sub(r'^\d+\. ', '', line)
                blocks.append({
                    "object": "block",
                    "type": "numbered_list_item",
                    "numbered_list_item": {
                        "rich_text": self._parse_inline_markdown(content)
                    }
                })
                i += 1
            
            # 구분선 처리 (---)
            elif line.startswith('---'):
                blocks.append({
                    "object": "block",
                    "type": "divider",
                    "divider": {}
                })
                i += 1
            
            # 일반 텍스트 처리
            else:
                blocks.extend(self._create_paragraph_blocks(line))
                i += 1
        
        return blocks
    
    def _create_table_block(self, table_lines: List[str]) -> Optional[Dict]:
        """마크다운 테이블을 Notion 테이블 블록으로 변환합니다."""
        try:
            # 헤더와 구분선 제거
            header_line = table_lines[0]
            data_lines = table_lines[2:] if len(table_lines) > 2 else []
            
            # 헤더 파싱
            header_cells = [cell.strip() for cell in header_line.split('|')[1:-1]]
            table_width = len(header_cells)
            
            if table_width == 0:
                return None
            
            # 테이블 행 생성
            children = []
            
            # 헤더 행
            children.append({
                "type": "table_row",
                "table_row": {
                    "cells": [[{"type": "text", "text": {"content": cell}}] for cell in header_cells]
                }
            })
            
            # 데이터 행들
            for line in data_lines:
                if '|' in line:
                    cells = [cell.strip() for cell in line.split('|')[1:-1]]
                    # 셀 수 맞추기
                    while len(cells) < table_width:
                        cells.append("")
                    cells = cells[:table_width]
                    
                    children.append({
                        "type": "table_row",
                        "table_row": {
                            "cells": [[{"type": "text", "text": {"content": cell}}] for cell in cells]
                        }
                    })
            
            return {
                "object": "block",
                "type": "table",
                "table": {
                    "table_width": table_width,
                    "has_column_header": True,
                    "has_row_header": False,
                    "children": children
                }
            }
        except Exception as e:
            logger.warning(f"테이블 파싱 실패: {str(e)}")
            return None
    
    def _parse_inline_markdown(self, text: str) -> List[Dict]:
        """인라인 마크다운을 Notion rich_text로 파싱합니다."""
        rich_text = []
        
        # 복잡한 마크다운 패턴 매칭
        pattern = r'(\*\*.*?\*\*|\*.*?\*|`.*?`|\[.*?\]\(.*?\)|~~.*?~~)'
        parts = re.split(pattern, text)
        
        for part in parts:
            if not part:
                continue
                
            annotations = {
                "bold": False,
                "italic": False,
                "strikethrough": False,
                "underline": False,
                "code": False,
                "color": "default"
            }
            
            content = part
            href = None
            
            # 볼드 텍스트 (**text**)
            if part.startswith('**') and part.endswith('**') and len(part) > 4:
                annotations["bold"] = True
                content = part[2:-2]
            # 이탤릭 텍스트 (*text*)
            elif part.startswith('*') and part.endswith('*') and len(part) > 2:
                annotations["italic"] = True
                content = part[1:-1]
            # 취소선 (~~text~~)
            elif part.startswith('~~') and part.endswith('~~') and len(part) > 4:
                annotations["strikethrough"] = True
                content = part[2:-2]
            # 인라인 코드 (`code`)
            elif part.startswith('`') and part.endswith('`') and len(part) > 2:
                annotations["code"] = True
                content = part[1:-1]
            # 링크 ([text](url))
            elif part.startswith('[') and '](' in part and part.endswith(')'):
                link_match = re.match(r'\[(.*?)\]\((.*?)\)', part)
                if link_match:
                    content = link_match.group(1)
                    href = link_match.group(2)
            
            text_obj = {
                "type": "text",
                "text": {"content": content},
                "annotations": annotations
            }
            
            if href:
                text_obj["text"]["link"] = {"url": href}
            
            rich_text.append(text_obj)
        
        return rich_text if rich_text else [{"type": "text", "text": {"content": text}}]
    
    def _create_paragraph_blocks(self, text: str) -> List[Dict]:
        """일반 텍스트를 paragraph 블록들로 생성합니다."""
        text_parts = self._split_text(text)
        blocks = []
        
        for part in text_parts:
            rich_text = self._parse_inline_markdown(part)
            blocks.append({
                "object": "block",
                "type": "paragraph",
                "paragraph": {
                    "rich_text": rich_text
                }
            })
        
        return blocks
    
    def _create_code_blocks(self, code: str, language: str) -> List[Dict]:
        """긴 코드를 여러 code 블록으로 생성합니다."""
        code_parts = self._split_text(code)
        blocks = []
        
        for i, part in enumerate(code_parts):
            blocks.append({
                "object": "block",
                "type": "code",
                "code": {
                    "language": language,
                    "rich_text": [{"type": "text", "text": {"content": part}}]
                }
            })
            
            # 연속된 코드 블록 사이에 구분선 추가 (마지막 제외)
            if i < len(code_parts) - 1:
                blocks.append({
                    "object": "block",
                    "type": "paragraph",
                    "paragraph": {
                        "rich_text": [{"type": "text", "text": {"content": "..."}}]
                    }
                })
        
        return blocks
    
    def _build_page_content(self, notion_page: NotionPage) -> List[Dict]:
        """Notion 페이지 컨텐츠를 구성합니다."""
        children = [
            # 메타데이터 테이블
            {
                "object": "block",
                "type": "table",
                "table": {
                    "table_width": 2,
                    "has_column_header": True,
                    "has_row_header": False,
                    "children": [
                        {
                            "type": "table_row",
                            "table_row": {
                                "cells": [
                                    [{"type": "text", "text": {"content": "속성"}}],
                                    [{"type": "text", "text": {"content": "값"}}]
                                ]
                            }
                        },
                        {
                            "type": "table_row",
                            "table_row": {
                                "cells": [
                                    [{"type": "text", "text": {"content": "언어"}}],
                                    [{"type": "text", "text": {"content": notion_page.language.title()}}]
                                ]
                            }
                        },
                        {
                            "type": "table_row",
                            "table_row": {
                                "cells": [
                                    [{"type": "text", "text": {"content": "사용자 복잡도"}}],
                                    [{"type": "text", "text": {"content": notion_page.user_complexity}}]
                                ]
                            }
                        },
                        {
                            "type": "table_row",
                            "table_row": {
                                "cells": [
                                    [{"type": "text", "text": {"content": "AI 복잡도"}}],
                                    [{"type": "text", "text": {"content": notion_page.ai_complexity}}]
                                ]
                            }
                        },
                        {
                            "type": "table_row",
                            "table_row": {
                                "cells": [
                                    [{"type": "text", "text": {"content": "태그"}}],
                                    [{"type": "text", "text": {"content": ", ".join(notion_page.tags)}}]
                                ]
                            }
                        }
                    ]
                }
            },
            {
                "object": "block",
                "type": "divider",
                "divider": {}
            },
            # 문제 섹션
            {
                "object": "block",
                "type": "heading_2",
                "heading_2": {
                    "rich_text": [{"type": "text", "text": {"content": "📝 문제"}}]
                }
            }
        ]
        
        # 문제 내용 추가 (고급 마크다운 파싱)
        children.extend(self._parse_markdown_blocks(notion_page.problem))
        
        # 사용자 솔루션 섹션
        children.append({
            "object": "block",
            "type": "heading_2",
            "heading_2": {
                "rich_text": [{"type": "text", "text": {"content": "👤 사용자 솔루션"}}]
            }
        })
        
        # 사용자 솔루션 코드 추가
        children.extend(self._create_code_blocks(notion_page.user_solution, notion_page.language))
        
        # AI 솔루션 섹션
        children.append({
            "object": "block",
            "type": "heading_2", 
            "heading_2": {
                "rich_text": [{"type": "text", "text": {"content": "🤖 AI 솔루션"}}]
            }
        })
        
        # AI 솔루션 코드 추가
        children.extend(self._create_code_blocks(notion_page.ai_solution, notion_page.language))
        
        # 비교 분석 섹션
        children.append({
            "object": "block",
            "type": "heading_2",
            "heading_2": {
                "rich_text": [{"type": "text", "text": {"content": "🔍 비교 분석"}}]
            }
        })
        
        # 비교 분석 내용 추가 (고급 마크다운 파싱)
        children.extend(self._parse_markdown_blocks(notion_page.comparison))
        
        # 시간 복잡도 분석 섹션
        children.append({
            "object": "block",
            "type": "heading_2",
            "heading_2": {
                "rich_text": [{"type": "text", "text": {"content": "⏱️ 시간 복잡도"}}]
            }
        })
        
        # 복잡도 분석 내용 추가 (고급 마크다운 파싱)
        children.extend(self._parse_markdown_blocks(notion_page.complexity_analysis))
        
        # 핵심 요약 섹션
        children.append({
            "object": "block",
            "type": "heading_2",
            "heading_2": {
                "rich_text": [{"type": "text", "text": {"content": "📋 핵심 요약"}}]
            }
        })
        
        # 요약 리스트 추가 (마크다운 파싱)
        for summary_item in notion_page.summary:
            if len(summary_item) <= 1900:
                rich_text = self._parse_inline_markdown(summary_item)
                children.append({
                    "object": "block",
                    "type": "bulleted_list_item",
                    "bulleted_list_item": {
                        "rich_text": rich_text
                    }
                })
            else:
                # 긴 요약은 마크다운 블록으로 파싱
                children.extend(self._parse_markdown_blocks(f"• {summary_item}"))
        
        return children

notion_service = NotionService()