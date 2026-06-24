"""
============================================================
智慧党建AI知识助手 - 主入口文件
============================================================
功能说明：
- 配置和启动 FastAPI 应用
- 定义网站路由
- 返回用户界面
- 后台线程初始化 RAG 服务（避免启动阻塞）
============================================================
"""

import logging
import os
import threading
import time
from dotenv import load_dotenv
from fastapi import FastAPI
from fastapi.responses import HTMLResponse
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates
from contextlib import asynccontextmanager
from app.api.routes import router as api_router

# 读取 .env 配置文件
load_dotenv()

# 配置日志系统
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s"
)

logger = logging.getLogger(__name__)


def _init_rag_background(app: FastAPI):
    """
    后台线程函数：初始化 RAG 服务
    在后台执行，不阻塞 FastAPI 启动
    """
    logger.info("[后台线程] 开始初始化 RAG 服务...")

    try:
        from app.service.rag_service import RAGService

        start_init = time.time()
        rag_service = RAGService()
        app.state.rag_service = rag_service
        logger.info("[后台线程] RAGService 创建完成")
    except Exception as e:
        logger.error(f"[后台线程] RAGService 创建失败: {e}")
        app.state.loading_message = f"初始化失败: {str(e)}"
        return

    # 预热：执行一次空检索，加载 Embedding 模型和 Chroma 缓存
    logger.info("[后台线程] 正在预热服务（加载模型到内存）...")
    try:
        rag_service.warm_up()
        init_cost = time.time() - start_init
        logger.info(f"[后台线程] 预热完成，耗时: {init_cost:.2f}s")
    except Exception as e:
        logger.warning(f"[后台线程] 预热过程出现异常（不影响后续使用）: {e}")

    # 标记系统就绪
    app.state.ready = True
    app.state.loading_message = "系统已就绪"
    logger.info("[后台线程] RAG 服务初始化完成，系统已就绪")


# ============================================================
# 应用生命周期管理：启动时后台线程初始化 RAG
# ============================================================
@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    应用启动和关闭的生命周期管理

    启动时：
    1. 立即标记系统为未就绪状态
    2. 启动后台线程执行 RAG 初始化（不阻塞）
    3. FastAPI 立即开始接受请求
    """
    # 初始化状态
    app.state.ready = False
    app.state.loading_message = "正在加载知识库..."
    app.state.rag_service = None

    logger.info("=" * 60)
    logger.info("FastAPI 启动中...")
    logger.info("系统状态: 未就绪（后台正在初始化 RAG 服务）")

    # 启动后台线程初始化 RAG（不阻塞主线程）
    init_thread = threading.Thread(
        target=_init_rag_background,
        args=(app,),
        daemon=True,  # 守护线程，主进程退出时自动结束
        name="RAG-Init-Thread"
    )
    init_thread.start()
    logger.info("后台初始化线程已启动")
    logger.info("=" * 60)

    yield  # 应用运行期间

    # 应用关闭时清理
    logger.info("正在关闭服务...")


# 创建 FastAPI 应用程序对象（使用 lifespan 替代废弃的 on_event）
app = FastAPI(
    title="智慧党建AI知识助手",
    description="""
## 智慧党建AI知识助手

基于大语言模型和向量检索技术构建的智能问答系统，专为党建知识问答场景设计。

### 核心技术

- **向量数据库**：ChromaDB
- **Embedding模型**：BAAI/bge-small-zh-v1.5
- **大语言模型**：阿里云百炼 qwen-plus
- **文档解析**：PyMuPDF

### 使用说明

1. 将党建相关PDF文档放入 `knowledge` 目录
2. 运行 `python scripts/build_index.py` 构建向量索引
3. 配置 `.env` 文件中的 DASHSCOPE_API_KEY
4. 启动服务：`python main.py`
5. 访问 http://localhost:8000/ 使用问答，http://localhost:8000/docs 查看API文档
    """,
    version="1.0.0",
    contact={
        "name": "开发团队",
        "email": "dev@example.com"
    },
    lifespan=lifespan  # 注册生命周期管理器
)

# 挂载静态文件目录
app.mount("/static", StaticFiles(directory="static"), name="static")

# 配置模板引擎
templates = Jinja2Templates(directory="templates")

# 将 API 路由注册到应用
app.include_router(api_router, prefix="/api")


# ============================================================
# 首页路由（永远可访问，不依赖 RAG 初始化状态）
# ============================================================
@app.get("/", response_class=HTMLResponse, summary="AI问答页面", description="基于党建知识库的智能问答页面")
async def root():
    return templates.TemplateResponse("index.html", {"request": {}})


# ============================================================
# 程序入口
# ============================================================
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True
    )
