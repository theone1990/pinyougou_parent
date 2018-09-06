app.controller('cartController',function ($scope, cartService) {
    //1.查询所有的购物车列表
    $scope.findCartList=function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList=response;
                $scope.totalValue=cartService.getSum($scope.cartList);
            }
        )
    }
    //2.添加购物车或者减少购物车商品
    $scope.addCartItem=function (itemId, num) {
        cartService.addCartItem(itemId,num).success(
            function (response) {
                if(response.success){
                    $scope.findCartList();
                }else{
                    alert(response.message);
                }
            }
        )
    }
})