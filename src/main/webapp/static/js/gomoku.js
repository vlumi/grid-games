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
            auth = (function () {
                var self = {
                    enableLogin: function () {
                        $("#loginArea input").prop("disabled", false);
                        $("#logoutArea input").prop("disabled", true);
                    },
                    disableLogin: function () {
                        $("#loginArea input").prop("disabled", true);
                        $("#logoutArea input").prop("disabled", false);
                    }
                };
                return {
                    session: {
                        name: ""
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
                        auth.session.name = "";
                        websocket.disconnect();
                        lobby.close();
                        self.enableLogin();
                    },
                    onMessage: function (payload) {
                        $("#name").val(payload.name);
                        auth.session.name = payload.name;
                        lobby.init(payload.members);
                    },
                    onError: function (message) {
                        alert("Login failed: " + message.message);
                        self.enableLogin();
                    },
                    init: function () {
                        $(document)
                                .on("click.login", "#login", function () {
                                    auth.login();
                                    return false;
                                })
                                .on("click.logout", "#logout", function () {
                                    auth.logout();
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
                            if (name === auth.session.name) {
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
                    onMessage: function (payload) {
                        var $message;

                        switch (payload.type) {
                            case "message":
                                if (payload.from === undefined) {
                                    $message = $("<div>").text(payload.message);
                                } else if (payload.from === auth.session.name) {
                                    $message = $("<div>")
                                            .text(payload.from + ": " + payload.message)
                                            .addClass("self");
                                } else {
                                    $message = $("<div>").text(payload.from + ": " + payload.message);
                                }
                                break;
                            case "join":
                                if (self.active) {
                                    $message = $("<div>").text("*** " + payload.name + " has joined.");
                                    self.addMemberToList(payload.name);
                                }
                                break;
                            case "part":
                                $message = $("<div>").text("*** " + payload.name + " has left.");
                                $("#lobbyMembers option[value=" + payload.name + "]").remove();
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
            websocket = (function () {
                var self = {
                    event: {
                        onMessage: function (evt) {
                            var msg = $.parseJSON(evt.data);
                            console.log("Message is received... " + msg);

                            if (msg.error) {
                                if (msg.type in self.event.error) {
                                    self.event.error[msg.type](msg);
                                } else {
                                    alert("Received error: " + msg.message);
                                }
                            } else if (msg.type in self.event.payload) {
                                self.event.payload[msg.type](msg.payload);
                            }
                        },
                        onClose: function () {
                            // websocket is closed.
                            console.log("Connection is closed...");
                            auth.logout();
                        },
                        payload: {
                            auth: auth.onMessage,
                            lobby: lobby.onMessage,
                            gameGomoku: function (payload) {

                            }
                        },
                        error: {
                            auth: auth.onError
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
                            websocket.send("auth", {
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
                auth.init();
            };


    $(document).ready(function () {
        if ("WebSocket" in window) {
            init();
        }
    });
})(jQuery);