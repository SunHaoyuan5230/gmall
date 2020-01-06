package com.atguigu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @Auther: SunHaoyuan
 * @Date: 2020/1/5 20:31
 * @Description:
 */

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter(){
        CorsConfiguration config = new CorsConfiguration();
        // 允许的域名
        config.addAllowedOrigin("http://localhost:1000");
        // 允许的头信息
        config.addAllowedHeader("*");
        // 允许的请求方法
        config.addAllowedMethod("*");
        // 是否开启认证：即携带cookie信息
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",config);
        return new CorsWebFilter(configurationSource);
    }
}