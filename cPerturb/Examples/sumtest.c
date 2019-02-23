#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>

int main(){
    int acc = 0;
    int bound = 8;
    for ( int i = bound ; i > 0 ; i--){
        acc += i;
    }
    printf("\n%d", acc);
    return 0;
}
