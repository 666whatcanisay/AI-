package com.party.controller;

import com.party.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI助手", description = "智慧党建AI知识助手接口")
public class AiController {

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate;

    public AiController() {
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/status")
    @Operation(summary = "获取AI服务状态")
    public Result<Map<String, Object>> getStatus() {
        try {
            String url = aiServiceUrl + "/api/status";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> data = new HashMap<>();
            if (response.getBody() != null) {
                data.put("ready", response.getBody().get("ready"));
                data.put("message", response.getBody().get("message"));
                data.put("aiUrl", aiServiceUrl);
            }
            return Result.success(data);
        } catch (RestClientException e) {
            Map<String, Object> data = new HashMap<>();
            data.put("ready", false);
            data.put("message", "AI服务未启动，请联系管理员");
            data.put("aiUrl", aiServiceUrl);
            return Result.success(data);
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "获取AI服务统计信息")
    public Result<Map<String, Object>> getStats() {
        try {
            String url = aiServiceUrl + "/api/stats";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getBody() != null) {
                return Result.success(response.getBody());
            }
            return Result.error("获取统计信息失败");
        } catch (RestClientException e) {
            return Result.error("AI服务未启动: " + e.getMessage());
        }
    }

    @PostMapping("/chat")
    @Operation(summary = "发送问题获取AI回答")
    public Result<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            if (message == null || message.trim().isEmpty()) {
                return Result.error("问题不能为空");
            }

            String url = aiServiceUrl + "/api/chat";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> body = new HashMap<>();
            body.put("message", message);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            if (response.getBody() != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("answer", response.getBody().get("answer"));
                result.put("sources", response.getBody().get("sources"));
                return Result.success(result);
            }
            return Result.error("获取回答失败");
        } catch (RestClientException e) {
            return Result.error("AI服务未启动: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查")
    public Result<Map<String, Object>> health() {
        try {
            String url = aiServiceUrl + "/api/health";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> data = new HashMap<>();
            if (response.getBody() != null) {
                data.put("status", response.getBody().get("状态"));
                data.put("connected", true);
            }
            return Result.success(data);
        } catch (RestClientException e) {
            Map<String, Object> data = new HashMap<>();
            data.put("status", "AI服务未连接");
            data.put("connected", false);
            return Result.success(data);
        }
    }
}