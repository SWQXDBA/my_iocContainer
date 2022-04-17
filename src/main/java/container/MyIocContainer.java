package container;


import annotation.AfterBeanInited;
import annotation.MyComponent;
import annotation.MyInject;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyIocContainer {
    private Map<Class<?>,Object> beansByType = new HashMap<>();
    private Map<String,Object> beansByName = new HashMap<>();
    public void run(Class clazz) {
        //全限定名
        final List<String> classNames = getClassName(clazz.getPackageName());
        final ClassLoader classLoader = MyIocContainer.class.getClassLoader();
        //初始化bean
        for (String className : classNames) {
            try {
                final Class<?> aClass = classLoader.loadClass(className);
                try {
                    if(!(aClass.isInterface()|| aClass.isAnnotation())){
                        if(aClass.isAnnotationPresent(MyComponent.class)){
                            final Object bean = aClass.getDeclaredConstructor().newInstance();
                            beansByType.put(bean.getClass(),bean);
                            beansByName.put(bean.getClass().getSimpleName(),bean);
                        }
                    }

                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        //进行注入属性
        beansByType.forEach((k,v)->{
            for (Field declaredField : k.getDeclaredFields()) {
                final Class<?> otherBeanType = declaredField.getType();
                if(declaredField.isAnnotationPresent(MyInject.class)){
                    final Object otherBean = beansByType.get(otherBeanType);
                    try {
                        declaredField.setAccessible(true);
                        declaredField.set(v,otherBean);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            //调用后置通知方法
            for (Method declaredMethod : k.getDeclaredMethods()) {
                if(declaredMethod.isAnnotationPresent(AfterBeanInited.class)){
                    try {
                        declaredMethod.invoke(v);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

        });


    }

    private static List<File> enumAllSubFiles(String topDir) {
        List<File> result = new ArrayList<>();
        File dir = new File(topDir);
        final File[] files = dir.listFiles();
        if (files == null) return result;
        for (File file : files) {
            if (file.isDirectory()) {
                result.addAll(enumAllSubFiles(file.getPath()));
            } else if(file.isFile()){
                result.add(file);
            }
        }
        return result;
    }

    private static List<String> getClassName(String scanPackageName) {
        List<String> names = new ArrayList<>();
        URL url = MyIocContainer.class.getClassLoader().getResource(scanPackageName.replace('.', '/'));
        if (null == url) {
            throw new IllegalStateException("无法通过包名找到路径: " + scanPackageName);
        }
        File dir = new File(url.getFile());
        if (dir.isFile() || !dir.canRead()) {
            throw new IllegalStateException("目录无法读取:" + dir.getAbsolutePath());
        }

        //找出包下所有类
        final List<File> files = enumAllSubFiles(dir.getPath());

        for (File f : files) {

            //getPath是绝对路径 此时用减去 scanPackageName的绝对路径 就是相对路径了 然后转换成包名
            String name = f.getPath().replace(dir.getPath()+"\\","")
                    .replaceAll("\\.class", "")
                    .replace("\\",".");
            String className;
            if(scanPackageName.equals("")){
                className = name;
            }else{
                className = scanPackageName + "." + name;
            }

            names.add(className);

      /*          try {
                    Class clazz = Class.forName(className);

                    System.out.println(className);

                    // 忽略抽象类和接口
                    if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface()) {
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
        }

        return names;

    }


}
