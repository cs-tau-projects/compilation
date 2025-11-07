#!/bin/bash

###############
# DIRECTORIES #
###############
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
INPUT_DIR="${SCRIPT_DIR}/input"
OUTPUT_DIR="${SCRIPT_DIR}/output"
EXPECTED_OUTPUT_DIR="${SCRIPT_DIR}/expected_output"
LEXER_JAR="${SCRIPT_DIR}/LEXER"

#########
# COLORS #
#########
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

###########
# COUNTERS #
###########
total_tests=0
passed_tests=0
failed_tests=0

#############
# FUNCTIONS #
#############

# Function to compare two files
compare_files() {
    local output_file="$1"
    local expected_file="$2"
    
    if diff -q "$output_file" "$expected_file" > /dev/null 2>&1; then
        return 0  # Files are identical
    else
        return 1  # Files differ
    fi
}

# Function to run a single test
run_test() {
    local input_file="$1"
    local input_basename="$(basename "$input_file")"
    local output_file="${OUTPUT_DIR}/${input_basename}"
    
    # Determine expected output filename
    # Remove .txt extension and add _Expected_Output.txt
    local test_name="${input_basename%.txt}"
    local expected_file="${EXPECTED_OUTPUT_DIR}/${test_name}_Expected_Output.txt"
    
    # Check if expected output file exists
    if [ ! -f "$expected_file" ]; then
        echo -e "${YELLOW}SKIP${NC}   ${input_basename} (no expected output file)"
        return
    fi
    
    # Run the lexer
    java -jar "$LEXER_JAR" "$input_file" "$output_file" > /dev/null 2>&1
    
    # Check if output file was created
    if [ ! -f "$output_file" ]; then
        echo -e "${RED}FAIL${NC}   ${input_basename} (output file not created)"
        ((failed_tests++))
        ((total_tests++))
        return
    fi
    
    # Compare output with expected output
    if compare_files "$output_file" "$expected_file"; then
        echo -e "${GREEN}PASS${NC}   ${input_basename}"
        ((passed_tests++))
    else
        echo -e "${RED}FAIL${NC}   ${input_basename}"
        echo -e "       ${BLUE}Diff:${NC}"
        diff "$output_file" "$expected_file" | head -20 | sed 's/^/       /'
        ((failed_tests++))
    fi
    ((total_tests++))
}

########
# MAIN #
########

echo "========================================"
echo "  Running Lexer Tests"
echo "========================================"
echo ""

# Check if LEXER JAR exists
if [ ! -f "$LEXER_JAR" ]; then
    echo -e "${RED}ERROR:${NC} LEXER JAR not found at: $LEXER_JAR"
    echo "Please run 'make' first to build the LEXER"
    exit 1
fi

# Create output directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

# Find all .txt files in input directory and run tests
echo "Running tests..."
echo ""

for input_file in "$INPUT_DIR"/*.txt; do
    # Check if any .txt files exist
    if [ ! -e "$input_file" ]; then
        echo -e "${YELLOW}No test files found in ${INPUT_DIR}${NC}"
        exit 0
    fi
    
    run_test "$input_file"
done

echo ""
echo "========================================"
echo "  Test Summary"
echo "========================================"
echo -e "Total:  ${total_tests}"
echo -e "Passed: ${GREEN}${passed_tests}${NC}"
echo -e "Failed: ${RED}${failed_tests}${NC}"
echo "========================================"

# Exit with appropriate code
if [ $failed_tests -eq 0 ]; then
    echo -e "${GREEN}All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}Some tests failed.${NC}"
    exit 1
fi

