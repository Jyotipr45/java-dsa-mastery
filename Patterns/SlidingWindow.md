# Sliding Window

## What

Sliding Window looks at a contiguous part of an array or string, the window, and moves it from left to right while keeping a small summary of what is inside, such as a sum, a count of each character or the number of distinct values. When the window moves, the summary is updated using only the element that enters and the element that leaves, instead of being recomputed from scratch.

There are two common forms:

- **Fixed size.** The window always holds exactly `k` elements. Each step adds one element on the right and removes one on the left. Used for questions like "the largest sum of any `k` consecutive elements".
- **Variable size.** The window grows on the right one element at a time and shrinks from the left only when it has to. Used for questions like "the shortest subarray with sum at least `target`" or "the longest substring with no repeated character".

Both forms turn an O(n²) or O(n·k) scan over every subarray into a single O(n) pass.

## When

Consider Sliding Window when:

- The answer is a contiguous subarray or substring, not an arbitrary subset.
- You need the best (longest, shortest, largest, smallest) such range, or you need to check every range of a fixed length.
- The condition can be kept up to date cheaply as elements enter and leave: a running sum, a frequency map, a count of distinct values.
- For the variable form, the condition behaves monotonically: making a valid window larger keeps it valid (or making it smaller keeps it valid), so you never need to move the left edge backwards.

## Why it works

The fixed-size form is simple bookkeeping. Two neighbouring windows of size `k` share `k - 1` elements. The sum of the next window is the sum of the current one, plus the element entering, minus the element leaving. That is one addition and one subtraction per step instead of `k` additions.

The variable form needs a monotonic condition. Take "shortest subarray with sum at least `target`" when every value is positive:

- Adding an element on the right can only increase the sum. So if the window `[left, right]` is too small, the only way to fix it is to move `right` further.
- Removing an element on the left can only decrease the sum. Once `[left, right]` is valid, any window that starts at `left` and ends after `right` is longer, so it cannot be a better answer. The window can record its length and drop `nums[left]` for good.

Because of this, `left` never needs to move backwards. Every element enters the window once and leaves at most once, and no window that could be the answer is ever skipped.

If the array can contain zero or negative values, both statements break. Adding an element might lower the sum, and dropping one might raise it, so the window cannot decide which edge to move. That is the most important assumption to check before using this pattern.

## How

**Fixed size (largest sum of `k` consecutive elements)**

1. Add up the first `k` elements. This is the first window and the best so far.
2. For each `right` from `k` to `n - 1`, add `nums[right]` and subtract `nums[right - k]`, the element that just left.
3. Update the best value after each slide.

**Variable size (shortest subarray with sum at least `target`)**

1. Set `left = 0` and `sum = 0`.
2. For each `right`, add `nums[right]` to `sum`.
3. While `sum >= target`, the window is valid: record its length, subtract `nums[left]` and move `left` one step right.
4. At the end, the smallest recorded length is the answer, or 0 if nothing was ever valid.

A "longest" question uses the same loop with the roles swapped: shrink while the window is invalid, then record the length once it is valid again.

**Pseudocode**

```
maxSumOfSizeK(nums, k):
    sum = nums[0] + ... + nums[k - 1]
    best = sum
    for right from k to length(nums) - 1:
        sum = sum + nums[right] - nums[right - k]
        best = max(best, sum)
    return best

shortestWithSumAtLeast(nums, target):
    left = 0
    sum = 0
    shortest = infinity
    for right from 0 to length(nums) - 1:
        sum = sum + nums[right]
        while sum >= target:
            shortest = min(shortest, right - left + 1)
            sum = sum - nums[left]
            left = left + 1
    return 0 if shortest is infinity, otherwise shortest
```

**Worked example**

Find the shortest subarray of `[2, 3, 1, 2, 4, 3]` whose sum is at least 7.

| Step | Action | left | right | Window | sum | Shortest so far |
|---|---|---|---|---|---|---|
| 1 | Add 2 | 0 | 0 | [2] | 2 | none |
| 2 | Add 3 | 0 | 1 | [2, 3] | 5 | none |
| 3 | Add 1 | 0 | 2 | [2, 3, 1] | 6 | none |
| 4 | Add 2 | 0 | 3 | [2, 3, 1, 2] | 8 | none |
| 5 | Valid, record 4, remove 2 | 1 | 3 | [3, 1, 2] | 6 | 4 |
| 6 | Add 4 | 1 | 4 | [3, 1, 2, 4] | 10 | 4 |
| 7 | Valid, record 4, remove 3 | 2 | 4 | [1, 2, 4] | 7 | 4 |
| 8 | Valid, record 3, remove 1 | 3 | 4 | [2, 4] | 6 | 3 |
| 9 | Add 3 | 3 | 5 | [2, 4, 3] | 9 | 3 |
| 10 | Valid, record 3, remove 2 | 4 | 5 | [4, 3] | 7 | 3 |
| 11 | Valid, record 2, remove 4 | 5 | 5 | [3] | 3 | 2 |

The answer is 2, from the window `[4, 3]`. Look at steps 7 and 8: the window `[1, 2, 4]` is valid, so it is recorded and then shrunk again, because a shorter valid window might still start further right. The left edge moved five times in total and the right edge six times, so the work is linear even though there is a loop inside a loop.

**Java example**

The third method applies the "longest" form to a string, using a map of character counts as the window's summary.

```java
import java.util.*;

public class SlidingWindowDemo {

    // Fixed size: returns the largest sum of any k consecutive elements.
    // Assumes 1 <= k <= nums.length.
    static long maxSumOfSizeK(int[] nums, int k) {
        long windowSum = 0;
        for (int i = 0; i < k; i++) {
            windowSum += nums[i];
        }
        long best = windowSum;
        for (int right = k; right < nums.length; right++) {
            // Slide by one: add the element entering, remove the one leaving.
            windowSum += nums[right] - nums[right - k];
            best = Math.max(best, windowSum);
        }
        return best;
    }

    // Variable size: returns the length of the shortest subarray whose sum is
    // at least target, or 0 if there is none. All values must be positive.
    static int shortestWithSumAtLeast(int[] nums, int target) {
        int left = 0;
        long windowSum = 0;
        int shortest = Integer.MAX_VALUE;
        for (int right = 0; right < nums.length; right++) {
            windowSum += nums[right];
            // Shrink while the window is still valid, recording each valid length.
            while (windowSum >= target) {
                shortest = Math.min(shortest, right - left + 1);
                windowSum -= nums[left];
                left++;
            }
        }
        return shortest == Integer.MAX_VALUE ? 0 : shortest;
    }

    // Variable size on a string: returns the length of the longest substring
    // with no repeated character.
    static int longestWithoutRepeats(String s) {
        Map<Character, Integer> counts = new HashMap<>();
        int left = 0;
        int longest = 0;
        for (int right = 0; right < s.length(); right++) {
            char entering = s.charAt(right);
            counts.put(entering, counts.getOrDefault(entering, 0) + 1);
            // Shrink until the entering character appears only once again.
            while (counts.get(entering) > 1) {
                char leaving = s.charAt(left);
                counts.put(leaving, counts.get(leaving) - 1);
                left++;
            }
            longest = Math.max(longest, right - left + 1);
        }
        return longest;
    }

    public static void main(String[] args) {
        int[] nums = {2, 1, 5, 1, 3, 2};
        System.out.println("max sum of 3: " + maxSumOfSizeK(nums, 3));

        int[] positives = {2, 3, 1, 2, 4, 3};
        System.out.println("shortest with sum >= 7: " + shortestWithSumAtLeast(positives, 7));
        System.out.println("shortest with sum >= 100: " + shortestWithSumAtLeast(positives, 100));

        System.out.println("longest without repeats in \"abcabcbb\": " + longestWithoutRepeats("abcabcbb"));
        System.out.println("longest without repeats in \"\": " + longestWithoutRepeats(""));
    }
}
```

Output:

```
max sum of 3: 9
shortest with sum >= 7: 2
shortest with sum >= 100: 0
longest without repeats in "abcabcbb": 3
longest without repeats in "": 0
```

## Problem clues

- "Subarray" or "substring", meaning contiguous, combined with longest, shortest, maximum or minimum.
- "Every window of size `k`", or "`k` consecutive elements".
- "At most `k` distinct characters", "no repeated characters", "contains all characters of another string" and similar conditions that a frequency map can track.
- All values are non-negative, and the condition is a sum or product bound.
- A brute-force solution that tries every start index and extends to the right, recomputing almost the same thing each time.

Clues are hints, not rules. Always check that the pattern's assumptions actually hold for the problem. A subarray sum problem with negative numbers, such as "count subarrays whose sum equals `k`", looks like a sliding window problem but is usually solved with prefix sums and hashing.

## Complexity

- **Time:** O(n). In the fixed form each element is added once and removed once. In the variable form `right` moves `n` times and `left` moves at most `n` times in total across the whole run, so the inner loop adds at most `n` steps overall, not `n` steps per element. This assumes updating the window's summary costs O(1), which holds for a running sum and, on average, for a `HashMap` of counts.
- **Auxiliary space:** O(1) for a running sum. When the window keeps a frequency map, it is O(m), where `m` is the number of distinct values that can be in the window at once, for example at most 26 for lowercase English letters.

## Limitations

- The range must be contiguous. For subsets or subsequences that can skip elements, the window idea does not apply; look at dynamic programming or backtracking instead.
- The variable form needs a monotonic condition. Sums with negative numbers, or conditions like "the sum is exactly `k`" when values can be zero or negative, break the reasoning that lets `left` only move forward.
- The summary must be cheap to update in both directions. A running maximum is easy to add to but not to remove from: when the largest element leaves, you would have to rescan the window. For "maximum of every window of size `k`", use a monotonic queue to keep the update O(1).
- Off-by-one errors are common. Decide once whether the window is `[left, right]` inclusive, and compute its length as `right - left + 1` everywhere.

## Alternatives

| Technique | Use it when | Trade-off |
|---|---|---|
| Brute force over all subarrays | The input is tiny, or you need to check every range anyway | O(n²) or worse, but works for any condition |
| Prefix Sum | You need the sums of many ranges, or values can be negative | O(1) range sums after O(n) setup, but needs O(n) extra space and does not by itself find the best range |
| Prefix Sum with hashing | You count subarrays with an exact sum, including negative values | O(n) time and O(n) space, and does not need a monotonic condition |
| Two Pointers | The condition depends only on the two elements at the pointers, often on sorted input | Also two indices, but it does not track what lies between them |
| Monotonic Queue | You need the maximum or minimum of every fixed-size window | O(n) time with an O(k) deque, where a plain window would need to rescan |

The topic guides for [Array](../DSA/01-Array/README.md), [String](../DSA/02-String/README.md) and [Hashing](../DSA/03-Hashing/README.md) cover the data structures these techniques rely on.

## Practice problems

None yet. Problems that use this pattern will be listed here as they are added.

## Quick revision

Sliding Window keeps a summary of a contiguous range and updates it as elements enter on the right and leave on the left, so every range is considered in one O(n) pass. Use a fixed window for "every `k` consecutive elements" and a variable window for the longest or shortest range that meets a condition. The variable form is only correct when the condition is monotonic, which usually means non-negative values for sum conditions. With negative numbers or exact-sum counting, use prefix sums with hashing instead.
