package org.skywind.ray.tests;

import junit.framework.TestCase;
import org.skywind.ray.base_scope.ChildBean;
import org.skywind.ray.core.BeanContainer;
import org.skywind.ray.core.Context;

/**
 * Author: Sergey Saiyan sergey.sova42@gmail.com
 * Date: 08.07.2015 18:05
 */
public class InheritanceTest extends TestCase {

    public void test() throws Exception {
        Context context = new TestAnnotationContext();
        context.refresh();

        BeanContainer beanContainer = context.getBeanContainer();
        ChildBean bean = beanContainer.getBean(ChildBean.class);

        assertNotNull(bean.getSingle());
        assertNotNull(bean.getAnnBeanNoName());
    }
}
