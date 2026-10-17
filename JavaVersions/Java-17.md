# Java 17 (LTS, 2021)

Java 17 brings together a group of features that make Java code shorter and safer, especially when modeling data. Several of them are genuinely useful in algorithm problems.

## Language features

**Records** (finalized in Java 16) are classes that only carry data. One line, such as `record Point(int row, int col) {}`, generates the constructor, accessors, `equals`, `hashCode` and `toString`. This makes records ideal as `HashMap` keys or `HashSet` elements, for example to store visited grid cells. A hand-written class with a missing `hashCode` is a classic bug that records prevent.

**Switch expressions** (finalized in Java 14) let `switch` return a value, use `->` arrows, and group several labels. There is no fall-through, so forgetting `break` is no longer a bug.

**Text blocks** (finalized in Java 15) are multi-line string literals written between `"""` delimiters. They are handy for writing small grids or test input.

**Pattern matching for `instanceof`** (finalized in Java 16) checks a type and binds a variable in one step, removing the explicit cast.

**Sealed classes** (finalized in Java 17) restrict which classes can extend or implement a type. This lets the compiler know every possible subtype, which Java 21 uses to check that a `switch` covers all cases.

## Library features

**`Stream.toList()`** (added in Java 16) is a shorter replacement for `collect(Collectors.toList())`. The returned list cannot be modified.

## Example

```java
import java.util.*;

public class Example {
    record Point(int row, int col) {}

    sealed interface Shape permits Circle, Square {}
    record Circle(double radius) implements Shape {}
    record Square(double side) implements Shape {}

    static double area(Shape shape) {
        if (shape instanceof Circle c) {
            return Math.PI * c.radius() * c.radius();
        } else if (shape instanceof Square s) {
            return s.side() * s.side();
        }
        throw new IllegalArgumentException();
    }

    public static void main(String[] args) {
        Set<Point> visited = new HashSet<>();
        visited.add(new Point(0, 1));
        System.out.println(visited.contains(new Point(0, 1)));

        String day = "SAT";
        int hours = switch (day) {
            case "SAT", "SUN" -> 0;
            default -> 8;
        };
        System.out.println(hours);

        System.out.println(area(new Square(3)));

        String grid = """
                #.#
                ...
                """;
        System.out.print(grid);

        System.out.println(List.of(3, 1, 2).stream().sorted().toList());
    }
}
```

Output:

```
true
0
9.0
#.#
...
[1, 2, 3]
```

## Runtime improvements

**Helpful `NullPointerException` messages** (enabled by default since Java 15) name exactly which variable was `null`, for example "because `node.next` is null". This makes debugging linked list and tree code much faster.

Java 17 also strongly encapsulates the JDK's internal APIs. Code that relied on internal classes through reflection may need changes. Plain DSA code is not affected.
