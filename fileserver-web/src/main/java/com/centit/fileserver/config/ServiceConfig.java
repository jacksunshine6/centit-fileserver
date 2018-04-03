package com.centit.fileserver.config;

import com.centit.fileserver.service.FileStoreFactory;
import com.centit.fileserver.service.impl.FileStoreFactoryImpl;
import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.components.impl.TextOperationLogWriterImpl;
import com.centit.framework.core.config.DataSourceConfig;
import com.centit.framework.hibernate.config.HibernateConfig;
import com.centit.framework.ip.app.config.IPAppSystemBeanConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.config.SpringSecurityDaoConfig;
import com.centit.search.document.FileDocument;
import com.centit.search.service.Indexer;
import com.centit.search.service.IndexerSearcherFactory;
import org.springframework.context.annotation.*;

/**
 * Created by codefan on 17-7-18.
 */
@ComponentScan(basePackages = "com.centit",
        excludeFilters = @ComponentScan.Filter(value = org.springframework.stereotype.Controller.class))
@Import({SpringSecurityDaoConfig.class,
        IPAppSystemBeanConfig.class,
        DataSourceConfig.class,
        HibernateConfig.class})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ServiceConfig {
   /* @Bean
    @Lazy(value = false)
    public IntegrationEnvironment integrationEnvironment() {
        return new DummyIntegrationEnvironment();
    }*/

    @Bean
    public FileStoreFactory fileStoreFactory() {
        return new FileStoreFactoryImpl();
    }

    @Bean
    public Indexer documentIndexer(){
        return IndexerSearcherFactory.obtainIndexer(
                IndexerSearcherFactory.loadESServerConfigFormProperties(
                        SysParametersUtils.loadProperties()), FileDocument.class);
    }

    @Bean
    public NotificationCenter notificationCenter() {
        NotificationCenterImpl notificationCenter = new NotificationCenterImpl();
        notificationCenter.initMsgSenders();
        //notificationCenter.registerMessageSender("innerMsg",innerMessageManager);
        return notificationCenter;
    }

    @Bean
    @Lazy(value = false)
    public OperationLogWriter operationLogWriter() {
        TextOperationLogWriterImpl operationLog = new TextOperationLogWriterImpl();
        operationLog.init();
        return operationLog;
    }

    @Bean
    public InstantiationServiceBeanPostProcessor instantiationServiceBeanPostProcessor() {
        return new InstantiationServiceBeanPostProcessor();
    }
}

