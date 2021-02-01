package org.meiconjun.erp4m.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
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

    @Resource
    private DatabaseProperties databaseProperties;

    /**
     * 数据库数据源配置
     * @return
     */
    @Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
    public DruidDataSource druidDataSource () throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        HashMap<String, Object> jdbc = databaseProperties.getJdbc();
        druidDataSource.setDriverClassName((String)jdbc.get("driver"));
        druidDataSource.setUrl((String)jdbc.get("url"));
        druidDataSource.setUsername((String)jdbc.get("username"));
        druidDataSource.setPassword((String)jdbc.get("password"));
        druidDataSource.setFilters((String)jdbc.get("filters"));
        druidDataSource.setMaxActive((int)jdbc.get("maxActive"));
        druidDataSource.setInitialSize((int)jdbc.get("initialSize"));
        druidDataSource.setMaxWait((int)jdbc.get("maxWait"));
        druidDataSource.setMinIdle((int)jdbc.get("minIdle"));
        druidDataSource.setTimeBetweenEvictionRunsMillis((int)jdbc.get("timeBetweenEvictionRunsMillis"));
        druidDataSource.setMinEvictableIdleTimeMillis((int)jdbc.get("minEvictableIdleTimeMillis"));
        druidDataSource.setValidationQuery((String)jdbc.get("validationQuery"));
        druidDataSource.setTestWhileIdle((boolean)jdbc.get("testWhileIdle"));
        druidDataSource.setTestOnBorrow((boolean)jdbc.get("testOnBorrow"));
        druidDataSource.setTestOnReturn((boolean)jdbc.get("testOnReturn"));
        druidDataSource.setPoolPreparedStatements((boolean)jdbc.get("poolPreparedStatements"));
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize((int)jdbc.get("maxPoolPreparedStatementPerConnectionSize"));
        return druidDataSource;
    }

    /**
     * sqlSessionFactory配置
     * @return
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactory() throws SQLException, IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(druidDataSource());
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
    public DataSourceTransactionManager dataSourceTransactionManager() throws SQLException {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(druidDataSource());
        return dataSourceTransactionManager;
    }




}
