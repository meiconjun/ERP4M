package org.meiconjun.erp4m.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.meiconjun.erp4m.interceptor.SpringContextHolder;
import org.meiconjun.erp4m.util.LogUtil;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: spring相关核心配置
 * @date 2021/1/26 22:56
 */
@Configuration
public class ApplicationContextConfig {
    private Logger logger = LogUtil.getPlatformLogger();
    /**
     * 数据库数据源配置
     * @return
     */
    @Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
    public DruidDataSource druidDataSource (@Value("${database-config.jdbc.driver}") String driver, @Value("${database-config.jdbc.url}") String url, @Value("${database-config.jdbc.username}") String username,
                                            @Value("${database-config.jdbc.password}") String password, @Value("${database-config.jdbc.filters}") String filters, @Value("${database-config.jdbc.maxActive}") int maxActive,
                                            @Value("${database-config.jdbc.initialSize}") int initialSize, @Value("${database-config.jdbc.maxWait}") int maxWait, @Value("${database-config.jdbc.minIdle}") int minIdle,
                                            @Value("${database-config.jdbc.timeBetweenEvictionRunsMillis}") int timeBetweenEvictionRunsMillis, @Value("${database-config.jdbc.minEvictableIdleTimeMillis}") int minEvictableIdleTimeMillis,
                                            @Value("${database-config.jdbc.validationQuery}") String validationQuery, @Value("${database-config.jdbc.testWhileIdle}") boolean testWhileIdle,
                                            @Value("${database-config.jdbc.testOnBorrow}") boolean testOnBorrow, @Value("${database-config.jdbc.testOnReturn}") boolean testOnReturn,
                                            @Value("${database-config.jdbc.poolPreparedStatements}") boolean poolPreparedStatements, @Value("${database-config.jdbc.maxPoolPreparedStatementPerConnectionSize}") int maxPoolPreparedStatementPerConnectionSize) throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();

        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setFilters(filters);
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        druidDataSource.setValidationQuery(validationQuery);
        druidDataSource.setTestWhileIdle(testWhileIdle);
        druidDataSource.setTestOnBorrow(testOnBorrow);
        druidDataSource.setTestOnReturn(testOnReturn);
        druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        return druidDataSource;
    }

    /**
     * sqlSessionFactory配置
     * @return
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactory(DruidDataSource dataSource) throws SQLException, IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mysql");
        pageInterceptor.setProperties(properties);
        Interceptor[] interceptors = new Interceptor[]{pageInterceptor};
        sqlSessionFactoryBean.setPlugins(interceptors);
        sqlSessionFactoryBean.setTypeAliasesPackage("org.meiconjun.erp4m.bean");
        sqlSessionFactoryBean.setMapperLocations(getMapperResources());
        return sqlSessionFactoryBean;
    }

    private org.springframework.core.io.Resource[] getMapperResources() throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        String mapper1 = "classpath:mapper/*.xml";
        org.springframework.core.io.Resource[] resource = resourcePatternResolver.getResources(mapper1);
        return resource;
    }

    /**
     * 配置扫描Dao接口包，动态实现Dao接口，注入到spring容器中
     * @return
     */
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("org.meiconjun.erp4m.dao");
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return mapperScannerConfigurer;
    }

    /**
     * 配置事务管理器
     */
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager(DruidDataSource dataSource) throws SQLException {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }




}
