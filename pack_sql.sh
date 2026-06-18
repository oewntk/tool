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

echo -e "${M}${dbtag}-${build} ${C}${indir} -> ${outdir}${Z}"

# D I R S

DISTDIR=${outdir}
DATADIR=${indir}
echo "pack to ${DISTDIR} from ${DATADIR}"

# M A I N

echo -e "${C}packing ${Y}${TAG}${Z}"
echo "ant pack with dbtag=${TAG}"
ant -f make-dist-sql.xml -Ddbdir=${DATADIR} -Ddbtag=${TAG} -Dversion="${BUILD}" -Doutdir="${DISTDIR}"

