"""
============================================================
向量存储 - 把文档转成向量并保存，用于检索
============================================================
功能说明：
- 把文字切割成合理的块（Chunks）
- 用 Embedding 模型把文字转成向量
- 存储到 Chroma 向量数据库
- 根据用户问题检索相关的内容

关键优化：
- 使用 sentence_transformers.SentenceTransformer 直接本地加载模型
- 完全绕过 ChromaDB 的 SentenceTransformerEmbeddingFunction
- 禁止访问 huggingface.co，完全离线运行
============================================================
"""

import os
import numpy as np
import chromadb
from chromadb.api.types import EmbeddingFunction, Documents, Embeddings
from typing import List, Dict, Any

os.environ.setdefault("USE_TF", "0")


class LocalEmbeddingFunction(EmbeddingFunction[Documents]):
    """
    自定义本地 Embedding 函数
    
    继承 ChromaDB 的 EmbeddingFunction 协议，实现所有必须方法。
    直接使用 sentence_transformers.SentenceTransformer 加载本地模型，
    完全绕过 ChromaDB 的 SentenceTransformerEmbeddingFunction，避免联网检查。
    """
    
    def __init__(self, model_name: str = "BAAI/bge-small-zh-v1.5"):
        """
        初始化本地 Embedding 函数
        
        参数:
            model_name: 模型名称或本地路径
        """
        from sentence_transformers import SentenceTransformer
        
        print(f"[LocalEmbedding] 正在加载模型: {model_name}")
        
        # 保存模型名称，用于序列化配置
        self._model_name = model_name
        
        local_files_only = os.getenv("HF_LOCAL_FILES_ONLY", "0").lower() in {"1", "true", "yes"}

        self.model = SentenceTransformer(
            model_name_or_path=model_name,
            local_files_only=local_files_only,
            device="cpu"
        )
        print(f"[LocalEmbedding] 模型加载成功")
    
    def __call__(self, input: Documents) -> Embeddings:
        """
        将文本列表转换为向量列表
        
        参数:
            input: 文本列表（Documents 类型，即 List[str]）
            
        返回:
            向量列表（Embeddings 类型，即 List[np.ndarray]）
        """
        # 使用模型编码文本
        embeddings = self.model.encode(
            input,
            normalize_embeddings=True,  # 归一化向量
            show_progress_bar=False
        )
        # 将 numpy 数组转换为 Python 列表（兼容 ChromaDB 的 Embeddings 类型）
        return embeddings.tolist()
    
    @staticmethod
    def name() -> str:
        """返回 embedding 函数名称"""
        return "local_sentence_transformer"
    
    def get_config(self) -> Dict[str, Any]:
        """返回可序列化的配置"""
        return {
            "model_name": self._model_name
        }
    
    @staticmethod
    def build_from_config(config: Dict[str, Any]) -> "LocalEmbeddingFunction":
        """从配置构建实例"""
        return LocalEmbeddingFunction(model_name=config.get("model_name", "BAAI/bge-small-zh-v1.5"))


class VectorStore:
    """
    向量存储类
    
    负责把文档转换成向量，存储到数据库，并根据查询检索相关文档
    """
    
    def __init__(
        self,
        persist_dir: str = "vector_db",
        collection_name: str = "documents",
        model_name: str = "BAAI/bge-small-zh-v1.5",
        chunk_size: int = 500,
        overlap: int = 100
    ):
        """
        初始化向量存储
        
        参数:
            persist_dir: 向量数据库保存的文件夹
            collection_name: 集合名称
            model_name: Embedding 模型
            chunk_size: 文本块的大小
            overlap: 文本块之间的重叠
        """
        self.persist_dir = persist_dir
        self.collection_name = collection_name
        self.model_name = model_name
        self.chunk_size = chunk_size
        self.overlap = overlap
        
        # 如果保存文件夹不存在，就创建一个
        os.makedirs(persist_dir, exist_ok=True)
        
        # 创建 Chroma 数据库客户端（持久化模式，保存到本地文件夹）
        self.client = chromadb.PersistentClient(path=persist_dir)
        
        # 使用自定义本地 Embedding 函数（完全离线）
        self.embedding_fn = LocalEmbeddingFunction(model_name=model_name)
        
        # 获取或创建集合（如果集合已经存在，就用已有的）
        self.collection = self.client.get_or_create_collection(
            name=collection_name,
            embedding_function=self.embedding_fn,
            metadata={"hnsw:space": "cosine"}
        )
    
    def chunk_text(self, text: str) -> List[str]:
        """
        把长文本切割成合理的块（Chunks）
        
        为了确保语义完整性，我们优先按标点符号切割
        
        参数:
            text: 要切割的长文本
            
        返回:
            切割后的文本块列表
        """
        chunks = []
        start = 0
        text_length = len(text)
        
        # 定义中文常用的分隔符（优先级从高到低）
        chinese_separators = ['\n', '。', '？', '！', '；', '；', '、', '.', '?', '!']
        
        # 循环直到处理完所有文字
        while start < text_length:
            # 初步确定结束位置
            end = start + self.chunk_size
            
            # 如果还没到文本末尾，找个合适的地方切（在句子结尾）
            if end < text_length:
                best_end = end
                
                # 尝试在各种分隔符处切
                for sep in chinese_separators:
                    pos = text.rfind(sep, start, end)
                    
                    # 如果找到了，而且位置在块中间之后（不要切得太短）
                    if pos != -1 and pos > start + self.chunk_size // 2:
                        best_end = pos + 1
                        break
                
                end = best_end
            
            # 提取这个块的文字，并去除空白
            chunk = text[start:end].strip()
            
            # 如果这个块有内容（不是全空白），就保存
            if chunk:
                chunks.append(chunk)
            
            # 计算下一个块的起始位置（要有重叠）
            start = end - self.overlap
            
            # 防止 start 变成负数
            if start < 0:
                start = 0
        
        return chunks
    
    def build_index(self, documents: List[Dict[str, Any]]) -> None:
        """
        把文档列表添加到向量索引中
        
        参数:
            documents: 文档列表，每个文档是一个字典：
            {
                "filename": 文件名,
                "page_number": 页码,
                "text": 文字内容
            }
        """
        all_chunks = []
        all_metadata = []
        all_ids = []
        
        doc_id = 0
        # 逐个处理每个文档页
        for doc in documents:
            text = doc.get("text", "")
            filename = doc.get("filename", "")
            page_number = doc.get("page_number", 0)
            
            # 把这一页的文字切割成块
            chunks = self.chunk_text(text)
            
            # 处理每一个切好的块
            for idx, chunk in enumerate(chunks):
                # 生成唯一的 ID（文件名_页码_块索引）
                chunk_id = f"{filename}_{page_number}_{idx}"
                all_ids.append(chunk_id)
                
                # 保存这个块的文字
                all_chunks.append(chunk)
                
                # 保存元数据（文件名、页码、内容预览）
                all_metadata.append({
                    "file": filename,
                    "page": page_number,
                    "content": chunk[:100] + "..." if len(chunk) > 100 else chunk
                })
                doc_id += 1
        
        # 如果有要处理的块，就添加到集合中
        if all_chunks:
            self.collection.add(
                documents=all_chunks,
                metadatas=all_metadata,
                ids=all_ids
            )
            print(f"Indexed {len(all_chunks)} chunks from {len(documents)} document pages")
    
    def search(self, query: str, top_k: int = 5) -> List[Dict[str, Any]]:
        """
        根据查询问题，在向量数据库中检索相关的内容
        
        参数:
            query: 用户的问题
            top_k: 返回最相关的前几个结果
            
        返回:
            相关结果列表，每个结果是一个字典：
            {
                "file": 文件名,
                "page": 页码,
                "content": 文字内容,
                "distance": 距离（越小越相关）
            }
        """
        # 在向量数据库中查询
        results = self.collection.query(
            query_texts=[query],
            n_results=top_k
        )
        
        # 把查询结果整理成更友好的格式
        search_results = []
        for i in range(len(results['ids'][0])):
            search_results.append({
                "file": results['metadatas'][0][i]['file'],
                "page": results['metadatas'][0][i]['page'],
                "content": results['documents'][0][i],
                "distance": results['distances'][0][i]
            })
        
        return search_results
    
    def get_collection_stats(self) -> Dict[str, Any]:
        """
        获取集合的统计信息
        
        返回:
            包含统计信息的字典
        """
        stats = self.collection.get()
        return {
            "total_chunks": len(stats['ids']),
            "collection_name": self.collection_name,
            "model_name": self.model_name
        }
