package me.a186.mvc.model.base;

import me.a186.mvc.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by PunkVv, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseAuthGroupModules<M extends BaseAuthGroupModules<M>> extends BaseModel<M> implements IBean {

	public void setIds(java.lang.String ids) {
		set("ids", ids);
	}

	public java.lang.String getIds() {
		return get("ids");
	}

	public void setGroupIds(java.lang.String groupIds) {
		set("groupIds", groupIds);
	}

	public java.lang.String getGroupIds() {
		return get("groupIds");
	}

	public void setModulesIds(java.lang.String modulesIds) {
		set("modulesIds", modulesIds);
	}

	public java.lang.String getModulesIds() {
		return get("modulesIds");
	}

}