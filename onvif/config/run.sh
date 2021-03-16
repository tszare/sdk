#! /bin/bash

basepath=$(cd `dirname $0`; pwd)
echo $basepath
cd $basepath
cd ..

CURR_PATH=`pwd ./`
echo $CURR_PATH

_startJava() {
    ps axu | grep java | grep -v 'grep' | grep "$CURR_PATH/client_gsms.jar" | awk '{print $2}' | xargs -r kill
    java -Xms512M -Xmx1024M -Duser.timezone=GMT+08 -cp "$CURR_PATH/client_gsms.jar:$CURR_PATH/lib/*" regis.external.application.* <&- 1>/dev/null 2>&1 &
}

if [ "$1" = "start" ]; then
    _startJava
elif [ "$1" = "stop" ]; then
    cat $CURR_PATH/run.pid | xargs -r kill ; 
    #ps axu | grep ffmpeg | grep -v 'grep' | awk '{print $2}' | xargs kill -9
    #ps axu | grep ffmpeg | grep -v 'grep' | awk '{print $2}' | xargs kill -9
else
    _startJava
fi

