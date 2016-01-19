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
public class MysqlConfiguration {

    @Autowired
    private Environment environment;

    @Bean(name="mysqlSessionFactory")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(new String[] { "com.tj.batch.spring.model" });
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
     }
	
    @Bean(name="mysqlDataSource")
    public DataSource dataSource() {
    	String[] urls = environment.getRequiredProperty("mysql.urls").split(",");
		String[] usernames = environment.getRequiredProperty("mysql.usernames")
				.split(",");
		String[] passwords = environment.getRequiredProperty("mysql.passwords")
				.split(",");
		String driverClassName = environment
				.getRequiredProperty("mysql.driverClassName");
		MySqlRoutingDataSource router = new MySqlRoutingDataSource();
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
				.get(MyDataSources.MYSQLEMP));
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
        properties.put("hibernate.dialect", environment.getRequiredProperty("mysql.dialect"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("mysql.show_sql"));
        properties.put("hibernate.format_sql", environment.getRequiredProperty("mysql.format_sql"));
       // properties.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("cand.hbm2ddl.auto"));
       /* properties.put("hibernate.current_session_context_class", "thread");*/
        return properties;        
    }
    
	@Bean(name="mysqlTxn")
    @Autowired
    public HibernateTransactionManager transactionManager(@Qualifier("mysqlSessionFactory")SessionFactory s) {
       HibernateTransactionManager txManager = new HibernateTransactionManager();
       txManager.setSessionFactory(s);
       return txManager;
    }
}


