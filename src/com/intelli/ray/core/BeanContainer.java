package com.intelli.ray.core;

import java.io.OutputStream;
import java.util.Collection;

/**
 * Author: Sergey42
 * Date: 17.05.2015 17:24
 */
public interface BeanContainer {

    <T> T getBean(Class<T> beanClass);

    <T> T getBeanAnyScope(Class<T> beanClass);

    <T> T getBean(String name);

    <T> Collection<T> getBeansByType(Class<T> beanClass);

    <T> T createPrototype(Class<T> beanClass);

    <T> T createPrototype(String name);

    <T> T createPrototype(Class<T> beanClass, Object... constructorParams);

    Iterable<BeanDefinition> getBeanDefinitions();

    BeanDefinition getBeanDefinition(Class beanClass);

    void register(BeanDefinition beanDefinition);

    void printConfiguredBeans(OutputStream outputStream);
}
