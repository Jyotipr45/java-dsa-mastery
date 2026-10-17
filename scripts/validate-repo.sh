#!/usr/bin/env bash
# Checks repository structure: topic and problem naming, numbering,
# required files, stray build output and internal Markdown links.
# Usage: ./scripts/validate-repo.sh   (run from anywhere inside the repo)
# Exits with status 1 if any error is found.

set -u
cd "$(dirname "$0")/.." || exit 1

errors=0
warnings=0
error()   { echo "ERROR: $*"; errors=$((errors + 1)); }
warning() { echo "WARNING: $*"; warnings=$((warnings + 1)); }

DIFFICULTIES="Easy Medium Hard"

# 1. Top-level files that must exist
for f in README.md ROADMAP.md CONTRIBUTING.md Patterns/README.md JavaVersions/README.md DSA/README.md; do
  [ -f "$f" ] || error "missing required file: $f"
done

# 2. Topic folders: numbered in learning order, e.g. 04-LinkedList
seen_topic_numbers=""
for topic_dir in DSA/*/; do
  topic_dir=${topic_dir%/}
  topic=$(basename "$topic_dir")

  if ! [[ $topic =~ ^[0-9]{2}-[A-Z][A-Za-z]*$ ]]; then
    error "$topic_dir: topic folders must be a two-digit number, a hyphen and a CamelCase name, e.g. 04-LinkedList"
  else
    topic_number=${topic%%-*}
    if [[ " $seen_topic_numbers " == *" $topic_number "* ]]; then
      error "$topic_dir: topic number $topic_number is used by more than one topic"
    fi
    seen_topic_numbers="$seen_topic_numbers $topic_number"
  fi
  [ -f "$topic_dir/README.md" ] || error "$topic_dir: missing README.md"

  for d in "$topic_dir"/*/; do
    [ -d "$d" ] || continue
    name=$(basename "$d")
    [[ " $DIFFICULTIES " == *" $name "* ]] || error "$topic_dir/$name: unexpected folder; only Easy, Medium and Hard are allowed"
  done

  for difficulty in $DIFFICULTIES; do
    diff_dir="$topic_dir/$difficulty"
    if [ ! -d "$diff_dir" ]; then
      error "$diff_dir: missing difficulty folder"
      continue
    fi

    seen_numbers=""
    for problem_dir in "$diff_dir"/*/; do
      [ -d "$problem_dir" ] || continue
      problem_dir=${problem_dir%/}
      problem=$(basename "$problem_dir")

      if ! [[ $problem =~ ^[0-9]{3}-[a-z0-9]+(-[a-z0-9]+)*$ ]]; then
        error "$problem_dir: problem folders must look like 001-problem-name (three digits, lowercase, hyphens)"
        continue
      fi

      number=${problem%%-*}
      if [[ " $seen_numbers " == *" $number "* ]]; then
        error "$problem_dir: number $number is used by more than one problem in $diff_dir"
      fi
      seen_numbers="$seen_numbers $number"

      [ -f "$problem_dir/README.md" ]     || error "$problem_dir: missing README.md"
      [ -f "$problem_dir/Solution.java" ] || error "$problem_dir: missing Solution.java"
      for f in "$problem_dir/README.md" "$problem_dir/Solution.java"; do
        if [ -f "$f" ] && [ ! -s "$f" ]; then error "$f: file is empty"; fi
      done

      if [ -d "$problem_dir/versions" ]; then
        for v in "$problem_dir"/versions/*/; do
          [ -d "$v" ] || continue
          vname=$(basename "$v")
          [[ $vname =~ ^java-(8|11|17|21|25)$ ]] || error "$problem_dir/versions/$vname: must be java-8, java-11, java-17, java-21 or java-25 (LTS only)"
          [ -f "$v/Solution.java" ] || error "$problem_dir/versions/$vname: missing Solution.java"
        done
      fi
    done

    # A difficulty folder with problems should not keep its .gitkeep placeholder
    if [ -f "$diff_dir/.gitkeep" ] && [ -n "$seen_numbers" ]; then
      warning "$diff_dir: has problems, so .gitkeep can be removed"
    fi
  done
done

# 3. Stray build output
while IFS= read -r f; do
  error "$f: compiled .class files must not be committed"
done < <(find . -name '*.class' -not -path './.git/*')

# 4. Internal links and images in Markdown (templates contain placeholders and are skipped)
while IFS= read -r md; do
  dir=$(dirname "$md")
  # Extract targets of [text](target) and ![alt](target)
  grep -oE '\]\([^)]+\)' "$md" | sed -E 's/^\]\((.*)\)$/\1/' | while IFS= read -r target; do
    target=${target%% *}            # drop an optional "title"
    case "$target" in
      http://*|https://*|mailto:*|\#*) continue ;;
    esac
    path=${target%%#*}               # drop an #anchor
    [ -z "$path" ] && continue
    if [ ! -e "$dir/$path" ]; then
      echo "ERROR: $md: broken link or image: $target"
    fi
  done
done < <(find . -name '*.md' -not -path './.git/*' -not -path './templates/*' -not -path './.maintainer/*') > /tmp/validate-links.$$

link_errors=$(grep -c '^ERROR' /tmp/validate-links.$$ || true)
cat /tmp/validate-links.$$
rm -f /tmp/validate-links.$$
errors=$((errors + link_errors))

# Summary
echo
if [ "$errors" -eq 0 ]; then
  echo "Repository validation passed ($warnings warning(s))."
  exit 0
else
  echo "Repository validation failed: $errors error(s), $warnings warning(s)."
  exit 1
fi
