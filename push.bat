cd ..
cd "rojar"
if exist pasta goto temPasta
mkdir pasta
goto fim
:temPasta
cd pasta
if exist "bot.jar" del "bot.jar"
if exist ".env" del ".env"
cd ..
rmdir pasta
:fim
mkdir pasta
cd ..
cd "Nova pasta"
cd build
cd libs
if exist pasta goto temPasta2
mkdir pasta
goto fim2
:temPasta2
cd pasta
if exist "bot.jar" del "bot.jar"
if exist ".env" del ".env"
cd ..
rmdir pasta
:fim2
mkdir pasta
copy "rode-1.0-all.jar" "pasta/rode.jar"
copy "../../.env" "pasta/.env"
copy "pasta" "../../../rojar/pasta"
cd ..
cd ..
cd ..
cd "rojar"
git add .
git commit -m "tome um push loucão"
git push heroku master
cd ..
cd "Nova pasta"