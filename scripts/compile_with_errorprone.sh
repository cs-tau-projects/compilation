#!/usr/bin/env bash
set -euo pipefail

EP_VERSION="2.41.0"
DATAFLOW_VERSION="3.42.0-eisop4"

EP_JAR="tools/error_prone_core-$EP_VERSION-with-dependencies.jar"
DF_JAR="tools/dataflow-errorprone-$DATAFLOW_VERSION.jar"

# External jars needed for compilation
EXT_JARS="ex3/external_jars/java-cup-11b-runtime.jar"

mkdir -p ex3/bin

# Find all Java source files and write to a file
find ex3/src -name "*.java" > sources.txt

echo "Found $(wc -l < sources.txt) Java files to compile"

javac \
  -J--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \
  -J--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED \
  -J--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED \
  -J--add-exports=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED \
  -J--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED \
  -J--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED \
  -J--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
  -J--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED \
  -J--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED \
  -J--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED \
  -XDcompilePolicy=simple \
  -XDshould-stop.ifError=FLOW \
  -processorpath "$EP_JAR:$DF_JAR" \
  "-Xplugin:ErrorProne" \
  -cp "$EXT_JARS" \
  -d ex3/bin \
  @sources.txt

echo "Error Prone compilation completed successfully"
