from __future__ import division # Float division
from ast import literal_eval as make_tuple
import sys
import re # reggex
from termcolor import colored

class ReferenceCalculator():
    def __init__(self, k):
        self.attacks = []
        self.mostCommonSumBytes = []
        self.mostCommonAbsBytes = []
        self.key = k

    def loadReferenceAttacks(self, fname, numberOf):
        # fname = "chess2016_ref_attack_500_"
        for i in range(numberOf):
            self.attacks.append(AttackStatitic(fname + str(i)))
            self.attacks[i].loadFromFile(fname + str(i))




class AttackStatitic():
    def __init__(self, t):
        self.title = t
        self.realTime = 0
        self.userTime = 0
        self.sysTime = 0
        self.sumCorrelationMatrix = []
        self.highestAbsMatrix = []
        self.keyGuessAbs = []
        self.keyGuessSum = []
        self.Score = -1

    def parseDaredevilData(self, data, times):
        # Key bytes, best candidates
        # Stored in a matrix of pairs. First is the byte, next if the calculated certainy acording to the techneque
        abs_ = re.findall("Best\ 10\ candidates\ for\ key\ byte\ \#[0-9][0-9]?\ according\ to\ highest\ abs\(bit_correlations\):\n((.*\n){10})", data)
        for keyByteList in abs_:
            self.highestAbsMatrix.append([(row.strip().split()[1], row.strip().split()[3]) for row in keyByteList[0].strip().split("\n")])

        # Key bytes, best candidates
        # Stored in a matrix of pairs. First is the byte, next if the calculated certainy acording to the techneque
        sum_ = re.findall("Best\ 10\ candidates\ for\ key\ byte\ \#[0-9][0-9]?\ according\ to\ sum\(abs\(bit_correlations\)\):\n((.*\n){10})", data)
        for keyByteList in sum_:
            self.sumCorrelationMatrix.append([(row.strip().split()[1], row.strip().split()[3]) for row in keyByteList[0].strip().split("\n")])

        # Most probable key sum(abs):
        sumAbsSection = re.findall("Most\ probable\ key\ sum\(abs\):\n((.*\n){10})", data)[0][0]
        self.keyGuessSum = re.findall("[0-9a-f]{32}", sumAbsSection)

        # Most probable key max(abs):
        maxAbsSection = re.findall("Most\ probable\ key\ max\(abs\):\n((.*\n){10})", data)[0][0]
        self.keyGuessAbs = re.findall("[0-9a-f]{32}", maxAbsSection)

        # Get Runtimes
        tl = times.split(" ")
        try:
            self.userTime = re.search("[0-9\.:]+", tl[0]).group()
            self.sysTime = re.search("[0-9\.:]+", tl[1]).group()
            self.realTime = re.search("[0-9\.:]+", tl[2]).group()
        except AttributeError:
            raise Exception("Memorytracefiles could not be parsed")

    def evaluateAttack(self, reference, key):
        self.evaluteCorrelationMatrix(self.sumCorrelationMatrix, reference.sumCorrelationMatrix, key)
        self.evaluteCorrelationMatrix(self.highestAbsMatrix, reference.highestAbsMatrix, key)

    def evaluteCorrelationMatrix(self, attackM, referenceM, key):
        keyByteList = [key[i:i+2] for i in range(0, len(key), 2)]

        lst = []
        ref = []

        for byteIndex in range(len(keyByteList)):
            for i in range(len(attackM[byteIndex])):
                found = False
                if attackM[byteIndex][i][0] == '0x'+ keyByteList[byteIndex]:
                    found = True
                    lst.append((attackM[byteIndex][i], i))
                    break
            if not found:
                lst.append((('0x'+ keyByteList[byteIndex], "0.0"), 16))

            for i in range(len(referenceM[byteIndex])):
                found = False
                if referenceM[byteIndex][i][0] == '0x'+ keyByteList[byteIndex]:
                    found = True
                    ref.append((referenceM[byteIndex][i], i))
                    break
            if not found:
                ref.append((('0x'+ keyByteList[byteIndex], "0.0"), 16))


        print("Index" + "\t" + "Byte" + "\t" + "Att-max".ljust(8) + "\t" + "Ref-max".ljust(8) + "\t" + "Att-pos" + "\t" + "Ref-pos")
        for i in range(len(lst)):
            print("{5}\t{0}\t{1}\t{2}\t{3}\t{4}".format(
            lst[i][0][0],
            lst[i][0][1].ljust(8),
            ref[i][0][1].ljust(8),
            colored(lst[i][1], 'green') if lst[i][1]>ref[i][1] else colored(lst[i][1], "red"),
            str(ref[i][1]),
            str(i+1)))
        print("\n")

    def setProgramScore(self, key):
        keyByteList = [key[i:i+2] for i in range(0, len(key), 2)]
        imSumScore = 0
        imAbsScore = 0
        for byteIndex in range(len(keyByteList)):
            if self.highestAbsMatrix[byteIndex][0][0] == '0x'+ keyByteList[byteIndex]:
                imAbsScore += 1
        for byteIndex in range(len(keyByteList)):
            if self.sumCorrelationMatrix[byteIndex][0][0] == '0x'+ keyByteList[byteIndex]:
                imSumScore += 1

        imSumScore = imSumScore/len(keyByteList)
        imAbsScore = imAbsScore/len(keyByteList)

        print(("abs", imAbsScore))
        print(("sum", imSumScore))

        print("")

    def loadFromFile(self, fn):
        fd = open(fn, "r")
        l = fd.readlines()
        l.remove("\n")
        fl = iter(l)
        fd.close()

        self.title = next(fl).strip()
        self.realTime = next(fl).strip()
        self.userTime = next(fl).strip()
        self.sysTime = next(fl).strip()

        for i in range(16):
            r = []
            for j in range(10):
                t = make_tuple(next(fl).strip())
                r.append(t)
            self.sumCorrelationMatrix.append(r)

        for i in range(16):
            r = []
            for j in range(10):
                t = make_tuple(next(fl).strip())
                r.append(t)
            self.highestAbsMatrix.append(r)

        next(fl)
        for i in range(10):
            self.keyGuessAbs.append(next(fl).strip())
        next(fl)
        for i in range(10):
            self.keyGuessSum.append(next(fl).strip())

    def saveToFile(self, fname):
        fd = open(fname, "w")
        fd.write(self.title)
        fd.write("\n")
        fd.write(self.realTime)
        fd.write("\n")
        fd.write(self.userTime)
        fd.write("\n")
        fd.write(self.sysTime)
        fd.write("\n")

        for li in self.sumCorrelationMatrix:
            fd.write("\n".join([str(pair_) for pair_ in li]))
            fd.write("\n")
        fd.write("\n")
        for li in self.highestAbsMatrix:
            fd.write("\n".join([str(pair_) for pair_ in li]))
            fd.write("\n")
        fd.write("\n")
        for li in self.keyGuessAbs:
            fd.write(li)
            fd.write("\n")
        fd.write("\n")
        for li in self.keyGuessSum:
            fd.write(li)
            fd.write("\n")
        fd.write("\n")

        fd.close()
