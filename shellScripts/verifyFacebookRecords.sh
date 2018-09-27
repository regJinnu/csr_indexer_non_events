#!/bin/sh

cd /tmp/FacebookDownloadedFiles/

cat facebook*txt | grep -i $1 | wc -l