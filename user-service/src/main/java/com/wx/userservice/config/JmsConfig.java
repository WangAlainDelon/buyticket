package com.wx.userservice.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.TransactionAwareConnectionFactoryProxy;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.transaction.PlatformTransactionManager;

import javax.jms.ConnectionFactory;

/**
 * Created by mavlarn on 2018/1/26.
 */
@Configuration
public class JmsConfig {
    private static final Logger LOG = LoggerFactory.getLogger(JmsConfig.class);

    @Bean
    public ConnectionFactory collectionFactory() {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://47.100.92.103:61617");
        //Spring 提供的MQ的事务和数据库的事务做一个同步
        TransactionAwareConnectionFactoryProxy transactionAwareConnectionFactoryProxy = new TransactionAwareConnectionFactoryProxy();
        transactionAwareConnectionFactoryProxy.setTargetConnectionFactory(connectionFactory);
        transactionAwareConnectionFactoryProxy.setSynchedLocalTransactionAllowed(true);
        return transactionAwareConnectionFactoryProxy;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory, MessageConverter jacksonJmsMessageConverter) {
        LOG.debug("init jms template with converter.");
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(jacksonJmsMessageConverter);
        // JmsTemplate使用的connectionFactory跟JmsTransactionManager使用的必须是同一个，不能在这里封装成caching之类的。
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    // 这个用于设置 @JmsListener使用的containerFactory
    @Bean
    public JmsListenerContainerFactory<?> msgFactory(ConnectionFactory connectionFactory,
                                                     DefaultJmsListenerContainerFactoryConfigurer configurer,
                                                     PlatformTransactionManager transactionManager) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setTransactionManager(transactionManager);
//        factory.setCacheLevelName("CACHE_CONNECTION");
        factory.setReceiveTimeout(10000L);
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    /**
     * 收发消息自动将java对象转换成json数据。
     * @return
     */
    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter mappingJackson2MessageConverter =
                new MappingJackson2MessageConverter();
        mappingJackson2MessageConverter.setTargetType(MessageType.TEXT);
        mappingJackson2MessageConverter.setTypeIdPropertyName("_type");
        return mappingJackson2MessageConverter;
    }

//    @Bean
//    public PlatformTransactionManager transactionManager(ConnectionFactory connectionFactory) {
//        return new JmsTransactionManager(connectionFactory);
//    }

}
