package com.mash.dib.weblogic.processor.jms.consumer;

import org.apache.nifi.controller.ConfigurationContext;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

import static com.mash.dib.weblogic.processor.jms.consumer.JMSJndiConnectionFactoryProvider.*;

public class ConnectionFactoryContextResolver {

    private final Hashtable environment;

    private final String connectionFactoryJndiName;

    public ConnectionFactoryContextResolver(ConfigurationContext context) {
        Hashtable properties = new Hashtable();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, context.getProperty(INITIAL_CONTEXT_FACTORY).getValue());
        properties.put(Context.PROVIDER_URL, context.getProperty(PROVIDER_URL).getValue());
        properties.put(Context.SECURITY_PRINCIPAL, context.getProperty(LOGIN).getValue());
        properties.put(Context.SECURITY_CREDENTIALS, context.getProperty(PASSWORD).getValue());
        this.environment = properties;
        connectionFactoryJndiName = context.getProperty(CONNECTION_FACTORY_JNDI_NAME).getValue();
    }

    public ConnectionFactory resolveConnectionFactory() throws Exception {
        Context context = null;
        try {
            context = new InitialContext(environment);
            return (ConnectionFactory) context.lookup(connectionFactoryJndiName);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

}