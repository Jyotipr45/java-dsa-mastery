public class Solution {

    /**
     * Returns true if s reads the same forwards and backwards after every
     * uppercase letter is changed to lowercase and every character that is
     * not a letter or a digit is removed. The input is printable ASCII.
     */
    public static boolean isPalindrome(String s) {
        int left = 0;
        int right = s.length() - 1;

        while (left < right) {
            // Skip characters that are not part of the cleaned string.
            // The left < right check stops the scan when nothing is left to
            // compare, for example in a string of only punctuation.
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
                left++;
            }
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
                right--;
            }

            // Compare the two characters without caring about case.
            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false;
            }

            // This pair matches, so both characters are done.
            left++;
            right--;
        }
        return true;
    }

    public static void main(String[] args) {
        String[] testCases = {
            "A man, a plan, a canal: Panama",
            "race a car",
            " ",
            "Madam, I'm Adam",
            "0P",
            ".,!?",
            "ab_a",
            "No lemon, no melon"
        };

        for (String s : testCases) {
            System.out.println("\"" + s + "\" -> " + isPalindrome(s));
        }
    }
}
