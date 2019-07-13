#!/bin/bash

set -e # Exit on first error

time python attack.py

time daredevil -c mem_addr1_rw1_2000_32400.attack_sbox.config
