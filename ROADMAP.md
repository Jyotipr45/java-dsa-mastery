# Learning Roadmap

This roadmap orders the material so that each stage gives you the tools the next stage depends on. It is a suggested path, not a set of gates. If a topic already feels comfortable, skim its guide, solve one or two problems to confirm, and move on.

![Suggested learning order in six stages: Foundations, Core patterns, Linear structures, Recursion, Trees and graphs, and Optimization](assets/concepts/learning-roadmap.webp)

## How to use this roadmap

For each stage:

1. Read the topic guide in `DSA/<nn>-<Topic>/README.md` and the related pattern guides in `Patterns/`.
2. Solve the Easy problems. Try each one yourself before reading the solution, and use the Pattern Recognition section only when you are stuck.
3. Solve a few Medium problems. These are where pattern recognition actually gets tested.
4. Attempt Hard problems when you want a challenge. You do not need to finish them before moving on.
5. Come back to problems you have solved after about one day, three days, one week and two weeks. Re-solving from memory is what turns understanding into skill.

A stage is done when you can look at a new problem from that stage and name a reasonable approach before writing any code. It is not done when you have read every solution.

## Stage 1: Foundations

**Topics:** Complexity analysis, Arrays, Strings, Hashing

**Prerequisites:** Basic Java: variables, loops, methods, arrays and classes.

Complexity analysis comes first because every later decision depends on it. When a problem says the input can have 100,000 elements, you need to know immediately that a nested loop over it will be too slow. Without that instinct, there is no reason to look for a better pattern.

Arrays and strings are the input format of most problems, so working with indices, bounds and character manipulation must become automatic. Hashing is the first big improvement most learners meet: trading extra memory for constant-time lookups turns many slow solutions into fast ones.

**You are ready to move on when** you can estimate the time complexity of your own code and explain when a `HashMap` makes a solution faster.

## Stage 2: Core patterns

**Topics:** Two Pointers, Sliding Window, Prefix Sum

**Prerequisites:** Stage 1.

These three patterns work on arrays and strings, and together they cover a large share of interview problems. They come right after Stage 1 because they are mostly ways of avoiding the nested loops that complexity analysis taught you to distrust.

- **Two Pointers** works when the input is sorted, or when you compare elements from both ends.
- **Sliding Window** works for contiguous subarrays or substrings with some condition.
- **Prefix Sum** works when you need the sums of many ranges.

Learning them together teaches the most important skill in this repository: telling similar-looking patterns apart by their clues.

**You are ready to move on when** you can read a problem about subarrays and decide between sliding window, prefix sum or neither, and explain why.

## Stage 3: Linear structures

**Topics:** Linked Lists, Stacks, Queues, Binary Search

**Prerequisites:** Stages 1 and 2.

Linked lists introduce references between objects, which is the foundation for trees and graphs later. Stacks and queues are simple structures with powerful uses: matching brackets, undo operations, next greater element problems (Monotonic Stack) and level-by-level processing (the start of BFS).

Binary search belongs here because, beyond searching sorted arrays, it introduces a deeper idea: searching over the space of possible answers when that space is monotonic. This idea comes back in many Medium and Hard problems.

**You are ready to move on when** you can reverse a linked list without looking it up and recognize when a problem's answer space can be binary searched.

## Stage 4: Recursion

**Topics:** Recursion, Backtracking

**Prerequisites:** Stages 1 to 3.

Recursion is the hardest mental shift for most learners, so it gets its own stage. You learn to trust that a function solves a smaller version of the problem and to define exactly when it stops. Backtracking builds on this by exploring choices and undoing them, which is how you generate permutations, subsets and solutions to constraint problems.

Trees, graphs and dynamic programming all depend on recursion, so it must be solid before Stage 5.

**You are ready to move on when** you can write a recursive solution, trace its calls on paper and explain its base case and complexity.

## Stage 5: Trees and graphs

**Topics:** Trees, Binary Search Trees, Heaps, Graphs, Tries

**Prerequisites:** Stages 1 to 4, especially recursion and queues.

Trees are where recursion becomes natural, since every subtree is a smaller tree. Depth-first search (DFS) and breadth-first search (BFS) on trees prepare you for the same techniques on graphs. Heaps, implemented with `PriorityQueue` in Java, solve problems about the smallest or largest elements efficiently.

Graphs generalize trees. They add cycles, multiple paths and dependencies, which bring in topological sort, Union-Find and shortest paths. Tries are specialized trees for prefix problems on strings.

**You are ready to move on when** you can choose between BFS and DFS for a problem and explain the reason, such as BFS for shortest paths in unweighted graphs.

## Stage 6: Optimization

**Topics:** Greedy, Dynamic Programming

**Prerequisites:** All earlier stages, especially recursion.

These come last because they are about making optimal choices, and both are easy to misapply. A greedy algorithm makes the best local choice at each step and is only correct when you can argue that local choices lead to a global optimum. Dynamic programming handles the cases where greedy fails, by solving overlapping subproblems once and reusing the results.

Dynamic programming is easiest to learn as an improvement over a recursive solution. That is why recursion must come first.

**You are ready when** you can take a slow recursive solution, identify the repeated subproblems and convert it into a memoized or tabulated solution.

## Supporting topics

These topics are used across several stages rather than belonging to one:

- **Matrix** problems apply array, BFS and DFS techniques to grids. Start them after Stage 3.
- **Intervals** problems use sorting and greedy reasoning. Start them after Stage 2.
- **Bit Manipulation** is useful for specific tricks. Learn it any time after Stage 1.

## Java versions

All solutions run on every Java LTS release from Java 8 onward (8, 11, 17, 21 and 25). If you want to learn what newer versions add, see [JavaVersions/](JavaVersions/README.md). You can do this alongside any stage.
