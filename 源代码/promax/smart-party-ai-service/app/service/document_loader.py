"""
============================================================
文档加载器 - 读取和解析 PDF 文件
============================================================
功能说明：
- 扫描 knowledge 目录下的 PDF 文件
- 提取 PDF 文件中的文字内容
- 返回格式化的文档数据
============================================================
"""

import os  # 导入操作系统模块，用于文件路径操作
import fitz  # 导入 PyMuPDF 库，专门用于读取 PDF 文件
from typing import List, Dict, Any  # 导入类型提示


class DocumentLoader:
    """
    文档加载类
    
    负责从 PDF 文件中提取文字内容
    """
    
    def __init__(self, knowledge_dir: str = "knowledge"):
        """
        初始化文档加载器
        
        参数:
            knowledge_dir: 知识库文件夹路径，默认为 "knowledge"
        """
        self.knowledge_dir = knowledge_dir  # 保存知识库文件夹路径
        
        # 如果文件夹不存在，就创建一个
        if not os.path.exists(knowledge_dir):
            os.makedirs(knowledge_dir)
    
    def scan_pdf_files(self) -> List[str]:
        """
        扫描知识库文件夹，找出所有 PDF 文件
        
        返回:
            PDF 文件完整路径的列表（按字母顺序排序）
        """
        pdf_files = []  # 用来保存找到的 PDF 文件路径
        
        # 遍历文件夹中的所有文件
        for filename in os.listdir(self.knowledge_dir):
            # 检查文件扩展名是否是 PDF（不区分大小写）
            if filename.lower().endswith(".pdf"):
                # 把完整的文件路径加进去
                pdf_files.append(os.path.join(self.knowledge_dir, filename))
        
        # 返回按字母排序后的文件列表
        return sorted(pdf_files)
    
    def extract_text_from_pdf(self, file_path: str) -> List[Dict[str, Any]]:
        """
        从 PDF 文件中提取每一页的文字内容
        
        参数:
            file_path: PDF 文件路径
            
        返回:
            包含每页数据的列表，每页是一个字典：
            {
                "filename": 文件名,
                "page_number": 页码（从1开始）,
                "text": 文字内容
            }
        """
        pages_data = []  # 保存每一页的数据
        
        try:
            # 打开 PDF 文件
            doc = fitz.open(file_path)
            # 获取文件名
            filename = os.path.basename(file_path)
            
            # 遍历 PDF 的每一页
            for page_num in range(len(doc)):
                # 获取当前页面对象
                page = doc[page_num]
                # 提取页面文字
                text = page.get_text()
                
                # 只保存有内容的页面（去除空白字符后有文字）
                if text.strip():
                    pages_data.append({
                        "filename": filename,  # 文件名
                        "page_number": page_num + 1,  # 页码（从1开始，不是0）
                        "text": text.strip()  # 去除首尾空白字符的文字
                    })
            
            # 关闭 PDF 文件
            doc.close()
        except Exception as e:
            # 如果出错，打印错误信息
            print(f"Error processing {file_path}: {str(e)}")
        
        # 返回提取的页面数据
        return pages_data
    
    def load_all_documents(self) -> List[Dict[str, Any]]:
        """
        加载知识库文件夹中的所有 PDF 文档
        
        返回:
            所有 PDF 所有页面的列表
        """
        all_pages = []  # 保存所有页面的数据
        
        # 先扫描文件夹，找到所有 PDF 文件
        pdf_files = self.scan_pdf_files()
        
        # 逐个处理每一个 PDF 文件
        for pdf_file in pdf_files:
            # 提取这个 PDF 的文字
            pages_data = self.extract_text_from_pdf(pdf_file)
            # 把提取的内容加到总列表里
            all_pages.extend(pages_data)
        
        # 返回所有页面数据
        return all_pages


# ============================================================
# 如果直接运行这个文件，就测试一下功能
# ============================================================
if __name__ == "__main__":
    """
    测试代码：运行这个文件可以测试 PDF 加载功能
    """
    print("正在测试文档加载器...")
    loader = DocumentLoader()  # 创建文档加载器
    documents = loader.load_all_documents()  # 加载所有文档
    
    print(f"\nTotal pages extracted: {len(documents)}")  # 打印总共提取了多少页
    print("-" * 50)  # 打印分隔线
    
    # 打印每一页的信息（只显示前200个字符）
    for doc in documents:
        print(f"File: {doc['filename']}")
        print(f"Page: {doc['page_number']}")
        print(f"Text preview: {doc['text'][:200]}...")
        print("-" * 50)
