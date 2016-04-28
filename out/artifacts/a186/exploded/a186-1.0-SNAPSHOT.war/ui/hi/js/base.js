var hi = {
    hiCurrentDataTable: {},
    himan: function (title, text) {
        $.gritter.add({
            title: title,
            text: text,
            image: '/ui/color/assets/img/user-3.jpg',
            sticky: false,
            time: '1000'
        });
    },
    //检查字段
    hiCheck: function (obj) {
        var errors = obj.find(".parsley-error");
        if (errors.length > 0) {
            return false;
        }
        return true;
    },
    hiConfirm: function (content, okCallback) {
        var dom = '<div class="modal fade"><div class="modal-dialog">' +
            '<div class="modal-content"><div class="modal-header"> ' +
            '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button> ' +
            '<h4 class="modal-title">提示</h4> ' +
            '</div><div class="modal-body"> ' +
            '<div class="alert alert-danger m-b-0">' +
            '<h4><i class="fa fa-info-circle"></i> 警告</h4><p>' + content + '</p></div></div>' +
            '<div class="modal-footer"><a href="javascript:;" class="btn btn-sm btn-white" data-dismiss="modal">关闭</a> ' +
            '<a href="javascript:;" class="btn btn-sm btn-danger" data-hiok="modal">确认</a>' +
            '</div></div></div></div>';
        $model = $(dom).modal("show").on("click", '[data-hiok="modal"]', function () {
            okCallback();
            $model.modal("hide");
        });
    },
    hiInit: function () {
        //左侧菜单栏点击操作
        $("#sidebar_nav").on("click", ".ajaxMainPanel", function (e) {
            e.preventDefault();
            var url = $(this).attr("href");
            $.hiajax.ajaxMainPanel(url);
        });

        //全局ajax加载框
        $(document).ajaxStart(function () {
            $('#page-loader').removeClass('hide');
        });
        $(document).ajaxStop(function () {
            $('#page-loader').addClass('hide');
        });
    }
};
hi.hiInit();
