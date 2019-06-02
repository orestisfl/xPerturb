#!/usr/bin/python
from __future__ import division # Float division
import sys
import os
import subprocess # Popen
import random
from tqdm import tqdm # Progress bar
import time
import threading
from compilationTools import *

#
# percentHops = 5
# percentOfPointsToInvestigate = 0.01
# number_of_concurrent_threads = 5
# atPercent = 5
# path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/"
# executable = "linked_challenge"
# results = {}
# threadQueue = []

class Experiment():
    def __init__(self):
        self.Results = {}
        self.Jobs = []
        self.PerturbationPoints = []
        self.Path = None
        self.Executable = None
        self.PercentOfPointsToInvestigate = None
        self.NumberOfConcurrentJobs = 5
        self.Probabilities = []
    def setPath(self, path):
        self.Path = path
    def setExecutable(self, exe):
        self.Executable = exe
    def setProbabilities(self, lista):
        self.Probabilities = lista
    def setPercentOfPointsToInvestigate(self, p):
        self.PercentOfPointsToInvestigate = p
    def findAllPerturbationPoints(self):
        self.PerturbationPoints = range(0, getNumberOfPerturbationPoints(self.Path))
    def getPointsFromFile(self, filename):
        # File with contents like: (25, 0.997, 4800, 16)
        # (Probability, Correctness, PerturbationIndex, Activations)
        fd = open(filename, "r")
        lines = fd.readlines()
        fd.close()
        for l in lines:
            self.Results.append(int(eval(l.strip())[2]))
    def queuePerturbationJobs(self):
        points = self.PerturbationPoints[::int(1/self.PercentOfPointsToInvestigate)]
        for i in range(len(points)):
            self.Results[points[i]] = {}

        for pr in self.Probabilities:
            generatePerturbationType(pr, True) # TODO Move to a place that is more intuitive

        for i in range(len(points)):
            for p in self.Probabilities: # Probabilities to investigate
                self.Results[points[i]][p] = RESULT(p, self.Executable, self.Path, points[i])
                self.Jobs.append(Job(p, self.Executable, self.Path, points[i], self.Results))
    def executeJobs(self):
        # Execute a couple of threads simountaniusly, not all at once!
        indexCounter = 0
        for indexCounter in tqdm(range(0, len(self.Jobs), self.NumberOfConcurrentJobs)):
            for i in range(0, self.NumberOfConcurrentJobs):
                if indexCounter + i == len(self.Jobs):
                    break
                self.Jobs[indexCounter + i].start()
                time.sleep(0.050) # Neccesary to give a different seed to all the threads
            for i in range(0, self.NumberOfConcurrentJobs):
                if indexCounter + i == len(self.Jobs):
                    break
                self.Jobs[indexCounter + i].join()
            if indexCounter + i == len(self.Jobs):
                return
    def saveExperiment(self):
        if len(self.Probabilities) == 1:
            fn = "./experiment_results/points_p" + str(self.Probabilities[0]) + "_all.cvc"
        else:
            fn = "./experiment_results/points_px_top" + str(len(self.PerturbationPoints)) + ".cvc"
        fd = open(fn, "w")
        for i in self.Results.keys():
            for p in self.Results[i].keys():
                fd.write(self.Results[i][p].print_plottable_data())
                fd.write("\n")
        fd.close()
    def printExperiemntResults(self):
        for i in self.Results.keys():
            for p in self.Results[i].keys():
                print(self.Results[i][p])
                print("")



class Job (threading.Thread, Experiment):
   def __init__(self, probability, exe, path, perturbationIndex, res):
      threading.Thread.__init__(self)
      self.PerturbationIndex = perturbationIndex
      self.Probability = probability
      self.Executable = exe
      self.Path = path
      self.NumberOfInputs = 1000
      self.ResultsReference = res

   def run(self):
       #print("\ngeneratePerturbationType(self.Probability, self.PerturbationIndex)")
       ## generatePerturbationType(self.Probability, True) ## TODO Moove this outside of tread!, works bad if multiple wants to write and read at the same time!
       #print("\ninsertPerturbationProtocol(self.PerturbationIndex, self.Probability, self.Path)")
       insertPerturbationProtocol(self.PerturbationIndex, self.Probability, self.Path)
       #print("\ninsertPerturbationPoint(self.PerturbationIndex)")
       insertPerturbationPoint(self.PerturbationIndex, self.Path, self.Probability)
       #print("\ntestPerturbationPoint(self.PerturbationPoint)")
       succ_fail_err_act = testPerturbationPoint(self.PerturbationIndex, self.Path, self.Probability, self.NumberOfInputs)
       self.ResultsReference[self.PerturbationIndex][self.Probability].Success = succ_fail_err_act[0]
       self.ResultsReference[self.PerturbationIndex][self.Probability].Fail = succ_fail_err_act[1]
       self.ResultsReference[self.PerturbationIndex][self.Probability].Error = succ_fail_err_act[2]
       self.ResultsReference[self.PerturbationIndex][self.Probability].Activations = succ_fail_err_act[3]
       #print("Thread done")
       cleanFiles(self.PerturbationIndex, self.Probability, self.Path, False)


def main():
    print("Start!")
    #print("\ncompileWhiteBoxToLLVM(self.Probability, self.Path)")
    path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/"
    compileReferenceWhitebox(path)
    compileWhiteBoxToLLVM(path)

    E = Experiment()
    E.setPath(path)
    E.setExecutable("linked_challenge")
    E.findAllPerturbationPoints()
    # E.getPointsFromFile("filename.cvc")
    E.setPercentOfPointsToInvestigate(0.01)
    E.setProbabilities([5])
    E.queuePerturbationJobs()
    E.executeJobs()
    E.saveExperiment()
    E.printExperiemntResults()

    print("Fin")

main()
