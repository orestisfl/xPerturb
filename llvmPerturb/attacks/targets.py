import sys
from deadpool_dca import *
import subprocess # Popen
from tqdm import tqdm # Progress bar
import attack_statistics

class TraceChess2016():
    def __init__(self, b, r):
        self.BinaryPath = b
        self.runns = r # Original value = 2000
        self.corectKey = "dec1a551f1eddec0de4b1dae5c0de511"

    def processinput(self, iblock, blocksize):
        p='%0*x' % (2*blocksize, iblock)
        return (None, [p[j*2:(j+1)*2] for j in range(len(p)/2)])

    def processoutput(self, output, blocksize):
        return int(''.join([x for x in output.split('\n') if x.find('OUTPUT')==0][0][10:].split(' ')), 16)

    def accuireTrace(self, ppr = None, ppo = None):
        if ppr and ppo:
            self.accuireTransformedTrace(ppr, ppo)
        else:
            self.accuireReferenceTrace()

    def accuireReferenceTrace(self):
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
        return out, err

class TraceNsc2013():
    pass

class TraceKryptologik():
    def __init__(self, b, r):
        self.BinaryPath = b
        self.runns = r # Original value = 2000
        self.corectKey = ""

    def processinput(self, iblock, blocksize):
        p='%0*x' % (2*blocksize, iblock)
        return (None, [p[j*2:(j+1)*2] for j in range(len(p)//2)])

    def processoutput(self, output, blocksize):
        return int(output, 16)

    def accuireTrace(self, ppr = None, ppo = None):
        if ppr and ppo:
            self.accuireTransformedTrace(ppr, ppo)
        else:
            self.accuireReferenceTrace()

    def accuireReferenceTrace(self):
        T = TracerGrind(self.BinaryPath + 'src/wb_reference', self.processinput, self.processoutput, ARCH.amd64, 16)
        # To run kryptologik it neds the file DemoKey_table.bin in its working directory. Add this file and rerun
        T.run(self.runns)# original 200
        bin2daredevil(config={'algorithm':'AES', 'position':'LUT/AES_AFTER_SBOX'}) # keywords=filters,

    def accuireTransformedTrace(self, prob, point):
        T=TracerGrind(self.BinaryPath + 'perturbations/wb_p_' + str(prob) + '_' + str(point), self.processinput, self.processoutput, ARCH.amd64, 16)
        T.run(self.runns)# original 200
        bin2daredevil(config={'algorithm':'AES', 'position':'LUT/AES_AFTER_SBOX'}) # keywords=filters,

    def performDaredevilAttack(self):
        # mem_stack_w1_200_33544
        sp = subprocess.Popen(["time", "daredevil", "-c", "stack_w1_" + str(self.runns) + "_33536.config"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = sp.communicate()
        return out, err
