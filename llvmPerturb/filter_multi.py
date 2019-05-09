from __future__ import division # Float division
import sys
import os
import subprocess # Popen
import random
from tqdm import tqdm # Progress bar
import time
from concurrent.futures import ThreadPoolExecutor # ThreadPoolExecutor
from compilationTools import *

number_of_threads = 5
# number_of_inputs_to_try = 1000
# highpass = 90
atPercent = 50
# threshold = number_of_inputs_to_try * (100-highpass)/100
# activeThreads = 10
# countBad = 0
# countGood = 0
# stdoutAvalible = True
executable = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/linked_challenge_pone.bc"
# path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/"
results = []

pool = ThreadPoolExecutor(max_workers = number_of_threads)


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
    compileWhiteBoxToLLVM()
    ## Get number of perturbation points
    number_of_perturbation_points = getNumberOfPerturbationPoints()
    print("Probability: " + str(atPercent))

    generatePerturbationType(atPercent, True)

    # Embedd the perturbation code inside the wb
    sp = subprocess.call("clang -c -emit-llvm " + path + "../perturbation_types/pone.c -o " + path + "../perturbation_types/pone.bc", shell=True)
    sp = subprocess.call("llvm-link " + path + "../perturbation_types/pone.bc " + path + "linked_challenge.bc -o " + path + "linked_challenge_pone.bc", shell=True)
    print("Number of threads: " + str(number_of_threads))
    for i in range(10000): ## number_of_perturbation_points PerturbationPoint
        results.append(RESULT(0, 0, 0, atPercent, executable, i))
        pool.submit(handlePerturbationPoint, i)
        #handlePerturbationPoint(i)
    pool.shutdown(wait=True)

    printExperiemntResult(results)
    print(countBad)
    print("Fin")

main()
