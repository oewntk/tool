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
source define_colors.sh

# P A R A M S

dbtag=$1
[ "$#" -eq 0 ] || shift
if [ -z "${dbtag}" ]; then
  dbtag=TAG
fi

build=$1
[ "$#" -eq 0 ] || shift
if [ -z "${build}" ]; then
  build=BUILD
fi

indir=$1
[ "$#" -eq 0 ] || shift
if [ -z "${indir}" ]; then
  indir=$(pwd)
fi

outdir=$1
[ "$#" -eq 0 ] || shift
if [ -z "${outdir}" ]; then
  outdir=$(pwd)
fi

outfile=$1
[ "$#" -eq 0 ] || shift
if [ -z "${outfile}" ]; then
  outfile="oewn"
fi

echo -e "${M}${dbtag}-${build} ${C}${indir} -> ${outdir}/${outfile}${Z}"
echo "pack ${outfile}-${dbtag}.yaml.zip to ${outdir} from ${indir}"

# M A I N

mkdir -p ${outdir}

ZIP_ARCHIVE=${outdir}/${outfile}-${dbtag}.yaml.zip
echo -e "${M}pack to $(basename ${ZIP_ARCHIVE})${Z}"
rm -f ${ZIP_ARCHIVE}
zip -j ${ZIP_ARCHIVE} "${indir}"/*
zip ${ZIP_ARCHIVE} OEWN_LICENSE.md
echo -e "${C}"
unzip -l ${ZIP_ARCHIVE}
echo -en "${Z}"
echo -e "${G}${ZIP_ARCHIVE}${Z}"
echo
