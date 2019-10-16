package cn.xr.service.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class JPAServiceNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("service-scan", new JPAServiceScanBeanDefinitionParser());
	}

}
