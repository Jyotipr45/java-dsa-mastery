#!/usr/bin/env bash
# Compiles and runs every Solution.java on each installed Java LTS release
# (8, 11, 17, 21, 25) and compares the output with the "Expected output"
# block in the problem's README.md.
#
# Usage: ./scripts/validate-java.sh
#
# JDKs are found from JAVA_HOME_<N>_X64 (set by GitHub Actions setup-java),
# JAVA<N>_HOME, or /usr/lib/jvm/java-<N>-openjdk*. If none are found, the
# java and javac on PATH are used.
# Exits with status 1 if any solution fails to compile, run or match.

set -u
cd "$(dirname "$0")/.." || exit 1

LTS_VERSIONS="8 11 17 21 25"
RUN_TIMEOUT=10
failures=0
checked=0

find_jdk() {
  local v=$1 home=""
  local gh_var="JAVA_HOME_${v}_X64" plain_var="JAVA${v}_HOME"
  if [ -n "${!gh_var:-}" ]; then home=${!gh_var}
  elif [ -n "${!plain_var:-}" ]; then home=${!plain_var}
  else
    for d in /usr/lib/jvm/java-"$v"-openjdk* /usr/lib/jvm/temurin-"$v"-jdk*; do
      [ -x "$d/bin/javac" ] && { home=$d; break; }
    done
  fi
  [ -n "$home" ] && [ -x "$home/bin/javac" ] && echo "$home"
}

declare -A JDK
available=""
for v in $LTS_VERSIONS; do
  home=$(find_jdk "$v")
  if [ -n "$home" ]; then JDK[$v]=$home; available="$available $v"; fi
done

if [ -z "$available" ]; then
  if command -v javac >/dev/null 2>&1; then
    raw=$(javac -version 2>&1 | grep -oE '[0-9]+(\.[0-9]+)*' | head -1)
    major=${raw%%.*}; [ "$major" = "1" ] && major=$(echo "$raw" | cut -d. -f2)
    JDK[$major]=$(dirname "$(dirname "$(readlink -f "$(command -v javac)")")")
    available=" $major"
    echo "No LTS JDKs found in standard locations; using javac on PATH (Java $major)."
  else
    echo "ERROR: no JDK found. Install a JDK or set JAVA_HOME_<N>_X64."
    exit 1
  fi
fi
echo "Java versions available for testing:$available"
echo

unset JAVA_TOOL_OPTIONS _JAVA_OPTIONS
work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT

# Prints the first fenced block after a line starting with "Expected output"
expected_output() {
  awk '
    /^Expected output/ { want = 1; next }
    want && /^```/ { if (inblock) exit; inblock = 1; next }
    inblock { print }
  ' "$1"
}

# test_solution <source file> <minimum Java version> <README with expected output>
test_solution() {
  local src=$1 min=$2 readme=$3
  local expected="" have_expected=0
  if [ -f "$readme" ] && grep -q '^Expected output' "$readme"; then
    expected=$(expected_output "$readme"); have_expected=1
  fi

  for v in $available; do
    [ "$v" -lt "$min" ] && continue
    local home=${JDK[$v]} out="$work/$checked-$v"
    mkdir -p "$out"
    checked=$((checked + 1))

    # Canonical solutions must stay Java 8 compatible, even when compiled by a newer JDK
    local release=""
    [ "$min" -eq 8 ] && [ "$v" -gt 8 ] && release="--release 8"
    if ! "$home/bin/javac" $release -nowarn -d "$out" "$src" > "$out/compile.log" 2>&1; then
      echo "FAIL  $src  (Java $v: compile error)"
      sed 's/^/      /' "$out/compile.log"
      failures=$((failures + 1)); continue
    fi

    local actual
    if ! actual=$(cd "$out" && timeout "$RUN_TIMEOUT" "$home/bin/java" -cp . Solution 2>&1); then
      echo "FAIL  $src  (Java $v: runtime error or timeout)"
      echo "$actual" | sed 's/^/      /'
      failures=$((failures + 1)); continue
    fi

    if [ "$have_expected" -eq 1 ] && [ "$actual" != "$expected" ]; then
      echo "FAIL  $src  (Java $v: output does not match README)"
      diff <(echo "$expected") <(echo "$actual") | sed 's/^/      /'
      failures=$((failures + 1)); continue
    fi

    local note=""; [ "$have_expected" -eq 0 ] && note=" (no expected output in README)"
    echo "PASS  $src  (Java $v)$note"
  done
}

solutions=0
while IFS= read -r src; do
  solutions=$((solutions + 1))
  problem_dir=$(dirname "$src")
  if [[ $problem_dir == */versions/java-* ]]; then
    min=${problem_dir##*/java-}
    readme="$(dirname "$(dirname "$problem_dir")")/README.md"
  else
    min=8
    readme="$problem_dir/README.md"
  fi
  # Version-specific implementations must print the same output as the canonical one
  test_solution "$src" "$min" "$readme"
done < <(find DSA -name Solution.java | sort)

echo
if [ "$solutions" -eq 0 ]; then
  echo "No Solution.java files found yet. Nothing to test."
  exit 0
fi
if [ "$failures" -eq 0 ]; then
  echo "Java validation passed: $solutions solution file(s), $checked run(s) on Java$available."
  exit 0
else
  echo "Java validation failed: $failures of $checked run(s) failed."
  exit 1
fi
