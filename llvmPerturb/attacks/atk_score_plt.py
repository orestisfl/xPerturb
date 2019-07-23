import matplotlib.pyplot as plt
import numpy as np

dataset = [0.8125, 0.75, 0.75, 0.9375, 0.8125, 0.75, 0.8125, 0.8125, 0.875, 0.9375]
q1, q2, q3 = np.percentile(dataset,[25, 50, 75])
upper = q3 + (q3-q1)*1.5
lower = q1 - (q3-q1)*1.5
x = range(0, 11)
y1 = [q1] * len(x)
y2 = [q2] * len(x)
y3 = [q3] * len(x)

fd = open("/home/koski/xPerturb/llvmPerturb/experiment_results/nsc_attackpoints", "r")
fl = fd.readlines()
fd.close()

ppts = [i.strip().split()[2] for i in fl]
ppts = list(dict.fromkeys(ppts)) #Remove duplicates

fl = [(float(i.strip().split()[0]), i.strip().split()[2]) for i in fl]

fig, ax1 = plt.subplots()

for i in range(10):
    y = [j[0] for j in fl if j[1] == ppts[i]]
    myX = [i] * len(y)
    print(len(myX), len(y))
    print(myX)
    print(y)
    ax1.scatter(myX, y)


plt.ylim(0,1)
plt.xlim(0,10)
plt.title("NSC Attacks")
plt.xlabel("Perturbation point")
plt.ylabel("Success-score")

ax1.fill_between(x, y1, y3, alpha=0.1)
ax1.plot(x, y1, color="blue", alpha=0.1)
ax1.plot(x, y2, color="blue", alpha=0.1)
ax1.plot(x, y3, color="blue", alpha=0.1)


print(q1, q2, q3)
plt.show()
