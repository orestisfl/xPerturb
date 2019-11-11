#!/usr/bin/python
from __future__ import division # Float division
import sys
import os
import subprocess # Popen
import random
from tqdm import tqdm # Progress bar
import time
import threading
from compilationTools import * # Compiler, Transformator, Runner
from Result import *

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
                time.sleep(2) # Neccesary to give a different seed to all the threads
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
      elif challenge_name == "nsc2013 - noenc" or challenge_name == "nsc2013 - noextenc" or challenge_name == "nsc2013 - allenc":
          self.R.getRandomInput = self.input_nsc2013
      elif challenge_name == "nsc2013 - noenc - generator":
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
       T = Transformator()
       T.setSourceFolder(self.Path)
       T.setSourceFiles(self.Files)
       T.setProbability(self.Probability)
       T.setPerturbationIndex(self.PerturbationIndex)
       T.setPerturbationType("PONE")

       T.insertPerturbationProtocol()
       T.insertPerturbationPoint()
       T.cleanFiles(False)
       # The nsc whitebox need a bit different version of a runner then the other whiteboxes do
       if os.getcwd().endswith("wbs_aes_nsc2013_variants_generator/perturbations"):
           succ_fail_err_act = self.R.run_nsc_generator_version(self.Path, self.PerturbationIndex, self.Probability, self.NumberOfInputs)
       else:
           succ_fail_err_act = self.R.testPerturbationPoint(self.Path, self.PerturbationIndex, self.Probability, self.NumberOfInputs)
       self.ResultsReference[self.PerturbationIndex][self.Probability].Success = succ_fail_err_act[0]
       self.ResultsReference[self.PerturbationIndex][self.Probability].Fail = succ_fail_err_act[1]
       self.ResultsReference[self.PerturbationIndex][self.Probability].Error = succ_fail_err_act[2]
       self.ResultsReference[self.PerturbationIndex][self.Probability].Activations = succ_fail_err_act[3]
       print(self.ResultsReference[self.PerturbationIndex][self.Probability])



def ches2016():
    print("Starting ches2016")
    path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/src/"
    files = ["challenge.c", "chow_aes3_encrypt_wb.c"]
    probabilities = [5, 10, 50, 90, 99]
    percentOfPointsToInvestigate = 1 # Percentage in decimal (0.3 = 30%) of perturbation points to investigate
    C = Compiler(path, files)
    C.compileWhiteBoxToLLVM()
    C.compileReferenceWhiteBox(["gcc -o " + path + "wb_reference " + path + "challenge.c " + path + "chow_aes3_encrypt_wb.c"])
    for p in probabilities:
        C.generatePerturbationType(p, True)

    E = Experiment()
    E.setPath(path)
    E.setFiles(files)
    E.setTitle("ches2016")
    # E.findAllPerturbationPoints() # Uncomment if you want to search the whitebox for perturbation points
    E.PerturbationPoints = [48000, 11400, 19800, 11700, 28200, 16200, 24600, 7300, 59700, 10000]
    E.setPercentOfPointsToInvestigate(percentOfPointsToInvestigate)
    E.setProbabilities(probabilities)
    E.queuePerturbationJobs()
    E.executeJobs()
    E.saveExperiment()
    E.printExperiemntResults()
    print("Fin")

def kryptologik():
    if not os.getcwd().endswith("wbs_aes_kryptologik/src"):
        print("This experiment on Kryptologik needs to be run from within kryptologiks src folder")
        return
    print("Starting Kryptologik")
    path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_kryptologik/src/"
    files = ["DemoKey_table_encrypt.c"]
    probabilities = [5, 10, 50, 90, 99]
    percentOfPointsToInvestigate = 1 # Percentage in decimal (0.3 = 30%) of perturbation points to investigate
    C = Compiler(path, files)
    C.compileWhiteBoxToLLVM()
    C.compileReferenceWhiteBox(["gcc -o " + path + "wb_reference " + path + "DemoKey_table_encrypt.c"])
    for p in probabilities:
        C.generatePerturbationType(p, True)

    E = Experiment()
    E.setPath(path)
    E.setFiles(files)
    E.setTitle("kryptologik")
    # E.findAllPerturbationPoints() # Uncomment if you want to search the whitebox for perturbation points
    E.PerturbationPoints = [8100, 18900, 12000, 3600, 12300, 15000, 1800, 19800, 16200, 16900]
    E.setPercentOfPointsToInvestigate(percentOfPointsToInvestigate)
    E.setProbabilities(probabilities)
    E.queuePerturbationJobs()
    E.executeJobs()
    E.saveExperiment()
    E.printExperiemntResults()
    print("Fin")

def nsc2013_variants_noenc_gen():
    if not os.getcwd().endswith("wbs_aes_nsc2013_variants_generator/perturbations"):
        print("Must be run from within the nsc perturbation folder!")
        return
    print("Starting nsc2013_variants_noenc_2")
    path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_nsc2013_variants_generator/src/"
    files = ["nosuchcon_2013_whitebox_noenc_generator.c"]
    probabilities = [5, 10, 50, 90, 99]
    percentOfPointsToInvestigate = 1 # Percentage in decimal (0.3 = 30%) of perturbation points to investigate
    C = Compiler(path, files)
    C.compileWhiteBoxToLLVM()
    C.compileReferenceWhiteBox(["gcc -o " + path + "wb_reference " + path + "nosuchcon_2013_whitebox_noenc.c", "gcc -o " + path + "nosuchcon_2013_whitebox_noenc_generator " + path + "nosuchcon_2013_whitebox_noenc_generator.c",
    path + "nosuchcon_2013_whitebox_noenc_generator",
    "cp " + path + "wb_reference "+ path +"../perturbations/nosuchcon_2013_whitebox_noenc"])
    for p in probabilities:
        C.generatePerturbationType(p, True)

    E = Experiment()
    # OBS!!! Runner must be run in ingle thread due to the fact on how it stores lookuptables between LUT generation and LUT lokup operations
    E.NumberOfConcurrentJobs = 1
    E.setPath(path)
    E.setFiles(files)
    E.setTitle("nsc2013 - noenc - generator")
    E.findAllPerturbationPoints()
    E.setPercentOfPointsToInvestigate(percentOfPointsToInvestigate)
    E.setProbabilities(probabilities)
    E.queuePerturbationJobs()
    E.executeJobs()
    E.saveExperiment()
    E.printExperiemntResults()
    print("Fin")

def main():
    ches2016()
    kryptologik()
    nsc2013_variants_noenc_gen()

main()
