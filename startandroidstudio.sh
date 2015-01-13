#!/bin/bash

ASEXECCACHE=~/.asexeccache
SDKLOCCACHE=~/.sdklockcache

if [ $# -gt 0 ]
then
  if [ $1 == "-r" ]
  then
    rm -f ${SEXECCACHE} ${SDKLOCCACH}
  else
    echo "usage: $0 [-r]"
    echo "options"
    echo "  -r    remove cache files and search for executable and sdk again"
    exit 1
  fi
fi

function findandroidstudioexecutable
{
  ASEXEC=($(find ~ -name 'studio.sh'))
  # Check if found files are readable and executable
  INDEX=0
  for asexec in ${ASEXEC[@]}
  do
    if ! [ -x ${asexec} ] && [ -r ${asexec} ]
    then
  	unset ASEXEC[${INDEX}]
    fi
    INDEX=$((INDEX+1))
  done
  
  if [ ${#ASEXEC[@]} -gt 1 ]
  then
    echo "Multiple executables found:"
    INDEX=0
    for asexec in ${ASEXEC[@]}
    do
      echo ${INDEX}: ${asexec}
      INDEX=$((INDEX+1))
    done
    echo "Which executable should be started? (0-$((INDEX-1)))"
    while read answer
    do 
      if [ ${answer} -gt -1 ] && [ ${answer} -lt ${INDEX} ]
      then
        ASEXEC=${ASEXEC[${answer}]}
        break
      fi
    done
  else
    ASEXEC=${ASEXEC[0]}
  fi  
  
  echo ${ASEXEC} > ${ASEXECCACHE}
}

function findandroidsdk
{
  SDKLOC=($(find ~ -name 'SDK Readme.txt' -exec dirname {} \;))
  if [ ${#SDKLOC[@]} -gt 1 ]
  then
    echo "Multiple SDKs found:"
    INDEX=0
    for sdkloc in "${SDKLOC[@]}"
    do
      echo ${INDEX}: ${sdkloc}
      INDEX=$((INDEX+1))
    done
    echo "Which SDK do you want to use? (0-$((INDEX-1)))"
    while read answer
    do 
      if [ ${answer} -gt -1 ] && [ ${answer} -lt ${INDEX} ]
      then
        SDKLOC=${SDKLOC[${answer}]}
        break
      fi
    done
  fi  
  
  echo ${SDKLOC} > ${SDKLOCCACHE}
}

[ -r ${ASEXECCACHE} ] || findandroidstudioexecutable
[ -r ${SDKLOCCACHE} ] || findandroidsdk

export ANDROID_HOME=${SDKLOCCACHE}
exec $(cat ${ASEXECCACHE})
