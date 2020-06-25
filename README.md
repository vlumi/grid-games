# Two-player Grid Games

Grid-type games, with a square grid and two sides, implemented using a Jakarta EE 8 server and WebSockets.

Games currently included are:

* Tic-Tac-Toe
* Gomoku (five-in-a-row)

The framework features a rudimentary lobby chat, allowing people to login and challenge each others to a game of chosen variant.

The server backend is Jakarta EE 8 on Java 14, and the front-end is HTML 5 with Bootstrap 4.5.0 and jQuery 3.5.1.

## Requirements

* JDK 14+
* Jakarta EE 8 compliant application server (tested on Payara Micro 5)
* Modern web browser with JavaScript enabled

## Running instructions

* Build the project: `mvn package`
* Deploy the created `target/grid-games-1.1.1.war` to the application server
  * E.g. run with Payara Micro as: `java -jar /path/to/payara-micro-5.2020.2.jar target/grid-games-1.1.1.war`
* Connect to the service on the browser at e.g. http://localhost:8080/grid
  * The default context path `/grid` is pre-configured for Glassfish, Payara, and JBoss

## Wish-list

* Score tracking
* Persistence; current implementation is transient for easier deployment
* More games; reversi, go
* Standard/free-style variations to gomoku

## Version History

* v1.1: Updated dependencies to latest versions
  * Java 14
  * Jakarta EE 8
  * jQuery 3.5.1
  * Bootstrap 4.5.0
* v1.0: Initial version, based on Java 8, Java EE 7
