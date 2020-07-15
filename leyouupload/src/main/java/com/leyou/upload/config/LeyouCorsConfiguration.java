package com.leyou.upload.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class LeyouCorsConfiguration {
    @Bean
    public CorsFilter corsFilter() {
        //1.初始化CORS配置信息
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedMethod("*");//3) 允许的请求方式
        corsConfiguration.addAllowedHeader("*");  // 4）允许的头信息
        corsConfiguration.addAllowedOrigin("http://manage.leyou.com");// 1）允许的域,注意要写上协议http
        corsConfiguration.setAllowCredentials(true);//2) 是否发送Cookie信息
        //2初始化cors配置源对象
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        //3.返回新的CorsFilter.
        return new CorsFilter(corsConfigurationSource);
    }
}
