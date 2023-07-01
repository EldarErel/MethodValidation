# MethodValidation - JSON Schema Validation in Java
annotation to validate method's parameters using aspectj

This repository provides examples of performing JSON schema validation in Java using reflection and Spring AOP. It demonstrates how to use JSON schemas to validate method parameters and method return values.

## Features

- Annotation-based method validation using JSON schemas.
- Aspect-oriented programming (AOP) to intercept method calls and perform validation.
- Support for validating non-null values, non-empty strings, non-empty arrays, and non-empty objects.
- Support validation using custom schemas

## Prerequisites

- Java 17 or higher
- Spring Framework (for Spring AOP)
- Jackson JSON library (for JSON handling)

## Getting Started
## Getting Started

1. Clone this repository:

   ```shell
   git clone https://github.com/EldarErel/MethodValidation.git
   ```
Build and run the project using your preferred build tool (e.g., Maven or Gradle).

Explore the provided code examples in the src directory:

ValidationSchema.java: Contains predefined JSON schemas for common validations.
ValidationAspect.java: Aspect class responsible for intercepting method calls and performing validation.
ValidationAspectTest.java: Sample class with annotated methods to demonstrate validation.
ValidatedMethod.java: Method annotation
ValidatedParam.java: Parameter annotation

## Usage
Define your JSON schemas for validation in the ValidationSchema.java file.

Annotate your methods and parameters with the appropriate validation annotations:

@ValidatedMethod: Annotate a method to enable method-level validation. Use attributes to control behavior.
@ValidatedParam: Annotate a parameter to validate it using a JSON schema.
Run your application, and the aspect will intercept method calls and perform validation based on the annotations.

## Examples
### Method-Level Validation
```java
@ValidatedMethod(allowNull = true, allowEmpty = true, throwException = false)
public String myMethod(String arg1) {
return arg1;
}
```
In this example, the myMethod is annotated with @ValidatedMethod, allowing arg1 to be null or an empty string without throwing an exception.

## Parameter-Level Validation
```java
public String myMethod(@ValidatedParam(ValidationSchema.NON_EMPTY_STRING) String arg1) {
return arg1;
}
```
In this example, the arg1 parameter of the myMethod is annotated with @ValidatedParam and validated against the NON_EMPTY_STRING JSON schema.

License
This project is licensed under the MIT License. See the LICENSE file for details.

Feel free to customize the content, add additional sections, or modify it to suit your project's needs.