import java.util.Arrays;

public class Solution {

    /**
     * Returns the length of the longest substring of s in which no character
     * appears more than once. The input is ASCII, as the problem states.
     */
    public static int lengthOfLongestSubstring(String s) {
        // lastSeen[c] is the index where character c was last seen, or -1.
        int[] lastSeen = new int[128];
        Arrays.fill(lastSeen, -1);

        int left = 0;
        int longest = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);

            // If c already appears inside the window [left, right - 1], the
            // window must start just after that earlier copy. An index before
            // left belongs to a character that already left the window, so it
            // must not move left backwards.
            if (lastSeen[c] >= left) {
                left = lastSeen[c] + 1;
            }

            lastSeen[c] = right;
            longest = Math.max(longest, right - left + 1);
        }
        return longest;
    }

    public static void main(String[] args) {
        String[] testCases = {
            "abcabcbb",
            "bbbbb",
            "pwwkew",
            "",
            " ",
            "abba",
            "dvdf",
            "tmmzuxt",
            "a b!c a"
        };

        for (String s : testCases) {
            System.out.println("\"" + s + "\" -> " + lengthOfLongestSubstring(s));
        }
    }
}
