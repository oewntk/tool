#!/bin/bash

#
# Copyright (c) 2021-2024. Bernard Bou.
#

set -Eeo pipefail
on_err() {
  local exit_code=$?
  local line_no=${BASH_LINENO[0]}
  echo "Error on line $line_no (exit code: $exit_code)."
  # do cleanup here
}
trap on_err ERR

#./find.sh -if json -is model out-plus/json_model/oewn-model.json -of json -os data -oj j "$@"
./find_all_y.sh PLUSJSN_M YAM_O AUTO "$@"
