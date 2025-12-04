#!/bin/bash

# Test runner script for ex2
cd "$(dirname "$0")"

echo "Running all tests..."
echo "===================="

passed=0
failed=0

# Test files
tests=(
    "TEST_01_Print_Primes"
    "TEST_02_Bubble_Sort"
    "TEST_03_Merge_Lists"
    "TEST_04_Matrices"
    "TEST_05_Classes"
    "TEST_06_Print_Primes_Error"
    "TEST_07_Bubble_Sort_Error"
    "TEST_08_Merge_Lists_Error"
    "TEST_09_Matrices_Error"
    "TEST_10_Classes_Error"
)

for test in "${tests[@]}"; do
    input_file="input/${test}.txt"
    output_file="output/${test}_output.txt"
    expected_file="expected_output/${test}_Expected_Output.txt"
    
    echo -n "Testing ${test}... "
    
    # Run the parser
    java -jar PARSER "$input_file" "$output_file" > /dev/null 2>&1
    
    # Compare output
    if diff -q "$output_file" "$expected_file" > /dev/null 2>&1; then
        echo "✓ PASS"
        ((passed++))
    else
        echo "✗ FAIL"
        echo "  Expected: $(cat "$expected_file")"
        echo "  Got:      $(cat "$output_file")"
        ((failed++))
    fi
done

echo "===================="
echo "Results: $passed passed, $failed failed"

if [ $failed -eq 0 ]; then
    echo "All tests passed! ✓"
    exit 0
else
    echo "Some tests failed."
    exit 1
fi

