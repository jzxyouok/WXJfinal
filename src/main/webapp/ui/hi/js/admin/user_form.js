/**
 * 用户添加和编辑页面js
 * Created by Punk on 2016/4/12.
 */

var hi_user_from = function () {
    "use strict";

    /**
     * 验证用户名是否存在
     * @param userName
     */
    var valiUserName = function (userName, userIds) {
        var param = {"userName": userName, "userIds": userIds};
        var result = $.hiajax.ajaxFunc("/jf/room/login/valiUserName", param);
        return result;
    }


    var dataVali = function (form) {
        var usernameId = $("#usernameId").val();

        var isAdd = $(form).prop("isAdd");
        if (isAdd) { // 添加
            if (usernameId != "" && valiUserName(usernameId) != "true") {
                hi.himan("账号不可用！");
                return false;
            }
        } else { // 更新

            var userIds = $("#userIds").val();

            if (usernameId != "" && valiUserName(usernameId, userIds) != "true") {
                hi.himan("账号不可用！");
                return false;
            }
        }

        var pass1Id = $("#pass1Id").val();
        var pass2Id = $("#pass2Id").val();
        if (pass1Id != pass2Id) {
            hi.himan("两次输入密码不一致，请从新输入！");
            return false;
        }

        $.hiajax.ajaxFormMainPanel(form);
    }

    var userParsley = function () {
        $('[data-validate="parsley"]').each(function () {
            $(this).parsley();
            $(this).on("submit", function (e) {
                e.preventDefault();
                if (hi.hiCheck(this)) {
                    hi_user_from.submitValiFunc(this);
                }
            });
        });
    }

    return {
        submitValiFunc: dataVali,
        userParsley: userParsley
    };

}();
