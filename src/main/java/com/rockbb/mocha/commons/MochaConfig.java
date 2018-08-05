package com.rockbb.mocha.commons;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MochaConfig {
    private static Logger logger = LoggerFactory.getLogger(MochaConfig.class);

    private Set<String> scanPakcages;
    private List<String> interceptorClasses;
    private Map<String, Object> configs;

    public MochaConfig(String configLocation) {
        this.scanPakcages = new HashSet<>();
        this.interceptorClasses = new ArrayList<>();
        this.configs = new HashMap<>();

        // 创建saxReader对象
        SAXReader reader = new SAXReader();
        // 通过read方法读取一个文件 转换成Document对象
        InputStream is = MochaConfig.class.getResourceAsStream(configLocation);
        try {
            Document document = reader.read(is);
            // 读取待扫描的包名集合
            List<Element> elms = document.getRootElement().elements("component-scan");
            for (Element elm : elms) {
                String packages = elm.attributeValue("base-package");
                String[] packageArray = packages.split("\\s*,\\s*");
                this.scanPakcages.addAll(Arrays.asList(packageArray));
            }
            // 读取拦截器列表
            for (Iterator<Element> it = document.getRootElement().element("interceptors").elementIterator(); it.hasNext();) {
                Element elm = it.next();
                String className = elm.attributeValue("class");
                this.interceptorClasses.add(className);
            }
            // 读取静态配置变量
            elms = document.getRootElement().elements("constant");
            for (Element elm : elms) {
                String name = elm.attributeValue("name");
                String value = elm.attributeValue("value");
                String type = elm.attributeValue("type");
                if (type.equals("int")) {
                    configs.put(name, Integer.parseInt(value));
                } else if (type.equals("string")) {
                    configs.put(name, value);
                } else if (type.equals("array")) {
                    configs.put(name, value.split("\\s*,\\s*"));
                }
            }
        } catch (DocumentException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public List<String> getInterceptorClasses() {return interceptorClasses;}

    public Set<String> getScanPakcages() {
        return scanPakcages;
    }

    public Map<String, Object> getConfigs() {
        return configs;
    }

	public String get(String key) {
        if (configs.get(key) != null) {
            return (String) configs.get(key);
        }
        return null;
	}

	public int getInt(String key) {
		if (configs.get(key) != null) {
            return (Integer) configs.get(key);
        }
		return -1;
	}

	public String[] getArray(String key) {
        if (configs.get(key) != null) {
            return (String[]) configs.get(key);
        }
		return null;
	}
}
