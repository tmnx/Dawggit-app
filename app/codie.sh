#!/bin/bash
echo "Finding duplicate files with suffixes 2, 3, 4"
find . -name "* 2*"
find . -name "* 3*"
find . -name "* 4*"

echo "Enter y to delete"
read answer

if [ $answer == "y" ]
then
	find . -name "* 2*" -delete
	find . -name "* 3*" -delete
	find . -name "* 4*" -delete
fi
echo "Deleted"
