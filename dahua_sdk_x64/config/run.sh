#! /bin/bash

basepath=$(cd `dirname $0`; pwd)
echo $basepath

cd $basepath
cd ..

CURR_PATH=`pwd ./`
echo $CURR_PATH

export LD_LIBRARY_PATH=/usr/lib64/:$CURR_PATH/centos_lib/

echo $LD_LIBRARY_PATH

_startJava() {
    ps axu | grep java | grep -v 'grep' | grep "$CURR_PATH/dahua_sdk_x64.jar" | awk '{print $2}' | xargs -r kill
java -Xmx1024M -cp "$CURR_PATH/dahua_sdk_x64.jar:$CURR_PATH/lib/*" com.main.TestDPSDKMain    
#java -Xmx1024M -cp "$CURR_PATH/dahua_sdk_x64.jar:$CURR_PATH/lib/*" com.main.TestDPSDKMain <&- 1>/dev/null 2>&1 &
}

if [ "$1" = "start" ]; then
    _startJava
elif [ "$1" = "stop" ]; then
    cat $CURR_PATH/run.pid | xargs -r kill 
else
    _startJava
fi


