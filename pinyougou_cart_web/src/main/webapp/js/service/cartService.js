app.service('cartService',function ($http) {

    //查询购物车列表
    this.findCartList=function () {
        return $http.get("/cart/findCartList.do");
    }

    //添加购物车列表
    this.addCartItem=function (itemId,num) {
        return $http.get("/cart/addCartItem.do?itemId="+itemId+"&num="+num);
    }
    //求合计
    this.getSum=function (cartList) {
        var totalValue={totalNum:0,totalMoney:0.00};
        for(var i=0;i<cartList.length;i++){
            var cart=cartList[i];
            for(var j=0;j<cart.orderItemList.length;j++){
                var orderItem=cart.orderItemList[j];
                totalValue.totalNum+=orderItem.num;
                totalValue.totalMoney+=orderItem.totalFee;
            }
        }
        return totalValue;
    }
})