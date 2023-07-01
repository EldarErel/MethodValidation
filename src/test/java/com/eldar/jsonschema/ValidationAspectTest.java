package com.eldar.jsonschema;

import com.eldar.jsonschema.exception.UnProcessableObject;
import com.eldar.jsonschema.validators.ValidatorService;
import com.eldar.jsonschema.validators.aspect.ValidationAspect;
import com.eldar.jsonschema.validators.annotations.ValidatedMethod;
import com.eldar.jsonschema.validators.annotations.ValidatedParam;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
@Tag("unit_test")
class ValidationAspectTest {
    private static ValidationAspect plainValidationAspect;

    @BeforeAll
    static void init() {
        ValidatorService validatorService = new ValidatorService();
        plainValidationAspect = new ValidationAspect(validatorService);
    }

    @ParameterizedTest
    @MethodSource("getValidatedMethodStringParameters")
    void testValidateMethod_AllParametersValid_emptyStringAllowButNotNull(String arg, String expectedResult) throws Throwable {
        ProceedingJoinPoint joinPoint = createJoinPoint(TestClass.class.getMethod("validMethodAllowEmptyButNotNull",
                String.class, int.class), arg, 42);
        Object result = plainValidationAspect.validateMethod(joinPoint);
        assertEquals(expectedResult, result);
    }

    @Test
    void testValidateMethod_NullParameter_ThrowException() throws Throwable {
        ProceedingJoinPoint joinPoint = createJoinPoint(TestClass.class.getMethod(
                "validaMethodNoNullAndEmptyThrowException", String.class), (String) null);

        assertThrows(UnProcessableObject.class, () -> plainValidationAspect.validateMethod(joinPoint));
    }

    @Test
    void testValidateMethod_NullParameter_NoThrowException() throws Throwable {
        ProceedingJoinPoint joinPoint = createJoinPoint(TestClass.class.getMethod("validMethodAllowNullAndEmpty",
                String.class), (Object) null);

        Object result = plainValidationAspect.validateMethod(joinPoint);
        assertNull(result);

    }

    @Test
    void testValidateMethod_NullListParameter_ReturnEmptyList() throws Throwable {
        ProceedingJoinPoint joinPoint = createJoinPoint(TestClass.class.getMethod("validMethodAllowNullAndEmpty",
                List.class), (List) null);
        assertEquals(List.of(), plainValidationAspect.validateMethod(joinPoint));
    }

    @Test
    void testValidateMethod_NullListParameter_ThrowException() throws Throwable {
        ProceedingJoinPoint joinPoint = createJoinPoint(TestClass.class.getMethod(
                "validaMethodNoNullAndEmptyThrowException", List.class), (List) null);

        assertThrows(UnProcessableObject.class, () -> plainValidationAspect.validateMethod(joinPoint));
    }

    @Test
    void testValidateMethod_EmptyParameter_NoThrowException_ReturnEmptyString() throws Throwable {
        ProceedingJoinPoint joinPoint = createJoinPoint(TestClass.class.getMethod("validMethodAllowNullAndEmpty",
                String.class), "");

        Object result = plainValidationAspect.validateMethod(joinPoint);
        assertEquals("", result);

    }

    @Test
    void testValidateMethod_NoValidation() throws Throwable {
        ProceedingJoinPoint joinPoint = createJoinPoint(TestClass.class.getMethod("noValidation", String.class),
                "arg1");
        Object result = plainValidationAspect.validateMethod(joinPoint);
        assertEquals("arg1", result);

    }

    @Test
    void testValidateParam_ValidParameter() throws Throwable {
        ProceedingJoinPoint joinPoint = createJoinPoint(TestClass.class.getMethod("nonEmptyStringValidation",
                String.class), "arg1");
        Object result = plainValidationAspect.validateParam(joinPoint);
        assertEquals("arg1", result);

    }

    @Test
    void testValidateParam_NullParameter_ThrowException() throws Throwable {
        ProceedingJoinPoint joinPoint = createJoinPoint(TestClass.class.getMethod("nonEmptyStringValidationThrows",
                String.class), (Object) null);

        assertThrows(UnProcessableObject.class, () -> plainValidationAspect.validateParam(joinPoint));
    }

    @Test
    void testValidateParam_NullParameter_NoThrowException() throws Throwable {
        ProceedingJoinPoint joinPoint = createJoinPoint(TestClass.class.getMethod("nonEmptyStringValidation",
                String.class), (Object) null);

        Object result = plainValidationAspect.validateParam(joinPoint);
        assertEquals("", result, "expected empty string as default value for return type String");

    }

    @Test
    void testValidateParam_EmptyString_ThrowException() throws Throwable {
        ProceedingJoinPoint joinPoint = createJoinPoint(TestClass.class.getMethod("nonEmptyStringValidationThrows",
                String.class), "");

        assertThrows(UnProcessableObject.class, () -> plainValidationAspect.validateParam(joinPoint));
    }

    @Test
    void testValidateParam_MethodWithNoValidation() throws Throwable {
        ProceedingJoinPoint joinPoint = createJoinPoint(TestClass.class.getMethod("noValidation", String.class),
                "arg1");
        Object result = plainValidationAspect.validateParam(joinPoint);
        assertEquals("arg1", result);

    }

    @Test
    void testValidateParam_CustomSchema_NotThrows() throws Throwable {
        ProceedingJoinPoint joinPoint = createJoinPoint(TestClass.class.getMethod("stringMinLengthOf2ValidationThrows"
                        , String.class),
                "arg1");
        Object result = plainValidationAspect.validateParam(joinPoint);
        assertEquals("arg1", result);

    }

    @Test
    void testValidateParam_CustomSchema_Throws() throws Throwable {
        ProceedingJoinPoint joinPoint = createJoinPoint(TestClass.class.getMethod("stringMinLengthOf2ValidationThrows"
                        , String.class),
                "a");
        assertThrows(UnProcessableObject.class, () -> plainValidationAspect.validateParam(joinPoint));

    }

    @Test
    void testValidateParam_ObjectReturnType_ShouldReturnNewObject() throws Throwable {
        ProceedingJoinPoint joinPoint = createJoinPoint(TestClass.class.getMethod("testClassObjectReturnTypeWhenNull"
                        , String.class),
                (String) null);
        TestClass result = (TestClass) plainValidationAspect.validateParam(joinPoint);
        assertEquals("default", result.name);
    }

    @Test
    void testValidateParam_VoidMethod_ShouldReturnNull() throws Throwable {
        ProceedingJoinPoint joinPoint = createJoinPoint(TestClass.class.getMethod("voidMethod", Object.class),
                (String) null);
        TestClass result = (TestClass) plainValidationAspect.validateParam(joinPoint);
        assertNull(result);
    }

    /* Helper test methods and classes */

    private ProceedingJoinPoint createJoinPoint(Method method, Object... args) {
        return new ProceedingJoinPoint() {
            @Override
            public void set$AroundClosure(AroundClosure aroundClosure) {
            }

            @Override
            public Object proceed() throws Throwable {
                return method.invoke(method.getDeclaringClass().newInstance(), args);
            }

            @Override
            public Object proceed(Object[] objects) {
                return "test";
            }

            @Override
            public Object getThis() {
                return null;
            }

            @Override
            public Object getTarget() {
                return null;
            }

            @Override
            public Object[] getArgs() {
                return args;
            }

            @Override
            public Signature getSignature() {
                return new MethodSignature() {
                    @Override
                    public String getName() {
                        return method.getName();
                    }

                    @Override
                    public int getModifiers() {
                        return method.getModifiers();
                    }

                    @Override
                    public Class<?> getDeclaringType() {
                        return method.getDeclaringClass();
                    }

                    @Override
                    public String getDeclaringTypeName() {
                        return method.getDeclaringClass().getName();
                    }

                    @Override
                    public Class<?> getReturnType() {
                        return method.getReturnType();
                    }

                    @Override
                    public Method getMethod() {
                        return method;
                    }

                    @Override
                    public String[] getParameterNames() {
                        return new String[0];
                    }

                    @Override
                    public Class<?>[] getParameterTypes() {
                        return method.getParameterTypes();
                    }

                    @Override
                    public Class<?>[] getExceptionTypes() {
                        return new Class[0];
                    }

                    @Override
                    public String toShortString() {
                        return null;
                    }

                    @Override
                    public String toLongString() {
                        return null;
                    }

                    @Override
                    public String toString() {
                        return null;
                    }

                    @Override
                    public boolean equals(Object o) {
                        return false;
                    }

                    @Override
                    public int hashCode() {
                        return 0;
                    }
                };
            }

            @Override
            public SourceLocation getSourceLocation() {
                return null;
            }

            @Override
            public String getKind() {
                return null;
            }

            @Override
            public StaticPart getStaticPart() {
                return null;
            }

            @Override
            public String toShortString() {
                return null;
            }

            @Override
            public String toLongString() {
                return null;
            }

            @Override
            public String toString() {
                return null;
            }
        };
    }

    public static class TestClass {
        private String name;

        public TestClass() {
            name = "default";
        }

        public TestClass(String name) {
            this.name = name;
        }

        @ValidatedMethod(allowNull = true, allowEmpty = true, throwException = false)
        public String validMethodAllowNullAndEmpty(String arg1) {
            return arg1;
        }

        @ValidatedMethod(allowNull = false, allowEmpty = false, throwException = true)
        public String validaMethodNoNullAndEmptyThrowException(String arg1) {
            return arg1;
        }

        @ValidatedMethod(allowNull = false, allowEmpty = false, throwException = true)
        public List validaMethodNoNullAndEmptyThrowException(List arg1) {
            return arg1;
        }

        @ValidatedMethod(allowNull = false, allowEmpty = false, throwException = false)
        public List<?> validMethodAllowNullAndEmpty(List<?> arg1) {
            return arg1;
        }

        @ValidatedMethod(allowNull = true, allowEmpty = true, throwException = false)
        public String validMethodAllowNullAndEmpty(String arg1, int arg2) {
            return arg1;
        }

        @ValidatedMethod(allowNull = false, allowEmpty = true, throwException = false)
        public String validMethodAllowEmptyButNotNull(String arg1, int arg2) {
            return arg1;
        }

        public String noValidation(String arg1) {
            return arg1;
        }

        @SuppressWarnings("unused")
        @ValidatedMethod(allowNull = false, allowEmpty = true, throwException = false)
        public void voidMethod(Object obj1) {
        }

        @SuppressWarnings("unused")
        public int primitiveMethod(int arg1) {
            return arg1;
        }

        @SuppressWarnings("unused")
        public List<String> genericMethod(List<String> arg1) {
            return arg1;
        }

        @SuppressWarnings("unused")
        public Map<String, Integer> mapMethod(Map<String, Integer> arg1) {
            return arg1;
        }

        public String nonEmptyStringValidation(@ValidatedParam(ValidationSchema.NON_EMPTY_STRING) String arg1) {
            return arg1;
        }

        public String nonEmptyStringValidationThrows(@ValidatedParam(value = ValidationSchema.NON_EMPTY_STRING,
                throwException = true) String arg1) {
            return arg1;
        }

        public String stringMinLengthOf2ValidationThrows(@ValidatedParam(value = "{\"type\":\"string\"," +
                "\"minLength\":2}",
                throwException = true) String arg1) {
            return arg1;
        }

        public TestClass testClassObjectReturnTypeWhenNull(@ValidatedParam(value = ValidationSchema.NOT_NULL) String arg1) {
            return new TestClass("test");
        }
    }

    private static Stream<Arguments> getValidatedMethodStringParameters() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("value", "value"),
                Arguments.of(null, "")
        );
    }
}

