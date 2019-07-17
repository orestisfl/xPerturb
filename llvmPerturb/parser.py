
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
        self.Title = ""
    def setTitle(self, t):
        self.Title = t
    def getPointsFromFile(self, executable):
        lista = []
        try:
            fd = open(executable, "r")
        except:
            return lista
        lines = fd.readlines()
        for line in lines:
            probability = int(line.strip().split(", ")[0])
            corr = float(line.strip().split(", ")[1])
            index = int(line.strip().split(", ")[2])
            self.Points.append(CorrPoint(probability, corr, index))
        fd.close()

    def plotTopPoints(self, num, prob):
        #print("Top " + str(num) + " for " + self.Title + " at " + str(prob))
        top = [i for i in self.Points if i.percent == prob]
        top.sort()
        top.reverse()
        colorCounter = 0
        plt.ylim(0,1)
        plt.xlim(0,100)
        plt.title("Top - 10 highest correctness")
        plt.xlabel("Perturbation probability")
        plt.ylabel("Correctness")
        for i in top[0:num]:
            top_point_xy = [(y.percent, y.corr) for y in self.Points if i.index == y.index]
            x, y = zip(*top_point_xy)
            z = np.polyfit(x, y, 1)
            p = np.poly1d(z)
            plt.plot(x, p(x), c = color_list[colorCounter])
            plt.scatter(x, y, c = color_list[colorCounter])
            colorCounter += 1
            #print(i)

    def saveTopPoints(self, num, prob):
        fd = open("./experiment_results/" + self.Title + "_top" + str(num) + "_p" + str(prob) + ".pts", "w")
        top = [i for i in self.Points if i.percent == prob]
        top.sort()
        top.reverse()
        for i in top[0:num]:
            fd.write(str(i))
            fd.write("\n")
        fd.close()

    def plotPoints(self, top = 0):
        if not top:
            top = len(self.Points)
        x = [i.percent for i in self.Points[0:top]]
        y = [i.corr for i in self.Points[0:top]]
        plt.ylim(0,1)
        plt.xlim(0,100)
        plt.title(self.Title)
        plt.xlabel("Perturbation probability")
        plt.ylabel("Correctness")
        plt.scatter(x, y)

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
        return str(self.percent) + ", " + str(self.corr) + ", " + str(self.index)

def ches2016_all():
    point_list = CorrPointList()
    point_list.setTitle("ches2016")
    ches2016_files = ["./experiment_results/old/ches2016/points_p25_all.cvc",
    "./experiment_results/old/ches2016/points_p50_all.cvc",
    "./experiment_results/old/ches2016/points_p90_all.cvc",
    "./experiment_results/old/ches2016/points_p99_all.cvc",
    "./experiment_results/old/ches2016/points_p5_all.cvc",
    "./experiment_results/old/ches2016/points_p10_all.cvc"
    ]
    for i in ches2016_files:
        point_list.getPointsFromFile(i)

    plt.subplot(2,3,1)
    point_list.plotPoints()
    plt.subplot(2,3,4)
    point_list.plotTopPoints(10, 90)
    point_list.saveTopPoints(10, 90)

def kryptologik_all():
    point_list = CorrPointList()
    point_list.setTitle("kryptologik")
    kryptologik_files = ["./experiment_results/kryptologik_points_px_n23721.cvc"]
    for i in kryptologik_files:
        point_list.getPointsFromFile(i)

    plt.subplot(2,3,2)
    point_list.plotPoints()
    plt.subplot(2,3,5)
    point_list.plotTopPoints(10, 90)
    point_list.saveTopPoints(10, 90)

def nsc2013_all():
    point_list = CorrPointList()
    point_list.setTitle("nsc2013")
    # nsc2013_files = ["./experiment_results/nsc2013 - noenc_points_px_n114.cvc"]
    # nsc2013 - noenc - generator_points_p50_all
    nsc2013_files = ["./experiment_results/nsc2013 - noenc - generator_points_p50_all.cvc",
    "./experiment_results/nsc2013 - noenc - generator_points_p99_all.cvc",
    "./experiment_results/nsc2013 - noenc - generator_points_p5_all.cvc"]
    for i in nsc2013_files:
        point_list.getPointsFromFile(i)

    plt.subplot(2,3,3)
    point_list.plotPoints()
    plt.subplot(2,3,6)
    point_list.plotTopPoints(10, 90)
    point_list.saveTopPoints(10, 90)

def main():
    plt.title("Perturbation points")
    ches2016_all()
    kryptologik_all()
    nsc2013_all()
    plt.show()
main()
