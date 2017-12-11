package com.mccabe.util;

import com.mccabe.Main;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import static com.mccabe.McCabeConfig.log;

public class LibClassLoader {
    public static void loadJarIndDir() throws Exception {
        loadJarIndDir(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent() + File.separator + "lib");
    }

    public static void loadJarIndDir(String dir) {
        try {
            final URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            final Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            log("Load lib path : " + dir);
            new File(dir).listFiles(jar -> {
                // jar 파일인 경우만 로딩
                if (jar.toString().toLowerCase().contains(".jar")) {
                    try {
                        // URLClassLoader.addURL(URL url) 메소드 호출
                        method.invoke(loader, new Object[]{jar.toURI().toURL()});
                        log(jar.getName() + " is loaded.");
                    } catch (Exception e) {
                        log(jar.getName() + " can't load.");
                    }
                }
                return false;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}