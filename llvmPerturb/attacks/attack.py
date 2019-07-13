#!/usr/bin/env python
import re # reggex
import sys
from deadpool_dca import *
import subprocess # Popen

class AttackStatitic():
    def __init__(self):
        self.realTime = 0
        self.userTime = 0
        self.sysTime = 0
        self.sumCorrelationMatrix = []
        self.highestAbsMatrix = []
        
    def parseData(self, data, times):
        abss = re.findall("Best\ 10\ candidates\ for\ key\ byte\ \#[0-9][0-9]?\ according\ to\ highest\ abs\(bit_correlations\):\n((.*\n){10})", data)
        self.highestAbsMatrix = [re.findall("0x[0-9a-f][0-9a-f]", i[0]) for i in abss]

        corr = re.findall("Best\ 10\ candidates\ for\ key\ byte\ \#[0-9][0-9]?\ according\ to\ sum\(abs\(bit_correlations\)\):\n((.*\n){10})", data)
        self.sumCorrelationMatrix = [re.findall("0x[0-9a-f][0-9a-f]", i[0]) for i in corr]


        # Get Runtimes
        tl = times.split(" ")
        self.userTime = re.search("[0-9\.:]+", tl[0]).group()
        self.sysTime = re.search("[0-9\.:]+", tl[1]).group()
        self.realTime = re.search("[0-9\.:]+", tl[2]).group()
        print(self.userTime)
        print(self.sysTime)
        print(self.realTime)



class TraceChess2016():
    def __init__(self, b):
        self.BinaryPath = b
        self.runns = 5 # Original value = 2000

    def processinput(self, iblock, blocksize):
        p='%0*x' % (2*blocksize, iblock)
        return (None, [p[j*2:(j+1)*2] for j in range(len(p)/2)])

    def processoutput(self, output, blocksize):
        return int(''.join([x for x in output.split('\n') if x.find('OUTPUT')==0][0][10:].split(' ')), 16)


    def accuireOriginalTrace(self):
        #subprocess.Popen(["rm mem_*"], shell=True)
        #subprocess.Popen(["rm stack_*"], shell=True)
        T = TracerGrind(self.BinaryPath + 'src/wb_reference', self.processinput, self.processoutput, ARCH.amd64, 16,  addr_range='0x108000-0x130000')
        # Tracing only the first round:
        # T=TracerGrind('../target/wb_challenge', processinput, processoutput, ARCH.amd64, 16,  addr_range='0x108000-0x10c000')
        T.run(self.runns) # Original number 2000
        bin2daredevil(configs={'attack_sbox':   {'algorithm':'AES', 'position':'LUT/AES_AFTER_SBOX'},
                               'attack_multinv':{'algorithm':'AES', 'position':'LUT/AES_AFTER_MULTINV'}})

    def accuireTransformedTrace(self, perturbationProbability, perturbationPoint):
        T = TracerGrind(self.BinaryPath + "perturbations/wb_p_" + str(perturbationProbability) + "_" + str(perturbationPoint), self.processinput, self.processoutput, ARCH.amd64, 16, addr_range='0x400000-0x440000')
        # T=TracerGrind('../target/wb_challenge', self.processinput, self.processoutput, ARCH.amd64, 16,  addr_range='0x108000-0x130000')
        # Tracing only the first round:
        # T=TracerGrind('../target/wb_challenge', processinput, processoutput, ARCH.amd64, 16,  addr_range='0x108000-0x10c000')
        T.run(self.runns) # Original number 2000
        bin2daredevil(configs={'attack_sbox':   {'algorithm':'AES', 'position':'LUT/AES_AFTER_SBOX'},
                               'attack_multinv':{'algorithm':'AES', 'position':'LUT/AES_AFTER_MULTINV'}})

    def performDaredevilAttack(self):
        sp = subprocess.Popen(["time", "daredevil", "-c", "mem_addr1_rw1_" + str(self.runns) + "_32400.attack_sbox.config"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = sp.communicate()
        atts = AttackStatitic()
        atts.parseData(out, err)





class TraceNsc2013():
    pass

class TraceKryptologik():
    pass


def runRegularAttack(targets):
    for t in targets:
        #t.accuireOriginalTrace()
        t.performDaredevilAttack()

def runPerturbationAttack():
    pass


def main():
    targets = []
    targets.append(TraceChess2016("../example_programs/wbs_aes_ches2016/"))

    runRegularAttack(targets)


main()
