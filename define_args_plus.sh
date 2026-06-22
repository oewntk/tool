#!/bin/bash

# Copyright (c) 2021-2026. Bernard Bou.

IN_YAML=yamlplus
IN_YAML2=yaml2
OUT=out-plus

OUT_INFO=oewn.info
OUT_SER=ser/oewn.ser
OUT_SER2=ser/oewn.ser.info
OUT_YAML=yaml
OUT_WNDB=wndb
OUT_JSON=json
OUT_JSON_FILE=oewn-model.json
OUT_SQL=sql

declare -A BY_KEY_PLUS
export BY_KEY_PLUS=(
#                   FLAGS           FROM     TO        INPUT      INPUT2           OUTPUT                                    OUTPUT2
#________________________________________________________________________________________________________________________________________________________________________
[PLUS_YAM_SER]="    -p              -if yaml -of ser   ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_SER}                         -oi ${OUT}/${OUT_SER2}                     "
[PLUS_YAM_YAM]="    -p -om          -if yaml -of yaml  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_YAML}                        -oi ${OUT}/${OUT_YAML}/${OUT_INFO}         "
[PLUS_YAM_SQL]="    -p              -if yaml -of sql   ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_SQL}                         -oi ${OUT}/${OUT_SQL}/${OUT_INFO}          "
[PLUS_YAM_WNB]="    -p              -if yaml -of wndb  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_WNDB}                        -oi ${OUT}/${OUT_WNDB}/${OUT_INFO}         "
[PLUS_YAM_WNB_C]="  -p -wp -wl -wv  -if yaml -of wndb  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_WNDB}_compat                 -oi ${OUT}/${OUT_WNDB}_compat/${OUT_INFO}  "
[PLUS_YAM_JSN_O]="  -p -os o        -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}                        -oi ${OUT}/${OUT_JSON}/${OUT_INFO}         "
[PLUS_YAM_JSN_O1]=" -p -os o -o1    -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}1                       -oi ${OUT}/${OUT_JSON}1/${OUT_INFO}        "
[PLUS_YAM_JSN_DA]=" -p -os d -oj a  -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}_data                   -oi ${OUT}/${OUT_JSON}_data/${OUT_INFO}    "
[PLUS_YAM_JSN_DV]=" -p -os d -oj v  -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}_data_vw                -oi ${OUT}/${OUT_JSON}_data_vw/${OUT_INFO} "
[PLUS_YAM_JSN_DJ]=" -p -os d -oj j  -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}_data_je                -oi ${OUT}/${OUT_JSON}_data_je/${OUT_INFO} "
[PLUS_YAM_JSN_D1]=" -p -os d -o1    -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}_data1                  -oi ${OUT}/${OUT_JSON}_data1/${OUT_INFO}   "
[PLUS_YAM_JSN_M]="  -p -os m        -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}_model/${OUT_JSON_FILE} -oi ${OUT}/${OUT_JSON}_model/${OUT_INFO}   "
)
export KEYS_PLUS="${!BY_KEY_PLUS[@]}"
export KEYS_PLUS="PLUS_YAM_SER PLUS_YAM_YAM PLUS_YAM_SQL PLUS_YAM_WNB PLUS_YAM_WNB_C PLUS_YAM_JSN_O PLUS_YAM_JSN_O1 PLUS_YAM_JSN_DA PLUS_YAM_JSN_DV PLUS_YAM_JSN_DJ PLUS_YAM_JSN_D1 PLUS_YAM_JSN_M"
