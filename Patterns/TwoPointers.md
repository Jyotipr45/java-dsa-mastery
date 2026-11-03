# Two Pointers

## What

Two Pointers uses two indices that move through an array or string in a planned way, so that one pass replaces a pair of nested loops. Each step looks at the elements under the two pointers, decides which pointer to move, and never moves a pointer backwards.

There are two common forms:

- **Opposite ends.** One pointer starts at the beginning and one at the end, and they move towards each other. Used for pair conditions on sorted input, palindromes and reversing.
- **Same direction.** Both pointers start at the beginning. A fast "read" pointer scans every element and a slow "write" pointer marks where the next kept element goes. Used for removing, filtering or compacting elements in place.

The result is usually O(n) time instead of O(n²), with O(1) extra space.

## When

Consider Two Pointers when:

- The input is sorted, or sorting it does not lose information the answer needs, and you are looking for a pair (or triplet) whose sum or difference meets a condition.
- You compare elements from both ends, as in checking a palindrome or reversing in place.
- You must rearrange, filter or deduplicate an array in place with O(1) extra space.
- You are merging or comparing two sorted sequences, with one pointer in each.

## Why it works

The opposite-ends form depends on sorted order. Suppose the array is sorted in ascending order and you want two numbers that add up to `target`. Look at `nums[left] + nums[right]`:

- If the sum is too small, `nums[left]` cannot be part of any answer with an element at or before `right`, because every one of those elements is at most `nums[right]`, and even `nums[right]` was not enough. So `left` can be discarded safely.
- If the sum is too large, `nums[right]` cannot pair with any element at or after `left`, because each of them is at least `nums[left]`. So `right` can be discarded safely.

Every step rules out one element for good, without ever skipping a valid pair. That is why one pass is enough. Without sorted order, neither conclusion holds, and the pattern gives wrong answers.

The same-direction form works for a simpler reason: the write pointer never gets ahead of the read pointer. So writing to `nums[write]` only overwrites a position that has already been read, and the first `write` elements always hold exactly the kept elements, in their original order.

## How

**Opposite ends (pair with a given sum in a sorted array)**

1. Set `left = 0` and `right = n - 1`.
2. While `left < right`, compute `sum = nums[left] + nums[right]`.
3. If `sum` equals the target, return the pair.
4. If `sum` is smaller, move `left` one step right. If it is larger, move `right` one step left.
5. If the pointers meet, no pair exists.

**Same direction (remove duplicates from a sorted array in place)**

1. Keep the first element. Set `write = 1`.
2. For each `read` from 1 to `n - 1`, if `nums[read]` differs from the last kept value `nums[write - 1]`, copy it to `nums[write]` and increase `write`.
3. The first `write` elements are the result.

**Pseudocode**

```
pairWithSum(nums, target):
    left = 0
    right = length(nums) - 1
    while left < right:
        sum = nums[left] + nums[right]
        if sum == target: return (left, right)
        if sum < target: left = left + 1
        else: right = right - 1
    return none

removeDuplicates(nums):
    if nums is empty: return 0
    write = 1
    for read from 1 to length(nums) - 1:
        if nums[read] != nums[write - 1]:
            nums[write] = nums[read]
            write = write + 1
    return write
```

**Worked example**

Find two numbers in `[1, 3, 4, 6, 8, 11]` that add up to 10.

| Step | left | right | nums[left] + nums[right] | Decision |
|---|---|---|---|---|
| 1 | 0 | 5 | 1 + 11 = 12 | Too large, move `right` left |
| 2 | 0 | 4 | 1 + 8 = 9 | Too small, move `left` right |
| 3 | 1 | 4 | 3 + 8 = 11 | Too large, move `right` left |
| 4 | 1 | 3 | 3 + 6 = 9 | Too small, move `left` right |
| 5 | 2 | 3 | 4 + 6 = 10 | Found: indices 2 and 3 |

In step 1, 11 is discarded because even the smallest number, 1, makes the sum too large. In step 2, 1 is discarded because even the largest remaining number, 8, is not enough. Each step removes one candidate, so at most `n - 1` steps are needed.

**Java example**

```java
import java.util.*;

public class TwoPointersDemo {

    // Opposite ends: returns the indices of two numbers that add up to target,
    // or an empty array if no such pair exists. nums must be sorted ascending.
    static int[] pairWithSum(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        while (left < right) {
            // long avoids overflow when both values are large
            long sum = (long) nums[left] + nums[right];
            if (sum == target) {
                return new int[] {left, right};
            }
            if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        return new int[0];
    }

    // Same direction: removes duplicates from a sorted array in place
    // and returns how many unique values are now at the front.
    static int removeDuplicates(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }
        int write = 1;
        for (int read = 1; read < nums.length; read++) {
            if (nums[read] != nums[write - 1]) {
                nums[write] = nums[read];
                write++;
            }
        }
        return write;
    }

    public static void main(String[] args) {
        int[] sorted = {1, 3, 4, 6, 8, 11};
        System.out.println("pair for 10: " + Arrays.toString(pairWithSum(sorted, 10)));
        System.out.println("pair for 2: " + Arrays.toString(pairWithSum(sorted, 2)));

        int[] withDuplicates = {1, 1, 2, 3, 3, 3, 5};
        int unique = removeDuplicates(withDuplicates);
        System.out.println("unique count: " + unique);
        System.out.println("unique values: " + Arrays.toString(Arrays.copyOf(withDuplicates, unique)));
    }
}
```

Output:

```
pair for 10: [2, 3]
pair for 2: []
unique count: 4
unique values: [1, 2, 3, 5]
```

## Problem clues

- "Sorted array" combined with a pair, triplet or difference condition.
- "In place" or "O(1) extra space" when removing, moving or partitioning elements.
- Comparing characters from both ends, as in palindromes.
- Two sorted inputs that must be merged or intersected.
- A brute-force solution that checks every pair `(i, j)`, where moving one index clearly makes a value only larger or only smaller.

Clues are hints, not rules. Always check that the pattern's assumptions actually hold for the problem. A pair-sum problem on unsorted input where the original indices must be returned is usually better solved with hashing, because sorting would lose the indices.

## Complexity

- **Time:** O(n) for one pass, because each step moves at least one pointer and no pointer moves backwards. If the input must be sorted first, the total becomes O(n log n).
- **Auxiliary space:** O(1) for the pointers themselves. Sorting may add its own extra space depending on the algorithm.

## Limitations

- The opposite-ends form needs a monotonic relationship, usually sorted order. On unsorted data the "discard one side" reasoning is false.
- Sorting changes positions. If the problem asks for original indices, either keep the indices alongside the values while sorting, or use hashing instead.
- It finds pairs or narrows down a range. It is not a good fit when you need every pair, or when the condition depends on elements between the pointers rather than the elements at them. For contiguous ranges with a running condition, see Sliding Window.
- Duplicate values need care when the problem asks for unique pairs or triplets. Skip equal neighbours after recording an answer.

## Alternatives

| Technique | Use it when | Trade-off |
|---|---|---|
| Brute force over all pairs | The input is tiny or you need every pair | O(n²) time, but simple and needs no sorting |
| Hashing | The input is unsorted, or original indices are needed | O(n) time, but O(n) extra space |
| Binary search for the complement | The input is sorted and you fix one element at a time | O(n log n) time; slower than two pointers but sometimes easier to adapt |
| Sliding Window | The answer is a contiguous subarray or substring that must satisfy a running condition | Also two indices moving in one direction, but it tracks the whole window between them |

The topic guides for [Array](../DSA/01-Array/README.md) and [Hashing](../DSA/03-Hashing/README.md) cover the data structures these alternatives rely on.

## Practice problems

None yet. Problems that use this pattern will be listed here as they are added.

## Quick revision

Two Pointers moves two indices through the data so that each step permanently rules out at least one candidate, which turns many O(n²) pair searches into one O(n) pass with O(1) extra space. Use opposite-end pointers for pair conditions on sorted input and for palindromes, and same-direction read and write pointers for in-place filtering. The opposite-ends form is only correct when the data is sorted or otherwise monotonic. When it is not, and sorting would lose needed information, use hashing instead.
