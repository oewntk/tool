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
[PLUS_YAM_SER]="   -v -p           -if yaml -i2 ${IN_YAML2}  -of ser  -o2 ${OUT}/${OUT_SER2}              ${IN_YAML}  ${OUT}/${OUT_SER}"
[PLUS_YAM_YAM]="   -v -p           -if yaml -i2 ${IN_YAML2}  -of yaml -o2 ${OUT}/${OUT_YAML}/${OUT_INFO}  ${IN_YAML}  ${OUT}/${OUT_YAML}"
[PLUS_YAM_SQL]="   -v -p           -if yaml -i2 ${IN_YAML2}  -of sql  -o2 ${OUT}/${OUT_SQL}/${OUT_INFO}   ${IN_YAML}  ${OUT}/${OUT_SQL}"
[PLUS_YAM_WNB]="   -v -p           -if yaml -i2 ${IN_YAML2}  -of wndb -o2 ${OUT}/${OUT_WNDB}/${OUT_INFO}  ${IN_YAML}  ${OUT}/${OUT_WNDB}"
[PLUS_YAM_JSNO]="  -v -p -os o     -if yaml -i2 ${IN_YAML2}  -of json -o2 ${OUT}/${OUT_JSON}/${OUT_INFO}  ${IN_YAML}  ${OUT}/${OUT_JSON}"
[PLUS_YAM_JSNO1]=" -v -p -os o -o1 -if yaml -i2 ${IN_YAML2}  -of json -o2 ${OUT}/${OUT_JSON}/${OUT_INFO}  ${IN_YAML}  ${OUT}/${OUT_JSON}1"
[PLUS_YAM_JSND]="  -v -p -os d     -if yaml -i2 ${IN_YAML2}  -of json -o2 ${OUT}/${OUT_JSON}/${OUT_INFO}  ${IN_YAML}  ${OUT}/${OUT_JSON}data"
[PLUS_YAM_JSND1]=" -v -p -os d -o1 -if yaml -i2 ${IN_YAML2}  -of json -o2 ${OUT}/${OUT_JSON}/${OUT_INFO}  ${IN_YAML}  ${OUT}/${OUT_JSON}data1"
[PLUS_YAM_JSNM]="  -v -p -os m     -if yaml -i2 ${IN_YAML2}  -of json -o2 ${OUT}/${OUT_JSON}/${OUT_INFO}  ${IN_YAML}  ${OUT}/${OUT_JSON}model/${OUT_JSON_FILE}"
)
export KEYS_PLUS="${!BY_KEY_PLUS[@]}"
