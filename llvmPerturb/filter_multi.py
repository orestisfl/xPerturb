#!/usr/bin/python
from __future__ import division # Float division
import sys
import os
import subprocess # Popen
import random
#from tqdm import tqdm # Progress bar
import time
import threading
#from concurrent.futures import ThreadPoolExecutor # ThreadPoolExecutor
from compilationTools import *

number_of_threads = 4
search_space = 0.1
# number_of_inputs_to_try = 1000
# highpass = 90
atPercent = 10
# threshold = number_of_inputs_to_try * (100-highpass)/100
# activeThreads = 10
# countBad = 0
# countGood = 0
# stdoutAvalible = True
executable = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/linked_challenge_pone.bc"
# path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/"
results = {}
threadLock = threading.Lock()
threadQueue = []
# pool = ThreadPoolExecutor(max_workers = number_of_threads)


class transformationThread (threading.Thread):
   def __init__(self, threadID):
      threading.Thread.__init__(self)
      self.threadID = threadID
   def run(self):
      print("Starting job number " + str(self.threadID))
      handlePerturbationPoint(self.threadID)
      print("Exiting job number " + str(self.threadID))

def handlePerturbationPoint(i):
    global results
    insertPerturbationPoint(i)
    results[i].Success, results[i].Fail, results[i].Error = testPerturbationPoint(i)

    cleanFiles(i, False)

    print(results[i])
    print("")


def executeThreads():
    """ Execute a couple of threads simountaniusly, not all at once! """
    global threadQueue
    indexCounter = 0
    while indexCounter < len(threadQueue):
        for i in range(0, number_of_threads):
            if indexCounter + i == len(threadQueue):
                break
            threadQueue[indexCounter + i].start()
        for i in range(0, number_of_threads):
            if indexCounter + i == len(threadQueue):
                break
            threadQueue[indexCounter + i].join()
        if indexCounter + i == len(threadQueue):
            return
        indexCounter += number_of_threads

def main():
    generatePerturbationType(atPercent, True)
    compileWhiteBoxToLLVM()

    number_of_perturbation_points = getNumberOfPerturbationPoints()
    print("Probability: " + str(atPercent))

    # Embedd the perturbation code inside the wb
    sp = subprocess.call("clang -c -emit-llvm " + path + "../perturbation_types/pone.c -o " + path + "../perturbation_types/pone.bc", shell=True)
    sp = subprocess.call("llvm-link " + path + "../perturbation_types/pone.bc " + path + "linked_challenge.bc -o " + path + "linked_challenge_pone.bc", shell=True)

    for i in range(0, number_of_perturbation_points, int(number_of_perturbation_points*search_space)): ## number_of_perturbation_points PerturbationPoint
        results[i] = RESULT(0, 0, 0, atPercent, executable, i)
        threadQueue.append(transformationThread(i))

    executeThreads()

    print("------------------------")
    printExperiemntResult(results)
    print("Fin")

main()
