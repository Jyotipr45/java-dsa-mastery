# Valid Anagram

**Source:** [LeetCode 242](https://leetcode.com/problems/valid-anagram/) · **Difficulty:** Easy · **Topic:** [String](../../README.md), [Hashing](../../../03-Hashing/README.md) · **Patterns:** [Hashing](../../../../Patterns/Hashing.md) (frequency count)

## 1. Problem Statement

You are given two strings `s` and `t`. Return `true` if `t` is an anagram of `s`, and `false` otherwise. An anagram uses exactly the same letters as the original, each one the same number of times, but possibly in a different order. For example, `"silent"` is an anagram of `"listen"`.

**Constraints:**

- `1 <= s.length, t.length <= 5 * 10^4`
- `s` and `t` contain only lowercase English letters.

## 2. Examples

**Example 1**

```
Input:  s = "listen", t = "silent"
Output: true
```

Both words contain one each of `e`, `i`, `l`, `n`, `s` and `t`.

**Example 2**

```
Input:  s = "rat", t = "car"
Output: false
```

`t` has a `c` that `s` does not have, and `s` has a `t` that `t` does not have.

**Example 3**

```
Input:  s = "aab", t = "abb"
Output: false
```

Both strings use the letters `a` and `b`, but `s` has two `a`s and `t` has only one. Having the same set of letters is not enough; the counts must match too.

## 3. Pattern Recognition

**What is the problem asking?** Do the two strings contain the same multiset of characters? Order does not matter, but how many times each character appears does.

**What clues are present?**

- The word "anagram", or any wording like "rearrange", "permutation of" or "same characters", means the order of the characters is irrelevant.
- Only the number of times each character appears decides the answer, so the question becomes "do the two strings have the same character counts?"
- The alphabet is small and fixed: 26 lowercase letters.

**Which patterns should I consider?** Matching each character of `s` with an unused character of `t` by searching, sorting both strings and comparing them, and counting characters with the frequency count form of the [Hashing](../../../../Patterns/Hashing.md) pattern, either in a `HashMap<Character, Integer>` or in an `int[26]` array.

**Why does the chosen pattern fit?** Two strings are anagrams exactly when every character has the same count in both. A frequency count computes those counts directly in one pass, without caring about order. Because the characters are limited to `'a'` to `'z'`, the "hash function" can simply be `c - 'a'`, which maps each letter to its own slot in an array of 26 counters. That is a hash table with no collisions, no boxing and no resizing.

**Why are the alternatives less suitable?** Searching `t` for a match to every character of `s` takes O(n²) time. Sorting both strings works and is easy to get right, but it costs O(n log n) time and needs two `char[]` copies, because Java strings cannot be changed. A `HashMap` gives the same O(n) time as the array, but every count is a boxed `Integer`, so it is slower and uses more memory for no gain when only 26 keys are possible. The `HashMap` becomes the right choice when the characters are not limited to a small range (see the follow-ups).

**What assumptions must hold?** Every character is between `'a'` and `'z'`. If any other character appears, `c - 'a'` falls outside 0 to 25 and the array version throws `ArrayIndexOutOfBoundsException`. Upper and lower case are different characters, as in the problem.

**How do the constraints influence the choice?** With strings of up to 5 × 10^4 characters, an O(n²) search would do over 10^9 steps in the worst case, which is too slow. Sorting is fast enough, but the counting array is simpler and linear. A count never exceeds 5 × 10^4 in either direction, so `int` counters are more than enough.

## 4. Prerequisites

- [String](../../README.md): reading characters with `charAt`, and why sorting a string means copying it into a `char[]`.
- [Hashing](../../../03-Hashing/README.md): how a hash table maps a key to a slot, which the counting array does with `c - 'a'`.
- [Hashing pattern](../../../../Patterns/Hashing.md): the frequency count form, and when an array of counters can replace a map.

## 5. Intuition

Think of each string as a bag of letter tiles, like in a word game. Two words are anagrams if their bags hold exactly the same tiles. The order you pull the tiles out in does not matter.

To compare two bags, you do not have to line the tiles up. Keep a tally sheet with one row per letter. For every tile from the first bag, add one to its row. For every tile from the second bag, take one away. If the bags were the same, every row ends at zero. If any row is positive, the first bag had more of that letter; if it is negative, the second bag had more.

With only 26 letters, the tally sheet is just an array of 26 integers.

## 6. Brute-Force Approach

For each character of `s`, find an unused matching character in `t` and mark it as used.

```
if length(s) != length(t): return false
used = boolean array of length(t), all false
for each character c in s:
    found = false
    for j from 0 to length(t) - 1:
        if not used[j] and t[j] == c:
            used[j] = true
            found = true
            break
    if not found: return false
return true
```

This is correct, because every character of `s` consumes a different character of `t`, and the lengths are equal, so nothing in `t` is left over. But each search can scan all of `t`, so it takes O(n²) time and O(n) extra space for the `used` array. For 5 × 10^4 characters that is too slow.

## 7. Improved Approach

Sort the characters of both strings and compare the results. Sorting puts equal characters next to each other in a fixed order, so two anagrams become the same sequence.

```
if length(s) != length(t): return false
a = sorted characters of s
b = sorted characters of t
return a == b
```

In Java this means `s.toCharArray()`, `Arrays.sort`, and `Arrays.equals`, which is the version shown in the [String](../../README.md) guide. It takes O(n log n) time and O(n) extra space for the two arrays. It is short and works for any characters, not only lowercase letters, which makes it a good first answer in an interview. It is slower than counting because sorting does more work than the problem needs: we only need how many of each letter there are, not their order.

## 8. Preferred Approach

**Core idea:** Keep an array of 26 counts. Add one for each letter of `s` and subtract one for each letter of `t`. The strings are anagrams exactly when every count ends at zero.

**Invariant:** After processing the first `i` positions, `counts[c - 'a']` equals the number of times `c` appears in `s[0..i - 1]` minus the number of times it appears in `t[0..i - 1]`, for every letter `c`.

**Why it is correct:** The invariant holds at the start, when both prefixes are empty and every count is zero, and each step adds one for `s.charAt(i)` and subtracts one for `t.charAt(i)`, so it still holds. When the loop ends, the prefixes are the whole strings, so each count is the difference between that letter's count in `s` and in `t`. All differences are zero exactly when every letter appears equally often, which is the definition of an anagram. The early length check is needed because the single loop walks both strings with the same index; it is also a correct answer on its own, since strings of different lengths cannot have equal counts.

**Steps:**

1. If the lengths differ, return `false`.
2. Create `int[] counts = new int[26]`.
3. For each index `i`, increase `counts[s.charAt(i) - 'a']` and decrease `counts[t.charAt(i) - 'a']`.
4. If any count is not zero, return `false`. Otherwise return `true`.

**Trade-offs:** The array works only because the alphabet is small and known. For arbitrary Unicode text it would need over a million slots, and a `HashMap` is the better tool. The loop cannot stop early, because a count that is not zero halfway through can still return to zero later; for example, after the first character of `"ab"` and `"ba"`, the `a` count is 1. A two-loop version, which counts all of `s` first and then decrements for `t`, can return `false` as soon as a count goes below zero, which the [Hashing pattern](../../../../Patterns/Hashing.md) guide's example does. Both are O(n).

## 9. Dry Run

**Input:** `s = "listen"`, `t = "silent"`

The lengths are both 6, so the loop runs. Only letters with a count that is not zero are shown.

| `i` | `s.charAt(i)` | `t.charAt(i)` | Counts after this step |
|---|---|---|---|
| 0 | `l` | `s` | `l: 1, s: -1` |
| 1 | `i` | `i` | `l: 1, s: -1` |
| 2 | `s` | `l` | all zero |
| 3 | `t` | `e` | `e: -1, t: 1` |
| 4 | `e` | `n` | `n: -1, t: 1` |
| 5 | `n` | `t` | all zero |

Every count is zero, so the method returns `true`.

At `i = 0`, `t` uses an `s` before `s` does, so the `s` count goes to -1 for a while. That is not a mismatch yet: `s` reaches its own `s` at `i = 2` and the count returns to zero. This is why the method checks the counts only after the loop.

For `s = "aab"` and `t = "abb"`, the steps are `a: 0` (both `a`), then `a: 1, b: -1`, then `a: 1, b: -1` again (both `b`). The `a` count is 1 and the `b` count is -1 at the end, so the method returns `false`.

## 10. Java Solution

See [Solution.java](Solution.java). To run it:

```bash
javac Solution.java
java Solution
```

Expected output:

```
s = "listen", t = "silent" -> true
s = "rat", t = "car" -> false
s = "aab", t = "abb" -> false
s = "a", t = "a" -> true
s = "ab", t = "abc" -> false
s = "anagram", t = "nagaram" -> true
s = "zzzz", t = "zzzz" -> true
```

The fifth case has different lengths and is rejected before any counting. The last case uses `z`, the last slot of the array, to show that `c - 'a'` stays within 0 to 25.

Runs on Java 8, 11, 17, 21 and 25.

## 11. Complexity Analysis

- **Time:** O(n), where n is the length of the strings. The loop does constant work for each of the n positions, and the final check reads 26 counts, which is constant. If the lengths differ, the method returns in O(1).
- **Auxiliary space:** O(1), because the array always has 26 counts, however long the strings are. It would be O(k) for an alphabet of k characters.

## 12. Edge Cases

- **Different lengths,** such as `"ab"` and `"abc"`. Returned as `false` immediately. Without this check, the loop would read past the end of the shorter string.
- **Single characters,** such as `"a"` and `"a"`. One step, one count back to zero.
- **Same letters, different counts,** such as `"aab"` and `"abb"`. Comparing only which letters appear would wrongly say `true`; comparing counts says `false`.
- **Identical strings,** such as `"zzzz"` and `"zzzz"`. A string is an anagram of itself. Each step adds and removes one `z`.
- **A count that goes negative before it recovers,** as in the dry run. The final check, not the loop, decides the answer.
- **Characters outside `'a'` to `'z'`.** The problem rules them out. With an uppercase letter or a digit, `c - 'a'` is outside the array and the method throws. Use a map, or a larger array, for wider input.

## 13. Common Mistakes

- **Comparing sets instead of counts.** Putting the characters into two `HashSet`s and comparing them says `"aab"` and `"abb"` are anagrams. Every character's count matters.
- **Forgetting the length check.** In the single-loop version, `t.charAt(i)` throws when `t` is shorter, and when `t` is longer its extra characters are never counted.
- **Returning `false` as soon as a count becomes non-zero inside the loop.** In the single-loop version, counts move up and down before they settle, as the dry run shows. Only the two-loop version can stop when a count goes below zero.
- **Comparing sorted arrays with `==`.** In Java, `a == b` on two `char[]` arrays compares references and is always `false` for two different arrays. Use `Arrays.equals(a, b)`, or `new String(a).equals(new String(b))`.
- **Using `s.equals(t)` after sorting only one string.** Both strings must be sorted the same way before they are compared.
- **Assuming the array works for any text.** `c - 'a'` is only a valid index for lowercase English letters. For anything wider, use `counts.merge(c, 1, Integer::sum)` on a `HashMap`.

## 14. Interview Follow-ups

- **The strings can contain any Unicode characters.** Count with a `HashMap<Integer, Integer>` keyed by code point, using `s.codePoints()`, so that characters outside the Basic Multilingual Plane, such as most emoji, are counted as one character instead of two `char` halves. The time is still O(n) on average, and the extra space grows to O(k) for k distinct characters. Counting or sorting `char` values is not enough here. The strings `"😀🤀"` (U+1F600 and U+1F900) and `"🔀🨀"` (U+1F500 and U+1FA00) contain the same four `char` halves, so their sorted `char[]` arrays are equal, but they are made of four different characters. Sorting the code points instead, with `s.codePoints().sorted().toArray()`, gives the right answer.
- **Group a list of words into sets of anagrams** ([LeetCode 49](https://leetcode.com/problems/group-anagrams/)). Give each word a key that is the same for all its anagrams, such as its sorted letters or its 26 counts written as a string, and group the words in a map from key to list. The [Hashing pattern](../../../../Patterns/Hashing.md) guide shows the sorted-letters version.
- **Can one string be built from the letters of another?** ([LeetCode 383](https://leetcode.com/problems/ransom-note/), Ransom Note). Count the letters of the magazine, then subtract the letters of the note and return `false` if any count goes below zero. Unused letters may be left over, so the lengths do not have to match and the counts do not have to end at zero.
- **Find every place where an anagram of `p` appears in `s`** ([LeetCode 438](https://leetcode.com/problems/find-all-anagrams-in-a-string/)). Keep the 26 counts for a window of `p.length()` characters and slide it along `s`, adding the character that enters and removing the one that leaves. This combines frequency counting with a fixed-size sliding window.

## 15. Revision Summary

Two strings are anagrams exactly when every character has the same count in both. With only lowercase letters, an `int[26]` array indexed by `c - 'a'` is a collision-free hash table: add one for each letter of `s`, subtract one for each letter of `t`, and check that every count is zero. Check the lengths first, and do not judge the counts until the loop ends. This is the frequency count form of the [Hashing](../../../../Patterns/Hashing.md) pattern and takes O(n) time and O(1) auxiliary space; sorting both strings is a simpler O(n log n) alternative, and a `HashMap` replaces the array when the characters are not limited to a small range.
