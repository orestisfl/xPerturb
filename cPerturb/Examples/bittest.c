#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>

int main(){
    int acc = 0;
    int bound = 8;
    int mask = 0x02;
    for ( int i = bound ; i > 0 ; i-- ){
        if(i == 7){
          acc |= i+1 >> mask;
        } else acc |= i >> mask;
        printf("%d\n", acc);
    }
    printf("\n%d", acc);
    return 0;
}
