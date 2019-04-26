#include <stdlib.h>
#include <time.h>
#include <stdio.h>

int pone(){
  int prob = 101; // 101 = allways perturbe for testing-purposes
  srandom(time(0));
  if (random()%100 < prob){
    return 1;
  }else{
    return 0;
  }
}
