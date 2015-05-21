#!/bin/bash

ASEXECCACHE=~/.asexeccache
SDKLOCCACHE=~/.sdklockcache

if [ $# -gt 0 ]
then
  if [ $1 == "-r" ]
  then
    rm -f ${ASEXECCACHE} ${SDKLOCCACHE}
  elif [ $1 == "-e" ]
  then
    echo export ANDROID_HOME=$(cat ${SDKLOCCACHE})
    exit 0
  else
    echo "usage: $0 [-r]"
    echo "options"
    echo "  -r    remove cache files and search for executable and sdk again"
    exit 1
  fi
fi

function findandroidstudioexecutable
{
  if [ -e '/Applications/Android Studio.app/Contents/MacOS/studio' ] # assume we are running on Mac OSX
  then
    ASEXEC=("/Applications/Android Studio.app/Contents/MacOS/studio")
  else
    ASEXEC=($(find ~ -name 'studio.sh'))
  fi

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
  if [ ${#SDKLOC[@]} -eq 0 ]
  then
    echo "Error: no SDK found"
    exit 1
  fi

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

export ANDROID_HOME=$(cat ${SDKLOCCACHE})

echo Starting "$(cat ${ASEXECCACHE})"
exec "$(cat ${ASEXECCACHE})"
