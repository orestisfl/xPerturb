from __future__ import division # Float division
import sys
import os
import subprocess # Popen
import random
import time
from compilationTools import *

atPercent = 50

countBad = 0
stdoutAvalible = True
executable = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/linked_challenge_pone.bc"
results = []

def handlePerturbationPoint(i):
    global countBad
    global results
    global lock
    insertPerturbationPoint(i)
    fails = 0
    print("insertPerturbationPoint done!")
    results[i].Success, results[i].Fail, results[i].Error = testPerturbationPoint(i)
    print("testPerturbationPoint done!")
    # for j in range(number_of_inputs_to_try): ## Inputs
    #     if results[i].Error + results[i].Fail > threshold:
    #         results[i].Bad = True
    #         #print(results[i])
    #         break
    #     testResult = testPerturbationPoint(i)
    #
    #     if testResult == "Error":
    #         results[i].Error = results[i].Error +1
    #     elif testResult == "Success":
    #         results[i].Success = results[i].Success + 1
    #     else:
    #         results[i].Fail = results[i].Fail + 1

    if results[i].Bad:
        cleanFiles(i, True)
    else:
        cleanFiles(i, False)
    print(results[i])
    print("")

def main():
    generatePerturbationType(atPercent, True)
    compileWhiteBoxToLLVM()
    ## Get number of perturbation points
    number_of_perturbation_points = getNumberOfPerturbationPoints()
    print("Probability: " + str(atPercent))

    # Embedd the perturbation code inside the wb
    sp = subprocess.call("clang -c -emit-llvm " + path + "../perturbation_types/pone.c -o " + path + "../perturbation_types/pone.bc", shell=True)
    sp = subprocess.call("llvm-link " + path + "../perturbation_types/pone.bc " + path + "linked_challenge.bc -o " + path + "linked_challenge_pone.bc", shell=True)

    for i in range(number_of_perturbation_points): ## PerturbationPoint
        results.append(RESULT(0, 0, 0, atPercent, executable, i))
        handlePerturbationPoint(i)

    printExperiemntResult(results)
    print(countBad)
    print("Fin")

main()
