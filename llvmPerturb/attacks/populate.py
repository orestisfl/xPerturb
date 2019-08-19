import matplotlib.pyplot as plt
import numpy as np
from scipy import stats # t-test
from termcolor import colored

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

def populateGraph(path, points_db, probability, left_label = None, right_label = None, bottom_label = None):
    print(path.split("/")[-1])
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
    plt.title(path.split("/")[-1])

    xTicks = []
    corrs = []
    letters = "abcdefghij"
    for i in range(len(ppts)):
        c = get_correctness_for_point(ppts[i], probability, points_db)
        a = get_activations_for_point(ppts[i], probability, points_db)
        if c == "":
            c = 0.0
        corrs.append(c)
        xTicks.append(r'${}\ _{{{}}}^{{{}}}$'.format(str(letters[i]), str(a), ppts[i]))

        y = [j[0] for j in attack_db if int(j[1]) == ppts[i]]
        t = calculate_t_test(ref, y)
        str_buff = letters[i] + " " + str(round(t[0], 2)) + " " + str(round(t[1], 2))
        color = ""
        if t[0]>0:
            color = "green"
        else:
            color = "red"

        #bump = " "*int(round(t[1]*10))
        bump = ""
        print colored(bump + str_buff, color)

        myX = [i] * len(y)
        plt.scatter(myX, y, s=5)
        plt.violinplot(y, [i], showmeans=True, showextrema=True, showmedians=True)
    plt.xticks(x, xTicks, rotation=45)
    print("")
    if left_label:
        plt.ylabel(left_label)
    if bottom_label:
        plt.xlabel(bottom_label)

    corrs = corrs + ([0] * ((len(x)-1) - len(corrs))) # Zero extend correctness barchart to cover all ticks

    plt2 = plt.twinx()  # instantiate a second axes that shares the same x-axis
    plt2.set_ylim(0,3)
    plt2.set_yticks([0, 0.25, 0.50, 0.75, 1])
    plt2.set_yticklabels([0, 0.25, 0.50, 0.75, 1])
    plt2.bar(range(len(ppts)), corrs, width=0.1, alpha=0.3)
    if right_label:
        plt2.set_ylabel(right_label) # we already handled the x-label with ax1
