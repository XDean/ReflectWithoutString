# Reflect without String
[![Build Status](https://travis-ci.org/XDean/ReflectWithoutString.svg?branch=master)](https://travis-ci.org/XDean/ReflectWithoutString)
[![codecov.io](http://codecov.io/github/XDean/ReflectWithoutString/coverage.svg?branch=master)](https://codecov.io/gh/XDean/ReflectWithoutString/branch/master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.XDean/ReflectWithoutString/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.XDean/ReflectWithoutString)

- Reflect method and field without String.
- Get the reflect object from invocation.
- A type-safe solution of reflection.

## Usage

### Get Field
```java
	FieldGetter fg = ReflectWithoutString.fieldGetter(SomeClass.class);
	Field field = fg.getField(o -> o.getSomeField());// or s.someField
```
	
### Get Method
```java
	MethodGetter mg = ReflectWithoutString.methodGetter(SomeClass.class);
	Method method = mg.getMethod(o -> o.someMethod());
```

### Get Property name and type
```java
	PropertyGetter pg = ReflectWithoutString.propertyGetter(SomeClass.class);
	String name = pg.getPropName(o -> o.getSomeField());// name = "someField"
	Class<?> type = pg.getPropType(o -> o.getSomeField());// type = someField's type
```

## Current two implementations compare
Class | Base on | Construct cost | Support field invoke | Limit
--- | --- | --- | --- |---
`UnsafeFieldGetter` | `com.misc.Unsafe` | 0.5s for 1M construct | Yes | No
`ProxyMethodGetter` | [cglib](link1) | 1s for 1K construct | No | Final class and method

<sup>*Also see their javadoc*</sup>
