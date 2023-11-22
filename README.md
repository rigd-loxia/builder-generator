
# Software Component: Builder generator

This builder came forth from a need for a builder which would work the same as the one generated by a jaxb-module.

## Yet another builder generator, but different
Below is a table with different builders and what support they have in comparison to this builder generator:

| Builder            | Fluent Prefix 'with' | Nested Builders    | Builder inheritence | Remarks |
| :----------------- | :------------------: | :----------------: | :-----------------: | :------ |
| lombok Builder     | :heavy_check_mark:   |                    |                     | With the way lombok works implementing nested builders will not be supported |
| lombok SuperBuilder| :heavy_check_mark:   |                    | :heavy_check_mark:  | With the way lombok works implementing nested builders will not be supported |
| Jilt               |                      |                    |                     | Does not follow javabean spec |
| Pojobuilder        | :heavy_check_mark:   |                    |                     |         |
| Freebuilder        |                      | :heavy_check_mark: |                     | Requires interfaces instead of classes as the source for the builder. |
| Immutables         |                      |                    | :heavy_check_mark:  | Requires interfaces instead of classes as the source for the builder. |
| RIGD-Loxia builder | :heavy_check_mark:   | :heavy_check_mark: | :heavy_check_mark:  |         |

Need a support for other styles, feel free to make an issue for this. If you want you can even make a PR for it.

## Description
Generates `Builder` classes based on the `@Builder` annotation.
The following are by default generated
* fluent api met `with` prefix.
* builder chaining
* builder inheritence
* builder copyOf method

There are currently no support for options, although with PR's being merged this will change.

[See the Reference Guide for all supported features.](./documentation/index.md)

## Usage example
Take for example the following structure of classes:
```java
class Car {
    private String brand;
	private int numberOfDoors;
	private Engine engine;
	private List<Seat> seats;
}

class Engine {
	private String name;
	private int topSpeed;
}

class Seat {
	private String type;
}
```

If we would annotate all classes with the `@Builder` annotation we could use the following bit of code to create the Car class:

```java
private Car createCar() {
	return new CarBuilder()
		.withBrand("Custom Made")
		.withNumberOfDoors(4)
		.withEngine()
			.withName("V7 refurbished")
			.withTopSpeed(250)
		.end()
		.addSeats()
			withType("driver seat")
		.end()
		.addSeats()
			withType("front passenger seat")
		.end()
		.addSeats()
			withType("large back seat")
		.end()
		.build();
}
```

## Installation using maven
### Adding the dependency on the annotations
```
<dependency>
    <groupId>nl.loxia.builder.generator</groupId>
    <artifactId>annotations</artifactId>
    <version>${buildergenerator.version}</version>
</dependency>
```
### Configuring APT for generating classes
```
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <annotationProcessorPath>
                            <groupId>nl.loxia.builder.generator</groupId>
                            <artifactId>builderGenerator</artifactId>
                            <version>${buildergenerator.version}</version>
                        </annotationProcessorPath>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
```

## Overview compiler arguments
|| Compiler argument || default value || behaviour ||
|nl.loxia.BuilderGenerator.copyOfMethodGeneration|true|determines whether or not copyOf methods should be generated|

## Release Notes
### 0.2.0 (upcoming release)
* copyOf method can now be disabled, this allows for generation of builders without matching get methods for each field.
* support InnerClassBuilder when InnerClass is in a List

### 0.1.0
* Fixed SeeAlso ordering, first childs then parents instead of first parents then childs.
* copyOf method now supports the SeeAlso referencing.
* copyOf method now does an in-depth copy.
* Builder-collections for builder-chaining combined with collections will now be correctly instantiated.
* Builders for builder-chaining with direct field will only be instantiated once. Sequential actions take place on the same builder.
* Lists now have a `add...` method which accepts a varArgs instead of a single value.
* Compiler warning when parent is missing `@Builder` annotation.
* Added 'copyOf' method to all builders 
