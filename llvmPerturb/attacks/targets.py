import os
from subprocess import Popen, PIPE

from deadpool_dca import ARCH, TracerGrind, bin2daredevil


class TraceChess2016:
    def __init__(self, b, r):
        self.BinaryPath = b
        self.runns = r  # Original value = 2000
        self.corectKey = "dec1a551f1eddec0de4b1dae5c0de511"

    def processinput(self, iblock, blocksize):
        p = "%0*x" % (2 * blocksize, iblock)
        return None, [p[j * 2 : (j + 1) * 2] for j in range(len(p) / 2)]

    def processoutput(self, output, blocksize):
        return int("".join([x for x in output.split("\n") if x.find("OUTPUT") == 0][0][10:].split(" ")), 16,)

    def accuireTrace(self, ppr=None, ppo=None):
        if ppr and ppo:
            self.accuireTransformedTrace(ppr, ppo)
        else:
            self.accuireReferenceTrace()

    def accuireReferenceTrace(self):
        T = TracerGrind(
            self.BinaryPath + "src/wb_reference",
            self.processinput,
            self.processoutput,
            ARCH.amd64,
            16,
            addr_range="0x108000-0x3ffffff",
            debug=True,
        )
        # Tracing only the first round:
        # T=TracerGrind('../target/wb_challenge', processinput, processoutput, ARCH.amd64, 16,  addr_range='0x108000-0x10c000')
        T.run(self.runns)  # Original number 2000
        bin2daredevil(
            configs={
                "attack_sbox": {"algorithm": "AES", "position": "LUT/AES_AFTER_SBOX"},
                "attack_multinv": {"algorithm": "AES", "position": "LUT/AES_AFTER_MULTINV",},
            }
        )

    def accuireTransformedTrace(self, perturbationProbability, perturbationPoint):
        T = TracerGrind(
            self.BinaryPath + "perturbations/wb_p_" + str(perturbationProbability) + "_" + str(perturbationPoint),
            self.processinput,
            self.processoutput,
            ARCH.amd64,
            16,
            addr_range="0x400000-0x440000",
            debug=True,
        )
        # T=TracerGrind('../target/wb_challenge', self.processinput, self.processoutput, ARCH.amd64, 16,  addr_range='0x108000-0x130000')
        # Tracing only the first round:
        # T=TracerGrind('../target/wb_challenge', processinput, processoutput, ARCH.amd64, 16,  addr_range='0x108000-0x10c000')
        T.run(self.runns)  # Original number 2000
        bin2daredevil(
            configs={
                "attack_sbox": {"algorithm": "AES", "position": "LUT/AES_AFTER_SBOX"},
                "attack_multinv": {"algorithm": "AES", "position": "LUT/AES_AFTER_MULTINV",},
            }
        )

    def performDaredevilAttack(self):
        fl = os.listdir(".")
        f = [i for i in fl if i.startswith("mem_addr1_rw1_" + str(self.runns)) and i.endswith(".attack_sbox.config")][0]
        sp = Popen(["time", "daredevil", "-c", f], stdout=PIPE, stderr=PIPE,)
        out, err = sp.communicate()
        return out, err


class TraceNsc2013:
    def __init__(self, b, r):
        self.BinaryPath = b
        self.runns = r  # Original value = 2000
        self.corectKey = "4b45595f4b45595f4b45595f4b45595f"

    def processinput(self, iblock, blocksize):
        return None, ["%0*x" % (2 * blocksize, iblock)]

    def processoutput(self, output, blocksize):
        # print(''.join([x for x in output.split('\n') if x.find('Output')==0][0][10:57].split(' ')))
        return int("".join([x for x in output.split("\n") if x.find("Output") == 0][0][10:57].split(" ")), 16,)

    def accuireTrace(self, ppr=None, ppo=None):
        if ppr and ppo:
            self.accuireTransformedTrace(ppr, ppo)
        else:
            self.accuireReferenceTrace()

    def accuireReferenceTrace(self):
        Popen(
            [self.BinaryPath + "src/nosuchcon_2013_whitebox_noenc_generator"], shell=True,
        )
        T = TracerGrind(
            self.BinaryPath + "src/wb_reference",
            self.processinput,
            self.processoutput,
            ARCH.amd64,
            16,
            addr_range="0x108000-0x130000",
        )  # filters=[DefaultFilters.stack_w1]
        T.run(self.runns)  # 25 Original
        bin2daredevil(config={"algorithm": "AES", "position": "LUT/AES_AFTER_SBOX"})  # keyword=DefaultFilters.stack_w1,
        print("Done!")

    def accuireTransformedTrace(self, prob, point):
        Popen(
            [self.BinaryPath + "perturbations/wb_p_" + str(prob) + "_" + str(point)], shell=True,
        )
        T = TracerGrind(
            self.BinaryPath + "perturbations/nosuchcon_2013_whitebox_noenc",
            self.processinput,
            self.processoutput,
            ARCH.amd64,
            16,
            addr_range="0x108000-0x130000",
        )  # filters=[DefaultFilters.stack_w1]
        T.run(self.runns)  # 25 Original
        bin2daredevil(config={"algorithm": "AES", "position": "LUT/AES_AFTER_SBOX"})  # keyword=DefaultFilters.stack_w1,

    def performDaredevilAttack(self):
        fl = os.listdir(".")

        # "stack_w1_" + str(self.runns) + "_32768.config"
        f = [i for i in fl if i.startswith("stack_w1_" + str(self.runns)) and i.endswith(".config")][0]
        print(f)
        sp = Popen(["time", "daredevil", "-c", f], stdout=PIPE, stderr=PIPE,)
        out, err = sp.communicate()
        return out, err


class TraceKryptologik:
    def __init__(self, b, r):
        self.BinaryPath = b
        self.runns = r  # Original value = 2000
        self.corectKey = "0D9BE960C438FF85F656BD48B78A0EE2"

    def processinput(self, iblock, blocksize):
        p = "%0*x" % (2 * blocksize, iblock)
        return None, [p[j * 2 : (j + 1) * 2] for j in range(len(p) // 2)]

    def processoutput(self, output, blocksize):
        return int(output, 16)

    def accuireTrace(self, ppr=None, ppo=None):
        if ppr and ppo:
            self.accuireTransformedTrace(ppr, ppo)
        else:
            self.accuireReferenceTrace()

    def accuireReferenceTrace(self):
        # To run kryptologik it neds the file DemoKey_table.bin in its working directory. Add this file and rerun
        # print(self.BinaryPath + 'src/DemoKey_table_encrypt')
        T = TracerGrind(self.BinaryPath + "src/wb_reference", self.processinput, self.processoutput, ARCH.amd64, 16,)
        T.run(self.runns)
        bin2daredevil(config={"algorithm": "AES", "position": "LUT/AES_AFTER_SBOX"})  # keywords=filters,

    def accuireTransformedTrace(self, prob, point):
        # print(self.BinaryPath + 'perturbations/wb_p_' + str(prob) + '_' + str(point))
        T = TracerGrind(
            self.BinaryPath + "perturbations/wb_p_" + str(prob) + "_" + str(point),
            self.processinput,
            self.processoutput,
            ARCH.amd64,
            16,
        )
        T.run(self.runns)
        bin2daredevil(config={"algorithm": "AES", "position": "LUT/AES_AFTER_SBOX"})  # keywords=filters,

    def performDaredevilAttack(self):
        # mem_stack_w1_200_33544
        fl = os.listdir(".")
        # "stack_w1_" + str(self.runns) + "_32768.config"
        f = [i for i in fl if i.startswith("stack_w1_" + str(self.runns)) and i.endswith(".config")][0]
        print(f)
        sp = Popen(["time", "daredevil", "-c", f], stdout=PIPE, stderr=PIPE,)
        out, err = sp.communicate()
        return out, err
