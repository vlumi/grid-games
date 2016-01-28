(function ($) {
    var
            init,
            player,
            lobby,
            game,
            websocket,
            util;
    $(document).ready(function () {
        if ("WebSocket" in window) {
            init();
        } else {
            util.showMessage("Error", "Websocket is not supported by your browser!");
        }
    });
    /**
     * Initialize the application.
     */
    init = function () {
        if ('ontouchstart' in document) {
            $("#gameArea").removeClass("noTouch");
        }
        player.init();
        lobby.init();
        game.init();
    };
    /**
     * Player session and authentication.
     */
    player = (function () {
        var self = {
            session: {
                name: ""
            },
            getDefaultUrlFromLocation: function () {
                var
                        pageUrl = window.location.href,
                        parts = pageUrl.match(/^([a-z]+):\/\/(.+)(\/index\.html)?$/),
                        protocol = "",
                        defaultUrl;
                if (parts.length < 2) {
                    return;
                }
                switch (parts[1]) {
                    case "http":
                        protocol = "ws";
                        break;
                    case "https":
                        protocol = "wss";
                        break;
                    default:
                        // Unsupported URL, maybe running off local disk?
                        return "";
                }
                defaultUrl = protocol + "://" + parts[2];
                if (/[^\/]$/.test(defaultUrl)) {
                    defaultUrl += "/";
                }
                defaultUrl += "game";
                return defaultUrl;
            },
            setDefaultUrl: function () {
                var defaultUrl = "";
                $.ajax({
                    dataType: "json",
                    url: "config.json",
                    async: false,
                    success: function (data) {
                        defaultUrl = data.server;
                    }
                });
                if (typeof defaultUrl === "string" && defaultUrl !== "") {
                    // Use configured address.
                    $("#urlGroup").hide();
                } else {
                    defaultUrl = self.getDefaultUrlFromLocation();
                }
                $("#url").val(defaultUrl);
            },
            enableLogin: function () {
                $("#loginModal").modal("show");
            },
            disableLogin: function () {
                $("#loginModal").modal("hide");
            },
            login: function () {
                var
                        url = $("#url").val(),
                        name = $("#name").val(),
                        password = $("#password").val();
                var callback = function () {
                    websocket.send("player", {
                        "type": "login",
                        "name": name,
                        "password": password
                    });
                };
                websocket.connect(url, callback);
            },
            logout: function () {
                self.session.name = "";
                if (websocket.isConnected()) {
                    websocket.disconnect();
                }
                self.enableLogin();
            },
            handler: {
                data: {
                    login: function (data) {
                        self.disableLogin();
                        $("#name").val(data.name);
                        self.session.name = data.name;
                        $("#loginModal").modal("hide");
                    }
                }
            }
        };
        return {
            /**
             * Current session information.
             */
            session: self.session,
            /**
             * Initialize player.
             */
            init: function () {
                websocket.registerHandler("data", "player", function (data) {
                    if (data.type in self.handler.data) {
                        self.handler.data[data.type](data);
                    }
                });
                websocket.registerHandler("close", "player", function () {
                    self.logout();
                });
                websocket.registerHandler("error", "player", function (message) {
                    // TODO: nicer message
                    util.showMessage("Error", "Login failed: " + message.message);
                    self.enableLogin();
                });
                $(document)
                        .on("click.login", "#login", function () {
                            self.login();
                            return false;
                        })
                        .on("click.logout", "#logout", function () {
                            self.logout();
                            return false;
                        })
                        .on("keypress", "#url, #name, #password", function (event) {
                            switch (event.keyCode) {
                                case 13:
                                    self.login();
                                    return false;
                            }
                        })
                        .on("hide.bs.modal", "#loginModal", function (event) {
                            if (self.session.name === "") {
                                return false;
                            }
                        });
                self.setDefaultUrl();
                $("#loginModal").modal({keyboard: false});
            }
        };
    })();
    /**
     * Chat and status message window, as well as player list.
     */
    lobby = (function () {
        var self = {
            memberList: [],
            memberStatuses: {},
            ui: {
                getTimestamp: function () {
                    var d = new Date(),
                            hourStr = d.getHours() < 10 ? "0" + d.getHours() : d.getHours(),
                            minuteStr = d.getMinutes() < 10 ? "0" + d.getMinutes() : d.getMinutes(),
                            secondStr = d.getSeconds() < 10 ? "0" + d.getSeconds() : d.getSeconds(),
                            timestamp = hourStr + ":" + minuteStr + ":" + secondStr;
                    return timestamp;
                },
                updateMemberList: function () {
                    var
                            $memberList = $("#memberList"),
                            selected = $("#memberList :selected").val();
                    $memberList.find("option[value!='']").remove();
                    $.each(self.memberList, function (index, name) {
                        var
                                $element,
                                status;

                        if (name in self.memberStatuses) {
                            status = self.memberStatuses[name];
                        } else {
                            status = self.memberStatuses[name] = "free";
                        }

                        $element = $("<option>")
                                .attr("val", name)
                                .text(name)
                                .attr("data-status", status)
                                .appendTo($memberList);
                        if (name === player.session.name) {
                            $element.prop("disabled", true);
                        } else if (name === selected) {
                            $element.prop("selected", true);
                        }
                    });
                    game.updateOpponentList();
                },
                sendMessage: function () {
                    var
                            message,
                            to = $("#memberList").val();
                    message = {
                        type: "chatMessage",
                        message: $("#lobbyMessage").val(),
                        to: to,
                        private: false
                    };
                    if ($("#lobbyPrivateMessage").prop("checked")) {
                        message.private = true;
                    }
                    if (typeof message.message === "string" && message.message.length > 0) {
                        websocket.send("lobby", message);
                    }
                    $("#lobbyMessage").val("");
                },
                addMessage: function (from, message, isFromSelf, isPrivate) {
                    var $entry, $timestamp, $player, $message;
                    $entry = $("<div>");
                    $timestamp = $("<span>")
                            .addClass("timestamp")
                            .text(self.ui.getTimestamp())
                            .appendTo($entry);
                    $player = $("<span>")
                            .addClass("player")
                            .text(from)
                            .attr("title", from)
                            .appendTo($entry);
                    $message = $("<span>")
                            .addClass("message")
                            .text(message)
                            .attr("title", message)
                            .appendTo($entry);
                    if (isPrivate) {
                        $entry.addClass("private");
                    }
                    if (isFromSelf) {
                        $entry.addClass("fromSelf");
                    }

                    $("#lobby").prepend($entry);
                },
                addStatusMessage: function (message) {
                    self.ui.addMessage("***", message, false, false)
                }
            },
            populateMemberList: function (members) {
                self.memberList = [];
                $.each(members.sort(util.caseInsensitiveSort), function (index, member) {
                    if ($.inArray(member, self.memberList) === -1) {
                        self.memberList.push(member);
                    }
                });
                self.ui.updateMemberList();
            },
            addMemberToList: function (name) {
                self.memberList.push(name);
                self.populateMemberList(self.memberList);
            },
            removeMemberFromList: function (name) {
                var index = self.memberList.indexOf(name);
                if (index >= 0) {
                    self.memberList.splice(index, 1);
                    self.populateMemberList(self.memberList);
                }
            },
            markMembersBusy: function (names) {
                $.each(names, function (index, name) {
                    self.memberStatuses[name] = "busy";
                });
                self.populateMemberList(self.memberList);
            },
            markMemberBusy: function (name) {
                self.memberStatuses[name] = "busy";
                self.populateMemberList(self.memberList);
            },
            markMemberFree: function (name) {
                self.memberStatuses[name] = "free";
                self.populateMemberList(self.memberList);
            },
            clearMemberList: function () {
                self.memberList = [];
                self.populateMemberList(self.memberList);
            },
            handler: {
                data: {
                    init: function (data) {
                        if (self.active) {
                            return;
                        }
                        self.ui.addStatusMessage("Welcome, " + player.session.name + "!");
                        self.populateMemberList(data.members);
                        self.markMembersBusy(data.busyMembers);
                        $("#mainArea").addClass("active");
                    },
                    join: function (data) {
                        if (data.name !== player.session.name) {
                            self.ui.addStatusMessage(data.name + " has joined.");
                            self.addMemberToList(data.name);
                            self.markMemberFree(data.name);
                        }
                    },
                    part: function (data) {
                        self.ui.addStatusMessage(data.name + " has left.");
                        self.removeMemberFromList(data.name);
                    },
                    chatMessage: function (data) {
                        var message, isFromSelf, isPrivate;
                        if (data.from === undefined) {
                            // System message
                            self.ui.addStatusMessage(data.message);
                        } else {
                            if (data.to !== undefined) {
                                // Targeted message
                                message = data.to + "> " + data.message;
                                if (data.private) {
                                    isPrivate = true;
                                }
                            } else {
                                // Public message
                                message = data.message;
                            }
                            if (data.from === player.session.name) {
                                isFromSelf = true;
                            }
                            self.ui.addMessage(data.from, message, isFromSelf, isPrivate);
                        }
                    },
                    status: function (data) {
                        if (data.name !== player.session.name) {
                            self.ui.addStatusMessage(data.name + " is " + data.status + ".");
                            switch (data.status) {
                                case "busy":
                                    self.markMemberBusy(data.name);
                                    break;
                                case "free":
                                    self.markMemberFree(data.name);
                                    break;
                            }
                        }

                    }
                }
            }

        };
        return {
            /**
             * Initialize lobby.
             */
            init: function () {
                websocket.registerHandler("data", "lobby", function (data) {
                    if (data.type in self.handler.data) {
                        self.handler.data[data.type](data);
                    }
                });
                websocket.registerHandler("close", "lobby", function () {
                    $("#mainArea").removeClass("active");
                    self.ui.addStatusMessage("Disconnected.");
                    self.clearMemberList();
                    self.active = false;
                });
                $(document)
                        .on("click.member", "#memberList li:not(.self):not(.busy)", function (event) {
                            $("#memberList li.selected").removeClass("selected");
                            $(event.currentTarget).addClass("selected");
                        })
                        .on("click.sendLobbyMessage", "#mainArea.active #sendLobbyMessage", function (event) {
                            self.ui.sendMessage();
                            return false;
                        })
                        .on("keypress", "#lobbyMessage", function (event) {
                            switch (event.keyCode) {
                                case 13:
                                    self.ui.sendMessage();
                                    return false;
                            }
                        });
            }
        };
    })();
    /**
     * Game logic.
     */
    game = (function () {
        var self = {
            gameRunning: false,
            mySide: null,
            challenger: "",
            boardCreator: (function () {
                var
                        createTitleRow = function (length) {
                            var
                                    $row = $("<tr>"),
                                    column;
                            for (column = 0; column < length; column++) {
                                $("<th>")
                                        .clone()
                                        .text(String.fromCharCode(column + 65))
                                        .appendTo($row);
                            }
                            $("<th>")
                                    .appendTo($row)
                                    .clone()
                                    .prependTo($row);
                            return $row;
                        },
                        createGridRow = function ($row, row, length) {
                            var
                                    $cellCopy,
                                    $piece = $("<div>"),
                                    column,
                                    cellClass;
                            for (column = 0; column < length; column++) {
                                if (column === 0) {
                                    cellClass = "left";
                                } else if (column === length - 1) {
                                    cellClass = "right";
                                } else {
                                    cellClass = "center";
                                }

                                $cellCopy = $("<td>")
                                        .addClass(cellClass)
                                        .appendTo($row);
                                $piece
                                        .clone()
                                        .prop("id", "pos_" + row + "_" + column)
                                        .addClass("free")
                                        .appendTo($cellCopy)
                                        .html("&nbsp;");
                            }
                        },
                        createGameBoard = function ($gameArea, columns, rows, cellSize, title) {
                            var
                                    $gameBoard,
                                    $titleRow,
                                    $row,
                                    row,
                                    rowClass;
                            $gameBoard = $("<table>")
                                    .addClass("gameBoard")
                                    .addClass("size" + cellSize)
                                    .appendTo($gameArea);
                            if (title) {
                                $titleRow = createTitleRow(columns)
                                        .appendTo($gameBoard);
                            }
                            for (row = 0; row < rows; row++) {
                                if (row === 0) {
                                    rowClass = "top";
                                } else if (row === rows - 1) {
                                    rowClass = "bottom";
                                } else {
                                    rowClass = "middle";
                                }
                                $row = $("<tr>")
                                        .addClass(rowClass)
                                        .appendTo($gameBoard);
                                createGridRow($row, row, columns);
                                if (title) {
                                    $("<th>")
                                            .text(row + 1)
                                            .prependTo($row)
                                            .clone()
                                            .appendTo($row);
                                }
                            }
                            if (title) {
                                $titleRow
                                        .clone()
                                        .appendTo($gameBoard);
                            }
                        };
                return {
                    gomoku: (function () {
                        return function ($gameArea, opponent) {
                            createGameBoard($gameArea, 19, 19, 30, true);
                        };
                    })(),
                    tictactoe: (function () {
                        return function ($gameArea, opponent) {
                            createGameBoard($gameArea, 3, 3, 128, false);
                        };
                    })()
                };
            })(),
            createGame: function (opponent, variant) {
                var
                        $gameModal = $("#gameModal"),
                        $gameVariant = $("#gameVariant"),
                        $gameOpponent = $("#gameOpponent"),
                        $gameArea = $("#gameArea");

                $gameArea.html("");
                $gameVariant.text(variant);
                $gameOpponent.text(opponent);

                if (variant in self.boardCreator) {
                    self.boardCreator[variant]($gameArea, opponent);
                    $gameModal.modal("show");
                    $("#gameStatus").text("");
                }
            },
            challenge: function (challengee, variant) {
                var
                        $gameModal = $("#gameModal"),
                        $gameVariant = $("#gameVariant"),
                        $gameOpponent = $("#gameOpponent"),
                        $gameStatus = $("#gameStatus"),
                        $gameArea = $("#gameArea"),
                        message;

                $gameStatus.text("Waiting for other player...");
                $gameArea.html("");
                $gameVariant.text(variant);
                $gameOpponent.text(challengee);
                $gameModal.modal("show");

                message = {
                    type: "challenge",
                    to: challengee,
                    from: player.session.name,
                    variant: variant
                };
                websocket.send("game", message);

            },
            acceptChallenge: function () {
                var message = {
                    type: "acceptChallenge",
                    to: self.challenger
                };
                websocket.send("game", message);
                // TODO: set message to lobby
                // TODO: set status on lobby
                self.challenger = "";
            },
            rejectChallenge: function () {
                var message = {
                    type: "rejectChallenge",
                    to: self.challenger
                };
                websocket.send("game", message);
                self.challenger = "";
            },
            placePiece: function (row, column) {
                var message = {
                    type: "placePiece",
                    row: row,
                    column: column
                };
                websocket.send("game", message);
            },
            newGame: function () {
                var message = {
                    type: "newGame"
                };
                websocket.send("game", message);
                $("#gameStatus").text("Waiting for other player...");
            },
            close: function () {
                var message = {
                    type: "leave"
                };
                websocket.send("game", message);
                self.gameRunning = false;
            },
            terminateGame: function () {
                var
                        $gameBoard = $("#gameArea .gameBoard");

                $gameBoard.find(".lastMove").removeClass("lastMove");
                $gameBoard
                        .addClass("gameOver")
                        .removeClass("myTurn");
                self.gameRunning = false;
            },
            handler: {
                data: {
                    state: function (data) {
                        var
                                $gameBoard = $("#gameArea .gameBoard");
                        if ($gameBoard.length > 0) {
                            $gameBoard.remove();
                        }
                        self.createGame(data.opponent, data.variant);
                        $gameBoard = $("#gameArea .gameBoard");
                        $.each(data.moves, function (index, move) {
                            var
                                    side = "side" + move[0],
                                    column = move[1],
                                    row = move[2],
                                    id = "pos_" + row + "_" + column;
                            $("#" + id)
                                    .removeClass("free")
                                    .addClass(side);
                        });
                        self.mySide = data.you;
                        $gameBoard.addClass("side" + self.mySide);
                        if (self.mySide === data.turn) {
                            $gameBoard.addClass("myTurn");
                        }
                        self.gameRunning = true;
                    },
                    challenge: function (data) {
                        self.challenger = data.from;
                        $("#challengeMessageBody").text("Accept challenge from " + self.challenger + " for a game of " + data.variant + "?");
                        $("#challengeModal").modal();
                    },
                    placePiece: function (data) {
                        var
                                $gameBoard = $("#gameArea .gameBoard"),
                                side = "side" + data.side;
                        $gameBoard.find(".lastMove").removeClass("lastMove");
                        $.each(data.moves, function (index, move) {
                            var
                                    column = move[0],
                                    row = move[1],
                                    id = "pos_" + row + "_" + column;
                            $("#" + id)
                                    .removeClass("free")
                                    .addClass(side)
                                    .addClass("lastMove");
                        });
                        $gameBoard.removeClass("myTurn");
                        if (self.mySide === data.turn) {
                            $gameBoard.addClass("myTurn");
                        }
                    },
                    gameOver: function (data) {
                        var
                                $gameBoard = $("#gameArea .gameBoard"),
                                newGameResponse,
                                gameOverMessage = "Game over!";
                        if ("winner" in data && data.winner !== "") {
                            // TODO: to function
                            $.each(data.positions, function (index, position) {
                                var
                                        side = position[0],
                                        column = position[1],
                                        row = position[2],
                                        id = "pos_" + row + "_" + column;
                                $("#" + id)
                                        .addClass("winner");
                            });
                            // TODO: keep track of wins
                            if (data.winner === player.session.name) {
                                gameOverMessage += " You won!";
                            } else {
                                gameOverMessage += " You lost!";
                            }
                        } else {
                            gameOverMessage += " A tie!";
                        }
                        $gameBoard
                                .removeClass("myTurn")
                                .addClass("gameOver");
                        $("#gameOverMessageBody").text(gameOverMessage + " Start a new game?");
                        $("#gameOverModal").modal();
                        // TODO: set status on lobby
                        self.gameRunning = false;
                    },
                    leave: function (data) {
                        if ($("#gameModal").is(":visible")) {
                            $("#gameOverModal").modal("hide");
                            $("#gameStatus").text("The game has ended.");
                            self.terminateGame();
                        } else if ($("#challengeModal").is(":visible")) {
                            $("#challengeModal").modal("hide");
                        }
                    }
                }
            }
        };
        return {
            /**
             * Initialize game logic.
             */
            init: function () {
                var $variantList = $("#variantList");
                websocket.registerHandler("data", "game", function (data) {
                    if (data.type in self.handler.data) {
                        self.handler.data[data.type](data);
                    }
                });
                $.each(self.boardCreator, function (variant) {
                    $("<option>")
                            .val(variant)
                            .text(variant)
                            .appendTo($variantList);
                });
                $(document)
                        .on("click", "#startGameButton", function (event) {
                            $("#startGameModal").modal("show");
                        })
                        .on("click", "#challengeButton", function (event) {
                            var
                                    challengee = $("#opponentList").val(),
                                    variant = $("#variantList").val();
                            if (typeof challengee === "string" && challengee !== "") {
                                self.challenge(challengee, variant);
                            }
                        })
                        .on("click", "#gameArea .gameBoard.myTurn .free", function (event) {
                            var
                                    id = event.currentTarget.id,
                                    idSplit = id.split("_"),
                                    row = parseInt(idSplit[1]),
                                    column = parseInt(idSplit[2]);
                            self.placePiece(row, column);
                        })
                        .on("click", "#challengeYes", function (event) {
                            self.acceptChallenge();
                        })
                        .on("click", "#challengeNo", function (event) {
                            self.rejectChallenge();
                        })
                        .on("click", "#newGameYes", function (event) {
                            self.newGame();
                        })
                        .on("click", "#newGameNo", function (event) {
                            self.close();
                        })
                        .on("click", "#gameCloseButton", function (event) {
                            self.close();
                            $("#gameModal").modal("hide");
                        })
                        .on("hide.bs.modal", "#gameModal", function (event) {
                            if (self.gameRunning) {
                                return false;
                            }
                        });
            },
            onClose: function () {
                self.close();
            },
            updateOpponentList: function () {
                var
                        $opponentList = $("#opponentList"),
                        $opponentListEmpty = $("#opponentListEmpty"),
                        $challengeButton = $("#challengeButton"),
                        $memberList = $("#memberList"),
                        selected = $("#opponentList :selected").val();

                $opponentList.find("option").remove();
                $memberList
                        .find("option[value!=''][data-status=free]:not(:disabled)")
                        .clone()
                        .appendTo($opponentList);

                if (typeof selected === "string" && selected !== "") {
                    $opponentList.find("option[value='" + selected + "']");
                }

                if ($opponentList.find("option").length > 0) {
                    $challengeButton.prop("disabled", false);
                    $opponentList.show();
                    $opponentListEmpty.hide();
                } else {
                    $challengeButton.prop("disabled", true);
                    $opponentList.hide();
                    $opponentListEmpty.show();
                }
            }
        };
    })();
    /**
     * Websocket handling.
     */
    websocket = (function () {
        var self = {
            socket: undefined,
            event: {
                onMessage: function (event) {
                    var message = $.parseJSON(event.data);
                    util.log("Received message: " + event.data);
                    if (message.context in self.handler.message) {
                        self.handler.message[message.context](message);
                    } else if (message.context in self.eventHandlers.data) {
                        self.eventHandlers.data[message.context](message.data);
                    }
                },
                onError: function (event) {
                    util.showMessage("Error", "Could not connect: " + event);
                },
                onClose: function () {
                    $.each(self.eventHandlers.close, function (index, handler) {
                        handler();
                    });
                }
            },
            /**
             * Registered handlers for websocket events. Each
             * handler should be registered through the public
             * registerHandler method.
             *
             * Each event may have one handler for each context,
             * a function with the signature defined below.
             */
            eventHandlers: {
                /**
                 * Normal data, received from the server.
                 *
                 * function(data)
                 */
                data: {},
                /**
                 * Connection close event.
                 *
                 * function()
                 */
                close: {},
                /**
                 * Error received from the server.
                 *
                 * function(message)
                 */
                error: {}
            },
            handler: {
                /**
                 * Handlers accepting the whole received message.
                 */
                message: {
                    error: function (message) {
                        if (message.context in self.eventHandlers.error) {
                            self.eventHandlers.error[message.context](message);
                        } else {
                            util.showMessage("Error", message.text);
                        }
                        // TODO:
                    }
                },
            }
        };
        return {
            connect: function (url, callback) {
                if (typeof self.socket === "object") {
                    self.socket.close();
                }
                self.socket = new WebSocket(url);
                self.socket.onopen = callback;
                self.socket.onmessage = self.event.onMessage;
                self.socket.onerror = self.event.onError;
                self.socket.onclose = self.event.onClose;
            },
            isConnected: function () {
                return typeof self.socket === "object";
            },
            disconnect: function () {
                if (typeof self.socket === "object") {
                    self.socket.close();
                }
                self.socket = undefined;
            },
            send: function (context, data) {
                var message, messageStr;
                message = {
                    "context": context,
                    "data": data
                };
                messageStr = JSON.stringify(message);
                util.log("Send message: " + messageStr);
                self.socket.send(messageStr);
            },
            registerHandler: function (event, context, handler) {
                self.eventHandlers[event][context] = handler;
            }
        };
    })();
    /**
     * Common utilities.
     */
    util = (function () {
        return {
            caseInsensitiveSort: function (a, b) {
                return a.toUpperCase().localeCompare(b.toUpperCase());
            },
            log: function (message) {
                if ("console" in window
                        && "log" in console
                        && typeof console.log === "function") {
                    console.log(message);
                }
            },
            showMessage: function (title, message) {
                $("#messageTitle").text(title);
                $("#messageBody").text(message);
                $("#messageModal").modal();
            }
        };
    })();
})(jQuery);