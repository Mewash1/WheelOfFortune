# PAP22Z Z26

# Skład zespołu:
- Miłosz Mizak
- Milan Wróblewski
- Bartłomiej Pełka
- Wiktor Topolski

# Proponowany temat projektu:
## Gra Koło Fortuny
Gra oparta na popularnym teleturnieju o tej samej nazwie
- każdy odcinek teleturnieju składa się z pięciu rund podstawowych oraz finału - w każdej rundzie konkuruje ze sobą trójka graczy
- rundy 1, 3 i 5 polegają na grze w wisielca - ruch gracza składa się z zakręcenia tytułowym kołem fortuny, które losuje nagrodę punktową za każdą poprawnie odgadniętą w danym ruchu spółgłoskę. Jeżeli gracz poda literę która występuje w haśle, to otrzymuje punkty za ilość odgadniętych w ruchu liter i może ruszyć się ponownie. Na kole znajdują się również takie pola jak "bankrut", które kończy turę gracza i wyzerowuje jego punkty zdobyte w danej rundzie, "stop" które po prostu kończy turę gracza, oraz "graj dalej", które każe zakręcić graczowi ponownie. Gracz może zakupić za zdobyte punkty dowolną samogłoskę aby ułatwić sobie zgadnięcie hasła. Rundę wygrywa gracz, który zgadnie hasło, i jako jedyny dopisuje on sobie do puli zdobytych punktów wynik z danej rundy.
- runda 2 rozgrywana jest inaczej - na początek losowana jest nagroda za każdą podaną poprawnie literę, potem każdy gracz ma trzy sekundy na podanie dowolnej litery, jeśli poda poprawną to dostaje punkty. W ramach swojego ruchu gracz może również spróbować odgadnąć całe hasło, pod warunkiem że zgadł poprawną literę, wygrywa wtedy również całą rundę. Dopisanie punktów odbywa się tak jak w "zwyczajnej" rundzie
- runda 4 ma jeszcze inny format - ta balicy pojawia się puste hasło, i co sekundę pojawia się kolejna losowa litera - każdy może w dowolnym momencie spróbować odgadnąć całe hasło, za co zdobywa 1000 punktów.
- runda finałowa działa zupełnie inaczej - gracz, który wygrał część główną (czyli zdobył najwięcej punktów z pięciu pierwszych rund) najpierw losuje kopertę z nagrodą którą może zdobyć za wygranie finału. W haśle do zgadnięcia uzupełniane są litery R, S, T, L, N, oraz samogłoska E, gracz może podać trzy spółgłoski i jedną samogłoskę które również mają zostać odsłonięte. Następnie ma 10 sekund na odgadnięcie hasła, jeżeli mu się uda zdobywa wylosowaną przed rozpoczęciem finału nagrodę.

Na tej podstawie proponujemy jako temat naszego projektu napisanie gry inspirowanej tym teleturniejem
- gra miałaby przede wszystkim możliwość rozegrania całego "odcinka" przeciwko botom
  - przed rozpoczęciem gry możliwe będzie wybranie poziomu umiejętności botów, kategorie haseł do zgadywania oraz poziom ich trudności
  - wyniki gier będą zapisywane w lokalnej bazie danych, zapisywany będzie nick gracza, wynik punktowy oraz kategoria
- drugim dostępnym trybem gry byłaby lokalna gra wieloosobowa
  - trójka graczy grałaby przy jednym komputerze, zmieniając się co turę
  - realizacja rundy czwartej może działać tak, że każdy gracz ma określony klawisz który zatrzymuje grę i pozwala im zgadnąć hasło w danym momencie
- jako ostatni tryb (funkcjonalność dodatkowa) proponujemy grę wieloosobową przez internet
  - w ramach tego trybu gry gracze łączyliby się z serwerem gry który przydzielałby graczy do gry i zarządzał grami toczącymi się
  - wyniki osiągnięte w grze przez internet byłyby automatycznie zapisywane w ogólnodostępnej bazie danych rekordów graczy
- serwer główny gry poza grą przez internet umożliwiałby
  - wrzucanie swoich rekordów do tabel online
  - pobieranie list rekordów najlepszych graczy
  - aktualizowanie bazy danych słów
- interfejs aplikacji wykonany zostanie w Java Swing