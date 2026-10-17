# Java 11 (LTS, 2018)

Java 11 was the first LTS release after Java 8. It also collects the features added in the non-LTS releases 9 and 10, so many developers met all of them at once when upgrading.

## Language features

**`var` for local variables** (added in Java 10) lets the compiler infer a variable's type from its initializer. It reduces repetition when the type is obvious from the right-hand side, such as `var map = new HashMap<String, Integer>()`. Avoid it when the type would not be clear to a reader.

**`var` in lambda parameters** (added in Java 11) allows `(var a, var b) -> ...`, mainly so that annotations can be placed on lambda parameters.

## Library features

**`List.of`, `Set.of` and `Map.of`** (added in Java 9) create small collections in one line. The collections they return cannot be modified, and they do not allow `null` elements. Wrap them in `new ArrayList<>(...)` when you need to change them.

**New `String` methods:** `isBlank`, `strip`, `stripLeading`, `stripTrailing`, `repeat` and `lines`. `strip` is Unicode-aware, unlike the older `trim`.

**The HTTP Client** (`java.net.http.HttpClient`) became standard. It replaced the old `HttpURLConnection` for calling web services. It is not used in DSA problems.

## Running a single file

Java 11 can compile and run a single source file in one step, with no `.class` file left behind:

```bash
java Solution.java
```

This is a convenient way to run any solution in this repository on Java 11 or later.

## Example

```java
import java.util.*;

public class Example {
    public static void main(String[] args) {
        var counts = new HashMap<String, Integer>();
        for (var word : List.of("a", "b", "a")) {
            counts.merge(word, 1, Integer::sum);
        }
        System.out.println(new TreeMap<>(counts));

        System.out.println("  ".isBlank());
        System.out.println("ab".repeat(3));
        System.out.println("  trim me  ".strip() + "|");
        System.out.println("line1\nline2".lines().count());
    }
}
```

Output:

```
{a=2, b=1}
true
ababab
trim me|
2
```

## Compatibility notes

Java 9 introduced the module system, and Java 11 removed several Java EE and CORBA modules from the JDK, such as `javax.xml.bind`. Old projects that depended on them need extra libraries when upgrading. Plain DSA code is not affected.

## Runtime improvements

G1 became the default garbage collector in Java 9, giving shorter pauses for most applications.
