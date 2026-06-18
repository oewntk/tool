@echo off
rem 1313ou@gmail.com
rem 03/12/2021 

set /P DB=Enter database name:

set DBTYPE=mysql
set DBUSER=root
set /P DBPWD=Enter %DBUSER% password:
set TABLES=synsets words casedwords pronunciations morphs poses relations domains samples vframes vtemplates adjpositions lexes senses lexes_morphs lexes_pronunciations senses_adjpositions lexrelations senses_vframes senses_vtemplates semrelations usages ilis wikidatas

if "%1"=="-d" call :deletedb
call :dbexists
if not %DBEXISTS%==0 call :createdb

for %%T in (%TABLES%) do call :process sql/%DBTYPE%/create/%%T-create.sql "create %%T" %DB%
for %%T in (%TABLES%) do call :process sql/data/%%T.sql "data %%T" %DB%
for %%T in (%TABLES%) do call :process sql/%DBTYPE%/index/%%T-index.sql "index %%T" %DB% --force
for %%T in (%TABLES%) do call :process sql/%DBTYPE%/reference/%%T-reference.sql "reference %%T" %DB% --force
goto :eof

:process
setlocal
echo process %2
if not exist %1 goto :processerror
echo 	sql %1
mysql -u %DBUSER% --password=%DBPWD% %4 %3 < %1
goto :processend
:processerror
echo	sql file %1 does not exist
:processend
endlocal
goto :eof

:dbexists
setlocal
mysql -u %DBUSER% --password=%DBPWD% -e "\q" %DB% > NUL 2> NUL
endlocal & set DBEXISTS=%ERRORLEVEL% 
goto :eof

:deletedb
setlocal
echo delete %DB%
mysql -u %DBUSER% --password=%DBPWD% -e "DROP DATABASE %DB%;"
endlocal
goto :eof

:createdb
setlocal
echo create %DB%
mysql -u %DBUSER% --password=%DBPWD% -e "CREATE DATABASE %DB% DEFAULT CHARACTER SET UTF8;"
endlocal
goto :eof

:eof
