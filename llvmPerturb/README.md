# A perturbation / correctness attraction / randomizaton framework for LLVM IR.

## Investigate correctness attraction
In order to investigate correctness attraction for a given white box implementation a perturbation tick is fastened to the programe at a set probability of activiating at a specific point inside the programe.

This tick is located inside the "example_programs/perturbation_types" folder and can only perturb the programe by adding one to integers repreesented in LLVM as 8 bit, 16, bit, 32, bit or 64 bit integers.

The tick itself is generated, from a python template found in "perturbation_templates", to a c program found in "example_programs/perturbation_types". The C program (the tick) is added to the program and using an LLVM opt pass the tick is linked to a specific perturbation point inside the program. For every whitebox implementation, perturbation point and activation probability there exists a separate compiled binary.

The pipeline for conducting the correctness attracion experiment are in the experiment.py file.

In here we compile a reference whitebox in the coresponding whitebox source folder.

All transfored whiteboxes will be compiled. One compilation is one called a "Job". There are 5 jobs executed in paralell with some delay added in between them. Kryptologik seems to have a probelm with multiple threads starting all at once. After all compilation jobs are done the correctness atraction testing will start. All ches2016 and kryptologik can be tested in a multi threaded setting meanwhile nsc generator needs to be tested in a single thread.

The whiteboxes correctness atraction are validated with with 1000 randomly generated inputs.

## Investigate attack resistance
