#!/usr/bin/env python
import os
from errno import EEXIST

from deadpool_dca import bin2daredevil
from attack_statistics import AttackStatitic
from targets import TracerWASM, TraceChess2016


def makedirs(path):
    try:
        os.makedirs(path)
    except OSError as e:
        if e.errno != EEXIST:
            raise


RUNS = 200

t = TracerWASM("../example_programs/wbs_aes_ches2016/src/a.wasm")
args, outs = t.run(RUNS)
for i in range(RUNS):
    t.get_trace(i, args[i][1], outs[i])
bin2daredevil(
    configs={
        "attack_sbox": {"algorithm": "AES", "position": "LUT/AES_AFTER_SBOX"},
        "attack_multinv": {"algorithm": "AES", "position": "LUT/AES_AFTER_MULTINV",},
    },
    keywords=t.filters,
)

# for f in os.listdir("."):
#     if f.startswith("mem_addr1_rw1_" + str(RUNS)) and f.endswith(".attack_sbox.config"):
#         os.system("daredevil -c %s" % f)
#         break

t = TraceChess2016("", RUNS)
out, err = t.performDaredevilAttack()
atts = AttackStatitic("chess2016")
atts.parseDaredevilData(out, err)
makedirs("./logs")
atts.saveToFile("./logs/" + "wasm-attack")
