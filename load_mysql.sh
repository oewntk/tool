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

# M A I N

for subtag in "" "-plus"; do

        base=oewn${subtag}-${TAG}-mysql-${BUILD}
        source=${base}.zip
        expanded="${base}"

        echo -e "${Y}load ${base}${Z}"

        pushd ../dist/data/sql >/dev/null

        rm -fR ${expanded}/*
        unzip -q ${source} -d ${expanded}

        pushd ${expanded} >/dev/null
          chmod +x restore-mysql.sh
          ./restore-mysql.sh -y -d "oewn${subtag}"
        popd >/dev/null

        popd >/dev/null

done
