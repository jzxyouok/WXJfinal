/**
 * Created by Punk on 2015/11/22.
 */
/* Table initialisation */
$(document).ready(function () {
    var oTable = hi.hiDataTable("#data-table", {
        "sAjaxSource": "/admin/essayClassify/getDataTableList",
        "aoColumns": [
            {
                "mData": "name"
            },
            {
                "mData": "time",
                "mRender": function (data, type, full) {
                    return hi.hiFromUnixStamp(data);
                }
            },
            {
                "mData": "id",
                "bSortable": false,
                "mRender": function (data, type, full) {
                    return '<a href="/admin/essayClassify/edit/' + data + '" class="btn btn-sm btn-success m-r-5">修改</a>' +
                        '<a href="/admin/essayClassify/delete/' + data + '"class="btn btn-sm btn-danger m-r-5 hiDelete">删除</a>';
                }
            }],
        "fnInitComplete": function () {
            $tableFilter = $("#data-table_filter");
            $tableFilter.empty();
            $tableFilter.append(hi.hiElements.getSearchButton("查询", "btnSearch"));
            $tableFilter.append(hi.hiElements.getSearchText("分类名称", "search"));
            $('#btnSearch').on('click', function () {
                oTable.fnFilter($('#search').val());
            });
        },
        // 查询时，需要传递到服务端的参数
        "fnServerParams": function (aoData) {
            aoData.push({"name": "search", "value": $('#search').val()});
        }
    });

    $("#data-table").on("click", ".hiDelete", function (e) {
        e.preventDefault();
        var url = $(this).attr("href");
        hi.hiConfirm("是否删除该分类和该分类下的所有文章？", function () {
            $.post(url, function (data) {
                oTable.fnDraw();
            });
        });
    });
});