@echo off
cd build
cd libs
if exist libs.zip del libs.zip
if exist "resources" del "resources"
mkdir "resources"
copy ./../../src/main/resources/messages_en_US.properties resources/messages_en_US.properties
copy ./../../src/main/resources/messages_pr_BR.properties resources/messages_pt_BR.properties
7z a -tzip libs.zip rode-all.jar ./../../.env ./resources
del "resources"
cd..
cd..