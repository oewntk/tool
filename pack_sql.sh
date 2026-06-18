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

plus=$1
[ "$#" -eq 0 ] || shift
if [ -z "${plus}" ]; then
  plus=""
fi

echo -e "${M}${dbtag}-${build} ${C}${indir} -> ${outdir}${Z}"
echo "pack to ${outdir} from ${indir} plus=${plus}"

# M A I N

echo -e "${C}packing ${Y}${dbtag}${Z}"
echo "ant pack with dbtag=${dbtag}"
ant -f make-dist-sql.xml -Ddbdir=${indir} -Ddbtag=${dbtag} -Dversion="${build}" -Doutdir="${outdir}" -Dplus="${plus}"

