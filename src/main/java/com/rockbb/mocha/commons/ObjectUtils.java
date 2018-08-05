package com.rockbb.mocha.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ObjectUtils {
	private static Logger logger = LoggerFactory.getLogger(ObjectUtils.class);

	/**
     * 获取当前包路径下的所有类
     *
     * @param packageName 包名
     * @param recursive 是否查找子目录
     */
    public static List<Class<?>> extractClasses(String packageName, boolean recursive) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    logger.info("Scan file under path");
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    fetchClassesInPackage(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    logger.info("Scan files in jar");
                    JarFile jar;
                    try {
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.charAt(0) == '/') {
                                name = name.substring(1);
                            }
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            logger.error(e.getMessage(), e);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return classes;
    }

    public static void fetchClassesInPackage(
            String packageName,
            String packagePath,
            final boolean recursive,
            List<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles(
                new FileFilter() {
                    public boolean accept(File file) {
                        return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
                    }
                });
        for (File file : files) {
            if (file.isDirectory()) {
                fetchClassesInPackage(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 用forName会触发static方法，没有使用classLoader的load干净
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }


	public static <T> Map<String, List<String>> compare(T a, T b, Class<T> t) {
		Map<String, List<String>> diff = new HashMap<String, List<String>>();
		if (a == null || b == null || t == null) return diff;

		Field[] fields = t.getDeclaredFields();
		if (fields != null) {
			for (Field field : fields) {
				try {
					field.setAccessible(true);
					Object property1 = field.get(a);
					Object property2 = field.get(b);
					if (property1 == null && property2 == null) {
						// Do nothing
					} else if (property1 == null && property2 != null) {
						diff.put(field.getName(), Arrays.asList("null", property2.toString()));
					} else if (property2 == null && property1 != null) {
						diff.put(field.getName(), Arrays.asList(property1.toString(), "null"));
					} else if (!property1.equals(property2)) {
						diff.put(field.getName(), Arrays.asList(property1.toString(), property2.toString()));
					}
				} catch (IllegalAccessException e) {
					logger.error("IllegalAccessException:", e);
				} catch (Exception e) {
					logger.error("Exception:", e);
				} finally {
					field.setAccessible(false);
				}
			}
		}

		return diff;
	}

    public static Object newInstanceByName(String className) {
		Object obj = null;
		try {
			obj = (Thread.currentThread().getContextClassLoader().loadClass(className)).newInstance();
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFoundException", e);
		} catch (InstantiationException e) {
			logger.error("InstantiationException", e);
		} catch (IllegalAccessException e) {
			logger.error("IllegalAccessException", e);
		} catch (Exception e) {
			logger.error("Unknown Exception", e);
		}
		return obj;
	}

	public static void main(String[] args) {
        List<Class<?>> classes = extractClasses("com.rockbb.jshadow.web.controller", true);
    }

}
