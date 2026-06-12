#!/bin/bash

#
# Copyright (c) 2021-2024. Bernard Bou.
#

set -e

source define_grind_help.sh

if [ "$1" == "-h" ]; then
  echo "${grind_help}"
  exit 1
fi

jar=tool-3.0.1-uber.jar
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
eval java -ea -cp "${jar}" org.oewntk.grind.Make "$*"
