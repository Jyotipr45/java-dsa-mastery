# Java 25 (LTS, 2025)

Java 25 is the newest LTS release. It collects features finalized in the non-LTS releases 22, 23 and 24, and it makes small Java programs much simpler to write, which is especially helpful for beginners and for quick algorithm experiments.

## Language features

**Compact source files and instance `main` methods** let a program be written without a class declaration or `public static void main(String[] args)`. A file can start directly with `void main() { ... }`. The new `IO` class provides `IO.println` and `IO.readln` for simple console input and output. This removes most of the boilerplate that beginners struggle with.

**Unnamed variables and patterns** (finalized in Java 22) use `_` for a variable you must declare but never use, such as a loop variable when only the count matters, or an exception you catch but do not inspect. This makes the intent clear to readers.

**Flexible constructor bodies** allow statements, such as argument validation, before calling `super(...)` or `this(...)` in a constructor.

**Module import declarations** let a file import every public class from a module with one line, for example `import module java.base;`.

## Library features

**Stream gatherers** (finalized in Java 24) let you add custom intermediate steps to a stream. The built-in `Gatherers.windowSliding(k)` produces every sliding window of size `k`, and `Gatherers.windowFixed(k)` splits a stream into chunks. These are useful for prototyping, but a hand-written sliding window is still the expected answer in interviews, because it runs in O(n) instead of building every window.

**Scoped values** are a safer way to share data with code running on many virtual threads, as an alternative to `ThreadLocal`.

## Running programs

Since Java 22, `java Main.java` can run a program split across several source files in the same folder, without a build step.

## Example

This is a complete file. It has no class declaration:

```java
import java.util.*;
import java.util.stream.*;

void main() {
    // Compact source file: no class declaration needed
    IO.println("Hello from Java 25");

    // Unnamed variable: the loop value is not needed
    int count = 0;
    for (var _ : List.of("a", "b", "c")) {
        count++;
    }
    IO.println(count);

    // Stream gatherer: sums of every sliding window of size 3
    List<Integer> windowSums = Stream.of(1, 2, 3, 4, 5)
            .gather(Gatherers.windowSliding(3))
            .map(w -> w.stream().mapToInt(Integer::intValue).sum())
            .toList();
    IO.println(windowSums);
}
```

Output:

```
Hello from Java 25
3
[6, 9, 12]
```

## Runtime improvements

**Compact object headers** can be enabled to reduce the memory each object uses, which helps programs that create many small objects, such as nodes in large trees or graphs.

## Preview features to recognize

Structured concurrency and primitive types in patterns are still preview features in Java 25. They need the `--enable-preview` flag and may change, so this repository does not use them.
