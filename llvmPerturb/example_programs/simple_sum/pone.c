#include <stdlib.h>
#include <time.h>
#include <stdio.h>

int pone(){
  int prob = 50;
  srandom(time(0));
  if (random()%100 < prob){
    return 1;
  }else{
    return 0;
  }
}
