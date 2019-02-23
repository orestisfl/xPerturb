#!/bin/sh
###CONFIG VARIABLES
FILE_NAME="./mergesort.c"
PMODE=PONE
LOOP_NO=30
MIN_LOOP=0
SUPPRESS=0
INPUTDATA=3
###
BOLD=`tput bold`
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0`
clear
echo "Advanced test starting."
echo ""
echo "cPerturb about to run."
echo ""
echo "1 = incorrect, 0 = correct" > testsh.txt
`gcc "${FILE_NAME}" -o oracle`
`chmod a+rx oracle`
ORACLE="`./oracle "${INPUTDATA}"`"
`srcml "${FILE_NAME}" -o code.xml`
echo "Starting test now..."
while [ "${LOOP_NO}" -ge "${MIN_LOOP}" ]
do
    echo "`./cPerturb ./code.xml "${PMODE}" -o pcode.xml -i $LOOP_NO -s`"
    `srcml ./pcode.xml -o pcode.c`
    `gcc ./pcode.c -o pProg`
    `chmod a+rx ./pProg`
    RESULT="`./pProg "${INPUTDATA}"`"
    if [ "$ORACLE" = "$RESULT" ]
    then
        CMP_RESULT=0
    else
        CMP_RESULT=1
    fi
    if [ "${SUPPRESS}" -ne "2" ] ; then
        echo
        echo "Testing node #$LOOP_NO"
        if [ "${SUPPRESS}" -ne "1" ]
        then
            echo "Perturbed result: "${RESULT}""
            echo "Expected  result: "${ORACLE}""
        fi
        if [ "${CMP_RESULT}" -eq "0" ]
        then
            echo "${BOLD}Test result: ${GREEN}PASS${NC}"
        else
            echo "${BOLD}Test result: ${RED}FAIL${NC}"
        fi
    fi
    echo Node "$LOOP_NO" , Result "$CMP_RESULT" >> testsh.txt
    LOOP_NO=`expr "$LOOP_NO" - 1`
done
rm oracle
rm pProg
rm code.xml
rm pcode.xml
rm pcode.c
echo
echo "Test completed"
echo
