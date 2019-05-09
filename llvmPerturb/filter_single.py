from __future__ import division # Float division
import sys
import os
import subprocess # Popen
import random
from tqdm import tqdm # Progress bar
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
    for j in range(number_of_inputs_to_try): ## Inputs
        if results[i].Error + results[i].Fail > threshold:
            results[i].Bad = True
            #print(results[i])
            break
        testResult = testPerturbationPoint(i)

        if testResult == "Error":
            results[i].Error = results[i].Error +1
        elif testResult == "Success":
            results[i].Success = results[i].Success + 1
        else:
            results[i].Fail = results[i].Fail + 1

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
    for i in range(number_of_perturbation_points): ## PerturbationPoint
        results.append(RESULT(0, 0, 0, atPercent, executable, i))
        handlePerturbationPoint(i)

    printExperiemntResult(results)
    print(countBad)
    print("Fin")

main()
