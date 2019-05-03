from __future__ import division # Float division
import sys
import os
import subprocess # Popen
import random
from tqdm import tqdm # Progress bar

number_of_inputs_to_try = 1000
executable = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/linked_challenge_pone.bc"

class RESULT:
    def __init__(self, s, f, err, pr, e, pe):
        self.Success = s
        self.Fail = f
        self.Error = err
        self.Probability = pr
        self.Executable = e
        self.PerturbationPoint = pe
    def __str__(self):
        return "Executable: " + self.Executable + "\nCorrectness ratio " + str(self.Success/(self.Fail + self.Error + self.Success)) + "\nSuccess: " + str(self.Success) + "\nFails: " + str(self.Fail) + "\nErrors: " + str(self.Error)

def getRandomInput():
    # Returns a randomly generated input tailored for the wb in mind
    ret = '{0:0{1}X}'.format(random.randint(0, 255),2)
    for i in range(15):
        ret = ret + " " + '{0:0{1}X}'.format(random.randint(0, 255),2)
    return ret

def printExperiemntResult(result_list):
    for i in result_list:
        print(i)

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


def main():
    results = []
    path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/"
    reference = "gcc -O3 -o " + path + "wb_challenge " + path + "challenge.c " + path + "chow_aes3_encrypt_wb.c"
    sp = subprocess.call(reference, shell=True)
    cmd1 = " ".join(["clang", "-c", "-emit-llvm", path + "chow_aes3_encrypt_wb.c", "-o", path + "chow_aes3_encrypt_wb.bc"])
    sp = subprocess.call(cmd1, shell=True)
    cmd2 = " ".join(["clang", "-c", "-emit-llvm", path + "challenge.c", "-o",  path + "challenge.bc"])
    sp = subprocess.call(cmd2, shell=True)
    cmd3 = " ".join(["llvm-link", path + "challenge.bc", path + "chow_aes3_encrypt_wb.bc", "-o", path + "linked_challenge.bc"])
    sp = subprocess.call(cmd3, shell=True)

    ## Get number of perturbation points
    sp = subprocess.Popen(["opt", "-load", "/home/koski/llvm-8.0.0.src/build/lib/LLVMPerturbCount.so", "-Count", path + "linked_challenge.bc"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = sp.communicate()
    number_of_perturbation_points = int(err)
    print("Number of pp: " + str(number_of_perturbation_points))

    for p in range(0, 101): ## Probability
        print("Probability: " + str(p))

        # Generate and embedd the perturbation code along with the wb
        generatePerturbationType(p, True)
        sp = subprocess.call("clang -c -emit-llvm " + path + "../perturbation_types/pone.c -o " + path + "../perturbation_types/pone.bc", shell=True)
        sp = subprocess.call("llvm-link " + path + "../perturbation_types/pone.bc " + path + "linked_challenge.bc -o " + path + "linked_challenge_pone.bc", shell=True)

        for i in range(0, number_of_perturbation_points): ## PerturbationPoint
            print("Perturbationpoint: " + str(i))
            results.append(RESULT(0, 0, 0, p, executable, i))

            ## Generate a binary for the i:th pp
            sp5 = subprocess.call("opt -load \"/home/koski/llvm-8.0.0.src/build/lib/LLVMRandom.so\" -Random -o " + path + "linked_challenge_pone_opt.bc -pp " + str(i) + " < " + path + "linked_challenge_pone.bc", shell=True)
            sp6 = subprocess.call("llc " + path + "linked_challenge_pone_opt.bc -o " + path + "linked_challenge_pone_opt.s", shell=True)
            sp7 = subprocess.call("gcc " + path + "linked_challenge_pone_opt.s -o " + path + "linked_challenge_pone_opt -no-pie", shell=True)
            sp8 = subprocess.call("chmod +x " + path + "linked_challenge_pone_opt", shell=True)


            for j in tqdm(range(number_of_inputs_to_try)): ## Inputs
                input_hex = getRandomInput()
                cmd1 = [path + "linked_challenge_pone_opt"] + input_hex.split()
                cmd2 = [path + "wb_challenge"] + input_hex.split()

                ## Run the perturbed binary and the reference binary
                out_opt, err_opt = subprocess.Popen(" ".join(cmd1), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()
                out_ref, err_ref = subprocess.Popen(" ".join(cmd2), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()

                if err_opt:
                    results[i].Error = results[i].Error +1
                    continue

                if out_ref == out_opt:
                    results[i].Success = results[i].Success + 1
                else:
                    results[i].Fail = results[i].Fail + 1
            print("Results are:")
            print(results[i])
            break
        break
    printExperiemntResult(results)
main()
