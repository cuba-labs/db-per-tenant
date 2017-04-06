# Sample Database-per-tenant Application

## Overview

This application demonstrates an approach to implement a multi-tenant application with separate databases containing domain data for each tenant. Information about users and security settings are stored in a shared database. After a user logs in, the application determines a tenant database for the user and all subsequent requests for domain entities go to this database. 

The implementation is based on the following:

* Domain entities are mapped to tables located in an additional data store.

* The additional data store is connected to a special routing data source. It determines a database address by a user session attribute, dynamically creates real data sources and dispatches requests to them.

## Development Approach

* Start a new project and create your data model and UI working with the single main database. In this example the main database is at `jdbc:postgresql://localhost/dbpt_main` URL.

* When your data model is finished, create an additional data store. For example, `tenant` at `jdbc:postgresql://localhost/dbpt_tenant` URL.

* Open the project in the IDE and move all your persistent classes from `persistence.xml` to `tenant-persistence.xml`.

* Create databases for your tenants, e.g. `dbpt_tenant1` and `dbpt_tenant2` and execute SQL scripts from the `modules/core/db/init` folder on them.
 
* Now all your persistent entities belong to the additional data store, so when you start the application server next time you'll get a warning about incompatibility between your data model (now using additional data store) and main database still containing tables for entities. Select the *Do not show this dialog again* checkbox and continue by pressing *Run server*.

* At this point a user will not be able to work with your entities in the application because your additional data store points to non-existent database `dbpt_tenant` (rememeber, you have created `dbpt_tenant1` and `dbpt_tenant2`). So when opening an entity screen, the user will get an exeption like `PSQLException: FATAL: database "dbpt_tenant" does not exist`.
  
* Now you should introduce routing of requests to appropriate databases - see the next section.

## Introducing Routing

* In Studio, edit *Project properties*, go to *Advanced* tab and add new dependency for *Core Module*: `org.apache.commons:commons-dbcp2:2.1.1`. After closing the page Studio re-creates IDE project files. 

* Open the project in IDE. Create `com.haulmont.multitenancy` package in your project's `core` module and copy `TenantsRoutingDatasource` class from this sample to it.

* Open the `spring.xml` file of the `core` module and add the following:

    ```
    <!-- Routing datasource to be used for additional data store -->
    <bean id="routingDataSource"
          class="com.haulmont.multitenancy.TenantsRoutingDatasource">
        
        <!-- Name of the app property that contains JNDI name of the additional data store -->  
        <property name="jndiNameAppProperty"
                  value="cuba.dataSourceJndiName_tenant"/>
        
        <!-- This prefix will be added to each database address passed to the routing data source.
            Effectively defines a JDBC driver to use -->
        <property name="urlPrefix"
                  value="jdbc:postgresql://"/>
                  
        <!-- Database that will be used if no information about user's database is provided -->         
        <property name="defaultDbAddress"
                  value="localhost/dbpt_main"/>
        
        <!-- Name of a user session attribute containing user's database address -->
        <property name="sessionAttributeName"
                  value="tenantDbAddress"/>
        
        <!-- Name of a prototype bean that implements javax.sql.DataSource to route requests to -->         
        <property name="tenantDataSourceBeanName"
                  value="tenantDataSource"/>
    </bean>
    
    <!-- Prototype of a real javax.sql.DataSource implementation to be used by routing data source -->
    <bean id="tenantDataSource"
          class="org.apache.commons.dbcp2.BasicDataSource"
          scope="prototype">
        <property name="username" value="cuba"/>
        <property name="password" value="cuba"/>
        <property name="maxTotal" value="20"/>
    </bean>
    ```
* In the same `spring.xml` file, find the `entityManagerFactory_tenant` bean and add the `depends-on="routingDataSource"` attribute to it:

    ```
    <bean id="entityManagerFactory_tenant"
          class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean"
          lazy-init="false"
          depends-on="routingDataSource">    
    ```
    
    This is needed to make sure the routing data source is initialized before the entity manager factory.
    
* Open `app.properties` file of the `core` module and change the value of the `cuba.dataSourceJndiName_tenant` to something without "/", for example:
   
   ```
   cuba.dataSourceJndiName_tenant = routing_datasource
   ``` 
   
   Otherwise you can get `javax.naming.NameNotFoundException` exception on the server startup when the routing data source tries to bound itself to a JNDI context.
   
* Now you can start the server, log in as admin and try to work with your domain entities. Routing datasource now has no information about user's database, so it routes to the default database (see a relevant message in `app.log`) which we have defined as `localhost/dbpt_main` (main data store). If you haven't removed entity tables from the main data store, you can successfully work with your entities. However, they are now stored in the main database, which is not what we want.

* Open *Administration > Access Groups* screen and create the following structure of groups:

    ```
    Company
        Tenants
            Tenant 1
            Tenant 2
    ```

* For groups `Tenant 1` and `Tenant 2` create the following attributes on the *Session Attribute* tab:
        
    * Name: `tenantDbAddress`
    * Type: `String`
    * Value: `localhost/dbpt_tenant1` (or `localhost/dbpt_tenant2` correspondingly)
    
    Also, create users `u1` and `u2` in groups`Tenant 1` and `Tenant 2` correspondingly.
    
* Log in as user `u1` and open a domain entity browser. You will see the following messages in `app.log`:

    ```
    ...TenantsRoutingDatasource - Using DB address: localhost/dbpt_tenant1
    ...TenantsRoutingDatasource - Creating datasource for localhost/dbpt_tenant1
    ```
    Domain entities will be saved in the `dbpt_tenant1` database.
    
    If you log in as `u2`, you will work with the `dbpt_tenant2` database.
    
## Known Issues and Limitations    

* User settings (screen parameters like table column widths, splitter positions, etc.) and filters are stored in the main database.

* Optional platform mechanisms save data in the main database:

    * Dynamic attributes
    * Entity log
    * Entity snapshots
    
* If you use entities with `Long` or `Integer` keys (subclasses of `BaseLongIdEntity` or `BaseIntegerIdEntity`), sequences for the generation of keys will be created in the main database. It means that the key values will be sequential across all databases.

* More?