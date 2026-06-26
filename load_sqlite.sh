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

        base=oewn${subtag}-${TAG}-sqlite-${BUILD}
        source=${base}.zip
        sqlite=${base}.sqlite
        sqlitezip=${sqlite}.zip
        expanded="${base}"

        echo -e "${Y}load ${base}${Z}"

        pushd ../dist/data/sql >/dev/null

        rm -fR ${expanded}/*
        unzip -q ${source} -d ${expanded}

        pushd ${expanded} >/dev/null
          sed -i -r 's/sqlite3 (.*)$/sqlite3 -bail \1 2>>LOG || echo -e "${R}FAILED ${sqlfile}${Z}"/g' restore-sqlite.sh 
          chmod +x restore-sqlite.sh
          ./restore-sqlite.sh -y -d ${sqlite}

          zip ${sqlitezip} ${sqlite}

          mv "${sqlite}" ../
          mv "${sqlitezip}" ../
          ln -sf "../../${sqlitezip}"
        popd >/dev/null

        ln -sf "sql/${sqlite}" "../"
        ln -sf "sql/${sqlitezip}" "../"

        T=${G}
        [ -e "${sqlite}" ] || T=${R}
        echo -e "${T}${sqlite}${Z}"
          
        T=${G}
        [ -e "${sqlitezip}" ] || T=${R}
        echo -e "${T}${sqlitezip}${Z}"

        popd >/dev/null
        
done
