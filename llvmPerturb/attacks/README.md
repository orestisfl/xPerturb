# Pipeline

## 1.Run `python attack.py`

Carries out a complete attack with both deadpool tracer and daredevil. Parsed
logs are saved into logs and raw daredevil logs are saved into daredevilLogs.
Running the kryptologik attack needs to have access to DemoKey_table.bin in the current working directory. Add this file to fit your setup.

Uses the module `targets.py`

## 2. Run `python score.py`

In order to determine the success of an attack, a score is calculated. score.py
takes the parsed daredevil loggs and prints the score of each attack to std-out.
These scores are manually moved to for example the experiment_results/attack/WHITEBOX_attack_score_50 or similar.

Uses the module `attacks_statistics.py`

## 3. Run `python atk_score_plt.py`

Plots the scores available in example the experiment_results/attack/ folder

Uses the module `populate.py`
