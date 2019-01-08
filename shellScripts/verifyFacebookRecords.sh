#!/bin/sh

cd /Users/kumar/Desktop/Coviam/Test/SEARCH/csr_xsearch_api/src/test/resources/FacebookDownloadedFiles/

cat facebook*txt | grep -i $1 | wc -l