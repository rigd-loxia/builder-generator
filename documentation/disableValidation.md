[Home](../) - [Reference Guide](index.md)
# Disable validation code generation configuration
It is possible to configure whether or not code is generated for validation of the constructor arguments through the use of compiler arguments or `@Builder` annotation property.
If the `@Builder` annotation property is defined then this value is used, otherwise the compiler argument is used.
If neither are present then the validation code will be generated.

example for disabling the validation code:
```java
@Builder(validation = false)
```
or through compiler arguments:
```
	<compilerArgs>
		<!-- testCompiling is used to skip the generation of builder classes for which the processor should generate a compilation error. -->
		<arg>-Anl.loxia.BuilderGenerator.builderValidation=false</arg>
	</compilerArgs>
```
