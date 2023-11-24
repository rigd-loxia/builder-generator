[Home](../) - [Reference Guide](index.md)
# CopyOf method configuration
It is possible to configure whether or not a copyOf method is generated through the use of compiler arguments or `@Builder` annotation property.
If the `@Builder` annotation property is defined then this value is used, otherwise the compiler argument is used.
If neither are present then the copyOf method will be generated.

The copyOf method requires that for each property a matching getter is present. If there are no matching getters present then the builder cannot be generated.

example for disabling the copyOf method:
```java
@Builder(copyOf = false)
```
or through compiler arguments:
```
	<compilerArgs>
		<!-- testCompiling is used to skip the generation of builder classes for which the processor should generate a compilation error. -->
		<arg>-Anl.loxia.BuilderGenerator.copyOfMethodGeneration=false</arg>
	</compilerArgs>
```
