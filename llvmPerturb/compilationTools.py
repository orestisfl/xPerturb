from __future__ import division # Float division
import sys
import os
import subprocess # Popen
import random


number_of_inputs_to_try = 1000

class RESULT:
    def __init__(self, pr, e, path, pe):
        self.Success = 0
        self.Fail = 0
        self.Error = 0
        self.Activations = 0
        self.Probability = pr
        self.Executable = e
        self.PerturbationPoint = pe
        self.Path = path

    def __str__(self):
        return"""Executable: %s
Correctness %1.3f
Success: %d
Fails: %d
Errors: %d
Index: %d
Percent: %d
Activations: %1.3f""" %(
        self.Executable,
        self.get_correctness(),
        self.Success,
        self.Fail,
        self.Error,
        self.PerturbationPoint,
        self.Probability,
        self.Activations
        )

    def get_correctness(self):
        try:
            return self.Success/(self.Fail + self.Error + self.Success)
        except ZeroDivisionError:
            return -1


    def print_plottable_data(self):
        return str(self.Probability) + ", " + str(self.get_correctness()) + ", " + str(self.PerturbationPoint) + ", " + str(self.Activations)

class Compiler():
    def __init__(self, sfo, sfi):
        self.SourceFolder = sfo
        self.SourceFiles = sfi

    def changeFileTypeTo(self, src, ending):
        dotIndex = src.index(".")
        return src[:dotIndex] + "." + ending

    def compileWhiteBoxToLLVM(self):
        cmds = []
        if len(self.SourceFiles) == 2:
            for i in range(len(self.SourceFiles)):
                cmds.append("clang -S -emit-llvm " + self.SourceFolder + self.SourceFiles[i] + " -o " + self.SourceFolder + self.changeFileTypeTo(self.SourceFiles[i], "ll"))
            cmds.append("llvm-link " + self.SourceFolder + self.changeFileTypeTo(self.SourceFiles[0], "ll") + " " + self.SourceFolder + self.changeFileTypeTo(self.SourceFiles[1], "ll") + " -o " + self.SourceFolder + "linked_challenge.bc")
            cmds.append("llvm-dis " + self.SourceFolder + "linked_challenge.bc -o " + self.SourceFolder + "wb.ll")
        elif len(self.SourceFiles) == 1:
            cmds.append("clang -S -emit-llvm " + self.SourceFolder + self.SourceFiles[0] + " -o " + self.SourceFolder + "wb.ll")
        else:
            raise ValueError("To many source files to handle!")
        for cmd in cmds:
            sp = subprocess.call(cmd, shell=True)

    def compileReferenceWhiteBox(self):
        pass

    def generatePerturbationType(self, prob, plus):
        # Generate the apropriate perturbation function with set probability and pp-model
        d = {}
        d['probability'] = prob
        if plus:
            d['minus'] = ""
        else:
            d['minus'] = "-"

        with open("perturbation_templates/pm_one.tmp", 'r') as ftemp:
            templateString = ftemp.read()
        with open("example_programs/perturbation_types/pone_" + str(prob) + ".c", 'w') as f:
            f.write(templateString.format(**d))

        with open("perturbation_templates/pm_oneh.tmp", 'r') as ftemp:
            templateString = ftemp.read()
        with open("example_programs/perturbation_types/pone_"+ str(prob) +".h", 'w') as f:
            f.write(templateString)

        cmd0 = "clang -S -emit-llvm example_programs/perturbation_types/pone_"+ str(prob) +".c -o example_programs/perturbation_types/pone_"+ str(prob) +".ll"
        # cmd0 = " ".join(["gcc", "-c", "example_programs/perturbation_types/pone_"+ str(prob) +".c", "-o", "example_programs/perturbation_types/pone_"+ str(prob) +".o" ])
        sp = subprocess.call(cmd0, shell=True)


class Transformator():
    def __init__(self):
        self.SourceFolder = ""
        self.SourceFiles = []
        self.Probability = None
        self.PerturbationIndex = None
        self.PerturbationType = ""
        self.Output = "wb"

    def setSourceFolder(self, i):
        self.SourceFolder = i
    def setSourceFiles(self, i):
        if type(i) != type([]):
            raise ValueError("Source files must be a list of strings or list of string")
        self.SourceFiles = i
    def setProbability(self, i):
        self.Probability = i
    def setPerturbationIndex(self, i):
        self.PerturbationIndex = i
    def setPerturbationType(self, i):
        self.PerturbationType = i

    def insertPerturbationProtocol(self):
        cmds = [None] * 1
        cmds[0] = "llvm-link example_programs/perturbation_types/pone_"+ str(self.Probability) +".ll " + self.SourceFolder + "wb.ll -o " + self.SourceFolder + "../perturbations/wb_p_" + str(self.Probability) +".bc"
        for cmd in cmds:
            sp = subprocess.call(cmd, shell=True)

    def insertPerturbationPoint(self):
        cmds = [None] * 4
        cmds[0] = "opt -load \"/home/koski/llvm-8.0.0.src/build/lib/LLVMRandom.so\" -Random -pp " + str(self.PerturbationIndex) + " -o " + self.SourceFolder + "../perturbations/wb_p_" + str(self.Probability) +"_"+ str(self.PerturbationIndex) + ".bc" + " < " + self.SourceFolder + "../perturbations/wb_p_"+ str(self.Probability) +".bc"
        cmds[1] = "llc -o " + self.SourceFolder + "../perturbations/wb_p_" + str(self.Probability) + "_" + str(self.PerturbationIndex) + ".s " + self.SourceFolder + "../perturbations/wb_p_" + str(self.Probability) + "_" + str(self.PerturbationIndex) + ".bc"
        cmds[2] = "gcc -o " + self.SourceFolder + "../perturbations/wb_p_" + str(self.Probability) + "_" + str(self.PerturbationIndex) + " " + self.SourceFolder + "../perturbations/wb_p_" + str(self.Probability) + "_" + str(self.PerturbationIndex) + ".s -no-pie"
        cmds[3] = "chmod +x " + self.SourceFolder + "../perturbations/wb_p_" + str(self.Probability) +"_"+ str(self.PerturbationIndex)
        for cmd in cmds:
            sp = subprocess.call(cmd, shell=True)

    def cleanFiles(self, delete_all): ## TODO Denna lirar inte med den nya strukturen
        cmds = []
        cmds.append("rm " + self.SourceFolder + "../perturbations/wb_p_" + str(self.Probability) + "_" + str(self.PerturbationIndex) + ".bc")
        cmds.append("rm " + self.SourceFolder + "../perturbations/wb_p_" + str(self.Probability) + "_" + str(self.PerturbationIndex) + ".s")
        cmds.append("rm " + self.SourceFolder + "../perturbations/wb_p_" + str(self.Probability) + ".bc")
        if delete_all:
            cmds.append("rm " + self.SourceFolder + "../perturbations/wb_p_" + str(self.Probability))

        for i in cmds:
            out_opt, err_opt = subprocess.Popen(i, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()



class Runner():
    def __init__(self):
        self.Source = ""
        self.logs = ""
        self.NumberOfInputs = 1000

    def getRandomInput(self):
        # Random generated input tailored for chow2016 challenge
        ret = '{0:0{1}X}'.format(random.randint(0, 255),2)
        for i in range(15):
            ret = ret + " " + '{0:0{1}X}'.format(random.randint(0, 255),2)
        return ret

    def countPerturbations(self, logs):
        onces = 0
        zeros = 0
        for line in logs.split("\n"):
            try:
                if line[:4] == "PP: ":
                    if int(line.strip()[-1]):
                        onces += 1
                    else:
                        zeros +=1
            except:
                continue

        return onces + zeros
    def testPerturbationPoint(self, path, i, prob, numberOfInputs):
        success = 0
        fail = 0
        error = 0
        activations = []

        for inp in range(numberOfInputs):
            input_hex = self.getRandomInput()
            cmd1 = [path + "../perturbations/wb_p_"+ str(prob) +"_"+ str(i)] + input_hex.split()
            cmd2 = [path + "wb_challenge"] + input_hex.split()
            p1 = subprocess.Popen(" ".join(cmd1), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            out_opt, err_opt  = p1.communicate()
            rc1 = p1.returncode

            p2 = subprocess.Popen(" ".join(cmd2), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            out_ref, err_ref = p2.communicate()
            rc2 = p1.returncode

            # out_opt, err_opt = subprocess.Popen(" ".join(cmd1), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()
            # out_ref, err_ref = subprocess.Popen(" ".join(cmd2), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()

            if int(rc1):
                error +=1
            elif out_ref == out_opt:
                success+=1
            else:
                fail+=1

            activations.append(self.countPerturbations(err_opt))

        return success, fail, error, (lambda lista: sum(lista)/len(lista))(activations)
