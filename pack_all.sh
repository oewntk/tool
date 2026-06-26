#!/bin/bash

#
# Copyright (c) 2024. Bernard Bou.
#

set -Eeo pipefail
on_err() {
  local exit_code=$?
  local line_no=${BASH_LINENO[0]}
  echo "Error on line $line_no (exit code: $exit_code)."
  # do cleanup here
}
trap on_err ERR

source define_build.sh
source define_colors.sh

./pack_base_all.sh "$@"
./pack_plus_all.sh "$@"
