package org.skywind.ray.core;

import org.skywind.ray.meta.InterfaceAudience;

/**
 * Author: Sergey Saiyan sergey.sova42@gmail.com
 * Date: 05.07.2015 19:23
 */
@InterfaceAudience.Public
public class JsonContext extends BaseDefinitionContext {

    /**
     * Creates JSON application context from .json configuration files
     *
     * @param jsonLocations locations of json files
     */
    public JsonContext(String... jsonLocations) {
        super(jsonLocations);
    }

    @Override
    protected ContextReader createContextReader() {
        return new JsonContextReader();
    }
}
