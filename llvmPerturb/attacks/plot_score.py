import matplotlib.pyplot as plt
import numpy as np
from scipy import stats # t-test
from populate import *

# Create graphs over attack scores and save them in "experiment_results"

def plot_10():
    path = "/home/koski/xPerturb/llvmPerturb/experiment_results/attack/graphs/"
    attack_pairs = [
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/chess_attack_score_10",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/ches/ches2016_points_db.cvc"),
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/kryptologik_attack_score_10",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/kryptologik/kryptologik_points_db.cvc"),
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/nsc_attack_score_10",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/nsc/nsc2013_gen_points_db.cvc")
    ]
    for pair in attack_pairs:
        try:
            populateGraph(pair[0], pair[1], 10)
        except IOError:
            pass
        plt.savefig(path + pair[0].split("/")[-1])
        plt.clf()

def plot_50():
    path = "/home/koski/xPerturb/llvmPerturb/experiment_results/attack/graphs/"
    attack_pairs = [
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/chess_attack_score_50",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/ches/ches2016_points_db.cvc"),
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/kryptologik_attack_score_50",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/kryptologik/kryptologik_points_db.cvc"),
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/nsc_attack_score_50",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/nsc/nsc2013_gen_points_db.cvc")
    ]
    for pair in attack_pairs:
        try:
            populateGraph(pair[0], pair[1], 50)
        except IOError:
            pass
        plt.savefig(path + pair[0].split("/")[-1])
        plt.clf()

def plot_90():
    path = "/home/koski/xPerturb/llvmPerturb/experiment_results/attack/graphs/"
    attack_pairs = [
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/chess_attack_score_90",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/ches/ches2016_points_db.cvc"),
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/kryptologik_attack_score_90",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/kryptologik/kryptologik_points_db.cvc"),
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/nsc_attack_score_90",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/nsc/nsc2013_gen_points_db.cvc")
    ]
    for pair in attack_pairs:
        try:
            populateGraph(pair[0], pair[1], 90)
        except IOError:
            pass
        plt.savefig(path + pair[0].split("/")[-1])
        plt.clf()

def plot_references():
    result_path = "/home/koski/xPerturb/llvmPerturb/experiment_results/attack/graphs/"
    path = "/home/koski/xPerturb/llvmPerturb/experiment_results/attack/"
    r = ["chess", "kryptologik", "nsc"]
    references = []
    for i in r:
        fd = open(path + i + "_ref_score", "r")
        lines = fd.readlines()
        fd.close()
        references.append([float(i.strip().split()[0]) for i in lines])
    plt.title("Reference attack score")
    plt.ylabel("Attack score")
    plt.boxplot(references, positions = [1,2,3])
    plt.xticks([1 ,2, 3], ["ches2016", "kryptologik", "nsc_2013"])
    plt.grid(True, axis="y")

    plt.savefig(result_path + "reference_box")
    plt.clf()

def plotD():
    path = "/home/koski/xPerturb/llvmPerturb/experiment_results/discussion/graphs/"
    attack_pairs = [
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/chess_attack_score_90",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/ches/ches2016_points_db.cvc"),
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/kryptologik_attack_score_90",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/kryptologik/kryptologik_points_db.cvc"),
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/nsc_attack_score_90",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/nsc/nsc2013_gen_points_db.cvc")
    ] + [
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/chess_attack_score_50",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/ches/ches2016_points_db.cvc"),
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/kryptologik_attack_score_50",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/kryptologik/kryptologik_points_db.cvc"),
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/nsc_attack_score_50",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/nsc/nsc2013_gen_points_db.cvc")
    ] + [
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/chess_attack_score_10",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/ches/ches2016_points_db.cvc"),
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/kryptologik_attack_score_10",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/kryptologik/kryptologik_points_db.cvc"),
    ("/home/koski/xPerturb/llvmPerturb/experiment_results/attack/nsc_attack_score_10",
    "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/nsc/nsc2013_gen_points_db.cvc")
    ]

    for pair in attack_pairs:
        probability = int(pair[0][-2:])
        try:
            plotDiscussionGraph(pair[0], pair[1], probability)
        except IOError:
            pass

    legend_elements = [
    plt.scatter([-1], [-1], color='indianred', marker="o", label='chess2016'),
    plt.scatter([-1], [-1], color='cornflowerblue', marker="s", label='kryptologik'),
    plt.scatter([-1], [-1], color='yellowgreen', marker="X", label='nsc2013')
                   ]

    # Create the figure
    plt.fill_between([2, -1], [1]*2, [3.0]*2, color="red", alpha=0.05) ## P - Value

    plt.fill_between([2, -1], [0.1]*2, [-1]*2, color="lightsteelblue", alpha=0.3) ## P - Value
    plt.fill_between([2, -1], [1.9]*2, [3]*2, color="lightsteelblue", alpha=0.3) ## P - Value
    plt.fill_between([2, 0.5], [2]*2, [-1]*2, color="lightgreen", alpha=0.1) ## Correctness
    plt.plot([-1, 2],[1,1], color="black", linewidth=0.5)

    plt.legend(handles=legend_elements, loc='center left')
    plt.title("Perturbation points correctness attraction \n in relation to its attack score P - Value")
    plt.savefig(path + pair[0].split("/")[-1])
    plt.clf()

def main():
    plot_10()
    plot_50()
    plot_90()
    plot_references()

    plotD()

main()
