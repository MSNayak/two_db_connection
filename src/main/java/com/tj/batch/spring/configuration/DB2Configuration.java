package com.tj.batch.spring.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({ "com.tj.batch.spring.configuration" })
@PropertySource(value = { "classpath:db.properties" })
public class DB2Configuration {

	@Autowired
	private Environment environment;
    
	@Bean(name = "db2SessionFactory")
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory
				.setPackagesToScan(new String[] { "com.tj.batch.spring.model" });
		sessionFactory.setHibernateProperties(hibernateProperties());
		return sessionFactory;
	}

	@Bean(name = "db2DataSource")
	public DataSource dataSource() {
		String[] urls = environment.getRequiredProperty("db2.urls").split(",");
		String[] usernames = environment.getRequiredProperty("db2.usernames")
				.split(",");
		String[] passwords = environment.getRequiredProperty("db2.passwords")
				.split(",");
		String driverClassName = environment
				.getRequiredProperty("db2.driverClassName");
		RoutingDataSource router = new RoutingDataSource();
		Map<Object, Object> targetDataSources = new HashMap<Object, Object>();

		for (int i = 0; i < urls.length; i++) {
			String url = urls[i];
			String[] split = url.split("=");
			String key = split[0];
			String jdbcUrl = split[1];
			DataSource ds = prepareDS(jdbcUrl, usernames[i], passwords[i],
					driverClassName);

			System.out
					.println("RoutingDataSource>>> Putting datasource for lookup key="
							+ key);
			targetDataSources.put(key, ds);
		}
		// default ds
		router.setDefaultTargetDataSource(targetDataSources
				.get(MyDataSources.CAND));
		router.setTargetDataSources(targetDataSources);
		return router;
	}

	private DataSource prepareDS(String jdbcUrl, String username,
			String password, String driverClassName) {
		BasicDataSource s = new BasicDataSource();
		s.setUsername(username);
		s.setPassword(password);
		s.setUrl(jdbcUrl);
		s.setDriverClassName(driverClassName);
		return s;
	}

	private Properties hibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect",
				environment.getRequiredProperty("db2.dialect"));
		properties.put("hibernate.show_sql",
				environment.getRequiredProperty("db2.show_sql"));
		properties.put("hibernate.format_sql",
				environment.getRequiredProperty("db2.format_sql"));
		// properties.put("hibernate.hbm2ddl.auto",
		//environment.getRequiredProperty("db2.hbm2ddl.auto"));
		/* properties.put("hibernate.current_session_context_class", "thread"); */
		return properties;
	}

	@Bean(name = "db2Txn")
	@Autowired
	public HibernateTransactionManager transactionManager(
			@Qualifier("db2SessionFactory") SessionFactory s) {
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(s);
		return txManager;
	}
	
	/*@Bean(name = "db2AwareTxn")
	@Autowired
	public TransactionAwareDataSourceProxy awareTransactionManager(
			@Qualifier("db2DataSource") DataSource targetDataSource) {
		TransactionAwareDataSourceProxy txManager = new TransactionAwareDataSourceProxy();
		txManager.setTargetDataSource(targetDataSource);
		
		return txManager;
	}
	
	@Bean(name = "db2CustomTxn")
	@Autowired
	public DataSourceTransactionManager dsTransactionManager(
			@Qualifier("db2AwareTxn") TransactionAwareDataSourceProxy proxy) {
		DataSourceTransactionManager dsTxManager = new DataSourceTransactionManager();
		dsTxManager.setDataSource(proxy);
		return dsTxManager;
	}*/
	/*@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}*/
}
