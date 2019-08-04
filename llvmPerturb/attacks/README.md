## Pipeline

python attack.py

Carries out a complete attack with both deadpool tracer and daredevil. Parsed
logs are saved into logs and raw daredevil logs are saved into daredevilLogs.
Running the kryptologik attack needs to have acces to DemoKey_table.bin in the current working direcotry. Add this file to fit your setup.

python score.py

In order to determine the sucess of an attack a score is calculated. score.py
takes the parsed daredevil loggs and prints the score of each attack to std-out.
Theese scores are manually moved to the experiments results folder.

python atk_score_plt.py

Plots the scores
