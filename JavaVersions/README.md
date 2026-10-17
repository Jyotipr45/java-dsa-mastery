# Java LTS Versions

This repository covers only the long-term support (LTS) releases of Java: 8, 11, 17, 21 and 25. LTS releases receive updates for years, so they are the versions most companies run in production and the ones you are most likely to meet at work or in an interview. Non-LTS releases are skipped. A feature that first appeared in a non-LTS release is listed under the first LTS release that includes it.

## Which version do the solutions use?

Every `Solution.java` is written to be Java 8 compatible by default, so it runs unchanged on all five LTS releases. This keeps solutions readable for the widest audience and means you never need a specific JDK just to run a problem.

A problem includes a version-specific implementation in `versions/java-<N>/` only when a newer release offers a genuinely clearer way to write it. Newer syntax is never added just to show it off.

## The guides

| Version | Released | Highlights for DSA work |
|---|---|---|
| [Java 8](Java-8.md) | 2014 | Lambdas, streams, `Optional`, `Map.merge` |
| [Java 11](Java-11.md) | 2018 | `var`, `List.of`, new `String` methods, run a `.java` file directly |
| [Java 17](Java-17.md) | 2021 | Records, sealed classes, switch expressions, text blocks |
| [Java 21](Java-21.md) | 2023 | Pattern matching for `switch`, record patterns, sequenced collections, virtual threads |
| [Java 25](Java-25.md) | 2025 | Compact source files, unnamed variables, stream gatherers |

Each guide separates **language features** (new syntax), **library features** (new classes and methods) and **runtime improvements** (how the JVM runs your code), because they matter in different ways. Only finalized features are recommended. Preview features are mentioned only so you can recognize them, since they can change or be removed.

## Compatibility matrix

A tick means the feature is available, finalized, in that LTS release.

| Feature | 8 | 11 | 17 | 21 | 25 |
|---|:-:|:-:|:-:|:-:|:-:|
| Lambdas and streams | ✓ | ✓ | ✓ | ✓ | ✓ |
| `Optional`, `java.time` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `var` for local variables |  | ✓ | ✓ | ✓ | ✓ |
| `List.of`, `Set.of`, `Map.of` |  | ✓ | ✓ | ✓ | ✓ |
| `String.isBlank`, `strip`, `repeat`, `lines` |  | ✓ | ✓ | ✓ | ✓ |
| Run a single `.java` file with `java File.java` |  | ✓ | ✓ | ✓ | ✓ |
| Switch expressions |  |  | ✓ | ✓ | ✓ |
| Text blocks |  |  | ✓ | ✓ | ✓ |
| Records |  |  | ✓ | ✓ | ✓ |
| Pattern matching for `instanceof` |  |  | ✓ | ✓ | ✓ |
| Sealed classes |  |  | ✓ | ✓ | ✓ |
| `Stream.toList()` |  |  | ✓ | ✓ | ✓ |
| Pattern matching for `switch` |  |  |  | ✓ | ✓ |
| Record patterns |  |  |  | ✓ | ✓ |
| Sequenced collections (`getFirst`, `getLast`, `reversed`) |  |  |  | ✓ | ✓ |
| Virtual threads |  |  |  | ✓ | ✓ |
| `Math.clamp` |  |  |  | ✓ | ✓ |
| Unnamed variables (`_`) |  |  |  |  | ✓ |
| Stream gatherers |  |  |  |  | ✓ |
| Compact source files and `void main()` |  |  |  |  | ✓ |
| Running a program split across several `.java` files |  |  |  |  | ✓ |

## Compiling for an older version

A newer JDK can compile code against an older version's API with `--release`. This catches accidental use of newer features:

```bash
javac --release 8 Solution.java
```

This checks that the code would compile on Java 8, but it still runs on the JDK you used. To be sure a program behaves the same on Java 8, run it on a real Java 8 runtime. The repository's validation script does this when the JDKs are installed.

All code examples in these guides were compiled and run on the matching OpenJDK release.
