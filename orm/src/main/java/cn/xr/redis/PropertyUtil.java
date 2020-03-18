package cn.xr.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;

/**
 * 加载指定配置文件获取PropertySource对象
 *
 * @author liuliuliu
 * @since 2019/11/28
 */
@Slf4j
public class PropertyUtil {
    /**
     * 加载指定配置文件获取PropertySource对象
     *
     * @param fileName 文件名称
     * @return 指定文件的{@link PropertySource}对象，可能为空
     */
    public static PropertySource get(String fileName) throws IOException {
        return get(fileName, new Hashtable<>());
    }

    /**
     * 加载指定配置文件获取PropertySource对象
     *
     * @param fileName 文件名称
     * @return 指定文件的{@link PropertySource}对象，可能为空
     */
    @SuppressWarnings("unchecked")
    private static PropertySource get(String fileName, Hashtable<Object, Object> parentData) throws IOException {
        Assert.notNull(parentData, "parentData must no be null");
        String activeProfileKey = "spring.profiles.active";
        String suffix = ".yml";
        Resource resourceClass = new ClassPathResource(fileName);
        Object activeProfile;
        PropertySource propertySource;
        URL url = null;
        try {
            url = resourceClass.getURL();
        } catch (IOException ignored) {

        }
        if (url == null && fileName.endsWith(suffix)) {
            fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".properties";
            resourceClass = new ClassPathResource(fileName);
            try {
                resourceClass.getURL();
            } catch (IOException e) {
                throw new RuntimeException("加载配置文件错误!");
            }
            EncodedResource resource = new EncodedResource(resourceClass);
            ResourcePropertySource resourcePropertySource = new ResourcePropertySource(resource);
            activeProfile = resourcePropertySource.getProperty(activeProfileKey);
            propertySource = resourcePropertySource;
        } else {
            EncodedResource resource = new EncodedResource(resourceClass);
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();
            Properties propertiesFromYaml = (factory.getObject());
            Assert.notNull(resourceClass, "resourceClass must not be null");
            String filename = resourceClass.getFilename();
            Assert.notNull(filename, "filename must not be null");
            Assert.notNull(propertiesFromYaml, "propertiesFromYaml must not be null");
            PropertiesPropertySource propertiesPropertySource = new PropertiesPropertySource(filename, propertiesFromYaml);
            activeProfile = propertiesPropertySource.getProperty(activeProfileKey);
            propertySource = propertiesPropertySource;
        }
        Object source = propertySource.getSource();
        if (!ObjectUtils.isEmpty(activeProfile)) {
            parentData.putAll((Hashtable) source);
            String substring = fileName.substring(fileName.lastIndexOf("."));
            fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "-" + activeProfile.toString() + substring;
            return get(fileName, parentData);
        }
        ((Hashtable) source).putAll(parentData);
        return propertySource;
    }

    public static void main(String[] args) {
        try {
            PropertySource propertySource = PropertyUtil.get("application.yml");
            Object database = propertySource.getProperty("spring.redis.database");
            Object host = propertySource.getProperty("spring.redis.host");
            Object port = propertySource.getProperty("spring.redis.port");
            Object password = propertySource.getProperty("spring.redis.password");
            log.info("database:{}", database);
            log.info("host:{}", host);
            log.info("database:{}", port);
            log.info("database:{}", password);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
