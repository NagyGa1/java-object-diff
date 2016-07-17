## Features added to upstream project

* IdentityStrategy adheres the Java spec on equals() and hashCode()
* IdentityConfigurer allows to configure by element type within the collection

```java
ObjectDifferBuilder
  .startBuilding()
  .identity().ofType(ElementClass.class).toUse(codeIdentity).and()
  ...
```
