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
                    callback();
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

})(jQuery);
