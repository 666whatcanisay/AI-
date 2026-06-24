import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from app.service.vector_store import VectorStore

vs = VectorStore()
stats = vs.get_collection_stats()
print(f"collection名称: {stats['collection_name']}")
print(f"chunk总数: {stats['total_chunks']}")
print("前5条metadata:")
data = vs.collection.get(limit=5)
for i, meta in enumerate(data['metadatas'][:5]):
    print(f"{i+1}. {meta}")