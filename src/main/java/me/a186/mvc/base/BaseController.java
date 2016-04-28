package me.a186.mvc.base;

import com.jfinal.core.Controller;
import com.jfinal.render.JsonRender;
import me.a186.constant.ConstantWebContext;
import me.a186.dto.SplitPage;
import me.a186.mvc.index.IndexService;
import me.a186.mvc.model.Syslog;
import me.a186.mvc.model.User;
import me.a186.plugin.I18NPlugin;
import me.a186.tools.ToolString;
import me.a186.tools.ToolWeb;
import org.apache.log4j.Logger;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公共Controller
 * Created by Punk on 2016/2/25.
 */
public class BaseController extends Controller {
    private static Logger log = Logger.getLogger(BaseController.class);

    /**
     * 全局变量
     */
    protected String ids;            // 主键
    protected SplitPage splitPage;    // 分页封装
    protected List<?> list;            // 公共list
    protected Syslog reqSysLog;//访问日志

    /**
     * 分页
     *
     * @param dataSource
     * @param selectSqlId
     * @param fromSqlId
     */
    protected void paging(String dataSource, String selectSqlId, String fromSqlId) {
        splitPage();
        BaseService.service.paging(dataSource, splitPage, selectSqlId, fromSqlId);
    }

    /**
     * 分页初始化
     */
    private void splitPage() {
        splitPage = new SplitPage();
        //分页查询参数分拣
        Map<String, Object> queryParam = new HashMap<>();
        String localePram = getAttr(ConstantWebContext.request_localePram);
        queryParam.put(ConstantWebContext.request_localePram, localePram);// 设置国际化当前语言环境
        queryParam.put(ConstantWebContext.request_i18nColumnSuffix, I18NPlugin.columnSuffix(localePram)); // 设置国际化动态列后缀
        Enumeration<String> paramNames = getParaNames();
        String name = null;
        String value = null;
        String key = null;
        while (paramNames.hasMoreElements()) {
            name = paramNames.nextElement();
            value = getPara(name);
            if (name.startsWith(ConstantWebContext.request_query) && null != value && !value.trim().isEmpty()) {// 查询参数分拣
                log.debug("分页，查询参数：name = " + name + " value = " + value);
                key = name.substring(7);
                if (ToolString.regExpVali(key, ToolString.regExp_letter_5)) {
                    queryParam.put(key, value.trim());
                } else {
                    log.error("分页，查询参数存在恶意提交字符：name = " + name + " value = " + value);
                }
            }
        }
        splitPage.setQueryParam(queryParam);

        String orderColumn = getPara(ConstantWebContext.request_orderColumn);// 排序条件
        if (null != orderColumn && !orderColumn.isEmpty()) {
            log.debug("分页，排序条件：orderColumn = " + orderColumn);
            splitPage.setOrderColumn(orderColumn);
        }

        String orderMode = getPara(ConstantWebContext.request_orderMode);// 排序方式
        if (null != orderMode && !orderMode.isEmpty()) {
            log.debug("分页，排序方式：orderMode = " + orderMode);
            splitPage.setOrderMode(orderMode);
        }

        String pageNumber = getPara(ConstantWebContext.request_pageNumber);// 第几页
        if (null != pageNumber && !pageNumber.isEmpty()) {
            log.debug("分页，第几页：pageNumber = " + pageNumber);
            splitPage.setPageNumber(Integer.parseInt(pageNumber));
        }

        String pageSize = getPara(ConstantWebContext.request_pageSize);// 每页显示几多
        if (null != pageSize && !pageSize.isEmpty()) {
            log.debug("分页，每页显示几多：pageSize = " + pageSize);
            splitPage.setPageSize(Integer.parseInt(pageSize));
        }
    }

    /**
     * 根据当前国际化查询字段扩展名
     *
     * @return
     */
    protected String geti18nColumnSuffix() {
        String localePram = getAttr(ConstantWebContext.request_localePram);
        return I18NPlugin.columnSuffix(localePram);
    }

    /**
     * 请求/WEB-INF/下的视图文件
     */
    public void toUrl() {
        String toUrl = getPara(ConstantWebContext.request_toUrl);
        render(toUrl);
    }

    /**
     * 重写getPara，进行二次decode解码
     */
    @Override
    public String getPara(String name) {
        if ("GET".equalsIgnoreCase(getRequest().getMethod().toUpperCase())) {
            return ToolWeb.getParam(getRequest(), name);
        }
        return super.getPara(name);
    }

    /**
     * 重写renderJson，避免出现IE8下出现下载弹出框
     */
    @Override
    public void renderJson(Object object) {
        String userAgent = getRequest().getHeader("User-Agent");
        if (userAgent.toLowerCase().indexOf("msie") != -1) {
            render(new JsonRender(object).forIE());
        } else {
            super.renderJson(object);
        }
    }

    /**
     * 获取项目请求根路径
     *
     * @return
     */
    protected String getCxt() {
        return getAttr(ConstantWebContext.request_cxt);
    }

    /**
     * 获取用户拥有有权限
     *
     * @return
     */
    public List<String> getAuthList() {
        List<String> authList = getSessionAttr("authList");
        if (authList == null || authList.size() == 0) {
            authList = IndexService.service.getAuthList(getCUser().getIds());//获取用户操作权限列表
            setSessionAttr("authList", authList);
        }
        return authList;
    }


    /**
     * 获取当前用户
     *
     * @return
     */
    protected User getCUser() {
        return getAttr(ConstantWebContext.request_cUser);
    }

    public Syslog getReqSysLog() {
        return reqSysLog;
    }

    public void setReqSysLog(Syslog reqSysLog) {
        this.reqSysLog = reqSysLog;
    }

    public void setSplitPage(SplitPage splitPage) {
        this.splitPage = splitPage;
    }
}
