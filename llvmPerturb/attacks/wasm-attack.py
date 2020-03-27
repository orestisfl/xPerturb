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


RUNS = 2000

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

tc = TraceChess2016("", RUNS)
makedirs("./logs")
for keyword in map(str, t.filters):
    out, err = tc.performDaredevilAttack(config_startswith=keyword + "_")
    atts = AttackStatitic("chess2016")
    atts.parseDaredevilData(out, err)
    atts.saveToFile("./logs/" + "wasm-attack-" + keyword)
    atts.setProgramScore("dec1a551f1eddec0de4b1dae5c0de511")
