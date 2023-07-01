package com.eldar.jsonschema.validators.aspect;

import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

public class AspectsUtils {

    public static <T extends Annotation> Optional<T> getParamAnnotation(JoinPoint joinPoint, int index,
                                                                        Class<T> annotationClass) {
        if (ObjectUtils.anyNull(joinPoint, annotationClass)) {
            return Optional.empty();
        }
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Annotation[] annotations = method.getParameterAnnotations()[index];
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annotationClass) {
                return Optional.of((T) annotation);
            }
        }
        return Optional.empty();
    }

    public static <T extends Annotation> Optional<T> getMethodAnnotation(JoinPoint joinPoint,
                                                                         Class<T> annotationClass) {
        if (ObjectUtils.anyNull(joinPoint, annotationClass)) {
            return Optional.empty();
        }
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        return Optional.ofNullable(method.getAnnotation(annotationClass));
    }

}
