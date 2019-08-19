#!/usr/bin/python
from __future__ import division # Float division
import sys
import os
import random
from tqdm import tqdm # Progress bar
import time
import resource
import numpy as np
import subprocess # Popen
import matplotlib.pyplot as plt
import scipy.stats as stats

lastSum = 0

def only_char(s):
    res = ""
    for i in s:
        if i.isalpha():
            res = res+i
    return res

class CryptoImplementation():
    def __init__(self):
        self.title = ""
    def input_(self):
        pass
    def time_reference(self):
        times = []
        global lastSum
        for inp in range(self.numberOfInputs):
            cmd = [self.path + "src/wb_reference"] + self.input_().split()
            p1 = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            out, err = p1.communicate()
            info = resource.getrusage(resource.RUSAGE_CHILDREN)
            rc1 = p1.returncode
            if not int(rc1):
                times.append((info.ru_utime + info.ru_stime - lastSum)*1000)
                lastSum = info.ru_utime + info.ru_stime
        return times

    def variance(self):
        pass

    def time_perturbation(self, point, probability):
        times = []
        global lastSum
        for inp in range(self.numberOfInputs):
            cmd = [self.path + "perturbations/wb_p_" + str(probability) + "_" + str(point)] + self.input_().split()
            p1 = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            out, err = p1.communicate()
            rc1 = p1.returncode
            info = resource.getrusage(resource.RUSAGE_CHILDREN)
            if not int(rc1):
                times.append((info.ru_utime + info.ru_stime - lastSum)*1000)
                lastSum = info.ru_utime + info.ru_stime
        return times

    def get_correctness(self, probability):
        fd = open("/home/koski/xPerturb/llvmPerturb/experiment_results/correctness/" + only_char(self.title) + "/" + self.title + "_points_db.cvc", "r")
        fdl = fd.readlines()
        fd.close()
        return [(i.strip().split(", ")[1], i.strip().split(", ")[2]) for i in fdl if int(i.strip().split(", ")[0]) == probability]

class Ches2016(CryptoImplementation):
    def __init__(self):
        self.top_points = [48000, 11400, 19800, 11700, 28200, 16200, 24600, 7300, 59700, 10000]
        self.path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_ches2016/"
        self.numberOfInputs = 1000
        self.title = "ches2016"
    def input_(self):
        ret = '{0:0{1}X}'.format(random.randint(0, 255),2)
        for i in range(15):
             ret = ret + " " + '{0:0{1}X}'.format(random.randint(0, 255),2)
        return ret

class Kryptologik(CryptoImplementation):
    def __init__(self):
        self.top_points = [8100, 18900, 12000, 3600, 12300, 15000, 1800, 19800, 16200, 16900]
        self.path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_kryptologik/"
        self.numberOfInputs = 1000
        self.title = "kryptologik"
    def input_(self):
        ret = '{0:0{1}X}'.format(random.randint(0, 255),2)
        for i in range(15):
            ret = ret + " " + '{0:0{1}X}'.format(random.randint(0, 255),2)
        return ret

class Nsc2013(CryptoImplementation):
    # /home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_nsc2013_variants_generator/src/nosuchcon_2013_whitebox_noenc_generator
    def __init__(self):
        self.top_points = [0, 12, 6, 122, 120, 118, 174, 288, 280, 108]
        self.path = "/home/koski/xPerturb/llvmPerturb/example_programs/wbs_aes_nsc2013_variants_generator/"
        self.numberOfInputs = 1000
        self.title = "nsc2013"
    def input_(self):
        ret = '{0:0{1}X}'.format(random.randint(0, 255),2)
        for i in range(15):
            ret = ret + '{0:0{1}X}'.format(random.randint(0, 255),2)
        return ret
    def time_reference(self):
        times = []
        global lastSum
        for inp in range(self.numberOfInputs):
            cmd = [self.path + "src/nosuchcon_2013_whitebox_noenc_generator"]
            p1 = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            out, err = p1.communicate()
            info = resource.getrusage(resource.RUSAGE_CHILDREN)
            rc1 = p1.returncode
            if not int(rc1):
                times.append((info.ru_utime + info.ru_stime - lastSum)*1000)
                lastSum = info.ru_utime + info.ru_stime
        return times


class TimeGraph():
    def __init__(self):
        self.result_path = "/home/koski/xPerturb/llvmPerturb/experiment_results/timing/graphs/"
        self.fig, self.ax = plt.subplots()
        # self.ax.set_xlim(0.0, 5)     # set the xlim to left, right
        # self.ax.set_ylim(0.0, 800)     # set the xlim to left, right

    def get_variance(self, l):
        return round(np.var(l), 2)

    def get_mean(self, l):
        return round(np.mean(l), 2)

    def create_graph(self, implementation, probability):
        plt.title("Execution time " + str(probability) + "% perturbation probability")
        plt.ylabel("Occurences")
        plt.xlabel("Time (ms)")

        t = implementation.time_reference()
        t.sort()
        fit = stats.norm.pdf(t, np.mean(t), np.std(t)) # Create the curve

        self.ax.plot(t, fit, label="ref: " + r"$\mu$: " + str(self.get_mean(t)) + r", $\sigma ^{2}$: " + str(self.get_variance(t)))
        self.ax.fill_between(t, fit, color='#539ecd', alpha=0.5)

        corr = implementation.get_correctness(probability)

        letters = "abcdefghijk"
        for i in tqdm(range(len(implementation.top_points))):
            t = implementation.time_perturbation(implementation.top_points[i], probability)
            t.sort()
            fit = stats.norm.pdf(t, np.mean(t), np.std(t))
            pc = -1
            for j in corr:
                if int(j[1]) == implementation.top_points[i]:
                    pc = int(round(float(j[0])*100))
                    break
            if pc == -1:
                print(str(implementation.top_points[i]) + " was not found in:")
                print(corr)
            self.ax.plot(t, fit, label=letters[i] + ", c: " + str(pc)+"%, " + r"$\mu$: " + str(self.get_mean(t)) + r", $\sigma ^{2}$: " + str(self.get_variance(t)))
            self.ax.legend(title="Point, Correctness, Mean, Variance")
        self.fig.savefig(self.result_path + implementation.path.split("/")[-2] + "_" + str(probability))

def main():
    implementations = [Ches2016(), Kryptologik(), Nsc2013()]
    for i in range(len(implementations)):
        tg10 = TimeGraph()
        tg50 = TimeGraph()
        tg90 = TimeGraph()
        tg10.create_graph(implementations[i], 10)
        tg50.create_graph(implementations[i], 50)
        tg90.create_graph(implementations[i], 90)
main()
