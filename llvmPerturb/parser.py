
import numpy as np
import matplotlib.pyplot as plt

color_list = ['#e6194b', '#3cb44b', '#ffe119', '#4363d8', '#f58231', '#911eb4', '#46f0f0', '#f032e6', '#bcf60c', '#fabebe', '#008080', '#e6beff', '#9a6324', '#fffac8', '#800000', '#aaffc3', '#808000', '#ffd8b1', '#000075', '#808080', '#ffffff', '#000000']

"""
Executable: /home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/linked_challenge_pone.bc
Correctness ratio: 0.595
Success: 595
Fails: 401
Errors: 4
Index: 0

"""

points = []

class CorrPoint():
    def __init__(self, p, c, i):
        self.percent = p
        self.corr = c
        self.index = i
    def __lt__(self, other):
        if(self.corr == other.corr):
            return self.percent < other.percent
        else:
            return self.corr < other.corr
    def __str__(self):
        return str((self.percent, self.corr, self.index))


def readFile(filename):
    lista = []
    try:
        fd = open(filename, "r")
    except:
        return lista
    lines = fd.readlines()
    for line in lines:
        probability = int(line.strip().split(", ")[0])
        corr = float(line.strip().split(", ")[1])
        index = int(line.strip().split(", ")[2])
        points.append(CorrPoint(probability, corr, index))
    return lista

def advancedPlot(points):
    colorCounter = 0
    indx = set([i.index for i in points])
    for ind in indx:
        x = [i.percent for i in points if i.index == ind]
        y = [i.corr for i in points if i.index == ind]
        z = np.polyfit(x, y, 1)
        p = np.poly1d(z)
        plt.plot(x, p(x), c = color_list[colorCounter])

        # plt.scatter(x, y, c = color_list[colorCounter])
        colorCounter += 1



def main():
    ## readFile("./experiment_results/results_pone_p.txt")
    readFile("./experiment_results/points_pALL_top10.cvc")

    points.sort()
    for i in points:
        print(i)
    # new_list = [expression(i) for i in old_list if filter(i)]
    x = [i.percent for i in points]
    y = [i.corr for i in points]
    plt.ylim(0,1)
    plt.xlim(0,100)
    plt.title("Perturbationpoint " + str(points[0].index))
    plt.xlabel("Perturbation probability")
    plt.ylabel("Correctness")
    advancedPlot(points)
    #plt.scatter(x, y)
    plt.show()

    # for point in points:
    #     print(point)



main()
