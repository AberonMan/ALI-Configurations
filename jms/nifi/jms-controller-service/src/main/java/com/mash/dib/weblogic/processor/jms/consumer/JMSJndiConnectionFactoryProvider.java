package com.mash.dib.weblogic.processor.jms.consumer;

import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnDisabled;
import org.apache.nifi.annotation.lifecycle.OnEnabled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.Validator;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.jms.cf.JMSConnectionFactoryProviderDefinition;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.reporting.InitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.Serializable;
import java.lang.IllegalStateException;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;


@Tags({"jms", "messaging", "integration", "queue", "topic", "publish", "subscribe", "jndi"})
@CapabilityDescription("Provides a generic service to get Connection Factory from JNDI Tree")
@SeeAlso(
        classNames = {"org.apache.nifi.jms.processors.ConsumeJMS", "org.apache.nifi.jms.processors.PublishJMS"}
)
public class JMSJndiConnectionFactoryProvider extends AbstractControllerService implements JMSConnectionFactoryProviderDefinition {

    private static final List<PropertyDescriptor> PROPERTY_DESCRIPTORS;
    private static final Logger logger = LoggerFactory.getLogger(JMSJndiConnectionFactoryProvider.class);


    public static final PropertyDescriptor CONNECTION_FACTORY_IMPL;
    public static final PropertyDescriptor CONNECTION_FACTORY_JNDI_NAME;
    public static final PropertyDescriptor PROVIDER_URL;
    public static final PropertyDescriptor LOGIN;
    public static final PropertyDescriptor PASSWORD;
    public static final PropertyDescriptor INITIAL_CONTEXT_FACTORY;
    private volatile boolean configured;
    private volatile ConnectionFactory connectionFactory;


    @Override
    public ConnectionFactory getConnectionFactory() {
        if (this.configured) {
            return this.connectionFactory;
        } else {
            throw new IllegalStateException("ConnectionFactory can not be obtained unless this ControllerService is configured. See onConfigure(ConfigurationContext) method.");
        }
    }


    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }


    @OnEnabled
    public void enable(ConfigurationContext context) throws InitializationException {
        try {
            if (!this.configured) {
                if (logger.isInfoEnabled()) {
                    logger.info("Configure JMS Connection Factory  JNDI Name: {}, JNDI Server URL",
                            context.getProperty(CONNECTION_FACTORY_JNDI_NAME).getValue(), context.getProperty(PROVIDER_URL).getValue());
                }

                this.createConnectionFactoryInstance(context);
            }

            this.configured = true;
        } catch (Exception var3) {
            logger.error("Failed to configure " + this.getClass().getSimpleName(), var3);
            this.configured = false;
            throw new IllegalStateException(var3);
        }
    }

    @OnDisabled
    public void disable() {
        this.connectionFactory = null;
        this.configured = false;
    }

    private void createConnectionFactoryInstance(ConfigurationContext context) throws Exception {
        ConnectionFactoryContextResolver resolver = new ConnectionFactoryContextResolver(context);
        this.connectionFactory = resolver.resolveConnectionFactory();
    }


    static {
        CONNECTION_FACTORY_IMPL = new PropertyDescriptor.Builder().name("cf")
                .displayName("MQ ConnectionFactory Implementation")
                .description("A fully qualified name of the JMS ConnectionFactory implementation class (i.e., org.apache.activemq.ActiveMQConnectionFactory)")
                .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
                .required(true)
                .expressionLanguageSupported(true).build();

        CONNECTION_FACTORY_JNDI_NAME = new PropertyDescriptor.Builder()
                .name("cfjndi")
                .displayName("Connection Factory JNDI Name")
                .description("JNDI Name of Connection Factory")
                .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
                .required(true)
                .expressionLanguageSupported(true)
                .build();


        PROVIDER_URL = new PropertyDescriptor.Builder()
                .name("JNDI Provider URL")
                .description("URL to remote jndi server. It should be specified if you want to get connection factory from remote destination")
                .required(false)
                .expressionLanguageSupported(true)
                .addValidator(Validator.VALID)
                .build();

        LOGIN = new PropertyDescriptor.Builder()
                .name("Login")
                .description("Login to remote JNDI server")
                .required(false)
                .expressionLanguageSupported(true)
                .addValidator(Validator.VALID)
                .build();

        PASSWORD = new PropertyDescriptor.Builder()
                .name("Password")
                .description("Password to JNDI server")
                .required(false)
                .sensitive(true)
                .addValidator(Validator.VALID)
                .build();

        INITIAL_CONTEXT_FACTORY = new PropertyDescriptor.Builder()
                .name("Context Class Name")
                .description("Context Factory Full Class Name, e.g. weblogic.jndi.WLInitialContextFactory ")
                .required(false)
                .addValidator(Validator.VALID)
                .build();


        PROPERTY_DESCRIPTORS = unmodifiableList(asList(
                CONNECTION_FACTORY_IMPL,
                CONNECTION_FACTORY_JNDI_NAME,
                INITIAL_CONTEXT_FACTORY,
                PROVIDER_URL,
                LOGIN,
                PASSWORD
        ));
    }






}
