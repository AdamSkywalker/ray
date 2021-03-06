package org.skywind.ray.base_scope;

import org.skywind.ray.meta.ManagedComponent;

import javax.annotation.PostConstruct;

/**
 * Author: Sergey Saiyan sergey.sova42@gmail.com
 * Date: 02.07.2015 23:24
 */
@ManagedComponent
public class BeanWithPostConstruct1 {

    private int init1InvokeCnt;
    private int init2InvokeCnt;

    protected int field1;
    protected int field2;

    @PostConstruct
    protected void init1() {
        init1InvokeCnt++;
        field1 = 1;
    }

    @PostConstruct
    protected void init2() {
        init2InvokeCnt++;
        field2 = 2;
    }

    public int getField1() {
        return field1;
    }

    public int getField2() {
        return field2;
    }

    public int getInit1InvokeCnt() {
        return init1InvokeCnt;
    }

    public int getInit2InvokeCnt() {
        return init2InvokeCnt;
    }
}
