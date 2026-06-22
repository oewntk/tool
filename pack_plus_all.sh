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

function pack_yaml(){
    echo -e "${M}yaml2yaml${Z}"
    ./pack_yaml.sh "${TAG}" "${BUILD}" "out-plus/yaml" "${distdir}/yaml" "oewn-plus"
    check_file "${distdir}/yaml/oewn-plus-${TAG}.yaml.zip"
 }

function pack_json() {
    echo -e "${M}yaml2json${Z}"
    ./pack_json.sh "${TAG}" "${BUILD}" "out-plus/json" "${distdir}/json" "oewn-plus"
    ./pack_json.sh "${TAG}" "${BUILD}" "out-plus/json_data" "${distdir}/json" "oewn-plus-data"
    ./pack_json.sh "${TAG}" "${BUILD}" "out-plus/json_model" "${distdir}/json" "oewn-plus-model"
    check_file "${distdir}/json/oewn-plus-${TAG}.json.zip"
    check_file "${distdir}/json/oewn-plus-data-${TAG}.json.zip"
    check_file "${distdir}/json/oewn-plus-model-${TAG}.json.zip"
}

function pack_wndb() {
    echo -e "${M}yaml2wndb${Z}"
    ./pack_wndb.sh "plus-${TAG}" "${BUILD}" "out-plus/wndb" "${distdir}/wndb" ''
    ./pack_wndb.sh "plus-${TAG}" "${BUILD}" "out-plus/wndb_compat" "${distdir}/wndb" '-compat'
    check_file "${distdir}/wndb/oewn-plus-${TAG}.dict.tar.gz"
    check_file "${distdir}/wndb/oewn-plus-${TAG}.zip"
    check_file "${distdir}/wndb/oewn-plus-${TAG}_bare.dict.tar.gz"
    check_file "${distdir}/wndb/oewn-plus-${TAG}_bare.zip"
    check_file "${distdir}/wndb/oewn-compat-plus-${TAG}.dict.tar.gz"
    check_file "${distdir}/wndb/oewn-compat-plus-${TAG}.zip"
}

function pack_sql(){
    echo -e "${M}yaml2sql${Z}"
    ./pack_sql.sh "${TAG}" "${BUILD}" "out-plus/sql" "${distdir}/sql" "-plus"
    check_file "${distdir}/sql/oewn-plus-${TAG}-mysql-${BUILD}.zip"
    check_file "${distdir}/sql/oewn-plus-${TAG}-sqlite-${BUILD}.zip"
}

# M A I N

echo -e "${Y}pack plus${Z}"
packs="$1"
if [ -z "${packs}" ]; then
  packs="WNDB SQL YAML JSON"
fi
for p in ${packs}; do
  echo -e "${Y}${p}${Z}"
  case "$p" in
    YAML) pack_yaml ;;
    JSON) pack_json ;;
    SQL)  pack_sql  ;;
    WNDB) pack_wndb ;;
  esac
done