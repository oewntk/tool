#!/bin/bash

#
# Copyright (c) 2024. Bernard Bou.
#

set -Eeuo pipefail

on_err() {
  local exit_code=$?
  local line_no=${BASH_LINENO[0]}
  echo "Error on line $line_no (exit code: $exit_code)."
  # do cleanup here
}

trap on_err ERR

source define_build.sh
source define_colors.sh

# D I S T

distdir=../dist/data
distdir=$(readlink -m "${distdir}")

# L I B

function check_file() {
    local t=$(readlink -m "$1")
    if [ -e "${t}" ]; then
        echo -e "${B}${t}${Z}"
        pushd "${distdir}" >/dev/null
        ln -sf "${t}"
        popd  >/dev/null
    else
        echo -e "${R}${t}${Z}"
        return 1
    fi
}

# M A I N

echo -e "${Y}pack plus${Z}"

echo -e "${M}yaml2json${Z}"
./pack_json.sh "${TAG}" "${BUILD}" "out-plus/json" "${distdir}/json" "oewn-plus"
./pack_json.sh "${TAG}" "${BUILD}" "out-plus/json_data" "${distdir}/json" "oewn-plus-data"
./pack_json.sh "${TAG}" "${BUILD}" "out-plus/json_model" "${distdir}/json" "oewn-plus-model"
check_file "${distdir}/json/oewn-plus-${TAG}.json.zip"
check_file "${distdir}/json/oewn-plus-data-${TAG}.json.zip"
check_file "${distdir}/json/oewn-plus-model-${TAG}.json.zip"

echo -e "${M}yaml2wndb${Z}"
./pack_wndb.sh "plus-${TAG}" "${BUILD}" "out-plus/wndb" "${distdir}/wndb" ''
./pack_wndb.sh "plus-${TAG}" "${BUILD}" "out-plus/wndb_compat" "${distdir}/wndb" '-compat'
check_file "${distdir}/wndb/oewn-plus-${TAG}.dict.tar.gz"
check_file "${distdir}/wndb/oewn-plus-${TAG}.zip"
check_file "${distdir}/wndb/oewn-plus-${TAG}_bare.dict.tar.gz"
check_file "${distdir}/wndb/oewn-plus-${TAG}_bare.zip"
check_file "${distdir}/wndb/oewn-compat-plus-${TAG}.dict.tar.gz"
check_file "${distdir}/wndb/oewn-compat-plus-${TAG}.zip"

echo -e "${M}yaml2sql${Z}"
./pack_sql.sh "${TAG}" "${BUILD}" "out-plus/sql" "${distdir}/sql" "-plus"
check_file "${distdir}/sql/oewn-plus-${TAG}-mysql-${BUILD}.zip"
check_file "${distdir}/sql/oewn-plus-${TAG}-sqlite-${BUILD}.zip"
