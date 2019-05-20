from __future__ import division # Float division
import sys
import os
import subprocess # Popen
import random
import time
from compilationTools import *

atPercent = 10
executable = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/linked_challenge_pone_opt_"
results = []

def handlePerturbationPoint(i):
    global results
    insertPerturbationPoint(i)
    results[i].Success, results[i].Fail, results[i].Error = testPerturbationPoint(i)

    # if results[i].Bad:
    #     cleanFiles(i, True)
    # else:
    #     cleanFiles(i, False)
    print(results[i])
    print("")

def main():
    generatePerturbationType(atPercent, True)
    compileWhiteBoxToLLVM()

    number_of_perturbation_points = getNumberOfPerturbationPoints()
    print("Probability: " + str(atPercent))

    # Embedd the perturbation code inside the wb
    sp = subprocess.call("clang -c -emit-llvm " + path + "../perturbation_types/pone.c -o " + path + "../perturbation_types/pone.bc", shell=True)
    sp = subprocess.call("llvm-link " + path + "../perturbation_types/pone.bc " + path + "linked_challenge.bc -o " + path + "linked_challenge_pone.bc", shell=True)

    for i in range(0, number_of_perturbation_points, int(number_of_perturbation_points/100)): ## PerturbationPoint
        print(i)
        results.append(RESULT(0, 0, 0, atPercent, executable + str(i), i))
        handlePerturbationPoint(i)

    print("------------------------")
    printExperiemntResult(results)
    print(countBad)
    print("Fin")

main()
