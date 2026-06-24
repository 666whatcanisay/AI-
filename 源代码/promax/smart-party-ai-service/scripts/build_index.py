import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from app.service.document_loader import DocumentLoader
from app.service.vector_store import VectorStore

def build_vector_index():
    print("=== 构建向量索引 ===")
    
    print("\n1. 加载文档...")
    loader = DocumentLoader()
    documents = loader.load_all_documents()
    
    if not documents:
        print("未找到PDF文档，请检查knowledge目录")
        return
    
    print(f"已加载 {len(documents)} 页文档")
    
    print("\n2. 清除旧索引...")
    if os.path.exists("vector_db"):
        import shutil
        shutil.rmtree("vector_db")
        print("已清除旧索引")
    
    print("\n3. 构建向量索引...")
    vector_store = VectorStore()
    vector_store.build_index(documents)
    
    stats = vector_store.get_collection_stats()
    print(f"\n索引构建完成！")
    print(f"总Chunks: {stats['total_chunks']}")
    print(f"集合名称: {stats['collection_name']}")
    
    print("\n4. 测试检索...")
    test_query = "党员义务"
    results = vector_store.search(test_query, top_k=3)
    print(f"测试查询: {test_query}")
    print(f"找到 {len(results)} 条相关结果")
    for i, result in enumerate(results):
        print(f"  {i+1}. 文件: {result['file']}, 页码: {result['page']}, 距离: {result['distance']:.4f}")
#加新数据后运行更新chromaDB持久化数据库--------
if __name__ == "__main__":
    build_vector_index()