#!/bin/sh
##todo应判断当晨的run.pid是否存在,及pid进程是否存在

pidFile="run.pid"
if [ -f "$pidFile" ]
then
   pid=$(cat "$pidFile")
   echo 服务已启动,PID为$pid
else
    (nohup java -jar document-transform-0.0.1-SNAPSHOT.jar)&echo $! >run.pid
fi



