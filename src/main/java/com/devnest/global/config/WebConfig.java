package com.devnest.global.config;

// 외부 폴더에서 정적 리소스를 매핑시키기 위해

import com.devnest.global.interceptor.HotTopicInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final HotTopicInterceptor hotTopicInterceptor;

    @Autowired
    public WebConfig(HotTopicInterceptor hotTopicInterceptor) {
        this.hotTopicInterceptor = hotTopicInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/images/");

        // 질문 글 이미지 업로드
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(hotTopicInterceptor)
                .addPathPatterns("/topics/**")
                .excludePathPatterns();
    }
}
