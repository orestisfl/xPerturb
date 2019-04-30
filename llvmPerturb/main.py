import sys
import os
import subprocess
import random

number_of_inputs_to_try = 5
probability_of_perturbating = 1
executable = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/linked_challenge_pone.bc"

class RESULT:
    def __init__(self, s, f, pr, e, pe):
        self.Success = s
        self.Fail = f
        self.Probability = pr
        self.Executable = e
        self.PerturbationPoint = pe
    def __str__(self):
        return self.Executable + ", " + str(self.Executable) + ", " + str(self.Success/(self.Fail + self.Success))

def getRandomInput():
    ret = '{0:0{1}X}'.format(random.randint(0, 255),2)
    for i in range(15):
        ret = ret + " " + '{0:0{1}X}'.format(random.randint(0, 255),2)
    return ret

def printExperiemntResult(result_list):
    for i in result_list:
        print(i)

def generatePerturbationType(prob, plus):
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
    sp = subprocess.Popen(["clang", "-emit-llvm", path + "chow_aes3_encrypt_wb.c"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = sp.communicate()
    sp = subprocess.Popen(["clang", "-emit-llvm", path + "challenge.c"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = sp.communicate()
    sp = subprocess.Popen(["llvm-link", path + "challenge.ll", path + "chow_aes3_encrypt_wb.ll", "-o", path + "linked_challenge.bc"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = sp.communicate()
    sp = subprocess.Popen(["llvm-dis", path + "linked_challenge.bc", "-o", path + "linked_challenge.ll"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = sp.communicate()



    ## ./home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/linked_challenge_pone_opt.bc
    ## ./home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/linked_challenge_pone_opt.bc


    for p in range(50, 101): ## Probability
        print("Probability: " + str(p)) ## TODO Implemnt a progressbar

        ## Build the llvm IR with correct perturbation type
        ## Set probability of perturbating to p
        generatePerturbationType(p, True)
        sp = subprocess.Popen(["clang", "-S", "-emit-llvm", path + "../perturbation_types/pone.c", "-o", path + "../perturbation_types/pone.ll"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = sp.communicate()
        sp = subprocess.Popen(["llvm-link", path + "../perturbation_types/pone.ll", path + "linked_challenge.ll", "-o", path + "linked_challenge_pone.bc"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = sp.communicate()
        sp = subprocess.Popen(["chmod", "+x", path + "linked_challenge.bc"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = sp.communicate()

        ## Get number of perturbation points
        sp = subprocess.Popen(["opt", "-load", "/home/koski/llvm-8.0.0.src/build/lib/LLVMPerturbCount.so", "-Count", path + "linked_challenge_pone.bc"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = sp.communicate()
        number_of_perturbation_points = int(err)

        print("number_of_perturbation_points: " + str(number_of_perturbation_points))
        for i in range(0, number_of_perturbation_points): ## PerturbationPoint
            if i%1000 == 0:
                print("Pertrubartionpoint: " + str(i))
            results.append(RESULT(0, 0, probability_of_perturbating, executable, i))

            ## Generate a binary for the i:th pp set
            sp = subprocess.Popen(["opt", "-load", "/home/koski/llvm-8.0.0.src/build/lib/LLVMPerturbCount.so", "-Random", "-pp", str(i), "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/linked_challenge_pone.bc", ">", "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/linked_challenge_pone_opt.bc"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            out, err = sp.communicate()

            for j in range(number_of_inputs_to_try): ## Inputs
                print("Inputs: " + str(j))
                input_hex = getRandomInput()
                ## print(input_hex)

                sp = subprocess.Popen(["chmod", "+x", path + "linked_challenge_pone_opt.bc"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
                out, err = sp.communicate()

                ## Run the generated binary with the input
                sp = subprocess.Popen([path + "linked_challenge_pone_opt.bc"] + input_hex.split(), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
                out_opt, err_opt = sp.communicate()
                ## Run the reference binary
                sp = subprocess.Popen([path + "linked_challenge.bc"] + input_hex.split(), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
                out_ref, err_ref = sp.communicate()

                ## If corect output
                    ## results[i].Success +=1
                ## Else incorrect output
                    ## results[i].Fail +=1
                print(out_ref)
                print(out_opt)
                if out_ref == out_opt:
                    results[i].Success = results[i].Success + 1
                else:
                    results[i].Fail = results[i].Fail + 1
            print(results[i])
            break
    printExperiemntResult(results)
main()
