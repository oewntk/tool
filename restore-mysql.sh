#!/bin/bash
#
# Copyright (c) 2024. Bernard Bou.
#

# 22/11/2021

set -e

# C O N S T S

thisdir=`dirname $(readlink -m "$0")`
sqldir="${thisdir}/sql"
dbtype=mysql
modules="wn"
tables="
synsets
words
casedwords
pronunciations
morphs
poses
relations
domains
samples
vframes
vtemplates
adjpositions
lexes
senses
lexes_morphs
lexes_pronunciations
senses_adjpositions
lexrelations
senses_vframes
senses_vtemplates
semrelations
usages 
ilis 
wikidatas
"

# C O L O R S

export R='\u001b[31m'
export G='\u001b[32m'
export B='\u001b[34m'
export Y='\u001b[33m'
export M='\u001b[35m'
export C='\u001b[36m'
export Z='\u001b[0m'

# M A I N

if [ "$1" == "-y" ]; then
	silent=true
	[ "$#" -eq 0 ] || shift
else
  echo -e "${Y}Restore utility for ${dbtype}${Z}"
  echo -e "${R}-the -d switch will delete an existing database with this name${Z}"
  read -r -p "Are you sure? [y/N] " response
  case "$response" in
      [yY][eE][sS]|[yY])
          ;;
      *)
          exit 1
          ;;
  esac
fi

# D E L E T E (PARAM 1)

dbdelete=
if [ "$1" == "-d" ]; then
	dbdelete=true
	[ "$#" -eq 0 ] || shift
fi

# D A T A B A S E (PARAM 2)

db="$1"
if [ -z "${db}" ]; then
	read -p "Enter ${dbtype} database name: " db
fi
export db

# F U N C T I O N S

function process()
{
	local sqlfile="$1"
	local op="$2"
	if [ ! -e "${sqlfile}" ];then
		echo -e "${R}${sqlfile} does not exist${Z}"
		return
	fi
	local base="$(basename "${sqlfile}")"
	#echo "${base}"
	mysql ${creds} "${db}" < "${sqlfile}"
}

function dbexists()
{
	mysql ${creds} -e "\q" ${db} > /dev/null 2> /dev/null
	return $? 
}

function deletedb()
{
	echo -e "${M}delete ${db}${Z}"
	mysql ${creds} -e "DROP DATABASE ${db};"
}

function createdb()
{
	echo -e "${M}create ${db}${Z}"
	mysql ${creds} -e "CREATE DATABASE ${db} DEFAULT CHARACTER SET UTF8;"
}

function getcredentialslegacy()
{
  # read user
	read -p "Enter database user: " dbuser
	if [ -z "${dbuser}" ]; then
		echo "Define ${dbtype} user"
		exit 1
	fi

  # read password unless et in en variable
	if [ -z "$MYSQLPASSWORD" ]; then
		read -s -p "Enter ${dbuser}'s password (type '?' if you want to be asked each time, because it's unsafe): " dbpasswd
	else
	  dbpasswd="$MYSQLPASSWORD"
	fi

  # output as commandline switches
  echo -n "-u ${dbuser} "
	if [ ! -z "${dbpasswd}" ]; then
		if [ "${dbpasswd}" == "?" ]; then
			echo "--password"
		else
			echo "--password=${dbpasswd}"
		fi
	fi
}

function getcredentials()
{
  >&2 echo "This requires mysql_config_editor."
  profiles=`mysql_config_editor print --all | grep '\[.*\]'`
  if [ ! -z "${profiles}" ]; then
    >&2 echo "Existing profiles recorded by mysql_config_editor:"
    >&2 echo "${profiles}"
  fi

  # read profile
  read -p "Enter database user profile: " dbprofile
  if [ -z "${dbprofile}" ]; then
    echo "Define ${dbtype} user profile"
    exit 1
  fi

  if ! echo "${profiles}" | grep -q "\[${dbprofile}\]"; then

    # read user
    read -p "Enter database user: " dbuser
    if [ -z "${dbuser}" ]; then
      echo "Define ${dbtype} user"
      exit 1
    fi

    # editor
    >&2 echo "Passing data to mysql_config_editor (password will be obfuscated ~/.mylogin.cnf)"
    mysql_config_editor set --login-path=${dbprofile} --host=localhost --user=${dbuser} --password

  fi

	# output as commandline switches
	echo "--login-path=${dbprofile}"
}

# R U N

echo -e "${M}restoring ${db}${Z}"

#credentials
#export lcreds=`getcredentialslegacy`
#echo "credentials (old style) ${lcreds}"
export creds=`getcredentials`
#echo "credentials ${creds}"

#database
if [ ! -z "${dbdelete}" ]; then
	deletedb
fi
if ! dbexists; then
	createdb
fi

# modules
for m in ${modules}; do
	echo -e "${C}${m}${Z}"
	for op in create data index reference; do
		echo -e "${M}${op}${Z}"
		case ${op} in
			data) 
				dir="${sqldir}/${op}"
				suffix=
				;;
		 	create|index|reference)
		 		dir="${sqldir}/${dbtype}/${op}"
				suffix="-${op}"
		 		;;	
		esac
		for table in ${tables}; do
			f="${dir}/${table}${suffix}.sql"
			if [ ! -e "${f}" -a "${op}" == "reference" ]; then
			  continue
			fi
			echo -e "sql=${Y}$(basename ${f})${Z}"
			process "${f}" "${op}"
		done
	done
done
