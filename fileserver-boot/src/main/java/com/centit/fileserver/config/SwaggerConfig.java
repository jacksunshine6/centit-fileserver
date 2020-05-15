package com.centit.fileserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public Docket buildDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(buildApiInf())//.pathMapping("../service")
            .select()
            .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class) )
            //.apis(RequestHandlerSelectors.basePackage("com.centit.framework.system.controller"))//controller路径
            //.apis(RequestHandlerSelectors.basePackage("com.otherpackage.controller"))//controller路径
            .paths(PathSelectors.any())
            .build();
    }

    private ApiInfo buildApiInf(){
        return new ApiInfoBuilder()
            .title("文件服务框架接口")
            .termsOfServiceUrl("")
            .description("文件服务框架接口")
            .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
