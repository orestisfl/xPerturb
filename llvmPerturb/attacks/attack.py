#!/usr/bin/env python
from targets import *
from attack_statistics import *

def saveDaredevil(name, out, times):
    fd = open("./daredevilLogs/daredevil_" + name, 'w')
    fd.write(times)
    fd.write("\n")
    fd.write(out)
    fd.write("\n")
    fd.close()

def runAttack(points, runns, name, path, perturbation_probability = 0):
    print(name)
    print(perturbation_probability)
    #runns = 500
    #target = TraceChess2016("../example_programs/wbs_aes_ches2016/", runns)
    if("ches" in name):
        target = TraceChess2016(path, runns)
    elif("kryptologik" in name):
        target = TraceKryptologik(path, runns)
    elif("nsc" in name):
        target = TraceNsc2013(path, runns)
    else:
        raise NameError("Did not recognize whitebox!")
    for p in points:
        print(p)
        for i in range(4, 30): # 0 - 30
            #attackTitle = "chess2016_attack_" + str(runns) + "_" + str(p) + "_" + str(i)
            attackTitle = name + "_attack_" + str(runns) + "_" + str(p) + "_" + str(i)
            if p == "ref":
                target.accuireTrace() ## No arguments for reference trace
            else:
                target.accuireTrace(perturbation_probability, p) ## No arguments for reference trace
            out, err = target.performDaredevilAttack()
            atts = AttackStatitic(name)
            atts.parseDaredevilData(out, err)
            atts.saveToFile("./logs/" + attackTitle)
            saveDaredevil(attackTitle, out, err)

            subprocess.Popen(["rm mem_*"], shell=True)
            subprocess.Popen(["rm stack_*"], shell=True)


def chess_attack():

    # runAttack(["ref"],
    #             200,
    #             "chess2016",
    #             "../example_programs/wbs_aes_ches2016/")

    runAttack([48000, 11400, 19800, 11700, 28200, 16200, 24600, 7300, 59700, 10000],
                200,
                "chess2016",
                "../example_programs/wbs_aes_ches2016/",
                50)

    # runAttack([8040, 4740, 1320, 4860, 2760, 960, 6860, 3060, 4020, 1080],
    #             200,
    #             "chess2016_r1",
    #             "../example_programs/wbs_aes_ches2016/",
    #             50)  # R1 top 10

def kryptologik_attack():

    # runAttack(["ref"],
    #             80,
    #             "kryptologik",
    #             "../example_programs/wbs_aes_kryptologik/")

    runAttack([8100, 18900, 12000, 3600, 12300, 15000, 1800, 19800, 16200, 16900],
                80,
                "kryptologik",
                "../example_programs/wbs_aes_kryptologik/",
                50)

    # runAttack([1440, 1380, 480, 780, 180, 1740, 1620, 1800, 1560, 1320],
    #             80,
    #             "kryptologik_r1",
    #             "../example_programs/wbs_aes_kryptologik/",
    #             50)  # R1 top 10

def nsc_attack():

    # runAttack(["ref"],
    #             25,
    #             "nsc_gen",
    #             "../example_programs/wbs_aes_nsc2013_variants_generator/")

    runAttack([0, 12, 6, 122, 120, 118, 174, 288, 280, 108],
                25,
                "nsc_gen",
                "../example_programs/wbs_aes_nsc2013_variants_generator/",
                50)

    # runAttack([7, 17, 11, 0, 12, 3, 6, 5, 13, 1],
    #             25,
    #             "nsc_gen_r1",
    #             "../example_programs/wbs_aes_nsc2013_variants_generator/",
    #             50)  # R1 top 10


def main():
    #runNsc([0, 12, 6, 122, 120, 118, 174, 288, 280, 108])
    # chess_attack()
    # kryptologik_attack()
    nsc_attack()

main()
