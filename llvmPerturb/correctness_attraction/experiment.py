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

    def findAllPerturbationPointsFirstRound(self, rounds):
        sp = subprocess.Popen(["opt", "-load", "/home/koski/llvm-8.0.0.src/build/lib/LLVMPerturbCount.so", "-Count", "-o", "/dev/null", self.Path + "/wb.ll"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = sp.communicate()
        print("Number of pp: " + str(int(out)))
        r1 = (int(out)/rounds) * 1.5
        self.PerturbationPoints = range(0, int(r1))

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
       ## generatePerturbationType(self.Probability, True) ## TODO Moove this outside of tread!, works bad if multiple wants to write and read at the same time!

       # T = Transformator()
       # T.setSourceFolder(self.Path)
       # T.setSourceFiles(self.Files)
       # T.setProbability(self.Probability)
       # T.setPerturbationIndex(self.PerturbationIndex)
       # T.setPerturbationType("PONE")
       #
       # T.insertPerturbationProtocol()
       # T.insertPerturbationPoint()
       # T.cleanFiles(False)
       # # succ_fail_err_act = self.R.run_nsc_generator_version(self.Path, self.PerturbationIndex, self.Probability, self.NumberOfInputs)
       succ_fail_err_act = self.R.testPerturbationPoint(self.Path, self.PerturbationIndex, self.Probability, self.NumberOfInputs)
       self.ResultsReference[self.PerturbationIndex][self.Probability].Success = succ_fail_err_act[0]
       self.ResultsReference[self.PerturbationIndex][self.Probability].Fail = succ_fail_err_act[1]
       self.ResultsReference[self.PerturbationIndex][self.Probability].Error = succ_fail_err_act[2]
       self.ResultsReference[self.PerturbationIndex][self.Probability].Activations = succ_fail_err_act[3]
       print(self.ResultsReference[self.PerturbationIndex][self.Probability])



def ches2016():
    print("Starting ches2016")
    #print("\ncompileWhiteBoxToLLVM(self.Probability, self.Path)")
    path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/src/"
    files = ["challenge.c", "chow_aes3_encrypt_wb.c"]
    probabilities = [50]
    percentOfPointsToInvestigate = 1
    # C = Compiler(path, files)
    # C.compileWhiteBoxToLLVM()
    # C.compileReferenceWhiteBox(["gcc -o " + path + "wb_reference " + path + "challenge.c " + path + "chow_aes3_encrypt_wb.c"])

    # for p in range(0, 101):
    #     C.generatePerturbationType(p, True)

    E = Experiment()
    E.setPath(path)
    E.setFiles(files)
    E.setTitle("ches2016")
    # E.findAllPerturbationPoints()
    # E.findAllPerturbationPointsFirstRound(10)
    E.PerturbationPoints = [48000, 11400, 19800, 11700, 28200, 16200, 24600, 7300, 59700, 10000]
    # E.PerturbationPoints = [8040, 4740, 1320, 4860, 2760, 960, 6860, 3060, 4020, 1080]
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
    probabilities = [50]
    percentOfPointsToInvestigate = 1
    C = Compiler(path, files)
    C.compileWhiteBoxToLLVM()
    C.compileReferenceWhiteBox(["gcc -o " + path + "wb_reference " + path + "DemoKey_table_encrypt.c"])

    # for p in range(0, 101):
    #     C.generatePerturbationType(p, True)

    E = Experiment()
    E.setPath(path)
    E.setFiles(files)
    E.setTitle("kryptologik")
    # E.findAllPerturbationPoints()
    # E.findAllPerturbationPointsFirstRound(14)
    E.PerturbationPoints = [1440, 1380, 480, 780, 180, 1740, 1620, 1800, 1560, 1320]
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
    percentOfPointsToInvestigate = 1.0
    C = Compiler(path, files)
    C.compileWhiteBoxToLLVM()
    C.compileReferenceWhiteBox(["gcc -o " + path + "nosuchcon_2013_whitebox_noenc_generator " + path + "nosuchcon_2013_whitebox_noenc_generator.c",
    path + "nosuchcon_2013_whitebox_noenc_generator"])

    # for p in range(0, 101):
    #     C.generatePerturbationType(p, True)

    E = Experiment()
    E.setPath(path)
    E.setFiles(files)
    E.setTitle("nsc2013 - noenc")
    E.findAllPerturbationPoints()
    # E.setPercentOfPointsToInvestigate(percentOfPointsToInvestigate)
    # E.setProbabilities(probabilities)
    # E.queuePerturbationJobs()
    # E.executeJobs()
    # E.saveExperiment()
    # E.printExperiemntResults()

    print("Fin")

def nsc2013_variants_noenc_2():
    print("Starting nsc2013_variants_noenc_2")
    print("Must be run frm the nsc perturbation folder!")
    path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_nsc2013_variants_generator/src/"
    files = ["nosuchcon_2013_whitebox_noenc_generator.c"]

    # Compile nosuchcon_2013_whitebox_noenc.c separate
    probabilities = [50]
    percentOfPointsToInvestigate = 1
    C = Compiler(path, files)
    C.compileWhiteBoxToLLVM()
    C.compileReferenceWhiteBox(["gcc -o " + path + "wb_reference " + path + "nosuchcon_2013_whitebox_noenc.c", "gcc -o " + path + "nosuchcon_2013_whitebox_noenc_generator " + path + "nosuchcon_2013_whitebox_noenc_generator.c",
    path + "nosuchcon_2013_whitebox_noenc_generator",
    "cp " + path + "wb_reference "+ path +"../perturbations/nosuchcon_2013_whitebox_noenc"])

    # for p in range(0, 101):
    #     C.generatePerturbationType(p, True)

    # OBS !! Korningsdelem (Runnern, Maste koras ien trad. Genereringen av perts kan koras i fler tradar)


    E = Experiment()
    E.NumberOfConcurrentJobs = 1
    E.setPath(path)
    E.setFiles(files)
    E.setTitle("nsc2013 - noenc - generator")
    # E.findAllPerturbationPoints()
    E.findAllPerturbationPointsFirstRound(10)
    E.PerturbationPoints = [5, 1, 6, 11, 13, 7, 0, 17, 12, 3]
    E.setPercentOfPointsToInvestigate(percentOfPointsToInvestigate)
    E.setProbabilities(probabilities)
    E.queuePerturbationJobs()
    E.executeJobs()
    E.saveExperiment()
    E.printExperiemntResults()

    print("Fin")



def main():
    ches2016()
    # kryptologik()
    # nsc2013_variants_noenc_1()
    # nsc2013_variants_noenc_2()

main()
