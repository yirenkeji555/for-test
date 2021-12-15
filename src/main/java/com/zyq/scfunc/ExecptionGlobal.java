package com.zyq.scfunc;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Order(1000)
@WebFilter(filterName = "checkLogin", urlPatterns = "/*", asyncSupported = true)
public class ExecptionGlobal implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.logger.info("ExecptionGlobal过滤器创建");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        this.logger.info("requestPath========>" + path);

        Map<String, String> headers = Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(h -> h, request::getHeader));
        this.logger.info("headerNames==>" + headers);
        this.logger.info("request==>" + request);
        this.logger.info("request params ==>{}", request.getParameterMap().entrySet().stream().collect(Collectors.toList()));
        this.logger.info("request params ==>{}", request.getParameterMap().values().stream().collect(Collectors.toList()));
        this.logger.info("response==>" + response.toString());
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
