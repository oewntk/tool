#!/bin/bash

sed -n 's/.*\(!![a-zA-Z._]*\).*/\1/p' "$@"