package com.project.shelf.controller;

import com.project.shelf.exception.BusinessException;
import com.project.shelf.resp.CommonResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理、数据预处理等
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {
    /**
     * 校验异常统一处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public CommonResp validExceptionHandler(BindException e) {
        CommonResp commonResp = new CommonResp();
        log.warn("Parameter validation fails：{}", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        commonResp.setSuccess(false);
        commonResp.setMessage(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return commonResp;
    }

    /**
     * 业务异常统一处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public CommonResp businessExceptionHandler(BusinessException e) {
        CommonResp commonResp = new CommonResp();
        log.warn("Business exception：{}", e.getCode().getDesc());
        commonResp.setSuccess(false);
        commonResp.setMessage(e.getCode().getDesc());
        return commonResp;
    }


    /**
     * 其他异常拦截
     * @param ex 异常
     * @return 接口响应
     */

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResp handleException(Exception ex) {
        CommonResp commonResp = new CommonResp();
        log.error("System exception：", ex);
        commonResp.setSuccess(false);
        commonResp.setMessage("System error occurred. Please contact technical support.");
        return commonResp;
    }
}
