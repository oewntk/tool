#!/bin/bash

# Copyright (c) 2021-2026. Bernard Bou.

set -e

source define_colors.sh

if [ "$1" == "-h" ]; then
  echo "
in1          String,                                         Input dir or file
out          String,                                         Output dir or file
in2          String,  shortName = i2, fullName = in2,        Extra Input dir or file         default=
inFormat     String,  shortName = if, fullName = in_format,  In format                       default=yaml
inPlus       Boolean, shortName = p,  fullName = plus,       Plus input                      default=false
outFormat    String,  shortName = of, fullName = out_format, Output format                   default=yaml
out2         String,  shortName = o2, fullName = out_info,   Extra Output dir or file        default=
outOne       Boolean, shortName = 1,  fullName = out_one,    Output one file                 default=false
outMerge     Boolean, shortName = m,  fullName = merge,      Do not group generated entries  default=false
verbose      Boolean, shortName = v,  fullName = verbose,    Verbose output                  default=false
"
exit 1
fi

IN_YAML=yaml
IN_YAML2=yaml2
IN_YAMLPLUS=yamlplus

OUT=out
OUTPLUS=out-plus

OUT_INFO=oewn.info
OUT_SER=ser/oewn.ser
OUT_SER2=ser/oewn.ser.info
OUT_YAML=yaml
OUT_WNDB=wndb
OUT_JSON=json
OUT_JSON_FILE=${OUT_JSON}/oewn.json
OUT_SQL=sql/data

declare -A BY_KEY
export BY_KEY=(
[YAM_SER]="     -v    -if yaml -i2 ${IN_YAML2}  -of ser  -o2 ${OUT}/${OUT_SER2}                  ${IN_YAML}      ${OUT}/${OUT_SER}"
[YAM_YAM]="     -v    -if yaml -i2 ${IN_YAML2}  -of yaml -o2 ${OUT}/${OUT_YAML}/${OUT_INFO}      ${IN_YAML}      ${OUT}/${OUT_YAML}"
[YAM_SQL]="     -v    -if yaml -i2 ${IN_YAML2}  -of sql  -o2 ${OUT}/${OUT_SQL}/${OUT_INFO}       ${IN_YAML}      ${OUT}/${OUT_SQL}"
[YAM_WNB]="     -v    -if yaml -i2 ${IN_YAML2}  -of wndb -o2 ${OUT}/${OUT_WNDB}/${OUT_INFO}      ${IN_YAML}      ${OUT}/${OUT_WNDB}"
[YAM_JSN]="     -v    -if yaml -i2 ${IN_YAML2}  -of json -o2 ${OUT}/${OUT_JSON}/${OUT_INFO}      ${IN_YAML}      ${OUT}/${OUT_JSON_FILE}"

[PLUS_YAM_SER]="-v -p -if yaml -i2 ${IN_YAML2}  -of ser  -o2 ${OUTPLUS}/${OUT_SER2}              ${IN_YAMLPLUS}  ${OUTPLUS}/${OUT_SER}"
[PLUS_YAM_YAM]="-v -p -if yaml -i2 ${IN_YAML2}  -of yaml -o2 ${OUTPLUS}/${OUT_YAML}/${OUT_INFO}  ${IN_YAMLPLUS}  ${OUTPLUS}/${OUT_YAML}"
[PLUS_YAM_SQL]="-v -p -if yaml -i2 ${IN_YAML2}  -of sql  -o2 ${OUTPLUS}/${OUT_SQL}/${OUT_INFO}   ${IN_YAMLPLUS}  ${OUTPLUS}/${OUT_SQL}"
[PLUS_YAM_WNB]="-v -p -if yaml -i2 ${IN_YAML2}  -of wndb -o2 ${OUTPLUS}/${OUT_WNDB}/${OUT_INFO}  ${IN_YAMLPLUS}  ${OUTPLUS}/${OUT_WNDB}"
#[PLUS_YAM_JSN]="-v -p -if yaml -i2 ${IN_YAML2}  -of json -o2 ${OUTPLUS}/${OUT_JSON}/${OUT_INFO}  ${IN_YAMLPLUS}  ${OUTPLUS}/${OUT_JSON_FILE}"
)
export KEYS="${!BY_KEY[@]}"

for k in ${KEYS}; do
  args=${BY_KEY[${k}]}

  echo -e "${Y}${k}${Z}"
  echo -e "${B}${args}${Z}"
  cl="./grind.sh ${args}"
  eval "${cl}"
done