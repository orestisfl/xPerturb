#!/usr/bin/env python
import subprocess
from errno import EEXIST
from tqdm import tqdm

from targets import *
from attack_statistics import *

def makedirs(path):
    try:
        os.makedirs(path)
    except OSError as e:
        if e.errno != EEXIST:
            raise

def saveDaredevil(name, out, times):
    makedirs("./daredevilLogs")
    fd = open("./daredevilLogs/daredevil_" + name, 'w')
    fd.write(times)
    fd.write("\n")
    fd.write(out)
    fd.write("\n")
    fd.close()

def runAttack(points, runns, name, path, perturbation_probability = 0):
    print(name)
    print(perturbation_probability)
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
        for i in tqdm(range(0, 100)): # 0 - 30
            attackTitle = name + "_attack_" + str(runns) + "_" + str(p) + "_" + str(i)
            if p == "ref":
                target.accuireTrace() ## No arguments for reference trace
            else:
                target.accuireTrace(perturbation_probability, p) ## No arguments for reference trace

            # Backup traces
            assert subprocess.call('zip -q %d.zip *.grind* && rm -f *.grind*' % i, shell=True) == 0

            out, err = target.performDaredevilAttack()
            atts = AttackStatitic(name)
            atts.parseDaredevilData(out, err)
            makedirs("./logs")
            atts.saveToFile("./logs/" + attackTitle)
            saveDaredevil(attackTitle, out, err)

            subprocess.Popen(["rm mem_*"], shell=True)
            subprocess.Popen(["rm stack_*"], shell=True)


def chess_attack(probability = 50, reference_points = False, overall_top_points = False):

    if reference_points:
        runAttack(["ref"],
                    200,
                    "chess2016",
                    "../example_programs/wbs_aes_ches2016/")

    if overall_top_points:
        ## The perturbation points listed in top10 bellow are derived from the experiments in ../correctness_attraction
        top10 = [48000, 11400, 19800, 11700, 28200, 16200, 24600, 7300, 59700, 10000]
        runAttack(top10,
                    200,
                    "chess2016",
                    "../example_programs/wbs_aes_ches2016/",
                    probability)

def kryptologik_attack(probability = 50, reference_points = False, overall_top_points = False):

    if reference_points:
        runAttack(["ref"],
                    200,
                    "kryptologik",
                    "../example_programs/wbs_aes_kryptologik/")
    if overall_top_points:
        ## The perturbation points listed in top10 bellow are derived from the experiments in ../correctness_attraction
        top10 = [8100, 18900, 12000, 3600, 12300, 15000, 1800, 19800, 16200, 16900]
        runAttack(top10,
                    80,
                    "kryptologik",
                    "../example_programs/wbs_aes_kryptologik/",
                    probability)

def nsc_attack(probability = 50, reference_points = False, overall_top_points = False):
    if reference_points:
        runAttack(["ref"],
                    25,
                    "nsc_gen",
                    # "../example_programs/wbs_aes_nsc2013_variants_generator/src/")
                    "/slumps/vulnerable_programs/deadpool/nosuchcon_2013_whitebox_noenc/bitcode/")

    if overall_top_points:
        ## The perturbation points listed in top10 bellow are derived from the experiments in ../correctness_attraction
        top10 = [0, 12, 6, 122, 120, 118, 174, 288, 280, 108]
        runAttack(top10,
                    25,
                    "nsc_gen",
                    "../example_programs/wbs_aes_nsc2013_variants_generator/",
                    probability)


def main():
    #kryptologik_attack(reference_points = True)
    nsc_attack(reference_points=True)

main()
