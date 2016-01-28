Two-player Grid Games
=====================

Grid-type games, with a square grid and two sides, implemented using a Java EE 7
server and WebSockets.

Games currently included are:

* Tic-Tac-Toe
* Gomoku (five-in-a-row)

The framework features a rudimentary lobby chat, allowing people to login and
challenge each others to a game of chosen variant.

The server backend is Java EE 7 on Java 8, and the front-end is HTML 5 with Bootstrap 3.3 and jQuery 1.11.

Requirements
------------

* JDK 1.8
* Java EE 7 compliant application server (developed against GlassFish 4.1)
* Modern web browser with JavaScript enabled

Wish-list
---------

* Score tracking
* Persistence; current implementation is transient for easier deployment
* More games; reversi, go
* Standard/free-style variations to gomoku