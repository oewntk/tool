#!/bin/bash

# Copyright (c) 2021-2026. Bernard Bou.

IN=.
IN2=.
INB=out
INB_PLUS=out-plus

declare -A BY_KEY_COMPARE
export BY_KEY_COMPARE=(
#               FLAGS              A         B          A INPUT + INPUT2                        B INPUT
#__________________________________________________________________________________________________________________________________
# with yaml source
[YAM_SER]="                        -Aif yaml -Bif ser   ${IN}/yaml -Ai2 ${IN2}/yaml2            ${INB}/ser/oewn.ser               "
[YAM_YAM]="                        -Aif yaml -Bif yaml  ${IN}/yaml -Ai2 ${IN2}/yaml2            ${INB}/yaml                       "
[YAM_WNB]="                        -Aif yaml -Bif wndb  ${IN}/yaml -Ai2 ${IN2}/yaml2            ${INB}/wndb                       "
[YAM_WNB_C]="                      -Aif yaml -Bif wndb  ${IN}/yaml -Ai2 ${IN2}/yaml2            ${INB}/wndb                       "
[YAM_JSN_O]="   -Bis o             -Aif yaml -Bif json  ${IN}/yaml -Ai2 ${IN2}/yaml2            ${INB}/json                       "
[YAM_JSN_O1]="  -Bis o -Bi1        -Aif yaml -Bif json  ${IN}/yaml -Ai2 ${IN2}/yaml2            ${INB}/json1                      "
[YAM_JSN_D]="   -Bis d             -Aif yaml -Bif json  ${IN}/yaml -Ai2 ${IN2}/yaml2            ${INB}/json_data                  "
[YAM_JSN_D1]="  -Bis d -Bi1        -Aif yaml -Bif json  ${IN}/yaml -Ai2 ${IN2}/yaml2            ${INB}/json_data1                 "
[YAM_JSN_M]="   -Bis m             -Aif yaml -Bif json  ${IN}/yaml -Ai2 ${IN2}/yaml2            ${INB}/json_model/oewn-model.json "

# with model
[JSN_M_JSN_O]=" -Ais m -Bis o      -Aif json -Bif json  ${INB}/json_model/oewn-model            ${INB}/json                       "
[JSN_M_JSN_O1]="-Ais m -Bis o -Bi1 -Aif json -Bif json  ${INB}/json_model/oewn-model            ${INB}/json1                      "
[JSN_M_JSN_D]=" -Ais m -Bis d      -Aif json -Bif json  ${INB}/json_model/oewn-model            ${INB}/json_data                  "
[JSN_M_JSN_D1]="-Ais m -Bis d -Bi1 -Aif json -Bif json  ${INB}/json_model/oewn-model            ${INB}/json_data1                 "

# generated plus with saved plus
[YAM_PLUS_PLUSYAM]=" -Ap -Av -Bv   -Aif yaml -Bif yaml  ${IN}/yamlplus -Ai2 ${IN2}/yaml2        ${INB_PLUS}/yaml                  "
)
export KEYS_COMPARE="${!BY_KEY_COMPARE[@]}"
export KEYS_COMPARE="YAM_JSN_M YAM_SER YAM_YAM YAM_WNB YAM_WND_C YAM_JSN_O YAM_JSN_O1 YAM_JSN_D YAM_JSN_D1 JSN_M_JSN_O JSN_M_JSN_O1 JSN_M_JSN_D JSN_M_JSN_D1 YAM_PLUS_PLUSYAM"
