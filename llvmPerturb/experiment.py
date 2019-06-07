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
                self.Jobs.append(Job(p, self.Files, self.Path, points[i], self.Results, self.Title.lower()))

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
            fn = "/home/koski/xPerturb/llvmPerturb/experiment_results/" + self.Title.lower() + "_points_p" + str(self.Probabilities[0]) + "_all.cvc"
        else:
            fn = "/home/koski/xPerturb/llvmPerturb/experiment_results/" + self.Title.lower() + "_points_px_n" + str(len(self.PerturbationPoints)) + ".cvc"
        fd = open(fn, "w")
        for i in self.Results.keys():
            for p in self.Results[i].keys():
                fd.write(self.Results[i][p].print_plottable_data())
                fd.write("\n")
        fd.close()
    def printExperiemntResults(self):
        print("")
        for i in self.Results.keys():
            for p in self.Results[i].keys():
                print(self.Results[i][p])
                print("")



class Job (threading.Thread, Experiment):
   def __init__(self, probability, f, path, perturbationIndex, res, challenge_name):
      threading.Thread.__init__(self)
      self.PerturbationIndex = perturbationIndex
      self.Probability = probability
      self.Files = f
      self.Path = path
      self.NumberOfInputs = 1000
      self.ResultsReference = res
      self.R = Runner()


      if challenge_name == "ches2016":
          self.R.getRandomInput = self.input_ches2016
      elif challenge_name == "kryptologik":
          self.R.getRandomInput = self.input_kryptologik
      elif challenge_name == "nsc2013":
          self.R.getRandomInput = self.input_nsc2013
      else:
          raise ValueError("Wrong title on whitebox, could not find coresponding input setting for that white-box")

   def input_ches2016(self):
       ret = '{0:0{1}X}'.format(random.randint(0, 255),2)
       for i in range(15):
            ret = ret + " " + '{0:0{1}X}'.format(random.randint(0, 255),2)
       return ret
   def input_kryptologik(self):
       ret = '{0:0{1}X}'.format(random.randint(0, 255),2)
       for i in range(15):
           ret = ret + " " + '{0:0{1}X}'.format(random.randint(0, 255),2)
       return ret
   def input_nsc2013(self):
       ret = '{0:0{1}X}'.format(random.randint(0, 255),2)
       for i in range(15):
           ret = ret + '{0:0{1}X}'.format(random.randint(0, 255),2)
       return ret

   def run(self):
       #print("\ngeneratePerturbationType(self.Probability, self.PerturbationIndex)")
       ## generatePerturbationType(self.Probability, True) ## TODO Moove this outside of tread!, works bad if multiple wants to write and read at the same time!
       #print("\ninsertPerturbationProtocol(self.PerturbationIndex, self.Probability, self.Path)")
       T = Transformator()
       T.setSourceFolder(self.Path)
       T.setSourceFiles(self.Files)
       T.setProbability(self.Probability)
       T.setPerturbationIndex(self.PerturbationIndex)
       T.setPerturbationType("PONE")

       T.insertPerturbationProtocol()
       #print("\ninsertPerturbationPoint(self.PerturbationIndex)")
       T.insertPerturbationPoint()
       #print("\ntestPerturbationPoint(self.PerturbationPoint)")

       succ_fail_err_act = self.R.testPerturbationPoint(self.Path, self.PerturbationIndex, self.Probability, self.NumberOfInputs)
       self.ResultsReference[self.PerturbationIndex][self.Probability].Success = succ_fail_err_act[0]
       self.ResultsReference[self.PerturbationIndex][self.Probability].Fail = succ_fail_err_act[1]
       self.ResultsReference[self.PerturbationIndex][self.Probability].Error = succ_fail_err_act[2]
       self.ResultsReference[self.PerturbationIndex][self.Probability].Activations = succ_fail_err_act[3]
       #print("Thread done")
       T.cleanFiles(False)

def ches2016():
    print("Starting ches2016")
    #print("\ncompileWhiteBoxToLLVM(self.Probability, self.Path)")
    path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/src/"
    files = ["challenge.c", "chow_aes3_encrypt_wb.c"]
    probabilities = [5]
    percentOfPointsToInvestigate = 0.01
    C = Compiler(path, files)
    C.compileWhiteBoxToLLVM()
    # C.compileReferenceWhiteBox()

    # for p in range(0, 101):
    #     C.generatePerturbationType(p, True)

    E = Experiment()
    E.setPath(path)
    E.setFiles(files)
    E.setTitle("ches2016")
    E.findAllPerturbationPoints()
    # E.getPointsFromFile("filename.cvc")
    E.setPercentOfPointsToInvestigate(percentOfPointsToInvestigate)
    E.setProbabilities(probabilities)
    E.queuePerturbationJobs()
    E.executeJobs()
    E.saveExperiment()
    E.printExperiemntResults()

    print("Fin")

def kryptologik():
    print("Needs to be run from kryptologiks src folder")
    print("Starting Kryptologik")
    #print("\ncompileWhiteBoxToLLVM(self.Probability, self.Path)")
    path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_kryptologik/src/"
    files = ["DemoKey_table_encrypt.c"]
    probabilities = [5, 10, 50, 75, 90, 99]
    percentOfPointsToInvestigate = 0.01
    C = Compiler(path, files)
    C.compileWhiteBoxToLLVM()
    # C.compileReferenceWhiteBox()

    # for p in range(0, 101):
    #     C.generatePerturbationType(p, True)

    E = Experiment()
    E.setPath(path)
    E.setFiles(files)
    E.setTitle("kryptologik")
    E.findAllPerturbationPoints()
    # E.getPointsFromFile("filename.cvc")
    E.setPercentOfPointsToInvestigate(percentOfPointsToInvestigate)
    E.setProbabilities(probabilities)
    E.queuePerturbationJobs()
    E.executeJobs()
    E.saveExperiment()
    E.printExperiemntResults()

    print("Fin")

def nsc2013_variants_noenc_1():
    print("Starting nsc2013_variants_noenc_1")
    #print("\ncompileWhiteBoxToLLVM(self.Probability, self.Path)")
    path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_nsc2013_variants/src/"
    files = ["nosuchcon_2013_whitebox_noenc.c"]
    # Compile nosuchcon_2013_whitebox_noenc_generator.c separate
    probabilities = [5, 10, 50, 75, 90, 99]
    percentOfPointsToInvestigate = 1
    C = Compiler(path, files)
    C.compileWhiteBoxToLLVM()
    C.compileReferenceWhiteBox(["gcc -o " + path + "nosuchcon_2013_whitebox_noenc_generator " + path + "nosuchcon_2013_whitebox_noenc_generator.c",
    path + "nosuchcon_2013_whitebox_noenc_generator"])

    # for p in range(0, 101):
    #     C.generatePerturbationType(p, True)

    E = Experiment()
    E.setPath(path)
    E.setFiles(files)
    E.setTitle("nsc2013")
    E.findAllPerturbationPoints()
    # E.getPointsFromFile("filename.cvc")
    E.setPercentOfPointsToInvestigate(percentOfPointsToInvestigate)
    E.setProbabilities(probabilities)
    E.queuePerturbationJobs()
    E.executeJobs()
    E.saveExperiment()
    E.printExperiemntResults()

    print("Fin")

def main():
    nsc2013_variants_noenc_1()
    ches2016()
    kryptologik()
    # TODO Fix inpt generator set
main()
