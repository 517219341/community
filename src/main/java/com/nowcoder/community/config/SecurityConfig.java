package com.nowcoder.community.config;

import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig implements CommunityConstant {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
//                        "/letter/**",
//                        "/notice/**",
//                        "/like",
                        "/follow",
                        "/unfollow")
                .access(AuthorizationManagers.allOf(AuthorityAuthorizationManager.hasRole(AUTHORITY_ADMIN)
                        , AuthorityAuthorizationManager.hasRole(AUTHORITY_MODERATOR)
                        , AuthorityAuthorizationManager.hasRole(AUTHORITY_USER)))
                .requestMatchers("/resources/**").permitAll()
                .anyRequest().permitAll()
        ).csrf().disable();

        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    // ?????????????????????
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        String XRW = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(XRW)) {     // ?????????????????????
                            response.setContentType("application/plan;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403,"??????????????????"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    // ????????????
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        String XRW = request.getHeader("x-requested-with");
                        if (XRW.equals("XMLHttpRequest")) {     // ?????????????????????
                            response.setContentType("application/plan;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403,"?????????????????????????????????"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

        // security?????????????????????logout???????????????????????????
        //
        http.logout().logoutUrl("/fakeLoginOut");
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return ( web ) -> web.ignoring().requestMatchers("/resources/**");
    }



}
