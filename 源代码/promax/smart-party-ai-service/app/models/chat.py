"""
============================================================
数据模型 - 定义请求和响应的格式
============================================================
功能说明：
- 用 Pydantic 定义数据模型
- 自动进行数据验证
- 生成 Swagger 文档示例
============================================================
"""

from pydantic import BaseModel, Field  # 导入 Pydantic 组件
from typing import List, Optional  # 导入类型提示


class ChatRequest(BaseModel):
    """
    问答请求模型
    
    这是 POST /api/chat 接口的请求体格式
    """
    message: str = Field(
        ..., 
        min_length=1,  # 至少 1 个字符
        max_length=1000,  # 最多 1000 个字符
        description="用户提问内容",
        json_schema_extra={"example": "党员有哪些义务？"}
    )


class SourceItem(BaseModel):
    """
    参考来源项模型
    
    包含文件、页码、相似度、内容预览
    """
    file: str = Field(
        ..., 
        description="来源文件名",
        json_schema_extra={"example": "党章.pdf"}
    )
    page: int = Field(
        ..., 
        description="页码",
        json_schema_extra={"example": 5}
    )
    distance: float = Field(
        ..., 
        description="距离（越小越相似）",
        json_schema_extra={"example": 0.35}
    )
    content_preview: str = Field(
        ..., 
        description="检索到的原文前200字",
        json_schema_extra={"example": "党员必须履行下列义务：（一）认真学习马克思列宁主义、毛泽东思想..."}
    )


class ChatResponse(BaseModel):
    """
    问答响应模型
    
    这是 POST /api/chat 接口的返回格式
    """
    answer: str = Field(
        ..., 
        description="回答内容",
        json_schema_extra={"example": "根据党章规定，党员必须履行八项义务，包括认真学习党的理论知识..."}
    )
    sources: List[SourceItem] = Field(
        ..., 
        description="参考来源列表"
    )
