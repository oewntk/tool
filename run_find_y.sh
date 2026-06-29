#!/bin/bash

# Copyright (c) 2021-2026. Bernard Bou.

set -Eeo pipefail
on_err() {
  local exit_code=$?
  local line_no=${BASH_LINENO[0]}
  echo "Error on line $line_no (exit code: $exit_code)."
}
trap on_err ERR

source define_colors.sh
source define_args_find.sh

if [ "$1" == "-h" ]; then
  ./factory.sh --help
  exit 0
fi

# MODEL (compulsory)
model_key="$1"
[ "$#" -eq 0 ] || shift
model_args=${BY_KEY_FIND[${model_key}]}
if [ -z "$model_args" ] ;then
  echo -e "${R}Unknown key ${key}${Z}"
  exit $1
  fi
model_args=$(echo "$model_args" | sed 's/\s\+/ /g')
echo -e "${B}${model_args}${Z}"

# OUT FORMAT (optional)
out_key="$1"
out_args=${BY_KEY_OUT[${out_key}]}
if [ ! -z "$out_args" ] ;then
  [ "$#" -eq 0 ] || shift
fi
out_args=$(echo "$out_args" | sed 's/\s\+/ /g')
echo -e "${M}${out_args}${Z}"

# YAML OUT FORMAT (optional)
yaml_out_key="$1"
yaml_out_args=${BY_KEY_YAML_OUT[${yaml_out_key}]}
if [ ! -z "$yaml_out_args" ] ;then
  [ "$#" -eq 0 ] || shift
fi
yaml_out_args=$(echo "$yaml_out_args" | sed 's/\s\+/ /g')
echo -e "${M}${yaml_out_args}${Z}"

cl="./find.sh ${model_args} ${out_args} ${yaml_out_args} $@"
# echo $cl

if ! eval "${cl}"; then
    echo -e "${R}${cl}${Z}"
    fi
