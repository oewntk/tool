#!/bin/bash

# Copyright (c) 2021-2026. Bernard Bou.

set -e

source define_colors.sh
source define_grind_help.sh
source define_args_plus.sh

if [ "$1" == "-h" ]; then
  echo "${grind_help}"
  exit 1
fi

for k in ${KEYS_PLUS}; do
  args=${BY_KEY_PLUS[${k}]}
  args=$(echo "${args}" | sed 's/\s\+/ /g')
  echo -e "${Y}${k}${Z}"
  echo -e "${B}${args}${Z}"

  cl="./grind.sh -v ${args}"
  eval "${cl}"
done
