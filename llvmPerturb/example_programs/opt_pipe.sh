#!/bin/bash
cp -R "$HOME/xPerturb/llvmPerturb/llvm_src_folder/lib" "$HOME/llvm-8.0.0.src/"

cd "$HOME/llvm-8.0.0.src/build"

make LLVMRandom

echo ""
echo "---------------------------------------"
echo ""

cd "$HOME/xPerturb/llvmPerturb/example_programs/simple_sum"

clang -S -emit-llvm pone.c
clang -S -emit-llvm sum.c
llvm-link pone.ll sum.ll -o sump.bc
llvm-dis sump.bc -o sump.ll

opt -load "$HOME/llvm-8.0.0.src/build/lib/LLVMRandom.so" -Random < sump.bc > sump_opt.bc

llvm-dis sump_opt.bc
