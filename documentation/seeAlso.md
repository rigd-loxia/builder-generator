[Home](../) - [Reference Guide](index.md)
# SeeAlso annotation

## Description
If a class or interface is annotated with `@SeeAlso(class, class, ...)` then any non-abstract class mentioned here will be available for chaining from the builder.
It does not matter where the SeeAlso annotation is defined, only the annotation name is checked.

example for the annotation:
```java
@Retention(RUNTIME)
@Target(TYPE)
public @interface SeeAlso {
    Class<?>[] value() default {};
}
```

## Usage
Below you see a structure where the see also annotation would come into play:

```java

```