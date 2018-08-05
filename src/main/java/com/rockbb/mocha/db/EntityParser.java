package com.rockbb.mocha.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EntityParser {
    private static Logger logger = LoggerFactory.getLogger(EntityParser.class);
    private PropertyDescriptor[] properties;
    private String idKey;

    public <T> EntityParser(Class<T> clazz, String idKey) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
            this.properties = beanInfo.getPropertyDescriptors();
            this.idKey = idKey;
        } catch (IntrospectionException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public <T> ClauseGen getUpdateClause(T bean) {
        ClauseGen clause = new ClauseGen().set();
        try {
            for (int i = 0; i < properties.length; i++) {
                String name = properties[i].getName();
                String column = underscoreName(name);
                if (name.equals(idKey) || column.equals(idKey)) continue;
                clause.comma(column + " = ?", callGetter(bean, properties[i]));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return clause;
    }

    public <T> ClauseGen getInsertClause(T bean) {
        ClauseGen clause = new ClauseGen();
        try {
            clause.append(" (", " VALUES (", null);
            for (int i = 0; i < properties.length; i++) {
                String name = properties[i].getName();
                String column = underscoreName(name);
                if (i == 0) {
                    clause.append(column, "?", callGetter(bean, properties[i]));
                } else {
                    clause.append(", " +column, ", ?", callGetter(bean, properties[i]));
                }
            }
            clause.append(")", ")", null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return clause;
    }

    private <T> Object callGetter(T bean, PropertyDescriptor prop)
            throws IllegalAccessException, InvocationTargetException {
        Method getter = prop.getReadMethod();
        if (getter != null) {
            return getter.invoke(bean);
        }
        return null;
    }

    private static String underscoreName(String camelCaseName) {
        StringBuilder sb = new StringBuilder();
        if (camelCaseName != null && camelCaseName.length() > 0) {
            for (int i = 0; i < camelCaseName.length(); i++) {
                char ch = camelCaseName.charAt(i);
                if (Character.isUpperCase(ch)) {
                    if (i > 0) {
                        sb.append("_");
                    }
                    sb.append(Character.toLowerCase(ch));
                } else {
                    sb.append(ch);
                }
            }
        }
        return sb.toString();
    }

}