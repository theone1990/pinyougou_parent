 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService,uploadService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.add=function(){
		//先取出富文本编辑器中的内容,赋值给变量
		var htmltext = editor.html();
		$scope.entity.goodsDesc.introduction=htmltext;
        goodsService.add( $scope.entity  ).success(
			function(response){
                alert(response.message);
                $scope.entity={};
                editor.html('');//清空富文本编辑器
			}		
		);				
	}

	//上传图片
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(
			function (response) {
				if(response.success){
					//上传成功, 取出url, 设置文件地址
					$scope.image_entity.url=response.message;
				}else{
					alert(response.message);
				}
            }
		)
    }

    //添加图片列表
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]},itemList:[]};
	$scope.add_image_entity=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //列表中移除图片
	$scope.remove_image_entity=function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//读取一级分类
	$scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(
			function (response) {
				$scope.itemCat1List=response;
            }
		)
    }

    //读取二级分类
	$scope.$watch('entity.goods.category1Id',function (newValue, oldValue) {
		//$watch方法用于监控某个变量的值, 值发生变化时自动执行函数
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.itemCat2List=response;
            }
		)
    })

    //读取三级分类
    $scope.$watch('entity.goods.category2Id',function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List=response;
            }
        )
    })

	//根据三级分类,读取模板id
	$scope.$watch('entity.goods.category3Id',function (newValue, oldValue) {
		itemCatService.findOne(newValue).success(
			function (response) {
				$scope.entity.goods.typeTemplateId=response.typeId;
            }
		)
    })

	//模板id选择后, 更新品牌列表
	$scope.$watch('entity.goods.typeTemplateId',function (newValue, oldValue) {
		typeTemplateService.findOne(newValue).success(
			function (response) {
				$scope.typeTemplate=response;//获取类型模板
				$scope.typeTemplate.brandIds=angular.fromJson($scope.typeTemplate.brandIds);
				//扩展属性
				$scope.entity.goodsDesc.customAttributeItems=angular.fromJson($scope.typeTemplate.customAttributeItems);
            }
		)

		//查询规格列表
		typeTemplateService.findSpecList(newValue).success(
			function (response) {
				$scope.specList=response;
            }
		)
    })

	$scope.updateSpecAttribute=function ($event, name, value) { //name:attributeName(网络), value:attributeValue[3G,4G]
		var object =$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",name);
		if(object != null){
			//如果有这个选项名, 就直接添加这个选项值attributeValue
			if($event.target.checked){
				object.attributeValue.push(value);
			}else{
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);
			}
			if(object.attributeValue.length==0){
				$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
			}
		}else{
			//如果没有这个选项, 就直接添加一个对象
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
    }

    //创建sku列表
	$scope.createItemList=function () {
		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'}]; //每次都先初始化,再深克隆
		var items=$scope.entity.goodsDesc.specificationItems;
		for(var i=0;i<items.length;i++){
			$scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
		}
    }

    //添加列值, 深克隆
	addColumn=function (list, columnName, columnValues) {
		var newList=[]; //新的集合
		for(var i=0;i<list.length;i++){
			var oldRow=list[i];
			for(var j=0;j<columnValues.length;j++){
				var newRow=angular.fromJson(angular.toJson(oldRow));//深克隆
				newRow.spec[columnName]=columnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
    }
});	
