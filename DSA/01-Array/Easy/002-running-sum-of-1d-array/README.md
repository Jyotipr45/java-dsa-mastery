# Running Sum of 1d Array

**Source:** [LeetCode 1480](https://leetcode.com/problems/running-sum-of-1d-array/) · **Difficulty:** Easy · **Topic:** [Array](../../README.md) · **Patterns:** Prefix Sum (see the [patterns index](../../../../Patterns/README.md))

## 1. Problem Statement

You are given an integer array `nums`. Return an array of the same length where the value at position `i` is the sum of every element from `nums[0]` up to and including `nums[i]`.

**Constraints:**

- `1 <= nums.length <= 1000`
- `-10^6 <= nums[i] <= 10^6`

## 2. Examples

**Example 1**

```
Input:  nums = [1, 2, 3, 4]
Output: [1, 3, 6, 10]
```

The answer is `[1, 1 + 2, 1 + 2 + 3, 1 + 2 + 3 + 4]`.

**Example 2**

```
Input:  nums = [3, 1, 2, 10, 1]
Output: [3, 4, 6, 16, 17]
```

Each value is the previous value plus the next number: 3, then 3 + 1, then 4 + 2, then 6 + 10, then 16 + 1.

## 3. Pattern Recognition

**What is the problem asking?** For every index `i`, find the sum of the prefix `nums[0..i]`.

**What clues are present?**

- Every answer is a sum that starts at index 0. Only the end of the range moves.
- Neighboring answers overlap almost completely. The sum up to `i` is the sum up to `i - 1` plus one more number.
- The output has one value per index, so we have to produce `n` numbers no matter what.

**Which patterns should I consider?** Adding each prefix from scratch, and building each sum from the one before it, which is the prefix sum idea.

**Why does the chosen pattern fit?** Because the answer at `i` contains the answer at `i - 1`, we can carry one running total from left to right and never add the same element twice. The array this produces is exactly the prefix sum array that the Prefix Sum pattern uses to answer range-sum questions, so this problem is the first step of that pattern.

**Why are the alternatives less suitable?** Adding each prefix from scratch repeats work: `nums[0]` is added `n` times, `nums[1]` is added `n - 1` times, and so on, which is O(n²) in total. Sorting or hashing do not help, because the order of the elements defines every prefix.

**What assumptions must hold?** The sums are taken over prefixes in the original order. Negative numbers are fine, because we only add. The sums must fit in the chosen number type.

**How do the constraints influence the choice?** With at most 1000 elements, even the O(n²) version would finish quickly, so the constraints do not force the faster approach here. It is still the one to learn, because the same idea is what makes range-sum problems with 10^5 elements solvable. The constraints also settle the number type: the largest possible sum in absolute value is 1000 × 10^6 = 10^9, which is below the `int` limit of about 2.1 × 10^9.

## 4. Prerequisites

- [Array](../../README.md): indexing and scanning an array once from left to right.
- [Complexity analysis in the roadmap](../../../../ROADMAP.md): why reusing earlier work turns O(n²) into O(n).

## 5. Intuition

Think of a bank balance. After each deposit or withdrawal you do not add up the whole history again. You take the previous balance and apply the newest change. The running sum is that list of balances.

So keep one variable, `total`, that holds the sum of everything seen so far. At each index, add the current number to `total` and write `total` into the answer.

## 6. Brute-Force Approach

For each index `i`, add up `nums[0]` to `nums[i]` with an inner loop.

```
for i from 0 to n - 1:
    sum = 0
    for j from 0 to i:
        sum = sum + nums[j]
    result[i] = sum
return result
```

This is correct and uses no extra space beyond the answer, but it does 1 + 2 + ... + n = n(n + 1) / 2 additions, so it takes O(n²) time. Every prefix is recomputed even though the previous prefix was just calculated.

## 7. Preferred Approach

**Core idea:** The sum up to `i` equals the sum up to `i - 1` plus `nums[i]`, so carry the sum forward instead of recomputing it.

**Invariant:** At the start of the loop for index `i`, `total` equals `nums[0] + ... + nums[i - 1]`, which is `0` when `i` is `0`.

**Why it is correct:** The invariant holds before the first step, because the empty prefix has sum 0. Each step adds `nums[i]` to `total`, so `total` becomes the sum of `nums[0..i]`, which is exactly the value that belongs in `result[i]`, and the invariant now holds for `i + 1`. When the loop ends, every position has been filled with its prefix sum.

**Steps:**

1. Create `result` with the same length as `nums`, and set `total` to `0`.
2. For each index `i` from left to right:
   1. Add `nums[i]` to `total`.
   2. Store `total` in `result[i]`.
3. Return `result`.

Starting `total` at `0` and looping from index 0 means the first element needs no special case.

**Trade-offs:** This solution returns a new array and leaves the input unchanged. If changing the input is allowed, you can write the sums back into `nums` itself (`nums[i] += nums[i - 1]` for `i` from 1), which saves the O(n) output array. Java 8 also has `Arrays.parallelPrefix(nums, Integer::sum)`, which does the same in-place update in one call. It is handy in real code, but in an interview you should be able to write the loop.

## 8. Dry Run

**Input:** `nums = [3, 1, 2, 10, 1]`

Before the loop, `total = 0`.

| `i` | `nums[i]` | `total` before | `total` after | `result` after this step |
|---|---|---|---|---|
| 0 | 3 | 0 | 3 | [3, 0, 0, 0, 0] |
| 1 | 1 | 3 | 4 | [3, 4, 0, 0, 0] |
| 2 | 2 | 4 | 6 | [3, 4, 6, 0, 0] |
| 3 | 10 | 6 | 16 | [3, 4, 6, 16, 0] |
| 4 | 1 | 16 | 17 | [3, 4, 6, 16, 17] |

The result is `[3, 4, 6, 16, 17]`.

Each row does one addition. At `i = 3`, the brute force would add 3 + 1 + 2 + 10 from scratch, while here `total` already holds 6, the sum of the first three numbers, so only 10 is added. The unfilled positions show `0` because Java initializes a new `int` array with zeros.

## 9. Java Solution

See [Solution.java](Solution.java). To run it:

```bash
javac Solution.java
java Solution
```

Expected output:

```
nums = [1, 2, 3, 4] -> running sum = [1, 3, 6, 10]
nums = [1, 1, 1, 1, 1] -> running sum = [1, 2, 3, 4, 5]
nums = [3, 1, 2, 10, 1] -> running sum = [3, 4, 6, 16, 17]
nums = [5] -> running sum = [5]
nums = [-2, 5, -1, -3] -> running sum = [-2, 3, 2, -1]
1000 copies of 1000000 -> last running sum = 1000000000
```

The last line checks the largest sum the constraints allow, to show it fits in an `int`.

Runs on Java 8, 11, 17, 21 and 25.

## 10. Complexity Analysis

- **Time:** O(n), because the loop visits each element once and does one addition and one assignment for it.
- **Auxiliary space:** O(1), because apart from the output array, which the problem requires, only the single variable `total` is used. Counting the output, the space is O(n).

## 11. Edge Cases

- **One element,** such as `[5]`. The answer is the same single value.
- **Negative numbers,** such as `[-2, 5, -1, -3]`. The running sum can go down as well as up, and can end below zero. Nothing special is needed, because the algorithm only adds.
- **The largest sums.** 1000 values of 10^6 give 10^9, which fits in an `int`. If the constraints allowed more or larger values, `total` and the result would need to be `long`.
- **An empty array.** The constraints rule it out, but the method returns an empty array instead of failing.

## 12. Common Mistakes

- **Recomputing each prefix with an inner loop.** It gives the right answer, but misses the point of the problem and does not scale to the range-sum problems that build on it.
- **Starting the loop at index 0 with `nums[i] += nums[i - 1]`.** The in-place version must start at index 1, otherwise it reads `nums[-1]` and throws an `ArrayIndexOutOfBoundsException`.
- **Writing into `nums` when the caller still needs the original values.** The in-place version destroys the input. Use a separate result array unless changing the input is allowed.
- **Ignoring overflow.** Here the bounds make `int` safe, but that should be a checked conclusion, not an assumption. Prefix sums over large inputs are a common source of overflow bugs.

## 13. Interview Follow-ups

- **Do it in place.** Loop from index 1 and set `nums[i] += nums[i - 1]`. The auxiliary space stays O(1) and the output array is no longer needed.
- **Answer many range-sum queries** ([LeetCode 303](https://leetcode.com/problems/range-sum-query-immutable/)). Build a prefix array with one extra leading `0`, so the sum of `nums[left..right]` is `prefix[right + 1] - prefix[left]`, and each query takes O(1).
- **Find the index where the left sum equals the right sum** ([LeetCode 724](https://leetcode.com/problems/find-pivot-index/)). With the total sum known, the right sum at `i` is `total - leftSum - nums[i]`, so one running sum is enough.
- **Count subarrays whose sum is `k`** ([LeetCode 560](https://leetcode.com/problems/subarray-sum-equals-k/)). A subarray sum is the difference of two prefix sums, so store how often each prefix sum has appeared in a hash map.
- **The values arrive as a stream.** The same single variable works, since each new running sum depends only on the previous one and the new value.

## 14. Revision Summary

The sum up to index `i` is the sum up to `i - 1` plus `nums[i]`, so one running total gives every prefix sum in a single pass. This is the prefix sum array, the building block of the Prefix Sum pattern for range sums and subarray counting. Start the total at 0, check that the largest sum fits the number type, and decide whether the input may be overwritten. It takes O(n) time and O(1) auxiliary space besides the output.
