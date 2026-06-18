#!/bin/bash

#
# Copyright (c) 2024. Bernard Bou.
#

set -e

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

# D I R S

DISTDIR=${outdir}
DATADIR=${indir}
echo "pack ${outfile}-${dbtag}.json.zip to ${DISTDIR} from ${DATADIR}"

# M A I N

mkdir -p ${DISTDIR}

ZIP_ARCHIVE=${DISTDIR}/${outfile}-${dbtag}.json.zip
echo -e "${M}pack to $(basename ${ZIP_ARCHIVE})${Z}"
rm -f ${ZIP_ARCHIVE}
zip -j ${ZIP_ARCHIVE} "${DATADIR}"/*
zip ${ZIP_ARCHIVE} OEWN_LICENSE.md
echo -e "${C}"
unzip -l ${ZIP_ARCHIVE}
echo -en "${Z}"
echo -e "${G}${ZIP_ARCHIVE}${Z}"
echo
