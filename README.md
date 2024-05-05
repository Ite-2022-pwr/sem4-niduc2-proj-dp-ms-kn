NIDUC – PROJEKT
Temat: Transmisja w systemie FEC (Forward Error Correction)

Harmonogram pracy nad projektem:
0.	Temat + harmonogram – 19.03 (zrobione)
1.	Wstępne założenia + generator liczb pseudolosowych – 19.03 (zrobione):
W sprawozdaniu powinniśmy dopisać, że rozkład dla generatora jest normalny i uzasadnić to obliczeniami.
2.	Postęp pracy (gotowy symulator) – 09.04
3.	Podsumowanie badań – 23.04
4.	Sprawozdanie + kod – 07.05

Ogólny opis: Projekt implementuje kanał komunikacyjny (modele BSC i Gilberta-Elliotta) oraz system transmisji FEC z różnymi kodami korekcyjnymi (np. powielanie bitów, BCH, RS, LDPC, turbo, fontannowe). Symulacyjnie bada skuteczność transmisji dla różnych parametrów kanału (BER, błędy niezależne, błędy grupowe) i parametrów systemu transmisji (czyli typ kanału komunikacyjnego i typ kodowania FEC). 

Cel projektu: Program za zadanie ma implementację danych kanałów komunikacyjnych oraz systemów transmisji FEC z danymi kodami korekcyjnymi. Celem jest symulacja skuteczności transmisji danych dla różnych parametrów kanału oraz analiza wpływu różnych parametrów systemu transmisji na skuteczność FEC.

Definicja systemu FEC: System FEC to technika używana w komunikacji cyfrowej, która umożliwia odbiorcy wykrycie i korekcję błędów w przesyłaniu danych bez konieczności żądania ponownej transmisji. W systemie FEC nadawca dodaje nadmiarowe bity (nazywane kodami korekcyjnymi) do przesyłania danych, które zawierają dodatkowe informacje umożliwiające odbiorcy wykrycie i naprawę błędów. Zatem w przypadku transmisji błędnych odbiorca może wykorzystać dodatkowe informacje zawarte w kodzie korekcyjnym, aby samodzielnie naprawić błędy. 

Definicja kanału BSC: Kanał BSC (Binary Symmetrical Channel) to jedno z najprostszych i najbardziej podstawowych matematycznych modeli kanału komunikacyjnego, w którym każdy bit przesyłany przez kanał może zostać przekłamany z pewnym ustalonym prawdopodobieństwem p. Oznacza to, że błędy mogą występować niezależnie dla każdego bitu danych, a prawdopodobieństwo przekłamania bitu wynosi p. Zarówno poprawne, jak i przekłamane bity są transmitowane przez kanał bez zmiany. 

Definicja kanału Gilberta-Elliotta: Kanał Gilberta-Elliotta to model kanału komunikacyjnego, który uwzględnia zmienność charakterystyki kanału w czasie. Jest on bardziej zaawansowany od kanału BSC. W kanale Gilberta-Elliotta występują dwa stany – „dobry” i „zły”, które są przełączane między sobą w czasie zgodnie z określonymi prawdopodobieństwami przejścia. W stanie „dobrym” kanał charakteryzuje się niskim poziomem zakłóceń i rzadkim występowaniem błędów transmisji, a w „złym” zakłócenia są częste. 

Typy kodów korekcyjnych używane w projekcie:
•	Powielanie bitów – polega na prostym mechanizmie, w którym każdy bit jest kopiowany określoną liczę razy, jest to najprostsza forma kodowania bitowego, ale jest mało efektywna w wykorzystanie przestrzeni bitowej
•	BCH (Bose-Chaudguri-Hocquenghem) – jest oparty na kalkulacji wielomianów nad ciałem skończonym, ma wysoką zdolność korekcyjną
•	RS (Reed-Solomon) - jest oparty na kalkulacji wielomianów nad ciałem skończonym, ma wysoką zdolność korekcyjną
•	LDPC (Low-Density Parity-Check) – oparte na grafach, gdzie bity informacyjne są połączone z bitami parzystości w sposób, który tworzy rzadką macierz parzystości, jest wysoko wydajny blisko granicy pojemności kanału
•	Turbo kody – składają się z dwóch lub więcej kodów konwolucyjnych, połączonych z interwejwerem, osiągają dobre wyniki korekcji błędów
•	Kody fontannowe – wykorzystują technikę, w której nadajnik wysyła sygnały do odbiorcy, aż ten ostatni uzyska wystarczającą ilość informacji do odtworzenia oryginalnej wiadomości, charakteryzują się nieograniczonym czasem kodowania

Typy parametrów kanału używane w projekcie:
•	BER (Bit Error Rate) – współczynnik błędów bitowych, określa prawdopodobieństwo, że pojedynczy bit przesłanej informacji zostanie przekłamany podczas transmisji
•	Błędy niezależne – pojawiają się losowo i nie są wzajemnie skorelowane, zatem pojawienie się jednego błędu nie wpływa na pojawienie się kolejnych błędów
•	Błędy zależne – pojawiają się w określonych grupach lub sekwencjach bitów, mogą być wynikiem konkretnych warunków transmisyjnych, zakłóceń lub interferencji



Plan projektu:
1.	Stworzenie generatora liczb losowych (zrobione).
2.	Implementacja modeli kanałów BSC oraz Gilberta-Elliotta.
3.	Implementacja systemu FEC z wybranymi kodami korekcyjnymi (np. powielanie bitów, BCH, RS, LDPC, turbo, fontannowe).
4.	Symulacja transmisji danych przez modele kanałów komunikacyjnych z użyciem FEC.
5.	Symulacja transmisji danych dla różnych parametrów kanału (BER, błędy niezależne, błędy grupowe).
6.	Analiza wyników symulacji i ocena skuteczności transmisji dla różnych parametrów systemu transmisji.
7.	Umieszczenie opisu implementacji, wyników symulacji i wniosków w sprawozdaniu.



Gosia:
- BSC
- turbo kody
- kody fontannowe

Krysia:
- Gilbert-Elliott
- RS
- LDPC

Dominik:
- powielanie bitów
- BCH
- sprawdzanie innych ;P

