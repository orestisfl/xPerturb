#include <stdio.h>
int add(int num1, int num2)
{
     int sum;
     sum = num1+num2;
     return sum;
}

int main()
{
     int var1, var2;
     var1 = 2;
     var2 = 3;

     int res = add(var1, var2);
     printf ("%d\n", res);

     return 0;
}
