# Prefix Sum

## What

A prefix sum array stores, for every position, the total of all elements before it. Once it is built, the sum of any contiguous range is the difference of two stored values, so each range query costs O(1) instead of a loop over the range.

This guide uses the version with a leading zero, because it removes most special cases:

```
prefix[0] = 0
prefix[i] = nums[0] + nums[1] + ... + nums[i - 1]
```

The array has `n + 1` slots, and the sum of `nums[left..right]`, both ends included, is `prefix[right + 1] - prefix[left]`.

The same idea has a second common use. Combined with a hash map of the prefix sums seen so far, it counts or finds subarrays with an exact sum in a single pass, even when the array contains negative numbers.

## When

Consider Prefix Sum when:

- The array does not change, and you need the sums of many different ranges.
- A question asks about subarrays with an exact sum, a sum divisible by `k`, or an equal number of two kinds of values (map them to +1 and -1 first).
- You need to compare the total on the left of an index with the total on its right, as in "find the index where both sides balance".
- Values can be negative or zero, so a sliding window cannot decide which edge to move.

## Why it works

Write `S(i)` for `prefix[i]`, the sum of the first `i` elements. The sum of the first `right + 1` elements contains the first `left` elements followed by exactly the range `nums[left..right]`:

```
S(right + 1) = S(left) + (nums[left] + ... + nums[right])
```

Subtracting `S(left)` from both sides leaves the range sum. Nothing about this depends on the values being positive, so it works for any integers.

The counting version uses the same equation from the other direction. A subarray that ends at index `i` and sums to `k` exists exactly when some earlier prefix sum equals `S(i + 1) - k`. So if you walk through the array, keep the running total, and remember how many times each earlier prefix sum has appeared, then at every index one map lookup tells you how many valid subarrays end there. The map starts with `0` seen once, which stands for the empty prefix and lets a subarray that starts at index 0 be counted.

## How

**Range sum queries**

1. Create `prefix` with `n + 1` slots, `prefix[0] = 0`.
2. For each `i` from 0 to `n - 1`, set `prefix[i + 1] = prefix[i] + nums[i]`.
3. Answer each query `(left, right)` with `prefix[right + 1] - prefix[left]`.

**Counting subarrays with sum `k`**

1. Start a map with `{0: 1}`, a running total of 0 and a count of 0.
2. For each element, add it to the running total.
3. Add the number of times `running - k` has been seen to the count.
4. Record the current running total in the map.

The order of steps 3 and 4 matters. Looking up before recording means a subarray is never matched against its own end point, which would wrongly count an empty subarray when `k` is 0.

**Pseudocode**

```
buildPrefix(nums):
    prefix = array of length(nums) + 1, filled with 0
    for i from 0 to length(nums) - 1:
        prefix[i + 1] = prefix[i] + nums[i]
    return prefix

rangeSum(prefix, left, right):
    return prefix[right + 1] - prefix[left]

countSubarraysWithSum(nums, k):
    seen = map with seen[0] = 1
    running = 0
    count = 0
    for value in nums:
        running = running + value
        count = count + seen.get(running - k, 0)
        seen[running] = seen.get(running, 0) + 1
    return count
```

**Worked example: range sums**

For `nums = [3, -1, 4, 2, -3, 5]`:

| i | 0 | 1 | 2 | 3 | 4 | 5 | 6 |
|---|---|---|---|---|---|---|---|
| nums[i] | 3 | -1 | 4 | 2 | -3 | 5 | |
| prefix[i] | 0 | 3 | 2 | 6 | 8 | 5 | 10 |

The sum of `nums[1..3]` is `prefix[4] - prefix[1] = 8 - 3 = 5`, which matches `-1 + 4 + 2`. The sum of the single element `nums[4]` is `prefix[5] - prefix[4] = 5 - 8 = -3`. The negative values cause no trouble.

**Worked example: counting subarrays**

Count the subarrays of `[1, 2, -1, 2, 1]` whose sum is 3.

| Index | Value | running | running - k | Times seen | count | Map after this step |
|---|---|---|---|---|---|---|
| start | | 0 | | | 0 | {0: 1} |
| 0 | 1 | 1 | -2 | 0 | 0 | {0: 1, 1: 1} |
| 1 | 2 | 3 | 0 | 1 | 1 | {0: 1, 1: 1, 3: 1} |
| 2 | -1 | 2 | -1 | 0 | 1 | {0: 1, 1: 1, 3: 1, 2: 1} |
| 3 | 2 | 4 | 1 | 1 | 2 | {0: 1, 1: 1, 3: 1, 2: 1, 4: 1} |
| 4 | 1 | 5 | 2 | 1 | 3 | {0: 1, 1: 1, 3: 1, 2: 1, 4: 1, 5: 1} |

The answer is 3. Each match points to where a subarray starts. At index 1 the match is the empty prefix, giving `[1, 2]`. At index 3 the match is the prefix 1, which ended after index 0, giving `nums[1..3] = [2, -1, 2]`. At index 4 the match is the prefix 2, which ended after index 2, giving `nums[3..4] = [2, 1]`.

**Java example**

```java
import java.util.*;

public class PrefixSumDemo {

    // Builds prefix sums with a leading 0: prefix[i] is the sum of the
    // first i elements, so prefix has one more slot than nums.
    static long[] buildPrefix(int[] nums) {
        long[] prefix = new long[nums.length + 1];
        for (int i = 0; i < nums.length; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }
        return prefix;
    }

    // Sum of nums[left..right], both ends inclusive, in O(1).
    // Assumes 0 <= left <= right < nums.length.
    static long rangeSum(long[] prefix, int left, int right) {
        return prefix[right + 1] - prefix[left];
    }

    // Counts the subarrays whose sum is exactly k. Works with negative values.
    static int countSubarraysWithSum(int[] nums, long k) {
        Map<Long, Integer> seen = new HashMap<>();
        // The empty prefix: lets a subarray that starts at index 0 be counted.
        seen.put(0L, 1);
        long running = 0;
        int count = 0;
        for (int value : nums) {
            running += value;
            // Every earlier prefix equal to running - k starts a subarray
            // that ends here and sums to k.
            count += seen.getOrDefault(running - k, 0);
            seen.put(running, seen.getOrDefault(running, 0) + 1);
        }
        return count;
    }

    public static void main(String[] args) {
        int[] nums = {3, -1, 4, 2, -3, 5};
        long[] prefix = buildPrefix(nums);
        System.out.println("prefix: " + Arrays.toString(prefix));
        System.out.println("sum of [1..3]: " + rangeSum(prefix, 1, 3));
        System.out.println("sum of [4..4]: " + rangeSum(prefix, 4, 4));
        System.out.println("sum of [0..5]: " + rangeSum(prefix, 0, 5));

        int[] values = {1, 2, -1, 2, 1};
        System.out.println("subarrays with sum 3: " + countSubarraysWithSum(values, 3));
        System.out.println("subarrays with sum 0 in [0, 0, 0]: " + countSubarraysWithSum(new int[] {0, 0, 0}, 0));
        System.out.println("subarrays with sum 5 in []: " + countSubarraysWithSum(new int[0], 5));
    }
}
```

Output:

```
prefix: [0, 3, 2, 6, 8, 5, 10]
sum of [1..3]: 5
sum of [4..4]: -3
sum of [0..5]: 10
subarrays with sum 3: 3
subarrays with sum 0 in [0, 0, 0]: 6
subarrays with sum 5 in []: 0
```

The last two lines check the edge cases. With `[0, 0, 0]` and `k = 0`, every one of the six non-empty subarrays counts, which is why the lookup happens before the current total is recorded. An empty array has no subarrays at all.

## Two dimensions

The same idea extends to a grid. Let `prefix[r][c]` hold the sum of the rectangle from the top-left corner up to, but not including, row `r` and column `c`. Each cell is built as `prefix[r][c] = grid[r - 1][c - 1] + prefix[r - 1][c] + prefix[r][c - 1] - prefix[r - 1][c - 1]`, where the last term is subtracted because it was added twice. The sum of any sub-rectangle then takes four lookups. It will be covered in detail with the Matrix problems.

## Problem clues

- Many queries asking for the sum of a range of a fixed array.
- "Number of subarrays whose sum equals `k`", especially when values can be negative.
- "Subarray sum divisible by `k`": store prefix sums modulo `k` in the map instead.
- "Longest subarray with sum `k`" or "with equal numbers of 0s and 1s": store the first index where each prefix sum appeared, instead of a count.
- A brute force that recomputes the sum of nearly the same range over and over.

Clues are hints, not rules. Always check that the pattern's assumptions actually hold for the problem. If every value is positive and you want the shortest or longest range meeting a sum bound, a sliding window gives the same answer in O(1) extra space.

## Complexity

- **Time:** O(n) to build the prefix array and O(1) per range query, so `q` queries cost O(n + q) in total instead of O(n · q). The counting version is O(n) on average, since it does one `HashMap` lookup and one update per element, each O(1) on average.
- **Auxiliary space:** O(n) for the prefix array, which has `n + 1` entries. The counting version needs O(n) for the map in the worst case, when every prefix sum is different, and O(1) besides that.

Prefix sums can be much larger than any single element. With `int` input, store them as `long`, as the example does, or they may overflow.

## Limitations

- The array must not change between queries. One update to `nums[i]` changes every prefix after it, which costs O(n). For frequent updates, use a Fenwick tree (binary indexed tree) or a segment tree, which handle both updates and range sums in O(log n).
- It answers questions about sums, or any operation that can be undone by subtraction, such as XOR. It does not work for range minimum or maximum, because there is no way to "subtract" the part before the range.
- The extra O(n) memory is real. If you only need one range sum, a plain loop is simpler.
- Off-by-one errors are common. Decide once whether `prefix[i]` covers the first `i` elements (as here) or the elements up to and including `i`, and use the matching formula everywhere.

## Alternatives

| Technique | Use it when | Trade-off |
|---|---|---|
| Plain loop over the range | You need one or very few range sums | No extra space, but O(n) per query |
| Sliding Window | All values are non-negative and you want the best range under a sum bound | O(1) extra space, but needs a monotonic condition |
| Difference array | You apply many range additions first and read the values afterwards | The reverse of prefix sums: O(1) per range update, one O(n) pass to rebuild |
| Fenwick tree or segment tree | The array changes between queries | O(log n) per update and per query, with more code |
| Brute force over all subarrays | The input is tiny | O(n²) time, but works for any condition |

The [Array](../DSA/01-Array/README.md) and [Hashing](../DSA/03-Hashing/README.md) topic guides cover the data structures this pattern relies on.

## Practice problems

None yet. Problems that use this pattern will be listed here as they are added.

## Quick revision

A prefix sum array stores the running total before each position, so the sum of `nums[left..right]` is `prefix[right + 1] - prefix[left]` in O(1) after an O(n) build. Combined with a hash map of earlier prefix sums, it counts subarrays with an exact sum in one O(n) pass, and unlike a sliding window it works with negative numbers. It needs a fixed array and an operation that subtraction can undo, and it uses O(n) extra space. Store the sums in `long` to avoid overflow.
