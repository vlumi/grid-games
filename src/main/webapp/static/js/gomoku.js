(function ($) {

    var
            webSocket,
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

                        websocket.connect(url, name, password);
                        self.disableLogin();
                    },
                    logout: function () {
                        user.session.name = "";
                        websocket.disconnect();
                        lobby.close();
                        self.enableLogin();
                    },
                };
                return {
                    session: {
                        name: ""
                    },
                    logout: self.logout,
                    onMessagePayload: function (payload) {
                        switch (payload.type) {
                            // TODO: implement
                            case "login":
                                $("#name").val(payload.name);
                                user.session.name = payload.name;
                                break;
                        }
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
                                    user.logout();
                                    return false;
                                });
                    }
                };
            })(),
            lobby = (function () {
                var self = {
                    active: false,
                    send: function () {
                        var message = $("#lobbyMessage").val();
                        if (typeof message === "string" && message.length > 0) {
                            websocket.send("lobby", {
                                "type": "message",
                                "message": message
                            });
                        }
                        $("#lobbyMessage").val("");
                    },
                    populateMemberList: function (members) {
                        var $lobbyMembers = $("#lobbyMembers");
                        $lobbyMembers.find("option").remove();
                        members = members.sort(util.caseInsensitiveSort);
                        $.each(members, function (index, name) {
                            var $option = $("<option>")
                                    .text(name)
                                    .val(name);
                            if (name === user.session.name) {
                                $option.prop("disabled", true);
                            }
                            $lobbyMembers
                                    .append($option);
                        });
                    },
                    addMemberToList: function (name) {
                        var
                                $lobbyMembers = $("#lobbyMembers"),
                                members = [name];

                        $.each($lobbyMembers.find("option"), function (index, option) {
                            members.push($(option).val());
                        });
                        self.populateMemberList(members);
                    }
                };
                return {
                    onMessagePayload: function (payload) {
                        var $message;

                        switch (payload.type) {
                            case "init":
                                lobby.init(payload.members);
                                break;
                            case "join":
                                if (self.active && payload.name !== user.session.name) {
                                    $message = $("<div>").text("*** " + payload.name + " has joined.");
                                    self.addMemberToList(payload.name);
                                }
                                break;
                            case "part":
                                $message = $("<div>").text("*** " + payload.name + " has left.");
                                $("#lobbyMembers option[value=" + payload.name + "]").remove();
                                break;
                            case "message":
                                if (payload.from === undefined) {
                                    $message = $("<div>").text(payload.message);
                                } else if (payload.from === user.session.name) {
                                    $message = $("<div>")
                                            .text(payload.from + ": " + payload.message)
                                            .addClass("self");
                                } else {
                                    $message = $("<div>").text(payload.from + ": " + payload.message);
                                }
                                break;
                        }
                        $("#lobby").append($message);
                    },
                    init: function (members) {
                        var $lobbyMembers = $("#lobbyMembers");

                        if (self.active) {
                            return;
                        }
                        $(document)
                                .on("click.sendLobbyMessage", "#sendLobbyMessage", function () {
                                    self.send();
                                    return false;
                                });
                        $message = $("<div>").text("*** Connected");
                        $("#lobby").append($message);
                        self.populateMemberList(members);

                        self.active = true;
                    },
                    close: function () {
                        if (!self.active) {
                            return;
                        }
                        $(document)
                                .off("click.sendLobbyMessage");
                        $message = $("<div>").text("*** Disconnected");
                        $("#lobby").append($message);
                        $("#lobbyMembers > option").remove();
                        self.active = false;
                    }
                };
            })(),
            gomoku = (function () {
                var self = {
                    active: false
                };
                return {
                    onMessagePayload: function (payload) {
                        switch (payload.type) {
                            // TODO: implement
                        }
                    },
                    init: function (members) {
                        // TODO: implement
                        self.active = true;
                    },
                    close: function () {
                        if (!self.active) {
                            return;
                        }
                        // TODO: implement
                        self.active = false;
                    }
                };
            }),
            websocket = (function () {
                var self = {
                    event: {
                        onMessage: function (evt) {
                            var message = $.parseJSON(evt.data);
                            console.log("Received message: " + message);

                            if (message.type in self.handler.message) {
                                self.handler.message[message.type](message);
                            } else if (message.type in self.handler.payload) {
                                self.handler.payload[message.type](message.payload);
                            }
                        },
                        onClose: function () {
                            // websocket is closed.
                            console.log("Connection closed.");
                            user.logout();
                        }
                    },
                    handler: {
                        message: {
                            error: function (message) {
                                if (message.type in self.handler.error) {
                                    self.handler.error[message.type](message);
                                } else {
                                    // TODO: framework
                                    alert("Received error: " + message.text);
                                }
                                user.logout();
                            }
                        },
                        payload: {
                            user: user.onMessagePayload,
                            lobby: lobby.onMessagePayload,
                            gameGomoku: function (payload) {

                            }
                        },
                        error: {
                            user: user.onError
                        }
                    }
                };
                return {
                    connect: function (url, name, password) {
                        if (typeof webSocket === "object") {
                            webSocket.close();
                        }
                        webSocket = new WebSocket(url);
                        webSocket.onopen = function () {
                            // Web Socket is connected, send data using send()
                            websocket.send("user", {
                                "type": "login",
                                "name": name,
                                "password": password
                            });
                            console.log("Message is sent...");
                        };
                        webSocket.onmessage = self.event.onMessage;
                        webSocket.onclose = self.event.onClose;
                    },
                    disconnect: function () {
                        if (typeof webSocket === "object") {
                            webSocket.close();
                        }
                        webSocket = undefined;
                    },
                    send: function (type, payload) {
                        var message = {
                            "type": type,
                            "payload": payload
                        };
                        webSocket.send(JSON.stringify(message));
                    }
                };
            })(),
            init = function () {
                user.init();
            };


    $(document).ready(function () {
        if ("WebSocket" in window) {
            init();
        }
    });
})(jQuery);