
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


def readFile(percent):
    try:
        fd = open("./experiment_results/results_pone_p" + str(percent) + ".txt", "r")
    except:
        return
    print("Working on file: " + str(percent))
    while fd.readline():
        try:
            corr = float(fd.readline().strip().split(" ")[-1])
            succ = fd.readline()
            fails = fd.readline()
            errors = fd.readline()
            index = int(fd.readline().strip().split(" ")[-1])
            fd.readline()
        except:
            fd.close()
            return
        points.append(CorrPoint(percent, corr, index))


def main():
    for percentage in range(0, 101):
        readFile(percentage)
    points.sort()

    for point in points:
        print(point)



main()
