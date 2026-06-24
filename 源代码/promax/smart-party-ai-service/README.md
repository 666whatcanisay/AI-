# 智慧党建AI知识助手

基于大语言模型和向量检索技术构建的智能问答系统，专为党建知识问答场景设计。系统支持上传党建相关PDF文档，自动构建向量索引，用户可以通过自然语言提问，系统基于知识库返回精准答案和参考来源。

---

## 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| Web框架 | FastAPI | 高性能异步Web框架，自动生成API文档 |
| 向量数据库 | ChromaDB | 本地持久化向量存储，支持语义检索 |
| Embedding模型 | BAAI/bge-small-zh-v1.5 | 中文语义向量模型，完全离线运行 |
| 大语言模型 | 阿里云百炼 qwen-turbo | 通过OpenAI兼容接口调用 |
| 文档解析 | PyMuPDF | PDF文本提取 |
| 前端 | 原生HTML/CSS/JS | 简洁现代风格，响应式适配 |

---

## 项目结构

```
smart-party-ai-service/
├── main.py                      # 应用入口：FastAPI启动、路由配置、后台RAG初始化
├── .env                         # 环境变量（API密钥等敏感配置）
├── .env.example                 # 环境变量模板
├── requirements.txt             # Python依赖清单
├── README.md                    # 项目说明文档
│
├── app/                         # 应用核心代码
│   ├── __init__.py
│   ├── api/
│   │   ├── __init__.py
│   │   └── routes.py            # API路由：/health /version /status /stats /chat
│   ├── models/
│   │   ├── __init__.py
│   │   └── chat.py              # Pydantic数据模型：请求/响应格式定义
│   └── service/
│       ├── __init__.py
│       ├── document_loader.py   # PDF文档加载与文本提取
│       ├── vector_store.py      # 向量存储：文本分块、Embedding、ChromaDB操作
│       └── rag_service.py       # RAG服务：检索+生成完整流程
│
├── scripts/
│   └── build_index.py           # 构建向量索引脚本
│
├── knowledge/                   # 知识库目录（存放PDF文件）
│   └── *.pdf
│
├── vector_db/                   # 向量数据库持久化文件（自动生成）
│
├── templates/                   # HTML模板
│   └── index.html               # 前端聊天页面
│
├── static/                      # 静态资源
│   ├── css/style.css            # 样式表
│   ├── js/chat.js               # 前端交互脚本
│   └── images/ai-avatar.svg     # AI头像
│
└── logs/                        # 日志目录（自动生成）
    └── app.log
```

---

## 安装步骤

### 1. 克隆项目

```bash
git clone <项目仓库地址>
cd smart-party-ai-service
```

### 2. 创建虚拟环境

```bash
python -m venv .venv

# Windows
.venv\Scripts\activate

# macOS/Linux
source .venv/bin/activate
```

### 3. 安装依赖

```bash
pip install -r requirements.txt
```

### 4. 下载Embedding模型（首次运行）

系统使用 `BAAI/bge-small-zh-v1.5` 模型进行文本向量化。首次启动时会自动下载模型到本地缓存：

```bash
# 模型将缓存到以下路径（示例）
# Windows: C:\Users\<用户名>\.cache\torch\sentence_transformers\
# Linux: ~/.cache/torch/sentence_transformers/
```

> **注意**：如果服务器无法访问 HuggingFace，请提前将模型文件下载到本地，或配置镜像源。

---

## 启动步骤

### 1. 配置环境变量

```bash
# 复制模板文件
cp .env.example .env

# 编辑 .env 文件，填入你的阿里云百炼API密钥
```

### 2. 准备知识库

将党建相关PDF文档放入 `knowledge/` 目录：

```bash
mkdir -p knowledge
# 复制PDF文件到 knowledge/ 目录
cp /path/to/your/docs/*.pdf knowledge/
```

### 3. 构建向量索引

```bash
python scripts/build_index.py
```

输出示例：
```
=== 构建向量索引 ===

1. 加载文档...
已加载 150 页文档

2. 清除旧索引...
已清除旧索引

3. 构建向量索引...
Indexed 320 chunks from 150 document pages

索引构建完成！
总Chunks: 320
集合名称: documents

4. 测试检索...
测试查询: 党员义务
找到 3 条相关结果
  1. 文件: 党章.pdf, 页码: 5, 距离: 0.2341
  2. 文件: 党员手册.pdf, 页码: 12, 距离: 0.3123
  3. 文件: 党章.pdf, 页码: 8, 距离: 0.3456
```

### 4. 启动服务

```bash
python main.py
```

服务启动后：
- 前端界面：http://localhost:8000/
- API文档：http://localhost:8000/docs
- 健康检查：http://localhost:8000/api/health

> **注意**：首次启动时，RAG服务会在后台线程初始化（约10-30秒），期间可以访问前端页面，但问答接口会返回"系统正在初始化"。

---

## 环境变量配置

创建 `.env` 文件，配置以下变量：

| 变量名 | 必填 | 说明 | 示例 |
|--------|------|------|------|
| `DASHSCOPE_API_KEY` | 是 | 阿里云百炼API密钥 | `sk-xxxxxxxxxxxxxxxx` |
| `DISTANCE_THRESHOLD` | 否 | 向量相似度阈值（越小越严格） | `0.35` |

### 获取阿里云百炼API密钥

1. 访问 [阿里云百炼控制台](https://bailian.console.aliyun.com/)
2. 注册/登录阿里云账号
3. 创建API Key
4. 复制Key到 `.env` 文件

---

## 接口说明

### 1. 智能问答

```http
POST /api/chat
Content-Type: application/json
```

**请求体：**
```json
{
    "message": "党员有哪些义务？"
}
```

**成功响应（200）：**
```json
{
    "answer": "根据党章规定，党员必须履行以下义务：...",
    "sources": [
        {
            "file": "党章.pdf",
            "page": 5,
            "distance": 0.2341,
            "content_preview": "党员必须履行下列义务：..."
        }
    ]
}
```

**系统未就绪（503）：**
```json
{
    "answer": "系统正在初始化，请稍后",
    "sources": []
}
```

### 2. 健康检查

```http
GET /api/health
```

**响应：**
```json
{
    "状态": "正常"
}
```

### 3. 系统状态

```http
GET /api/status
```

**响应：**
```json
{
    "ready": true,
    "message": "系统已就绪"
}
```

### 4. 系统统计

```http
GET /api/stats
```

**响应：**
```json
{
    "文档数量": 5,
    "向量块数量": 320,
    "嵌入模型": "BAAI/bge-small-zh-v1.5",
    "向量数据库": "ChromaDB"
}
```

### 5. 版本信息

```http
GET /api/version
```

**响应：**
```json
{
    "版本": "1.0.0"
}
```

---

## 知识库构建说明

### 添加新文档

1. 将PDF文件放入 `knowledge/` 目录
2. 重新构建索引：

```bash
python scripts/build_index.py
```

### 文档规范

- **格式**：仅支持PDF
- **内容**：建议为文字型PDF（扫描版PDF需先OCR识别）
- **命名**：使用中文文件名，便于来源展示

### 索引参数

如需调整分块策略，修改 `app/service/vector_store.py`：

```python
VectorStore(
    chunk_size=500,    # 每块最大字符数
    overlap=100        # 块之间重叠字符数
)
```

### 检索参数

调整相似度阈值，修改 `.env`：

```bash
DISTANCE_THRESHOLD=0.35   # 越小越严格，越大召回率越高
```

或在 `app/service/rag_service.py` 中修改：

```python
self.distance_threshold = 0.35  # 默认阈值
self.top_k = 2                  # 返回最相关的前N条
```

---

## 常见问题

### Q1: 启动时提示 "系统正在初始化，请稍后"

**原因**：RAG服务在后台初始化中（加载Embedding模型、连接向量库）。

**解决**：等待10-30秒，刷新页面即可。可通过 `/api/status` 查看初始化进度。

### Q2: 问答返回 "知识库中未找到相关内容"

**原因**：
1. 向量索引未构建或已损坏
2. 问题与知识库内容不匹配
3. 相似度阈值设置过于严格

**解决**：
1. 运行 `python scripts/build_index.py` 重建索引
2. 检查 `knowledge/` 目录是否有PDF文件
3. 适当调高 `DISTANCE_THRESHOLD`（如0.5）

### Q3: 问答返回 "AI模型调用失败: Error code: 401"

**原因**：阿里云百炼API密钥无效或未配置。

**解决**：
1. 检查 `.env` 文件中 `DASHSCOPE_API_KEY` 是否正确
2. 确认API Key未过期
3. 确认阿里云账号有余额

### Q4: 首次启动下载模型很慢

**原因**：需要从HuggingFace下载 `BAAI/bge-small-zh-v1.5` 模型。

**解决**：
1. 配置国内镜像源：
   ```bash
   pip install -U huggingface_hub
   export HF_ENDPOINT=https://hf-mirror.com  # Linux
   set HF_ENDPOINT=https://hf-mirror.com     # Windows
   ```
2. 或手动下载模型到本地缓存目录

### Q5: 如何清空知识库重新构建？

```bash
# 删除向量数据库
rm -rf vector_db

# 删除知识库文件
rm -rf knowledge/*

# 放入新文档后重建索引
python scripts/build_index.py
```

### Q6: 如何查看系统日志？

```bash
# 实时查看日志
tail -f logs/app.log

# Windows
Get-Content logs/app.log -Wait
```

---

## 部署说明

### 生产环境部署

#### 1. 使用Gunicorn + Uvicorn

```bash
pip install gunicorn

gunicorn main:app -w 4 -k uvicorn.workers.UvicornWorker --bind 0.0.0.0:8000
```

#### 2. 使用Docker部署

```dockerfile
FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 8000

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
```

构建并运行：
```bash
docker build -t smart-party-ai .
docker run -d -p 8000:8000 --env-file .env -v $(pwd)/knowledge:/app/knowledge -v $(pwd)/vector_db:/app/vector_db smart-party-ai
```

#### 3. Nginx反向代理配置

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://127.0.0.1:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 性能优化建议

1. **模型缓存**：确保Embedding模型已下载到本地，避免每次启动重复下载
2. **向量索引预热**：系统启动时会自动预热，无需额外操作
3. **日志轮转**：生产环境建议配置日志轮转，避免日志文件过大
4. **监控**：可通过 `/api/health` 和 `/api/status` 实现健康检查监控

---

## 开发团队

如有问题，请联系开发团队。

---

## 更新日志

### v1.0.0
- 基础RAG问答功能
- PDF文档解析与向量化
- 前端聊天界面
- API接口完善