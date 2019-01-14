/* global angular */

var
        main = "localhost:8080/IndividualAssignment_war_exploded/api/v1",
        base = "http://" + main;

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

angular.module('chetter', [])

        .run(function ($http) {
            $http.defaults.headers.common.Authorization = _=> 'Bearer ' + getCookie("login");
            $http.defaults.responseType = 'json';
        })

        .controller('partyCtrl', function ($scope, $http) {

            $scope.init = function () {
                $http.get(base + '/user/self/profile').then(
                        function success(response) {
                            $scope.user = response.data;

                            if (!$scope.hasOwnProperty('party') && response.data.parties.length) {
                                $scope.switchParty(response.data.parties[0]);
                            }
                        },
                        function error(response) {
                            // if (response.status == 401)
                            console.log(response.status);
                        }
                );
            };

            $scope.res = [];
            $scope.search = function (query) {
//                $http.get(base + '/party/search', query).then(
//                        function success(response) {
//                            $scope.res = response.data;
//                        },
//                        function error(response) {
//
//                        }
//                );
            };

            $scope.switchParty = function (party) {
                if ($scope.party && party.id === $scope.party.id)
                    return;

                $http.get(base + '/party/' + party.id).then(
                        function success(response) {
                            $scope.party = response.data;

                            if ($scope.party.channels.length) {
                                $scope.switchChannel($scope.party.channels[0]);
                            } else {
                                $scope.channel = null;
                            }
                        },
                        function error(response) {
                            console.log(response.status);
                        }
                );
            };

            $scope.switchChannel = function (channel) {
                if ($scope.channel && channel.id === $scope.channel.id)
                    return;

                $http.get(base + '/party/' + $scope.party.id + '/channel/' + channel.id).then(
                        function success(response) {
                            $scope.channel = response.data;
                            $scope.getMessageCache(channel);
                        },
                        function error(response) {
                            console.log(response.status);
                        }
                );
            };

            $scope.getMessageCache = function (channel) {
                $http.get(base + '/party/' + $scope.party.id + '/channel/' + channel.id + '/message').then(
                        function success(response) {
                            $scope.messages = response.data;

                            startSocket($scope.party.id, channel.id);
                        },
                        function error(response) {
                            console.error(response.status);
                        }
                );
            };

            var startSocket = function (party, channel) {
                if (!$scope.connection)
                    $scope.connection = new WebSocket("ws:" + main + "/party/" + party + "/channel/" + channel + "/socket");
                else
                    $scope.connection.send(channel);

                $scope.connection.onmessage = function (message) {
                    $scope.messages.push(JSON.parse(message.data));

                    setTimeout(function () {
                        $scope.$apply();
                        var el = document.getElementById('message-holder');
                        el.scrollTop = el.scrollHeight;
                    }, 100);
                };
            };

            $scope.sendMessage = function () {
                var content = document.getElementById('chatbox').firstElementChild;

                $http.put(base + '/party/' + $scope.party.id + '/channel/' + $scope.channel.id + '/message', {
                    content: content.value,
                    author: $scope.user
                }).then(
                        function success(response) {
                            content.value = "";
                            content.focus();
                        },
                        function error(response) {
                            console.error(response.data);
                        }
                );
            };

            $scope.createParty = function (nParty) {

                nParty.image = "/";
                $http.put(base + '/party', nParty).then(
                        function success(response) {
                            $scope.init();

                            nParty = {};
                            $scope.showPartyOverlay = false;
                        },
                        function error(response) {
                            console.error('error', response);
                        }
                );
            };

            $scope.authenticate = function (nUser) {
                if ($scope.isRegister)
                    $scope.register(nUser);
                else
                    $scope.login(nUser);
            };

            $scope.register = function (user) {
                $http.put(base + "/auth", user).then(
                        function success(response) {
                            $scope.login(user);
                        },
                        function error(response) {
                            user.password = "";
                            console.error("Failed");
                        }
                );
            };

            $scope.login = function (user) {
                $http.post(base + "/auth", {email: user.email, password: user.password}).then(
                        function success(response) {
                            setCookie("login", response.data.token, 1);
                            $scope.init();
                        },
                        function error(response) {
                            user.password = "";
                            console.error("Failed");
                        }
                );
            };

            $scope.createChannel = function (nChannel) {

                nChannel.party = $scope.party;
                nChannel.image = "/";
                nChannel.description = "";
                $http.put(base + '/party/' + $scope.party.id + "/channel", nChannel).then(
                        function success(response) {
                            $http.get(base + '/party/' + $scope.party.id).then(
                                    function success(response) {
                                        $scope.party.channels = response.data.channels;

                                        if (!$scope.hasOwnProperty("channel") && $scope.party.channels.length) {
                                            $scope.switchChannel($scope.party.channels[0]);
                                        }
                                    },
                                    function error(response) {
                                        console.log(response.status);
                                    }
                            );

                            nChannel = {};
                            $scope.showChannelOverlay = false;
                        },
                        function error(response) {
                            console.error('error', response);
                        }
                );

            };

            if (getCookie("login"))
                $scope.init();

        });