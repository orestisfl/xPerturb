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
    runns = 500
    target = TraceChess2016("../example_programs/wbs_aes_ches2016/", runns)

    for i in range(10):
        attackTitle = "chess2016_ref_attack_" + str(runns) + "_" + str(i)
        # target.accuireTrace(50, 48000) ## No arguments for reference trace
        target.accuireTrace() ## No arguments for reference trace
        out, err = target.performDaredevilAttack()
        atts = AttackStatitic("Chess2016")
        atts.parseDaredevilData(out, err)
        atts.saveToFile("./logs/" + attackTitle)
        saveDaredevil(attackTitle, out, err)

        subprocess.Popen(["rm mem_*"], shell=True)
        subprocess.Popen(["rm stack_*"], shell=True)

def runKryptologik():
    runns = 500
    target = TraceKryptologik("../example_programs/wbs_aes_kryptologik/", runns)
    for i in range(10):
        attackTitle = "kryptologik_ref_attack_" + str(runns) + "_" + str(i)
        # t.accuireTrace(50, 48000) ## No arguments for reference trace
        target.accuireTrace() ## No arguments for reference trace
        out, err = target.performDaredevilAttack()
        atts = AttackStatitic("Kryptologik")
        atts.parseDaredevilData(out, err)
        atts.saveToFile("./logs/" + attackTitle)
        saveDaredevil(attackTitle, out, err)

        subprocess.Popen(["rm mem_*"], shell=True)
        subprocess.Popen(["rm stack_*"], shell=True)


def main():

    #runChess()
    #runKryptologik()

main()
