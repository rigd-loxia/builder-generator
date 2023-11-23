[Home](../) - [Reference Guide](index.md)
# methodPrefix method configuration
It is possible to configure which method prefix is used for the builders.
If the `@Builder` annotation property is defined then this value is used, otherwise the compiler argument is used.
If neither are present then the the prefix `with` will be used.

## Examples

### fluent methods using no prefix

```java
@Builder(methodPrefix = "")
public class NoPrefix {

    private boolean booleanField;
    private final String stringField;

    public NoPrefix(String stringField) {
        this.stringField = stringField;
    }

    public String getStringField() {
        return stringField;
    }

    public boolean getBooleanField() {
        return booleanField;
    }

    public void setBooleanField(boolean booleanField) {
        this.booleanField = booleanField;
    }
}
```

will generate a Builder with the following methods:

```java
public class NoPrefixBuilder<PARENT> {
    private boolean booleanField;
    private String stringField;

    private PARENT parent;

    public NoPrefixBuilder() {
    }

    public NoPrefixBuilder(PARENT parent) {
        this.parent = parent;
    }

    public NoPrefixBuilder<PARENT> booleanField(boolean booleanField) {
        this.booleanField = booleanField;
        return this;
    }

    public NoPrefixBuilder<PARENT> stringField(String stringField) {
        this.stringField = stringField;
        return this;
    }

/* followed by build method, end method, etc. */
}
```

### methods using a different prefix

```java
@Builder(methodPrefix = "set")
public class SetPrefix {

    private boolean booleanField;
    private final String stringField;

    public SetPrefix(String stringField) {
        this.stringField = stringField;
    }

    public String getStringField() {
        return stringField;
    }

    public boolean getBooleanField() {
        return booleanField;
    }

    public void setBooleanField(boolean booleanField) {
        this.booleanField = booleanField;
    }
}
```

will generate a Builder with the following methods:

```java
public class SetPrefixBuilder<PARENT> {
    private boolean booleanField;
    private String stringField;

    private PARENT parent;

    public SetPrefixBuilder() {
    }

    public SetPrefixBuilder(PARENT parent) {
        this.parent = parent;
    }

    public SetPrefixBuilder<PARENT> setBooleanField(boolean booleanField) {
        this.booleanField = booleanField;
        return this;
    }

    public SetPrefixBuilder<PARENT> setStringField(String stringField) {
        this.stringField = stringField;
        return this;
    }

/* followed by build method, end method, etc. */
}
```

