# Contributing

Thank you for helping improve this repository. Corrections, clearer explanations and new problems are all welcome. This guide explains how to contribute so that new material stays consistent with the rest of the repository.

The repository is maintained by Jyoti Prakash Parida ([@Jyotipr45](https://github.com/Jyotipr45)). Maintainers review every pull request before it is merged.

## Ways to contribute

- **Report a mistake.** Open an issue that names the file, quotes the incorrect part and explains what is wrong. Wrong complexity analysis, a dry run that does not match the code, or a solution that fails an edge case are all worth reporting.
- **Improve an explanation.** If something confused you, it probably confuses others too. A clearer sentence or a better example is a valuable contribution.
- **Add a problem.** Follow the steps below.
- **Add a diagram.** Diagrams are welcome when they make a concept easier to understand, not as decoration.

For anything larger than a small fix, please open an issue first so the approach can be agreed before you spend time on it.

## Adding a problem

1. **Check for duplicates.** Search the repository for the problem name and its source ID. Each problem has exactly one folder, even if it uses several patterns. Pattern guides link to it instead of copying it.
2. **Choose the folder.** Put it under the topic that best describes its main data structure or technique, in the difficulty folder that matches the official difficulty on its source platform.
3. **Name the folder.** Use the next unused three-digit number in that difficulty folder, followed by the problem name in lowercase words separated by hyphens:

   ```
   DSA/Array/Easy/003-move-zeroes/
   ```

   Numbering starts at `001` in each difficulty folder. Never renumber existing problems.
4. **Create the files.** Copy [templates/problem-template.md](templates/problem-template.md) to `README.md` and write `Solution.java`. A diagram named `explanation.webp` is optional.
5. **Update the indexes.** Add the problem to its topic README and to every pattern guide it uses.
6. **Validate.** Run the checks described below and fix anything they report.

## Writing standards

### Explanations

- Write problem statements in your own words. Link to the original source, but do not copy its full text or its editorial solution.
- Put the Pattern Recognition section first. Explain the clues in the problem that point to a technique, and why other techniques fit less well. The aim is to teach the reasoning, not just the answer.
- Make sure the dry run matches what the code actually does, step by step.
- Explain time and auxiliary space complexity separately, and justify each.
- Use simple English and short paragraphs. Avoid marketing language, decorative emojis and repeating what another guide already explains. Link to that guide instead.

### Java code

Every `Solution.java` must:

- Compile and run on its own with `javac Solution.java` and `java Solution`, with no external libraries.
- Contain a `public class Solution` with a `main` method that runs the algorithm on sample input and prints the result.
- Be Java 8 compatible by default, so it runs on every LTS release (Java 8, 11, 17, 21 and 25).
- Use descriptive names, handle edge cases and use `long` where an `int` could overflow.
- Comment the important decisions in the algorithm, not every line.

A version-specific implementation in a `versions/java-<N>/` folder is welcome only when a newer LTS release offers a meaningfully better way to write the solution. Do not add identical copies. Only LTS versions are used in this repository.

### Diagrams

- Use WebP for images and keep file sizes small.
- Create original diagrams. Do not use images you do not have permission to use.
- Explain each diagram in the surrounding text, and use the same terms as the documentation.
- Simple flowcharts can be written in Mermaid directly in Markdown instead.

## Running the checks

From the repository root:

```bash
./scripts/validate-repo.sh    # naming, numbering, required files, links, images
./scripts/validate-java.sh    # compiles and runs every Solution.java
```

Both scripts print a clear message for each problem they find and exit with a non-zero status if anything fails. The same checks run automatically on every pull request.

## Pull requests

- Keep each pull request focused on one problem or one fix.
- Describe what you changed and how you checked it.
- Make sure both validation scripts pass.

## Attribution

Problems from external platforms belong to those platforms and their authors. Always link to the original.
