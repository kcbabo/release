package org.switchyard.as7.extension.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jboss.as.naming.context.NamespaceContextSelector;
import org.jboss.logging.Logger;
import org.switchyard.ServiceDomain;

public class NamespaceContextProcessor implements Processor {
	
	private static final String NS_CONTEXT_PROP = "org.switchyard.component.camel.contextPushed";
	private static final Logger LOG = 
			Logger.getLogger(NamespaceContextProcessor.class);
	
	private NamespaceContextSelector _selector;
	
	public NamespaceContextProcessor(NamespaceContextSelector selector) {
		_selector = selector;
	}
	
	public void setDomain(ServiceDomain domain) {
		domain.setProperty("NamespaceContextProcessor", this);
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		if (exchange.getProperty(NS_CONTEXT_PROP) == null) {
			before(exchange);
		} else {
			after(exchange);
		}
	}
	
	private void before(Exchange exchange) {
		if (_selector != null) {
			LOG.infof("**** Pushing namespace context %s for exchange %s", _selector, exchange.getExchangeId());
			NamespaceContextSelector.pushCurrentSelector(_selector);
			exchange.setProperty(NS_CONTEXT_PROP, true);
		}
		
	}
	
	private void after(Exchange exchange) {
		NamespaceContextSelector selector = NamespaceContextSelector.getCurrentSelector();
		LOG.infof("**** Popping namespace context %s for exchange %s", _selector, exchange.getExchangeId());
		if (selector != _selector) {
			LOG.errorf("Namespace contexts do not match for exchange: %s and %s", selector, _selector);
		}
		if (selector != null) {
			NamespaceContextSelector.popCurrentSelector();
		}
	}

}
