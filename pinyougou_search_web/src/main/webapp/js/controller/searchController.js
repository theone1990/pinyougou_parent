app.controller('searchController',function ($scope,searchService) {
    //写一个方法 用来根据写的主查询条件 调用service的方法 获取结果 结果通过遍历展示出来

   $scope.searchMap={keywords:'三星'};

    $scope.search1=function () {
     searchService.search($scope.searchMap).success(
         function (response) {//Map resultMap
            $scope.resultMap=response;
         }
     );
    }
})