from attack_statistics import *
import os


def main():

    print("Reference - Chess2016")
    print("Abs\tSum")
    for i in range(0,10):
        a = AttackStatitic("Chess2016")
        a.loadFromFile("./logs/chess2016_ref_attack_500_"+str(i))
        a.setProgramScore("dec1a551f1eddec0de4b1dae5c0de511")

    print("")

    for i in [j for j in os.listdir("./logs") if j.startswith("chess2016_attack_")]:
        a = AttackStatitic("Chess2016")
        a.loadFromFile("./logs/" + i)
        a.perturbationpoint = i.split("_")[-2]
        a.setProgramScore("dec1a551f1eddec0de4b1dae5c0de511")


    print("")
    print("Reference - Kryptologik")
    print("Abs\tSum")
    for i in range(0,10):
        a = AttackStatitic("Kryptologik")
        a.loadFromFile("./logs/kryptologik_ref_attack_80_"+str(i))
        a.setProgramScore("0d9be960c438ff85f656bd48b78a0ee2")

    print("")

    for i in [j for j in os.listdir("./logs") if j.startswith("kryptologik_attack_")]:
        a = AttackStatitic("Kryptologik")
        a.loadFromFile("./logs/" + i)
        a.perturbationpoint = i.split("_")[-2]
        a.setProgramScore("0d9be960c438ff85f656bd48b78a0ee2")

    print("")
    print("Reference - NSC")
    print("Abs\tSum")
    for i in range(0,10):
        a = AttackStatitic("Nsc2013")
        a.loadFromFile("./logs/nsc_gen_ref_attack_25_"+str(i))
        a.setProgramScore("4b45595f4b45595f4b45595f4b45595f")

    print("")

    for i in [j for j in os.listdir("./logs") if j.startswith("nsc_gen_attack_")]:
        a = AttackStatitic("NSC")
        a.loadFromFile("./logs/" + i)
        a.perturbationpoint = i.split("_")[-2]
        a.setProgramScore("4b45595f4b45595f4b45595f4b45595f")


main()
