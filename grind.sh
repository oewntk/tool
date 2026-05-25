#!/bin/bash

#
# Copyright (c) 2021-2024. Bernard Bou.
#

set -e
#
# IN="$1"
# if [ -z "$1" ]; then
# 	IN=wndb31
# fi
# echo "WNDB:  ${IN}" 1>&2;
#
# IN2="$2"
# if [ -z "$2" ]; then
# 	IN2=wndb2
# fi
# echo "WNDB2: ${IN2}" 1>&2;
#
# OUTFILE="$3"
# if [ -z "$3" ]; then
# 	OUTFILE=json/wn31.json
# fi
# mkdir -p $(dirname "${OUTDIR}")
# echo "OUT:   ${OUTFILE}" 1>&2;
#
# opts="-pretty"

jar=grind-2.4.0-uber.jar
if [ ! -e "${jar}" ]; then
  if [ ! -e "target/${jar}" ]; then
    echo "Non existing uber jar" >&2
    exit 1
    fi
  ln -s "target/${jar}"
  fi
if [ ! -e "${jar}" ]; then
  echo "Non existing uber jar" >&2
  exit 2
  fi
java -ea -jar "${jar}" ${opts} "${IN}" "${OUTFILE}"
