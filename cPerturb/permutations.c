#include <stdio.h>
#include <stdlib.h>

int main (int argc, char *argv[]) {
  srand (atoi(argv[1]));
  int cPerturbVararLen = rand()%2+3;
  char list[cPerturbVararLen];
  int cPerturbVarlooper;
  for (cPerturbVarlooper = 0; cPerturbVarlooper <= cPerturbVararLen; cPerturbVarlooper++)
  {
    list[cPerturbVarlooper] = rand()%25+97 ;
  }



//here we check arguments
	if (argc < 2) {
        printf("Enter an argument. Example 1234 or dcba:\n");
        return 0;
	}
//it calculates an array's length
        int x;
        for (x = 0; list[x] != '\0'; x++);
//buble sort the array
	int f, v, m;
	 for(f=0; f < x; f++) {
    	 for(v = x-1; v > f; v-- ) {
     	 if (list[v-1] > list[v]) {
	m=list[v-1];
	list[v-1]=list[v];
	list[v]=m;
    }
  }
}

//it calculates a factorial to stop the algorithm
    char a[x];
	int k=0;
	int fact=k+1;
             while (k!=x) {
                   a[k]=list[k];
               	   k++;
		  fact = k*fact;
                   }
                   a[k]='\0';
//Main part: here we permutate
           int i, j;
           int y=0;
           char c;
          while (y != fact) {
          printf("%s\n", a);
          i=x-2;
          while(a[i] > a[i+1] ) i--;
          j=x-1;
          while(a[j] < a[i] ) j--;
      c=a[j];
      a[j]=a[i];
      a[i]=c;
i++;
for (j = x-1; j > i; i++, j--) {
  c = a[i];
  a[i] = a[j];
  a[j] = c;
      }
y++;
   }
}
