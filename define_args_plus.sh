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
OUT_SQL=sql/data

declare -A BY_KEY_PLUS
export BY_KEY_PLUS=(
#                  FLAGS           FROM     TO        INPUT      INPUT2           OUTPUT                                   OUTPUT2
#_____________________________________________________________________________________________________________________________________________________________________
[PLUS_YAM_SER]="   -p              -if yaml -of ser   ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_SER}                        -o2 ${OUT}/${OUT_SER2}                    "
[PLUS_YAM_YAM]="   -p              -if yaml -of yaml  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_YAML}                       -o2 ${OUT}/${OUT_YAML}/${OUT_INFO}        "
[PLUS_YAM_SQL]="   -p              -if yaml -of sql   ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_SQL}                        -o2 ${OUT}/${OUT_SQL}/${OUT_INFO}         "
[PLUS_YAM_WNB]="   -p              -if yaml -of wndb  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_WNDB}                       -o2 ${OUT}/${OUT_WNDB}/${OUT_INFO}        "
[PLUS_YAM_JSNO]="  -p -os o        -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}                       -o2 ${OUT}/${OUT_JSON}/${OUT_INFO}        "
[PLUS_YAM_JSNO1]=" -p -os o -o1    -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}1                      -o2 ${OUT}/${OUT_JSON}1/${OUT_INFO}       "
[PLUS_YAM_JSND]="  -p -os d -oj a  -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}data                   -o2 ${OUT}/${OUT_JSON}data/${OUT_INFO}    "
[PLUS_YAM_JSNDV]=" -p -os d -oj v  -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}data_vw                -o2 ${OUT}/${OUT_JSON}data_vw/${OUT_INFO} "
[PLUS_YAM_JSNDJ]=" -p -os d -oj j  -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}data_je                -o2 ${OUT}/${OUT_JSON}data_je/${OUT_INFO} "
[PLUS_YAM_JSND1]=" -p -os d -o1    -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}data1                  -o2 ${OUT}/${OUT_JSON}data1/${OUT_INFO}   "
[PLUS_YAM_JSNM]="  -p -os m        -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}model/${OUT_JSON_FILE} -o2 ${OUT}/${OUT_JSON}model/${OUT_INFO}   "
)
export KEYS_PLUS="${!BY_KEY_PLUS[@]}"
export KEYS_PLUS="PLUS_YAM_SER PLUS_YAM_YAM PLUS_YAM_SQL PLUS_YAM_WNB PLUS_YAM_JSNO PLUS_YAM_JSNO1 PLUS_YAM_JSNDA PLUS_YAM_JSNV PLUS_YAM_JSNDJ PLUS_YAM_JSND1 PLUS_YAM_JSNM"
