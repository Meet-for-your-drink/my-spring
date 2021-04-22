package com.duang.springframework.servletv1;



import java.io.File;

/**
 * @author duang
 * @date 2021-04-21
 * @Describe
 */
public class MyViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFIX=".html";
    private File templateRootDir;

    public MyViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }

    public File getTemplateRootDir() {
        return templateRootDir;
    }

    public void setTemplateRoot(File templateRoot) {
        this.templateRootDir = templateRoot;
    }


    public MyView resolveViewName(String viewName) {
        if(null == viewName || "".equals(viewName.trim())){
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX)? viewName:(viewName+DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir.getPath()+"/"+viewName)
                .replaceAll("/+","/"));
        return new MyView(templateFile);
    }
}
