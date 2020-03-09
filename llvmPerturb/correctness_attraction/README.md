## Prerequisites

Pip install tqdm

## Run `python experiment.py`
Runns the complete correctness attraciton experiment. 

- Compiles the whitebox to LLVM IR
- Generates (in C) and compiles a perturbation function (PONE) with a probability defined in the code  
- Inserts a call to the perturbation function into a copy of the whitebox LLVM IR
- Compiles a the whitebox to assembly
- Compile the assembly to an executable saved in `perturbations/` as a subfolder under each whitebox types folder in the `example_program/` catalouge.
- Execute the binary using random input taylored to fit the input format the whitebox takes
- Take note of the statistics on succesfull runs and unsiccessfull runs

## Results
The results from the experiment is saved in `experiment_results/correctness/` and further in each corresponding whitebox subfolder from there on.
The result consists of a file with 3 columns: First column represents the perturbation probability, second the correctness atraction, third wich perturbation point that were tested and last on average how many tiimes the perturbation pont was hit during the execution. The average is calulated over all the runs made to derive the data on the whitebox

```
...
5, 0.963, 4300, 1.0
0, 1.0, 12900, 1.0
5, 0.962, 3900, 1.0
10, 0.908, 42000, 1.0
...
```

The file were all points are stored are named after the convention `[WHITEBOX-NAME]_points_db.cvc` in each whitebox types subfolder
From this it is possible to group the results by perturbation probability and look at the points scoring the 
highest correctness atraction for each tested probability. Theese are displayed in the files containing the 
phrase `ches2016_top10_p10.pts`

## Lessons learned
The process is designed for the ches2016 whitebox. 
The other two whiteboxes, Kryptologik and nsc_2013 might need somemanual adjustments in the code in order to have the pipeline above described execute properly.
