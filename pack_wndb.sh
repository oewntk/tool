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

compat=$1
[ "$#" -eq 0 ] || shift
if [ -z "${compat}" ]; then
  compat=""
fi

echo -e "${M}${dbtag}-${build} ${C}${indir} -> ${outdir}${Z}"
echo "pack to ${outdir} from ${indir}"

# A R C H I V E S

TAR_ARCHIVE=${outdir}/oewn${compat}-${dbtag}.dict.tar.gz
ZIP_ARCHIVE=${outdir}/oewn${compat}-${dbtag}.zip
TAR_ARCHIVE_MIN=${outdir}/oewn${compat}-${dbtag}_bare.dict.tar.gz
ZIP_ARCHIVE_MIN=${outdir}/oewn${compat}-${dbtag}_bare.zip

# M A I N

mkdir -p ${outdir}

# prepare dict container

if [ ! -e "${indir}" -a ! -d "${indir}" ]; then
  echo -e "${R}Non existent wndb dir${Z}"
  exit 1
fi
parent="$(dirname ${indir})"
base="$(basename ${indir})"
pushd "${parent}" >/dev/null
ln -sfT "${base}" dict
popd >/dev/null

# full

echo -e "${M}pack to $(basename ${TAR_ARCHIVE})${Z}"
rm -f ${TAR_ARCHIVE}
tar czfh ${TAR_ARCHIVE} OEWN_LICENSE.md -C ${parent} dict
echo -e "${C}"
tar tvf ${TAR_ARCHIVE}
echo -en "${Z}"
echo -e "${G}${TAR_ARCHIVE}${Z}"
echo

echo -e "${M}pack to $(basename ${ZIP_ARCHIVE})${Z}"
rm -f ${ZIP_ARCHIVE}
zip -j ${ZIP_ARCHIVE} ${parent}/dict/*
zip ${ZIP_ARCHIVE} OEWN_LICENSE.md
echo -e "${C}"
unzip -l ${ZIP_ARCHIVE}
echo -en "${Z}"
echo -e "${G}${ZIP_ARCHIVE}${Z}"
echo

# bare

echo -e "${M}pack to $(basename ${TAR_ARCHIVE_MIN})${Z}"
rm -f ${TAR_ARCHIVE_MIN}
tar czhf ${TAR_ARCHIVE_MIN} OEWN_LICENSE_short.md -C ${parent} --exclude --exclude dict/lexnames --exclude dict/sensemap.txt --exclude dict/cntlist --exclude dict/cntlist.rev dict
echo -e "${C}"
tar tvf ${TAR_ARCHIVE_MIN}
echo -en "${Z}"
echo -e "${G}${TAR_ARCHIVE_MIN}${Z}"
echo

echo -e "${M}pack to $(basename ${ZIP_ARCHIVE_MIN})${Z}"
rm -f ${ZIP_ARCHIVE_MIN}
zip -j ${ZIP_ARCHIVE_MIN} ${parent}/dict/* -x "*lexnames" -x "*sensemap.txt" -x "*cntlist" -x "*cntlist.rev"
zip ${ZIP_ARCHIVE_MIN} OEWN_LICENSE_short.md
echo -e "${C}"
unzip -l ${ZIP_ARCHIVE_MIN}
echo -en "${Z}"
echo -e "${G}${ZIP_ARCHIVE_MIN}${Z}"
echo

#rm ${indir}/dict
