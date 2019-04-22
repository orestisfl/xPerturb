#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int main()
{
     int var1;
     srandom(time(0));
     var1 = random();
     printf ("%d\n", var1);

     return 0;
}
