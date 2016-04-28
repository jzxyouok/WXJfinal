/**
 * Ajax请求方法二次封装
 * Created by Punk on 2016/3/22.
 */
(function ($) {

    $.hiajax = {};

    /**
     * ajax请求并返回结果
     * @param url
     * @param data
     * @param dataType
     * @param callback
     */
    $.hiajax.ajaxFunc = function (url, data, dataType, callback) {
        if (typeof (dataType) == "undefined" || dataType == null) {
            dataType = "html";
        }
        var result = "";
        $.ajax({
            type: "post",
            url: encodeURI(encodeURI(cxt + url)),
            data: data,
            dataType: dataType,
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            async: false,
            cache: false,
            success: function (response) {
                result = response;
                //扩展回调函数
                if (callback != null) {
                    callback(result);
                }
            }
        });
        return result;
    }

    /**
     * ajax请求url替换主面板内容
     * @param url
     * @param data
     * @param callback
     */
    $.hiajax.ajaxMainPanel = function (url, data, callback) {
        var result = $.hiajax.ajaxFunc(url, data, "html", callback);
        var content = $("#content");
        content.children().remove();
        content.append(result);
    }

    /**
     * ajax提交form替换指定主面板
     * @param form
     * @param callback
     */
    $.hiajax.ajaxFormMainPanel = function (form, callback) {
        if ($(form).hasClass("confirm")) {
            hi.hiConfirm("确认要执行该操作吗？", function (callback) {
                $.hiajax.ajaxForm(form, callback);
            });
        } else {
            $.hiajax.ajaxForm(form, callback);
        }
    }

    /**
     * ajax提交form求并返回结果
     */
    $.hiajax.ajaxForm = function (forms, callback) {
        var target, query, form;
        var form = $(forms);
        target = form.get(0).action;
        target = target.replace(cxt, "");
        query = form.serialize();
        var that = form.find(':submit');
        that.addClass('disabled').attr('autocomplete', 'off').prop('disabled', true);//防止表单重复提交
        $.hiajax.ajaxMainPanel(target, query, callback);
    }

})(jQuery);
