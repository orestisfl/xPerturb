#!/bin/bash
####### PPOINT MAX_########
# 1 Permutations : 36
# 2 Binsearch    : 38
# 3 Knapsack     : 51
# 4 Quicksort    : 22
# 5 MergeSort    : 33
###########################
#####CONFIG VARIABLES######
ALGO=3
PMODE=PONE
MAX_LOOP=2
PPOINT=0
FORCE_MAX=0
SUPPRESS=0
##########################
BOLD=`tput bold`
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0`
SUCCESSES=0
FAILURES=0
ATTEMPTS=0
RATIO=0
SEED=0
PERM="permutations.c"
PPERM=36
BIN="bsearch.c"
PBIN=38
KNAP="knapsack.c"
PKNAP=51
QUICK="quicksort.c"
PQUICK=22
MERGE="mergesort.c"
PMERGE=33
##########################
clear
case "${ALGO}" in
  1) FILE_NAME="${PERM}"
     PPOINT_MAX=${PPERM}
     ;;
  2) FILE_NAME="${BIN}"
     PPOINT_MAX=${PBIN}
     ;;
  3) FILE_NAME="${KNAP}"
     PPOINT_MAX=${PKNAP}
     ;;
  4) FILE_NAME="${QUICK}"
     PPOINT_MAX=${PQUICK}
     ;;
  5) FILE_NAME="${MERGE}"
     PPOINT_MAX=${PMERGE}
     ;;
  *) echo "Invalid file."
     exit
     ;;
esac
if [ ${FORCE_MAX} -ne 0 ]
then
    PPOINT_MAX=${FORCE_MAX}
fi
echo
echo "Perturbing "${FILE_NAME}" on `expr "${PPOINT_MAX}" + 1` perturbation points with `expr "${MAX_LOOP}" + 1` inputs each."
echo
###########################
bar="======================================"
barlength=${#bar}
echo "Perturbation point / Attempts | Successes / Failures" > testsh.txt
`gcc "./${FILE_NAME}" -o oracle`
`chmod a+rx oracle`
`srcml "./${FILE_NAME}" -o code.xml`
echo "Starting test now..."
echo
# looping over all points
while [ "${PPOINT}" -le "${PPOINT_MAX}" ]
do
    if [ "${SUPPRESS}" -le 1 ]
    then
        echo
        echo "Testing perturbation point #"${PPOINT}" out of "${PPOINT_MAX}""
        echo "Number of tests: `expr "${MAX_LOOP}" + 1`"
    fi
    SUCCESSES=0
    FAILURES=0
    ATTEMPTS=0
    LOOP_NO=0
    if [ "${SUPPRESS}" -eq 0 ]
    then
        n=$((ATTEMPTS*barlength / `expr ${MAX_LOOP} + 1`))
        printf "\r[%-${barlength}s] ${BOLD}(`expr ${LOOP_NO} + 1` / `expr "$MAX_LOOP" + 1`)${NC}   ${BOLD}(S: ${GREEN}${SUCCESSES} ${NC}${BOLD}F: ${RED}${FAILURES}${NC}${BOLD})${NC}" "${bar:0:n}"
    fi
    # looping over inputs
    while [ "${LOOP_NO}" -le "${MAX_LOOP}" ]
    do
        RESULT="1"
        ORACLE="0"
        SEED=$((10*${LOOP_NO}+5))
        ATTEMPTS=`expr "$ATTEMPTS" + 1`
        ORACLE="`./oracle "${SEED}"`"
        OUTPUT="pcode-${PMODE}-${PPOINT}.xml"
        `./cPerturb ./code.xml "${PMODE}" -o ${OUTPUT} -i "${PPOINT}" -s`
        `srcml ${OUTPUT} -o pcode.c`
        `gcc ./pcode.c -o pProg`
        `chmod a+rx ./pProg`
        RESULT="`./pProg "${SEED}" 2>/dev/null`"
        if [ "${ORACLE}" = "${RESULT}" ]
        then
            SUCCESSES=`expr "$SUCCESSES" + 1`
        else
            FAILURES=`expr "$FAILURES" + 1`
        fi
        if [ "${SUPPRESS}" -eq 0 ]
        then
            n=$((ATTEMPTS*barlength / `expr ${MAX_LOOP} + 1`))
            printf "\r[%-${barlength}s] ${BOLD}(`expr ${LOOP_NO} + 1` / `expr "$MAX_LOOP" + 1`)${NC}   ${BOLD}(S: ${GREEN}${SUCCESSES} ${NC}${BOLD}F: ${RED}${FAILURES}${NC}${BOLD})${NC}" "${bar:0:n}"
        fi
        LOOP_NO=`expr "$LOOP_NO" + 1`
    done
    if [ "${SUPPRESS}" -le 1 ]
    then
        echo
        echo
        echo "${BOLD}${GREEN}Successes: ${NC}${BOLD}"${SUCCESSES}"${NC}"
        echo "${BOLD}${RED}Failures:  ${NC}${BOLD}"${FAILURES}"${NC}"
    fi
    if [ "${SUPPRESS}" -le 2 ]
    then
        if [ "${FAILURES}" = 0 ]
        then
            echo "Node #${PPOINT} is ${BOLD}ANTI-FRAGILE${NC}. (${BOLD}${GREEN}${SUCCESSES}${NC}${BOLD} / ${ATTEMPTS}${NC})"
        else
            RATIO=$((100*SUCCESSES / ATTEMPTS))
            if [ ${RATIO} -ge 75 ]
            then
                echo "Node #${PPOINT} is ${BOLD}ROBUST${NC}. (${BOLD}${GREEN}${SUCCESSES}${NC}${BOLD} / ${ATTEMPTS}${NC})"
            else
                echo "Node #${PPOINT} is ${BOLD}FRAGILE${NC}. (${BOLD}${GREEN}${SUCCESSES}${NC}${BOLD} / ${ATTEMPTS}${NC})"
            fi
        fi
    fi
    if [ "${SUPPRESS}" -eq 0 ]
    then
        echo
        echo "----------------------------------------------------------------------------"
    fi
    echo "#${PPOINT} , ${ATTEMPTS}  ,  ${SUCCESSES} , ${FAILURES}" >> testsh.txt

    PPOINT=`expr "$PPOINT" + 1`
done
rm oracle
rm pProg
rm code.xml
rm pcode.xml
rm pcode.c
echo
echo "Test completed"
echo
