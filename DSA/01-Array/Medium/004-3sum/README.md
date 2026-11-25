# 3Sum

**Source:** [LeetCode 15](https://leetcode.com/problems/3sum/) · **Difficulty:** Medium · **Topic:** [Array](../../README.md) · **Patterns:** [Two Pointers](../../../../Patterns/TwoPointers.md), compared with [Hashing](../../../../Patterns/Hashing.md)

## 1. Problem Statement

You are given an integer array `nums`. Return every triplet `[nums[i], nums[j], nums[k]]` where `i`, `j` and `k` are three different indexes and `nums[i] + nums[j] + nums[k] == 0`. The answer must not contain the same triplet twice. Two triplets count as the same when they hold the same three values, whatever indexes they came from. The triplets and the values inside them may be returned in any order.

**Constraints:**

- `3 <= nums.length <= 3000`
- `-10^5 <= nums[i] <= 10^5`

## 2. Examples

**Example 1**

```
Input:  nums = [-1, 0, 1, 2, -1, -4]
Output: [[-1, -1, 2], [-1, 0, 1]]
```

`-1 + 0 + 1` and `-1 + -1 + 2` are both zero. The triplet `[-1, 0, 1]` can be built with either of the two `-1` values, but it is listed once.

**Example 2**

```
Input:  nums = [0, 1, 1]
Output: []
```

The only triplet sums to 2.

**Example 3**

```
Input:  nums = [0, 0, 0]
Output: [[0, 0, 0]]
```

## 3. Pattern Recognition

**What is the problem asking?** Find every set of three values, from three different positions, that adds up to zero, and report each set of values once.

**What clues are present?**

- The condition is about a fixed number of elements whose sum hits a target. Fix one of them and the rest is the pair problem from [Two Sum](../../Easy/004-two-sum/README.md), with target `-nums[i]`.
- The answer is made of values, not indexes. Unlike Two Sum, nothing stops us from sorting.
- The answer must not repeat a triplet, and the input can hold many copies of the same value.
- `n` is at most 3000, so O(n²) (about 9 × 10^6 steps) is fine, and O(n³) (about 4.5 × 10^9 triples) is not.

**Which patterns should I consider?** Checking every triple; fixing one element and running a [Hashing](../../../../Patterns/Hashing.md) pair search on the rest; and sorting, fixing one element and running opposite-end [Two Pointers](../../../../Patterns/TwoPointers.md) on the rest.

**Why does the chosen pattern fit?** Once the array is sorted and `nums[i]` is fixed, the other two values are a pair in a sorted range that must sum to `-nums[i]`. That is exactly the case the Two Pointers guide describes: if the sum is too small, the left value cannot work with anything still in range, so `left` moves; if it is too large, `right` moves. Each step rules out one element for good, so each fixed `i` costs O(n). Sorting also solves the duplicate problem cheaply. Equal values end up next to each other, so a repeated triplet can be avoided by skipping a value that equals its neighbour, which is the duplicate rule in the guide's Limitations section. The output also comes out already in order, with no set needed.

**Why are the alternatives less suitable?** Checking every triple is O(n³), far too slow for `n = 3000`, and still needs a set to remove repeats. Fixing `i` and using a hash set for the pair is also O(n²) time, but it needs O(n) extra space per fixed element, and removing repeated triplets is awkward: you either sort every triplet and put it in a set of lists, or you sort the array anyway, at which point two pointers does the same job without the hash set. Hashing is the better tool in Two Sum because there the original indexes must be returned and sorting would lose them. Here only values are returned, so that reason is gone.

**What assumptions must hold?** The answer is values, so sorting a copy is allowed. Sorting is what makes the pointer moves safe and puts copies next to each other. If the problem asked for the original indexes, you would have to sort the indexes by value or go back to hashing.

**How do the constraints influence the choice?** `n <= 3000` makes O(n²) the target and rules out O(n³). The values are at most `10^5` in size, so any sum of three is between `-3 × 10^5` and `3 × 10^5`, and an `int` is safe.

## 4. Prerequisites

- [Array](../../README.md): scanning and sorting arrays.
- [Two Pointers](../../../../Patterns/TwoPointers.md): the opposite-ends form on sorted input, and skipping equal neighbours.
- [Two Sum](../../Easy/004-two-sum/README.md) (Array, Easy 004): the pair version. Its sorted two-pointer approach is the inner loop here.

## 5. Intuition

Three unknowns are hard to search at once, but one is easy to fix. If the first value is `a`, the other two must sum to `-a`, and that is a pair search. Sorting turns that pair search into a single sweep with two pointers.

The harder part is not reporting the same triplet twice. In a sorted array, every copy of a value sits next to the others. If the first value equals the previous first value, it would find exactly the same pairs again, so skip it. After recording a triplet, move both pointers past every copy of the values just used, because any pair that keeps one of those values and still sums to the target must also repeat the other one.

## 6. Brute-Force Approach

Try every triple of indexes, keep the ones that sum to zero, and remove repeats.

```
sort nums
unique = empty ordered set
for i from 0 to n - 1:
    for j from i + 1 to n - 1:
        for k from j + 1 to n - 1:
            if nums[i] + nums[j] + nums[k] == 0:
                add [nums[i], nums[j], nums[k]] to unique
return unique as a list
```

Sorting first puts every triplet's values in non-decreasing order, so `[-1, 0, 1]` and `[0, -1, 1]` cannot both appear. This checks `n(n - 1)(n - 2) / 6` triples, so the time is O(n³). The set can hold up to O(n²) triplets in the worst case. It is implemented as `threeSumEveryTriple` in [Solution.java](Solution.java), and the program uses it to check the small cases.

## 7. Improved Approach: Fix One Value, Hash the Pair

For each `i`, scan the elements after it with a hash set of values seen so far, as in Two Sum. When `-nums[i] - nums[j]` is already in the set, a triplet is found. This is O(n²) time with O(n) extra space for the set. It removes the innermost loop, but repeats still have to be filtered, by sorting each triplet and storing it in a set of lists, or by sorting the array and skipping repeated first values. Once the array is sorted, the hash set is no longer needed, which leads to the preferred approach.

## 8. Preferred Approach: Sort, Fix One Value, Two Pointers

**Core idea:** Sort a copy of the array. For each index `i`, find every pair in `[i + 1, n - 1]` that sums to `-nums[i]` with opposite-end pointers, and skip values that would repeat a triplet.

**Invariant:** For a fixed `i`, every pair that uses an index outside `[left, right]` (and inside `[i + 1, n - 1]`) is either already recorded or cannot sum to `-nums[i]`.

**Why it is correct:** If `nums[i] + nums[left] + nums[right] < 0`, then pairing `left` with any index below `right` gives an even smaller sum, because the array is sorted. So `left` cannot be in any answer with the remaining range and can be dropped. The case `> 0` is the same for `right`. When the sum is zero, the triplet is recorded. Any other pair using the value `nums[left]` would need the value `nums[right]` as its partner and would repeat the triplet, so both pointers move past every copy of their values. The outer loop also skips `i` when `nums[i] == nums[i - 1]`: the earlier copy had the same target and a range that included everything this one sees, so it already found every triplet this one could. Finally, if `nums[i] > 0`, every later value is at least as large, so no sum can be zero and the loop stops.

**Steps:**

1. Copy and sort the array.
2. For `i` from 0 to `n - 3`:
   1. If `nums[i] > 0`, stop.
   2. If `i > 0` and `nums[i] == nums[i - 1]`, skip this `i`.
   3. Set `left = i + 1` and `right = n - 1`. While `left < right`:
      - If the sum is less than 0, add 1 to `left`.
      - If it is greater than 0, subtract 1 from `right`.
      - Otherwise record the triplet, move both pointers inwards once, then keep moving `left` while it equals the value before it and `right` while it equals the value after it.
3. Return the recorded triplets.

**Trade-offs:** O(n²) time and, apart from the output and the sorted copy, O(1) space. The program copies the array so the caller's input is not changed. Sorting in place would avoid the copy, but would change the caller's array. The triplets come out in sorted order, which makes testing easy. It is very hard to do better than O(n²) for this problem in general, so this is the approach interviewers expect.

## 9. Dry Run

**Input:** `nums = [-1, 0, 1, 2, -1, -4]`, sorted to `[-4, -1, -1, 0, 1, 2]` (indexes 0 to 5).

| `i` | `nums[i]` | `left` | `right` | Sum | Action |
|---|---|---|---|---|---|
| 0 | -4 | 1 | 5 | -4 + -1 + 2 = -3 | too small, `left` to 2 |
| 0 | -4 | 2 | 5 | -4 + -1 + 2 = -3 | too small, `left` to 3 |
| 0 | -4 | 3 | 5 | -4 + 0 + 2 = -2 | too small, `left` to 4 |
| 0 | -4 | 4 | 5 | -4 + 1 + 2 = -1 | too small, `left` to 5, pointers meet |
| 1 | -1 | 2 | 5 | -1 + -1 + 2 = 0 | record `[-1, -1, 2]`, `left` to 3, `right` to 4 |
| 1 | -1 | 3 | 4 | -1 + 0 + 1 = 0 | record `[-1, 0, 1]`, `left` to 4, `right` to 3, pointers cross |
| 2 | -1 | | | | same value as `nums[1]`, skipped |
| 3 | 0 | 4 | 5 | 0 + 1 + 2 = 3 | too large, `right` to 4, pointers meet |

`i` stops at 3, because `i` only goes up to `n - 3`. The result is `[[-1, -1, 2], [-1, 0, 1]]`.

With `-4` fixed, the largest pair is `1 + 2 = 3`, so nothing can reach 4 and `left` walks to the end. With the first `-1` fixed, both triplets appear. After the first one, neither pointer lands on a copy (the value after index 2 is 0, and the value before index 5 is 1), so no extra skipping happens. The second `-1` at index 2 is skipped. Without that skip it would find `[-1, 0, 1]` again, which is exactly what the version without skips prints below.

## 10. Java Solution

See [Solution.java](Solution.java). `threeSum` is the preferred approach and `threeSumEveryTriple` is the brute force, used to check the small cases. `threeSumWithoutSkips` is the same scan with every duplicate skip removed, kept only to show what goes wrong. To run it:

```bash
javac Solution.java
java Solution
```

Expected output:

```
nums = [-1, 0, 1, 2, -1, -4] -> [[-1, -1, 2], [-1, 0, 1]], matches every triple: true
nums = [0, 1, 1] -> [], matches every triple: true
nums = [0, 0, 0] -> [[0, 0, 0]], matches every triple: true
nums = [0, 0, 0, 0] -> [[0, 0, 0]], matches every triple: true
nums = [-2, 0, 0, 2, 2] -> [[-2, 0, 2]], matches every triple: true
nums = [-4, -2, -2, -2, 0, 1, 2, 2, 2, 3, 3, 4, 4, 6, 6] -> [[-4, -2, 6], [-4, 0, 4], [-4, 1, 3], [-4, 2, 2], [-2, -2, 4], [-2, 0, 2]], matches every triple: true
nums = [1, 2, -2, -1] -> [], matches every triple: true
without skips, [-1, 0, 1, 2, -1, -4] -> [[-1, -1, 2], [-1, 0, 1], [-1, 0, 1]]
without skips, [-2, 0, 0, 2, 2] -> [[-2, 0, 2], [-2, 0, 2]]
nums = 3000 zeros -> [[0, 0, 0]]
```

The two "without skips" lines show both kinds of repeat. In the first, the repeated `[-1, 0, 1]` comes from the second `-1` being used as the first value. In the second, `-2` is fixed once, but after recording `[-2, 0, 2]` the pointers land on the other `0` and the other `2` and record it again. The last line is the largest input allowed, all zeros, where every one of the roughly 4.5 × 10^9 triples sums to zero but only one triplet is unique. The brute force is not run there.

Runs on Java 8, 11, 17, 21 and 25.

## 11. Complexity Analysis

- **Time:** O(n²). Sorting is O(n log n). For each `i`, the range `[i + 1, n - 1]` holds `n - i - 1` elements and every step moves at least one of them inwards, including the skip loops, so the inner loop is O(n). With up to `n - 2` values of `i`, the total is O(n²), which dominates the sort.
- **Auxiliary space:** O(n) for the sorted copy, plus whatever the sort uses (`Arrays.sort` on an `int[]` is a dual-pivot quicksort, O(log n) stack). Sorting the input in place would bring this down to the sort's own space. The output list is not counted. It can hold O(n²) triplets in the worst case, which bounds how small any approach that lists them all can be.

## 12. Edge Cases

- **No triplet,** such as `[0, 1, 1]` or `[1, 2, -2, -1]`. The method returns an empty list.
- **All zeros,** such as `[0, 0, 0]`, `[0, 0, 0, 0]` or 3000 zeros. The single triplet `[0, 0, 0]` is recorded once. After it, the skip loops move `left` up to `right`, and every later `i` is skipped as a repeat.
- **A value used twice in one triplet,** such as `[-1, -1, 2]` or `[-4, 2, 2]`. These are fine, because the two equal values come from different indexes.
- **Copies on both sides,** such as `[-2, 0, 0, 2, 2]`. Without the inner skips, the triplet would be recorded twice.
- **All positive or all negative values.** All positive stops at `i = 0`. All negative never reaches a zero sum and returns an empty list after O(n²) steps.
- **Large values.** The sum of three values stays within `±3 × 10^5`, so `int` is safe.

## 13. Common Mistakes

- **Leaving out a skip.** Without the outer skip, the second `-1` in Example 1 records `[-1, 0, 1]` again. Without the inner skips, `[-2, 0, 0, 2, 2]` records `[-2, 0, 2]` twice. Both are shown in the program's output.
- **Skipping too early.** Writing the outer skip as `nums[i] == nums[i + 1]` skips the first copy instead of the later ones, and loses triplets such as `[-1, -1, 2]` that need both copies. Compare with the value before, not after.
- **Dropping `left < right` from the skip loops.** On an input of all zeros, `left` would run past the end of the array and throw `ArrayIndexOutOfBoundsException`. The check also keeps the pointers from crossing inside the skip.
- **Using a set of triplets to hide repeats.** It works, but it adds O(n²) space in the worst case and hides a bug in the scan instead of fixing it.
- **Trying to keep the original indexes.** The problem asks for values. Sorting is what makes the pointer moves correct, so the original positions are not available afterwards and are not needed.

## 14. Interview Follow-ups

- **3Sum Closest** ([LeetCode 16](https://leetcode.com/problems/3sum-closest/), Medium). The same sort and two pointers, but track the sum closest to a target instead of recording zeros. No duplicate handling is needed, because only one number is returned.
- **4Sum** ([LeetCode 18](https://leetcode.com/problems/4sum/), Medium). Fix two values with two outer loops and run the same pair scan, skipping repeats at each level. That is O(n³). Values there are large enough that the sum of four needs a `long`.
- **Count the triplets instead of listing them.** Counting all index triples with sum zero, including repeats, needs a different inner step: when values repeat at both pointers, count how many copies there are and multiply.
- **What if the input is already sorted?** Skip the sort and the copy. The time is still O(n²).
- **Can it be faster than O(n²)?** Not by much in general. The best known algorithms are only slightly faster than O(n²), so O(n²) is the accepted answer in an interview.

## 15. Revision Summary

Sort a copy, fix the first value, and find the other two with opposite-end [Two Pointers](../../../../Patterns/TwoPointers.md) on the rest, which turns 3Sum into `n` runs of the sorted pair search from [Two Sum](../../Easy/004-two-sum/README.md). Sorting also puts equal values next to each other, so repeats are avoided by skipping a first value equal to the previous one and by moving both pointers past copies after each recorded triplet. It runs in O(n²) time with O(n) auxiliary space for the sorted copy, and with these constraints the sums fit in an `int`.
