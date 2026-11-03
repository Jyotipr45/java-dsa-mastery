# Array

**Roadmap:** Stage 1: Foundations in the [learning roadmap](../../ROADMAP.md).

## What is it?

An array is a fixed number of elements of the same type, stored one after another in a single block of memory. Each element has an index, starting at 0, and you can read or write any element directly through its index.

In Java, `int[] nums = new int[5];` creates room for exactly five integers, at indices 0 to 4. The length is decided when the array is created and never changes afterwards.

## Why does it exist?

Most programs need to keep many values of the same kind together and reach any one of them quickly. An array is the simplest way to do that. Because the elements sit next to each other, the computer can find element `i` with one calculation instead of walking past the elements before it.

Arrays are also the building block for many other structures. `ArrayList`, `ArrayDeque`, `HashMap` and the usual binary heap are all built on top of plain arrays.

## How does it work?

An array knows where its block of memory starts and how large each element is. To find `nums[i]`, it computes:

```
address of nums[i] = start address + i × element size
```

That is one multiplication and one addition, no matter how long the array is or which index you ask for. This is what "random access in O(1)" means.

The cost of this layout appears when you change the array's shape. There are no gaps between elements, so inserting a value in the middle means moving every later element one place to the right to make room. Removing a value means moving every later element one place to the left to close the gap. And because the length is fixed, adding an element to a full array means creating a bigger array and copying everything across.

A few Java details matter in practice:

- A new array is filled with default values: `0` for numbers, `false` for `boolean`, `'\u0000'` for `char` and `null` for objects.
- An array of primitives such as `int[]` stores the values themselves. An array of objects such as `String[]` or `Integer[]` stores references, and the objects live elsewhere in memory.
- Every access is bounds-checked. Reading `nums[nums.length]` throws `ArrayIndexOutOfBoundsException` instead of reading random memory.
- A two-dimensional array such as `int[][] grid` is an array of arrays. Each row is a separate array, so rows can even have different lengths.

## Visual explanation

![Two rows of six cells. The top row reads nums[3] directly by computing its address. The bottom row shows 5 being inserted at index 1, with 3, 9, 4 and 8 each shifted one place to the right](../../assets/concepts/array-indexing-and-insertion.webp)

In the top row, reading `nums[3]` takes one step because the address is calculated from the index. Nothing else in the array is touched.

In the bottom row, 5 is inserted at index 1 into an array that still had one unused slot at the end. Before 5 can be written, the elements 3, 9, 4 and 8 must each move one place to the right. The shifting starts from the end so that no value is overwritten before it has been moved. With `n` elements, an insert near the front moves almost all of them, which is why insertion is O(n). A plain Java array cannot grow, so this "spare slot" situation is exactly what `ArrayList` manages for you behind the scenes.

## Operations and complexity

`n` is the number of elements.

| Operation | Time | Extra space | Notes |
|---|---|---|---|
| Read or write `nums[i]` | O(1) | O(1) | Direct address calculation |
| Search for a value (unsorted) | O(n) | O(1) | Must check elements one by one |
| Search for a value (sorted) | O(log n) | O(1) | Binary search, for example `Arrays.binarySearch` |
| Insert or delete at the end | O(1) | O(1) | Only if there is room, as in `ArrayList` |
| Insert or delete at index `i` | O(n) | O(1) | Shifts the `n - i` elements after it |
| Grow to a larger array | O(n) | O(n) | Allocate a new array and copy everything |
| `ArrayList.add` at the end | O(1) amortized | O(1) amortized | Occasional O(n) resize, averaged over many adds |
| Sort with `Arrays.sort` | O(n log n) typical | Up to O(n) | Primitives use a dual-pivot quicksort; objects use TimSort, which is stable |

"Amortized" means the cost averaged over a long sequence of operations. `ArrayList` grows its backing array by about 50% each time it fills up, so expensive copies happen rarely enough that each `add` costs O(1) on average.

## When should I use it?

Use an array when the number of elements is known, or changes only at the end, and you need fast access by position. Typical cases:

- The input to a problem is already an array, and you need to scan it, compare elements or build an answer from it.
- You need a lookup table indexed by small integers, such as counts of the letters `a` to `z` in an `int[26]`.
- You need to store results for every index, such as a prefix sum array or a dynamic programming table.
- You want the speed of contiguous memory. Scanning an array is usually faster in practice than scanning a linked structure of the same size.

Prefer a primitive array like `int[]` over `List<Integer>` in performance-sensitive code. It avoids boxing every number into an object.

## When should I avoid it?

Avoid a plain array when:

- The size is not known in advance and keeps growing. Use `ArrayList` instead.
- You insert or remove often at the front or in the middle. Consider `ArrayDeque` for both ends, or a linked list when you already hold a reference to the position.
- You mostly ask "is this value present?" or "how many times does it appear?" for arbitrary values. A `HashSet` or `HashMap` answers in O(1) on average instead of O(n). See [Hashing](../03-Hashing/README.md).
- The keys are huge or sparse, such as IDs up to one billion. A lookup array that large would waste memory, so a hash map is the better choice.

## Java implementation

This short example shows the operations used most often in array problems.

```java
import java.util.*;

public class ArrayBasics {
    public static void main(String[] args) {
        int[] nums = {7, 3, 9, 4, 8};

        // Direct access by index: O(1)
        System.out.println("nums[3] = " + nums[3]);

        // Scan every element: O(n)
        int sum = 0;
        for (int value : nums) {
            sum += value;
        }
        System.out.println("sum = " + sum);

        // Insert 5 at index 1. The length is fixed, so copy into a larger array.
        int[] bigger = new int[nums.length + 1];
        int insertAt = 1;
        System.arraycopy(nums, 0, bigger, 0, insertAt);
        bigger[insertAt] = 5;
        System.arraycopy(nums, insertAt, bigger, insertAt + 1, nums.length - insertAt);
        System.out.println("after insert = " + Arrays.toString(bigger));

        // Sort, then binary search. Binary search only works on sorted data.
        Arrays.sort(bigger);
        System.out.println("sorted = " + Arrays.toString(bigger));
        System.out.println("index of 8 = " + Arrays.binarySearch(bigger, 8));

        // A count table indexed by letter: counts['b' - 'a'] holds the count of 'b'
        int[] counts = new int[26];
        for (char c : "banana".toCharArray()) {
            counts[c - 'a']++;
        }
        System.out.println("count of a = " + counts['a' - 'a']);

        // ArrayList grows automatically
        List<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(20);
        list.add(0, 5); // insert at the front shifts the others: O(n)
        System.out.println("list = " + list);
    }
}
```

Output:

```
nums[3] = 4
sum = 31
after insert = [7, 5, 3, 9, 4, 8]
sorted = [3, 4, 5, 7, 8, 9]
index of 8 = 4
count of a = 3
list = [5, 10, 20]
```

## Common mistakes

- **Off-by-one errors.** Valid indices are `0` to `length - 1`. A loop written as `i <= nums.length` reads past the end.
- **Confusing `length`, `length()` and `size()`.** Arrays use the field `nums.length`, strings use the method `s.length()`, and collections use `list.size()`.
- **Assuming `=` copies an array.** `int[] b = a;` makes both names refer to the same array. Use `a.clone()` or `Arrays.copyOf(a, a.length)` for a real copy.
- **Printing an array directly.** `System.out.println(nums)` prints a type and hash code such as `[I@1b6d3586`. Use `Arrays.toString(nums)`.
- **Comparing arrays with `==` or `equals`.** Both check whether two names refer to the same array object. Use `Arrays.equals(a, b)` to compare contents.
- **Overflow when summing.** The sum of many `int` values can exceed about 2.1 billion. Use a `long` accumulator when the input can be large.
- **Binary searching unsorted data.** `Arrays.binarySearch` returns a meaningless result if the array is not sorted first.

## Related patterns

Many array problems are solved by one of these patterns. Their full guides are listed in the [patterns index](../../Patterns/README.md).

- **Two Pointers** for sorted arrays, pairs that meet a condition, or in-place rearranging.
- [Sliding Window](../../Patterns/SlidingWindow.md) for contiguous subarrays that must satisfy a condition.
- **Prefix Sum** for the sums of many ranges in the same array.
- **Hashing** for finding complements, duplicates or counts in a single pass.
- **Binary Search** for sorted arrays, or for searching a monotonic range of possible answers.

## Practice problems

### Easy

None yet.

### Medium

None yet.

### Hard

None yet.
