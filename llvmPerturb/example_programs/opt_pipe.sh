#!/bin/bash
cp -R "$HOME/xPerturb/llvmPerturb/opt_passes/lib" "$HOME/llvm-8.0.0.src/"

cd "$HOME/llvm-8.0.0.src/build"

make

echo ""
echo "---------------------------------------"
echo ""

cd "$HOME/xPerturb/llvmPerturb/example_programs"

opt -load "$HOME/llvm-8.0.0.src/build/lib/LLVMRandom.so" -Count < sum.bc > /dev/null
