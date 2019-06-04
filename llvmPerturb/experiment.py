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

class Experiment():
    def __init__(self):
        self.Results = {}
        self.Jobs = []
        self.PerturbationPoints = []
        self.Path = None
        self.Title = ""
        self.Files = None
        self.PercentOfPointsToInvestigate = None
        self.NumberOfConcurrentJobs = 5
        self.Probabilities = []

    def setTitle(self, title):
        self.Title = title
    def setPath(self, path):
        self.Path = path
    def setFiles(self, exe):
        self.Files = exe
    def setProbabilities(self, lista):
        self.Probabilities = lista
    def setPercentOfPointsToInvestigate(self, p):
        self.PercentOfPointsToInvestigate = p

    def findAllPerturbationPoints(self):
        sp = subprocess.Popen(["opt", "-load", "/home/koski/llvm-8.0.0.src/build/lib/LLVMPerturbCount.so", "-Count", "-o", "/dev/null", self.Path + "/wb.ll"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = sp.communicate()
        print("Number of pp: " + str(int(out)))
        self.PerturbationPoints = range(0, int(out))

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

        for i in range(len(points)):
            for p in self.Probabilities: # Probabilities to investigate
                self.Results[points[i]][p] = RESULT(p, self.Title, self.Path, points[i])
                self.Jobs.append(Job(p, self.Files, self.Path, points[i], self.Results))

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
   def __init__(self, probability, f, path, perturbationIndex, res):
      threading.Thread.__init__(self)
      self.PerturbationIndex = perturbationIndex
      self.Probability = probability
      self.Files = f
      self.Path = path
      self.NumberOfInputs = 1000
      self.ResultsReference = res

   def run(self):
       #print("\ngeneratePerturbationType(self.Probability, self.PerturbationIndex)")
       ## generatePerturbationType(self.Probability, True) ## TODO Moove this outside of tread!, works bad if multiple wants to write and read at the same time!
       #print("\ninsertPerturbationProtocol(self.PerturbationIndex, self.Probability, self.Path)")
       T = Transformator()
       T.setSourceFolder(self.Path)
       T.setSourceFiles(["challenge.c", "chow_aes3_encrypt_wb.c"])
       T.setProbability(self.Probability)
       T.setPerturbationIndex(self.PerturbationIndex)
       T.setPerturbationType("PONE")

       T.insertPerturbationProtocol()
       #print("\ninsertPerturbationPoint(self.PerturbationIndex)")
       T.insertPerturbationPoint()
       #print("\ntestPerturbationPoint(self.PerturbationPoint)")

       R = Runner()
       succ_fail_err_act = R.testPerturbationPoint(self.Path, self.PerturbationIndex, self.Probability, self.NumberOfInputs)
       self.ResultsReference[self.PerturbationIndex][self.Probability].Success = succ_fail_err_act[0]
       self.ResultsReference[self.PerturbationIndex][self.Probability].Fail = succ_fail_err_act[1]
       self.ResultsReference[self.PerturbationIndex][self.Probability].Error = succ_fail_err_act[2]
       self.ResultsReference[self.PerturbationIndex][self.Probability].Activations = succ_fail_err_act[3]
       #print("Thread done")
       T.cleanFiles(False)


def main():
    print("Start!")
    #print("\ncompileWhiteBoxToLLVM(self.Probability, self.Path)")
    path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/src/"
    files = ["challenge.c", "chow_aes3_encrypt_wb.c"]
    probabilities = [5]
    percentOfPointsToInvestigate = 0.00005
    C = Compiler(path, files)
    C.compileWhiteBoxToLLVM()
    C.compileReferenceWhiteBox()
    for p in range(0, 101):
        C.generatePerturbationType(p, True)

    E = Experiment()
    E.setPath(path)
    E.setFiles(files)
    E.setTitle("Ches2016")
    E.findAllPerturbationPoints()
    # E.getPointsFromFile("filename.cvc")
    E.setPercentOfPointsToInvestigate(percentOfPointsToInvestigate)
    E.setProbabilities(probabilities)
    E.queuePerturbationJobs()
    E.executeJobs()
    E.saveExperiment()
    E.printExperiemntResults()

    print("Fin")

main()
