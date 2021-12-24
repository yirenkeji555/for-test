package com.zyq.scfunc;

import com.taobao.arthas.compiler.DynamicCompiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

@SpringBootApplication
@Slf4j
@ServletComponentScan
public class ScfuncApplication {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    static String javaEntity = "package com.zyq.scfunc.pojo;\n" +
            "\n" +
            "import com.baomidou.mybatisplus.annotation.TableId;\n" +
            "import com.baomidou.mybatisplus.extension.activerecord.Model;\n" +
            "import lombok.Data;\n" +
            "import lombok.EqualsAndHashCode;\n" +
            "\n" +
            "@Data\n" +
            "@EqualsAndHashCode(callSuper = true)\n" +
            "public class Foo extends Model<Foo> {\n" +
            "    @TableId\n" +
            "    private Integer id;\n" +
            "    private String name;\n" +
            "    public Foo() {this.id = 1;this.name=\"1231\";}\n" +
            "    public void setName1(String name) { this.name = name; }\n" +
            "    public String getName1() { System.out.println(\"print--->\"+this.name); return this.name; }\n" +
            "@Override\n" +
            "public String toString() {\n" +
            "    return \"Foo{\" +\n" +
            "\t    \"name='\" + name + '\\'' +  \n" +
            "\t    '}';\n" +
            "}"+
            "}";

    static String javaMapper = "package com.zyq.scfunc.mapper;\n" +
            "\n" +
            "import com.baomidou.mybatisplus.core.mapper.BaseMapper;\n" +
            "import com.zyq.scfunc.pojo.Foo;\n" +
            "import org.apache.ibatis.annotations.Mapper;\n" +
            "\n" +
            "@Mapper\n" +
            "public interface FooMapper extends BaseMapper<Foo> {\n" +
            "\n" +
            "}";
    static String serviceStr = "package com.zyq.scfunc.service;\n" +
            "\n" +
            "import com.zyq.scfunc.pojo.Foo;\n" +
            "import com.baomidou.mybatisplus.extension.service.IService;\n" +
            "public interface FooService  extends IService<Foo>{\n" +

            "}";
    static String serviceImpStr = "package com.zyq.scfunc.service.impl;\n" +
            "\n" +
            "import com.zyq.scfunc.service.FooService;\n" +
            "import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;\n" +
            "import com.zyq.scfunc.mapper.FooMapper;\n" +
            "import com.zyq.scfunc.pojo.Foo;\n" +
            "import lombok.AllArgsConstructor;\n" +
            "import org.springframework.stereotype.Service;\n" +
            "\n" +
            "@Service\n" +
            "@AllArgsConstructor\n" +
            "public class FooServiceImp extends ServiceImpl<FooMapper, Foo> implements FooService {\n" +
            "\n" +
            "}\n";
    static String controllerStr = "package com.zyq.scfunc.contrller;\n" +
            "\n" +
            "import com.zyq.scfunc.service.FooService;\n" +
            "import lombok.AllArgsConstructor;\n" +
            "import org.springframework.web.bind.annotation.GetMapping;\n" +
            "import org.springframework.web.bind.annotation.RequestMapping;\n" +
            "import org.springframework.web.bind.annotation.RestController;\n" +
            "\n" +
            "\n" +
            "\n" +
            "@RestController\n" +
            "@AllArgsConstructor\n" +
            "@RequestMapping(\"/foo\")\n" +
            "public class FooController {\n" +
            "\n" +
            "    private final FooService fooService;\n" +
            "\n" +
            "    @GetMapping(\"/send\")\n" +
            "    public String sendSmsCode() {\n" +
            "        return \"OK\";\n" +
            "    }\n" +
            "\n" +
            "}";

    public static void main(String[] args) {
        SpringApplication.run(ScfuncApplication.class, args);
        System.out.println("打印 java.class.path-->" + System.getenv("M2_HOME"));
//        System.out.println("打印 java.class.path-->" + System.getProperty("java.class.path"));

        InvocationRequest request = new DefaultInvocationRequest();
//        request.setPomFile(new File("/Users/Excel/work/java/scfunc/pom.xml"));
        request.setBaseDirectory(new File("/Users/Excel/Documents/apc"));
//        request.setGoals(Arrays.asList("archetype:generate", "-DgroupId=tech.yiren", "-DartifactId=app1", "-Dversion=4.1.0", "-Dpackage=tech.yiren.ystart.app1", "-DarchetypeGroupId=tech.yiren.archetype", "-DarchetypeArtifactId=ystart-gen", "-DarchetypeVersion=4.1.0", "-DarchetypeCatalog=local"));

//
//        request.setGoals(Arrays.asList("package"));
//        request.setGoals(Arrays.asList("package"));
        request.setGoals(Collections.singletonList("archetype:generate"));
        Properties properties = new Properties();
        properties.setProperty("groupId", "tech.yiren");
        properties.setProperty("artifactId", "app2");
        properties.setProperty("package", "tech.yiren.ystart.app2");
        properties.setProperty("archetypeVersion", "4.1.0");
        properties.setProperty("archetypeGroupId", "tech.yiren.archetype");
        properties.setProperty("archetypeArtifactId", "ystart-gen");
        properties.setProperty("archetypeCatalog", "local");
        request.setProperties(properties);
        request.setBatchMode(true); // 默认使用true
        Invoker invoker = new DefaultInvoker();
        try {
            invoker.setMavenHome(new File("/Users/Excel/Documents/apache-maven-3.6.3"));
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }

        String feild = "cheng_li_ri_qi";
        String objType = "String";
        String a = GenCodeUtil.genSetAndGet(feild,objType);
        System.out.println(a);

        try {
            DynamicCompiler dynamicCompiler = new DynamicCompiler(Thread.currentThread().getContextClassLoader());
            dynamicCompiler.addSource("Foo", javaEntity);
            dynamicCompiler.addSource("FooMapper", javaMapper);
            dynamicCompiler.addSource("FooService", serviceStr);
            dynamicCompiler.addSource("FooServiceImp", serviceImpStr);
            dynamicCompiler.addSource("FooController", controllerStr);

            Map<String, Class<?>> compiled = dynamicCompiler.build();

            ClassLoader classLoader = dynamicCompiler.getClassLoader();
//            Class<?> FooClazz  =  classLoader.loadClass("com.zyq.scfunc.pojo.Foo");
//
//            Field[] fields = FooClazz.getFields();
//            System.out.println("----fields----");
//            System.out.println(fields);
//            System.out.println("----fields  length----");
//            System.out.println(fields.length);
//            Field[] fields1 = FooClazz.getDeclaredFields();
//            for (int i = 0; i < fields1.length; i++) {
//                Field method = fields1[i];
//                System.out.println("Field-->" + method.getName());
//            }
//
//            Method[] methods = FooClazz.getMethods();
//            for (int i = 0; i < methods.length; i++) {
//                Method method = methods[i];
//                System.out.println("method-->" + method.getName());
//            }
//
//            Object obj = FooClazz.newInstance();
//
//            Method setId = FooClazz.getMethod("setName1", String.class);
//
////            setId.invoke(obj, "123");
//
//            System.out.println(obj);



            ConfigurableApplicationContext applicationContext =
                    (ConfigurableApplicationContext)SpringContextHolder.getApplicationContext();

            BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry)applicationContext.getBeanFactory();

//            Class<?> FooServiceImp  =  classLoader.loadClass("com.zyq.scfunc.service.impl.FooServiceImp");

            Class<?> FooEntity  =  compiled.get("com.zyq.scfunc.pojo.Foo");
            Class<?> FooMapper  =  compiled.get("com.zyq.scfunc.mapper.FooMapper");
            Class<?> FooService  =  compiled.get("com.zyq.scfunc.service.FooService");
            Class<?> FooServiceImp  =  compiled.get("com.zyq.scfunc.service.impl.FooServiceImp");
            Class<?> FooController  =  compiled.get("com.zyq.scfunc.contrller.FooController");


//            Class<?> FooEntity  = classLoader.loadClass("com.zyq.scfunc.pojo.Foo");

//            Class<?> entityType = Class.forName("com.zyq.scfunc.pojo.Foo");


            Object obj = FooEntity.newInstance();
//
            Method setId = FooEntity.getMethod("setName1", String.class);
            Method getName1 = FooEntity.getMethod("getName1");

            setId.invoke(obj, "123");
            getName1.invoke(obj);

            System.out.println(obj);



//            BeanDefinitionBuilder beanDefinitionBuilderEntity = BeanDefinitionBuilder.genericBeanDefinition(FooEntity);
            BeanDefinitionBuilder beanDefinitionBuilderMap = BeanDefinitionBuilder.genericBeanDefinition(FooMapper);
            BeanDefinitionBuilder beanDefinitionBuilderService = BeanDefinitionBuilder.genericBeanDefinition(FooService);
            BeanDefinitionBuilder beanDefinitionBuilderServiceImp = BeanDefinitionBuilder.genericBeanDefinition(FooServiceImp);
            BeanDefinitionBuilder beanDefinitionBuilderController = BeanDefinitionBuilder.genericBeanDefinition(FooController);
//
//
//            // 设置构造器参数
////            beanDefinitionBuilderServiceImp.addConstructorArgValue("com.zyq.scfunc.service.impl.FooServiceImp");
//
//            beanFactory.registerBeanDefinition("com.zyq.scfunc.pojo.Foo", beanDefinitionBuilderEntity.getBeanDefinition());
//            beanFactory.registerBeanDefinition("com.zyq.scfunc.mapper.FooMapper", beanDefinitionBuilderMap.getBeanDefinition());
////            beanFactory.registerBeanDefinition("com.zyq.scfunc.service.FooService", beanDefinitionBuilderService.getBeanDefinition());
//            beanFactory.registerBeanDefinition("com.zyq.scfunc.service.impl.FooServiceImp", beanDefinitionBuilderServiceImp.getBeanDefinition());
////            beanFactory.registerBeanDefinition("com.zyq.scfunc.service.impl.FooServiceImp", beanDefinitionBuilder.getBeanDefinition());
//            beanFactory.registerBeanDefinition("com.zyq.scfunc.contrller.FooController", beanDefinitionBuilderController.getBeanDefinition());
//
//

            // 这里通过builder直接生成了mycontrooler的definition，然后注册进去
            RequestMappingHandlerMapping requestMappingHandlerMapping=(RequestMappingHandlerMapping)applicationContext.getBean("requestMappingHandlerMapping");
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(FooController);
//            defaultListableBeanFactory.registerBeanDefinition("com.zyq.scfunc.mapper.FooMapper", beanDefinitionBuilderMap.getBeanDefinition());
            defaultListableBeanFactory.registerBeanDefinition("FooServiceImp", beanDefinitionBuilderServiceImp.getBeanDefinition());
            defaultListableBeanFactory.registerBeanDefinition("FooController", beanDefinitionBuilder.getBeanDefinition());
            Method method=requestMappingHandlerMapping.getClass().getSuperclass().getSuperclass().getDeclaredMethod("detectHandlerMethods",Object.class);
            method.setAccessible(true);
            method.invoke(requestMappingHandlerMapping,"FooController");
            // 注册Bean
            // 设置构造器参数
//            beanDefinitionBuilder.addConstructorArgValue("FooServiceImp");

//            Class<?> FooMapper  =  classLoader.loadClass("com.zyq.scfunc.mapper.FooMapper");
//            Class<?> FooService  =  classLoader.loadClass("com.zyq.scfunc.service.FooService");
//            Class<?> FooServiceImp  =  classLoader.loadClass("com.zyq.scfunc.service.impl.FooServiceImp");
//            Class<?> FooController  =  classLoader.loadClass("com.zyq.scfunc.contrller.FooController");

//            String tName = "com.zyq.scfunc.service.impl.FooServiceImp";
//            Class cls = SpringContextUtil.getBean(tName).getClass();
//            Method m = cls.getDeclaredMethod("count");
//            Object o = m.invoke(SpringContextUtil.getBean(tName));
//            Class<?> serviceImplType1 = compiled.get("com.zyq.scfunc.service.impl.FooServiceImp");
//            Method method1 = serviceImplType1.getMethod("count");
//            ApplicationContext applicationContext = SpringBootBeanUtil.getApplicationContext();
//            Class<?> ServiceImplType = Class.forName("com.zyq.scfunc.service.impl.FooServiceImp");
//            // 这个count是mybatis-plus service里面内置的方法
//            Method method = FooServiceImp.getMethod("count");
//            //在ApplicationContext中根据class取出已实例化的bean
//            Object o = method.invoke(applicationContext.getBean("com.zyq.scfunc.service.impl.FooServiceImp"));
//            System.out.println(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}