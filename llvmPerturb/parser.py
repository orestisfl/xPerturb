
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
Activation: 16

"""

points = []

class CorrPointList():
    def __init__(self):
        self.Points = []
    def getPointsFromFile(self, executable):
        lista = []
        try:
            fd = open(executable + ".cvc", "r")
        except:
            return lista
        lines = fd.readlines()
        for line in lines:
            probability = int(line.strip().split(", ")[0])
            corr = float(line.strip().split(", ")[1])
            index = int(line.strip().split(", ")[2])
            self.Points.append(CorrPoint(probability, corr, index))
        self.Points.sort()
        self.Points.reverse()

    def printTopPoints(self, num):
        for i in self.Points[0:num]:
            print(i)

    def saveTopPoints(self, num, name):
        with open(name + "_top" + str(num) + ".cvc", "w") as fd:
            for i in self.Points[0:num]:
                fd.write(i)
                fd.write("\n")

    def plotPoints(self, name, top = 0):
        if not top:
            top = len(self.Points)
        x = [i.percent for i in self.Points[0:top]]
        y = [i.corr for i in self.Points[0:top]]
        plt.ylim(0,1)
        plt.xlim(0,100)
        plt.title("Executable " + name)
        plt.xlabel("Perturbation probability")
        plt.ylabel("Correctness")
        #advancedPlot(points)
        plt.scatter(x, y)
        plt.show()


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
    numberOfPointsToView = 25
    point_list = CorrPointList()
    files = ["./experiment_results/points_p25_all",
    "./experiment_results/points_p50_all",
    "./experiment_results/points_p90_all",
    "./experiment_results/points_p99_all",
    "./experiment_results/points_p5_all",
    "./experiment_results/points_p10_all"
    ]
    for i in files:
        point_list.getPointsFromFile(i)

    # point_list.printTopPoints(numberOfPointsToView)
    point_list.plotPoints("All points")



main()
