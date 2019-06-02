from __future__ import division # Float division
import sys
import os
import subprocess # Popen
import random


number_of_inputs_to_try = 1000
# highpass = 90
# threshold = number_of_inputs_to_try * (100-highpass)/100

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
        return self.Success/(self.Fail + self.Error + self.Success)

    def print_plottable_data(self):
        return str(self.Probability) + ", " + str(self.get_correctness()) + ", " + str(self.PerturbationPoint) + ", " + str(self.Activations)

def getRandomInput():
    # Returns a randomly generated input tailored for the wb in mind
    ret = '{0:0{1}X}'.format(random.randint(0, 255),2)
    for i in range(15):
        ret = ret + " " + '{0:0{1}X}'.format(random.randint(0, 255),2)
    return ret

def getNumberOfPerturbationPoints(path):
    sp = subprocess.Popen(["opt", "-load", "/home/koski/llvm-8.0.0.src/build/lib/LLVMPerturbCount.so", "-Count", "-o", "/dev/null", path + "src/wb.ll"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = sp.communicate()
    print("Number of pp: " + str(int(out)))
    return int(out)

def compileReferenceWhitebox(path):
    pass

def generatePerturbationType(prob, plus):
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

def compileWhiteBoxToLLVM(path):
    cmds = [None] * 4
    cmds[0] = "clang -S -emit-llvm " + path + "src/chow_aes3_encrypt_wb.c -o " + path + "src/chow_aes3_encrypt_wb.ll"
    cmds[1] = "clang -S -emit-llvm " + path + "src/challenge.c -o " + path + "src/challenge.ll"
    cmds[2] = "llvm-link " + path + "src/challenge.ll " + path + "src/chow_aes3_encrypt_wb.ll -o " + path + "src/linked_challenge.bc"
    cmds[3] = "llvm-dis " + path + "src/linked_challenge.bc -o " + path + "src/wb.ll"

    for cmd in cmds:
        sp = subprocess.call(cmd, shell=True)

def insertPerturbationProtocol(i, prob, path):
    cmds = [None] * 1
    cmds[0] = "llvm-link example_programs/perturbation_types/pone_"+ str(prob) +".ll " + path + "src/wb.ll -o " + path + "perturbations/wb_p_"+ str(prob) +".bc"
    for cmd in cmds:
        sp = subprocess.call(cmd, shell=True)

def insertPerturbationPoint(pointIndex, path, prob):
    cmds = [None] * 4
    cmds[0] = "opt -load \"/home/koski/llvm-8.0.0.src/build/lib/LLVMRandom.so\" -Random -pp " + str(pointIndex) + " -o " + path + "perturbations/wb_p_" + str(prob) +"_"+ str(pointIndex) + ".bc" + " < " + path + "perturbations/wb_p_"+ str(prob) +".bc"
    cmds[1] = "llc -o " + path + "perturbations/wb_p_" + str(prob) + "_" + str(pointIndex) + ".s " + path + "perturbations/wb_p_" + str(prob) + "_" + str(pointIndex) + ".bc"
    cmds[2] = "gcc -o " + path + "perturbations/wb_p_" + str(prob) + "_" + str(pointIndex) + " " + path + "perturbations/wb_p_" + str(prob) + "_" + str(pointIndex) + ".s -no-pie"
    cmds[3] = "chmod +x " + path + "perturbations/wb_p_" + str(prob) +"_"+ str(pointIndex)
    for cmd in cmds:
        sp = subprocess.call(cmd, shell=True)

def countPerturbations(logs):
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

def testPerturbationPoint(i, path, prob, numberOfInputs):
    success = 0
    fail = 0
    error = 0
    activations = []

    for inp in range(numberOfInputs):
        input_hex = getRandomInput()
        cmd1 = [path + "perturbations/wb_p_"+ str(prob) +"_"+ str(i)] + input_hex.split()
        cmd2 = [path + "src/wb_challenge"] + input_hex.split()
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

        activations.append(countPerturbations(err_opt))

    #
    # with open("/home/koski/xPerturb/llvmPerturb/experiment_results/inputs.txt", "r") as fi:
    #     line = fi.readline()
    #     c = 1
    #     while line: # and c < 50:
    #         cmd1 = [path + "perturbations/wb_p_"+ str(prob) +"_"+ str(i)] + input_hex.split()
    #         cmd2 = [path + "src/wb_challenge"] + input_hex.split()
    #         out_opt, err_opt = subprocess.Popen(" ".join(cmd1), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()
    #         out_ref, err_ref = subprocess.Popen(" ".join(cmd2), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()
    #
    #         if err_opt:
    #             error +=1
    #         elif out_ref == out_opt:
    #             success+=1
    #         else:
    #             fail+=1
    #         line = fi.readline()
    #         c += 1
    return success, fail, error, (lambda lista: sum(lista)/len(lista))(activations)


def cleanFiles(i, prob, path, delete_all): ## TODO Denna lirar inte med den nya strukturen
    if delete_all:
        cmd32 = ["rm", path + "perturbations/wb_p_" + str(prob)]
        out_opt, err_opt = subprocess.Popen(" ".join(cmd32), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()

    cmd30 = ["rm", path + "perturbations/wb_p_" + str(prob) +"_"+ str(i) + ".bc"]
    cmd31 = ["rm", path + "perturbations/wb_p_" + str(prob) +"_"+ str(i) + ".s"]
    cmd32 = ["rm", path + "perturbations/wb_p_" + str(prob) + ".bc"]
    out_opt, err_opt = subprocess.Popen(" ".join(cmd30), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()
    out_opt, err_opt = subprocess.Popen(" ".join(cmd31), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()
    out_opt, err_opt = subprocess.Popen(" ".join(cmd32), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()
