# PAP22Z Z26

# Skład zespołu:
- Miłosz Mizak
- Milan Wróblewski
- Bartłomiej Pełka
- Wiktor Topolski

# Temat projektu
## Gra "Koło Fortuny"

# Instrukcja instalacji

Do instalacji wymagana jest Java w wersji 16 lub nowszej oraz Gradle w wersji 7.1 lub nowszej. Skopiowanie i uruchomienie poniższych komend zainstaluje aplikację pod systemem Linux:
```shell
git clone https://gitlab-stud.elka.pw.edu.pl/wtopolsk/PAP22Z-Z26
cd PAP22Z-Z26
gradle build
cp build/libs/WheelOfFortune-1.0-RC-all.jar WheelOfFortune.jar
```
Aby instrukcja zadziałała pod kontrolą systemu Windows należy zamienić ostatnią komendę na `copy` i wszystkie slashe w jej treści na backslashe.  
Można również zamiast kopiować te komendy po prostu uruchomić skrypt `install.bat` (Windows) / `install.sh` (Linux/Mac).
Po wykonaniu tych kroków w katalogu z projektem pojawi się plik `WheelOfFortune.jar` który można uruchomić za pomocą komendy
`java -jar WheelOfFortune.jar`. Baza danych dostarczana jest razem ze wszystkimi plikami projektu.
Możliwe jest uruchomienie samodzielnego serwera gry, należy w tym celu uruchomić program z argumentem `server`:
```shell
java -jar WheelOfFortune.jar server -port 54321
```
Powyższa komenda uruchomi serwer bez uruchamiania samej gry który będzie nasłuchiwał na porcie 54321. Zalecane jest wykonanie kopii bazy danych oraz jara z programem i umieszczenie go w innej lokalizacji niż samej instalacji gry aby uniknąć kolizji przy zapisie/odczycie z bazy danych w przypadku gry na hostowanym przez siebie serwerze.

# Inne informacje

## Etap 2

Dokumentacja wykonanych prac znajduje się w pliku `PAP22Z-Z26_etap_2.pdf`.  
Film prezentujący działanie prototypu znajduje się [tutaj](https://youtu.be/BBDL65iB4lw).

## Etap 3

Dokumentacja wykonanych prac znajduje się w pliku `PAP22Z-Z26_etap_3.pdf`.