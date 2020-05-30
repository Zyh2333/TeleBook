package cn.edu.whu.zhuyuhan.demo1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Zhuyuhan
 * @date 2020-05-23 19:41
 */
@Configuration
public class ImageConfig implements WebMvcConfigurer {
    /**上传地址*/
    @Value("${file.upload.path}")
    private String filePath;
    /**显示相对地址*/
    @Value("${file.upload.path.relative}")
    private String fileRelativePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(fileRelativePath).
                addResourceLocations("file:/" + filePath);
    }
}
