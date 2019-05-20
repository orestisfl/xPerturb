from __future__ import division # Float division
import sys
import os
import subprocess # Popen
import random

number_of_inputs_to_try = 1000
highpass = 90
# atPercent = 50
threshold = number_of_inputs_to_try * (100-highpass)/100
# activeThreads = 10
# countBad = 0
# countGood = 0
# stdoutAvalible = True
path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/"
# results = []


class RESULT:
    def __init__(self, s, f, err, pr, e, pe):
        self.Success = s
        self.Fail = f
        self.Error = err
        self.Probability = pr
        self.Executable = e
        self.PerturbationPoint = pe

    def __str__(self):
        return "Executable: " + self.Executable + "\nCorrectness ratio: " + str(self.Success/(self.Fail + self.Error + self.Success)) + "\nSuccess: " + str(self.Success) + "\nFails: " + str(self.Fail) + "\nErrors: " + str(self.Error) + "\nIndex: " + str(self.PerturbationPoint)

def getRandomInput():
    # Returns a randomly generated input tailored for the wb in mind
    ret = '{0:0{1}X}'.format(random.randint(0, 255),2)
    for i in range(15):
        ret = ret + " " + '{0:0{1}X}'.format(random.randint(0, 255),2)
    return ret

def printExperiemntResult(result_list):
    fd = open("results.yay", "w")
    for i in result_list.keys():
        print(result_list[i])
        fd.write(str(result_list[i]))
        fd.write("\n")
        fd.write("\n")
    fd.close()
    # print("Bad: " + str(countBad))
    # print("Good: " + str(countGood))

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
    with open("example_programs/perturbation_types/pone.c", 'w') as f:
        f.write(templateString.format(**d))

    with open("perturbation_templates/pm_oneh.tmp", 'r') as ftemp:
        templateString = ftemp.read()
    with open("example_programs/perturbation_types/pone.h", 'w') as f:
        f.write(templateString)

def compileWhiteBoxToLLVM():
    reference = " ".join(["gcc", "-O3", "-I", path + "../perturbation_types", "-o ", path + "wb_challenge", path + "challenge.c", path + "chow_aes3_encrypt_wb.c"])
    sp = subprocess.call(reference, shell=True)

    # Embedd the perturbation code inside the wb
    cmd0 = " ".join(["gcc", "-c", path + "../perturbation_types/pone.c", "-o", path + "../perturbation_types/pone.o" ])
    sp = subprocess.call(cmd0, shell=True)

    # Compile the wb
    cmd1 = " ".join(["clang", "-c", "-emit-llvm", "-fexceptions", path + "chow_aes3_encrypt_wb.c", "-o", path + "chow_aes3_encrypt_wb.bc"])
    sp = subprocess.call(cmd1, shell=True)
    cmd2 = " ".join(["clang", "-c", "-I", path + "../perturbation_types", "-emit-llvm", "-fexceptions", path + "../perturbation_types/pone.o", path + "challenge.c", "-o",  path + "challenge.bc"])
    sp = subprocess.call(cmd2, shell=True)
    cmd3 = " ".join(["llvm-link", path + "challenge.bc", path + "chow_aes3_encrypt_wb.bc", "-o", path + "linked_challenge.bc"])
    sp = subprocess.call(cmd3, shell=True)

def getNumberOfPerturbationPoints():
    sp = subprocess.Popen(["opt", "-load", "/home/koski/llvm-8.0.0.src/build/lib/LLVMPerturbCount.so", "-Count", path + "linked_challenge.bc"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = sp.communicate()
    print("Number of pp: " + str(int(err)))
    return int(err)

def cleanFiles(i, delete_all):
    if delete_all:
        cmd32 = ["rm", path + "perturbations/linked_challenge_pone_opt_" + str(i)]
        out_opt, err_opt = subprocess.Popen(" ".join(cmd32), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()

    cmd30 = ["rm", path + "perturbations/linked_challenge_pone_opt_" + str(i) + ".bc"]
    cmd31 = ["rm", path + "perturbations/linked_challenge_pone_opt_" + str(i) + ".s"]
    out_opt, err_opt = subprocess.Popen(" ".join(cmd30), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()
    out_opt, err_opt = subprocess.Popen(" ".join(cmd31), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()

def insertPerturbationPoint(i):
    sp5 = subprocess.call("opt -load \"/home/koski/llvm-8.0.0.src/build/lib/LLVMRandom.so\" -Random -o " + path + "perturbations/linked_challenge_pone_opt_" + str(i) + ".bc -pp " + str(i) + " < " + path + "linked_challenge_pone.bc", shell=True)
    sp6 = subprocess.call("llc " + path + "perturbations/linked_challenge_pone_opt_" + str(i) + ".bc -o " + path + "perturbations/linked_challenge_pone_opt_" + str(i) + ".s", shell=True)
    sp7 = subprocess.call("gcc " + path + "perturbations/linked_challenge_pone_opt_" + str(i) + ".s -o " + path + "perturbations/linked_challenge_pone_opt_" + str(i) + " -no-pie", shell=True)
    sp8 = subprocess.call("chmod +x " + path + "perturbations/linked_challenge_pone_opt_" + str(i) + "", shell=True)

def testPerturbationPoint(i):
    success = 0
    fail = 0
    error = 0

    input_hex = getRandomInput()
    with open("/home/koski/xPerturb/llvmPerturb/experiment_results/inputs.txt", "r") as fi:
        line = fi.readline()
        c = 1
        while line: # and c < 50:
            cmd1 = [path + "perturbations/linked_challenge_pone_opt_" + str(i) + ""] + input_hex.split()
            cmd2 = [path + "wb_challenge"] + input_hex.split()
            out_opt, err_opt = subprocess.Popen(" ".join(cmd1), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()
            out_ref, err_ref = subprocess.Popen(" ".join(cmd2), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()

            if err_opt:
                error +=1
            elif out_ref == out_opt:
                success+=1
            else:
                fail+=1
            line = fi.readline()
            c += 1
    return success, fail, error

    # cmd1 = [path + "perturbations/linked_challenge_pone_opt_" + str(i) + ""] + input_hex.split()
    # cmd2 = [path + "wb_challenge"] + input_hex.split()
    #
    # ## Run the perturbed binary and the reference binary
    # out_opt, err_opt = subprocess.Popen(" ".join(cmd1), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()
    # ## print("printing mfs")
    #
    # # out_list = out_opt.split("\n")
    # # for row in out_list:
    # #     try:
    # #         print(i, int(row))
    # #     except Exception:
    # #         pass
    # #print(out_opt)
    # out_ref, err_ref = subprocess.Popen(" ".join(cmd2), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()
    #
    # if err_opt:
    #     return "Error"
    # elif out_ref == out_opt:
    #     return "Success"
    # else:
    #     return "Fail"
