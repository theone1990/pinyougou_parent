app.controller('shoploginController',function ($scope, shoploginService) {
    $scope.showLoginName=function () {
        shoploginService.loginName().success(
            function (response) {
                $scope.loginName=response.loginName;
            }
        )
    }
})