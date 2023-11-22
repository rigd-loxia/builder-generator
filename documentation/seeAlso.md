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

## Example
Below you see a structure where the see also annotation would come into play.

```java
@SeeAlso( { Cat.class, Dog.class } )
@Builder
class Animal {
	String food;
	/** constructors/getters/setters left out **/
}

@Builder
class Cat extends Animal {
	String tailColor;
	/** constructors/getters/setters left out **/
}

@Builder
class Dog extends Animal {
	String furColor;
	/** constructors/getters/setters left out **/
}
```

Taking the following class:

```java
@Builder
class Person {
	List<Animal> pets = new ArrayList<>();
	/** constructors/getters/setters left out **/
}
```
We will get the following methods for adding pets:
```java
class PersonBuilder {
...
	CatBuilder addCat() { ... }
	DogBuilder addDog() { ... }
	AnimalBuilder addPets() { ... }
...
}
```
This would allow you to fill up the pets using code like this:
```
personBuilder
	.addDog().withFood(...).withFurColor(...).end()
	.addCat().withFood(...).withTailColor(...).end()
	.addPets().withFood(...).end()
```
