#!/bin/bash

# Copyright (c) 2021-2026. Bernard Bou.

IN=out
IN_SER=ser/oewn.ser
IN_YAML=yaml
IN_JSON=json
IN_JSON_FILE=oewn-model.json
IN_WNDB=wndb

declare -A BY_KEY_IN
export BY_KEY_IN=(
#         FLAGS       FROM      INPUT
#_____________________________________________________________________
[SER]="               -if ser   ${IN}/${IN_SER}                      "
[YAM]="               -if yaml  ${IN}/${IN_YAML}                     "
[JSNO]="  -is o       -if json  ${IN}/${IN_JSON}                     "
[JSNO1]=" -is o -i1   -if json  ${IN}/${IN_JSON}1                    "
[JSND]="  -is d       -if json  ${IN}/${IN_JSON}data                 "
[JSNDJ]=" -is d -ij j -if json  ${IN}/${IN_JSON}data_je              "
[JSNDV]=" -is d -ij v -if json  ${IN}/${IN_JSON}data_vw              "
[JSND1]=" -is d -i1   -if json  ${IN}/${IN_JSON}data1                "
[JSNM]="  -is m       -if json  ${IN}/${IN_JSON}model/${IN_JSON_FILE}"
[WNB]="               -if wndb  ${IN}/${IN_WNDB}                     "
)

export KEYS_IN="SER YAM JSNO JSNO1 JSND JSNDJ JSNDV JSND1 JSNM WNB" # "${!BY_KEY_IN[@]}"
export KEYS_IN="WNB" # "${!BY_KEY_IN[@]}"