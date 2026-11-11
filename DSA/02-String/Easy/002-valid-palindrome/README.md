# Valid Palindrome

**Source:** [LeetCode 125](https://leetcode.com/problems/valid-palindrome/) · **Difficulty:** Easy · **Topic:** [String](../../README.md) · **Patterns:** [Two Pointers](../../../../Patterns/TwoPointers.md) (opposite ends)

## 1. Problem Statement

You are given a string `s`. Clean it in two ways: change every uppercase letter to lowercase, and remove every character that is not a letter or a digit. Return `true` if the cleaned string reads the same from left to right as from right to left, and `false` otherwise.

Letters and digits together are called alphanumeric characters. Spaces, punctuation and symbols such as `_` are not alphanumeric, so they are ignored. Digits are kept, so `"0P"` cleans to `"0p"`, which is not a palindrome.

**Constraints:**

- `1 <= s.length <= 2 * 10^5`
- `s` contains only printable ASCII characters.

## 2. Examples

**Example 1**

```
Input:  s = "A man, a plan, a canal: Panama"
Output: true
```

The cleaned string is `"amanaplanacanalpanama"`, which is the same in both directions.

**Example 2**

```
Input:  s = "race a car"
Output: false
```

The cleaned string is `"raceacar"`. The fourth character from the left is `e`, but the fourth from the right is `a`.

**Example 3**

```
Input:  s = " "
Output: true
```

The cleaned string is empty. An empty string reads the same in both directions, so it is a palindrome.

## 3. Pattern Recognition

**What is the problem asking?** After ignoring case and every character that is not a letter or a digit, is the first character equal to the last, the second equal to the second last, and so on?

**What clues are present?**

- "Reads the same forward and backward" means comparing the character at each position with the character at the mirrored position.
- Each pair of mirrored characters can be checked on its own, and the answer is `true` only if every pair matches.
- Some characters must be skipped, and the case of letters must be ignored, so the raw string cannot be compared directly.

**Which patterns should I consider?** Building a cleaned copy and comparing it with its reverse, or the opposite-ends form of [Two Pointers](../../../../Patterns/TwoPointers.md) on the original string, skipping unwanted characters as the pointers move.

**Why does the chosen pattern fit?** A palindrome is defined by mirrored pairs, and two pointers that start at both ends and move inwards visit exactly those pairs, outermost first. Unlike the pair-sum form in the Two Pointers guide, this does not depend on sorted input. Here the pointers move together after every match, and the only reason to move one pointer alone is to skip a character that the cleaned string would not contain. So the pointers walk the cleaned string from both ends without ever building it.

**Why are the alternatives less suitable?** The cleaned copy is easy to write and also O(n) time, but it allocates a second string, and usually a third for the reverse, so it uses O(n) extra memory. It also always reads the whole string, even when the first and last characters already differ. That approach is fine as a first answer, and it is shown below, but the two-pointer version does the same comparisons with O(1) extra space and can stop at the first mismatch.

**What assumptions must hold?** The input is ASCII, as the constraints say. `Character.isLetterOrDigit` and `Character.toLowerCase` then match the problem's definition exactly: all 95 printable ASCII characters are classified the same way as by "is `a`-`z`, `A`-`Z` or `0`-`9`". For non-ASCII text these methods also accept letters such as `é`, which is a different rule from the problem's (see the follow-ups).

**How do the constraints influence the choice?** With up to 2 × 10^5 characters, any O(n) method is fast. The constraints do not force the two-pointer version, but O(1) extra space is a common follow-up question, and the in-place version is the one interviewers usually expect.

## 4. Prerequisites

- [String](../../README.md): `charAt`, immutability, and the basic palindrome check in that guide's example, which this problem extends with skipping and case folding.
- [Two Pointers](../../../../Patterns/TwoPointers.md): the opposite-ends form, where two indices move towards each other.

## 5. Intuition

Imagine reading a sentence from both ends at once, with one finger on the first character and one on the last. If a finger is on a space or a comma, slide it inwards until it reaches a letter or a digit. Now compare the two characters, ignoring case. If they differ, the sentence is not a palindrome and you can stop. If they match, move both fingers one step inwards and repeat. When the fingers meet or cross, every mirrored pair has matched.

The skipping is what makes this problem different from the plain palindrome check. Each finger skips its own unwanted characters, so the two fingers can move at different speeds, but they always point at the two characters that would be mirrored in the cleaned string.

## 6. Brute-Force Approach

Build the cleaned string, then compare it with its reverse.

```
cleaned = empty builder
for each character c in s:
    if c is a letter or a digit:
        append lowercase(c) to cleaned
return cleaned == reverse(cleaned)
```

In Java this is a `StringBuilder`, `Character.isLetterOrDigit`, `Character.toLowerCase`, then `cleaned.equals(new StringBuilder(cleaned).reverse().toString())`. It is correct by definition, because it builds exactly the string the problem describes. It takes O(n) time, but it needs O(n) extra space for the cleaned string and its reverse, and it reads the whole input even when the answer is clear from the first pair.

## 7. Preferred Approach

**Core idea:** Keep `left` at the start and `right` at the end. Move each pointer past characters that are not letters or digits, compare the two characters in lowercase, and move both inwards after a match. Stop with `false` at the first mismatch.

**Invariant:** At the top of the outer loop, every alphanumeric character before `left` has been matched with an alphanumeric character after `right`, in mirrored order. In other words, the cleaned string's first k characters equal its last k characters reversed, where k is the number of matched pairs.

**Why it is correct:** The invariant holds at the start, when no pairs have been matched. Inside the loop, the skipping steps move `left` to the next alphanumeric character from the left and `right` to the next one from the right. Those are exactly the (k + 1)th characters from each end of the cleaned string, so comparing them checks the next mirrored pair. If they differ, the cleaned string is not a palindrome and `false` is correct. If they match, moving both pointers inwards keeps the invariant. When `left` is no longer less than `right`, at most one alphanumeric character is left in the middle, and a single middle character matches itself, so every pair has matched and `true` is correct.

The `left < right` checks inside the skipping loops matter. Without them, a string of only punctuation such as `".,!?"` would move `left` past the end of the string and throw `StringIndexOutOfBoundsException`. With them, a pointer can stop on a character that is not alphanumeric only when the two pointers have met, and then the comparison is between a character and itself, which always matches.

**Steps:**

1. Set `left = 0` and `right = s.length() - 1`.
2. While `left < right`:
   1. Move `left` right while `left < right` and `s.charAt(left)` is not a letter or digit.
   2. Move `right` left while `left < right` and `s.charAt(right)` is not a letter or digit.
   3. If the lowercase forms of the two characters differ, return `false`.
   4. Increase `left` and decrease `right`.
3. Return `true`.

**Trade-offs:** The cleaned-copy version is shorter and harder to get wrong, and it is a reasonable choice when memory does not matter. The two-pointer version has more boundary conditions to think about, which is exactly what interviewers like to test.

## 8. Dry Run

**Input:** `s = "Madam, I'm Adam"`

The string has 15 characters, so `right` starts at 14. The indexes that matter are: `M` 0, `a` 1, `d` 2, `a` 3, `m` 4, `,` 5, space 6, `I` 7, `'` 8, `m` 9, space 10, `A` 11, `d` 12, `a` 13, `m` 14.

| Step | `left`, `right` at the top | After skipping | Compared | Match |
|---|---|---|---|---|
| 1 | 0, 14 | 0, 14 | `M` and `m` | yes |
| 2 | 1, 13 | 1, 13 | `a` and `a` | yes |
| 3 | 2, 12 | 2, 12 | `d` and `d` | yes |
| 4 | 3, 11 | 3, 11 | `a` and `A` | yes |
| 5 | 4, 10 | 4, 9 | `m` and `m` | yes |
| 6 | 5, 8 | 7, 7 | `I` and `I` | yes |

After step 6, `left` becomes 8 and `right` becomes 6. `left < right` is false, so the loop ends and the method returns `true`.

In steps 1 and 4, the letters differ only in case, and comparing their lowercase forms makes them equal. In step 5, `right` starts on the space at index 10 and skips it to reach `m` at 9, while `left` does not move: the two pointers skip independently. In step 6, `left` skips the comma and the space to reach `I` at 7. Then `right` skips the apostrophe at 8, which brings it to 7 as well, so the inner loop stops because `left < right` is false. The comparison is `I` with itself, the middle character of the cleaned string `"madamimadam"`.

For `"race a car"`, the first three pairs (`r`/`r`, `a`/`a`, `c`/`c`) match. On the fourth, `left` is on `e` at index 3, and `right` starts on the space at index 6, skips it, and stops on `a` at index 5. `e` and `a` differ, so the method returns `false` without looking at the rest.

## 9. Java Solution

See [Solution.java](Solution.java). To run it:

```bash
javac Solution.java
java Solution
```

Expected output:

```
"A man, a plan, a canal: Panama" -> true
"race a car" -> false
" " -> true
"Madam, I'm Adam" -> true
"0P" -> false
".,!?" -> true
"ab_a" -> true
"No lemon, no melon" -> true
```

`"0P"` shows that digits are kept: `0` and `p` differ. `".,!?"` has no alphanumeric characters at all and checks that the pointers stay inside the string. `"ab_a"` shows that the underscore is skipped even though it looks like part of a word.

Runs on Java 8, 11, 17, 21 and 25.

## 10. Complexity Analysis

- **Time:** O(n), where n is the length of `s`. `left` only moves right and `right` only moves left, and the loops stop once they meet, so together they take at most about n steps. Each step does constant work.
- **Auxiliary space:** O(1). The method uses two `int` indexes and reads the string in place, without building a cleaned copy.

## 11. Edge Cases

- **No alphanumeric characters,** such as `" "` or `".,!?"`. The cleaned string is empty, which is a palindrome, so the answer is `true`. The `left < right` checks inside the skipping loops keep the pointers in range.
- **A single character,** such as `"a"`. `left` and `right` start equal, the loop never runs, and the answer is `true`.
- **Different case only,** such as `"Aa"`. Comparing the lowercase forms gives `true`.
- **Digits next to letters,** such as `"0P"`. Digits are alphanumeric and kept, and `'0'` is not equal to `'p'`, so the answer is `false`.
- **Symbols that look like word characters,** such as `_` in `"ab_a"`. `Character.isLetterOrDigit('_')` is `false`, so it is skipped.
- **Odd and even cleaned lengths.** With an odd length, the pointers meet on the middle character, as in the dry run. With an even length, such as `"abba"`, they cross without meeting. Both end the loop correctly.

## 12. Common Mistakes

- **Leaving out `left < right` in the skipping loops.** For `".,!?"`, `left` would run past the last index and `charAt` would throw. For other inputs, the pointers could cross and compare characters that were already checked.
- **Using `Character.isLetter` instead of `Character.isLetterOrDigit`.** Digits would be skipped, so `"0P"` would clean to `"p"` and wrongly return `true`.
- **Calling `s.toLowerCase()` with no locale in the cleaned-copy version.** `String.toLowerCase()` uses the default locale, and in a Turkish locale `"I"` becomes the dotless `"ı"`, not `"i"`. `Character.toLowerCase(char)` does not depend on the locale. For strings, use `s.toLowerCase(Locale.ROOT)`.
- **Moving only one pointer after a match.** Both characters of the pair are done, so both pointers must move.
- **Comparing the cleaned string with its reverse using `==`.** That compares references. Use `equals`.

## 13. Interview Follow-ups

- **Can you remove at most one character to make it a palindrome?** ([LeetCode 680](https://leetcode.com/problems/valid-palindrome-ii/), Valid Palindrome II). Run the same two pointers. At the first mismatch, check whether either `s[left + 1..right]` or `s[left..right - 1]` is a palindrome with a second two-pointer loop. It is still O(n) time and O(1) space.
- **Is an integer a palindrome, without converting it to a string?** ([LeetCode 9](https://leetcode.com/problems/palindrome-number/), Palindrome Number). Reverse the last half of the digits with `% 10` and `/ 10`, and compare it with the first half.
- **The text can contain any Unicode characters.** Then the problem's rule, "a letter or a digit", has to be defined again for scripts other than English, and `Character.isLetterOrDigit` already accepts letters such as `é`. Characters outside the Basic Multilingual Plane are stored as two `char` values, so compare code points (`s.codePointAt` and `Character.charCount`) instead of single `char`s, or build an `int[]` from `s.codePoints()` and run the pointers over that.
- **Find the longest palindromic substring** ([LeetCode 5](https://leetcode.com/problems/longest-palindromic-substring/)). The pointers now start in the middle and move outwards: for each centre, expand while the two ends match. That takes O(n²) time and O(1) space.

## 14. Revision Summary

A palindrome is a set of mirrored pairs, so the opposite-ends form of [Two Pointers](../../../../Patterns/TwoPointers.md) checks it directly: start at both ends, compare, and move inwards. In this problem each pointer first skips characters that are not letters or digits, and the comparison uses `Character.toLowerCase`, so the pointers walk the cleaned string without building it. Keep `left < right` in the skipping loops so that strings with no letters or digits stay in range. It takes O(n) time and O(1) auxiliary space; building a cleaned copy and comparing it with its reverse is a simpler O(n) time, O(n) space alternative.
