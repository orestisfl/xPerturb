#include <stdio.h>

#include <stdlib.h>
#include <stdint.h>
// int add(int num1, int num2)
// {
//      int sum;
//      sum = num1+num2;
//      return sum;
// }


int intis(int num1, int num2)
{
     int sum;
     sum = num1+num2;
     return sum;
}

char mul(char num1, char num2)
{
     char sum;
     sum = num1+num2;
     return sum;
}
// int sub(int num1, int num2)
// {
//      int sum;
//      sum = num1-num2;
//      return sum;
// }
// int div(int num1, int num2)
// {
//      int sum;
//      sum = num1/num2;
//      return sum;
// }
int main()
{
     char var1, var2;
     var1 = 2;
     var2 = 3;

     char res = mul(var1, var2);
     printf ("%d\n", res);

     return 0;
}
