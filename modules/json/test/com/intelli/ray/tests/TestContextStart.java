package com.intelli.ray.tests;

import com.intelli.ray.core.Context;
import junit.framework.TestCase;

/**
 * Author: Sergey42
 * Date: 16.11.13 17:06
 */
public class TestContextStart extends TestCase {

    public void test() {
        Context context = new JsonTestContext();

        assertFalse(context.isActive());

        context.refresh();

        assertTrue(context.isActive());
    }
}