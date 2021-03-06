package org.skywind.ray.core;

import org.skywind.ray.meta.Inject;
import org.skywind.ray.meta.InterfaceAudience;
import org.skywind.ray.meta.ManagedComponent;
import org.skywind.ray.meta.ManagedConstructor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * Author: Sergey Saiyan sergey.sova42@gmail.com
 * Date: 04.07.2015 12:07
 */
@InterfaceAudience.Development
public class DefaultAnnotationConfiguration implements AnnotationConfiguration {

    protected final NameAndScopeExtractor nameAndScopeExtractor = new NameAndScopeExtractor() {
        @Override
        public NameAndScope extract(Annotation componentAnnotation) {
            ManagedComponent annotation = (ManagedComponent) componentAnnotation;
            return new NameAndScope(annotation.name(), annotation.scope());
        }
    };

    @Override
    public Iterable<Class<? extends Annotation>> getManagedComponentAnnotations() {
        return Arrays.<Class<? extends Annotation>>asList(ManagedComponent.class);
    }

    @Override
    public NameAndScopeExtractor getNameAndScopeExtractor() {
        return nameAndScopeExtractor;
    }

    @Override
    public Iterable<Class<? extends Annotation>> getManagedConstructorAnnotations() {
        return Arrays.<Class<? extends Annotation>>asList(ManagedConstructor.class);
    }

    @Override
    public Iterable<Class<? extends Annotation>> getInitMethodAnnotations() {
        return Arrays.<Class<? extends Annotation>>asList(PostConstruct.class);
    }

    @Override
    public Iterable<Class<? extends Annotation>> getDestroyMethodAnnotations() {
        return Arrays.<Class<? extends Annotation>>asList(PreDestroy.class);
    }

    @Override
    public Iterable<Class<? extends Annotation>> getAutowiredAnnotations() {
        return Arrays.<Class<? extends Annotation>>asList(Inject.class);
    }
}
