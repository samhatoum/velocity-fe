package com.hatoum.velocity.framework;

public interface IPlugin {

	IAccessBean getAccessBean();

	void updateBean();

	void kill();
}
