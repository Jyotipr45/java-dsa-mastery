# Java 8 (LTS, 2014)

Java 8 changed how Java code is written more than any release before it. It introduced functional programming features that are now used everywhere, and it is the baseline for every solution in this repository.

## Language features

**Lambdas** are short anonymous functions. They exist so you can pass behavior, such as how to compare two elements, without writing a whole class. In DSA work they are most useful as comparators.

**Functional interfaces** are interfaces with exactly one abstract method, such as `Comparator`, `Runnable`, `Function` and `Predicate`. A lambda can be used wherever a functional interface is expected.

**Method references** like `Integer::sum` and `String::length` are a shorter form of a lambda that only calls an existing method.

**Default methods** let an interface provide a method body. This is how `List.sort` and `Map.merge` were added without breaking existing code.

## Library features

**Streams** process collections as a pipeline of steps such as `filter`, `map` and `collect`. They are concise, but a plain loop is often clearer and slightly faster for algorithm problems. Use them when they make the intent easier to read.

**`Optional`** represents a value that may be absent, so methods can return "no result" without returning `null`.

**`Map.merge`, `getOrDefault` and `computeIfAbsent`** make counting and grouping much shorter. These are some of the most useful additions for hashing problems.

**The `java.time` API** (`LocalDate`, `Duration` and others) replaced the old, error-prone `Date` and `Calendar` classes. It rarely appears in DSA problems but matters in real projects.

## Example

```java
import java.util.*;
import java.util.stream.*;

public class Example {
    public static void main(String[] args) {
        List<Integer> nums = Arrays.asList(5, 3, 8, 1);

        // Lambda as a Comparator: sort in descending order
        List<Integer> sorted = new ArrayList<>(nums);
        sorted.sort((a, b) -> Integer.compare(b, a));
        System.out.println(sorted);

        // Stream: keep even numbers and square them
        List<Integer> evenSquares = nums.stream()
                .filter(n -> n % 2 == 0)
                .map(n -> n * n)
                .collect(Collectors.toList());
        System.out.println(evenSquares);

        // Map.merge: count character frequencies
        Map<Character, Integer> freq = new TreeMap<>();
        for (char c : "banana".toCharArray()) {
            freq.merge(c, 1, Integer::sum);
        }
        System.out.println(freq);

        // Optional: a value that may be absent
        Optional<Integer> max = nums.stream().max(Integer::compare);
        System.out.println(max.orElse(-1));
    }
}
```

Output:

```
[8, 5, 3, 1]
[64]
{a=3, b=1, n=2}
8
```

## A common mistake

Use `Integer.compare(b, a)` rather than `b - a` in comparators. Subtraction overflows when the values are far apart, for example with `Integer.MIN_VALUE`, and silently produces the wrong order.

## Runtime improvements

Java 8 replaced the PermGen memory area with Metaspace, which removed a common source of `OutOfMemoryError` in large applications.
