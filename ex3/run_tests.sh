#!/bin/bash

cd "$(dirname "$0")"

echo "Building project..."
make > /dev/null 2>&1
if [ $? -ne 0 ]; then
  echo "Build failed!"
  exit 1
fi
echo "Build successful."
echo ""

echo "Running all semantic analysis tests..."
echo ""

passed=0
failed=0

for i in 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20; do
  input_file=$(ls input/TEST_${i}_*.txt 2>/dev/null)
  expected_file=$(ls expected_output/TEST_${i}_*_Expected_Output.txt 2>/dev/null)

  if [ -z "$input_file" ] || [ -z "$expected_file" ]; then
    echo "TEST_${i}: SKIPPED (files not found)"
    continue
  fi

  java -jar SEMANT "$input_file" output/test_output.txt 2>/dev/null
  result=$(cat output/test_output.txt)
  expected=$(cat "$expected_file")

  if [ "$result" = "$expected" ]; then
    echo "TEST_${i}: PASS"
    ((passed++))
  else
    echo "TEST_${i}: FAIL (got '$result', expected '$expected')"
    ((failed++))
  fi
done

echo ""
echo "================================"
echo "Results: $passed passed, $failed failed"

