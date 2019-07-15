#!/bin/bash

set -e # Exit on first error

if [[ $1 == "compile" ]]; then
  cp -R "$HOME/xPerturb/llvmPerturb/llvm_src_folder/lib" "$HOME/llvm-8.0.0.src/"

  cd "$HOME/llvm-8.0.0.src/build"

  make LLVMRandom
  #make LLVMPerturbCount

  echo ""
  echo "---------------------------------------"
  echo ""

fi


if [[ $1 == "small" ]] || [[ $2 == "small" ]]; then
  cd "$HOME/xPerturb/llvmPerturb/example_programs/simple_sum"


  # clang -S -emit-llvm sum.c
  llvm-link ../perturbation_types/pone_50.ll sum.ll -o sump.bc
  llvm-dis sump.bc -o sump.ll

  opt -load "$HOME/llvm-8.0.0.src/build/lib/LLVMRandom.so" -Random -pp 1 -o "sump_opt.bc" < sump.bc

  llvm-dis sump_opt.bc
  chmod +x sump_opt.bc

  echo "6"
  ./sump_opt.bc

fi

if [[ "$1" == "count" ]] || [[ "$2" == "count" ]]; then
  cd "$HOME/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/src"
  clang -S -emit-llvm chow_aes3_encrypt_wb.c
  clang -S -emit-llvm challenge.c
  llvm-link challenge.ll chow_aes3_encrypt_wb.ll -o linked_challenge.bc
  llvm-dis linked_challenge.bc -o linked_challenge.ll

  # clang -S -emit-llvm ../../perturbation_types/pone.c -o ../../perturbation_types/pone.ll
  llvm-link ../../perturbation_types/pone_10.ll linked_challenge.ll -o linked_challenge_pone.bc

  opt -load "$HOME/llvm-8.0.0.src/build/lib/LLVMPerturbCount.so" -Count -o "/dev/null" < linked_challenge_pone.bc
  echo "Done!"
  # echo "OUTPUT:    c1 bd 88 bf e6 5e 87 01 3f 3f 41 96 c1 8a f3 68 - EXPECTED"

  # chmod +x linked_challenge.bc
  # ./linked_challenge.bc 00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f


fi



if [[ "$1" == "big" ]] || [[ "$2" == "big" ]]; then
  cd "$HOME/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016"
  clang -S -emit-llvm src/chow_aes3_encrypt_wb.c
  clang -S -emit-llvm src/challenge.c
  llvm-link challenge.ll chow_aes3_encrypt_wb.ll -o linked_challenge.bc
  llvm-dis linked_challenge.bc -o linked_challenge.ll

  #clang -S -emit-llvm ../perturbation_types_backup/pone_0.c -o ../perturbation_types_backup/pone.ll
  llvm-link ../perturbation_types/pone_50.ll linked_challenge.ll -o linked_challenge_pone.bc

  opt -load "$HOME/llvm-8.0.0.src/build/lib/LLVMRandom.so" -Random -pp 0 < linked_challenge_pone.bc > linked_challenge_pone_opt.bc


  chmod +x linked_challenge.bc
  chmod +x linked_challenge_pone_opt.bc
  ./linked_challenge.bc 00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 00
  llc -o linked_challenge_pone_opt.s linked_challenge_pone_opt.bc
  gcc -o linked_challenge_pone_opt linked_challenge_pone_opt.s -no-pie
  chmod +x linked_challenge_pone_opt

  ./linked_challenge_pone_opt 00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 00
  #echo "OUTPUT:    c1 bd 88 bf e6 5e 87 01 3f 3f 41 96 c1 8a f3 68 - EXPECTED"

fi



if [[ "$1" == "nsc" ]] || [[ "$2" == "nsc" ]]; then
  cd "$HOME/xPerturb/llvmPerturb/example_programs/wbs_aes_nsc2013_variants"
  clang -S -emit-llvm -o linked_challenge.ll src/nosuchcon_2013_whitebox_noenc.c

  llvm-link ../perturbation_types/pone_10.ll linked_challenge.ll -o linked_challenge_pone.bc

  opt -load "$HOME/llvm-8.0.0.src/build/lib/LLVMRandom.so" -Random -pp 12 < linked_challenge_pone.bc > linked_challenge_pone_opt.bc


  chmod +x linked_challenge_pone_opt.bc

  #llc -o linked_challenge_pone_opt.s linked_challenge_pone_opt.bc
  #gcc -o linked_challenge_pone_opt linked_challenge_pone_opt.s -no-pie
  #chmod +x linked_challenge_pone_opt
  llvm-dis linked_challenge_pone_opt.bc
  ./linked_challenge_pone_opt.bc 000102030405060708090a0b0c0d0e00


fi
