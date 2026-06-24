package com.party.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.party.common.Result;
import com.party.dto.LoginDTO;
import com.party.entity.Application;
import com.party.entity.PartyMember;
import com.party.entity.SysUser;
import com.party.entity.TransferApplication;
import com.party.security.JwtUtil;
import com.party.service.ApplicationService;
import com.party.service.PartyMemberService;
import com.party.service.TransferApplicationService;
import com.party.service.UserService;
import com.party.vo.LoginVO;
import com.party.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "登录、管理员管理接口")
public class UserController {

    private final UserService userService;
    private final PartyMemberService partyMemberService;
    private final ApplicationService applicationService;
    private final TransferApplicationService transferApplicationService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService,
                          PartyMemberService partyMemberService,
                          ApplicationService applicationService,
                          TransferApplicationService transferApplicationService,
                          JwtUtil jwtUtil) {
        this.userService = userService;
        this.partyMemberService = partyMemberService;
        this.applicationService = applicationService;
        this.transferApplicationService = transferApplicationService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public Result<LoginVO> adminLogin(@RequestBody LoginDTO loginDTO) {
        SysUser user = userService.findByUsername(loginDTO.getUsername());
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        if (!loginDTO.getPassword().equals(user.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "ADMIN");
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setRole("ADMIN");
        userVO.setRealName(user.getRealName());
        loginVO.setUser(userVO);
        return Result.success(loginVO);
    }

    /**
     * 党员/申请人登录（用姓名登录）
     */
    @PostMapping("/portal/login")
    @Operation(summary = "党员/申请人登录")
    public Result<LoginVO> portalLogin(@RequestBody LoginDTO loginDTO) {
        String name = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        // 先查 party_member 表
        QueryWrapper<PartyMember> memberWrapper = new QueryWrapper<>();
        memberWrapper.eq("name", name).last("LIMIT 1");
        PartyMember member = partyMemberService.getOne(memberWrapper, false);

        if (member != null) {
            if (member.getPassword() == null || !member.getPassword().equals(password)) {
                return Result.error("用户名或密码错误");
            }
            String role = "member";
            String token = jwtUtil.generateToken(member.getId(), member.getName(), role);
            LoginVO loginVO = new LoginVO();
            loginVO.setToken(token);
            UserVO userVO = new UserVO();
            userVO.setId(member.getId());
            userVO.setUsername(member.getName());
            userVO.setRole(role);
            userVO.setMemberId(member.getId());
            userVO.setRealName(member.getName());
            loginVO.setUser(userVO);
            return Result.success(loginVO);
        }

        // 再查 application 表
        QueryWrapper<TransferApplication> transferredWrapper = new QueryWrapper<>();
        transferredWrapper.eq("member_name", name).eq("status", "已通过").last("LIMIT 1");
        if (transferApplicationService.count(transferredWrapper) > 0) {
            return Result.error("该党员已转出，无法登录本系统");
        }

        QueryWrapper<Application> appWrapper = new QueryWrapper<>();
        appWrapper.eq("name", name).orderByDesc("create_time").last("LIMIT 1");
        Application app = applicationService.getOne(appWrapper, false);

        if (app != null) {
            if (app.getPassword() == null || !app.getPassword().equals(password)) {
                return Result.error("用户名或密码错误");
            }
            String role = "applicant";
            String token = jwtUtil.generateToken(app.getId(), app.getName(), role);
            LoginVO loginVO = new LoginVO();
            loginVO.setToken(token);
            UserVO userVO = new UserVO();
            userVO.setId(app.getId());
            userVO.setUsername(app.getName());
            userVO.setRole(role);
            userVO.setRealName(app.getName());
            loginVO.setUser(userVO);
            return Result.success(loginVO);
        }

        return Result.error("用户名或密码错误");
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息")
    public Result<String> getUserInfo() {
        return Result.success("success");
    }

    @PostMapping("/logout")
    @Operation(summary = "用户退出")
    public Result<String> logout() {
        return Result.success("退出成功");
    }

    // ============ 管理员CRUD ============

    @GetMapping
    @Operation(summary = "获取管理员列表")
    public Result<IPage<SysUser>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like("username", username);
        }
        wrapper.orderByDesc("create_time");
        IPage<SysUser> result = userService.page(page, wrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取管理员详情")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        return Result.success(user);
    }

    @PostMapping
    @Operation(summary = "添加管理员")
    public Result<SysUser> add(@RequestBody SysUser user) {
        SysUser existing = userService.findByUsername(user.getUsername());
        if (existing != null) {
            return Result.error("用户名已存在");
        }
        user.setRole("ADMIN");
        user.setCreateTime(LocalDateTime.now());
        userService.save(user);
        return Result.success(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新管理员")
    public Result<SysUser> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            SysUser existing = userService.getById(id);
            user.setPassword(existing.getPassword());
        }
        user.setRole("ADMIN");
        userService.updateById(user);
        return Result.success(user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除管理员")
    public Result<String> delete(@PathVariable Long id) {
        userService.removeById(id);
        userService.resetAutoIncrement();
        return Result.success("删除成功");
    }
}
