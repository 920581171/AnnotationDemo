package com.luoyk.annotation.demo.register;

import com.luoyk.annotation.demo.anno.EnableRestClients;
import com.luoyk.annotation.demo.anno.RestClient;
import com.luoyk.annotation.demo.bean.RestFactoryBean;
import com.luoyk.annotation.demo.handler.CGLibHandler;
import com.luoyk.annotation.demo.handler.JdkHandler;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 实现ImportBeanDefinitionRegistrar用于注册bean
 * 以Aware结尾的接口是标记类，spring会自动装配对应的bean
 */
public class RestClientsRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private Environment environment;

    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        //spring先从所有注解中找到我们所定义的@EnableRestService
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableRestClients.class.getName());

        //获取@EnableRestService中的basePackages参数
        String[] packages = (String[]) annotationAttributes.get("basePackages");
        if (packages.length == 0) {
            //如果没有指定目录，以根目录为目录
            String className = importingClassMetadata.getClassName();
            String basePath = className.substring(0, className.lastIndexOf("."));
            packages = new String[]{basePath};
        }

        /*创建扫描器，重写isCandidateComponent方法
         *isCandidateComponent返回该类是否是候选类
         */
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(
                registry, false, environment, resourceLoader) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                /*
                 * beanDefinition.getMetadata()包含扫描到的类的信息
                 * 这里只有接口才符合我的要求
                 */
                if (!beanDefinition.getMetadata().isInterface()) {
                    System.out.println(beanDefinition.getMetadata().getClassName() + "不是接口");
                    return false;
                }
                return true;
            }
        };

        //添加扫描过滤器，这里指扫描含有@RestClient注解的类
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestClient.class));

        /* 调用scanner.findCandidateComponents()开始扫描候选类
         * 扫描指定包路径下的所有包含@RestClient注解的类
         * 交给ClassPathBeanDefinitionScanner.isCandidateComponent()方法判断是否是候选类
         *
         * 这里使用了java8 stream方法，
         * 其实就是遍历packages，把所有候选类放在一个set里面
         * */
        Set<BeanDefinition> beanDefinitions = Arrays.stream(packages)
                .map(scanner::findCandidateComponents)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        //遍历扫描出来的候选类
        for (BeanDefinition beanDefinition : beanDefinitions) {
            registerBean(beanDefinition.getBeanClassName(), registry);
        }
    }

    /**
     * 代理接口并注册
     *
     * @param className 类名
     */
    public void registerBean(String className, BeanDefinitionRegistry registry) {
        System.out.println("找到了一个接口：" + className);
        try {
            //通过接口名找到该接口类
            Class<?> aClass = Class.forName(className);

            Object object = true ? jdkProxy(aClass) : cglibProxy(aClass);

            //以类名首字母小写作为注册bean的名称
            String simpleName = aClass.getSimpleName();
            String beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);

            //获取Bean定义生成器
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RestFactoryBean.class);
            //可以看到Bean定义生成器是通过Class文件来获取的，所以我们需要调用addPropertyValue来对bean工厂进行赋值
            beanDefinitionBuilder.addPropertyValue("object", object);
            beanDefinitionBuilder.addPropertyValue("type", aClass);
            //注册Bean
            registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
            System.out.println("成功注册Bean：" + beanName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Object jdkProxy(Class<?> aClass) {
        //创建jdk代理处理程序
        JdkHandler jdkHandler = new JdkHandler();
        /*
         * 生成代理对象
         * 第一个参数传入一个ClassLoader
         * 第二个参数传入被代理的接口，是一个数组，
         * 这意味着你可以用一个代理对象去实现多个接口
         * 第三个参数是代理处理程序
         */
        return Proxy.newProxyInstance(aClass.getClassLoader(),
                new Class[]{aClass},
                jdkHandler);
    }

    public Object cglibProxy(Class<?> aClass) {

        //创建CGLib代理程序
        CGLibHandler cgLibHandler = new CGLibHandler();

        //创建增强对象
        Enhancer enhancer = new Enhancer();
        //设置增强对象所继承的类
        enhancer.setSuperclass(aClass);
        //设置控制类
        enhancer.setCallback(cgLibHandler);
        //创建代理对象
        return enhancer.create();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
