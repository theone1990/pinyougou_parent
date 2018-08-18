app.service('shoploginService',function($http){
    this.loginName=function () {
        return $http.get('/shoplogin/name.do');
    }
})