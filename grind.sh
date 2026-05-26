#!/bin/bash

#
# Copyright (c) 2021-2024. Bernard Bou.
#

set -e

if [ "$*" == "" ]; then
  echo "
in1          String,                                         Input dir or file
out          String,                                         Output dir or file
in2          String,  shortName = i2, fullName = in2,        Extra input dir or file         default=
inFormat     String,  shortName = if, fullName = in_format,  In format                       default=yaml
inPlus       Boolean, shortName = p,  fullName = plus,       Plus input                      default=false
outFormat    String,  shortName = of, fullName = out_format, Output format                   default=yaml
out2         String,  shortName = o2, fullName = out2,       Extra input dir or file         default=
outOne       Boolean, shortName = o1, fullName = out_one,    Output one file                 default=false
outMerge     Boolean, shortName = m,  fullName = merge,      Do not group generated entries  default=false
verbose      Boolean, shortName = v,  fullName = verbose,    Verbose output                  default=false
"
exit 1
fi

jar=grind-2.4.0-uber.jar
if [ ! -e "${jar}" ]; then
  if [ ! -e "target/${jar}" ]; then
    echo "Non existing uber jar" >&2
    exit 1
    fi
  ln -s "target/${jar}"
  fi
if [ ! -e "${jar}" ]; then
  echo "Non existing uber jar" >&2
  exit 2
  fi
eval java -ea -jar "${jar}" "$*"
