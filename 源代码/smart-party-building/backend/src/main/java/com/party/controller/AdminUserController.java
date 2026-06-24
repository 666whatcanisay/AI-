package com.party.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.party.common.Result;
import com.party.dto.LoginDTO;
import com.party.entity.AdminUser;
import com.party.security.JwtUtil;
import com.party.service.AdminUserService;
import com.party.vo.LoginVO;
import com.party.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员管理", description = "管理员登录、信息管理接口")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final JwtUtil jwtUtil;

    public AdminUserController(AdminUserService adminUserService, JwtUtil jwtUtil) {
        this.adminUserService = adminUserService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        AdminUser admin = adminUserService.findByUsername(loginDTO.getUsername());
        if (admin == null) {
            return Result.error("用户名或密码错误");
        }
        if (!loginDTO.getPassword().equals(admin.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), "ADMIN");
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        UserVO userVO = new UserVO();
        userVO.setId(admin.getId());
        userVO.setUsername(admin.getUsername());
        userVO.setRole("ADMIN");
        userVO.setRealName(admin.getRealName());
        loginVO.setUser(userVO);
        return Result.success(loginVO);
    }

    @GetMapping
    @Operation(summary = "获取管理员列表")
    public Result<IPage<AdminUser>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username) {
        Page<AdminUser> page = new Page<>(pageNum, pageSize);
        QueryWrapper<AdminUser> wrapper = new QueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like("username", username);
        }
        wrapper.orderByDesc("create_time");
        IPage<AdminUser> result = adminUserService.page(page, wrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取管理员详情")
    public Result<AdminUser> getById(@PathVariable Long id) {
        AdminUser admin = adminUserService.getById(id);
        return Result.success(admin);
    }

    @PostMapping
    @Operation(summary = "添加管理员")
    public Result<AdminUser> add(@RequestBody AdminUser admin) {
        AdminUser existing = adminUserService.findByUsername(admin.getUsername());
        if (existing != null) {
            return Result.error("用户名已存在");
        }
        admin.setCreateTime(LocalDateTime.now());
        adminUserService.save(admin);
        return Result.success(admin);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新管理员")
    public Result<AdminUser> update(@PathVariable Long id, @RequestBody AdminUser admin) {
        admin.setId(id);
        if (admin.getPassword() == null || admin.getPassword().isEmpty()) {
            AdminUser existing = adminUserService.getById(id);
            admin.setPassword(existing.getPassword());
        }
        adminUserService.updateById(admin);
        return Result.success(admin);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除管理员")
    public Result<String> delete(@PathVariable Long id) {
        adminUserService.removeById(id);
        return Result.success("删除成功");
    }
}
