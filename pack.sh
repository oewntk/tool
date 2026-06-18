#!/bin/bash

#
# Copyright (c) 2024. Bernard Bou.
#

set -e

source define_build.sh

# C O L O R S

export R='\u001b[31m'
export G='\u001b[32m'
export B='\u001b[34m'
export Y='\u001b[33m'
export M='\u001b[35m'
export C='\u001b[36m'
export Z='\u001b[0m'

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

echo -e "${Y}pack${Z}"

echo -e "${M}yaml2json${Z}"
./pack_json.sh "${TAG}" "${BUILD}" "out/json" "${distdir}/json" "oewn"
./pack_json.sh "${TAG}" "${BUILD}" "out/jsondata" "${distdir}/json" "oewn-data"
./pack_json.sh "${TAG}" "${BUILD}" "out/jsonmodel" "${distdir}/json" "oewn-model"
check_file "${distdir}/json/oewn-${TAG}.json.zip"
check_file "${distdir}/json/oewn-data-${TAG}.json.zip"
check_file "${distdir}/json/oewn-model-${TAG}.json.zip"

echo -e "${M}yaml2wndb${Z}" "out/sql" "${distdir}/sql"
./pack_wndb.sh "${TAG}" "${BUILD}" "out/wndb" "${distdir}/wndb"
check_file "${distdir}/wndb/oewn-${TAG}.dict.tar.gz"
check_file "${distdir}/wndb/oewn-${TAG}.zip"
check_file "${distdir}/wndb/oewn-${TAG}_bare.dict.tar.gz"
check_file "${distdir}/wndb/oewn-${TAG}_bare.zip"
#check_file "${distdir}/wndb/oewn-${TAG}_compat.dict.tar.gz"
#check_file "${distdir}/wndb/oewn-${TAG}_compat.zip"

echo -e "${M}yaml2sql${Z}"
./pack_sql.sh "${TAG}" "${BUILD}" "out/sql" "${distdir}/sql"
check_file "${distdir}/sql/oewn-${TAG}-mysql-${BUILD}.zip"
check_file "${distdir}/sql/oewn-${TAG}-sqlite-${BUILD}.zip"
