#!/bin/bash

# Copyright (c) 2021-2026. Bernard Bou.

set -e

source define_colors.sh
source define_compare_help.sh
source define_args_compare.sh

if [ "$1" == "-h" ]; then
  echo "${compare_help}"
  exit 1
fi

ks="$1"
if [ -z "$1" ] ;then
  ks="${KEYS_COMPARE}"
  fi

for k in ${ks}; do
  args=${BY_KEY_COMPARE[${k}]}
  args=$(echo "${args}" | sed 's/\s\+/ /g')
  echo -e "${Y}${k}${Z}"
  echo -e "${B}${args}${Z}"
  cl="./compare.sh -v -tt ${args}"
  if ! eval "${cl}"; then
    echo -e "${R}${cl}${Z}"
    fi
done
