
import numpy as np
import matplotlib.pyplot as plt

color_list = ['#e6194b', '#3cb44b', '#ffe119', '#4363d8', '#f58231', '#911eb4', '#46f0f0', '#f032e6', '#bcf60c', '#fabebe', '#008080', '#e6beff', '#9a6324', '#fffac8', '#800000', '#aaffc3', '#808000', '#ffd8b1', '#000075', '#808080', '#ffffff', '#000000']
path = "/home/koski/xPerturb/llvmPerturb/"
result_path = "/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/graphs/"

"""
Example of a experiment entry bellow:

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
            if probability not in [5, 10, 50, 90, 99]:
                continue
            corr = float(line.strip().split(", ")[1])
            if self.Title == "nsc2013" and corr == 1.0:
                continue # NSC has no real points with 100% corretness, all are false positives when examined further
            index = int(line.strip().split(", ")[2])
            actv = str(line.strip().split(", ")[3])
            cp = CorrPoint(probability, corr, index, actv)
            if cp not in self.Points:
                self.Points.append(cp)
        fd.close()

    def plotTopPoints(self, num, prob):
        print("Top " + str(num) + " for " + self.Title + " at " + str(prob))
        top = [i for i in self.Points if i.percent == prob and i.corr != 1]
        top.sort()
        top.reverse()
        colorCounter = 0
        plt.ylim(0,1)
        plt.xlim(0,100)
        plt.title("Top - 10 highest correctness for " + self.Title)
        plt.xlabel("Perturbation probability")
        plt.ylabel("Correctness")
        for i in top[0:num]:
            top_point_xy = [(y.percent, y.corr) for y in self.Points if i.index == y.index and y.corr >0]
            if top_point_xy == []:
                continue
            top_point_xy.append((0,1))
            x, y = zip(*top_point_xy)
            z = np.polyfit(x, y, 1)
            predicted = np.polyval(z, range(0, 101))
            plt.plot(range(0,101), predicted, c = color_list[colorCounter])
            plt.scatter(x, y, c = color_list[colorCounter])
            colorCounter += 1

    def saveTopPoints(self, num, prob):
        fd = open(path + "experiment_results/" + self.Title + "_top" + str(num) + "_p" + str(prob) + ".pts", "w")
        top = [i for i in self.Points if i.percent == prob and i.corr != 1]
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
        if top != 0:
            print(len(y))
        plt.ylim(0,1)
        plt.xlim(0,100)
        plt.title("Correctness attraction for " + self.Title)
        plt.xlabel("Perturbation probability")
        plt.ylabel("Correctness")
        plt.scatter(x, y)

class CorrPoint():
    def __init__(self, p, c, i, a):
        self.percent = p
        self.corr = c
        self.index = i
        self.activations = a
    def __lt__(self, other):
        if(self.corr == other.corr):
            return self.percent < other.percent
        else:
            return self.corr < other.corr
    def __eq__(self, other):
        return self.percent == other.percent and self.index == other.index

    def __str__(self):
        return str(self.percent) + ", " + str(self.corr) + ", " + str(self.index) + ", " + str(self.activations)

def ches2016_all():
    point_list = CorrPointList()
    point_list.setTitle("ches2016")
    ches2016_files = [path + "experiment_results/correctness/chess/chess2016_points_db.cvc"]
    for i in ches2016_files:
        point_list.getPointsFromFile(i)
    point_list.plotPoints()
    plt.savefig(result_path + "correctness_" + point_list.Title)
    plt.clf()
    point_list.plotTopPoints(10, 90)
    point_list.saveTopPoints(10, 10)
    point_list.saveTopPoints(10, 50)
    point_list.saveTopPoints(10, 90)
    plt.savefig(result_path + "correctness_" + point_list.Title + "top_10")
    plt.clf()

def kryptologik_all():
    point_list = CorrPointList()
    point_list.setTitle("kryptologik")
    kryptologik_files = [path + "experiment_results/correctness/kryptologik/kryptologik_points_db.cvc"]
    for i in kryptologik_files:
        point_list.getPointsFromFile(i)
    point_list.plotPoints()
    plt.savefig(result_path + "correctness_" + point_list.Title)
    plt.clf()
    point_list.plotTopPoints(10, 90)
    point_list.saveTopPoints(10, 10)
    point_list.saveTopPoints(10, 50)
    point_list.saveTopPoints(10, 90)
    plt.savefig(result_path + "correctness_" + point_list.Title + "top_10")
    plt.clf()

def nsc2013_all():
    point_list = CorrPointList()
    point_list.setTitle("nsc2013")
    nsc2013_files = [path + "experiment_results/correctness/nsc/nsc2013_gen_points_db.cvc"]
    for i in nsc2013_files:
        point_list.getPointsFromFile(i)
    point_list.plotPoints()
    plt.savefig(result_path + "correctness_" + point_list.Title)
    plt.clf()
    point_list.plotTopPoints(10, 90)
    point_list.saveTopPoints(10, 10)
    point_list.saveTopPoints(10, 50)
    point_list.saveTopPoints(10, 90)
    plt.savefig(result_path + "correctness_" + point_list.Title + "top_10")
    plt.clf()

def main():
    # Generate and save graphs over correctness attraction for different perturbation points over warious probabilities into the experiment_results folder
    plt.title("Perturbation points")
    ches2016_all()
    kryptologik_all()
    nsc2013_all()
main()
