from __future__ import division
import matplotlib.pyplot as plt
import numpy as np
from scipy import stats # t-test
from termcolor import colored
import math

def get_activations_for_points(points, prob, points_db):
    # returns [(point, no_activations), ...]
    fd = open(points_db, "r")
    l = fd.readlines()
    fd.close()
    l2 = [(int(i.strip().split(", ")[-2]), float(i.strip().split(", ")[-1])) for i in l if int(i.strip().split(", ")[-2]) in points and int(i.strip().split(", ")[0]) == prob]
    return l2

def get_activations_for_point(point, prob, points_db):
    fd = open(points_db, "r")
    l = fd.readlines()
    fd.close()
    l2 = []
    for i in l:
        if int(i.strip().split(", ")[-2]) == point and int(i.strip().split(", ")[0]) == prob:
            return int(round(float(i.strip().split(", ")[-1])))
    return ""

def get_correctness_for_points(points, prob, points_db):
    # returns [(point, no_activations), ...]
    fd = open(points_db, "r")
    l = fd.readlines()
    fd.close()
    l2 = [(int(i.strip().split(", ")[-2]), float(i.strip().split(", ")[1])) for i in l if int(i.strip().split(", ")[-2]) in points and int(i.strip().split(", ")[0]) == prob]
    return l2

def get_correctness_for_point(point, prob, points_db):
    fd = open(points_db, "r")
    l = fd.readlines()
    fd.close()
    l2 = []
    for i in l:
        if int(i.strip().split(", ")[-2]) == point and int(i.strip().split(", ")[0]) == prob:
            return float(i.strip().split(", ")[1])
    return ""

def plot_reference_lines(ref, x_length):
    dataset = [float(i.strip().split()[0]) for i in ref]
    q1, q2, q3 = np.percentile(dataset,[25, 50, 75])
    upper = q3 + (q3-q1)*1.5
    lower = q1 - (q3-q1)*1.5
    max_ = max(dataset)
    min_ = min(dataset)
    x = range(-1, x_length+1)
    y1 = [q1] * len(x)
    y2 = [q2] * len(x)
    y3 = [q3] * len(x)
    yMax = [max_] * len(x)
    yMin = [min_] * len(x)

    plt.fill_between(x, y1, y3, alpha=0.1)
    plt.plot(x, y1, color="blue", alpha=0.1)
    plt.plot(x, y2, color="blue", alpha=0.1)
    plt.plot(x, y3, color="blue", alpha=0.1)
    plt.plot(x, yMax, color="blue", alpha=0.1)
    plt.plot(x, yMin, color="blue", alpha=0.1)

def calculate_t_test(ref, oth_vector):
    ref_vector = [float(i.strip().split()[0]) for i in ref]
    return stats.ttest_ind(ref_vector, oth_vector, equal_var = False)

def getDataFromFile(path):
    fd = open(path)
    lines = fd.readlines()
    fd.close()
    try:
        ref = lines[0:lines.index("\n")]
        att = lines[lines.index("\n")+1:]
    except ValueError:
        print("Did not find readable data in file " + path)
        return [], [], 1
    return ref, att, 0

def plotDiscussionGraph(path, points_db, probability):
    markerDict={
    "ches2016_points_db.cvc": ("o", ["lightcoral","indianred", "brown"]),
    "kryptologik_points_db.cvc": ("s", ["lightsteelblue", "cornflowerblue", "midnightblue"]),
    "nsc2013_gen_points_db.cvc": ("X", ["yellowgreen", "yellowgreen", "darkolivegreen"])
    }
    plt.ylabel("P - Value")
    plt.xlabel("Correctness attraction")
    plt.ylim(2.02, -0.02)
    plt.xlim(-0.02, 1.02)

    ref, att, err = getDataFromFile(path)
    if err:
        return
    # Extract points from attack data
    ppts = [int(i.strip().split()[2]) for i in att]
    ppts = list(dict.fromkeys(ppts)) #Remove duplicates
    ppts.sort()
    x = range(0, len(ppts)+1)
    attack_db = [(float(i.strip().split()[0]), i.strip().split()[2]) for i in att]
    corrs = []
    letters = "abcdefghij"
    for i in range(len(ppts)):
        c = get_correctness_for_point(ppts[i], probability, points_db)
        if c == "":
            c = 0.0

        y = [j[0] for j in attack_db if int(j[1]) == ppts[i]]
        t = calculate_t_test(ref, y)
        if t[0] < 0:
            t = [t[0], 2-t[1]]
        myX = [i] * len(y)
        _marker = markerDict[points_db.split("/")[-1]][0]
        _color = markerDict[points_db.split("/")[-1]][1][1]
        _label = points_db.split("/")[-1].split("_")[0]
        plt.scatter(c, t[1], marker=_marker, color=_color, s=probability/2)
        yticks_part = [abs(j)/100 for j in range(0, 125, 25)]
        yticks_part.reverse()
        plt.yticks([f/100 for f in range(0,225, 25)], [abs(j)/100 for j in range(0, 100, 25)]+ yticks_part)

def populateGraph(path, points_db, probability):
    left_label = "Attack-score"
    bottom_label = r"$_{activations}\ Perturbation\ point\ ^{p\ value}_{corr. attr.}$"
    ref, att, err = getDataFromFile(path)
    if err:
        return

    # Extract points from attack data
    ppts = [int(i.strip().split()[2]) for i in att]
    ppts = list(dict.fromkeys(ppts)) #Remove duplicates
    ppts.sort()

    plot_reference_lines(ref, len(ppts))
    x = range(0, len(ppts)+1)

    attack_db = [(float(i.strip().split()[0]), i.strip().split()[2]) for i in att]
    plt.ylim(0.25,1.02)
    plt.xlim(-0.5, len(ppts))
    print(path.split("/")[-1] + " - 30 attacks")
    plt.title(path.split("/")[-1].replace("_", " ") + "% probability - 30 attacks")

    xTicks = []
    corrs = []
    letters = "abcdefghij"
    for i in range(len(ppts)):
        c = get_correctness_for_point(ppts[i], probability, points_db)
        a = get_activations_for_point(ppts[i], probability, points_db)
        if c == "":
            c = 0.0
        corrs.append(c)
        y = [j[0] for j in attack_db if int(j[1]) == ppts[i]]
        t = calculate_t_test(ref, y)
        str_buff = str(t[1])
        color = ""
        if t[0]>0:
            color = "green"
        else:
            color = "red"
        bump = ""
        print()
        xt = (r'$_{{{}}}\ {}\ _{{{}}}^{{{}}}$'.format(str(a), str(letters[i]), str(c), str(round(t[1], 2))))
        xTicks.append(xt)
        myX = [i] * len(y)
        plt.violinplot(y, [i], showmeans=False, showextrema=True, showmedians=True)
    plt.xticks(x, xTicks, rotation=45)

    print("")

    plt.ylabel(left_label)
    plt.xlabel(bottom_label)
    plt.tight_layout()
