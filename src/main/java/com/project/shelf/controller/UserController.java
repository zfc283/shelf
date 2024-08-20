package com.project.shelf.controller;

import com.project.shelf.req.UserLoginReq;
import com.project.shelf.req.UserQueryReq;
import com.project.shelf.req.UserResetPasswordReq;
import com.project.shelf.req.UserSaveReq;
import com.project.shelf.resp.CommonResp;
import com.project.shelf.resp.UserLoginResp;
import com.project.shelf.resp.UserQueryResp;
import com.project.shelf.resp.PageResp;
import com.project.shelf.service.UserService;
import com.project.shelf.util.SnowFlake;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SnowFlake snowFlake;

    @GetMapping("/list")
    public CommonResp list(@Valid UserQueryReq req) {        // 使用Java Bean接收请求参数. UserReq类需要对每个成员定义 getter 和 setter 才能成功接收
        CommonResp<PageResp<UserQueryResp>> commonResp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        commonResp.setContent(list);
        return commonResp;
    }

    @PostMapping
    public CommonResp insert(@Valid @RequestBody UserSaveReq req) {
        CommonResp commonResp = new CommonResp<>();
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        userService.insert(req);
        return commonResp;
    }

    @PutMapping
    public CommonResp update(@Valid @RequestBody UserSaveReq req) {
        CommonResp commonResp = new CommonResp<>();
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        userService.update(req);
        return commonResp;
    }

    @DeleteMapping("/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp commonResp = new CommonResp<>();
        userService.delete(id);
        return commonResp;
    }

    @PutMapping("/password")
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req) {
        CommonResp commonResp = new CommonResp<>();
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        userService.resetPassword(req);
        return commonResp;
    }

    @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody UserLoginReq req) {
        CommonResp<UserLoginResp> commonResp = new CommonResp<>();
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        UserLoginResp loginResp = userService.login(req);
        Long token = snowFlake.nextId();
        loginResp.setToken(token);
        redisTemplate.opsForValue().set(token.toString(), loginResp, 3600 * 24, TimeUnit.SECONDS);
        commonResp.setContent(loginResp);
        return commonResp;
    }

    @PostMapping("/logout")
    public CommonResp logout(@RequestBody String token) {
        CommonResp<UserLoginResp> commonResp = new CommonResp<>();
        redisTemplate.delete(token);
        return commonResp;
    }
}
