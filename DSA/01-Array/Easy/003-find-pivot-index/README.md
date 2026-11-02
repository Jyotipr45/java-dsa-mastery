# Find Pivot Index

**Source:** [LeetCode 724](https://leetcode.com/problems/find-pivot-index/) · **Difficulty:** Easy · **Topic:** [Array](../../README.md) · **Patterns:** Prefix Sum (see the [patterns index](../../../../Patterns/README.md))

## 1. Problem Statement

You are given an integer array `nums`. A pivot index is an index `i` where the sum of all elements strictly to the left of `i` equals the sum of all elements strictly to the right of `i`. The element at `i` itself is counted on neither side. If `i` is the first index, its left sum is 0, and if it is the last index, its right sum is 0.

Return the leftmost pivot index, or `-1` if there is none.

**Constraints:**

- `1 <= nums.length <= 10^4`
- `-1000 <= nums[i] <= 1000`

## 2. Examples

**Example 1**

```
Input:  nums = [2, 3, -1, 8, 4]
Output: 3
```

At index 3, the left side is 2 + 3 + (-1) = 4 and the right side is 4. No earlier index balances.

**Example 2**

```
Input:  nums = [1, 2, 3]
Output: -1
```

The left and right sums are 0 and 5 at index 0, 1 and 3 at index 1, and 3 and 0 at index 2. None are equal.

**Example 3**

```
Input:  nums = [5, 4, -4]
Output: 0
```

At index 0 nothing is on the left, so the left sum is 0. The right side is 4 + (-4) = 0.

## 3. Pattern Recognition

**What is the problem asking?** For each index, compare the sum of the prefix before it with the sum of the suffix after it, and return the first index where they are equal.

**What clues are present?**

- Every question is about the sum of a contiguous range, and every range touches one end of the array: the left side starts at index 0 and the right side ends at the last index.
- Neighboring indexes share almost all of their left side. Moving from `i` to `i + 1` adds exactly one element to the left.
- The whole array is fixed in advance, so its total sum can be computed once.

**Which patterns should I consider?** Adding both sides from scratch at every index, storing prefix and suffix sum arrays, and a single running prefix sum combined with the total, which is the Prefix Sum pattern.

**Why does the chosen pattern fit?** The left sum at `i` is a prefix sum, so it can be carried forward with one running total, as in Running Sum of 1d Array (Array, Easy 002). The right sum does not need its own running total, because the left side, the element at `i` and the right side together make up the whole array: `rightSum = total - leftSum - nums[i]`. One number computed in advance turns every right-side question into a subtraction.

**Why are the alternatives less suitable?** Adding both sides from scratch visits about `n` elements for every index, which is O(n²). Separate prefix and suffix arrays give O(n) time, but use O(n) extra memory to store values that the total already lets us derive. Two pointers moving inward do not work here: the values can be negative, so moving a pointer can make a side's sum go down as well as up, and there is no rule for which pointer to move. Sorting or hashing do not help either, because the order of the elements defines each side.

**What assumptions must hold?** Sums are taken in the original order, and the element at the pivot is excluded from both sides. Negative values and zeros are allowed, so more than one index can balance, and the scan must go from the left to return the leftmost one. The sums must fit in the chosen number type.

**How do the constraints influence the choice?** With up to 10^4 elements, the O(n²) version does about 10^8 additions in the worst case. That is slow enough to matter, so the O(n) approach is the right one. The constraints also settle the number type: the largest possible sum in absolute value is 10^4 × 1000 = 10^7, well below the `int` limit of about 2.1 × 10^9.

## 4. Prerequisites

- [Array](../../README.md): scanning an array from left to right with an index.
- Running Sum of 1d Array (Array, Easy 002): building prefix sums with one running total. This problem uses the same running total for the left side.

## 5. Intuition

Picture the array as weights on a seesaw, and each index as a place to put the pivot. To check a position you need the weight on each side.

The left weight is easy to keep up to date: every time the pivot moves one step right, one element moves from "the pivot" to "the left side". The right weight looks harder, but there is a shortcut. If you know the total weight of everything, the right side is simply what is left after taking away the left side and the element under the pivot.

So compute the total once, then walk from left to right keeping only the left sum.

## 6. Brute-Force Approach

For each index `i`, add up everything before it and everything after it, and compare.

```
for i from 0 to n - 1:
    leftSum = sum of nums[0 .. i - 1]
    rightSum = sum of nums[i + 1 .. n - 1]
    if leftSum == rightSum:
        return i
return -1
```

This is correct and uses O(1) extra space, but every index costs about `n` additions, so the total is O(n²) time. Each left sum repeats the work of the one before it, and so does each right sum.

## 7. Preferred Approach

**Core idea:** Compute the total sum once. While scanning from the left, keep the left sum as a running total and derive the right sum as `total - leftSum - nums[i]`.

**Invariant:** At the start of the loop for index `i`, `leftSum` equals `nums[0] + ... + nums[i - 1]`, which is `0` when `i` is `0`.

**Why it is correct:** The invariant holds before the first step, because the empty prefix has sum 0. At index `i`, the array splits into three disjoint parts, `nums[0..i - 1]`, `nums[i]` and `nums[i + 1..n - 1]`, whose sums add up to `total`. So `total - leftSum - nums[i]` is exactly the right sum, and the comparison is the definition of a pivot index. Adding `nums[i]` to `leftSum` before moving on keeps the invariant for `i + 1`. Because indexes are checked in increasing order and the method returns at the first match, the index it returns is the leftmost pivot. If the loop finishes, every index was checked and none balanced, so `-1` is correct.

**Steps:**

1. Add up every element into `total`.
2. Set `leftSum` to `0`.
3. For each index `i` from left to right:
   1. Compute `rightSum = total - leftSum - nums[i]`.
   2. If `leftSum == rightSum`, return `i`.
   3. Add `nums[i]` to `leftSum`.
4. Return `-1`.

The order inside the loop matters: compare first, then add `nums[i]` to the left side, because the element at the pivot belongs to neither side.

**Trade-offs:** The array is read twice, once for the total and once for the scan, which is still O(n). If the input arrives as a stream that can be read only once, this approach does not apply directly, because the total is not known in advance. Storing a full prefix sum array is useful when you need to answer many different range-sum questions about the same array, but for this single question the running total is enough.

## 8. Dry Run

**Input:** `nums = [2, 3, -1, 8, 4]`

The first pass gives `total = 2 + 3 + (-1) + 8 + 4 = 16`.

| `i` | `nums[i]` | `leftSum` | `rightSum = 16 - leftSum - nums[i]` | Equal? | `leftSum` after |
|---|---|---|---|---|---|
| 0 | 2 | 0 | 16 - 0 - 2 = 14 | No | 2 |
| 1 | 3 | 2 | 16 - 2 - 3 = 11 | No | 5 |
| 2 | -1 | 5 | 16 - 5 - (-1) = 12 | No | 4 |
| 3 | 8 | 4 | 16 - 4 - 8 = 4 | Yes, return 3 | |

The method returns `3`.

At `i = 2` the left sum goes down from 5 to 4 after adding `-1`, and the right sum at that step is larger than at `i = 1` even though the right side lost an element. Sums that move in both directions are why two pointers cannot decide which way to move here, while subtracting from the total still gives the right answer. At `i = 3` the method returns before adding 8, so index 4 is never checked.

## 9. Java Solution

See [Solution.java](Solution.java). To run it:

```bash
javac Solution.java
java Solution
```

Expected output:

```
nums = [2, 3, -1, 8, 4] -> pivot index = 3
nums = [1, 2, 3] -> pivot index = -1
nums = [5, 4, -4] -> pivot index = 0
nums = [1, -1, 0] -> pivot index = 2
nums = [0, 0, 0] -> pivot index = 0
nums = [7] -> pivot index = 0
nums = [-1, -1, -1, -1, 0, 1] -> pivot index = 1
```

Runs on Java 8, 11, 17, 21 and 25.

## 10. Complexity Analysis

- **Time:** O(n), because the first loop visits each element once to build `total`, and the second loop visits each element at most once, doing a constant amount of work for it. Two passes of O(n) are still O(n).
- **Auxiliary space:** O(1), because only `total`, `leftSum`, `rightSum` and the loop index are stored, however long the array is.

## 11. Edge Cases

- **One element,** such as `[7]`. Both sides are empty, so both sums are 0 and the answer is 0.
- **Pivot at the first index,** such as `[5, 4, -4]`. The left sum is 0 by definition, so index 0 is a pivot when the rest of the array sums to 0.
- **Pivot at the last index,** such as `[1, -1, 0]`. The right sum is 0 by definition, so index 2 is a pivot when everything before it sums to 0.
- **Several pivots,** such as `[0, 0, 0]`, where every index balances. The scan returns the first one, 0.
- **No pivot,** such as `[1, 2, 3]`. The loop finishes and the method returns `-1`.
- **Negative values,** such as `[-1, -1, -1, -1, 0, 1]`. Both sides are -1 at index 1. Nothing special is needed, because the method only adds and subtracts.

## 12. Common Mistakes

- **Including `nums[i]` in one of the sides.** Adding `nums[i]` to `leftSum` before the comparison, or computing the right sum as `total - leftSum`, counts the pivot element on one side. Compare first, then add.
- **Returning the last pivot instead of the first.** If the loop keeps going after a match and stores the index, it returns the rightmost pivot. Return as soon as a match is found.
- **Assuming a pivot must be in the middle.** Index 0 and the last index are valid answers when the other side sums to 0. Starting the loop at 1 or stopping before the last index misses them.
- **Using two pointers that move inward.** This only works when all values are non-negative, and even then it is easy to get wrong. With negative values allowed it fails, as the dry run shows.
- **Checking `2 * leftSum == total` instead.** That test also counts `nums[i]` on the right. The correct shortcut is `2 * leftSum + nums[i] == total`.

## 13. Interview Follow-ups

- **Return every pivot index.** Keep scanning after a match and collect each matching index. The time and space are still O(n) and O(1) apart from the output list.
- **The same problem under another name** ([LeetCode 1991](https://leetcode.com/problems/find-the-middle-index-in-array/)). Find the Middle Index in Array asks for exactly this, so the same code solves it.
- **Return the left and right sum differences for every index** ([LeetCode 2574](https://leetcode.com/problems/left-and-right-sum-differences/)). Use the same running left sum and `total - leftSum - nums[i]` for the right, and record `|leftSum - rightSum|` at every index instead of stopping.
- **Combine the two sides with an operation other than addition** ([LeetCode 238](https://leetcode.com/problems/product-of-array-except-self/)). Product of Array Except Self cannot divide by the total, because the array may contain zeros, so it keeps a running product from the left and another from the right instead.
- **What if the sums could overflow?** With larger values or longer arrays, store `total`, `leftSum` and `rightSum` as `long`.

## 14. Revision Summary

The left side, the pivot element and the right side always add up to the total, so after one pass for the total, a single running left sum gives the right sum as `total - leftSum - nums[i]`. This is the Prefix Sum pattern with one running prefix instead of a stored array. Compare before adding `nums[i]` to the left side, scan from the left to get the leftmost pivot, and remember that index 0 and the last index can be pivots. It takes O(n) time and O(1) auxiliary space.
