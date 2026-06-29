#!/bin/bash

#
# Copyright (c) 2024. Bernard Bou.
#

set -Eeo pipefail
on_err() {
  local exit_code=$?
  local line_no=${BASH_LINENO[0]}
  echo "Error on line $line_no (exit code: $exit_code)."
}
trap on_err ERR

source define_build.sh

# S Q L I T E

./load_sqlite.sh

# M Y S Q L

./load_mysql.sh

