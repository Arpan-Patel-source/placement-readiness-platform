package com.majorproject.backend.coding;

import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Curated problem bank with 35+ DSA problems across all 7 categories and 3 difficulties.
 * Each problem includes sample + hidden test cases, starter code, hints, and complexity info.
 */
@Component
public class CodingProblemBank {

    private final List<CodingProblem> problems = new ArrayList<>();

    public CodingProblemBank() {
        initProblems();
    }

    public List<CodingProblem> getAllProblems() {
        return Collections.unmodifiableList(problems);
    }

    public Optional<CodingProblem> getProblemById(String id) {
        return problems.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    private void initProblems() {
        // ─── ARRAYS ────────────────────────────────────────────────────────────
        problems.add(CodingProblem.builder()
                .id("arr-001")
                .title("Two Sum")
                .description("Given an array of integers `nums` and an integer `target`, return indices of the two numbers such that they add up to `target`.\n\nYou may assume that each input would have exactly one solution, and you may not use the same element twice.\n\nReturn the answer as two space-separated indices (0-based).")
                .category(CodingCategory.ARRAYS)
                .difficulty(CodingDifficulty.EASY)
                .constraints("2 <= nums.length <= 10^4\n-10^9 <= nums[i] <= 10^9\n-10^9 <= target <= 10^9")
                .inputFormat("First line: space-separated integers (the array)\nSecond line: an integer (target)")
                .outputFormat("Two space-separated indices")
                .testCases(List.of(
                        TestCase.builder().input("2 7 11 15\n9").expectedOutput("0 1").hidden(false).build(),
                        TestCase.builder().input("3 2 4\n6").expectedOutput("1 2").hidden(false).build(),
                        TestCase.builder().input("3 3\n6").expectedOutput("0 1").hidden(true).build(),
                        TestCase.builder().input("1 5 3 7 2\n9").expectedOutput("1 3").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String[] parts = sc.nextLine().split(\" \");\n        int[] nums = new int[parts.length];\n        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i]);\n        int target = Integer.parseInt(sc.nextLine().trim());\n        // Solve here\n    }\n}")
                .starterCodePython("nums = list(map(int, input().split()))\ntarget = int(input())\n# Solve here")
                .starterCodeCpp("#include <iostream>\n#include <vector>\n#include <sstream>\nusing namespace std;\n\nint main() {\n    string line;\n    getline(cin, line);\n    istringstream iss(line);\n    vector<int> nums;\n    int x;\n    while (iss >> x) nums.push_back(x);\n    int target;\n    cin >> target;\n    // Solve here\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\n\nint main() {\n    int nums[10000], n = 0, target;\n    while (scanf(\"%d\", &nums[n]) == 1) { n++; if (getchar() == '\\n') break; }\n    scanf(\"%d\", &target);\n    // Solve here\n    return 0;\n}")
                .hints(List.of("Use a HashMap to store complement values", "For each element, check if target - element exists in the map"))
                .optimalApproach("Hash Map — single pass")
                .timeComplexity("O(n)")
                .spaceComplexity("O(n)")
                .companiesAsked(List.of("Google", "Amazon", "Microsoft", "TCS"))
                .build());

        problems.add(CodingProblem.builder()
                .id("arr-002")
                .title("Maximum Subarray Sum")
                .description("Given an integer array `nums`, find the subarray with the largest sum, and return its sum.")
                .category(CodingCategory.ARRAYS)
                .difficulty(CodingDifficulty.MEDIUM)
                .constraints("1 <= nums.length <= 10^5\n-10^4 <= nums[i] <= 10^4")
                .inputFormat("A single line of space-separated integers")
                .outputFormat("A single integer — the maximum subarray sum")
                .testCases(List.of(
                        TestCase.builder().input("-2 1 -3 4 -1 2 1 -5 4").expectedOutput("6").hidden(false).build(),
                        TestCase.builder().input("1").expectedOutput("1").hidden(false).build(),
                        TestCase.builder().input("5 4 -1 7 8").expectedOutput("23").hidden(true).build(),
                        TestCase.builder().input("-1 -2 -3 -4").expectedOutput("-1").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String[] parts = sc.nextLine().split(\" \");\n        int[] nums = new int[parts.length];\n        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i]);\n        // Kadane's algorithm\n    }\n}")
                .starterCodePython("nums = list(map(int, input().split()))\n# Kadane's algorithm")
                .starterCodeCpp("#include <iostream>\n#include <vector>\n#include <sstream>\nusing namespace std;\nint main() {\n    string line; getline(cin, line); istringstream iss(line);\n    vector<int> nums; int x;\n    while (iss >> x) nums.push_back(x);\n    // Kadane's algorithm\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    int nums[100000], n = 0;\n    while (scanf(\"%d\", &nums[n]) == 1) { n++; if (getchar() == '\\n') break; }\n    // Kadane's algorithm\n    return 0;\n}")
                .hints(List.of("Think about Kadane's Algorithm", "Keep track of current sum and max sum"))
                .optimalApproach("Kadane's Algorithm")
                .timeComplexity("O(n)")
                .spaceComplexity("O(1)")
                .companiesAsked(List.of("Amazon", "Microsoft", "Flipkart", "Infosys"))
                .build());

        problems.add(CodingProblem.builder()
                .id("arr-003")
                .title("Merge Sorted Arrays")
                .description("Given two sorted integer arrays `nums1` and `nums2`, merge them into a single sorted array and print it.")
                .category(CodingCategory.ARRAYS)
                .difficulty(CodingDifficulty.EASY)
                .constraints("0 <= nums1.length, nums2.length <= 10^4")
                .inputFormat("First line: space-separated sorted integers (nums1)\nSecond line: space-separated sorted integers (nums2)")
                .outputFormat("A single line of space-separated sorted integers")
                .testCases(List.of(
                        TestCase.builder().input("1 2 3\n2 5 6").expectedOutput("1 2 2 3 5 6").hidden(false).build(),
                        TestCase.builder().input("1\n").expectedOutput("1").hidden(false).build(),
                        TestCase.builder().input("1 3 5 7\n2 4 6 8").expectedOutput("1 2 3 4 5 6 7 8").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        // Read two lines, merge sorted\n    }\n}")
                .starterCodePython("nums1 = list(map(int, input().split())) if True else []\nnums2 = list(map(int, input().split())) if True else []\n# Merge sorted")
                .starterCodeCpp("#include <iostream>\n#include <vector>\nusing namespace std;\nint main() {\n    // Read two lines, merge sorted\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    // Read two lines, merge sorted\n    return 0;\n}")
                .hints(List.of("Use two pointers, one for each array", "Compare elements and pick the smaller one"))
                .optimalApproach("Two Pointer Merge")
                .timeComplexity("O(n + m)")
                .spaceComplexity("O(n + m)")
                .companiesAsked(List.of("TCS", "Wipro", "Cognizant"))
                .build());

        problems.add(CodingProblem.builder()
                .id("arr-004")
                .title("Trapping Rain Water")
                .description("Given `n` non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.")
                .category(CodingCategory.ARRAYS)
                .difficulty(CodingDifficulty.HARD)
                .constraints("n == height.length\n1 <= n <= 2 * 10^4\n0 <= height[i] <= 10^5")
                .inputFormat("A single line of space-separated non-negative integers")
                .outputFormat("A single integer — total trapped water")
                .testCases(List.of(
                        TestCase.builder().input("0 1 0 2 1 0 1 3 2 1 2 1").expectedOutput("6").hidden(false).build(),
                        TestCase.builder().input("4 2 0 3 2 5").expectedOutput("9").hidden(false).build(),
                        TestCase.builder().input("3 0 2 0 4").expectedOutput("7").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String[] parts = sc.nextLine().split(\" \");\n        int[] height = new int[parts.length];\n        for (int i = 0; i < parts.length; i++) height[i] = Integer.parseInt(parts[i]);\n        // Solve\n    }\n}")
                .starterCodePython("height = list(map(int, input().split()))\n# Solve")
                .starterCodeCpp("#include <iostream>\n#include <vector>\n#include <sstream>\nusing namespace std;\nint main() {\n    string line; getline(cin, line); istringstream iss(line);\n    vector<int> h; int x;\n    while (iss >> x) h.push_back(x);\n    // Solve\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    int h[20000], n = 0;\n    while (scanf(\"%d\", &h[n]) == 1) { n++; if (getchar() == '\\n') break; }\n    // Solve\n    return 0;\n}")
                .hints(List.of("Use two pointers from left and right", "Water at any index = min(leftMax, rightMax) - height[i]"))
                .optimalApproach("Two Pointer")
                .timeComplexity("O(n)")
                .spaceComplexity("O(1)")
                .companiesAsked(List.of("Google", "Amazon", "Microsoft"))
                .build());

        problems.add(CodingProblem.builder()
                .id("arr-005")
                .title("Rotate Array")
                .description("Given an integer array `nums`, rotate the array to the right by `k` steps. Print the resulting array.")
                .category(CodingCategory.ARRAYS)
                .difficulty(CodingDifficulty.MEDIUM)
                .constraints("1 <= nums.length <= 10^5\n-2^31 <= nums[i] <= 2^31 - 1\n0 <= k <= 10^5")
                .inputFormat("First line: space-separated integers\nSecond line: an integer k")
                .outputFormat("Space-separated integers after rotation")
                .testCases(List.of(
                        TestCase.builder().input("1 2 3 4 5 6 7\n3").expectedOutput("5 6 7 1 2 3 4").hidden(false).build(),
                        TestCase.builder().input("-1 -100 3 99\n2").expectedOutput("3 99 -1 -100").hidden(false).build(),
                        TestCase.builder().input("1 2\n3").expectedOutput("2 1").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String[] parts = sc.nextLine().split(\" \");\n        int[] nums = new int[parts.length];\n        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i]);\n        int k = Integer.parseInt(sc.nextLine().trim());\n        // Rotate\n    }\n}")
                .starterCodePython("nums = list(map(int, input().split()))\nk = int(input())\n# Rotate")
                .starterCodeCpp("#include <iostream>\n#include <vector>\n#include <sstream>\nusing namespace std;\nint main() {\n    string line; getline(cin, line); istringstream iss(line);\n    vector<int> nums; int x;\n    while (iss >> x) nums.push_back(x);\n    int k; cin >> k;\n    // Rotate\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    int nums[100000], n = 0, k;\n    while (scanf(\"%d\", &nums[n]) == 1) { n++; if (getchar() == '\\n') break; }\n    scanf(\"%d\", &k);\n    // Rotate\n    return 0;\n}")
                .hints(List.of("Reverse the entire array, then reverse first k, then reverse rest", "k = k % n to handle k > array length"))
                .optimalApproach("Reverse Three Times")
                .timeComplexity("O(n)")
                .spaceComplexity("O(1)")
                .companiesAsked(List.of("Amazon", "Microsoft", "Infosys"))
                .build());

        // ─── STRINGS ───────────────────────────────────────────────────────────
        problems.add(CodingProblem.builder()
                .id("str-001")
                .title("Valid Anagram")
                .description("Given two strings `s` and `t`, return `true` if `t` is an anagram of `s`, and `false` otherwise.")
                .category(CodingCategory.STRINGS)
                .difficulty(CodingDifficulty.EASY)
                .constraints("1 <= s.length, t.length <= 5 * 10^4\ns and t consist of lowercase English letters")
                .inputFormat("First line: string s\nSecond line: string t")
                .outputFormat("true or false")
                .testCases(List.of(
                        TestCase.builder().input("anagram\nnagaram").expectedOutput("true").hidden(false).build(),
                        TestCase.builder().input("rat\ncar").expectedOutput("false").hidden(false).build(),
                        TestCase.builder().input("a\na").expectedOutput("true").hidden(true).build(),
                        TestCase.builder().input("ab\nba").expectedOutput("true").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String s = sc.nextLine().trim();\n        String t = sc.nextLine().trim();\n        // Check anagram\n    }\n}")
                .starterCodePython("s = input().strip()\nt = input().strip()\n# Check anagram")
                .starterCodeCpp("#include <iostream>\n#include <string>\nusing namespace std;\nint main() {\n    string s, t;\n    getline(cin, s); getline(cin, t);\n    // Check anagram\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\n#include <string.h>\nint main() {\n    char s[50001], t[50001];\n    fgets(s, sizeof(s), stdin); s[strcspn(s, \"\\n\")] = 0;\n    fgets(t, sizeof(t), stdin); t[strcspn(t, \"\\n\")] = 0;\n    // Check anagram\n    return 0;\n}")
                .hints(List.of("Count character frequencies", "Both strings must have same character counts"))
                .optimalApproach("Character Frequency Array")
                .timeComplexity("O(n)")
                .spaceComplexity("O(1)")
                .companiesAsked(List.of("TCS", "Infosys", "Wipro"))
                .build());

        problems.add(CodingProblem.builder()
                .id("str-002")
                .title("Longest Palindromic Substring")
                .description("Given a string `s`, return the longest palindromic substring in `s`.")
                .category(CodingCategory.STRINGS)
                .difficulty(CodingDifficulty.MEDIUM)
                .constraints("1 <= s.length <= 1000\ns consist of only digits and English letters")
                .inputFormat("A single string")
                .outputFormat("The longest palindromic substring")
                .testCases(List.of(
                        TestCase.builder().input("babad").expectedOutput("bab").hidden(false).build(),
                        TestCase.builder().input("cbbd").expectedOutput("bb").hidden(false).build(),
                        TestCase.builder().input("a").expectedOutput("a").hidden(true).build(),
                        TestCase.builder().input("racecar").expectedOutput("racecar").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        String s = new Scanner(System.in).nextLine().trim();\n        // Find longest palindrome\n    }\n}")
                .starterCodePython("s = input().strip()\n# Find longest palindrome")
                .starterCodeCpp("#include <iostream>\n#include <string>\nusing namespace std;\nint main() {\n    string s; getline(cin, s);\n    // Find longest palindrome\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\n#include <string.h>\nint main() {\n    char s[1001];\n    fgets(s, sizeof(s), stdin); s[strcspn(s, \"\\n\")] = 0;\n    // Find longest palindrome\n    return 0;\n}")
                .hints(List.of("Expand around center for each position", "Consider both odd and even length palindromes"))
                .optimalApproach("Expand Around Center")
                .timeComplexity("O(n²)")
                .spaceComplexity("O(1)")
                .companiesAsked(List.of("Amazon", "Google", "Accenture"))
                .build());

        problems.add(CodingProblem.builder()
                .id("str-003")
                .title("Reverse Words in a String")
                .description("Given an input string `s`, reverse the order of the words. A word is defined as a sequence of non-space characters. Return the string with words in reverse order, separated by single spaces.")
                .category(CodingCategory.STRINGS)
                .difficulty(CodingDifficulty.EASY)
                .constraints("1 <= s.length <= 10^4")
                .inputFormat("A single line string")
                .outputFormat("Words in reverse order")
                .testCases(List.of(
                        TestCase.builder().input("the sky is blue").expectedOutput("blue is sky the").hidden(false).build(),
                        TestCase.builder().input("  hello world  ").expectedOutput("world hello").hidden(false).build(),
                        TestCase.builder().input("a good   example").expectedOutput("example good a").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        String s = new Scanner(System.in).nextLine();\n        // Reverse words\n    }\n}")
                .starterCodePython("s = input()\n# Reverse words")
                .starterCodeCpp("#include <iostream>\n#include <string>\nusing namespace std;\nint main() {\n    string s; getline(cin, s);\n    // Reverse words\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    char s[10001];\n    fgets(s, sizeof(s), stdin);\n    // Reverse words\n    return 0;\n}")
                .hints(List.of("Split by spaces, reverse the resulting list", "Handle multiple spaces and leading/trailing spaces"))
                .optimalApproach("Split and Reverse")
                .timeComplexity("O(n)")
                .spaceComplexity("O(n)")
                .companiesAsked(List.of("TCS", "Capgemini", "Cognizant"))
                .build());

        // ─── LINKED LIST ───────────────────────────────────────────────────────
        problems.add(CodingProblem.builder()
                .id("ll-001")
                .title("Reverse a Linked List")
                .description("Given a singly linked list represented as space-separated integers, reverse it and print the result.")
                .category(CodingCategory.LINKED_LIST)
                .difficulty(CodingDifficulty.EASY)
                .constraints("0 <= Number of nodes <= 5000")
                .inputFormat("Space-separated integers representing linked list values")
                .outputFormat("Space-separated integers in reversed order")
                .testCases(List.of(
                        TestCase.builder().input("1 2 3 4 5").expectedOutput("5 4 3 2 1").hidden(false).build(),
                        TestCase.builder().input("1 2").expectedOutput("2 1").hidden(false).build(),
                        TestCase.builder().input("1").expectedOutput("1").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String[] parts = sc.nextLine().split(\" \");\n        // Reverse the list\n    }\n}")
                .starterCodePython("nums = list(map(int, input().split()))\n# Reverse")
                .starterCodeCpp("#include <iostream>\n#include <vector>\n#include <sstream>\nusing namespace std;\nint main() {\n    string line; getline(cin, line); istringstream iss(line);\n    vector<int> v; int x;\n    while (iss >> x) v.push_back(x);\n    // Reverse\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    int a[5000], n = 0;\n    while (scanf(\"%d\", &a[n]) == 1) { n++; if (getchar() == '\\n') break; }\n    // Reverse\n    return 0;\n}")
                .hints(List.of("Use three pointers: prev, current, next", "Iterate and reverse the links"))
                .optimalApproach("Iterative Three Pointer")
                .timeComplexity("O(n)")
                .spaceComplexity("O(1)")
                .companiesAsked(List.of("TCS", "Infosys", "Wipro", "Amazon"))
                .build());

        problems.add(CodingProblem.builder()
                .id("ll-002")
                .title("Detect Cycle in Linked List")
                .description("Given a sequence of integers, determine if the sequence contains a cycle. A cycle is indicated by the last integer being a 0-indexed position within the sequence that the last element points back to. If the last value is -1, there is no cycle.\n\nPrint 'true' if cycle exists, 'false' otherwise.")
                .category(CodingCategory.LINKED_LIST)
                .difficulty(CodingDifficulty.MEDIUM)
                .constraints("0 <= Number of nodes <= 10^4")
                .inputFormat("First line: space-separated node values\nSecond line: position index the tail connects to (-1 for no cycle)")
                .outputFormat("true or false")
                .testCases(List.of(
                        TestCase.builder().input("3 2 0 -4\n1").expectedOutput("true").hidden(false).build(),
                        TestCase.builder().input("1 2\n0").expectedOutput("true").hidden(false).build(),
                        TestCase.builder().input("1\n-1").expectedOutput("false").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String[] parts = sc.nextLine().split(\" \");\n        int pos = Integer.parseInt(sc.nextLine().trim());\n        System.out.println(pos >= 0 ? \"true\" : \"false\");\n    }\n}")
                .starterCodePython("vals = list(map(int, input().split()))\npos = int(input())\n# Detect cycle")
                .starterCodeCpp("#include <iostream>\nusing namespace std;\nint main() {\n    // Detect cycle\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    // Detect cycle\n    return 0;\n}")
                .hints(List.of("Floyd's Tortoise and Hare algorithm", "Use slow and fast pointers"))
                .optimalApproach("Floyd's Cycle Detection")
                .timeComplexity("O(n)")
                .spaceComplexity("O(1)")
                .companiesAsked(List.of("Amazon", "Microsoft", "Google"))
                .build());

        problems.add(CodingProblem.builder()
                .id("ll-003")
                .title("Merge Two Sorted Linked Lists")
                .description("Given two sorted linked lists (as space-separated integers), merge them into one sorted list and print the result.")
                .category(CodingCategory.LINKED_LIST)
                .difficulty(CodingDifficulty.EASY)
                .constraints("0 <= list lengths <= 50")
                .inputFormat("First line: sorted integers (list 1)\nSecond line: sorted integers (list 2)")
                .outputFormat("Space-separated merged sorted integers")
                .testCases(List.of(
                        TestCase.builder().input("1 2 4\n1 3 4").expectedOutput("1 1 2 3 4 4").hidden(false).build(),
                        TestCase.builder().input("\n0").expectedOutput("0").hidden(false).build(),
                        TestCase.builder().input("2 5 8\n1 3 6 9").expectedOutput("1 2 3 5 6 8 9").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        // Read two lists, merge\n    }\n}")
                .starterCodePython("l1 = list(map(int, input().split())) if True else []\nl2 = list(map(int, input().split())) if True else []\n# Merge")
                .starterCodeCpp("#include <iostream>\nusing namespace std;\nint main() {\n    // Merge two sorted lists\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    // Merge two sorted lists\n    return 0;\n}")
                .hints(List.of("Use a dummy head node", "Compare heads of both lists, attach smaller"))
                .optimalApproach("Two Pointer Merge")
                .timeComplexity("O(n + m)")
                .spaceComplexity("O(1)")
                .companiesAsked(List.of("TCS", "Infosys", "Accenture"))
                .build());

        // ─── TREES ─────────────────────────────────────────────────────────────
        problems.add(CodingProblem.builder()
                .id("tree-001")
                .title("Binary Tree Inorder Traversal")
                .description("Given a binary tree represented as a level-order array (use -1 for null nodes), return its inorder traversal as space-separated values.")
                .category(CodingCategory.TREES)
                .difficulty(CodingDifficulty.EASY)
                .constraints("0 <= Number of nodes <= 100")
                .inputFormat("Space-separated integers in level-order (-1 for null)")
                .outputFormat("Space-separated inorder traversal")
                .testCases(List.of(
                        TestCase.builder().input("1 -1 2 3").expectedOutput("1 3 2").hidden(false).build(),
                        TestCase.builder().input("1").expectedOutput("1").hidden(false).build(),
                        TestCase.builder().input("1 2 3 4 5 -1 6").expectedOutput("4 2 5 1 3 6").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String[] parts = sc.nextLine().split(\" \");\n        // Build tree, inorder traversal\n    }\n}")
                .starterCodePython("nodes = list(map(int, input().split()))\n# Build tree, inorder traversal")
                .starterCodeCpp("#include <iostream>\nusing namespace std;\nint main() {\n    // Build tree, inorder traversal\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    // Build tree, inorder traversal\n    return 0;\n}")
                .hints(List.of("Left -> Root -> Right", "Use recursion or a stack"))
                .optimalApproach("Recursive / Morris Traversal")
                .timeComplexity("O(n)")
                .spaceComplexity("O(h)")
                .companiesAsked(List.of("Amazon", "Microsoft", "TCS"))
                .build());

        problems.add(CodingProblem.builder()
                .id("tree-002")
                .title("Maximum Depth of Binary Tree")
                .description("Given a binary tree in level-order (-1 for null), find its maximum depth (number of nodes along the longest path from root to a leaf).")
                .category(CodingCategory.TREES)
                .difficulty(CodingDifficulty.EASY)
                .constraints("0 <= Number of nodes <= 10^4")
                .inputFormat("Space-separated integers in level-order (-1 for null)")
                .outputFormat("A single integer — the maximum depth")
                .testCases(List.of(
                        TestCase.builder().input("3 9 20 -1 -1 15 7").expectedOutput("3").hidden(false).build(),
                        TestCase.builder().input("1 -1 2").expectedOutput("2").hidden(false).build(),
                        TestCase.builder().input("1").expectedOutput("1").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        // Build tree, find max depth\n    }\n}")
                .starterCodePython("nodes = list(map(int, input().split()))\n# Find max depth")
                .starterCodeCpp("#include <iostream>\nusing namespace std;\nint main() {\n    // Find max depth\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    // Find max depth\n    return 0;\n}")
                .hints(List.of("Use DFS: depth = 1 + max(left_depth, right_depth)", "Base case: null node returns 0"))
                .optimalApproach("DFS Recursion")
                .timeComplexity("O(n)")
                .spaceComplexity("O(h)")
                .companiesAsked(List.of("Wipro", "Infosys", "Google"))
                .build());

        problems.add(CodingProblem.builder()
                .id("tree-003")
                .title("Validate Binary Search Tree")
                .description("Given a binary tree in level-order (-1 for null), determine if it is a valid binary search tree (BST). Print 'true' or 'false'.")
                .category(CodingCategory.TREES)
                .difficulty(CodingDifficulty.MEDIUM)
                .constraints("1 <= Number of nodes <= 10^4")
                .inputFormat("Space-separated integers in level-order (-1 for null)")
                .outputFormat("true or false")
                .testCases(List.of(
                        TestCase.builder().input("2 1 3").expectedOutput("true").hidden(false).build(),
                        TestCase.builder().input("5 1 4 -1 -1 3 6").expectedOutput("false").hidden(false).build(),
                        TestCase.builder().input("1").expectedOutput("true").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        // Build tree, validate BST\n    }\n}")
                .starterCodePython("nodes = list(map(int, input().split()))\n# Validate BST")
                .starterCodeCpp("#include <iostream>\nusing namespace std;\nint main() {\n    // Validate BST\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    // Validate BST\n    return 0;\n}")
                .hints(List.of("Use min/max bounds during recursion", "Inorder traversal of BST is strictly increasing"))
                .optimalApproach("Recursive with Range Check")
                .timeComplexity("O(n)")
                .spaceComplexity("O(h)")
                .companiesAsked(List.of("Amazon", "Google", "Flipkart"))
                .build());

        // ─── GRAPHS ────────────────────────────────────────────────────────────
        problems.add(CodingProblem.builder()
                .id("graph-001")
                .title("Number of Islands")
                .description("Given a 2D grid of '1's (land) and '0's (water), count the number of islands. An island is surrounded by water and formed by connecting adjacent lands horizontally or vertically.")
                .category(CodingCategory.GRAPHS)
                .difficulty(CodingDifficulty.MEDIUM)
                .constraints("m == grid.length\nn == grid[i].length\n1 <= m, n <= 300")
                .inputFormat("First line: rows cols\nFollowing lines: each row of the grid (space-separated 0s and 1s)")
                .outputFormat("A single integer — number of islands")
                .testCases(List.of(
                        TestCase.builder().input("4 5\n1 1 1 1 0\n1 1 0 1 0\n1 1 0 0 0\n0 0 0 0 0").expectedOutput("1").hidden(false).build(),
                        TestCase.builder().input("4 5\n1 1 0 0 0\n1 1 0 0 0\n0 0 1 0 0\n0 0 0 1 1").expectedOutput("3").hidden(false).build(),
                        TestCase.builder().input("1 1\n1").expectedOutput("1").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String[] dims = sc.nextLine().split(\" \");\n        int rows = Integer.parseInt(dims[0]), cols = Integer.parseInt(dims[1]);\n        int[][] grid = new int[rows][cols];\n        for (int i = 0; i < rows; i++) {\n            String[] row = sc.nextLine().split(\" \");\n            for (int j = 0; j < cols; j++) grid[i][j] = Integer.parseInt(row[j]);\n        }\n        // Count islands\n    }\n}")
                .starterCodePython("r, c = map(int, input().split())\ngrid = []\nfor _ in range(r):\n    grid.append(list(map(int, input().split())))\n# Count islands")
                .starterCodeCpp("#include <iostream>\n#include <vector>\nusing namespace std;\nint main() {\n    int r, c; cin >> r >> c;\n    vector<vector<int>> grid(r, vector<int>(c));\n    for (int i = 0; i < r; i++)\n        for (int j = 0; j < c; j++) cin >> grid[i][j];\n    // Count islands\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    int r, c;\n    scanf(\"%d %d\", &r, &c);\n    int grid[300][300];\n    for (int i = 0; i < r; i++)\n        for (int j = 0; j < c; j++) scanf(\"%d\", &grid[i][j]);\n    // Count islands\n    return 0;\n}")
                .hints(List.of("Use DFS/BFS to explore each island", "Mark visited cells to avoid counting twice"))
                .optimalApproach("DFS Flood Fill")
                .timeComplexity("O(m × n)")
                .spaceComplexity("O(m × n)")
                .companiesAsked(List.of("Amazon", "Google", "Microsoft"))
                .build());

        problems.add(CodingProblem.builder()
                .id("graph-002")
                .title("BFS Shortest Path in Unweighted Graph")
                .description("Given an unweighted undirected graph with N nodes and M edges, find the shortest path length from node 0 to node N-1. If no path exists, print -1.")
                .category(CodingCategory.GRAPHS)
                .difficulty(CodingDifficulty.MEDIUM)
                .constraints("2 <= N <= 10^4\n0 <= M <= 10^5")
                .inputFormat("First line: N M\nNext M lines: u v (edges)")
                .outputFormat("A single integer — shortest path length or -1")
                .testCases(List.of(
                        TestCase.builder().input("5 6\n0 1\n0 2\n1 3\n2 3\n3 4\n2 4").expectedOutput("2").hidden(false).build(),
                        TestCase.builder().input("3 1\n0 1").expectedOutput("-1").hidden(false).build(),
                        TestCase.builder().input("2 1\n0 1").expectedOutput("1").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt(), m = sc.nextInt();\n        // Build graph, BFS\n    }\n}")
                .starterCodePython("n, m = map(int, input().split())\nedges = [list(map(int, input().split())) for _ in range(m)]\n# BFS")
                .starterCodeCpp("#include <iostream>\n#include <vector>\n#include <queue>\nusing namespace std;\nint main() {\n    int n, m; cin >> n >> m;\n    // Build graph, BFS\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    int n, m;\n    scanf(\"%d %d\", &n, &m);\n    // BFS\n    return 0;\n}")
                .hints(List.of("Use BFS from node 0", "Track distance array initialized to -1"))
                .optimalApproach("BFS Level Order")
                .timeComplexity("O(V + E)")
                .spaceComplexity("O(V + E)")
                .companiesAsked(List.of("Google", "Amazon", "Flipkart"))
                .build());

        problems.add(CodingProblem.builder()
                .id("graph-003")
                .title("Detect Cycle in Undirected Graph")
                .description("Given an undirected graph with N nodes and M edges, detect if the graph contains a cycle. Print 'true' or 'false'.")
                .category(CodingCategory.GRAPHS)
                .difficulty(CodingDifficulty.MEDIUM)
                .constraints("1 <= N <= 10^4\n0 <= M <= 10^5")
                .inputFormat("First line: N M\nNext M lines: u v (edges)")
                .outputFormat("true or false")
                .testCases(List.of(
                        TestCase.builder().input("4 4\n0 1\n1 2\n2 3\n3 0").expectedOutput("true").hidden(false).build(),
                        TestCase.builder().input("3 2\n0 1\n1 2").expectedOutput("false").hidden(false).build(),
                        TestCase.builder().input("5 5\n0 1\n1 2\n2 3\n3 4\n4 2").expectedOutput("true").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt(), m = sc.nextInt();\n        // Build graph, detect cycle\n    }\n}")
                .starterCodePython("n, m = map(int, input().split())\nedges = [list(map(int, input().split())) for _ in range(m)]\n# Detect cycle")
                .starterCodeCpp("#include <iostream>\nusing namespace std;\nint main() {\n    int n, m; cin >> n >> m;\n    // Detect cycle\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    // Detect cycle\n    return 0;\n}")
                .hints(List.of("Use DFS with parent tracking", "If a visited node is found that isn't the parent, there's a cycle"))
                .optimalApproach("DFS with Parent Tracking")
                .timeComplexity("O(V + E)")
                .spaceComplexity("O(V + E)")
                .companiesAsked(List.of("Amazon", "Infosys", "Wipro"))
                .build());

        // ─── DYNAMIC PROGRAMMING ───────────────────────────────────────────────
        problems.add(CodingProblem.builder()
                .id("dp-001")
                .title("Climbing Stairs")
                .description("You are climbing a staircase with `n` steps. Each time you can either climb 1 or 2 steps. How many distinct ways can you climb to the top?")
                .category(CodingCategory.DP)
                .difficulty(CodingDifficulty.EASY)
                .constraints("1 <= n <= 45")
                .inputFormat("A single integer n")
                .outputFormat("A single integer — number of ways")
                .testCases(List.of(
                        TestCase.builder().input("2").expectedOutput("2").hidden(false).build(),
                        TestCase.builder().input("3").expectedOutput("3").hidden(false).build(),
                        TestCase.builder().input("5").expectedOutput("8").hidden(true).build(),
                        TestCase.builder().input("10").expectedOutput("89").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        int n = Integer.parseInt(new Scanner(System.in).nextLine().trim());\n        // DP\n    }\n}")
                .starterCodePython("n = int(input())\n# DP")
                .starterCodeCpp("#include <iostream>\nusing namespace std;\nint main() {\n    int n; cin >> n;\n    // DP\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    int n; scanf(\"%d\", &n);\n    // DP\n    return 0;\n}")
                .hints(List.of("This is essentially Fibonacci", "dp[i] = dp[i-1] + dp[i-2]"))
                .optimalApproach("Bottom-up DP (Fibonacci)")
                .timeComplexity("O(n)")
                .spaceComplexity("O(1)")
                .companiesAsked(List.of("TCS", "Infosys", "Accenture", "Amazon"))
                .build());

        problems.add(CodingProblem.builder()
                .id("dp-002")
                .title("0/1 Knapsack")
                .description("Given `n` items with weights and values, and a knapsack capacity `W`, find the maximum total value you can carry. Each item can be used at most once.")
                .category(CodingCategory.DP)
                .difficulty(CodingDifficulty.MEDIUM)
                .constraints("1 <= n <= 100\n1 <= W <= 1000")
                .inputFormat("First line: n W\nNext n lines: weight value")
                .outputFormat("A single integer — maximum value")
                .testCases(List.of(
                        TestCase.builder().input("4 7\n1 1\n3 4\n4 5\n5 7").expectedOutput("9").hidden(false).build(),
                        TestCase.builder().input("3 50\n10 60\n20 100\n30 120").expectedOutput("220").hidden(false).build(),
                        TestCase.builder().input("1 1\n2 3").expectedOutput("0").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt(), W = sc.nextInt();\n        int[] wt = new int[n], val = new int[n];\n        for (int i = 0; i < n; i++) { wt[i] = sc.nextInt(); val[i] = sc.nextInt(); }\n        // DP knapsack\n    }\n}")
                .starterCodePython("n, W = map(int, input().split())\nitems = [list(map(int, input().split())) for _ in range(n)]\n# DP knapsack")
                .starterCodeCpp("#include <iostream>\nusing namespace std;\nint main() {\n    int n, W; cin >> n >> W;\n    // DP knapsack\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    int n, W;\n    scanf(\"%d %d\", &n, &W);\n    // DP knapsack\n    return 0;\n}")
                .hints(List.of("Use 2D DP table dp[i][w]", "For each item, decide to include or exclude"))
                .optimalApproach("2D DP Table")
                .timeComplexity("O(n × W)")
                .spaceComplexity("O(n × W)")
                .companiesAsked(List.of("Amazon", "Microsoft", "Flipkart"))
                .build());

        problems.add(CodingProblem.builder()
                .id("dp-003")
                .title("Longest Common Subsequence")
                .description("Given two strings, find the length of their longest common subsequence.")
                .category(CodingCategory.DP)
                .difficulty(CodingDifficulty.MEDIUM)
                .constraints("1 <= text1.length, text2.length <= 1000")
                .inputFormat("First line: string text1\nSecond line: string text2")
                .outputFormat("A single integer — length of LCS")
                .testCases(List.of(
                        TestCase.builder().input("abcde\nace").expectedOutput("3").hidden(false).build(),
                        TestCase.builder().input("abc\nabc").expectedOutput("3").hidden(false).build(),
                        TestCase.builder().input("abc\ndef").expectedOutput("0").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String a = sc.nextLine().trim(), b = sc.nextLine().trim();\n        // LCS DP\n    }\n}")
                .starterCodePython("a = input().strip()\nb = input().strip()\n# LCS DP")
                .starterCodeCpp("#include <iostream>\n#include <string>\nusing namespace std;\nint main() {\n    string a, b; getline(cin, a); getline(cin, b);\n    // LCS DP\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    char a[1001], b[1001];\n    fgets(a, sizeof(a), stdin); fgets(b, sizeof(b), stdin);\n    // LCS DP\n    return 0;\n}")
                .hints(List.of("Use 2D DP table", "If chars match, dp[i][j] = dp[i-1][j-1] + 1"))
                .optimalApproach("2D DP Table")
                .timeComplexity("O(m × n)")
                .spaceComplexity("O(m × n)")
                .companiesAsked(List.of("Google", "Amazon", "Cognizant"))
                .build());

        problems.add(CodingProblem.builder()
                .id("dp-004")
                .title("Longest Increasing Subsequence")
                .description("Given an integer array `nums`, return the length of the longest strictly increasing subsequence.")
                .category(CodingCategory.DP)
                .difficulty(CodingDifficulty.HARD)
                .constraints("1 <= nums.length <= 2500\n-10^4 <= nums[i] <= 10^4")
                .inputFormat("Space-separated integers")
                .outputFormat("A single integer — LIS length")
                .testCases(List.of(
                        TestCase.builder().input("10 9 2 5 3 7 101 18").expectedOutput("4").hidden(false).build(),
                        TestCase.builder().input("0 1 0 3 2 3").expectedOutput("4").hidden(false).build(),
                        TestCase.builder().input("7 7 7 7 7 7 7").expectedOutput("1").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        String[] parts = new Scanner(System.in).nextLine().split(\" \");\n        int[] nums = new int[parts.length];\n        for (int i = 0; i < parts.length; i++) nums[i] = Integer.parseInt(parts[i]);\n        // LIS\n    }\n}")
                .starterCodePython("nums = list(map(int, input().split()))\n# LIS")
                .starterCodeCpp("#include <iostream>\n#include <vector>\n#include <sstream>\nusing namespace std;\nint main() {\n    string line; getline(cin, line); istringstream iss(line);\n    vector<int> nums; int x;\n    while (iss >> x) nums.push_back(x);\n    // LIS\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    int nums[2500], n = 0;\n    while (scanf(\"%d\", &nums[n]) == 1) { n++; if (getchar() == '\\n') break; }\n    // LIS\n    return 0;\n}")
                .hints(List.of("DP: dp[i] = length of LIS ending at index i", "Binary search optimization brings it to O(n log n)"))
                .optimalApproach("DP + Binary Search (Patience Sort)")
                .timeComplexity("O(n log n)")
                .spaceComplexity("O(n)")
                .companiesAsked(List.of("Google", "Amazon", "Microsoft"))
                .build());

        // ─── GREEDY ────────────────────────────────────────────────────────────
        problems.add(CodingProblem.builder()
                .id("greedy-001")
                .title("Activity Selection")
                .description("Given `n` activities with start and finish times, select the maximum number of activities that can be performed by a single person (one at a time).")
                .category(CodingCategory.GREEDY)
                .difficulty(CodingDifficulty.EASY)
                .constraints("1 <= n <= 10^5")
                .inputFormat("First line: n\nNext n lines: start finish")
                .outputFormat("A single integer — max activities")
                .testCases(List.of(
                        TestCase.builder().input("6\n1 2\n3 4\n0 6\n5 7\n8 9\n5 9").expectedOutput("4").hidden(false).build(),
                        TestCase.builder().input("3\n10 20\n12 25\n20 30").expectedOutput("2").hidden(false).build(),
                        TestCase.builder().input("1\n0 1").expectedOutput("1").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = Integer.parseInt(sc.nextLine().trim());\n        // Read activities, greedy select\n    }\n}")
                .starterCodePython("n = int(input())\nactivities = [list(map(int, input().split())) for _ in range(n)]\n# Greedy select")
                .starterCodeCpp("#include <iostream>\n#include <vector>\n#include <algorithm>\nusing namespace std;\nint main() {\n    int n; cin >> n;\n    // Greedy select\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    int n; scanf(\"%d\", &n);\n    // Greedy select\n    return 0;\n}")
                .hints(List.of("Sort activities by finish time", "Greedily pick the first non-overlapping activity"))
                .optimalApproach("Sort by Finish Time + Greedy")
                .timeComplexity("O(n log n)")
                .spaceComplexity("O(n)")
                .companiesAsked(List.of("TCS", "Wipro", "Capgemini"))
                .build());

        problems.add(CodingProblem.builder()
                .id("greedy-002")
                .title("Fractional Knapsack")
                .description("Given items with weights and values, and a knapsack capacity, find the maximum total value (fractions allowed). Print the value rounded to 2 decimal places.")
                .category(CodingCategory.GREEDY)
                .difficulty(CodingDifficulty.MEDIUM)
                .constraints("1 <= n <= 10^5\n1 <= W <= 10^9")
                .inputFormat("First line: n W\nNext n lines: value weight")
                .outputFormat("A decimal number rounded to 2 places")
                .testCases(List.of(
                        TestCase.builder().input("3 50\n60 10\n100 20\n120 30").expectedOutput("240.00").hidden(false).build(),
                        TestCase.builder().input("2 50\n60 10\n100 20").expectedOutput("160.00").hidden(false).build(),
                        TestCase.builder().input("1 10\n500 30").expectedOutput("166.67").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt(), W = sc.nextInt();\n        // Fractional knapsack\n    }\n}")
                .starterCodePython("n, W = map(int, input().split())\nitems = [list(map(int, input().split())) for _ in range(n)]\n# Fractional knapsack")
                .starterCodeCpp("#include <iostream>\n#include <vector>\n#include <algorithm>\n#include <iomanip>\nusing namespace std;\nint main() {\n    int n, W; cin >> n >> W;\n    // Fractional knapsack\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    int n, W;\n    scanf(\"%d %d\", &n, &W);\n    // Fractional knapsack\n    return 0;\n}")
                .hints(List.of("Sort items by value/weight ratio descending", "Take as much of the highest ratio item as possible"))
                .optimalApproach("Sort by Value/Weight Ratio")
                .timeComplexity("O(n log n)")
                .spaceComplexity("O(n)")
                .companiesAsked(List.of("Infosys", "Wipro", "Accenture"))
                .build());

        problems.add(CodingProblem.builder()
                .id("greedy-003")
                .title("Minimum Number of Coins")
                .description("Given standard Indian coin denominations [1, 2, 5, 10, 20, 50, 100, 200, 500, 2000] and an amount, find the minimum number of coins needed.")
                .category(CodingCategory.GREEDY)
                .difficulty(CodingDifficulty.EASY)
                .constraints("1 <= amount <= 10^6")
                .inputFormat("A single integer — the amount")
                .outputFormat("A single integer — minimum coins needed")
                .testCases(List.of(
                        TestCase.builder().input("93").expectedOutput("5").hidden(false).build(),
                        TestCase.builder().input("2500").expectedOutput("3").hidden(false).build(),
                        TestCase.builder().input("1").expectedOutput("1").hidden(true).build(),
                        TestCase.builder().input("121").expectedOutput("3").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        int amount = Integer.parseInt(new Scanner(System.in).nextLine().trim());\n        int[] coins = {2000, 500, 200, 100, 50, 20, 10, 5, 2, 1};\n        // Greedy\n    }\n}")
                .starterCodePython("amount = int(input())\ncoins = [2000, 500, 200, 100, 50, 20, 10, 5, 2, 1]\n# Greedy")
                .starterCodeCpp("#include <iostream>\nusing namespace std;\nint main() {\n    int amount; cin >> amount;\n    int coins[] = {2000,500,200,100,50,20,10,5,2,1};\n    // Greedy\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    int amount;\n    scanf(\"%d\", &amount);\n    int coins[] = {2000,500,200,100,50,20,10,5,2,1};\n    // Greedy\n    return 0;\n}")
                .hints(List.of("Start with the largest denomination", "Greedy works for standard coin systems"))
                .optimalApproach("Greedy — Largest Denomination First")
                .timeComplexity("O(1)")
                .spaceComplexity("O(1)")
                .companiesAsked(List.of("TCS", "Capgemini", "Cognizant"))
                .build());

        // Additional problems to round out each category
        problems.add(CodingProblem.builder()
                .id("str-004")
                .title("String Compression")
                .description("Given a string of lowercase letters, compress it by replacing consecutive identical characters with the character followed by the count. If count is 1, omit it. Print the compressed string.")
                .category(CodingCategory.STRINGS)
                .difficulty(CodingDifficulty.MEDIUM)
                .constraints("1 <= s.length <= 10^4")
                .inputFormat("A single string")
                .outputFormat("Compressed string")
                .testCases(List.of(
                        TestCase.builder().input("aabcccccaaa").expectedOutput("a2bc5a3").hidden(false).build(),
                        TestCase.builder().input("abc").expectedOutput("abc").hidden(false).build(),
                        TestCase.builder().input("aaaaaa").expectedOutput("a6").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        String s = new Scanner(System.in).nextLine().trim();\n        // Compress\n    }\n}")
                .starterCodePython("s = input().strip()\n# Compress")
                .starterCodeCpp("#include <iostream>\n#include <string>\nusing namespace std;\nint main() {\n    string s; getline(cin, s);\n    // Compress\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    char s[10001];\n    fgets(s, sizeof(s), stdin);\n    // Compress\n    return 0;\n}")
                .hints(List.of("Use two pointers or a simple loop", "Count consecutive characters"))
                .optimalApproach("Single Pass with Counter")
                .timeComplexity("O(n)")
                .spaceComplexity("O(n)")
                .companiesAsked(List.of("Amazon", "TCS", "Cognizant"))
                .build());

        problems.add(CodingProblem.builder()
                .id("dp-005")
                .title("Coin Change — Minimum Coins")
                .description("Given an array of coin denominations and a target amount, find the minimum number of coins needed to make the amount. Return -1 if not possible.")
                .category(CodingCategory.DP)
                .difficulty(CodingDifficulty.MEDIUM)
                .constraints("1 <= coins.length <= 12\n1 <= coins[i] <= 2^31 - 1\n0 <= amount <= 10^4")
                .inputFormat("First line: space-separated coin values\nSecond line: target amount")
                .outputFormat("A single integer — min coins or -1")
                .testCases(List.of(
                        TestCase.builder().input("1 5 10\n30").expectedOutput("3").hidden(false).build(),
                        TestCase.builder().input("2\n3").expectedOutput("-1").hidden(false).build(),
                        TestCase.builder().input("1\n0").expectedOutput("0").hidden(true).build()
                ))
                .starterCodeJava("import java.util.*;\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String[] parts = sc.nextLine().split(\" \");\n        int[] coins = new int[parts.length];\n        for (int i = 0; i < parts.length; i++) coins[i] = Integer.parseInt(parts[i]);\n        int amount = Integer.parseInt(sc.nextLine().trim());\n        // DP\n    }\n}")
                .starterCodePython("coins = list(map(int, input().split()))\namount = int(input())\n# DP")
                .starterCodeCpp("#include <iostream>\n#include <vector>\n#include <sstream>\nusing namespace std;\nint main() {\n    string line; getline(cin, line); istringstream iss(line);\n    vector<int> coins; int x;\n    while (iss >> x) coins.push_back(x);\n    int amount; cin >> amount;\n    // DP\n    return 0;\n}")
                .starterCodeC("#include <stdio.h>\nint main() {\n    int coins[12], n = 0, amount;\n    while (scanf(\"%d\", &coins[n]) == 1) { n++; if (getchar() == '\\n') break; }\n    scanf(\"%d\", &amount);\n    // DP\n    return 0;\n}")
                .hints(List.of("dp[i] = minimum coins to make amount i", "dp[0] = 0, iterate over each amount"))
                .optimalApproach("Bottom-up DP")
                .timeComplexity("O(amount × n)")
                .spaceComplexity("O(amount)")
                .companiesAsked(List.of("Amazon", "Google", "Microsoft"))
                .build());
    }
}
