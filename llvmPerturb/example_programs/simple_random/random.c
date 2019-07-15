#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <sys/time.h>


int pone(time_t seed){
  srandom(seed);
  int prob = 50;
  if (random()% 100 < prob){
    return 1;
  }else{
    return 0;
  }
}

int main(){
  time_t seed =  time(0);
  int one = 0;
  int zero = 0;
   for(int i = 0; i < 1000000; i++){
     if (pone(seed+i)){
      one++;
    }else{
      zero++;
    }
   }
   printf("Ones: %d\n", one);
   printf("Zeros: %d\n", zero);


   return 0;
}
