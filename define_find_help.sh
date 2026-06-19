#!/bin/bash

# Copyright (c) 2021-2026. Bernard Bou.

export find_help="
in1                 String,                                                Input dir or file
in2                 String,  shortName = i2, fullName = in2,               Extra Input dir or file,                                       default=
inFormat            String,  shortName = if, fullName = in_format,         In format,                                                     default=yaml
inSerialization     (o,d,m), shortName = is, fullName = in_serialization,  Input serialization mode (oewn,data,model),                    default=oewn
inJsonMethod        (a,v,j), shortName = ij, fullName = in_json,           JSON input method (any_serializer,value_wrapper,json_element), default=any_serializer
inOne               Boolean, shortName = i1, fullName = in_one,            Input in one file,                                             default=false
inPlus              Boolean, shortName = p,  fullName = plus,              Plus input,                                                    default=false
"