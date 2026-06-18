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
export BY_KE_COMPARE=(
#            FLAGS        FROM      TO         INPUT      INPUT2            OUTPUT
#______________________________________________________________________________________________________________________
[YAM_SER]="               -Aif yaml -Bif ser   ${IN1_YAML} -Ai2 ${IN1_YAML2}  ${IN2}/${IN2_SER}                       "
[YAM_YAM]="               -Aif yaml -Bif yaml  ${IN1_YAML} -Ai2 ${IN1_YAML2}  ${IN2}/${IN2_YAML}                      "
[YAM_WNB]="               -Aif yaml -Bif wndb  ${IN1_YAML} -Ai2 ${IN1_YAML2}  ${IN2}/${IN2_WNDB}                      "
[YAM_JSNO]=" -Bis o       -Aif yaml -Bif json  ${IN1_YAML} -Ai2 ${IN1_YAML2}  ${IN2}/${IN2_JSON}                      "
[YAM_JSNO1]="-Bis o -Bi1  -Aif yaml -Bif json  ${IN1_YAML} -Ai2 ${IN1_YAML2}  ${IN2}/${IN2_JSON}1                     "
[YAM_JSND]=" -Bis d       -Aif yaml -Bif json  ${IN1_YAML} -Ai2 ${IN1_YAML2}  ${IN2}/${IN2_JSON}data                  "
[YAM_JSND1]="-Bis d -Bi1  -Aif yaml -Bif json  ${IN1_YAML} -Ai2 ${IN1_YAML2}  ${IN2}/${IN2_JSON}data1                 "
[YAM_JSNM]=" -Bis m       -Aif yaml -Bif json  ${IN1_YAML} -Ai2 ${IN1_YAML2}  ${IN2}/${IN2_JSON}model/${IN2_JSON_FILE}"
)
export KEYS_COMPARE="${!BY_KEY[@]}"
export KEYS_COMPARE="YAM_SER YAM_YAM YAM_WNB YAM_JSNO YAM_JSNO1 YAM_JSND YAM_JSND1 YAM_JSNM"
