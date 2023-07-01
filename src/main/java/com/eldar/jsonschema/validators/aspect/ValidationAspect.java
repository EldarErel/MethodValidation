package com.eldar.jsonschema.validators.aspect;


import com.eldar.jsonschema.validators.ValidatorService;
import com.eldar.jsonschema.validators.annotations.ValidatedMethod;
import com.eldar.jsonschema.validators.annotations.ValidatedParam;
import com.eldar.jsonschema.validators.providers.DefaultValueProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.eldar.jsonschema.ValidationSchema.*;

@Aspect
@Slf4j
@Component
@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class ValidationAspect {

    private final ValidatorService validatorService;

    @Around("@annotation(com.eldar.jsonschema.validators.annotations.ValidatedMethod)")
    public Object validateMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Optional<ValidatedMethod> optionalAnnotation =
                AspectsUtils.getMethodAnnotation(joinPoint, ValidatedMethod.class);
        if (optionalAnnotation.isEmpty()) {
            return joinPoint.proceed();
        }
        ValidatedMethod methodValidator = optionalAnnotation.get();
        boolean isAllowEmpty = methodValidator.allowEmpty();
        boolean isAllowNull = methodValidator.allowNull();
        try {
            Object[] args = joinPoint.getArgs();
            if (args == null) {
                // no args to validate
                return joinPoint.proceed();
            }
            for (Object obj : args) {
                if (!isAllowEmpty) {
                    validateEmptyObject(obj);
                } else if (!isAllowNull) {
                    validatorService.validate(NOT_NULL, obj);
                }
            }
            return joinPoint.proceed();
        } catch (Exception e) {
            return defaultValueOrThrow(methodSignature, methodValidator.throwException(), e);
        }
    }

    @Around("execution(* *(.., @com.eldar.jsonschema.validators.annotations.ValidatedParam (*), ..))")
    public Object validateParam(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args == null) {
            // no args to validate
            return joinPoint.proceed();
        }
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        for (int i = 0; i < args.length; i++) {
            Optional<ValidatedParam> annotation = AspectsUtils.getParamAnnotation(joinPoint, i, ValidatedParam.class);
            if (annotation.isEmpty()) {
                continue;
            }
            ValidatedParam validParamAnnotation = annotation.get();
            String schema = validParamAnnotation.value();
            Object param = args[i];
            try {
                validatorService.validate(schema, param);
            } catch (Exception e) {
                return defaultValueOrThrow(methodSignature, validParamAnnotation.throwException(), e);
            }
        }
        return joinPoint.proceed();
    }

    private Object defaultValueOrThrow(MethodSignature methodSignature, boolean throwException, Exception e) throws Exception {
        if (throwException) {
            throw e;
        }
        log.warn("Validation failed for method: {}", methodSignature.getMethod().getName(), e);
        if (methodSignature.getReturnType() == void.class) {
            return null;
        }
        return DefaultValueProvider.getDefaultValue(methodSignature.getReturnType());
    }

    private void validateEmptyObject(Object obj) {
        if (obj instanceof String) {
            validatorService.validate(NON_EMPTY_STRING, obj);
        } else if (obj instanceof List || obj instanceof Set || obj instanceof Object[]) {
            validatorService.validate(NON_EMPTY_ARRAY, obj);
        } else if (obj instanceof Map) {
            validatorService.validate(NON_EMPTY_OBJECT, obj);
        } else {
            validatorService.validate(NOT_NULL, obj);
        }
    }

}
