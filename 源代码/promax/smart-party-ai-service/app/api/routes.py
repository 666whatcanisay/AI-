"""
============================================================
API 路由 - 定义所有对外接口
============================================================
功能说明：
- 健康检查接口
- 版本信息接口
- 系统状态接口（ready / loading）
- 系统统计接口
- 智能问答接口（未就绪时返回 503）
- 日志记录
============================================================
"""

import logging
import os
from fastapi import APIRouter, HTTPException, status, Request
from fastapi.responses import JSONResponse
from app.models.chat import ChatRequest, ChatResponse, SourceItem

# 确保日志目录存在
os.makedirs("logs", exist_ok=True)

# ========== 配置日志系统 ==========
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

file_handler = logging.FileHandler("logs/app.log", encoding="utf-8")
file_handler.setLevel(logging.INFO)
file_formatter = logging.Formatter("%(asctime)s - %(name)s - %(levelname)s - %(message)s")
file_handler.setFormatter(file_formatter)
logger.addHandler(file_handler)

console_handler = logging.StreamHandler()
console_handler.setLevel(logging.INFO)
console_formatter = logging.Formatter("%(asctime)s - %(name)s - %(levelname)s - %(message)s")
console_handler.setFormatter(console_formatter)
logger.addHandler(console_handler)

router = APIRouter(tags=["智慧党建API"])


# ========== 接口定义 ==========

@router.get(
    "/health",
    summary="健康检查",
    description="检查服务是否正常运行"
)
async def health_check():
    """健康检查接口 - 始终可用"""
    logger.info("健康检查接口被调用")
    return {"状态": "正常"}


@router.get(
    "/version",
    summary="版本信息",
    description="获取系统版本号"
)
async def get_version():
    """版本信息接口 - 始终可用"""
    logger.info("版本信息接口被调用")
    return {"版本": "1.0.0"}


@router.get(
    "/status",
    summary="系统状态",
    description="获取 RAG 服务初始化状态"
)
async def get_status(request: Request):
    """
    系统状态接口

    返回 RAG 服务是否已就绪，以及当前加载状态信息。
    前端可轮询此接口判断何时可以开始提问。
    """
    ready = getattr(request.app.state, "ready", False)
    message = getattr(request.app.state, "loading_message", "未知状态")

    logger.info(f"状态查询: ready={ready}, message={message}")

    return {
        "ready": ready,
        "message": message
    }


@router.get(
    "/stats",
    summary="系统统计",
    description="获取知识库和系统统计信息"
)
async def get_stats(request: Request):
    """
    系统统计接口

    需要 RAG 服务已就绪才能返回统计信息。
    如果未就绪，返回 503。
    """
    # 检查系统是否就绪
    if not getattr(request.app.state, "ready", False):
        logger.warning("统计接口被调用，但系统未就绪")
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="系统正在初始化，请稍后重试"
        )

    try:
        rag = request.app.state.rag_service
        vs = rag.vector_store
        stats = vs.get_collection_stats()

        # 统计 knowledge 目录下的 PDF 文件数量
        knowledge_dir = "knowledge"
        doc_count = 0
        if os.path.exists(knowledge_dir):
            doc_count = len([f for f in os.listdir(knowledge_dir) if f.lower().endswith(".pdf")])

        result = {
            "文档数量": doc_count,
            "向量块数量": stats.get("total_chunks", 0),
            "嵌入模型": stats.get("model_name", "未知"),
            "向量数据库": "ChromaDB"
        }

        logger.info(f"统计信息已获取: {result}")
        return result

    except Exception as e:
        logger.error(f"获取统计信息失败: {str(e)}", exc_info=True)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"获取统计信息失败: {str(e)}"
        )


@router.post(
    "/chat",
    response_model=ChatResponse,
    status_code=status.HTTP_200_OK,
    summary="智能问答",
    description="""
接收用户党建相关问题，返回基于知识库的智能回答和参考来源。

**注意：** 系统初始化完成前调用此接口会返回 503，提示"系统正在初始化，请稍后"。

**功能特点：**
- 基于向量检索技术查找相关文档
- 调用大语言模型生成答案
- 自动返回参考来源和相似度
- 对于知识库外的问题自动拒绝回答

**请求示例：**
```json
{
    "message": "党员有哪些义务？"
}
```

**响应示例：**
```json
{
    "answer": "根据党章规定，党员必须履行八项义务...",
    "sources": [
        {
            "file": "党章.pdf",
            "page": 5,
            "distance": 0.35,
            "content_preview": "党员必须履行下列义务：..."
        }
    ]
}
```

**拒答示例（知识库外问题）：**
```json
{
    "answer": "知识库中未找到相关依据。",
    "sources": []
}
```
    """
)
async def chat(request: Request, chat_request: ChatRequest):
    """
    智能问答接口

    访问地址: POST /api/chat
    从 app.state 获取已初始化的 RAGService 单例，直接复用。

    如果系统未就绪，返回 HTTP 503 + 友好提示，不报错。
    """
    question = chat_request.message
    logger.info(f"收到问答请求: {question[:50]}...")

    # 检查系统是否就绪
    if not getattr(request.app.state, "ready", False):
        logger.warning(f"问答请求被拒绝，系统未就绪: {question[:50]}...")
        # 返回 503，但前端能正常显示 answer 字段
        return JSONResponse(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            content={
                "answer": "系统正在初始化，请稍后",
                "sources": []
            }
        )

    try:
        # 从 app.state 获取已初始化的 RAGService 单例（零延迟）
        service = request.app.state.rag_service
        result = service.query(question)

        # 构建来源信息
        sources = []
        hit_files = []
        for source in result.get("sources", []):
            sources.append(SourceItem(
                file=source.get("file", ""),
                page=source.get("page", 0),
                distance=source.get("distance", 0.0),
                content_preview=source.get("content_preview", "")
            ))
            if source.get("file") not in hit_files:
                hit_files.append(source.get("file"))

        top_distance = result.get("sources", [{}])[0].get("distance", 0.0) if result.get("sources") else 0.0
        deepseek_time = result.get("deepseek_time_ms", 0)

        logger.info(f"检索结果: {len(sources)} 条, 最小距离: {top_distance:.4f}, 命中文件: {hit_files}")
        if deepseek_time > 0:
            logger.info(f"大模型API耗时: {deepseek_time}ms")

        response = ChatResponse(
            answer=result.get("answer", ""),
            sources=sources
        )

        logger.info("问答响应生成成功")
        return response

    except HTTPException as e:
        logger.error(f"HTTP错误处理问答请求: {e.detail}")
        raise e
    except Exception as e:
        logger.error(f"处理问答请求失败: {str(e)}", exc_info=True)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"服务器内部错误: {str(e)}"
        )
