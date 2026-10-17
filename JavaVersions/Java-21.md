# Java 21 (LTS, 2023)

Java 21 completes the pattern matching features started in earlier releases and adds virtual threads, the biggest change to Java concurrency in years.

## Language features

**Pattern matching for `switch`** lets a `switch` choose a branch by an object's type and bind it to a variable. Combined with sealed types, the compiler checks that every possible subtype is handled, so no `default` branch is needed and adding a new subtype causes a compile error until you handle it.

**Record patterns** take a record apart inside a pattern. `case Circle(double r)` matches a `Circle` and extracts its `radius` in one step.

## Library features

**Sequenced collections** add a common set of methods for collections with a defined order: `getFirst`, `getLast`, `addFirst`, `addLast`, `removeFirst`, `removeLast` and `reversed`. They work on `List`, `Deque`, `LinkedHashSet` and `LinkedHashMap`. Before Java 21, getting the last element of a list meant writing `list.get(list.size() - 1)`.

**`Math.clamp`** limits a value to a range, which is useful for keeping indices inside an array's bounds.

## Runtime improvements

**Virtual threads** are lightweight threads managed by the JVM rather than the operating system. A program can run millions of them, which makes simple blocking code scale for servers that handle many requests at once. They are not useful for single-threaded algorithm problems, but they are an important topic in Java interviews.

```java
try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> System.out.println("Running on a virtual thread"));
}
```

**Generational ZGC** is an option for applications that need very short garbage collection pauses on large heaps.

## Example

```java
import java.util.*;

public class Example {
    sealed interface Shape permits Circle, Square {}
    record Circle(double radius) implements Shape {}
    record Square(double side) implements Shape {}

    static double area(Shape shape) {
        return switch (shape) {
            case Circle(double r) -> Math.PI * r * r;
            case Square(double s) -> s * s;
        };
    }

    public static void main(String[] args) {
        System.out.println(area(new Square(2)));

        List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
        System.out.println(list.getFirst() + " " + list.getLast());
        System.out.println(list.reversed());

        System.out.println(Math.clamp(15, 0, 10));
    }
}
```

Output:

```
4.0
1 3
[3, 2, 1]
10
```

Compare `area` with the Java 17 version: the `instanceof` chain and the final `throw` are gone, because the compiler knows `Circle` and `Square` are the only possible shapes.

## Preview features to recognize

String templates were a preview feature in Java 21 and were later removed. Do not use them.
