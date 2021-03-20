@echo off
cd build/libs
if exist libs.zip (
  del libs.zip
)
7z a -tzip libs.zip rode-all.jar ./../../.env
cd ../..