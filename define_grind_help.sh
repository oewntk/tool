#!/bin/bash

# Copyright (c) 2021-2026. Bernard Bou.

export grind_help="
in1                 String,                                             Input dir or file
out                 String,                                             Output dir or file
in2                 String,  shortName = i2, fullName = in2,            Extra Input dir or file,                                  default=
inFormat            String,  shortName = if, fullName = in_format,      In format,                                                default=yaml
inPlus              Boolean, shortName = p,  fullName = plus,           Plus input,                                               default=false
outFormat           String,  shortName = of, fullName = out_format,     Output format,                                            default=yaml
out2                String,  shortName = o2, fullName = out_info,       Extra Output dir or file,                                 default=
outOne              Boolean, shortName = o1, fullName = out_one,        Output one file,                                          default=false
outMerge            Boolean, shortName = m,  fullName = merge,          Do not group generated entries,                           default=false
outSerialization    (o,d,m), shortName = os, fullName = serialization,  Serialization mode (oewn,data,model),                     default=oewn
outYaml             (a,b,f), shortName = y,  fullName = yaml,           YAML format (auto,block,flow),                            default=auto
outJson             (s,b,j), shortName = j,  fullName = json,           JSON method (value_wrapper,json_element,any_serializer),  default=any_serializer
verbose             Boolean, shortName = v,  fullName = verbose,        Verbose output,                                           default=false
"
