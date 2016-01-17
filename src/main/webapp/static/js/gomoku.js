(function ($) {
    var
            util = (function () {
                return {
                    caseInsensitiveSort: function (a, b) {
                        return a.toUpperCase().localeCompare(b.toUpperCase());
                    }
                };
            })(),
            user = (function () {
                var self = {
                    enableLogin: function () {
                        $("#loginArea input").prop("disabled", false);
                        $("#logoutArea input").prop("disabled", true);
                    },
                    disableLogin: function () {
                        $("#loginArea input").prop("disabled", true);
                        $("#logoutArea input").prop("disabled", false);
                    },
                    login: function () {
                        var
                                url = $("#url").val(),
                                name = $("#name").val(),
                                password = $("#password").val();

                        var callback = function () {
                            websocket.send("user", {
                                "type": "login",
                                "name": name,
                                "password": password
                            });
                        };
                        websocket.connect(url, callback);
                    },
                    logout: function () {
                        user.session.name = "";
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
                                user.session.name = data.name;
                            }
                        }
                    }
                };
                return {
                    session: {
                        name: ""
                    },
                    onMessageData: function (data) {
                        if (data.type in self.handler.data) {
                            self.handler.data[data.type](data);
                        }
                    },
                    onClose: function () {
                        self.logout();
                    },
                    onError: function (message) {
                        alert("Login failed: " + message.message);
                        self.enableLogin();
                    },
                    init: function () {
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
                                });
                        // TODO: move focus
                    }
                };
            })(),
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
                                    $lobbyMembers = $("#lobbyMembers"),
                                    selected = $("#lobbyMembers :selected").val();

                            $lobbyMembers.find("option").remove();
                            $.each(self.memberList, function (index, name) {
                                var $element;

                                $element = $("<option>")
                                        .attr("val", name)
                                        .text(name)
                                        .appendTo($lobbyMembers);
                                if (name === user.session.name) {
                                    $element
                                            .addClass("self")
                                            .prop("disabled", true);
                                } else if (name === selected) {
                                    $element
                                            .prop("selected", true);
                                }
                            });
                        },
                        sendMessage: function () {
                            var
                                    message,
                                    to = $("#lobbyMembers").val();
                            message = {
                                type: "chatMessage",
                                message: $("#lobbyMessage").val(),
                                to: "",
                                private: false
                            };
                            if ($("#lobbyPrivateMessage").prop("checked")) {
                                message.to = to;
                                message.private = true;
                            }
                            if (typeof message.message === "string" && message.message.length > 0) {
                                websocket.send("lobby", message);
                            }
                            $("#lobbyMessage").val("");
                        },
                        addMessage: function (from, message, isFromSelf, isPrivate) {
                            var $entry, $timestamp, $user, $message;
                            $entry = $("<div>");

                            $timestamp = $("<span>")
                                    .addClass("timestamp")
                                    .text(self.ui.getTimestamp())
                                    .appendTo($entry);
                            $user = $("<span>")
                                    .addClass("user")
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
                    clearMemberList: function () {
                        self.memberList = [];
                        self.populateMemberList(self.memberList);
                    },
                    handler: {
                        data: {
                            init: function (data) {
                                var members = data.members;
                                if (self.active) {
                                    return;
                                }
                                self.ui.addStatusMessage("Connected.");
                                self.populateMemberList(members);

                                $("#lobbyArea").addClass("active");
                            },
                            join: function (data) {
                                if (data.name !== user.session.name) {
                                    self.ui.addStatusMessage(data.name + " has joined.");
                                    self.addMemberToList(data.name);
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
                                    if (data.from === user.session.name) {
                                        isFromSelf = true;
                                    }
                                    self.ui.addMessage(data.from, message, isFromSelf, isPrivate);
                                }
                            },
                            status: function (data) {
                                var $element;
                                self.ui.addStatusMessage(data.name + " is " + data.status + ".");

                                if (data.name !== user.session.name) {

                                    self.memberStatuses[data.name] = data.status;
                                    $element = $("#lobbyMembers [val=" + data.name + "]");
                                    switch (status) {
                                        case "busy":
                                            $element
                                                    .addClass("busy")
                                                    .prop("disabled", true)
                                                    .prop("selected", false);
                                            break;
                                        case "free":
                                            $element
                                                    .removeClass("busy")
                                                    .prop("disabled", true);
                                            break;
                                    }
                                }

                            }
                        }
                    }

                };
                return {
                    init: function () {
                        $(document)
                                .on("click.member", "#lobbyMembers li:not(.self):not(.busy)", function (event) {
                                    $("#lobbyMembers li.selected").removeClass("selected");
                                    $(event.currentTarget).addClass("selected");
                                })
                                .on("click.sendLobbyMessage", "#lobbyArea.active #sendLobbyMessage", function (event) {
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
                    },
                    onMessageData: function (data) {
                        if (data.type in self.handler.data) {
                            self.handler.data[data.type](data);
                        }
                    },
                    sendPublicMessage: function (content) {
                        var message = {
                            type: "chatMessage",
                            message: content,
                            to: "",
                            private: false
                        };
                        if (typeof message.message === "string" && message.message.length > 0) {
                            websocket.send("lobby", message);
                        }
                    },
                    onClose: function () {
                        $("#lobbyArea").removeClass("active");
                        self.ui.addStatusMessage("Disconnected.");
                        self.clearMemberList();
                        self.active = false;
                    }
                };
            })(),
            gomoku = (function () {
                var self = {
                    mySide: null,
                    createGridRow: function (row, length, rowClass) {
                        var
                                $row = $("<tr>"),
                                $cell = $("<td>"),
                                cellCopy,
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

                            cellCopy = $cell
                                    .clone()
                                    .addClass(rowClass)
                                    .addClass(cellClass)
                                    .appendTo($row);
                            $piece
                                    .clone()
                                    .prop("id", "pos_" + row + "_" + column)
                                    .addClass("free")
                                    .appendTo(cellCopy)
                                    .html("&nbsp;");
                        }
                        return $row;
                    },
                    createGameBoard: function ($gomokuArea, sideLength, opponent) {
                        var
                                $gameBoard,
                                row,
                                rowClass;

                        $gameBoard = $("<table>")
                                .addClass("gameBoard")
                                .addClass("size30")
                                .appendTo($gomokuArea);
                        for (row = 0; row < sideLength; row++) {
                            if (row === 0) {
                                rowClass = "top";
                            } else if (row === sideLength - 1) {
                                rowClass = "bottom";
                            } else {
                                rowClass = "middle";
                            }
                            self.createGridRow(row, sideLength, rowClass)
                                    .appendTo($gameBoard);
                        }
                    },
                    createGame: function (opponent) {
                        var
                                $gomokuArea = $("#gomokuArea");

                        $gomokuArea.html("");
                        $("<input>")
                                .attr("name", "opponent")
                                .attr("value", opponent)
                                .appendTo($gomokuArea);
                        $("<div data-turn>")
                                .appendTo($gomokuArea);
                        self.createGameBoard($gomokuArea, 19, opponent);
                    },
                    challenge: function (challengee) {
                        var message = {
                            "type": "challenge",
                            "to": challengee,
                            "from": user.session.name
                        };
                        websocket.send("gomoku", message);
                        // TODO: set message to lobby
                        // TODO: set status on lobby
                    },
                    acceptChallenge: function (challenger) {
                        var message = {
                            "type": "acceptChallenge",
                            "to": challenger
                        };
                        websocket.send("gomoku", message);
                        // TODO: set message to lobby
                        // TODO: set status on lobby
                    },
                    rejectChallenge: function (challenger) {
                        var message = {
                            "type": "rejectChallenge",
                            "to": challenger
                        };
                        websocket.send("gomoku", message);
                    },
                    placePiece: function (row, column) {
                        var message = {
                            "type": "placePiece",
                            "row": row,
                            "column": column
                        };
                        websocket.send("gomoku", message);
                    },
                    newGame: function () {
                        var message = {
                            "type": "newGame"
                        };
                        websocket.send("gomoku", message);
                    },
                    close: function () {
                        $("#gomokuArea").html("");

                        var message = {
                            "type": "leave"
                        };
                        websocket.send("gomoku", message);
                    },
                    handler: {
                        data: {
                            state: function (data) {
                                var
                                        $gameBoard = $("#gomokuArea .gameBoard");
                                if ($gameBoard.length > 0) {
                                    $gameBoard.remove();
                                }
                                self.createGame(data.opponent);
                                $gameBoard = $("#gomokuArea .gameBoard");
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
                            },
                            challenge: function (data) {
                                var
                                        challenger = data.from,
                                        response = confirm("Accept challenge from " + challenger + "?");
                                if (response) {
                                    self.acceptChallenge(challenger);
                                } else {
                                    self.rejectChallenge(challenger);
                                }
                            },
                            placePiece: function (data) {
                                var
                                        $gameBoard = $("#gomokuArea .gameBoard"),
                                        row = data.row,
                                        column = data.column,
                                        side = "side" + data.side,
                                        id = "pos_" + row + "_" + column;
                                $("#" + id)
                                        .removeClass("free")
                                        .addClass(side);

                                $gameBoard.removeClass("myTurn");
                                if (self.mySide === data.turn) {
                                    $gameBoard.addClass("myTurn");
                                }
                            },
                            gameOver: function (data) {
                                var
                                        $gameBoard = $("#gomokuArea .gameBoard"),
                                        newGameResponse;
                                if ("winner" in data && data.winner !== "") {
                                    if (data.winner === user.session.name) {
                                        alert("Game over! You won!");
                                    } else {
                                        alert("Game over! You lost!");
                                    }
                                } else {
                                    alert("Game over! A tie!");
                                }
                                $gameBoard.removeClass("myTurn");

                                newGameResponse = confirm("Start a new game?");
                                if (newGameResponse) {
                                    self.newGame();
                                } else {
                                    self.close();
                                }
                                // TODO: set status on lobby
                            }
                        }
                    }
                };
                return {
                    onMessageData: function (data) {
                        if (data.type in self.handler.data) {
                            self.handler.data[data.type](data);
                        }
                    },
                    init: function () {
                        $(document)
                                // TODO: listener to lobbymembers -- disable challenge button if none selected
                                .on("click", "#challengeGomoku", function (event) {
                                    var challengee = $("#lobbyMembers").val();
                                    if (typeof challengee === "string" && challengee !== "") {
                                        self.challenge(challengee);
                                    }
                                })
                                .on("click.placePiece", "#gomokuArea .gameBoard.myTurn .free", function (event) {
                                    var
                                            id = event.currentTarget.id,
                                            idSplit = id.split("_"),
                                            row = parseInt(idSplit[1]),
                                            column = parseInt(idSplit[2]);
                                    self.placePiece(row, column);
                                });
                    },
                    onClose: function () {
                        self.close();
                    }
                };
            })(),
            websocket = (function () {
                var self = {
                    socket: undefined,
                    event: {
                        onMessage: function (evt) {
                            var message = $.parseJSON(evt.data);
                            console.log("Received message: " + evt.data);

                            if (message.context in self.handler.message) {
                                self.handler.message[message.context](message);
                            } else if (message.context in self.handler.data) {
                                self.handler.data[message.context](message.data);
                            }
                        },
                        onClose: function () {
                            $.each(self.handler.close, function (index, handler) {
                                handler();
                            });
                        }
                    },
                    handler: {
                        /**
                         * Handlers accepting the whole received message.
                         */
                        message: {
                            error: function (message) {
                                if (message.context in self.handler.error) {
                                    self.handler.error[message.context](message);
                                } else {
                                    // TODO: framework
                                    alert("Received error: " + message.text);
                                }
                                // TODO:
                            }
                        },
                        /**
                         * Handlers accepting the data part of the received message.
                         */
                        data: {
                            user: user.onMessageData,
                            lobby: lobby.onMessageData,
                            gomoku: gomoku.onMessageData
                        },
                        close: {
                            user: user.onClose,
                            lobby: lobby.onClose,
                            gomoku: gomoku.onClose
                        },
                        error: {
                            user: user.onError
                        }
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

                        console.log("Send message: " + messageStr);
                        self.socket.send(messageStr);
                    }
                };
            })(),
            init = function () {
                user.init();
                lobby.init();
                gomoku.init();
            };


    $(document).ready(function () {
        if ("WebSocket" in window) {
            init();
        }
    });
})(jQuery);