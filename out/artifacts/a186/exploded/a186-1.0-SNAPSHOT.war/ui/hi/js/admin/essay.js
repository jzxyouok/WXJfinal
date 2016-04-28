/**
 * Created by Punk on 2015/11/22.
 */
$(document).ready(function () {
    var oTable = hi.hiDataTable("#data-table", {
        "sAjaxSource": "/admin/essay/getDataTableList",
        "aoColumns": [
            {
                "mData": "title"
            },
            {
                "mData": "userName"
            },
            {
                "mData": "name"
            },
            {
                "mData": "createTime",
                "mRender": function (data, type, full) {
                    return hi.hiFromUnixStamp(data);
                }

            },
            {
                "mData": "publishTime",
                "mRender": function (data, type, full) {
                    return data == null ? '' : hi.hiFromUnixStamp(data);
                }
            },
            {
                "mData": "updateTime",
                "mRender": function (data, type, full) {
                    return data == null ? '' : hi.hiFromUnixStamp(data);
                }
            },
            {
                "mData": "id",
                "bSortable": false,
                "mRender": function (data, type, full) {
                    var status = full.status;
                    var dom = '<a href="/admin/essay/edit/' + data + '" class="btn btn-sm btn-success m-r-5">修改</a>' +
                        '<a href="/admin/essay/delete/' + data + '"class="btn btn-sm btn-danger m-r-5 delete">删除</a>';

                    if (status == 2 && status != 3) {
                        dom += '<a href="/admin/essay/stick/' + data + '"class="btn btn-sm btn-info m-r-5">置顶</a>';
                    }
                    if (status == 1) {
                        dom += '<a href="/admin/essay/publish/' + data + '" class="btn btn-sm btn-warning m-r-5 publish">发布</a>';
                    }
                    return dom;
                }
            }],
        "fnInitComplete": function () {
            $tableFilter = $("#data-table_filter");
            $tableFilter.empty();
            $tableFilter.append(hi.hiElements.getSearchButton("查询", "btnSearch"));
            $tableFilter.append(hi.hiElements.getSearchText("文章标题", "search"));
            $tableFilter.append(hi.hiElements.getSearchSelect("分类", "hiClassify", searchArray));
            $('#btnSearch').on('click', function () {
                oTable.fnFilter($('#search').val(), $('#hiClassify').val());
            });
        },
        // 查询时，需要传递到服务端的参数
        "fnServerParams": function (aoData) {
            aoData.push({"name": "search", "value": $('#search').val()});
            aoData.push({"name": "classifyId", "value": $('#hiClassify').val() == null ? 0 : $('#hiClassify').val()});
        }
    });

    $("#data-table").on("click", ".publish", function (e) {
        e.preventDefault();
        var url = $(this).attr("href");
        hi.hiConfirm("是否发布文章？", function () {
            $.post(url, function (data) {
                hi.himan(data.info);
                if (data.status == 1) {
                    oTable.fnDraw();
                }
            });
        });
    });
});