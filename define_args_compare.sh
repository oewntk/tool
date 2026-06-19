#!/bin/bash

# Copyright (c) 2021-2026. Bernard Bou.

IN1_YAML=yaml
IN1_YAML2=yaml2

IN2=out
IN2_INFO=oewn.info
IN2_SER=ser/oewn.ser
IN2_SER2=ser/oewn.ser.info
IN2_YAML=yaml
IN2_WNDB=wndb
IN2_JSON=json
IN2_JSON_FILE=oewn-model.json
IN2_SQL=sql/data

declare -A BY_KEY_COMPARE
export BY_KEY_COMPARE=(
#               FLAGS              A         B          A INPUT      A INPUT2                     B INPUT
#______________________________________________________________________________________           ________________________________________
[YAM_SER]="                        -Aif yaml -Bif ser   ${IN1_YAML} -Ai2 ${IN1_YAML2}             ${IN2}/${IN2_SER}                        "
[YAM_YAM]="                        -Aif yaml -Bif yaml  ${IN1_YAML} -Ai2 ${IN1_YAML2}             ${IN2}/${IN2_YAML}                       "
[YAM_WNB]="                        -Aif yaml -Bif wndb  ${IN1_YAML} -Ai2 ${IN1_YAML2}             ${IN2}/${IN2_WNDB}                       "
[YAM_WNB_C]="                      -Aif yaml -Bif wndb  ${IN1_YAML} -Ai2 ${IN1_YAML2}             ${IN2}/${IN2_WNDB}                       "
[YAM_JSN_O]="   -Bis o             -Aif yaml -Bif json  ${IN1_YAML} -Ai2 ${IN1_YAML2}             ${IN2}/${IN2_JSON}                       "
[YAM_JSN_O1]="  -Bis o -Bi1        -Aif yaml -Bif json  ${IN1_YAML} -Ai2 ${IN1_YAML2}             ${IN2}/${IN2_JSON}1                      "
[YAM_JSN_D]="   -Bis d             -Aif yaml -Bif json  ${IN1_YAML} -Ai2 ${IN1_YAML2}             ${IN2}/${IN2_JSON}_data                  "
[YAM_JSN_D1]="  -Bis d -Bi1        -Aif yaml -Bif json  ${IN1_YAML} -Ai2 ${IN1_YAML2}             ${IN2}/${IN2_JSON}_data1                 "
[YAM_JSN_M]="   -Bis m             -Aif yaml -Bif json  ${IN1_YAML} -Ai2 ${IN1_YAML2}             ${IN2}/${IN2_JSON}_model/${IN2_JSON_FILE}"

[JSN_M_JSN_O]=" -Ais m -Bis o      -Aif json -Bif json  ${IN2}/${IN2_JSON}_model/${IN2_JSON_FILE} ${IN2}/${IN2_JSON}                       "
[JSN_M_JSN_O1]="-Ais m -Bis o -Bi1 -Aif json -Bif json  ${IN2}/${IN2_JSON}_model/${IN2_JSON_FILE} ${IN2}/${IN2_JSON}1                      "
[JSN_M_JSN_D]=" -Ais m -Bis d      -Aif json -Bif json  ${IN2}/${IN2_JSON}_model/${IN2_JSON_FILE} ${IN2}/${IN2_JSON}_data                  "
[JSN_M_JSN_D1]="-Ais m -Bis d -Bi1 -Aif json -Bif json  ${IN2}/${IN2_JSON}_model/${IN2_JSON_FILE} ${IN2}/${IN2_JSON}_data1                 "
)
export KEYS_COMPARE="${!BY_KEY[@]}"
export KEYS_COMPARE="YAM_JSN_M YAM_SER YAM_YAM YAM_WNB YAM_WND_C YAM_JSN_O YAM_JSN_O1 YAM_JSN_D YAM_JSN_D1 JSN_M_JSN_O JSN_M_JSN_O1 JSN_M_JSN_D JSN_M_JSN_D1"
export KEYS_COMPARE="YAM_JSN_M YAM_SER YAM_YAM"
export KEYS_COMPARE="JSN_M_JSN_O JSN_M_JSN_O1 JSN_M_JSN_D JSN_M_JSN_D1"