app.controller('searchController',function ($scope,$location,searchService) {
    //写一个方法 用来根据写的主查询条件 调用service的方法 获取结果 结果通过遍历展示出来

   $scope.searchMap={'keywords':'','category':'','brand':'',spec:{},'price':'','pageNo':1,'pageSize':20,'sortField':'','sortType':''};//搜索对象

    $scope.search1=function () {
     searchService.search($scope.searchMap).success(
         function (response) {//Map resultMap
            $scope.resultMap=response;
            buildPageLabel();
         }
     );
    }

    $scope.clear1=function () {
        $scope.searchMap={'keywords':$scope.searchMap.keywords,'category':'','brand':'',spec:{},'price':'','pageNo':1,'pageSize':20,'sortField':'','sortType':''};
    }

    //构建分页标签
    buildPageLabel=function () {
        $scope.pageLabel=[];//新增分页栏属性
        var maxPageNo=$scope.resultMap.totalPages;//总页数
        var firstPage=1;
        var lastPage=maxPageNo;

        $scope.preDot=false;//前面没有点
        $scope.postDot=false;//后面没有点

        if(maxPageNo>5){ //如果总页数大于5
            if($scope.searchMap.pageNo<=3){ //如果当前页小于等于3
                lastPage=5; //展示前5页
                $scope.preDot=false;
                $scope.postDot=true;
            }else if($scope.searchMap.pageNo>3 && $scope.searchMap.pageNo<maxPageNo-2){ //显示中间5页
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
                $scope.preDot=true;
                $scope.postDot=true;
            }else{
                firstPage=$scope.resultMap.totalPages-4; //显示后5页
                $scope.preDot=true;
                $scope.postDot=false;
            }
        }
        //循环产生页码标签
        for(var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }
    }

    //根据页码查询
    $scope.queryByPage=function (pageNo) {
        console.log(isNaN(pageNo));
        //判断是否为数字, 是数字返回false
        if(!isNaN(pageNo) && pageNo>=1 && pageNo<=$scope.resultMap.totalPages){
            $scope.searchMap.pageNo=parseInt(pageNo);
            $scope.search1();
        }else{
            return;
        }
    }

    //设置排序规则
    $scope.sortSearch=function (sortField, sortType) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sortType=sortType;
        $scope.search1();
    }

    //判断关键词是不是品牌
    $scope.keywordIsBrand=function () {
        for(var i=0; i<$scope.resultMap.brandList.length; i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)!=-1){
                $scope.searchMap.brand=$scope.resultMap.brandList[i].text;
                return true;
            }
        }
        return false;
    }

    //添加搜索项
    $scope.addSearchItem=function (key, value) {
        if(key=='category' || key=='brand' || key=='price'){
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key]=value;
        }
        $scope.search1();
    }

    //移除搜索条件
    $scope.removeSearchItem=function (key) {
        if(key=="category" || key=="brand" || key=='price'){
            $scope.searchMap[key]="";
        }else{
            delete $scope.searchMap.spec[key];//移除此属性
        }
        $scope.search1();
    }

    //加载查询字符串
    $scope.loadKeywords=function () {
        $scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search1();
    }
})