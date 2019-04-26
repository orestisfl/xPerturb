#!/bin/bash

set -e # Exit on first error

if [[ $1 == "compile" ]]; then
  cp -R "$HOME/xPerturb/llvmPerturb/llvm_src_folder/lib" "$HOME/llvm-8.0.0.src/"

  cd "$HOME/llvm-8.0.0.src/build"

  make LLVMRandom

  echo ""
  echo "---------------------------------------"
  echo ""

fi


if [[ $1 == "small" ]] || [[ $2 == "small" ]]; then
  cd "$HOME/xPerturb/llvmPerturb/example_programs/simple_sum"

  clang -S -emit-llvm ../perturbation_types/pone.c -o ../perturbation_types/pone.ll
  clang -S -emit-llvm sum.c
  llvm-link ../perturbation_types/pone.ll sum.ll -o sump.bc
  llvm-dis sump.bc -o sump.ll

  opt -load "$HOME/llvm-8.0.0.src/build/lib/LLVMRandom.so" -Random < sump.bc > sump_opt.bc

  llvm-dis sump_opt.bc
  chmod +x sump_opt.bc

  echo "6"
  ./sump_opt.bc

fi

if [[ "$1" == "big" ]] || [[ "$2" == "big" ]]; then
  cd "$HOME/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016"
  clang -S -emit-llvm chow_aes3_encrypt_wb.c
  clang -S -emit-llvm challenge.c
  llvm-link challenge.ll chow_aes3_encrypt_wb.ll -o linked_challenge.bc
  llvm-dis linked_challenge.bc -o linked_challenge.ll

  clang -S -emit-llvm ../perturbation_types/pone.c -o ../perturbation_types/pone.ll
  llvm-link ../perturbation_types/pone.ll linked_challenge.ll -o linked_challenge_pone.bc

  opt -load "$HOME/llvm-8.0.0.src/build/lib/LLVMRandom.so" -Random < linked_challenge_pone.bc > linked_challenge_pone_opt.bc

  echo "OUTPUT:    c1 bd 88 bf e6 5e 87 01 3f 3f 41 96 c1 8a f3 68 - EXPECTED"

  chmod +x linked_challenge.bc
  chmod +x linked_challenge_pone_opt.bc
  #./linked_challenge.bc 00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f
  ./linked_challenge_pone_opt.bc 00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f



fi
