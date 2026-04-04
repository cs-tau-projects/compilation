#!/usr/bin/env bash
# ============================================================
#  run_tests.sh  –  build & test the COMPILER
#
#  Usage:  bash run_tests.sh
#
#  Pipeline per test:
#    1. java -jar COMPILER input.txt  →  output.asm
#    2. spim -file output.asm         →  actual_output.txt
#    3. diff actual_output.txt        vs  expected_output.txt
#
#  If the compiler exits non-zero (error-case tests), its
#  stderr is used as the actual output instead.
#
#  Output:
#    • Per-test [PASS] / [FAIL] printed to the terminal
#    • diff_report.txt – full unified diff for every test
# ============================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
INPUT_DIR="${SCRIPT_DIR}/input"
EXPECTED_DIR="${SCRIPT_DIR}/expected_output"
ACTUAL_DIR="${SCRIPT_DIR}/output"
DIFF_REPORT="${SCRIPT_DIR}/diff_report.txt"
COMPILER="${SCRIPT_DIR}/COMPILER"

# ── Colours ──────────────────────────────────────────────────
GREEN='\033[0;32m'
RED='\033[0;31m'
CYAN='\033[0;36m'
BOLD='\033[1m'
RESET='\033[0m'

# ── Step 1: Build ─────────────────────────────────────────────
echo -e "${BOLD}${CYAN}============================================================${RESET}"
echo -e "${BOLD}${CYAN}  Building with make ...${RESET}"
echo -e "${BOLD}${CYAN}============================================================${RESET}"

if make -C "${SCRIPT_DIR}" compile; then
    echo -e "${GREEN}Build succeeded.${RESET}\n"
else
    echo -e "${RED}Build FAILED. Aborting tests.${RESET}"
    exit 1
fi

mkdir -p "${ACTUAL_DIR}"

# ── Check for spim ────────────────────────────────────────────
if ! command -v spim &>/dev/null; then
    echo -e "${RED}ERROR: 'spim' not found in PATH. Cannot execute MIPS assembly.${RESET}"
    exit 1
fi

# ── Step 2: Run tests ─────────────────────────────────────────
passed=0
failed=0
total=0

# Clear (or create) the diff report
{
    echo "============================================================"
    echo "  DIFF REPORT  --  $(date)"
    echo "============================================================"
} > "${DIFF_REPORT}"

echo -e "${BOLD}${CYAN}============================================================${RESET}"
echo -e "${BOLD}${CYAN}  Running tests ...${RESET}"
echo -e "${BOLD}${CYAN}============================================================${RESET}"

for input_file in "${INPUT_DIR}"/TEST_*.txt; do
    test_basename="$(basename "${input_file}" .txt)"   # e.g. TEST_01_Print_Primes

    # ── Locate the matching expected output ──────────────────
    expected_file=""
    for candidate in \
        "${EXPECTED_DIR}/${test_basename}_Expected_Output.txt" \
        "${EXPECTED_DIR}/${test_basename%_*}_Expected_Output.txt"
    do
        if [[ -f "${candidate}" ]]; then
            expected_file="${candidate}"
            break
        fi
    done

    # Fallback: glob-search using numeric prefix (e.g. TEST_18)
    if [[ -z "${expected_file}" ]]; then
        num_prefix="${test_basename%%_*}_$(echo "${test_basename}" | cut -d_ -f2)"
        matches=( "${EXPECTED_DIR}/${num_prefix}"*_Expected_Output.txt )
        if [[ -f "${matches[0]}" ]]; then
            expected_file="${matches[0]}"
        fi
    fi

    if [[ -z "${expected_file}" ]]; then
        echo -e "  ${RED}[SKIP]${RESET}  ${test_basename}  (no expected output file found)"
        continue
    fi

    asm_file="${ACTUAL_DIR}/${test_basename}.asm"
    actual_file="${ACTUAL_DIR}/${test_basename}_Actual_Output.txt"
    compiler_stderr="${ACTUAL_DIR}/${test_basename}_compiler_stderr.txt"
    total=$(( total + 1 ))

    # ── Run the compiler ──────────────────────────────────────
    if ! java -jar "${COMPILER}" "${input_file}" "${asm_file}" 2>"${compiler_stderr}"; then
        # Compiler failed with non-zero exit – use stderr as the actual output
        cp "${compiler_stderr}" "${actual_file}"
    else
        # Compiler succeeded (exit 0)
        # Check if the output is actually an error message (like TEST_26 or Lexer ERROR) instead of MIPS Assembly
        if head -n 1 "${asm_file}" | grep -qE "Register Allocation Failed|^ERROR"; then
            cp "${asm_file}" "${actual_file}"
        else
            # Execute the assembly through SPIM
            spim -file "${asm_file}" > "${actual_file}" 2>&1 || true
        fi
    fi

    # ── Compare output ────────────────────────────────────────
    if diff -q -b "${expected_file}" "${actual_file}" > /dev/null 2>&1; then
        echo -e "  ${GREEN}[PASS]${RESET}  ${test_basename}"
        passed=$(( passed + 1 ))
        {
            echo ""
            echo "------------------------------------------------------------"
            echo "  TEST: ${test_basename}"
            echo "  STATUS: PASS"
            echo "------------------------------------------------------------"
        } >> "${DIFF_REPORT}"
    else
        echo -e "  ${RED}[FAIL]${RESET}  ${test_basename}"
        failed=$(( failed + 1 ))
        {
            echo ""
            echo "------------------------------------------------------------"
            echo "  TEST: ${test_basename}"
            echo "  STATUS: FAIL"
            echo "  DIFF (expected vs actual):"
            echo "------------------------------------------------------------"
            diff --unified=3 "${expected_file}" "${actual_file}" || true
        } >> "${DIFF_REPORT}"
    fi
done

# ── Step 3: Summary ───────────────────────────────────────────
echo ""
echo -e "${BOLD}${CYAN}============================================================${RESET}"
printf "${BOLD}  Results: ${GREEN}%d passed${RESET}${BOLD}, ${RED}%d failed${RESET}${BOLD}, %d total${RESET}\n" \
    "${passed}" "${failed}" "${total}"
echo -e "${BOLD}${CYAN}============================================================${RESET}"
echo -e "  Diff report written to: ${DIFF_REPORT}"
echo ""

if [[ "${failed}" -gt 0 ]]; then
    exit 1
fi
