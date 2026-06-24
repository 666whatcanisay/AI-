import os
import time
import logging
import traceback
from typing import Dict, List, Any
from dotenv import load_dotenv
from openai import OpenAI
from .vector_store import VectorStore

load_dotenv()
logger = logging.getLogger(__name__)

class RAGService:
    """
    RAG 检索增强生成服务（企业级优化版）
    """

    def __init__(self):
        # ===== 向量库 =====
        self.vector_store = VectorStore()
        logger.info("向量数据库连接完成")

        # ===== 配置 =====
        self.api_key = os.getenv("DASHSCOPE_API_KEY")
        self.base_url = "https://dashscope.aliyuncs.com/compatible-mode/v1"

        # 模型与阈值
        self.model_name = "qwen-turbo"  # 🚀 高速模型
        self.top_k = 2
        self.distance_threshold = float(os.getenv("DISTANCE_THRESHOLD", "0.35"))

        # ===== 初始化客户端 =====
        try:
            self.client = OpenAI(
                api_key=self.api_key,
                base_url=self.base_url
            )
            logger.info(f"LLM客户端初始化成功 | model={self.model_name}")
        except Exception as e:
            logger.error("LLM客户端初始化失败")
            logger.error(traceback.format_exc())
            self.client = None

    # =========================
    # 预热
    # =========================
    def warm_up(self):
        try:
            logger.info("RAG预热开始...")
            start = time.time()
            self.vector_store.search("测试", top_k=1)
            logger.info(f"RAG预热完成 | {time.time() - start:.2f}s")
        except Exception:
            logger.error("预热失败")
            logger.error(traceback.format_exc())

    # =========================
    # 构建上下文（防止过长）
    # =========================
    def build_context(self, results: List[Dict[str, Any]]) -> str:
        if not results:
            return ""

        max_chars = 2000
        context = []
        total = 0

        for i, r in enumerate(results):
            content = r.get("content", "")
            file = r.get("file", "")
            page = r.get("page", 0)

            block = f"""【资料{i+1}】
来源：{file} 第{page}页
内容：{content}
"""
            if total + len(block) > max_chars:
                break

            context.append(block)
            total += len(block)

        return "\n".join(context)

    # =========================
    # 调用模型
    # =========================
    def call_llm(self, query: str, context: str) -> str:
        if not self.client:
            return "LLM未初始化"

        if not self.api_key:
            return "未配置DASHSCOPE_API_KEY"

        context = context[:3000]

        prompt = f"""
你是智慧党建AI助手。
必须严格基于资料回答，不允许编造。

资料：
{context}

问题：
{query}

请用中文简洁回答：
"""

        try:
            start = time.time()

            response = self.client.chat.completions.create(
                model=self.model_name,
                messages=[
                    {"role": "system", "content": "你是党建知识助手"},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.3
            )

            answer = response.choices[0].message.content.strip()
            logger.info(
                f"LLM调用成功 | "
                f"耗时={time.time() - start:.2f}s | "
                f"Prompt长度={len(prompt)} | "
                f"Answer长度={len(answer)}"
            )

            return answer

        except Exception as e:
            logger.error("LLM调用失败（关键错误）")
            logger.error(str(e))
            logger.error(traceback.format_exc())
            return f"AI模型调用失败: {str(e)}"

    # =========================
    # 主流程
    # =========================
    def query(self, question: str) -> Dict[str, Any]:
        total_start = time.time()
        logger.info(f"用户问题: {question}")

        # 1️⃣ 向量检索
        t1 = time.time()
        results = self.vector_store.search(question, top_k=self.top_k)
        t_retrieval = time.time() - t1
        logger.info(f"检索完成 | {len(results)}条 | {t_retrieval*1000:.1f}ms")

        if not results:
            return {"answer": "知识库中未找到相关内容", "sources": []}

        # 2️⃣ 阈值过滤
        top_distance = results[0].get("distance", 1.0)
        if top_distance > self.distance_threshold:
            logger.info(f"置信度过低: {top_distance}")
            return {"answer": "知识库中未找到相关内容", "sources": []}

        # 3️⃣ 构建 context
        context = self.build_context(results)

        # 4️⃣ 调用 LLM
        t2 = time.time()
        answer = self.call_llm(question, context)
        t_llm = time.time() - t2

        # 5️⃣ 构建 sources
        sources = [
            {
                "file": r.get("file"),
                "page": r.get("page"),
                "distance": r.get("distance"),
                "content_preview": r.get("content", "")[:100]
            }
            for r in results
        ]

        logger.info(f"总耗时={time.time() - total_start:.2f}s | 检索={t_retrieval:.2f}s | LLM={t_llm:.2f}s")

        return {"answer": answer, "sources": sources}
