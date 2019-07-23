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

def runChess():
    print("Chess")
    runns = 500
    target = TraceChess2016("../example_programs/wbs_aes_ches2016/", runns)
    ptsL = [48000, 11400, 19800, 11700, 28200, 16200, 24600, 7300, 59700, 10000]
    for i in range(10):
        for p in ptsL:
            attackTitle = "chess2016_attack_" + str(runns) + "_" + str(p) + "_" + str(i)
            target.accuireTrace(50, p) ## No arguments for reference trace
            # target.accuireTrace() ## No arguments for reference trace
            out, err = target.performDaredevilAttack()
            atts = AttackStatitic("Chess2016")
            atts.parseDaredevilData(out, err)
            atts.saveToFile("./logs/" + attackTitle)
            saveDaredevil(attackTitle, out, err)

            subprocess.Popen(["rm mem_*"], shell=True)
            subprocess.Popen(["rm stack_*"], shell=True)

def runKryptologik():
    print("Kryptologik")
    runns = 80
    target = TraceKryptologik("../example_programs/wbs_aes_kryptologik/", runns)
    ptsL = [8100, 18900, 12000, 3600, 12300, 15000, 1800, 19800, 16200, 16900]
    for i in range(4):
        for p in ptsL:
            attackTitle = "kryptologik_attack_" + str(runns) + "_" + str(p) + "_" + str(i)
            target.accuireTrace(50, p) ## No arguments for reference trace
            # target.accuireTrace() ## No arguments for reference trace
            out, err = target.performDaredevilAttack()
            atts = AttackStatitic("Kryptologik")
            atts.parseDaredevilData(out, err)
            atts.saveToFile("./logs/" + attackTitle)
            saveDaredevil(attackTitle, out, err)

            subprocess.Popen(["rm mem_*"], shell=True)
            subprocess.Popen(["rm stack_*"], shell=True)

def runNsc():
    print("Nsc")
    runns = 25
    target = TraceNsc2013("../example_programs/wbs_aes_nsc2013_variants_generator/", runns)
    ptsL = [0, 12, 6, 122, 120, 118, 174, 288, 280, 108] # Top points at 50%

    for i in range(4):
        for p in ptsL:
            attackTitle = "nsc_gen_attack_" + str(runns) + "_" + str(p) + "_" + str(i)
            target.accuireTrace(50, p) ## No arguments for reference trace
            #target.accuireTrace() ## No arguments for reference trace
            out, err = target.performDaredevilAttack()
            atts = AttackStatitic("Nsc-Gen")
            atts.parseDaredevilData(out, err)
            atts.saveToFile("./logs/" + attackTitle)
            saveDaredevil(attackTitle, out, err)

            subprocess.Popen(["rm mem_*"], shell=True)
            subprocess.Popen(["rm stack_*"], shell=True)

def main():
    #runChess()
    #runKryptologik()
    runNsc()

main()
