#!/usr/bin/python
from __future__ import division # Float division



class RESULT:
    def __init__(self, pr, e, path, pe):
        self.Success = 0
        self.Fail = 0
        self.Error = 0
        self.Activations = 0
        self.Probability = pr
        self.Executable = e
        self.PerturbationPoint = pe
        self.Path = path

    def __str__(self):
        return"""Executable: %s
Correctness %1.3f
Success: %d
Fails: %d
Errors: %d
Index: %d
Percent: %d
Activations: %1.3f""" %(
        self.Executable,
        self.get_correctness(),
        self.Success,
        self.Fail,
        self.Error,
        self.PerturbationPoint,
        self.Probability,
        self.Activations
        )

    def get_correctness(self):
        try:
            return self.Success/(self.Fail + self.Error + self.Success)
        except ZeroDivisionError:
            return -1


    def print_plottable_data(self):
        return str(self.Probability) + ", " + str(self.get_correctness()) + ", " + str(self.PerturbationPoint) + ", " + str(self.Activations)
