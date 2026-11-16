# Maximum Average Subarray I

**Source:** [LeetCode 643](https://leetcode.com/problems/maximum-average-subarray-i/) · **Difficulty:** Easy · **Topic:** [Array](../../README.md) · **Patterns:** [Sliding Window](../../../../Patterns/SlidingWindow.md)

## 1. Problem Statement

You are given an integer array `nums` of length `n` and an integer `k`. Look at every contiguous subarray of exactly `k` elements and return the largest average among them. The answer is a decimal number, and any answer within 10^-5 of the exact value is accepted.

**Constraints:**

- `n == nums.length`
- `1 <= k <= n <= 10^5`
- `-10^4 <= nums[i] <= 10^4`

## 2. Examples

**Example 1**

```
Input:  nums = [1, 12, -5, -6, 50, 3], k = 4
Output: 12.75
```

There are three windows of four elements. Their sums are 2, 51 and 42, so the best window is `[12, -5, -6, 50]` and its average is 51 / 4 = 12.75.

**Example 2**

```
Input:  nums = [5], k = 1
Output: 5.0
```

The only window is `[5]`.

**Example 3**

```
Input:  nums = [-1, -2, -3], k = 2
Output: -1.5
```

The windows `[-1, -2]` and `[-2, -3]` have averages -1.5 and -2.5. Every average is negative, and the answer is the largest of them.

## 3. Pattern Recognition

**What is the problem asking?** Among all `n - k + 1` windows of exactly `k` consecutive elements, find the one with the largest average and return that average.

**What clues are present?**

- The subarray must be contiguous, and its length is fixed at `k`.
- Two neighboring windows share `k - 1` elements. Moving one step right adds one element and removes one.
- Every window has the same length, so comparing averages is the same as comparing sums.

**Which patterns should I consider?** Adding up every window from scratch, a prefix sum array that gives each window's sum by subtraction, and a fixed-size [Sliding Window](../../../../Patterns/SlidingWindow.md) that carries one running sum.

**Why does the chosen pattern fit?** "Every window of exactly `k` consecutive elements" is the fixed-size form of Sliding Window. The next window's sum is the current sum plus the element entering on the right minus the element leaving on the left, so each step costs O(1) instead of O(k). Because the length never changes, the window with the largest sum also has the largest average, and the division can wait until the end.

**Why are the alternatives less suitable?** Recomputing every window costs O(n·k), which is about 2.5 × 10^9 additions when `k` is near `n / 2` and `n` is 10^5. A [Prefix Sum](../../../../Patterns/PrefixSum.md) array also gives O(n) time, since each window's sum is `prefix[i + k] - prefix[i]`, but it stores `n + 1` extra numbers to answer questions that the running sum answers with one variable. Prefix sums are the better tool when the window length varies or when many different ranges are asked about, as in the follow-ups below.

**What assumptions must hold?** The window length is fixed, and `1 <= k <= n`, so at least one window exists. Negative values are fine here. The variable-size form of Sliding Window needs non-negative values to decide when to shrink, but the fixed-size form never makes that decision, so the sign of the values does not matter.

**How do the constraints influence the choice?** With `n` up to 10^5, the O(n·k) scan is too slow in the worst case, so an O(n) method is needed. The constraints also settle the number type. A window sum is at most `k × 10^4 <= 10^5 × 10^4 = 10^9` in absolute value, which is below the `int` limit of 2,147,483,647. Each slide changes the sum by `nums[right] - nums[right - k]`, which is at most 2 × 10^4 in absolute value, and the result is again a window sum, so no intermediate value goes beyond 10^9 either. An `int` sum is safe, and only the final average needs a `double`.

## 4. Prerequisites

- [Array](../../README.md): indexing and scanning an array from left to right.
- [Sliding Window](../../../../Patterns/SlidingWindow.md): the fixed-size form, which this problem uses directly. The guide's `maxSumOfSizeK` method is the same idea.
- [Running Sum of 1d Array](../002-running-sum-of-1d-array/README.md) (Array, Easy 002): keeping a running total instead of re-adding, which the window does with one subtraction added.

## 5. Intuition

Imagine a frame that covers exactly `k` cells of the array. Slide it one cell to the right. One cell disappears from the left edge of the frame and one new cell appears on the right edge, and the `k - 1` cells in between stay where they were. There is no reason to add those middle cells up again.

So add the first `k` elements once, then for every slide add the new element and subtract the old one. Keep the largest sum you have seen. Since every frame holds `k` cells, the frame with the largest sum also has the largest average, so divide by `k` only once at the very end.

## 6. Brute-Force Approach

Try every starting index, add up the `k` elements from there, and keep the largest sum.

```
best = minus infinity
for start from 0 to n - k:
    sum = nums[start] + ... + nums[start + k - 1]
    best = max(best, sum)
return best / k
```

This is correct and uses O(1) extra space, but there are `n - k + 1` windows and each costs `k` additions, so the time is O((n - k + 1) · k), which is O(n·k). It is worst when `k` is about `n / 2`. Each window repeats `k - 1` additions from the window before it.

## 7. Preferred Approach

**Core idea:** Compute the first window's sum, then slide the window one step at a time, adding the element that enters and subtracting the element that leaves. Track the largest sum and divide it by `k` at the end.

**Invariant:** At the start of the loop step for `right`, `windowSum` equals `nums[right - k] + ... + nums[right - 1]`, and `maxSum` is the largest sum of any window that ends at or before `right - 1`.

**Why it is correct:** Before the loop, `windowSum` is the sum of `nums[0..k - 1]`, the only window that ends at `k - 1`, and `maxSum` equals it, so the invariant holds for `right = k`. In the step for `right`, adding `nums[right]` and subtracting `nums[right - k]` turns the sum of `nums[right - k..right - 1]` into the sum of `nums[right - k + 1..right]`, the window ending at `right`. Comparing it with `maxSum` keeps the second half of the invariant. When the loop ends, every window has been compared once, so `maxSum` is the largest window sum. All windows have length `k`, and dividing by the same positive number keeps the order, so `maxSum / k` is the largest average.

**Steps:**

1. Add `nums[0]` to `nums[k - 1]` into `windowSum`.
2. Set `maxSum` to `windowSum`.
3. For each `right` from `k` to `n - 1`:
   1. Add `nums[right] - nums[right - k]` to `windowSum`.
   2. Set `maxSum` to the larger of `maxSum` and `windowSum`.
4. Return `maxSum` divided by `k` as a `double`.

`maxSum` starts at the first window's sum rather than at 0, because every window can have a negative sum, as in Example 3.

**Trade-offs:** The running sum uses O(1) extra space and reads each element at most twice. It only answers questions about windows of one fixed length. If the length can vary, or if you need the sums of many arbitrary ranges, build a prefix sum array instead. Comparing integer sums and dividing once also avoids doing `n - k + 1` floating-point divisions and comparing values that have already been rounded.

## 8. Dry Run

**Input:** `nums = [1, 12, -5, -6, 50, 3]`, `k = 4`

The first loop adds `1 + 12 + (-5) + (-6) = 2`, so `windowSum = 2` and `maxSum = 2`. This is the window `[1, 12, -5, -6]`, indexes 0 to 3.

| `right` | Enters `nums[right]` | Leaves `nums[right - k]` | `windowSum` after | Window | `maxSum` after |
|---|---|---|---|---|---|
| 4 | 50 | 1 | 2 + 50 - 1 = 51 | `[12, -5, -6, 50]` | 51 |
| 5 | 3 | 12 | 51 + 3 - 12 = 42 | `[-5, -6, 50, 3]` | 51 |

The loop ends after `right = 5`, and the method returns 51 / 4 = 12.75.

At `right = 4` the large value 50 enters while the small value 1 leaves, so the sum jumps from 2 to 51. At `right = 5` the sum drops because 12 leaves and only 3 enters, so `maxSum` stays 51. Only two slides were needed after the first window, and each one did one addition and one subtraction, however large `k` is.

## 9. Java Solution

See [Solution.java](Solution.java). To run it:

```bash
javac Solution.java
java Solution
```

Expected output:

```
nums = [1, 12, -5, -6, 50, 3], k = 4 -> maximum average = 12.75
nums = [5], k = 1 -> maximum average = 5.0
nums = [-1, -2, -3], k = 2 -> maximum average = -1.5
nums = [0, 4, 0, 3, 2], k = 1 -> maximum average = 4.0
nums = [3, 3, 4, 3, 0], k = 3 -> maximum average = 3.3333333333333335
nums = [4, -1, 2], k = 3 -> maximum average = 1.6666666666666667
nums = 100000 copies of 10000, k = 100000 -> maximum average = 10000.0
nums = 100000 copies of -10000, k = 100000 -> maximum average = -10000.0
```

The last two lines use the largest window allowed by the constraints. Their window sums are 10^9 and -10^9, which shows that an `int` sum does not overflow. Averages such as 10 / 3 cannot be stored exactly in a `double`, so Java prints the closest `double`, 3.3333333333333335. The problem accepts any answer within 10^-5, so this is fine.

Runs on Java 8, 11, 17, 21 and 25.

## 10. Complexity Analysis

- **Time:** O(n). The first loop does `k` additions and the second loop does `n - k` slides, each with a constant amount of work, so the total is `k + (n - k) = n` steps.
- **Auxiliary space:** O(1), because only `windowSum`, `maxSum` and the loop index are stored, however long the array is or however large `k` is.

## 11. Edge Cases

- **`k` equals 1,** such as `[0, 4, 0, 3, 2]` with `k = 1`. Every window is a single element, so the answer is the largest element, 4.0.
- **`k` equals `n`,** such as `[4, -1, 2]` with `k = 3`. There is only one window, the second loop does not run, and the answer is the average of the whole array, 5 / 3.
- **A single element,** such as `[5]` with `k = 1`. This is both of the cases above at once.
- **Every value negative,** such as `[-1, -2, -3]` with `k = 2`. The answer is negative, which is why `maxSum` must start at the first window's sum and not at 0.
- **Ties,** such as `[3, 3, 4, 3, 0]` with `k = 3`, where the first two windows both sum to 10. Only the value is returned, so it does not matter which window is counted.
- **The largest allowed sums,** 10^5 copies of 10^4 or of -10^4 with `k = 10^5`. The sums are ±10^9, which fit in an `int`.

## 12. Common Mistakes

- **Starting `maxSum` at 0.** When every window sum is negative, nothing beats 0, and the method returns 0.0 instead of the real answer. For `[-1, -2, -3]` with `k = 2` it would return 0.0 instead of -1.5. Start from the first window, or from `Integer.MIN_VALUE`.
- **Removing the wrong element.** The element that leaves when `nums[right]` enters is `nums[right - k]`, not `nums[right - k + 1]` or `nums[right - 1]`. Check it on the dry run: at `right = 4` with `k = 4`, the element leaving is `nums[0]`.
- **Using integer division.** `maxSum / k` with two `int` values throws away the fraction, so 51 / 4 gives 12 instead of 12.75. Cast one side to `double` first, as in `(double) maxSum / k`.
- **Starting the slide loop at the wrong index.** The first window already covers indexes 0 to `k - 1`, so the loop that slides must start at `right = k`. Starting at `right = k - 1` would add `nums[k - 1]` a second time and subtract `nums[-1]`, so it throws an `ArrayIndexOutOfBoundsException`.
- **Assuming the variable-window rules apply.** The variable form needs non-negative values to know when to shrink. This problem has a fixed length, so negative values cause no trouble, and there is no shrinking loop.

## 13. Interview Follow-ups

- **Return the window itself, not just its average.** Record the start index `right - k + 1` whenever `windowSum` beats `maxSum`, and `0` for the first window. The cost stays O(n) time and O(1) space.
- **Count the windows whose average is at least a threshold** ([LeetCode 1343](https://leetcode.com/problems/number-of-sub-arrays-of-size-k-and-average-greater-than-or-equal-to-threshold/)). Slide the same fixed window and count the windows with `windowSum >= threshold * k`, which avoids division altogether.
- **The window length is at least `k` instead of exactly `k`** ([LeetCode 644](https://leetcode.com/problems/maximum-average-subarray-ii/), Hard). Longer windows have different lengths, so the largest sum is no longer the largest average, and a single fixed window cannot solve it. A common approach is to binary search on the answer and use prefix sums to test whether some window of length at least `k` reaches a candidate average.
- **Return the maximum element of every window instead of the sum** ([LeetCode 239](https://leetcode.com/problems/sliding-window-maximum/)). A maximum cannot be updated by subtracting the element that leaves, so the window needs a monotonic deque instead of a single number.
- **What if the values or `k` were larger?** If `k × max|nums[i]|` could exceed about 2.1 × 10^9, store `windowSum` and `maxSum` as `long`.

## 14. Revision Summary

All windows have the same length `k`, so the window with the largest sum has the largest average: compare integer sums and divide once at the end. This is the fixed-size [Sliding Window](../../../../Patterns/SlidingWindow.md) pattern, where each slide adds `nums[right]` and subtracts `nums[right - k]`. Start `maxSum` from the first window because averages can be negative, and use a `double` division at the end. With these constraints an `int` sum is safe, and the method takes O(n) time and O(1) auxiliary space.
