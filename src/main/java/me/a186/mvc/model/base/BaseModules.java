package me.a186.mvc.model.base;

import me.a186.mvc.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by PunkVv, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseModules<M extends BaseModules<M>> extends BaseModel<M> implements IBean {

	public void setIds(java.lang.String ids) {
		set("ids", ids);
	}

	public java.lang.String getIds() {
		return get("ids");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setTitle(java.lang.String title) {
		set("title", title);
	}

	public java.lang.String getTitle() {
		return get("title");
	}

	public void setLevel(java.lang.String level) {
		set("level", level);
	}

	public java.lang.String getLevel() {
		return get("level");
	}

	public void setPids(java.lang.String pids) {
		set("pids", pids);
	}

	public java.lang.String getPids() {
		return get("pids");
	}

	public void setUrl(java.lang.String url) {
		set("url", url);
	}

	public java.lang.String getUrl() {
		return get("url");
	}

}
