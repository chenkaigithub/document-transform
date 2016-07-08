#!/bin/sh
pid=$(cat run.pid)
kill -9 $pid
rm -rf run.pid