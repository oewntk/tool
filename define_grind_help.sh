#!/bin/bash

# Copyright (c) 2021-2026. Bernard Bou.

export grind_help="
in1                 String,                                                Input dir or file
out                 String,                                                Output dir or file
in2                 String,  shortName = i2, fullName = in2,               Extra Input dir or file,                                       default=
inFormat            String,  shortName = if, fullName = in_format,         In format,                                                     default=yaml
inInverses          Boolean, shortName = iv, fullName = in_inverses,       Generate inverse relations,                                    default=true
inSerialization     (o,d,m), shortName = is, fullName = in_serialization,  Input serialization mode (oewn,data,model),                    default=oewn
inJsonMethod        (a,v,j), shortName = ij, fullName = in_json,           JSON input method (any_serializer,value_wrapper,json_element), default=any_serializer
inOne               Boolean, shortName = i1, fullName = in_one,            Input in one file,                                             default=false
inPlus              Boolean, shortName = p,  fullName = plus,              Plus input,                                                    default=false
outFormat           String,  shortName = of, fullName = out_format,        Output format,                                                 default=yaml
out2                String,  shortName = o2, fullName = out_info,          Extra Output dir or file,                                      default=
outNoInverses       Boolean, shortName = ov, fullName = out_no_inverses,   Do not output inverse relations                                default=false
outMerge            Boolean, shortName = m,  fullName = merge,             Do not group generated entries,                                default=false
outSerialization    (o,d,m), shortName = os, fullName = out_serialization, Output serialization mode (oewn,data,model),                   default=oewn
outYaml             (a,b,f), shortName = oy, fullName = yaml,              YAML format (auto,block,flow),                                 default=auto
outJson             (s,b,j), shortName = oj, fullName = json,              JSON method (value_wrapper,json_element,any_serializer),       default=any_serializer
outOne              Boolean, shortName = o1, fullName = out_one,           Output one file,                                               default=false
outPretty           Boolean, shortName = op, fullName = out_pretty,        JSON pretty print output,                                      default=true
verbose             Boolean, shortName = v,  fullName = verbose,           Verbose output,                                                default=false
"