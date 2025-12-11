#!/usr/bin/env bash
set -euo pipefail

EP_VERSION="2.41.0"
DATAFLOW_VERSION="3.42.0"

EP_JAR="tools/error_prone_core-$EP_VERSION-with-dependencies.jar"
DF_JAR="tools/dataflow-errorprone-$DATAFLOW_VERSION.jar"

# External jars needed for compilation
EXT_JARS="ex3/external_jars/java-cup-11b-runtime.jar"

mkdir -p ex3/bin

# Find all Java source files and write to a file
find ex3/src -name "*.java" > sources.txt

echo "Found $(wc -l < sources.txt) Java files to compile"

javac \
  -XDcompilePolicy=simple \
  -processorpath "$EP_JAR:$DF_JAR" \
  "-Xplugin:ErrorProne" \
  -cp "$EXT_JARS" \
  -d ex3/bin \
  --add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \
  --add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED \
  --add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED \
  --add-exports=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED \
  --add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED \
  --add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED \
  --add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
  --add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED \
  --add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED \
  --add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED \
  @sources.txt

echo "Error Prone compilation completed successfully"
