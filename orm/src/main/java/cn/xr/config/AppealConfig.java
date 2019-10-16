package cn.xr.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;


@Configuration
@Data
public class AppealConfig extends WebMvcConfigurationSupport {
    /**
     * 跨域处理
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //表示所有的请求路径都经过跨域处理
        registry.addMapping("/**")
                .allowedOrigins("*")
                /*.allowedMethods("*")
                .allowedHeaders("*")*/
                .allowedMethods("POST", "GET", "DELETE", "OPTIONS", "PUT")
                .allowedHeaders("Origin", "X-Requested-With", "Content-Type", "Accept", "Content-Length", "remember-me",
                        "auth", "Cookie", "Authorization", "AppId")
                //允许在请求头里存放信息,后端通过请求头来获取前端传来的信息
                .exposedHeaders("Authorization", "AppId")
                //设置是否允许跨域传cookie
                .allowCredentials(true)
                .maxAge(3600);
        super.addCorsMappings(registry);
    }


    /**
     * 对静态资源的配置
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/*")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * json返回中文乱码处理
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .serializationInclusion(JsonInclude.Include.NON_NULL);
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
    }

    @Bean
    public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
        return new ByteArrayHttpMessageConverter();
    }

    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        return new StringHttpMessageConverter();
    }

    @Bean
    public ResourceHttpMessageConverter resourceHttpMessageConverter() {
        return new ResourceHttpMessageConverter();
    }

//    @Bean
//    public FilterRegistrationBean zhjfWebApplicationFilter() {
//        FilterRegistrationBean frBean = new FilterRegistrationBean();
////        frBean.setFilter(new ZhjfWebApplicationFilter());
//        frBean.addUrlPatterns("/*");
//        frBean.addInitParameter("excludePaths","/css/**,/js/**,/druid/**,/swagger-resources,/v2/api-docs,/v2/api-docs-ext,/doc.html,/webjars/**");
//        return frBean;
//    }

}
