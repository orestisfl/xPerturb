#!/bin/bash
# Description: Run quick tests with perturbations on simple programs or entire whiteboxes
# Arguments:
# compile - After changes has been made to the perturbation inserter llvm optimizer pass. LLVM language needs to be recompiled. Compile does this
# test - Insert a 50% perturbation into the program simple_sum and run the perturbed program. Used for testing
# count - Count perturbation point in whitebox ches2016
# whitebox - Insert a 50% perturbation at perturbation point with index 0 and run the program.
#

set -e # Exit on first error

if [[ $1 == "compile" ]]; then
  cp -R "$HOME/xPerturb/llvmPerturb/llvm_src_folder/lib" "$HOME/llvm-8.0.0.src/"
  cd "$HOME/llvm-8.0.0.src/build"
  make LLVMRandom
  make LLVMPerturbCount
  echo ""
  echo "---------------------------------------"
  echo ""
fi


if [[ $1 == "test" ]] || [[ $2 == "test" ]]; then
  cd "$HOME/xPerturb/llvmPerturb/example_programs/simple_sum"
  clang -S -emit-llvm sum.c
  llvm-link ../perturbation_types/pone_50.ll sum.ll -o sump.bc
  llvm-dis sump.bc -o sump.ll
  opt -load "$HOME/llvm-8.0.0.src/build/lib/LLVMRandom.so" -Random -pp 1 -o "sump_opt.bc" < sump.bc
  llvm-dis sump_opt.bc
  chmod +x sump_opt.bc
  ./sump_opt.bc
fi

if [[ "$1" == "count" ]] || [[ "$2" == "count" ]]; then
  cd "$HOME/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/src"
  clang -S -emit-llvm chow_aes3_encrypt_wb.c
  clang -S -emit-llvm challenge.c
  llvm-link challenge.ll chow_aes3_encrypt_wb.ll -o linked_challenge.bc
  opt -load "$HOME/llvm-8.0.0.src/build/lib/LLVMPerturbCount.so" -Count -o "/dev/null" < linked_challenge.bc
fi

if [[ "$1" == "whitebox" ]] || [[ "$2" == "whitebox" ]]; then
  cd "$HOME/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/src"
  clang -S -emit-llvm src/chow_aes3_encrypt_wb.c
  clang -S -emit-llvm src/challenge.c
  llvm-link challenge.ll chow_aes3_encrypt_wb.ll -o linked_challenge.bc
  llvm-dis linked_challenge.bc -o linked_challenge.ll
  clang -S -emit-llvm ../perturbation_types/pone_50.c -o ../perturbation_types/pone_50.ll
  llvm-link ../perturbation_types/pone_50.ll linked_challenge.ll -o linked_challenge_pone.bc
  opt -load "$HOME/llvm-8.0.0.src/build/lib/LLVMRandom.so" -Random -pp 0 < linked_challenge_pone.bc > linked_challenge_pone_opt.bc

  chmod +x linked_challenge.bc
  chmod +x linked_challenge_pone_opt.bc
  llc -o linked_challenge_pone_opt.s linked_challenge_pone_opt.bc
  gcc -o linked_challenge_pone_opt linked_challenge_pone_opt.s -no-pie
  chmod +x linked_challenge_pone_opt

  ./linked_challenge_pone_opt 00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 00
  echo "OUTPUT:    c1 bd 88 bf e6 5e 87 01 3f 3f 41 96 c1 8a f3 68 - EXPECTED"
fi
