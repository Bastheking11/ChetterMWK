/* global angular */

angular.module('chetter')

        .directive('partytile', function () {
            return {
                // template: 'Hallo'
                templateUrl: 'angular/components/partytile.html'
            }
        })

        .component('message', {
            templateUrl: 'angular/components/message.html',
            controller: function ($scope) {
                $scope.moment = function (time) {
                    if (!Number.isInteger(time))
                        time = time.replace(new RegExp("\\[\\w*\\]"), "");
                    return moment(time).format("YYYY-MM-DD HH:mm:ss");
                }
            },
            bindings: {
                contents: '='
            }
        })

        .component('overlay', {
            bindings: {
                header: '@',
                close: '&'
            },
            transclude: true,
            templateUrl: 'angular/components/overlay.html'
        })

//        .component('partyoverview', {
//            templateUrl: 'angular/components/partyoverview.html',
//            controller: 'partyCtrl',
//            bindings: {
//                party: '='
//            }
//        });
        ;
