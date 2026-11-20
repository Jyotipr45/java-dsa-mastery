# Subarray Sum Equals K

**Source:** [LeetCode 560](https://leetcode.com/problems/subarray-sum-equals-k/) · **Difficulty:** Medium · **Topic:** [Array](../../README.md) · **Patterns:** [Prefix Sum](../../../../Patterns/PrefixSum.md), [Hashing](../../../../Patterns/Hashing.md)

## 1. Problem Statement

You are given an array of integers `nums` and an integer `k`. Count how many contiguous, non-empty subarrays of `nums` have a sum of exactly `k`.

**Constraints:**

- `1 <= nums.length <= 2 * 10^4`
- `-1000 <= nums[i] <= 1000`
- `-10^7 <= k <= 10^7`

## 2. Examples

**Example 1**

```
Input:  nums = [1, 1, 1], k = 2
Output: 2
```

The subarrays `nums[0..1]` and `nums[1..2]` both sum to 2. They contain the same values but start at different positions, so they count separately.

**Example 2**

```
Input:  nums = [1, 2, 3], k = 3
Output: 2
```

`[1, 2]` and `[3]` both sum to 3.

**Example 3**

```
Input:  nums = [1, -1, 5], k = 5
Output: 2
```

`[5]` and `[1, -1, 5]` both sum to 5. This is the input that breaks the sliding window in [Minimum Size Subarray Sum](../001-minimum-size-subarray-sum/README.md).

## 3. Pattern Recognition

**What is the problem asking?** Count the pairs of positions `(start, end)` for which `nums[start..end]` adds up to exactly `k`. The answer is a count, not a length or a single subarray.

**What clues are present?**

- "Subarray" means contiguous, and the question is about the sum of a range.
- The condition is an exact value, not a bound such as "at least `k`".
- The values can be negative or zero. This is the clue that rules out the obvious window.
- The answer counts every matching subarray, so overlapping and repeated ones all count.

**Which patterns should I consider?** Checking every subarray, a variable-size [Sliding Window](../../../../Patterns/SlidingWindow.md), and [Prefix Sum](../../../../Patterns/PrefixSum.md) combined with a [Hashing](../../../../Patterns/Hashing.md) frequency map.

**Why does the chosen pattern fit?** The sum of any range is the difference of two prefix sums: `sum(nums[start..end]) = prefix[end + 1] - prefix[start]`. Asking for that difference to equal `k` is the same as asking, at each end, how many earlier prefix sums equal `prefix[end + 1] - k`. That is a complement lookup, the same idea as [Two Sum](../../Easy/004-two-sum/README.md), but on prefix sums instead of values. A map from prefix sum to how many times it has appeared answers it in O(1) on average, so the whole count takes one pass.

**Why are the alternatives less suitable?** Checking every start and end is correct, but it is O(n²): about 2 × 10^8 additions for `n = 2 * 10^4`. That may pass, but it is the solution an interviewer expects you to improve. A sliding window does not work at all. It relies on adding an element always raising the sum and removing one always lowering it, which is only true for positive values. With `[1, -1, 5]` and `k = 5`, a window that shrinks once its sum is at least 5 misses `[5]` entirely (Minimum Size Subarray Sum shows the same input going wrong). Zeros break it too: in `[0, 0]` with `k = 0`, three subarrays match, and a window has no rule for when to grow or shrink. A sorted prefix array with binary search, the follow-up in Minimum Size Subarray Sum, also fails, because with negative values the prefix sums are not in increasing order.

**What assumptions must hold?** Sums of ranges must be differences of prefix sums, which is true for addition. No assumption about the sign of the values is needed. The map must be able to hold up to `n + 1` distinct prefix sums.

**How do the constraints influence the choice?** With `n` up to 2 × 10^4, O(n) with a map is comfortable. The constraints also settle the number type. Every prefix sum lies between `-2 * 10^4 × 1000 = -2 * 10^7` and `2 * 10^7`, and `running - k` lies between `-3 * 10^7` and `3 * 10^7`, so `int` is safe for both. The count itself is largest when every subarray matches, as with 2 × 10^4 zeros and `k = 0`: `n(n + 1) / 2 = 200,010,000`, which also fits in an `int`. The sample prints both extreme cases.

## 4. Prerequisites

- [Array](../../README.md): contiguous ranges and index arithmetic.
- [Prefix Sum](../../../../Patterns/PrefixSum.md): its "Counting subarrays with sum `k`" section is this problem's algorithm, with a worked example on a different input. This README does not repeat that explanation; it focuses on why the pattern is the right choice here and on the details that are easy to get wrong.
- [Hashing](../../../../Patterns/Hashing.md): frequency counts and looking up before recording.
- [Two Sum](../../Easy/004-two-sum/README.md) (Array, Easy 004): the same complement lookup on values.
- [Minimum Size Subarray Sum](../001-minimum-size-subarray-sum/README.md) (Array, Medium 001): the positive-values case, and why its window fails here.

## 5. Intuition

Walk through the array keeping a running total. When you stand at index `end` with total `running`, a subarray that ends here sums to `k` exactly when the total just before its start was `running - k`. So the question "how many subarrays ending here sum to `k`?" becomes "how many times have I already seen the total `running - k`?".

If you keep a count of every total you have seen, that question is one map lookup. Add the answer to the count, record the current total, and move on.

Two details make it work. The total before index 0 is 0, so the map has to start with `0` seen once; otherwise a subarray that starts at index 0 is never counted. And the current total must be recorded after the lookup, not before, so it is never matched with itself.

## 6. Brute-Force Approach

Fix each start, then extend the end one step at a time, keeping the sum as you go. Unlike in Minimum Size Subarray Sum, you cannot stop at the first match, because negative values can bring the sum back to `k` later.

```
count = 0
for start from 0 to n - 1:
    sum = 0
    for end from start to n - 1:
        sum = sum + nums[end]
        if sum == k:
            count = count + 1
return count
```

This is O(n²) time and O(1) extra space. Recomputing each sum from scratch instead of extending it would make it O(n³).

## 7. Preferred Approach: Prefix Sums with a Frequency Map

**Core idea:** Keep a running total and a map from each total seen so far to how many times it was seen. At each index, add `seen[running - k]` to the count, then record `running`.

**Invariant:** Before processing index `i`, `seen` holds the totals of the prefixes `nums[0..j - 1]` for every `j` from 0 to `i` (including the empty prefix, total 0), each with its number of occurrences, and `count` is the number of matching subarrays that end before `i`.

**Why it is correct:** A subarray `nums[start..i]` sums to `k` exactly when `prefix[i + 1] - prefix[start] = k`, that is, when `prefix[start] = running - k`, where `running = prefix[i + 1]`. The possible starts are 0 to `i`, which are exactly the prefixes stored in `seen` at that moment. So `seen[running - k]` is the number of matching subarrays ending at `i`. Every subarray has exactly one end, so adding these numbers over all `i` counts each matching subarray once.

**Steps:**

1. Put `0 -> 1` in `seen`. Set `running = 0` and `count = 0`.
2. For each value in `nums`:
   1. Add it to `running`.
   2. Add `seen[running - k]` (0 if absent) to `count`.
   3. Increase `seen[running]` by 1.
3. Return `count`.

**Trade-offs:** It needs O(n) extra space for the map, and each `HashMap` operation boxes its `Integer` keys and values, so the constant factor is larger than a plain loop. When every value is positive and the question is about a bound such as "at least `k`", a sliding window uses O(1) space instead. When the prefix sums fit in a small range, an array indexed by `running + offset` can replace the map, but here the prefix sums span 4 × 10^7 possible values, so an array would need far more memory than the at most 2 × 10^4 + 1 entries the map ever holds.

## 8. Dry Run

**Input:** `nums = [3, 4, 7, 2, -3, 1, 4, 2]`, `k = 7`

`seen` starts as `{0: 1}`.

| Index | Value | `running` | `running - k` | Times seen | `count` | `seen` after recording |
|---|---|---|---|---|---|---|
| 0 | 3 | 3 | -4 | 0 | 0 | `{0: 1, 3: 1}` |
| 1 | 4 | 7 | 0 | 1 | 1 | `{0: 1, 3: 1, 7: 1}` |
| 2 | 7 | 14 | 7 | 1 | 2 | `{0: 1, 3: 1, 7: 1, 14: 1}` |
| 3 | 2 | 16 | 9 | 0 | 2 | `{0: 1, 3: 1, 7: 1, 14: 1, 16: 1}` |
| 4 | -3 | 13 | 6 | 0 | 2 | `{0: 1, 3: 1, 7: 1, 14: 1, 16: 1, 13: 1}` |
| 5 | 1 | 14 | 7 | 1 | 3 | `{0: 1, 3: 1, 7: 1, 14: 2, 16: 1, 13: 1}` |
| 6 | 4 | 18 | 11 | 0 | 3 | `{0: 1, 3: 1, 7: 1, 14: 2, 16: 1, 13: 1, 18: 1}` |
| 7 | 2 | 20 | 13 | 1 | 4 | `{0: 1, 3: 1, 7: 1, 14: 2, 16: 1, 13: 1, 18: 1, 20: 1}` |

The method returns 4.

Each match points back to where its subarray starts. At index 1, the total 0 is the empty prefix, so the subarray is `[3, 4]` from index 0; without the starting `{0: 1}` it would be missed. At index 2, the total 7 was recorded after index 1, so the subarray is `[7]`. At index 5, the same total 7 is found again, giving `[7, 2, -3, 1]`, a subarray that only exists because the -3 brings the sum back down. At index 7, the total 13 was recorded after index 4, giving `[1, 4, 2]`. Notice also that the total 14 appears twice (after indexes 2 and 5), so its count in the map is 2; a later lookup for 14 would count both starts.

## 9. Java Solution

See [Solution.java](Solution.java). To run it:

```bash
javac Solution.java
java Solution
```

Expected output:

```
nums = [1, 1, 1], k = 2 -> 2
nums = [1, 2, 3], k = 3 -> 2
nums = [3, 4, 7, 2, -3, 1, 4, 2], k = 7 -> 4
nums = [1, -1, 5], k = 5 -> 2
nums = [1, -1, 1, -1], k = 0 -> 4
nums = [5], k = 3 -> 0
nums = 20000 zeros, k = 0 -> 200010000
nums = 20000 copies of 1000, k = 10000000 -> 10001
```

The last two lines use the largest inputs the constraints allow. With 20,000 zeros and `k = 0`, every one of the `20000 × 20001 / 2` subarrays matches, which is the largest possible count. With 20,000 copies of 1000 and the largest `k`, only subarrays of length exactly 10,000 match, and there are `20000 - 10000 + 1 = 10001` of them; the prefix sums reach 2 × 10^7 along the way.

Runs on Java 8, 11, 17, 21 and 25.

## 10. Complexity Analysis

- **Time:** O(n) on average. The loop visits each element once and does one map lookup and one map update, each O(1) on average for a `HashMap`. In the rare worst case where many keys collide, Java 8 and later turn a crowded bucket into a balanced tree, so a single operation is O(log n) rather than O(n).
- **Auxiliary space:** O(n) for the map, which holds at most `n + 1` distinct prefix sums (the empty prefix and one per index). `running` and `count` are O(1).

## 11. Edge Cases

- **`k = 0`.** Subarrays that cancel out, such as `[1, -1]`, must be counted, and empty subarrays must not. `[1, -1, 1, -1]` gives 4: `[1, -1]` at indexes 0 to 1, 1 to 2 and 2 to 3, and the whole array. Looking up before recording is what keeps the empty subarrays out.
- **Runs of zeros.** Every subarray of zeros matches `k = 0`, so the count grows quadratically, up to 200,010,000 for the largest input. The map handles this through its counts: the total 0 is seen many times, and each lookup adds all of them at once.
- **A subarray that starts at index 0,** such as `[3, 4]` in the dry run. It is found only because the map starts with `{0: 1}`.
- **No matching subarray,** such as `[5]` with `k = 3`. Every lookup returns 0 and the count stays 0.
- **Negative `k`,** for example `k = -2` with `[-1, -1]`. Nothing changes: `running - k` is just a larger number to look up.
- **An array of length 1.** The answer is 1 if the single value equals `k` and 0 otherwise.

## 12. Common Mistakes

- **Using a sliding window.** It assumes positive values. Here values can be negative or zero, and the window misses subarrays, as in `[1, -1, 5]` with `k = 5`.
- **Forgetting to start the map with `{0: 1}`.** Subarrays that begin at index 0 are then never counted. For `[1, 1, 1]` with `k = 2`, this returns 1 instead of 2.
- **Recording the current total before looking it up.** When `k = 0`, `running - k` is `running` itself, so each index matches its own total and counts an empty subarray. For `[1, 2, 3]` with `k = 0`, this returns 3 instead of 0.
- **Storing a set or a single index instead of a count.** The same total can appear many times, as 14 does in the dry run, and each occurrence is a different start. A set counts it once and gives too small an answer. A first index is the right thing to store only when the question asks for the longest matching subarray.
- **Stopping the brute force at the first match.** That shortcut only works with positive values. With `[1, -1, 1, -1]` and `k = 0`, the start at index 0 matches at index 1 and again at index 3.

## 13. Interview Follow-ups

- **Return the longest subarray with sum `k`** ([LeetCode 325](https://leetcode.com/problems/maximum-size-subarray-sum-equals-k/)). Store the first index where each total appeared instead of a count, and never overwrite it.
- **Count subarrays whose sum is divisible by `k`** ([LeetCode 974](https://leetcode.com/problems/subarray-sums-divisible-by-k/)). Use `((running % k) + k) % k` as the key, so negative totals land on the same key as their positive equivalents.
- **Equal numbers of 0s and 1s** ([LeetCode 525](https://leetcode.com/problems/contiguous-array/)). Replace each 0 with -1 and look for the longest subarray with sum 0.
- **Count subarrays with exactly `k` odd numbers** ([LeetCode 1248](https://leetcode.com/problems/count-number-of-nice-subarrays/)). Map each value to 1 if odd and 0 if even, and the problem becomes this one.
- **The values are all positive.** The map still works, but a sliding window that counts windows with sum exactly `k` also works in O(1) space, because the window rules from Minimum Size Subarray Sum hold again.
- **A 2D version: count submatrices with sum `k`** ([LeetCode 1074](https://leetcode.com/problems/number-of-submatrices-that-sum-to-target/), Hard). Fix a pair of rows, collapse the columns between them into a 1D array of column sums, and run this algorithm on it, for O(rows² × cols) time.

## 14. Revision Summary

The sum of `nums[start..end]` is `prefix[end + 1] - prefix[start]`, so counting subarrays with sum `k` becomes counting, at each end, the earlier prefix sums equal to `running - k`: a [Prefix Sum](../../../../Patterns/PrefixSum.md) combined with a [Hashing](../../../../Patterns/Hashing.md) frequency map. Start the map with `{0: 1}` for the empty prefix, and look up before recording so a prefix never matches itself. It works with negative values and zeros, where a sliding window fails, and runs in O(n) average time with O(n) auxiliary space.
