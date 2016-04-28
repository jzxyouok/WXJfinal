var hi_splitPage = function () {

    /**
     *
     * @param divId 分页显示div
     * @param formId 分页formId
     * @param totalRow 总行数
     * @param pageSize 每页显示多少条
     * @param pageNumber 当前第几页
     * @param totalPages 总页数
     * @param isSelectPage 是否显示第几页
     * @param isSelectSize 是否显示每页显示多少条
     * @param orderColumn 表格排序的列
     * @param orderMode  排序的方式
     * @param currentPageCount 当前页记录数量
     */
    var splitPageHtml = function (divId, formId, totalRow, pageSize, pageNumber, totalPages, isSelectPage, isSelectSize, orderColumn, orderMode, currentPageCount) {
        var start = 1;
        var end = currentPageCount;
        if (pageNumber != 1) {
            start = (pageNumber - 1) * pageSize + 1;
            end = start + currentPageCount - 1;
        }

        //显示从第几页到第几页
        var splitStr = '<div class="col-sm-6 ui-sortable hi-paging"><div class="dataTables_info" id="data-table_info">';
        splitStr += '显示 ' + start + ' 至 ' + end + ' 条，共 ' + totalRow + ' 条记录';
        splitStr += '</div></div>';

        //显示每页显示行数
        splitStr += '<div class="col-sm-6 ui-sortable hi-paging"><div class="dataTables_length hi-paging-select"><select name="pageSize" onChange="hi_splitPage.splitPageLink(\'' + divId + '\', \'' + formId + '\', 1);">';

        var optionStr = '<option value="10">' + i18n_common_splitPage_perPage + '10' + i18n_common_splitPage_strip + '</option>';
        optionStr += '<option value="20">' + i18n_common_splitPage_perPage + '20' + i18n_common_splitPage_strip + '</option>';
        optionStr += '<option value="40">' + i18n_common_splitPage_perPage + '40' + i18n_common_splitPage_strip + '</option>';
        optionStr += '<option value="80">' + i18n_common_splitPage_perPage + '80' + i18n_common_splitPage_strip + '</option>';
        optionStr += '<option value="100">' + i18n_common_splitPage_perPage + '100' + i18n_common_splitPage_strip + '</option>';
        optionStr += '<option value="200">' + i18n_common_splitPage_perPage + '200' + i18n_common_splitPage_strip + '</option>';
        optionStr = optionStr.replace('"' + pageSize + '"', '"' + pageSize + '" selected="selected"');

        splitStr += optionStr + ' </select></div></div>';

        //显示跳转到第几页
        splitStr += '<div class="col-sm-6 ui-sortable hi-paging"><div class="dataTables_length hi-paging-select"><select name="pageNumber" onChange="hi_splitPage.splitPageLink(\'' + divId + '\', \'' + formId + '\', this.value);">';
        for (var i = 1; i <= totalPages; i++) {
            if (i == pageNumber) {
                splitStr += '<option selected value="' + i + '">' + i18n_common_splitPage_jump + i + i18n_common_splitPage_jumpPage + '</option>';
            } else {
                splitStr += '<option value="' + i + '">' + i18n_common_splitPage_jump + i + i18n_common_splitPage_jumpPage + '</option>';
            }
        }
        splitStr += ' </select></div></div>';

        //显示翻页
        splitStr += '<div class="col-md-6 col-sm-6 ui-sortable hi-paging"><div class="dataTables_paginate paging_bootstrap pagination"><ul class="pagination m-t-0 m-b-0">';
        if (pageNumber == 1 || totalPages == 0) {
            splitStr += '<li class="prev disabled"><a href="javascript:void(0)">' + i18n_common_splitPage_previous + '</a></li>';
        } else {
            splitStr += '<li class="prev"><a href="javascript:hi_splitPage.splitPageLink(\'' + divId + '\', \'' + formId + '\', ' + (pageNumber - 1) + ');">' + i18n_common_splitPage_previous + '</a></li>';
        }
        for (var i = 1; i <= totalPages; i++) {
            if (i == 2 && pageNumber - 4 > 1) {
                splitStr += '<li><a href="javascript:void(0)">...</a></li>';
                i = pageNumber - 4;
            } else if (i == pageNumber + 4 && pageNumber + 4 < totalPages) {
                splitStr += '<li><a href="javascript:void(0)">...</a></li>';
                i = totalPages - 1;
            } else {
                if (pageNumber == i) {
                    splitStr += '<li class="active"><a href="javascript:void(0)">' + pageNumber + '</a></li>';
                } else {
                    splitStr += '<li><a href="javascript:hi_splitPage.splitPageLink(\'' + divId + '\', \'' + formId + '\', ' + i + ');">';
                    splitStr += i;
                    splitStr += '</a></li>';
                }
            }
        }
        if (pageNumber == totalPages || totalPages == 0) {
            splitStr += '<li class="next disabled"><a href="javascript:void(0)">' + i18n_common_splitPage_next + '</a></li>';
        } else {
            splitStr += '<li class="next"><a href="javascript:hi_splitPage.splitPageLink(\'' + divId + '\', \'' + formId + '\', ' + (pageNumber + 1) + ');">' + i18n_common_splitPage_next + '</a></li>';
        }
        splitStr += '</ul></div></div>';

        splitStr += '<input type="hidden" name="orderColumn" value="' + orderColumn + '"/>';
        splitStr += '<input type="hidden" name="orderMode" value="' + orderMode + '"/>';
        return splitStr;
    }

    var orderByFun = function (order) {
        var form = order.closest("form");
        var orderColumnNode = form.find("input[name=orderColumn]");
        var orderColumn = orderColumnNode.val();

        var orderModeNode = form.find("input[name=orderMode]");
        var orderMode = orderModeNode.val();

        var columnName = order.attr("orderby");
        if (columnName == orderColumn) {
            if (orderMode == "") {
                orderModeNode.val("asc");
            } else if (orderMode == "asc") {
                orderModeNode.val("desc");
            } else if (orderMode == "desc") {
                orderModeNode.val("");
            }
        } else {
            orderColumnNode.val(columnName);
            orderModeNode.val("asc");
        }

        $.hiajax.ajaxFormMainPanel(form);

    }

    /**
     * 分页链接处理
     * @param divId
     * @param formId
     * @param toPage
     */
    var splitPageLink = function (divId, formId, toPage) {
        $("#" + formId + " select[name=pageNumber]").val(toPage);
        $.hiajax.ajaxFormMainPanel($("#" + formId));
    }

    return {
        splitPage: splitPageHtml,
        orderByFun: orderByFun,
        splitPageLink: splitPageLink
    };
}();