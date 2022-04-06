$(function () {
    var getProductCategoryURL ='/o2o-1/shopadmin/getproductcategorylist';
    var addProductCategoryURL ='/o2o-1/shopadmin/addproductcategory';
    var deleteProductCategoryUrl ='/o2o-1/shopadmin/removeproductcategory';
    // 加载数据
    getProductCategoryList();

    function getProductCategoryList() {
        $.getJSON(getProductCategoryURL,
            function(data) {
                if (data.success) {
                    var dataList = data.data;
                    $('.product-categroy-wrap').html('');
                    var tempHtml = '';
                    dataList
                        .map(function(item, index) {
                            tempHtml += ''
                                + '<div class="row row-product-category now">'
                                + '<div class="col-33 product-category-name">'
                                + item.productCategoryName
                                + '</div>'
                                + '<div class="col-33">'
                                + item.priority
                                + '</div>'
                                + '<div class="col-33"><a href="#" class="button delete" data-id="'
                                + item.productCategoryId
                                + '">删除</a></div>'
                                + '</div>';
                        });
                    $('.product-categroy-wrap').append(tempHtml);
                }
            });
    }

    //  新增按钮的点击事件
    $('#new').click(
        function(){
            // 新增数据 以 temp 为标识，便于和库表中的数据区分开来
            var tempHtml = '<div class="row row-product-category temp">'
                + '<div class="col-33"><input class="category-input category" type="text" placeholder="分类名"></div>'
                + '<div class="col-33"><input class="category-input priority" type="number" placeholder="优先级"></div>'
                + '<div class="col-33"><a href="#" class="button delete">删除</a></div>'
                + '</div>';
            $('.product-categroy-wrap').append(tempHtml);
        });


    //点击提交按钮提交给后台
    $('#submit').click(function() {
        // 通过temp 获取新增的行
        var tempArr = $('.temp');
        // 定义数组接收新增的数据
        var productCategoryList = [];
        //依次遍历就完成了一行的添加
        tempArr.map(function(index, item) {
            var tempObj = {};
            tempObj.productCategoryName = $(item).find('.category').val();
            tempObj.priority = $(item).find('.priority').val();
            if (tempObj.productCategoryName && tempObj.priority) {
                productCategoryList.push(tempObj);
            }
        });
        $.ajax({
            url : addProductCategoryURL,
            type : 'POST',
            // 将其变成字符串传递给后台，后端通过 @HttpRequestBody直接接收，
            data : JSON.stringify(productCategoryList),
            contentType : 'application/json',
            success : function(data) {
                if (data.success) {
                    $.toast('提交成功！');
                    //重新加载数据
                    getProductCategoryList();
                } else {
                    $.toast('提交失败');
                }
            }
        });
    });

    //删除商品类别
    /*它有两种删除方式，一种是插入数据库了 now，在后台删除数据库中的信息
    * 另外一种就是你惦记了新增按钮，但是还没添加数据  temp，这时直接在前端删除即可*/
    $('.product-categroy-wrap').on('click', '.row-product-category.now .delete',
        function(e) {
            var target = e.currentTarget;
            $.confirm('确定么?', function() {
                $.ajax({
                    url : deleteProductCategoryUrl,
                    type : 'POST',
                    data : {
                        productCategoryId : target.dataset.id,
                    },
                    dataType : 'json',
                    success : function(data) {
                        if (data.success) {
                            $.toast('删除成功！');
                            // 重新加载数据
                            getProductCategoryList();
                        } else {
                            $.toast('删除失败！');
                        }
                    }
                });
            });
        });

    $('.product-categroy-wrap').on('click', '.row-product-category.temp .delete',
        function(e) {
            $(this).parent().parent().remove();
        });


});
