#!/bin/bash

# Copyright (c) 2021-2026. Bernard Bou.

export factory_help="
inA                 String,                                                           Input dir or file for model A
inB                 String,                                                           Input dir or file for model B
in2                 String,  shortName = (A|B)i2, fullName = (a|b)_in2,               Extra Input dir or file,                                       default=
inFormat            String,  shortName = (A|B)if, fullName = (a|b)_in_format,         In format,                                                     default=yaml
inSerialization     (o,d,m), shortName = (A|B)is, fullName = (a|b)_in_serialization,  Input serialization mode (oewn,data,model),                    default=oewn
inJsonMethod        (a,v,j), shortName = (A|B)ij, fullName = (a|b)_in_json,           JSON input method (any_serializer,value_wrapper,json_element), default=any_serializer
inOne               Boolean, shortName = (A|B)i1, fullName = (a|b)_in_one,            Input in one file,                                             default=false
inPlus              Boolean, shortName = (A|B)p,  fullName = (a|b)_plus,              Plus input,                                                    default=false
"