package com.intelli.ray.tests;

import com.intelli.ray.base_scope.BeanWithManagedConstructor;

/**
 * Author: Sergey Saiyan sergey.sova42@gmail.com
 * Date: 04.07.2015 15:30
 */
public class ManagedConstructorTest extends AnnotationTest {

    public void test() throws Exception {
        BeanWithManagedConstructor bean = context.getBeanContainer().createPrototype(BeanWithManagedConstructor.class, 6);
        assertNotNull(bean.getField());
    }
}