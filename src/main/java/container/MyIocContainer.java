package container;

import container.Sub.sub2.MyIocContainer2;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyIocContainer {
    public void run(Class clazz) {
        final List<String> classNames = getClassName(clazz.getPackageName());
        final ClassLoader classLoader = MyIocContainer.class.getClassLoader();
        for (String className : classNames) {
            try {

                System.out.println(className);
                final Class<?> aClass = classLoader.loadClass(className);
                try {
                    if(!(aClass.isInterface()|| aClass.isAnnotation())){
                        final Object o = aClass.getDeclaredConstructor().newInstance(null);
                        System.out.println(o);
                    }

                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
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
        URL url = MyIocContainer2.class.getClassLoader().getResource(scanPackageName.replace('.', '/'));
        if (null == url) {
            throw new IllegalStateException("无法通过包名找到路径: " + scanPackageName);
        }
        File dir = new File(url.getFile());
        if (dir.isFile() || !dir.canRead()) {
            throw new IllegalStateException("目录无法读取:" + dir.getAbsolutePath());
        }

        final List<File> files = enumAllSubFiles(dir.getPath());

        for (File f : files) {

            String name = f.getPath().replace(dir.getPath()+"\\","").replaceAll("\\.class", "").replace("\\",".");
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
