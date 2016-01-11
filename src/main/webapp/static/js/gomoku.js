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
                    onMessageData: function (data) {
                        switch (data.type) {
                            // TODO: implement
                            case "login":
                                $("#name").val(data.name);
                                user.session.name = data.name;
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
                    onMessageData: function (data) {
                        var $message;

                        switch (data.type) {
                            case "init":
                                lobby.init(data.members);
                                break;
                            case "join":
                                if (self.active && data.name !== user.session.name) {
                                    $message = $("<div>").text("*** " + data.name + " has joined.");
                                    self.addMemberToList(data.name);
                                }
                                break;
                            case "part":
                                $message = $("<div>").text("*** " + data.name + " has left.");
                                $("#lobbyMembers option[value=" + data.name + "]").remove();
                                break;
                            case "message":
                                if (data.from === undefined) {
                                    $message = $("<div>").text(data.message);
                                } else if (data.from === user.session.name) {
                                    $message = $("<div>")
                                            .text(data.from + ": " + data.message)
                                            .addClass("self");
                                } else {
                                    $message = $("<div>").text(data.from + ": " + data.message);
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
                    onMessageData: function (data) {
                        switch (data.type) {
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
                    socket: undefined,
                    event: {
                        onMessage: function (evt) {
                            var message = $.parseJSON(evt.data);

                            if (message.type in self.handler.message) {
                                self.handler.message[message.type](message);
                            } else if (message.type in self.handler.data) {
                                self.handler.data[message.type](message.data);
                            }
                        },
                        onClose: function () {
                            // websocket is closed.
                            user.logout();
                        }
                    },
                    handler: {
                        /**
                         * Handlers accepting the whole received message.
                         */
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
                        /**
                         * Handlers accepting the data part of the received message.
                         */
                        data: {
                            user: user.onMessageData,
                            lobby: lobby.onMessageData,
                            gameGomoku: function (data) {

                            }
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
                    disconnect: function () {
                        if (typeof self.socket === "object") {
                            self.socket.close();
                        }
                        self.socket = undefined;
                    },
                    send: function (type, data) {
                        var message = {
                            "type": type,
                            "data": data
                        };
                        self.socket.send(JSON.stringify(message));
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