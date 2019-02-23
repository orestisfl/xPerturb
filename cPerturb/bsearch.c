#include <stdio.h>
#include <stdlib.h>

int binsearch (int *a, int n, int x) {
    int i = 0, j = n - 1;
    while (i <= j) {
        int k = (i + j) / 2;
        if (a[k] == x) {
            return k;
        }
        else if (a[k] < x) {
            i = k + 1;
        }
        else {
            j = k - 1;
        }
    }
    return -1;
}

int binsearch_r (int *a, int x, int i, int j) {
    if (j < i) {
        return -1;
    }
    int k = (i + j) / 2;
    if (a[k] == x) {
        return k;
    }
    else if (a[k] < x) {
        return binsearch_r(a, x, k + 1, j);
    }
    else {
        return binsearch_r(a, x, i, k - 1);
    }
}

int main (int argc, char **argv)
{
    srand (atoi(argv[1]));
    int cPerturbVararLen = rand()%450+50;
    int a[cPerturbVararLen];
    int cPerturbVarj;
    int cPerturbNumber = 0;
    for (cPerturbVarj = 0; cPerturbVarj <= cPerturbVararLen; cPerturbVarj++)
    {
      cPerturbNumber = rand()%10 + cPerturbNumber;
      a[cPerturbVarj] = cPerturbNumber;
    }
    int m;
    for (m = 0; m < cPerturbVararLen; m++) {
      printf("%d ", a[m]);
    }

    //int a[] = {-31, 0, 1, 2, 2, 4, 65, 83, 99, 782};
    int n = sizeof a / sizeof a[0];
    printf("%d\n", n);
    int x = a[rand()%n];
    int i = binsearch(a, n, x);
    printf("\n%d is at index %d\n", x, i);
    x = a[rand()%n];
    i = binsearch_r(a, x, 0, n - 1);
    printf("%d is at index %d\n", x, i);
    return 0;
}
