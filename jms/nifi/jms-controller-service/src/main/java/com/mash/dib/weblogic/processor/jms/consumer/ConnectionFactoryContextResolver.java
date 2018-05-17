package com.mash.dib.weblogic.processor.jms.consumer;

import org.apache.nifi.controller.ConfigurationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.Hashtable;

import static com.mash.dib.weblogic.processor.jms.consumer.JMSJndiConnectionFactoryProvider.*;

public class ConnectionFactoryContextResolver {

    private final static Logger lof = LoggerFactory.getLogger(ConnectionFactoryContextResolver.class);

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


    private final class ConnectionFactoryWrapper implements ConnectionFactory {

        private ConnectionFactory factory;

        @Override
        public Connection createConnection() throws JMSException {
            return new ConnectionWrapper(factory.createConnection());
        }

        @Override
        public Connection createConnection(String s, String s1) throws JMSException {
            return new ConnectionWrapper(factory.createConnection(s, s1));
        }

        @Override
        public JMSContext createContext() {
            return factory.createContext();
        }

        @Override
        public JMSContext createContext(String s, String s1) {
            return factory.createContext(s, s1);
        }

        @Override
        public JMSContext createContext(String s, String s1, int i) {
            return factory.createContext(s, s1, i);
        }

        @Override
        public JMSContext createContext(int i) {
            return factory.createContext(i);
        }
    }

    private final class ConnectionWrapper implements Connection {

        private final Connection innerConnection;

        private ConnectionWrapper(Connection innerConnection) {
            this.innerConnection = innerConnection;
        }


        @Override
        public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
            return new SessionJNDIResolver(innerConnection.createSession(transacted, acknowledgeMode));
        }

        @Override
        public Session createSession(int sessionMode) throws JMSException {
            return new SessionJNDIResolver(innerConnection.createSession(sessionMode));
        }

        @Override
        public Session createSession() throws JMSException {
            return new SessionJNDIResolver(innerConnection.createSession());
        }

        @Override
        public String getClientID() throws JMSException {
            return innerConnection.getClientID();
        }

        @Override
        public void setClientID(String clientID) throws JMSException {
            innerConnection.setClientID(clientID);
        }

        @Override
        public ConnectionMetaData getMetaData() throws JMSException {
            return innerConnection.getMetaData();
        }

        @Override
        public ExceptionListener getExceptionListener() throws JMSException {
            return innerConnection.getExceptionListener();
        }

        @Override
        public void setExceptionListener(ExceptionListener listener) throws JMSException {
            innerConnection.setExceptionListener(listener);
        }

        @Override
        public void start() throws JMSException {
            innerConnection.start();
        }

        @Override
        public void stop() throws JMSException {
            innerConnection.stop();
        }

        @Override
        public void close() throws JMSException {
            innerConnection.close();
        }

        @Override
        public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
            return innerConnection.createConnectionConsumer(destination, messageSelector, sessionPool, maxMessages);
        }

        @Override
        public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
            return innerConnection.createSharedConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
        }

        @Override
        public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
            return innerConnection.createDurableConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
        }

        @Override
        public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
            return innerConnection.createSharedConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
        }
    }


    private final class SessionJNDIResolver implements Session {

        private final Session session;

        private SessionJNDIResolver(Session session) {
            this.session = session;
        }


        @Override
        public BytesMessage createBytesMessage() throws JMSException {
            return session.createBytesMessage();
        }

        @Override
        public MapMessage createMapMessage() throws JMSException {
            return session.createMapMessage();
        }

        @Override
        public Message createMessage() throws JMSException {
            return session.createMessage();
        }

        @Override
        public ObjectMessage createObjectMessage() throws JMSException {
            return session.createObjectMessage();
        }

        @Override
        public ObjectMessage createObjectMessage(Serializable object) throws JMSException {
            return session.createObjectMessage(object);
        }

        @Override
        public StreamMessage createStreamMessage() throws JMSException {
            return session.createStreamMessage();
        }

        @Override
        public TextMessage createTextMessage() throws JMSException {
            return session.createTextMessage();
        }

        @Override
        public TextMessage createTextMessage(String text) throws JMSException {
            return session.createTextMessage(text);
        }

        @Override
        public boolean getTransacted() throws JMSException {
            return session.getTransacted();
        }

        @Override
        public int getAcknowledgeMode() throws JMSException {
            return session.getAcknowledgeMode();
        }

        @Override
        public void commit() throws JMSException {
            session.commit();
        }

        @Override
        public void rollback() throws JMSException {
            session.rollback();
        }

        @Override
        public void close() throws JMSException {
            session.close();
        }

        @Override
        public void recover() throws JMSException {
            session.recover();
        }

        @Override
        public MessageListener getMessageListener() throws JMSException {
            return session.getMessageListener();
        }

        @Override
        public void setMessageListener(MessageListener listener) throws JMSException {
            session.setMessageListener(listener);
        }

        @Override
        public void run() {
            session.run();
        }

        @Override
        public MessageProducer createProducer(Destination destination) throws JMSException {
            return session.createProducer(destination);
        }

        @Override
        public MessageConsumer createConsumer(Destination destination) throws JMSException {
            return session.createConsumer(destination);
        }

        @Override
        public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException {
            return session.createConsumer(destination, messageSelector);
        }

        @Override
        public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal) throws JMSException {
            return session.createConsumer(destination, messageSelector, noLocal);
        }

        @Override
        public MessageConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName) throws JMSException {
            return session.createSharedConsumer(topic, sharedSubscriptionName);
        }

        @Override
        public MessageConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName, String messageSelector) throws JMSException {
            return session.createSharedConsumer(topic, sharedSubscriptionName, messageSelector);
        }

        @Override
        public Queue createQueue(String queueName) throws JMSException {
            InitialContext context = null;
            try {
                context = new InitialContext(environment);
                return (Queue) context.lookup(queueName);
            } catch (NamingException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (context != null) {
                        context.close();
                    }
                } catch (NamingException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public Topic createTopic(String topicName) throws JMSException {
            InitialContext context = null;
            try {
                context = new InitialContext(environment);
                return (Topic) context.lookup(topicName);
            } catch (NamingException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (context != null) {
                        context.close();
                    }
                } catch (NamingException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        @Override
        public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
            return session.createDurableSubscriber(topic, name);
        }

        @Override
        public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException {
            return session.createDurableSubscriber(topic, name, messageSelector, noLocal);
        }

        @Override
        public MessageConsumer createDurableConsumer(Topic topic, String name) throws JMSException {
            return session.createDurableConsumer(topic, name);
        }

        @Override
        public MessageConsumer createDurableConsumer(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException {
            return session.createDurableConsumer(topic, name, messageSelector, noLocal);
        }

        @Override
        public MessageConsumer createSharedDurableConsumer(Topic topic, String name) throws JMSException {
            return session.createSharedDurableConsumer(topic, name);
        }

        @Override
        public MessageConsumer createSharedDurableConsumer(Topic topic, String name, String messageSelector) throws JMSException {
            return session.createSharedDurableConsumer(topic, name, messageSelector);
        }

        @Override
        public QueueBrowser createBrowser(Queue queue) throws JMSException {
            return session.createBrowser(queue);
        }

        @Override
        public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException {
            return session.createBrowser(queue, messageSelector);
        }

        @Override
        public TemporaryQueue createTemporaryQueue() throws JMSException {
            return session.createTemporaryQueue();
        }

        @Override
        public TemporaryTopic createTemporaryTopic() throws JMSException {
            return session.createTemporaryTopic();
        }

        @Override
        public void unsubscribe(String name) throws JMSException {
            session.unsubscribe(name);
        }
    }


}