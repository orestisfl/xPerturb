#!/bin/bash

if [[ $1 != "run" ]]; then

  clang -S -emit-llvm chow_aes3_encrypt_wb.c
  clang -S -emit-llvm challenge.c

  llvm-link challenge.ll chow_aes3_encrypt_wb.ll -o linked_challenge.ll

  chmod +x linked_challenge.ll

fi

./linked_challenge.ll 00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f

echo "OUTPUT:    c1 bd 88 bf e6 5e 87 01 3f 3f 41 96 c1 8a f3 68 - EXPECTED"
