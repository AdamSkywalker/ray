package com.intelli.ray.core;

import com.intelli.ray.log.ContextLogger;
import com.intelli.ray.util.Exceptions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: Sergey42
 * Date: 17.05.2015 19:23
 */
@SuppressWarnings("unchecked")
public class SimpleBeanContainer implements InternalBeanContainer {

    protected final Map<String, BeanDefinition> definitionByName = new ConcurrentHashMap<>();
    protected final Map<Class, BeanDefinition> definitionByClass = new ConcurrentHashMap<>();
    protected final ContextLogger logger;

    public SimpleBeanContainer(ContextLogger logger) {
        this.logger = logger;
    }

    @Override
    public <T> T getBean(Class<T> beanClass) {
        BeanDefinition beanDefinition = checkNotNull(definitionByClass.get(beanClass), beanClass);
        beanDefinition.scopeShouldBe(Scope.SINGLETON);
        return (T) beanDefinition.singletonInstance;
    }

    @Override
    public <T> T getBean(String name) {
        BeanDefinition beanDefinition = checkNotNull(definitionByName.get(name), name);
        beanDefinition.scopeShouldBe(Scope.SINGLETON);
        return (T) beanDefinition.singletonInstance;
    }

    @Override
    public <T> Collection<T> getBeansByType(Class<T> beanClass) {
        List<T> beansOfType = new ArrayList<>();
        for (BeanDefinition definition : definitionByClass.values()) {
            if (definition.scope == Scope.SINGLETON && beanClass.isAssignableFrom(definition.beanClass)) {
                beansOfType.add((T) definition.singletonInstance);
            }
        }
        return beansOfType;
    }

    @Override
    public <T> T createPrototype(Class<T> beanClass) {
        BeanDefinition beanDefinition = checkNotNull(definitionByClass.get(beanClass), beanClass);
        beanDefinition.scopeShouldBe(Scope.PROTOTYPE);
        return createPrototype(beanDefinition);
    }

    @Override
    public <T> T createPrototype(String name) {
        BeanDefinition beanDefinition = checkNotNull(definitionByName.get(name), name);
        beanDefinition.scopeShouldBe(Scope.PROTOTYPE);
        return createPrototype(beanDefinition);
    }

    @Override
    public <T> T createPrototype(Class<T> beanClass, Object... constructorParams) {
        BeanDefinition beanDefinition = checkNotNull(definitionByClass.get(beanClass), beanClass);
        beanDefinition.scopeShouldBe(Scope.PROTOTYPE);

        try {
            //noinspection unchecked
            T prototypeInstance = (T) beanDefinition.managedConstructor.newInstance(constructorParams);
            doAutowire(prototypeInstance, beanDefinition);
            invokeInitMethods(beanDefinition, prototypeInstance);

            logger.log(new Date() + " - Created prototype instance of class " + beanClass.getSimpleName());

            return prototypeInstance;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new BeanInstantiationException(e);
        }
    }

    @Override
    public <T> T createPrototype(String name, Object... constructorParams) {
        BeanDefinition beanDefinition = checkNotNull(definitionByName.get(name), name);
        beanDefinition.scopeShouldBe(Scope.PROTOTYPE);

        try {
            //noinspection unchecked
            T prototypeInstance = (T) beanDefinition.managedConstructor.newInstance(constructorParams);
            doAutowire(prototypeInstance, beanDefinition);
            invokeInitMethods(beanDefinition, prototypeInstance);

            logger.log(new Date() + " - Created prototype instance with name " + name);

            return prototypeInstance;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new BeanInstantiationException(e);
        }
    }

    @Override
    public BeanDefinition getBeanDefinition(Class beanClass) {
        return definitionByClass.get(beanClass);
    }

    @Override
    public void register(BeanDefinition beanDefinition) {
        definitionByName.put(beanDefinition.id, beanDefinition);
        definitionByClass.put(beanDefinition.beanClass, beanDefinition);

        logger.log(String.format(new Date() + " - Registered bean of class %s with scope %s",
                beanDefinition.beanClass.getName(), beanDefinition.scope.getId()));
    }

    @Override
    public void autowireSingletons() {
        for (BeanDefinition definition : getBeanDefinitions()) {
            if (definition.scope == Scope.SINGLETON) {
                Object beanInstance = definition.singletonInstance;
                if (definition.autowiredFields != null) {
                    for (Field autowiredField : definition.autowiredFields) {
                        autowireField(beanInstance, autowiredField, definition);
                    }
                }
            }
        }
    }

    @Override
    public void initSingletons() {
        for (BeanDefinition beanDefinition : getBeanDefinitions()) {
            if (beanDefinition.scope == Scope.SINGLETON) {
                invokeInitMethods(beanDefinition);
            }
        }
    }

    @Override
    public synchronized void destroyBeans() {
        logger.log("Destroying beans in container : " + this);
        for (BeanDefinition definition : definitionByClass.values()) {
            if (definition.destroyMethods != null) {
                for (Method destroyMethod : definition.destroyMethods) {
                    logger.log(new Date() + String.format(" - Invoking pre-destroy method %s in bean %s",
                            destroyMethod.getName(), definition));
                    try {
                        destroyMethod.invoke(definition.singletonInstance);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        logger.log("Exception in pre-destroy method:\n" + Exceptions.toStr(e));
                    }
                }
            }
        }

        definitionByClass.clear();
        definitionByName.clear();
    }

    protected Iterable<BeanDefinition> getBeanDefinitions() {
        return definitionByClass.values();
    }

    protected <T> T getBeanAnyScope(Class<T> beanClass) {
        BeanDefinition beanDefinition = checkNotNull(definitionByClass.get(beanClass), beanClass);

        if (Scope.SINGLETON == beanDefinition.scope) {
            return (T) beanDefinition.singletonInstance;
        } else {
            return createPrototype(beanClass);
        }
    }

    protected void invokeInitMethods(BeanDefinition beanDefinition) {
        invokeInitMethods(beanDefinition, beanDefinition.singletonInstance);
    }

    protected void invokeInitMethods(BeanDefinition beanDefinition, Object instance) {
        Method[] initMethods = beanDefinition.initMethods;
        if (initMethods == null) {
            return;
        }

        for (int i = initMethods.length - 1; i >= 0; i--) {
            Method method = initMethods[i];
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            try {
                method.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new BeanInstantiationException("Failed to execute post construct methods of bean " +
                        beanDefinition.beanClass.getName(), e);
            }
        }
    }

    protected <T> T createPrototype(BeanDefinition beanDefinition) {
        try {
            //noinspection unchecked
            T prototypeInstance = (T) beanDefinition.beanClass.newInstance();
            doAutowire(prototypeInstance, beanDefinition);
            invokeInitMethods(beanDefinition, prototypeInstance);
            return prototypeInstance;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BeanInstantiationException(e);
        }
    }

    protected void doAutowire(Object instance, BeanDefinition definition) {
        Field[] fields = definition.autowiredFields;
        if (fields == null) {
            return;
        }

        for (Field field : fields) {
            autowireField(instance, field, definition);
        }
    }

    protected void autowireField(Object instance, Field field, BeanDefinition definition) {
        Class fieldClazz = field.getType();
        BeanDefinition fieldBeanDefinition = definitionByClass.get(fieldClazz);
        if (fieldBeanDefinition == null) {
            for (Class clazz : definitionByClass.keySet()) {
                //noinspection unchecked
                if (fieldClazz.isAssignableFrom(clazz)) {
                    fieldBeanDefinition = definitionByClass.get(clazz);
                    break;
                }
            }
            if (fieldBeanDefinition == null) {
                throw new BeanInstantiationException("Can not inject property '" + field.getName() + "' in bean " +
                        definition.beanClass.getName() + ", because property bean class " + fieldClazz.getName()
                        + " is not present in context.");
            }
        }
        try {
            if (!field.isAccessible()) field.setAccessible(true);
            field.set(instance, getBeanAnyScope(fieldBeanDefinition.beanClass));
        } catch (IllegalAccessException e) {
            throw new BeanInstantiationException(e);
        }
    }

    protected BeanDefinition checkNotNull(BeanDefinition definition, String id) {
        if (definition == null) {
            throw new BeanNotFoundException(id);
        }
        return definition;
    }

    protected BeanDefinition checkNotNull(BeanDefinition definition, Class id) {
        if (definition == null) {
            throw new BeanNotFoundException(id);
        }
        return definition;
    }
}
