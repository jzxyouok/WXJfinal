/**
 * 用户管理界面js
 * Created by Punk on 2016/4/13.
 */
jQuery(document).ready(function () {
    $(".resetPassword").click(function (e) {
        e.preventDefault();
        var url = $(this).attr("href");
        hi.hiConfirm("是否重置密码？", function () {
            var result = $.hiajax.ajaxFunc(url);
            alert("新密码为：" + result);
        });
    });
});
