import matplotlib.pyplot as plt
import numpy as np
from numpy import ones,vstack
from numpy.linalg import lstsq

# Create graphs visible in Discussion. Graphs show the span of expexted correctness attraction for the different whiteboxes for different perturbation probabilities

def kxm(points):
    x_coords, y_coords = zip(*points)
    A = vstack([x_coords,ones(len(x_coords))]).T
    m, c = lstsq(A, y_coords)[0]
    print("Line Solution is y = {m}x + {c}".format(m=m,c=c))

def profile_chess():
    plt.xlabel("Perturbation Probability")
    plt.ylabel("Correctness attraction")
    plt.ylim(0, 1)
    plt.xlim(0, 100.02)
    u = [1,0.48]
    m = [1, u[1]/2]
    l = [1, 0]

    print("Upper")
    kxm([(0,1),(100, u[1])])
    print("middle")
    kxm([(0,1),( 100, u[1]/2 )])
    print("lower")
    kxm([(0,1),( 100, 0)])

    upper = plt.plot([0,100],u, color='blue', alpha=0.3) # Upper
    middle = plt.plot([0,100],m) # Middle
    lower = plt.plot([0,100],l, color='blue', alpha=0.3) # Lower

    plt.fill_between([0,100], [1,0.48], [1,0], alpha=0.2)
    plt.title("Perturbation profile - Ches2016")
    plt.savefig("profile_ches2016")
    plt.clf()


def profile_kryptologik():
    plt.xlabel("Perturbation Probability")
    plt.ylabel("Correctness attraction")
    plt.ylim(0, 1)
    plt.xlim(0, 100.02)
    u = [1, 0.9]
    m = [1, u[1]/2]
    l = [1, 0]

    upper = plt.plot([0, 100],u, color='blue', alpha=0.3) # Upper
    middle = plt.plot([0, 100],m) # Middle
    lower = plt.plot([0, 100],l , color='blue', alpha=0.3) # Lower

    plt.fill_between([0,100], u, l, alpha=0.2)
    plt.title("Perturbation profile - Kryptologik")
    plt.savefig("profile_kryptologik")
    plt.clf()

def profile_nsc():
    plt.xlabel("Perturbation Probability")
    plt.ylabel("Correctness attraction")
    plt.ylim(0, 1)
    plt.xlim(0, 100.02)

    upper = plt.plot([0, 100],[1,0], color='blue', alpha=0.3) # Upper
    middle = plt.plot([0, 95/2],[1,0]) # Middle
    lower = plt.plot([0, 5],[1,0] , color='blue', alpha=0.3) # Lower

    plt.fill([0,5,100],[1,0,0], alpha=0.2)
    plt.title("Perturbation profile - nsc2013")
    plt.savefig("profile_nsc")
    plt.clf()

def main():
    profile_chess()
    profile_kryptologik()
    profile_nsc()

main()
