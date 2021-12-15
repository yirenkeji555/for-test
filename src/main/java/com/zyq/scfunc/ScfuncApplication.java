package com.zyq.scfunc;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

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
    static String serviceImpStr = "package com.zyq.scfunc.service;\n" +
            "\n" +
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

    public static void main(String[] args) {
        SpringApplication.run(ScfuncApplication.class, args);
        System.out.println("打印 java.class.path-->" + System.getProperty("java.class.path"));

        /**
         * 以下是打印jar包里面的内容
         */
//        Resource[] resources = new PathMatchingResourcePatternResolver().
//                getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "BOOT-INF/lib/*.jar");
//        for(Resource resource : resources) {
//            System.out.println("getFilename-->" + resource.getFilename());
//            System.out.println("getURI-->" + resource.getURI());
//            System.out.println("getURL-->" + resource.getURL());
//        }

        try {
            List<JavaSrc> javaSrcs = new ArrayList<>();

            JavaSrc javaSrc1 = new JavaSrc("Foo.java", javaEntity);
            javaSrcs.add(javaSrc1);

            JavaSrc javaSrc22 = new JavaSrc("FooMapper.java", javaMapper);
            javaSrcs.add(javaSrc22);

            JavaSrc javaSrcService = new JavaSrc("FooService.java", serviceStr);
            javaSrcs.add(javaSrcService);


            JavaSrc javaSrcServiceImp = new JavaSrc("FooServiceImp.java", serviceImpStr);
            javaSrcs.add(javaSrcServiceImp);

            URL classPathUrl = new URL("file://" + System.getProperty("java.class.path"));

            // 手动设置 classpath
            DynamicLoader.classpaths = new URL[]{classPathUrl};

            Map<String, byte[]> bytecode = DynamicLoader.compile(javaSrcs);

            DynamicLoader.MemoryClassLoader classLoader = new DynamicLoader.MemoryClassLoader(bytecode);

            Class clazzEntity = classLoader.loadClass("com.zyq.scfunc.pojo.Foo");
            Class clazzMapper = classLoader.loadClass("com.zyq.scfunc.mapper.FooMapper");
            Class clazzService = classLoader.loadClass("com.zyq.scfunc.service.FooService");
            Class clazzServiceImp = classLoader.loadClass("com.zyq.scfunc.service.FooServiceImp");


            //从ApplicationContext中取出已创建好的的对象
            //不可直接反射创建serviceimpi对象，因为反射创建出来的对象无法实例化dao接口
            ApplicationContext applicationContext = SpringBootBeanUtil.getApplicationContext();
            //反射创建serviceimpi实体对象，和实体类
            Object entity = clazzEntity.newInstance();
            Class<?> ServiceImplType = Class.forName("com.zyq.scfunc.service.FooServiceImp");
            // 这个count是mybatis-plus service里面内置的方法
            Method method = ServiceImplType.getMethod("count");
            //在ApplicationContext中根据class取出已实例化的bean

            Object o = method.invoke(applicationContext.getBean(ServiceImplType));
            System.out.println("\n执行结果 --> result： " + o); // 数据库有3条数据，理论上输出3
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}