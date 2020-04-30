import multiprocessing
import os
import random
import re
import tempfile
from subprocess import Popen, PIPE

from deadpool_dca import ARCH, Tracer, TracerGrind, bin2daredevil, DefaultFilters


class TraceChess2016:
    def __init__(self, b, r):
        self.BinaryPath = b
        self.runns = r  # Original value = 2000
        self.corectKey = "dec1a551f1eddec0de4b1dae5c0de511"

    @staticmethod
    def processinput(iblock, blocksize):
        p = "%0*x" % (2 * blocksize, iblock)
        return None, [p[j * 2: (j + 1) * 2] for j in range(len(p) / 2)]

    @staticmethod
    def processoutput(output, blocksize):
        return int("".join([x for x in output.split("\n") if x.find("OUTPUT") == 0][0][10:].split(" ")), 16, )

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
        )
        # Tracing only the first round:
        # T=TracerGrind('../target/wb_challenge', processinput, processoutput, ARCH.amd64, 16,  addr_range='0x108000-0x10c000')
        T.run(self.runns)  # Original number 2000
        bin2daredevil(
            configs={
                "attack_sbox": {"algorithm": "AES", "position": "LUT/AES_AFTER_SBOX"},
                "attack_multinv": {"algorithm": "AES", "position": "LUT/AES_AFTER_MULTINV", },
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

    def performDaredevilAttack(self, config_startswith="mem_addr1_rw1_"):
        f = [
            i
            for i in os.listdir(".")
            if i.startswith(config_startswith + str(self.runns)) and i.endswith(".attack_sbox.config")
        ][0]
        sp = Popen(["time", "daredevil", "-c", f], stdout=PIPE, stderr=PIPE,)
        out, err = sp.communicate()
        return out, err


class TraceNsc2013:
    def __init__(self, b, r):
        self.BinaryPath = b
        self.runns = r  # Original value = 2000
        self.corectKey = "4b45595f4b45595f4b45595f4b45595f"

    @staticmethod
    def processinput(iblock, blocksize):
        return None, ["%0*x" % (2 * blocksize, iblock)]

    @staticmethod
    def processoutput(output, blocksize):
        # print(''.join([x for x in output.split('\n') if x.find('Output')==0][0][10:57].split(' ')))
        return int("".join([x for x in output.split("\n") if x.find("Output") == 0][0][10:57].split(" ")), 16,)

    def accuireTrace(self, ppr=None, ppo=None):
        if ppr and ppo:
            self.accuireTransformedTrace(ppr, ppo)
        else:
            self.accuireReferenceTrace()

    def accuireReferenceTrace(self):
        Popen(
            [self.BinaryPath + "nosuchcon_2013_whitebox_noenc_generator"], shell=True,
        )
        T = TracerGrind(
            self.BinaryPath,
            self.processinput,
            self.processoutput,
            ARCH.amd64,
            16,
            addr_range="0x108000-0x3ffffff",
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
        f = [i for i in fl if i.startswith("mem_data_rw1_" + str(self.runns)) and i.endswith(".config")][0]
        print(f)
        sp = Popen(["time", "daredevil", "-c", f], stdout=PIPE, stderr=PIPE,)
        out, err = sp.communicate()
        return out, err


class TraceKryptologik:
    def __init__(self, b, r):
        self.BinaryPath = b
        self.runns = r  # Original value = 2000
        self.corectKey = "0D9BE960C438FF85F656BD48B78A0EE2"

    @staticmethod
    def processinput(iblock, blocksize):
        p = "%0*x" % (2 * blocksize, iblock)
        return None, [p[j * 2 : (j + 1) * 2] for j in range(len(p) // 2)]

    @staticmethod
    def processoutput(output, blocksize):
        return int(output, 16)

    def accuireTrace(self, ppr=None, ppo=None):
        if ppr and ppo:
            self.accuireTransformedTrace(ppr, ppo)
        else:
            self.accuireReferenceTrace()

    def accuireReferenceTrace(self):
        # To run kryptologik it neds the file DemoKey_table.bin in its working directory. Add this file and rerun
        # print(self.BinaryPath + 'src/DemoKey_table_encrypt')
        T = TracerGrind(self.BinaryPath + "src/wb_reference", self.processinput, self.processoutput, ARCH.amd64, 16,addr_range="0x108000-0x3ffffff",)
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
        f = [i for i in fl if i.startswith("mem_addr1_rw1_" + str(self.runns)) and i.endswith(".config")][0]
        print(f)
        sp = Popen(["time", "daredevil", "-c", f], stdout=PIPE, stderr=PIPE,)
        out, err = sp.communicate()
        return out, err


WASM_TRACE = ["node", "--experimental-wasm-bigint", "$(which wasm-trace)", "--optimize=0"]


class TracerWASM(Tracer):
    def __init__(
        self,
        target,
        processinput=TraceChess2016.processinput,
        processoutput=TraceChess2016.processoutput,
        arch=ARCH.amd64,
        blocksize=16,
        tmptracefile="default",
        addr_range="default",
        stack_range="default",
        filters=(DefaultFilters.mem_addr1_rw1,DefaultFilters.mem_data_rw1,DefaultFilters.mem_data_rw4),
        debug=True,
    ):
        super(TracerWASM, self).__init__(
            os.path.abspath(target),
            processinput,
            processoutput,
            arch,
            blocksize,
            tmptracefile,
            addr_range,
            stack_range,
            filters,
            tolerate_error=False,
            shell=True,
            debug=debug,
        )
        # TODO
        if addr_range == "default":
            self.addr_range = "0x400000-0x3ffffff"
        if stack_range == "default":
            if self.arch == ARCH.i386:
                self.stack_range = (0xF0000000, 0xFFFFFFFF)
            if self.arch == ARCH.amd64:
                self.stack_range = (0xFF0000000, 0xFFFFFFFFF)

        # Instrument target WebAssembly file.
        instrumented = self.target[0] + "-instrumented"
        if not os.path.exists(instrumented):
            # However, only do this if the instrumented target does not exist
            self._exec(WASM_TRACE + ["-M", "-o", "/dev/null", "--save-wasm", instrumented, self.target[0]], None)
        assert os.path.exists(instrumented), "Instrumented file successfully created"
        self.target[0] = instrumented

        self.verbose = False

    def get_trace(self, n, iblock, output):
        oblock = self.processoutput(output, self.blocksize)
        trace_log = "./trace%d-%d-converted.log" % (n, iblock)

        self._trace_init(n, iblock, oblock)
        with open(trace_log, "r") as trace:
            for line in iter(trace.readline, ""):
                mem_mode = line[line.index("MODE") + 6]
                mem_addr = int(line[line.index("START_ADDRESS") + 15 : line.index("START_ADDRESS") + 31], 16,)
                mem_size = int(line[line.index("LENGTH") + 7 : line.index("LENGTH") + 10])
                mem_data = int(line[line.index("DATA") + 6 :].replace(" ", ""), 16)
                for f in self.filters:
                    if mem_mode in f.modes and f.condition(self.stack_range, mem_addr, mem_size, mem_data):
                        self._trace_data[f.keyword].append(f.extract(mem_addr, mem_size, mem_data))
        self._trace_dump()
        return oblock

    def run(self, n, **_):
        pool = multiprocessing.Pool(multiprocessing.cpu_count())
        args = []
        for i in range(n):
            iblock = random.randint(0, (1 << (8 * self.blocksize)) - 1)
            input_stdin, input_args = self.processinput(iblock, self.blocksize)
            if input_stdin is None:
                input_stdin = b""
            if input_args is None:
                input_args = []
            args.append((i, iblock, self.target + input_args, input_stdin))
        # oblock=self.get_trace(i, iblock)
        # TODO: get_trace
        return args, pool.map(_exec_wasm_trace, args)


def _exec_wasm_trace(args):
    """Easily parallelizable function that executes wasm-trace"""
    i, iblock, cmd, input_stdin = args

    # wasm-trace uses file .wasm-trace.csv on the current work directory.
    # Because of this, we need to create a new empty directory to avoid each process conflicting with the others.
    # The trace.log output should always be in the original work directory.
    trace_log = os.path.abspath("./trace%d-%d.log" % (i, iblock))
    wd = tempfile.mkdtemp(suffix="-%d" % iblock)

    # When wasm-trace is called without optimization arguments it just runs the instrumented file,
    # providing the needed missing functions
    cmd = ("cd %s && " % wd) + " ".join(WASM_TRACE + ["-o", trace_log] + cmd) + (" && rm -rf %s" % wd)

    print(cmd, wd)
    p = Popen(cmd, stdin=PIPE, stdout=PIPE, stderr=PIPE, shell=True, executable="/bin/bash")
    out, err = p.communicate(input=input_stdin)
    if p.returncode != 0:
        raise RuntimeError("Cmd '%s' exited with code %d, message:\n%s" % (cmd, p.returncode, err))

    with open(trace_log) as f1:
        trace_log_converted = trace_log[:-4] + "-converted.log"
        with open(trace_log_converted, "w") as f2:
            f2.write("\n".join(wasm_trace2tracergrind(f1)))
    return out


def wasm_trace2tracergrind(f):
    def hexpad(d, length=8):
        fmt = "{:0>%d}" % (length * 2)
        formatted = fmt.format(hex(d)[2:])
        if formatted.endswith("L"):
            return formatted[:-1]
        return formatted

    pattern = re.compile(r"\s*(\d+) \| (i32|i64) \| (store|load) \s* (\d+)\+(\d+) (\d) (\-?\d+)")
    for line in f:
        line = line.strip()
        if not line:
            continue

        exec_id, _, mode, address, offset, length, value = pattern.search(line).groups()
        length = int(length)
        mode = "W" if mode == "store" else "R"
        # addresses should have 16 hex digits
        ins_address = hexpad(0)  # We don't care
        start_address = hexpad(int(address) + int(offset))
        value = int(value)
        if value < 0:
            value += 2 ** (length * 8)
        value = hexpad(value, length)
        yield "[M] EXEC_ID: %s INS_ADDRESS: %s START_ADDRESS: %s LENGTH: %d MODE: %s DATA: %s" % (
            exec_id,
            ins_address,
            start_address,
            length,
            mode,
            value,
        )
