from attack_statistics import *
import os

def chess():
    print("")
    print("Reference - Chess2016")
    print("Abs\tSum")
    for i in range(0,30):
        a = AttackStatitic("Chess2016")
        a.loadFromFile("./logs/references/chess/chess2016_attack_200_ref_"+str(i))
        a.setProgramScore("dec1a551f1eddec0de4b1dae5c0de511")

    print("")

    paths=["./logs/overall_top_points_10/chess/", "./logs/overall_top_points_50/chess/", "./logs/overall_top_points_90/chess/"]
    for path in paths:
        print("")
        print(path)

        for i in [j for j in os.listdir(path) if j.startswith("chess2016_attack_200")]:
            if int(i.split("_")[-2]) not in [48000, 11400, 19800, 11700, 28200]:
                continue
            a = AttackStatitic("Chess2016")
            a.loadFromFile(path + i)
            a.perturbationpoint = i.split("_")[-2]
            a.setProgramScore("dec1a551f1eddec0de4b1dae5c0de511")


def kryptologik():
    print("")
    print("Reference - Kryptologik")
    print("Abs\tSum")
    for i in range(0,30):
        a = AttackStatitic("Kryptologik")
        a.loadFromFile("./logs/references/kryptologik/kryptologik_attack_80_ref_"+str(i))
        a.setProgramScore("0d9be960c438ff85f656bd48b78a0ee2")

    print("")

    path ="./logs/overall_top_points_10/kryptologik/"
    # path ="./logs/overall_top_points_50/kryptologik/"
    # path ="./logs/overall_top_points_90/kryptologik/"

    for i in [j for j in os.listdir(path) if j.startswith("kryptologik_attack_")]:
        a = AttackStatitic("Kryptologik")
        a.loadFromFile("./logs/overall_top_points/kryptologik/" + i)
        a.perturbationpoint = i.split("_")[-2]
        a.setProgramScore("0d9be960c438ff85f656bd48b78a0ee2")



def nsc():
    print("")
    print("Reference - NSC")
    print("Abs\tSum")
    for i in range(0,30):
        a = AttackStatitic("Nsc2013")
        a.loadFromFile("./logs/references/nsc/nsc_gen_attack_25_ref_"+str(i))
        a.setProgramScore("4b45595f4b45595f4b45595f4b45595f")

    print("")
    #path = "./logs/overall_top_points/nsc/"
    # path = "./logs/overall_top_points_10/nsc/"
    path = "./logs/overall_top_points_90/nsc/"

    for i in [j for j in os.listdir(path) if j.startswith("nsc_gen_attack_")]:
        a = AttackStatitic("NSC")
        a.loadFromFile(path + i)
        a.perturbationpoint = i.split("_")[-2]
        a.setProgramScore("4b45595f4b45595f4b45595f4b45595f")


def main():
    chess()
    # kryptologik()
    # nsc()



main()
