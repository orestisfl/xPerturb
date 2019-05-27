#!/usr/bin/python
from __future__ import division # Float division
import sys
import os
import subprocess # Popen
import random
from tqdm import tqdm # Progress bar
import time
import threading
#from concurrent.futures import ThreadPoolExecutor # ThreadPoolExecutor
from compilationTools import *


percentHops = 5
percentToInvestigate = 0.01
number_of_concurrent_threads = 5
search_space = 600 # In decimal percent
# number_of_inputs_to_try = 1000
# highpass = 90
atPercent = 100
# threshold = number_of_inputs_to_try * (100-highpass)/100
# activeThreads = 10
# countBad = 0
# countGood = 0
# stdoutAvalible = True
path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/"
executable = "linked_challenge"
# path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/"
results = {}
threadLock = threading.Lock()
threadQueue = []
# pool = ThreadPoolExecutor(max_workers = number_of_concurrent_threads)


class transformationThread (threading.Thread):
   def __init__(self, perturbationIndex, probability, exe, path):
      threading.Thread.__init__(self)
      self.PerturbationIndex = perturbationIndex
      self.Probability = probability
      self.Executable = exe
      self.Path = path
      self.NumberOfInputs = 1000

   def run(self):
       #print("\ngeneratePerturbationType(self.Probability, self.PerturbationIndex)")
       generatePerturbationType(self.Probability, self.PerturbationIndex)
       #print("\ninsertPerturbationProtocol(self.PerturbationIndex, self.Probability, self.Path)")
       insertPerturbationProtocol(self.PerturbationIndex, self.Probability, self.Path)
       #print("\ninsertPerturbationPoint(self.PerturbationIndex)")
       insertPerturbationPoint(self.PerturbationIndex, self.Path, self.Probability)
       #print("\ntestPerturbationPoint(self.PerturbationPoint)")
       succ_fail_err = testPerturbationPoint(self.PerturbationIndex, self.Path, self.Probability, self.NumberOfInputs)
       results[self.PerturbationIndex][self.Probability].Success = succ_fail_err[0]
       results[self.PerturbationIndex][self.Probability].Fail = succ_fail_err[1]
       results[self.PerturbationIndex][self.Probability].Error = succ_fail_err[2]
       #print("Thread done")
       cleanFiles(self.PerturbationIndex, self.Probability, self.Path, False)

def subset(seq, perc):
    return seq[::int(1/perc)]

def executeThreads():
    """ Execute a couple of threads simountaniusly, not all at once! """
    global threadQueue
    indexCounter = 0
    for indexCounter in tqdm(range(0, len(threadQueue), number_of_concurrent_threads)):
        # pass
        # while indexCounter < len(threadQueue):
        for i in range(0, number_of_concurrent_threads):
            if indexCounter + i == len(threadQueue):
                break
            threadQueue[indexCounter + i].start()
            time.sleep(0.050)
        for i in range(0, number_of_concurrent_threads):
            if indexCounter + i == len(threadQueue):
                break
            threadQueue[indexCounter + i].join()
        if indexCounter + i == len(threadQueue):
            return
        # indexCounter += number_of_concurrent_threads

def readFile(filename):
    # File with contents like: (25, 0.997, 4800)
    # (Probability, Correctness, PerturbationIndex)
    results = [] # List of Indecies
    fd = open(filename, "r")
    lines = fd.readlines()
    fd.close()
    for l in lines:
        results.append(int(eval(l.strip())[-1]))
    return results

def queuePerturbationThreads(points, percent, fixedPercent):
    for i in range(len(points)):
        results[points[i]] = {}

    if fixedPercent:
        for i in range(len(points)):
            p = percent
            results[points[i]][p] = RESULT(p, executable, path, points[i])
            threadQueue.append(transformationThread(points[i], p, executable, path))

    else:
        for i in range(len(points)):
            for p in range(0, percent, percentHops): # Probabilities to investigate
                results[points[i]][p] = RESULT(p, executable, path, points[i])
                threadQueue.append(transformationThread(points[i], p, executable, path))


def main():
    print("Probability: " + str(atPercent))

    #print("\ncompileWhiteBoxToLLVM(self.Probability, self.Path)")
    compileReferenceWhitebox(path)
    compileWhiteBoxToLLVM(path)

    points = [0]
    #points = range(0, 100)
    #pNum = getNumberOfPerturbationPoints(path)
    #points = subset(range(0, pNum), percentToInvestigate)
    #points = readFile("./experiment_results/interestingPoints.txt")

    queuePerturbationThreads(points, atPercent, False) ## Boolean to only investigate the percentage, not all percentages
    executeThreads()

    print("------------------------")
    writeToFile("./experiment_results/points.cvc", results)
    printExperiemntResult(results)
    print("Fin")

main()
