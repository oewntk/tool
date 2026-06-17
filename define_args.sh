#!/bin/bash

# Copyright (c) 2021-2026. Bernard Bou.

IN_YAML=yaml
IN_YAML2=yaml2
OUT=out

OUT_INFO=oewn.info
OUT_SER=ser/oewn.ser
OUT_SER2=ser/oewn.ser.info
OUT_YAML=yaml
OUT_WNDB=wndb
OUT_JSON=json
OUT_JSON_FILE=oewn-model.json
OUT_SQL=sql

declare -A BY_KEY
export BY_KEY=(
#            FLAGS       FROM     TO        INPUT      INPUT2           OUTPUT                                   OUTPUT2
#__________________________________________________________________________________________________________________________________________________________
[YAM_SER]="              -if yaml -of ser   ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_SER}                        -o2 ${OUT}/${OUT_SER2}                    "
[YAM_YAM]="              -if yaml -of yaml  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_YAML}                       -o2 ${OUT}/${OUT_YAML}/${OUT_INFO}        "
[YAM_SQL]="              -if yaml -of sql   ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_SQL}                        -o2 ${OUT}/${OUT_SQL}/${OUT_INFO}         "
[YAM_WNB]="              -if yaml -of wndb  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_WNDB}                       -o2 ${OUT}/${OUT_WNDB}/${OUT_INFO}        "
[YAM_JSNO]=" -os o       -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}                       -o2 ${OUT}/${OUT_JSON}/${OUT_INFO}        "
[YAM_JSNO1]="-os o -o1   -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}1                      -o2 ${OUT}/${OUT_JSON}1/${OUT_INFO}       "
[YAM_JSND]=" -os d       -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}data                   -o2 ${OUT}/${OUT_JSON}data/${OUT_INFO}    "
[YAM_JSND]=" -os d -oj v -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}data_vw                -o2 ${OUT}/${OUT_JSON}data_vw/${OUT_INFO} "
[YAM_JSND]=" -os d -oj j -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}data_je                -o2 ${OUT}/${OUT_JSON}data_je/${OUT_INFO} "
[YAM_JSND1]="-os d -o1   -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}data1                  -o2 ${OUT}/${OUT_JSON}data1/${OUT_INFO}   "
[YAM_JSNM]=" -os m       -if yaml -of json  ${IN_YAML} -i2 ${IN_YAML2}  ${OUT}/${OUT_JSON}model/${OUT_JSON_FILE} -o2 ${OUT}/${OUT_JSON}model/${OUT_INFO}   "
)
export KEYS="${!BY_KEY[@]}"
export KEYS="YAM_SER YAM_YAM YAM_SQL YAM_WNB YAM_JSNO YAM_JSNO1 YAM_JSND YAM_JSND YAM_JSND YAM_JSND1 YAM_JSNM"
