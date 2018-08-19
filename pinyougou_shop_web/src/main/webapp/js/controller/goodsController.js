 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService,uploadService){
	
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
	$scope.entity={goods:{},goodsDesc:{itemImages:[]}};
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

});	
