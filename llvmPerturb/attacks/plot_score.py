import matplotlib.pyplot as plt
import numpy as np
from scipy import stats # t-test
from populate import *

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
            populateGraph(pair[0], pair[1], 10, left_label = "Attack-score", right_label = "Correctness", bottom_label = r"$Perturbation\ point_{activations}$")
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
            populateGraph(pair[0], pair[1], 50, left_label = "Attack-score", right_label = "Correctness", bottom_label = r"$Perturbation\ point_{activations}$")
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
            populateGraph(pair[0], pair[1], 90, left_label = "Attack-score", right_label = "Correctness", bottom_label = r"$Perturbation\ point_{activations}$")
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


def main():
    plot_10()
    plot_50()
    plot_90()
    plot_references()

main()
