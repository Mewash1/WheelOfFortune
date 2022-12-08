# PAP22Z Z26

# Skład zespołu:
- Miłosz Mizak
- Milan Wróblewski
- Bartłomiej Pełka
- Wiktor Topolski

# Temat projektu
## Gra "Koło Fortuny"

# Instrukcja instalacji

Do instalacji wymagana jest Java w wersji 17 lub nowszej oraz Gradle. Skopiowanie i uruchomienie poniższych komend zainstaluje aplikację pod systemem Linux:
```shell
git clone https://gitlab-stud.elka.pw.edu.pl/wtopolsk/PAP22Z-Z26
cd PAP22Z-Z26
gradle build
cp build/libs/WheelOfFortune-1.0-PROTOTYPE-all.jar WheelOfFortune.jar
```
Aby instrukcja zadziałała pod kontrolą systemu Windows należy zamienić ostatnią komendę na `copy` i wszystkie slashe w jej treści na backslashe.  
Można również zamiast kopiować te komendy po prostu uruchomić skrypt `install.bat` (Windows) / `install.sh` (Linux/Mac).
Po wykonaniu tych kroków w katalogu z projektem pojawi się plik `WheelOfFortune.jar` który można uruchomić za pomocą komendy
`java -jar WheelOfFortune.jar`. Baza danych pojawi się przy pierwszym uruchomieniu.

# Inne informacje

Dokumentacja wykonanych prac znajduje się w pliku `PAP22Z-Z26_etap_2.pdf`.  
Film prezentujący działanie aplikacji znajduje się [tutaj]{https://youtu.be/BBDL65iB4lw}.

