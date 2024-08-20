package com.project.shelf.interceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Resource
    private RedisTemplate redisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //OPTIONS请求不做校验,
        //前后端分离的架构, 前端会发一个OPTIONS请求先做预检, 对预检请求不做校验
        String method = request.getMethod().toUpperCase();
        if(method.equals("OPTIONS")){
            return true;
        }

        String uri = request.getRequestURI();

        if (uri.equals("/api/ebook/list") && method.equals("GET")) {
            return true;
        } else if (uri.equals("/api/category/list") && method.equals("GET")) {
            return true;
        }


        String token = request.getHeader("token");
        log.info("请求 {} token为 {}", uri, token);

        if (ObjectUtils.isEmpty(token)) {
            log.info("token为空，请求 {} 被拦截", uri);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        Object user = redisTemplate.opsForValue().get(token);
        if (ObjectUtils.isEmpty(user)) {
            log.info("token无效，请求 {} 被拦截", uri);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        return true;
    }
}
