# MTCG SWEN Projekt
# Anforderungen
Dieser HTTP/REST-basierte Server soll als Plattform zum Handeln und Kämpfen mit Karten dienen.
- Ein Benutzer ist ein registrierter Spieler mit Anmeldeinformationen (eindeutiger Benutzername, Passwort).
- Ein Benutzer kann seine Karten verwalten.
- Eine Karte besteht aus: einem Namen und mehreren Attributen (Schaden, Elementtyp).
- Eine Karte ist entweder eine Zauber- oder eine Monsterkarte.
- Der Schaden einer Karte ist konstant und ändert sich nicht.
- Ein Benutzer hat mehrere Karten in seinem Stapel.
- Ein Stapel ist die Sammlung aller seiner aktuellen Karten (Hinweis: Karten können durch Handel entfernt werden).
- Ein Benutzer kann Karten kaufen, indem er Pakete erwirbt.
- Ein Paket besteht aus 5 Karten und kann vom Server durch Zahlung von 5 virtuellen Münzen erworben werden.
- Jeder Benutzer hat 20 Münzen zum Kauf von (4) Paketen.
- Die besten 4 Karten werden vom Benutzer ausgewählt, um im Deck verwendet zu werden.
- Das Deck wird in den Kämpfen gegen andere Spieler verwendet.
- Ein Kampf ist eine Anfrage an den Server, um mit dem derzeit definierten Deck gegen einen anderen Benutzer anzutreten.

# Design
![Architecture](assets/Architektur_Swen.jpg)
# Lessons Learned
Einige wichtige Aspekte, die ich im Laufe dieses Projektes gelernt habe, sind:
- Code immer in kleinen Paketen zu testen 
- Payloads von Request immer ausgeben, um zu überprüfen, ob Daten im richtigen Format sind, bzw. richtig dekodiert und formatiert wurden
- Mit Debuggen können viele Fehler schnell behoben werden

Beim BattleService hatte ich Schwierigkeiten damit, dass die User im Curl-Skript, die Lobby fast gleichzeitig betreten. Dadurch kam es oft dazu, dass zwei separate Lobbys erstellt wurden und das Spiel somit nicht gestartet hat. Dies wurde durch einen synchronized-Block gelöst. Dabei wird ein gewisser Codeabschnitt von einem Thread gesperrt, solange bis er von diesem wieder freigegeben wird. Dadurch konnte der Bereich, in dem die Lobby erstellt wurde, geschützt werden. 

# Unit Tests
Ich habe meine Unit Tests ausschließlich an das „.yaml“ Dokument angelehnt und sie dementsprechend konzipiert. Es gibt jeweils einen Test für jeden HTTP-Response-Code. 

# Unique Feature
Ich hab mich dafür entschieden als zustäzliches Feature eine DELETE Funktionalität beim User-Service zu implementieren.
Dabei ist wichtig das nur der Admin User löschen kann, sonst wird ein HTTP-Forbidden Code zurückgeschickt. Der Admin muss jediglich die User-Id im Body übergeben, um einen User zu löschen.
Wird der User mit entsprechender uid nicht gefunden bzw existiert nicht, so wird ein NOT-FOUND retourniert.

Es wurden zusätzliche Tests implementiert und die Funktionaliät zu testen. 

# Tracked Time 
| Datum       | Zeit[h] | Kommentar                              |
|-------------|---------|----------------------------------------|
| 20.10.22    | 3       | Docker Installation & PostgreSQL Image |
| 21.10.22    | 4       | Datenbank entwurf erstellen            |
| 25.10.22    | 1       | Github Repository erstellt             |
| 01-30.10.22 | 15      | Implementierung Http-Server            |
| 27.10.22    | 2       | Datenbankverbindung                    |
| 28.10.22    | 5       | Implementierung User-Service           |
| 29.10.22    | 4       | Impl. Session Service                  |
| 30.10.22    | 2       | Tests für Services                     |
| 16.11.22    | 5       | Unit of Work                           |
| 17.11.22    | 2       | DB Singleton & Properties              |
| 20.11.22    | 6       | Package Service                        |
| 21.11.22    | 4       | Transactions Controller                |
| 26.11.22    | 4       | Card Service                           |
| 29.11.22    | 6       | Deck Service                           |
| 30.11.22    | 8       | Stats & Trading Service                |
| 01.12.22    | 4       | Trading Service fertigstellen          |
| 02-03.12.22 | 9       | Battle Service                         |
| 08-09.12.22 | 10      | Bug Fixing                             |
| Gesamt      | 94      |                                        |
