/*
 * Copyright (c) 2008-2017 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.haulmont.multitenancy;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A proxy data source routing to real data sources depending on an attribute present in the current user session.
 */
public class TenantsRoutingDatasource extends AbstractDataSource implements InitializingBean, ApplicationContextAware {

    protected Map<String, DataSource> dataSources;
    protected DataSource defaultDataSource;

    protected String jndiNameAppProperty;
    protected String urlPrefix;
    protected String defaultDbAddress;
    protected ApplicationContext applicationContext;
    protected String tenantDataSourceBeanName;
    protected String sessionAttributeName;

    private Logger log = LoggerFactory.getLogger(TenantsRoutingDatasource.class);

    public String getJndiNameAppProperty() {
        return jndiNameAppProperty;
    }

    public void setJndiNameAppProperty(String jndiNameAppProperty) {
        this.jndiNameAppProperty = jndiNameAppProperty;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public String getDefaultDbAddress() {
        return defaultDbAddress;
    }

    public void setDefaultDbAddress(String defaultDbAddress) {
        this.defaultDbAddress = defaultDbAddress;
    }

    public String getTenantDataSourceBeanName() {
        return tenantDataSourceBeanName;
    }

    public void setTenantDataSourceBeanName(String tenantDataSourceBeanName) {
        this.tenantDataSourceBeanName = tenantDataSourceBeanName;
    }

    public String getSessionAttributeName() {
        return sessionAttributeName;
    }

    public void setSessionAttributeName(String sessionAttributeName) {
        this.sessionAttributeName = sessionAttributeName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dataSources = new ConcurrentHashMap<>();
        defaultDataSource = createDataSource(defaultDbAddress);

        try {
            Context context = new InitialContext();
            String path = AppContext.getProperty(jndiNameAppProperty);
            if (path == null)
                throw new IllegalStateException("Property " + jndiNameAppProperty + " is not set");
            context.bind(path, this);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected DataSource createDataSource(String dbAddress) {
        log.info("Creating datasource for {}", dbAddress);
        BasicDataSource dataSource = (BasicDataSource) applicationContext.getBean(tenantDataSourceBeanName);
        dataSource.setUrl(urlPrefix + dbAddress);
        return dataSource;
    }

    protected String determineCurrentLookupKey() {
        UserSessionSource uss = AppBeans.get(UserSessionSource.class);
        if (uss.checkCurrentUserSession()) {
            if (sessionAttributeName == null)
                throw new IllegalStateException("sessionAttributeName is not set");
            String dbAddress = uss.getUserSession().getAttribute(sessionAttributeName);
            if (dbAddress != null) {
                return dbAddress;
            }
        }
        return null;
    }

    protected DataSource determineTargetDataSource() {
        String key = determineCurrentLookupKey();
        if (key == null) {
            log.debug("Using default DB");
            return defaultDataSource;
        }
        log.debug("Using DB address: {}", key);
        return dataSources.computeIfAbsent(key, this::createDataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return determineTargetDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return determineTargetDataSource().getConnection(username, password);
    }
}
