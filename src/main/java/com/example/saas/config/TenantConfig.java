package com.example.saas.config;
import com.example.saas.tenant.TenantIdentifierResolver;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import com.example.saas.tenant.SchemaMultiTenantConnectionProvider;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class TenantConfig {

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            SchemaMultiTenantConnectionProvider connectionProvider,
            TenantIdentifierResolver tenantResolver) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.example.saas.entity");
        em.setJpaVendorAdapter(jpaVendorAdapter());

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
        jpaProperties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);
        jpaProperties.put(Environment.FORMAT_SQL, true);

        em.setJpaPropertyMap(jpaProperties);
        return em;
    }
}