#!/bin/bash

#
# Copyright (c) 2021-2024. Bernard Bou.
#

set -e

if [ "$*" == "" ]; then
  echo "
in1          String,                                         Input dir or file
out          String,                                         Output dir or file
in2          String,  shortName = i2, fullName = in2,        Extra Input dir or file         default=yaml2
operation    String,  shortName = do, fullName = operation,  Operation                       default=nothing
inFormat     String,  shortName = if, fullName = in_format,  In format                       default=yaml
inPlus       Boolean, shortName = p,  fullName = plus,       Plus input                      default=false
outFormat    String,  shortName = of, fullName = out_format, Output format                   default=yaml
outInfo      String,  shortName = i,  fullName = out_info,   Output info                     default=oewn.info
outOne       Boolean, shortName = 1,  fullName = out_one,    Output one file                 default=false
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
java -ea -jar "${jar}" "$*"
