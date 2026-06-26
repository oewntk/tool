#!/bin/bash

# Copyright (c) 2021-2026. Bernard Bou.

set -Eeo pipefail
on_err() {
  local exit_code=$?
  local line_no=${BASH_LINENO[0]}
  echo "Error on line $line_no (exit code: $exit_code)."
  # do cleanup here
}
trap on_err ERR

source define_colors.sh
source define_args_factory.sh

if [ "$1" == "-h" ]; then
  ./factory.sh --help
  exit 0
fi

ks="$1"
if [ -z "$1" ] ;then
  ks="${KEYS_FACTORY}"
  fi

for k in ${ks}; do
  args=${BY_KEY_FACTORY[${k}]}
  args=$(echo "${args}" | sed 's/\s\+/ /g')
  echo -e "${Y}${k}${Z}"
  echo -e "${B}${args}${Z}"
  cl="./factory.sh -v -tt ${args}"
  if ! eval "${cl}"; then
    echo -e "${R}${cl}${Z}"
    fi
done
