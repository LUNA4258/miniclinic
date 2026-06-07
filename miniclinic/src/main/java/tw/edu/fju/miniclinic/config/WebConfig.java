package tw.edu.fju.miniclinic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import tw.edu.fju.miniclinic.interceptor.LoginRequiredInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginRequiredInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
            .addPathPatterns(
                "/dashboard",
                "/dashboard/**",
                "/api/auth/me",
                "/api/appointments/*/status",
                "/password",
                "/password/**"
            )
            .excludePathPatterns(
                "/login",
                "/logout",
                "/api/appointments",     // 排除建立掛號的 API，允許未登入存取
                "/appointments/**",      // 如果有前端掛號頁面，也可以在這裡排除
                "/api/stats"             // 排除統計摘要 API，供外部 AI agent 驗收
            );
    }
}