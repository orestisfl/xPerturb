#!/bin/bash
cp -R "$HOME/xPerturb/llvmPerturb/llvm_src_folder/lib" "$HOME/llvm-8.0.0.src/"

cd "$HOME/llvm-8.0.0.src/build"

make LLVMRandom

echo ""
echo "---------------------------------------"
echo ""

cd "$HOME/xPerturb/llvmPerturb/example_programs"

opt -load "$HOME/llvm-8.0.0.src/build/lib/LLVMRandom.so" -Count < sum.bc > sum_opt.bc
llvm-dis sum_opt.bc
