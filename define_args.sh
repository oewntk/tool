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
OUT_SQL=sql/data

declare -A BY_KEY
export BY_KEY=(
[YAM_SER]="       -v               -if yaml -i2 ${IN_YAML2}  -of ser  -o2 ${OUT}/${OUT_SER2}                  ${IN_YAML}      ${OUT}/${OUT_SER}"
[YAM_YAM]="       -v               -if yaml -i2 ${IN_YAML2}  -of yaml -o2 ${OUT}/${OUT_YAML}/${OUT_INFO}      ${IN_YAML}      ${OUT}/${OUT_YAML}"
[YAM_SQL]="       -v               -if yaml -i2 ${IN_YAML2}  -of sql  -o2 ${OUT}/${OUT_SQL}/${OUT_INFO}       ${IN_YAML}      ${OUT}/${OUT_SQL}"
[YAM_WNB]="       -v               -if yaml -i2 ${IN_YAML2}  -of wndb -o2 ${OUT}/${OUT_WNDB}/${OUT_INFO}      ${IN_YAML}      ${OUT}/${OUT_WNDB}"
[YAM_JSNO]="      -v -os o         -if yaml -i2 ${IN_YAML2}  -of json -o2 ${OUT}/${OUT_JSON}/${OUT_INFO}      ${IN_YAML}      ${OUT}/${OUT_JSON}"
[YAM_JSNO1]="     -v -os o -o1     -if yaml -i2 ${IN_YAML2}  -of json -o2 ${OUT}/${OUT_JSON}1/${OUT_INFO}     ${IN_YAML}      ${OUT}/${OUT_JSON}1"
[YAM_JSND]="      -v -os d         -if yaml -i2 ${IN_YAML2}  -of json -o2 ${OUT}/${OUT_JSON}/${OUT_INFO}      ${IN_YAML}      ${OUT}/${OUT_JSON}data"
[YAM_JSND1]="     -v -os d -o1     -if yaml -i2 ${IN_YAML2}  -of json -o2 ${OUT}/${OUT_JSON}/${OUT_INFO}      ${IN_YAML}      ${OUT}/${OUT_JSON}data1"
[YAM_JSNM]="      -v -os m         -if yaml -i2 ${IN_YAML2}  -of json -o2 ${OUT}/${OUT_JSON}/${OUT_INFO}      ${IN_YAML}      ${OUT}/${OUT_JSON}model/${OUT_JSON_FILE}"
)
export KEYS="${!BY_KEY[@]}"
