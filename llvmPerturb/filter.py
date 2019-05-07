from __future__ import division # Float division
import sys
import os
import subprocess # Popen
import random
from tqdm import tqdm # Progress bar

number_of_inputs_to_try = 1000
highpass = 90
atPercent = 50
threshold = number_of_inputs_to_try * (100-highpass)/100
activeThreads = 10
countBad = 0
countGood = 0
stdoutAvalible = True
executable = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/linked_challenge_pone.bc"
path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/"
results = []


class RESULT:
    def __init__(self, s, f, err, pr, e, pe):
        self.Success = s
        self.Fail = f
        self.Error = err
        self.Probability = pr
        self.Executable = e
        self.PerturbationPoint = pe
        self.Bad = False

    def __str__(self):
        return "Executable: " + self.Executable + "\nCorrectness ratio: " + str(self.Success/(self.Fail + self.Error + self.Success)) + "\nSuccess: " + str(self.Success) + "\nFails: " + str(self.Fail) + "\nErrors: " + str(self.Error) + "\nIndex: " + str(self.PerturbationPoint)

def getRandomInput():
    # Returns a randomly generated input tailored for the wb in mind
    ret = '{0:0{1}X}'.format(random.randint(0, 255),2)
    for i in range(15):
        ret = ret + " " + '{0:0{1}X}'.format(random.randint(0, 255),2)
    return ret

def printExperiemntResult(result_list):
    for i in result_list:
        if i.Bad:
            countBad +=1
        else:
            print(i)
    print("Bad: " + str(countBad))
    print("Good: " + str(countGood))

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

def getNumberOfPerturbationPoints():
    sp = subprocess.Popen(["opt", "-load", "/home/koski/llvm-8.0.0.src/build/lib/LLVMPerturbCount.so", "-Count", path + "linked_challenge.bc"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = sp.communicate()
    print("Number of pp: " + str(int(err)))
    return int(err)

def compileWhiteBoxToLLVM():
    reference = "gcc -O3 -o " + path + "wb_challenge " + path + "challenge.c " + path + "chow_aes3_encrypt_wb.c"
    sp = subprocess.call(reference, shell=True)
    cmd1 = " ".join(["clang", "-c", "-emit-llvm", path + "chow_aes3_encrypt_wb.c", "-o", path + "chow_aes3_encrypt_wb.bc"])
    sp = subprocess.call(cmd1, shell=True)
    cmd2 = " ".join(["clang", "-c", "-emit-llvm", path + "challenge.c", "-o",  path + "challenge.bc"])
    sp = subprocess.call(cmd2, shell=True)
    cmd3 = " ".join(["llvm-link", path + "challenge.bc", path + "chow_aes3_encrypt_wb.bc", "-o", path + "linked_challenge.bc"])
    sp = subprocess.call(cmd3, shell=True)

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
    input_hex = getRandomInput()
    cmd1 = [path + "perturbations/linked_challenge_pone_opt_" + str(i) + ""] + input_hex.split()
    cmd2 = [path + "wb_challenge"] + input_hex.split()

    ## Run the perturbed binary and the reference binary
    out_opt, err_opt = subprocess.Popen(" ".join(cmd1), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()
    out_ref, err_ref = subprocess.Popen(" ".join(cmd2), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()

    if err_opt:
        return "Error"
    elif out_ref == out_opt:
        return "Success"
    else:
        return "Fail"

def handlePerturbationPoint(i):
    global countBad
    global results
    results.append(RESULT(0, 0, 0, atPercent, executable, i))
    insertPerturbationPoint(i)
    fails = 0
    for j in range(number_of_inputs_to_try): ## Inputs
        if results[i].Error + results[i].Fail > threshold:
            results[i].Bad = True
            #print(results[i])
            break
        testResult = testPerturbationPoint(i)

        if testResult == "Error":
            results[i].Error = results[i].Error +1
        elif testResult == "Success":
            results[i].Success = results[i].Success + 1
        else:
            results[i].Fail = results[i].Fail + 1

    if results[i].Bad:
        countBad = countBad + 1
        cleanFiles(i, True)
    else:
        cleanFiles(i, False)
        print(results[i])
        print("")

def main():
    compileWhiteBoxToLLVM()
    ## Get number of perturbation points
    number_of_perturbation_points = getNumberOfPerturbationPoints()
    print("Probability: " + str(atPercent))

    generatePerturbationType(atPercent, True)

    # Embedd the perturbation code inside the wb
    sp = subprocess.call("clang -c -emit-llvm " + path + "../perturbation_types/pone.c -o " + path + "../perturbation_types/pone.bc", shell=True)
    sp = subprocess.call("llvm-link " + path + "../perturbation_types/pone.bc " + path + "linked_challenge.bc -o " + path + "linked_challenge_pone.bc", shell=True)
    for i in range(0, number_of_perturbation_points): ## PerturbationPoint
        handlePerturbationPoint(i)
    #printExperiemntResult(results)
    print(countBad)
    print("Fin")

main()
