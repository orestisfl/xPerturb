#include <stdlib.h>
// #include <time.h>
#include <sys/time.h>
#include <stdio.h>

#include <stdio.h>
#include "pone_10.h"

void init_pone(){
  struct timeval time;
  gettimeofday(&time, NULL);
  srandom((time.tv_sec * 1000) + (time.tv_usec / 1000));
}

long long pone(){ // Perturbe namechange
  int prob = 10;
  int r = random()% 100;
  if (r < prob){
    fprintf(stderr, "PP: %d\n", 1);
    return 1;
  }else{
    fprintf(stderr, "PP: %d\n", 0);
    return 0;
  }
}
