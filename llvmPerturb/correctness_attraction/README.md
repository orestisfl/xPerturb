## Prerequisites

- Install the perturbation optimizer pass, LLVM add-in `lvm_src_folder/`
- `pip install tqdm`


## Run `python experiment.py`

Runs the complete correctness attraction experiment consisting of but not limited to:

- Compiles the whitebox to LLVM IR
- Generates (in C) and compiles a perturbation function (PONE) with a probability defined in the code  
- Inserts a call to the perturbation function into a copy of the whitebox LLVM IR
- Compiles a the whitebox to assembly
- Compile the assembly to an executable saved in `perturbations/` as a subfolder under each whitebox types folder in the `example_program/` catalouge.
- Execute the binary using random input tailored to fit the input format the whitebox takes
- Take note of the statistics on successful runs and unsuccessful runs

## Results
The results from the experiment is saved in `experiment_results/correctness/` and further in each corresponding whitebox subfolder from there on.
The result consists of a file with 3 columns: First column represents the perturbation probability, second the correctness attraction, third which perturbation point that were tested and last on average how many times the perturbation point was hit during the execution. The average is calculated over all the runs made to derive the data on the whitebox

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
highest correctness attraction for each tested probability. These are displayed in the files containing the
phrase `ches2016_top10_p10.pts`

## Make sense of the results
`parser.py` takes the correctness attraction results and produces the graphs showing the correctness for different perturbation probabilities at different perturbation points. The graphs produced were used in the results section and saved in the repo to the folder `experiment_results/correctness/graphs`

`profiles.py` shows a perturbation span for each of the tested whiteboxes. The graphs produced here are used in the discussion part of the report and saved in the repo to `experiment_results/discussion/graphs`

## Lessons learned
The process of compiling a lot of programs take an awful lot of time. To mitigate the long execution times the process were paralleled in 4 threads to utilize all cores in the CPU. This also introduced some problems in the beginning, so i learned that you should not run multi thread unless you really really have to. Parts of the NSC_2013 experiment can not be run i multi thread and that part of the experiment are therefore executed in one thread.

The process is designed for the ches2016 whitebox.
The other two whiteboxes, Kryptologik and nsc_2013 might need some manual adjustments in the code in order to have the pipeline above described execute properly.
