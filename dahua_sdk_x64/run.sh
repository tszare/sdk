#! /bin/bash

basepath=$(cd `dirname $0`; pwd)
cd $basepath

CURR_PATH=`pwd ./`
echo $CURR_PATH

JARFILE=client_video.jar

_obf_jar() {
    mvn clean compile test-compile jar:jar -Ppro -Dmaven.test.skip=true
    java -jar _obf/allatori.jar _obf/_config.xml
    cp ./target/obf-client_video.jar ./target/client_video.jar
}

remote_dir() {
    REMOTE_HOST=$1
    REMOTE_DIR=$2
    REMOTE_PORT=22
    if [ "x$3" = "x" ]; then
	REMOTE_PORT=22
    else
	REMOTE_PORT=$3
    fi
    if ssh -p $REMOTE_PORT $REMOTE_HOST test -e $REMOTE_DIR ; then
	echo "["$(date '+%Y-%m-%d %H:%M:%S')"]" $REMOTE_HOST":"$REMOTE_DIR" EXIST";
    else
	echo "["$(date '+%Y-%m-%d %H:%M:%S')"]" $REMOTE_HOST":"$REMOTE_DIR" NOT_EXIST";
	exit 1;
    fi
}


if [ "$1" = "lib" ]; then
    rm -rf ./lib/*    
    mvn clean dependency:copy-dependencies -DoutputDirectory=lib
elif [ "$1" = "start" ]; then
    java -Xmx512M -cp "./target/classes:./lib/*" regis.DahuaApplication
    ##<&- 1>/dev/null 2>&1 &     
elif [ "$1" = "test" ]; then
    mvn clean compile test-compile jar:jar -Ppro -Dmaven.test.skip=true    
    REMOTE_DIR="~/Test_DPSDK_Java_win64"
    rsync -aPv --delete lib/ lucas@192.168.249.48:$REMOTE_DIR/lib
    rsync target/dahua_sdk_x64.jar lucas@192.168.249.48:$REMOTE_DIR/
else
    mvn eclipse:clean eclipse:eclipse -DdownloadSources=true
    #mvn eclipse:clean eclipse:eclipse
    #mvn clean compile test-compile    
fi

