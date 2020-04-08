# ProteGo

[Informacje o założeniach projektu](https://github.com/ProteGO-app/specs)

[Projekt GH do śledzenia zadań](https://github.com/orgs/ProteGO-app/projects/1)

## Dodawanie nowych tekstów do aplikacji

Teksty do aplikacji są [generowane skryptem](https://github.com/jakublipinski/export-gsheet-to-app-resources) 
na [podstawie arkusza](https://docs.google.com/spreadsheets/d/1vpJiu2jJcxBFWefIyi__QZZH6nwsIVktPFHEohJNMsQ/edit#gid=0).

Jeśli w ramach PR chcesz dodać nowy tekst do aplikacji, dodaj go do strings.xml i zostaw przy nim TODO - co jakiś czas nastąpi zbiorcze przeniesienie i aktualizacja.
Pliki Main.xml są wygenerowane, w związku z czym jakiekolwiek zmiany w nich zostaną nadpisane przy kolejnej aktualizacji na podstawie arkusza.

