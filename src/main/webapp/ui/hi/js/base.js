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
        var errors = $(obj).find(".parsley-error");
        if (errors.length > 0) {
            return false;
        }
        return true;
    },
    //表单检测提交
    hiParsley: function () {
        $('[data-validate="parsley"]').each(function () {
            $(this).parsley();
            $(this).on("submit", function (e) {
                e.preventDefault();
                if (hi.hiCheck(this)) {
                    $.hiajax.ajaxFormMainPanel(this);
                }
            });
        });
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
    hiGetCheckFunc: function (id) {
        var ids = "";
        $("#data-table input[name='checkIds']").each(function () {
            if ($(this)[0].checked == true) {
                ids += $(this).val() + ",";
            }
        });
        return ids;
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

        //表格全选
        $(document).on("change", ".table-check-all", function (e) {
            var checked = $(this).prop("checked");
            $("#data-table_wrapper input[name='checkIds']").each(function () {
                $(this).prop("checked", checked);
            });
        });

        //多行删除
        $(document).on("click", ".table-delete-all", function (e) {
            e.preventDefault();
            var ids = hi.hiGetCheckFunc();
            if (ids != "") {
                var url = $(this).attr("data-url");
                hi.hiConfirm("确认要执行该操作吗？", function (callback) {
                    var data = {'ids': ids};
                    $.hiajax.ajaxMainPanel(url, data);
                });
            } else {
                hi.himan("请选择操作数据！");
            }
        });

        //ajax请求
        $(document).on("click", ".ajax", function (e) {
            e.preventDefault();
            var target = $(this).attr("href");
            if ($(this).hasClass("confirm")) {
                hi.hiConfirm("确认要执行该操作吗？", function () {
                    $.hiajax.ajaxMainPanel(target);
                });
            } else {
                $.hiajax.ajaxMainPanel(target);
            }
        });

        //表单提交ajax
        $(document).on("submit", ".ajax-form", function (e) {
            e.preventDefault();
            $.hiajax.ajaxFormMainPanel(this);
        });

        //表格排序
        $(document).on("click", ".table-order", function (e) {
            hi_splitPage.orderByFun($(this));
        });

        //dialog
        $(document).on("click", ".ajax-dialog", function (e) {
            var url = $(this).attr("data-url");
            var data = $(this).attr("data-param");
            data = "[" + data + "]";
            data = eval(data);
            var modal = $("#ajax-modal");
            modal.load(url, data[0], function () {
                modal.modal();
            });
        });
    }
};
hi.hiInit();
