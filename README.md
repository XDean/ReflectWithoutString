# Reflect without String
[![Build Status](https://travis-ci.org/XDean/ReflectWithoutString.svg?branch=master)](https://travis-ci.org/XDean/ReflectWithoutString)
[![codecov.io](http://codecov.io/github/XDean/ReflectWithoutString/coverage.svg?branch=master)](https://codecov.io/gh/XDean/ReflectWithoutString/branch/master)

- Reflect method and field without String.
- Get the reflect object from invocation.
- A type-safe solution of reflection.

## Usage

### Get Field
```java
	FieldGetter fg = new UnsafeFieldGetter(SomeClass.class);
	Field field = fg.get(o -> o.getSomeField());// or s.someField
```
	
### Get Method
```java
	MethodGetter mg = new ProxyMethodGetter(SomeClass.class);
	Method method = mg.get(o -> o.someMethod());
```

### Get Property name or type
```java
	FieldGetter fg = new UnsafeFieldGetter(SomeClass.class);
	String name = fg.nameOf(o -> o.someField);// name = "someField"
	Class<?> type = fg.typeOf(o -> o.someField);// type = someField's type

	MethodGetter mg = new ProxyMethodGetter(SomeClass.class);
	String name = mg.nameOf(o -> o.getSomeField());// name = "someField"
	Class<?> type = mg.typeOf(o -> o.getSomeField());// type = someField's type
```

### Clearer Usage, use object value directly instead of lambda
```java
	FieldGetter fg = new UnsafeFieldGetter(SomeClass.class);
	SomeClass sc = fg.getMockObject();
	String fieldA = fg.nameOf(sc.fieldA);
	String fieldB = fg.nameOf(sc.fieldB);
	String fieldC = fg.nameOf(sc.fieldC);
```

## Current two implementations compare
Class | Base on | Construct cost | Support field invoke | Limit (Can't handle)
--- | --- | --- | --- |---
`UnsafeFieldGetter` | `com.misc.Unsafe` | 0.5s for 1M construct | Yes | Too many primitive type fields
`ProxyMethodGetter` | [cglib](link1) | 1s for 1K construct | No | Final class and method

<sup>*Also see there javadoc*</sup>

## TODO
1. Unload cglib created classes
2. Make cache