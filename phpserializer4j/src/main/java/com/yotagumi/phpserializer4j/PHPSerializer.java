package com.yotagumi.phpserializer4j;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * PHPSerializer
 * 
 * @author Yotama
 */
public class PHPSerializer {
    /**
     * Serialize to PHP format.
     * 
     * @param object
     * @return PHP serialized string
     */
    public String serialize(Object object) {
        StringBuilder result = new StringBuilder();
        serializeCore(object, result);

        return result.toString();
    }

    /**
     * Core method of serialize.
     * 
     * @param object
     * @param result
     */
    private void serializeCore(Object object, StringBuilder result) {
        if (object == null) {
            result.append("N;");
        } else if (object instanceof String) {
            serializeString((String) object, result);
        } else if (object instanceof Boolean) {
            serializeBoolean((Boolean) object, result);
        } else if (object instanceof Integer || object instanceof Long || object instanceof Byte || object instanceof Short) {
            serializeInteger((Number) object, result);
        } else if (object instanceof Number) {
            serializeDouble((Number) object, result);
        } else if (object instanceof Map) {
            serializeMap((Map<?, ?>) object, result);
        } else if (object instanceof List) {
            serializeList((List<?>) object, result);
        } else if (object.getClass().isArray()) {
            serializeArray(object, result);
        } else {
            serializeBean(object, result);
        }
    }

    /**
     * Serialize string
     * 
     * @param str target string
     * @param result output buffer for PHP format
     */
    private void serializeString(String str, StringBuilder result) {
        result.append("s:").append(str.getBytes().length).append(":\"").append(str).append("\";");
    }

    /**
     * Serialize boolean
     * 
     * @param bool
     * @param result
     */
    private void serializeBoolean(Boolean bool, StringBuilder result) {
        result.append("b:").append(bool ? "1" : "0").append(";");
    }

    /**
     * Serialize integer
     * 
     * @param number
     * @param result
     */
    private void serializeInteger(Number number, StringBuilder result) {
        result.append("i:").append(number.toString()).append(";");
    }

    /**
     * Serialize double
     * 
     * @param number
     * @param result
     */
    private void serializeDouble(Number number, StringBuilder result) {
        result.append("d:").append(number.toString()).append(";");
    }

    /**
     * Serialize Map
     * 
     * @param number
     * @param result
     */
    private void serializeMap(Map<?, ?> map, StringBuilder result) {
        result.append("a:").append(map.size()).append(":{");

        for (Object item : map.keySet()) {
            serializeCore(item, result);
            serializeCore(map.get(item), result);
        }

        result.append("}");
    }

    /**
     * Serialize List
     * 
     * @param list
     * @param result
     */
    private void serializeList(List<?> list, StringBuilder result) {
        result.append("a:").append(list.size()).append(":{");

        int i = 0;
        for (Object item : list) {
            serializeCore(i++, result);
            serializeCore(item, result);
        }

        result.append("}");
    }

    /**
     * Serialize array
     * 
     * @param array
     * @param result
     */
    private void serializeArray(Object array, StringBuilder result) {
        int arrayLength = Array.getLength(array);

        result.append("a:").append(arrayLength).append(":{");

        for (int i = 0; i < arrayLength; i++) {
            serializeCore(i, result);
            serializeCore(Array.get(array, i), result);
        }

        result.append("}");
    }

    /**
     * Serialize Bean
     * 
     * @param object Bean object
     * @param result
     */
    private void serializeBean(Object object, StringBuilder result) {
        result.append("a:");

        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(object.getClass());

        List<PropertyDescriptor> targetDescriptor = new ArrayList<PropertyDescriptor>();
        int propertyCount = 0;
        for (PropertyDescriptor descriptor : descriptors) {
            Method readMethod = descriptor.getReadMethod();
            if (readMethod.getDeclaringClass() == Object.class) {
                continue;
            }
            propertyCount++;
            targetDescriptor.add(descriptor);
        }

        result.append(propertyCount).append(":{");

        for (PropertyDescriptor descriptor : targetDescriptor) {
            Method readMethod = descriptor.getReadMethod();
            readMethod.setAccessible(true);
            Object property = null;
            try {
                property = readMethod.invoke(object, new Object[] {});
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }

            String propertyName = null;
            PHPSerializeHint hintAnnotation = readMethod.getAnnotation(PHPSerializeHint.class);
            if (hintAnnotation != null) {
                propertyName = hintAnnotation.name();
            } else {
                propertyName = descriptor.getName();
            }

            result.append("s:").append(propertyName.length()).append(":\"").append(propertyName).append("\";");
            serializeCore(property, result);
        }

        result.append("}");
    }
}
