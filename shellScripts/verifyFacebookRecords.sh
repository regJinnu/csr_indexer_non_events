#!/bin/sh

cd $1

cat facebook*txt | grep -i $2 | wc -l