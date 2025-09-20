package com.ecommerce.jsf.auth;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

@WebFilter("/*")
public class JwtCookieFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if ("JWT".equals(cookie.getName())) {
                    HttpServletRequestWrapper wrapped = new HttpServletRequestWrapper(req) {
                        @Override
                        public String getHeader(String name) {
                            if ("Authorization".equalsIgnoreCase(name)) {
                                return "Bearer " + cookie.getValue();
                            }
                            return super.getHeader(name);
                        }
                    };
                    chain.doFilter(wrapped, response);
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }
}