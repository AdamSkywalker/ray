package org.skywind.ray.base_scope;

import org.skywind.ray.meta.ManagedComponent;

/**
 * Author: Sergey Saiyan sergey.sova42@gmail.com
 * Date: 02.07.2015 23:25
 */
@ManagedComponent
public class BeanWithPostConstruct2 extends BeanWithPostConstruct1 {

    int initInvokeCnt;

    protected void init2() {
        initInvokeCnt++;
        field2 = 4;
    }

    public int getInitInvokeCnt() {
        return initInvokeCnt;
    }
}
