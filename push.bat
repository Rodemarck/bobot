@echo off
cd build/libs
if exist libs.zip (
  del libs.zip
)
7z a -tzip libs.zip rode-all.jar ./../../.env ./../../imgs
cd ../..
if exist libs.zip (
  del libs.zip
)
xcopy /s .\build\libs\libs.zip .\
