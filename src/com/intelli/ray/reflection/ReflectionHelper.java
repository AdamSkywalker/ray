package com.intelli.ray.reflection;

import com.intelli.ray.core.ManagedConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: Sergey42
 * Date: 24.05.14 21:18
 */
public class ReflectionHelper {

    public static Iterable<Class> getTypesAnnotatedWith(Iterable<Class> classes, Class<? extends Annotation> annotation) {
        Set<Class> result = new HashSet<>();
        for (Class clazz : classes) {
            if (clazz.isAnnotationPresent(annotation)) {
                result.add(clazz);
            }
        }
        return result;
    }

    public static Field[] getAllAnnotatedFields(Class clazz, Class<? extends Annotation> annotation) {
        Set<Field> fields = new HashSet<>();
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotation)) {
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[fields.size()]);
    }

    public static Constructor getManagedConstructor(Class clazz) {
        Constructor[] constructors = clazz.getConstructors();
        for (Constructor constructor : constructors) {
            if (constructor.getAnnotation(ManagedConstructor.class) != null) {
                return constructor;
            }
        }
        return null;
    }
}