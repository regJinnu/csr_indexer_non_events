#!/bin/sh

cd /Users/kumar/Desktop/FacebookDownloadedFiles

cat facebook*txt | grep -i $1 | wc -l