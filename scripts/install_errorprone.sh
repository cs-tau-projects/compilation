#!/usr/bin/env bash
set -euo pipefail

EP_VERSION="2.41.0"                 # recent; requires JDK 17+
DATAFLOW_VERSION="3.42.0"           # works with EP on modern JDKs

mkdir -p tools
cd tools

base="https://repo1.maven.org/maven2"

# Single fat jar (with dependencies) recommended for CLI usage
curl -sSLO "$base/com/google/errorprone/error_prone_core/$EP_VERSION/error_prone_core-$EP_VERSION-with-dependencies.jar"

# Required since JDK 16+ encapsulates internals; EP relies on dataflow
curl -sSLO "$base/io/github/eisop/dataflow-errorprone/$DATAFLOW_VERSION/dataflow-errorprone-$DATAFLOW_VERSION.jar"
