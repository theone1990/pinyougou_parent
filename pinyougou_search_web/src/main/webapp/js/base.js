var app = angular.module('pinyougou', []);//定义模块

/*$sce服务写成过滤器*/
app.filter('trustHtml',['$sce',function ($sce) {
    return function (data) {//data就是原来的html代码
        return $sce.trustAsHtml(data);//经过过滤的代码
    }
}]);