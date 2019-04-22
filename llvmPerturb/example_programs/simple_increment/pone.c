#include <stdio.h>
int pone(int num)
{
     return num + 1;
}

int main()
{
     int var1;
     var1 = 3;
     int res = pone(var1);
     printf ("%d\n", res);

     return 0;
}
