# Minimum Size Subarray Sum

**Source:** [LeetCode 209](https://leetcode.com/problems/minimum-size-subarray-sum/) · **Difficulty:** Medium · **Topic:** [Array](../../README.md) · **Patterns:** [Sliding Window](../../../../Patterns/SlidingWindow.md), [Prefix Sum](../../../../Patterns/PrefixSum.md)

## 1. Problem Statement

You are given an array of positive integers `nums` and a positive integer `target`. Find the length of the shortest contiguous subarray whose sum is greater than or equal to `target`. If no subarray reaches `target`, return 0.

**Constraints:**

- `1 <= target <= 10^9`
- `1 <= nums.length <= 10^5`
- `1 <= nums[i] <= 10^4`

The problem also asks, as a follow-up, for an O(n log n) solution once you have an O(n) one.

## 2. Examples

**Example 1**

```
Input:  target = 7, nums = [2, 3, 1, 2, 4, 3]
Output: 2
```

The subarray `[4, 3]` has sum 7. No single element reaches 7, so no shorter subarray works.

**Example 2**

```
Input:  target = 4, nums = [1, 4, 4]
Output: 1
```

The single element `[4]` already reaches the target.

**Example 3**

```
Input:  target = 11, nums = [1, 1, 1, 1, 1, 1, 1, 1]
Output: 0
```

The whole array sums to 8, so no subarray reaches 11.

## 3. Pattern Recognition

**What is the problem asking?** Among all contiguous subarrays whose sum is at least `target`, find the length of the shortest one, or report that none exists.

**What clues are present?**

- "Subarray" means contiguous, and the question asks for the shortest one that meets a condition.
- The condition is a bound on a sum ("at least `target`"), not an exact value.
- Every value is positive. This is the clue that matters most, and it is easy to skip past.

**Which patterns should I consider?** Checking every subarray, [Prefix Sum](../../../../Patterns/PrefixSum.md) combined with binary search, and a variable-size [Sliding Window](../../../../Patterns/SlidingWindow.md) that grows on the right and shrinks from the left.

**Why does the chosen pattern fit?** Because every value is positive, the sum of a window behaves predictably. Adding an element on the right always raises the sum, and removing one on the left always lowers it. So once a window reaches `target`, the only way a shorter window ending at the same place can still reach it is by dropping elements from the left, and as soon as the sum falls below `target`, dropping more cannot bring it back. That lets `left` move only forward, and each index enters and leaves the window at most once.

**Why are the alternatives less suitable?** Checking every start and end is O(n²) in the worst case, about 5 × 10^9 steps for `n = 10^5`. Prefix sums with binary search are correct and run in O(n log n), which is what the follow-up asks for, but they need O(n) extra space and do more work than the window. They are worth knowing because the same idea extends to problems where a single window does not apply, as in the follow-ups below.

**What assumptions must hold?** All values must be positive (non-negative would also work for the window). With a negative value, removing an element from the left can raise the sum, so the window can stop shrinking too early. For `nums = [1, -1, 5]` and `target = 5`, the window reaches the sum 5 at `[1, -1, 5]`, removes the 1, sees the sum 4 and stops, so it returns 3 even though `[5]` alone has length 1. Problems with negative values need a different method, see the follow-ups.

**How do the constraints influence the choice?** With `n` up to 10^5, O(n²) is too slow and both O(n) and O(n log n) are fine. The constraints also settle the number type. The sum of the whole array is at most `10^5 × 10^4 = 10^9`, so every window sum and every prefix sum fits in an `int`. The binary search compares against `prefix[start] + target`. Here `prefix[start]` is at most 10^9 - 1 (it leaves out at least the last element) and `target` is at most 10^9, so the sum stays below 2 × 10^9, just under the `int` limit of 2,147,483,647. That margin is small, so the code computes this one value as a `long` and stays correct if the limits grow.

## 4. Prerequisites

- [Array](../../README.md): scanning an array and working with index ranges.
- [Sliding Window](../../../../Patterns/SlidingWindow.md): the variable-size form. Its "Why it works" section explains why positive values are required, and its `shortestWithSumAtLeast` method is this problem's algorithm.
- [Prefix Sum](../../../../Patterns/PrefixSum.md): range sums as `prefix[end] - prefix[start]`, used by the O(n log n) approach.
- [Maximum Average Subarray I](../../Easy/005-maximum-average-subarray-i/README.md) (Array, Easy 005): the fixed-size window, which this problem turns into a window whose size changes.

## 5. Intuition

Think of the window as a stretch of the array that you can extend on the right and trim on the left. Extend it one element at a time. As long as its sum is below `target`, it is too short, so keep extending.

The moment the sum reaches `target`, you have a valid window. It might contain extra elements at the left that it does not need, so trim them one at a time, recording the length each time the sum is still at least `target`. When trimming pushes the sum below `target`, stop and extend again.

The reason you never need to move `left` backwards is that the values are positive. If the window `[left..right]` is too small in sum after trimming, any window that starts further left and ends at `right` was already recorded, and any window that starts at `left` or later and ends at `right` is smaller still.

## 6. Brute-Force Approach

Try every start index. From each start, add elements to the right until the sum reaches `target`, record that length and move to the next start. Because values are positive, there is no point continuing past the first end that reaches `target`.

```
shortest = infinity
for start from 0 to n - 1:
    sum = 0
    for end from start to n - 1:
        sum = sum + nums[end]
        if sum >= target:
            shortest = min(shortest, end - start + 1)
            stop this inner loop
return 0 if shortest is infinity, otherwise shortest
```

This uses O(1) extra space, but when no subarray or only long subarrays reach `target`, each start scans almost to the end, so the time is O(n²).

## 7. Improved Approach: Prefix Sums and Binary Search

This is the O(n log n) solution the follow-up asks for.

Build `prefix` with `prefix[0] = 0` and `prefix[i + 1] = prefix[i] + nums[i]`, so the sum of `nums[start..end - 1]` is `prefix[end] - prefix[start]`. Because every value is positive, `prefix` is strictly increasing. For each `start`, the shortest valid subarray ends at the smallest `end` with `prefix[end] >= prefix[start] + target`. A strictly increasing array can be binary searched, so each `start` costs O(log n).

```
build prefix of length n + 1
shortest = infinity
for start from 0 to n - 1:
    end = first index in [start + 1, n] with prefix[end] >= prefix[start] + target
    if end exists:
        shortest = min(shortest, end - start)
return 0 if shortest is infinity, otherwise shortest
```

For Example 1, `prefix = [0, 2, 5, 6, 8, 12, 15]`. At `start = 4`, `prefix[4] = 8`, so the search looks for the first `prefix[end] >= 8 + 7 = 15` and finds `end = 6`. That is the subarray `nums[4..5] = [4, 3]`, of length `6 - 4 = 2`. At `start = 5`, it looks for `prefix[end] >= 12 + 7 = 19` and finds nothing.

The time is O(n) to build the array plus O(log n) for each of the `n` searches, so O(n log n). The extra space is O(n) for `prefix`. It is implemented as `minSubArrayLenBinarySearch` in [Solution.java](Solution.java), and the program prints both results so you can see that they agree.

## 8. Preferred Approach: Shrinking Sliding Window

**Core idea:** Move `right` across the array, adding `nums[right]` to the window. While the window's sum is at least `target`, record its length, then remove `nums[left]` and move `left` forward.

**Invariant:** At the start of each step for `right`, `windowSum` equals the sum of `nums[left..right - 1]`, that sum is below `target`, and `shortest` is the length of the shortest valid subarray that ends before `right`.

**Why it is correct:** Fix an end index `right`, and let `best(right)` be the largest start such that `nums[best(right)..right]` reaches `target`. Since values are positive, every start at or before `best(right)` also gives a valid window, and the shortest one ending at `right` starts exactly at `best(right)`. The inner loop removes elements from the left while the sum stays at least `target`, recording each length, so it records the window starting at `best(right)` and stops right after it. `best(right)` never decreases as `right` grows, because adding an element on the right only raises the sum, so `left` never needs to move back. Every end index therefore has its shortest valid window recorded, and the minimum of these is the answer.

**Steps:**

1. Set `left = 0`, `windowSum = 0` and `shortest = Integer.MAX_VALUE`.
2. For each `right` from 0 to `n - 1`:
   1. Add `nums[right]` to `windowSum`.
   2. While `windowSum >= target`, set `shortest` to the smaller of `shortest` and `right - left + 1`, subtract `nums[left]` from `windowSum` and add 1 to `left`.
3. Return 0 if `shortest` is still `Integer.MAX_VALUE`, otherwise `shortest`.

**Trade-offs:** The window uses O(1) extra space and O(n) time, which beats the prefix sum approach on both counts. Its weakness is that it depends completely on the values being positive. The prefix sum approach also depends on it here (for binary search), but prefix sums adapt to more problems, for example with a monotonic deque when values can be negative.

## 9. Dry Run

**Input:** `target = 7`, `nums = [2, 3, 1, 2, 4, 3]`

| `right` | Enters | `windowSum` after adding | Inner loop | `left` after | `windowSum` after | `shortest` |
|---|---|---|---|---|---|---|
| 0 | 2 | 2 | does not run (2 < 7) | 0 | 2 | none |
| 1 | 3 | 5 | does not run | 0 | 5 | none |
| 2 | 1 | 6 | does not run | 0 | 6 | none |
| 3 | 2 | 8 | records `[0..3]`, length 4; removes 2, sum 6 | 1 | 6 | 4 |
| 4 | 4 | 10 | records `[1..4]`, length 4; removes 3, sum 7; records `[2..4]`, length 3; removes 1, sum 6 | 3 | 6 | 3 |
| 5 | 3 | 9 | records `[3..5]`, length 3; removes 2, sum 7; records `[4..5]`, length 2; removes 4, sum 3 | 5 | 3 | 2 |

The loop ends and the method returns 2.

The first valid window appears at `right = 3`, when the sum reaches 8. At `right = 4` the window can lose two elements from the left and still be valid at length 3. At `right = 5` it trims down to `[4, 3]`, length 2. Across the whole run, `left` moved forward five times and `right` six times, so the total work is linear even though the inner loop sometimes runs more than once.

## 10. Java Solution

See [Solution.java](Solution.java). `minSubArrayLen` is the sliding window and `minSubArrayLenBinarySearch` is the prefix sum follow-up. To run it:

```bash
javac Solution.java
java Solution
```

Expected output:

```
target = 7, nums = [2, 3, 1, 2, 4, 3] -> window: 2, binary search: 2
target = 4, nums = [1, 4, 4] -> window: 1, binary search: 1
target = 11, nums = [1, 1, 1, 1, 1, 1, 1, 1] -> window: 0, binary search: 0
target = 15, nums = [1, 2, 3, 4, 5] -> window: 5, binary search: 5
target = 6, nums = [10, 2, 3] -> window: 1, binary search: 1
target = 1000000000, nums = 100000 copies of 10000 -> window: 100000, binary search: 100000
target = 1000000000, nums = 100000 copies of 1 -> window: 0, binary search: 0
```

The last two lines use the largest input the constraints allow. In the first, the whole array sums to exactly 10^9, the largest allowed target, so the answer is the full length; this shows that the `int` sums do not overflow. In the second, the array sums to only 10^5, so no subarray works.

Runs on Java 8, 11, 17, 21 and 25.

## 11. Complexity Analysis

**Sliding window:**

- **Time:** O(n). `right` moves forward `n` times. `left` also only moves forward and can never pass `right + 1`, so the inner loop runs at most `n` times in total over the whole run, not `n` times per step. Each move does a constant amount of work, so the total is at most `2n` steps.
- **Auxiliary space:** O(1): `left`, `windowSum` and `shortest`.

**Prefix sums and binary search:**

- **Time:** O(n log n): O(n) to build `prefix` and one O(log n) search for each of the `n` start indexes.
- **Auxiliary space:** O(n) for the `prefix` array of length `n + 1`.

## 12. Edge Cases

- **No subarray reaches `target`,** such as Example 3. `shortest` is never updated, and the method returns 0 instead of `Integer.MAX_VALUE`.
- **The whole array is needed,** such as `target = 15` with `[1, 2, 3, 4, 5]`. The window only becomes valid at the last index and the answer is `n`.
- **A single element reaches `target`,** such as `target = 6` with `[10, 2, 3]`. The window is valid at `right = 0` with length 1, which is the smallest possible answer.
- **An array of length 1.** Either that element reaches `target` and the answer is 1, or the answer is 0.
- **The largest allowed sum,** 10^5 copies of 10^4 with `target = 10^9`. Every sum stays at most 10^9, which fits in an `int`.

## 13. Common Mistakes

- **Using `if` instead of `while` to shrink.** One element may not be enough to remove. In the dry run at `right = 5`, the window shrinks twice, from length 4 down to 2. With `if`, it would shrink only once per step and miss `[4, 3]`.
- **Recording the length after removing `nums[left]`.** The length must be recorded while the window still reaches `target`, before the element is removed. Otherwise the recorded window is one element too short and may not be valid.
- **Returning `Integer.MAX_VALUE` when there is no answer.** The problem asks for 0. Check for the sentinel before returning.
- **Using the window with negative values.** For `[1, -1, 5]` with `target = 5`, the window returns 3 instead of 1. Check the sign of the values before choosing this pattern.
- **Ignoring overflow in the binary search target.** With these limits `prefix[start] + target` stays below 2 × 10^9 and fits in an `int`, but only just. If the limits were a little larger, an `int` sum could wrap around to a negative number, every `prefix[end]` would look large enough, and the search would wrongly return length 1. Computing it as a `long` removes the risk.

## 14. Interview Follow-ups

- **Return the subarray itself.** Store `left` and `right` whenever `shortest` improves. The cost stays O(n) time and O(1) space.
- **The values can be negative** ([LeetCode 862](https://leetcode.com/problems/shortest-subarray-with-sum-at-least-k/), Hard). The window no longer works, and binary search fails too because `prefix` is no longer increasing. A common approach keeps a monotonic deque of start indexes with increasing prefix sums.
- **The sum must equal `target` exactly, and you count the subarrays** ([LeetCode 560](https://leetcode.com/problems/subarray-sum-equals-k/)). Use prefix sums with a hash map of earlier prefix sums, as in the [Prefix Sum](../../../../Patterns/PrefixSum.md) guide. This works with negative values.
- **The longest subarray with sum at most `target`,** with positive values. The same window works, but the roles swap: record the length after shrinking until the sum is at most `target`.
- **Count the subarrays with product less than `k`** ([LeetCode 713](https://leetcode.com/problems/subarray-product-less-than-k/)). Positive values make the product monotonic, so a shrinking window works again.

## 15. Revision Summary

Because every value is positive, adding on the right raises the sum and removing on the left lowers it, so a variable-size [Sliding Window](../../../../Patterns/SlidingWindow.md) can grow until the sum reaches `target`, then shrink with a `while` loop, recording the length before each removal. `left` only moves forward, giving O(n) time and O(1) auxiliary space. The follow-up uses [Prefix Sum](../../../../Patterns/PrefixSum.md) with binary search in O(n log n) time and O(n) space. With negative values, neither works as written, so look for a monotonic deque or a prefix sum map instead.
