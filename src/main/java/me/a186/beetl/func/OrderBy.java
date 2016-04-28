package me.a186.beetl.func;

import me.a186.constant.ConstantWebContext;
import me.a186.dto.SplitPage;
import org.apache.log4j.Logger;
import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * 分页列表排序函数
 * Created by Punk on 2016/2/21.
 */
public class OrderBy implements Function {
    private static Logger log = Logger.getLogger(OrderBy.class);

    /**
     * 分页列表排序
     */
    @Override
    public Object call(Object[] paras, Context ctx) {
        if (paras.length != 1 || null == paras[0]) {
            log.error("分页列表排序标签，参数不正确");
            return "";
        }

        String orderLaber = null;//排序列
        String orderColumn = null;//排序条件
        String orderMode = null;//排序方式

        try {
            orderLaber = (String) paras[0];
            SplitPage splitPage = (SplitPage) ctx.getGlobal("splitPage");

            orderColumn = splitPage.getOrderColumn();
            orderMode = splitPage.getOrderMode();
        } catch (Exception e) {
            log.error("分页列表排序标签，获取参数异常：" + e.getMessage());
            return "";
        }

        log.debug("分页列表排序，orderLaber=" + orderLaber + "， orderColumn=" + orderColumn + "，orderMode= " + orderMode);

        if (null != orderMode && orderLaber.equals(orderColumn)) {
            String cxt = (String) ctx.getGlobal(ConstantWebContext.request_cxt);
            if (orderMode.equals("asc")) {
                return "sorting_asc";
            } else if (orderMode.equals("desc")) {
                return "sorting_desc";
            }
        }
        return "sorting";
    }
}
