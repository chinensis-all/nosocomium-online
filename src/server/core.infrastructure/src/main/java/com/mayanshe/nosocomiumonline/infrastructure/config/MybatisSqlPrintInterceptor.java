package com.mayanshe.nosocomiumonline.infrastructure.config;

import com.mayanshe.nosocomiumonline.shared.util.PrintUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 * MybatisSqlPrintInterceptor: MyBatis SQL 打印拦截器
 *
 * @author zhangxihai
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Component
@Profile({"dev", "test"})
public class MybatisSqlPrintInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = result = invocation.proceed();
        String sql = "SQL_PARSE_ERROR";

        try {
            MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
            Object parameter = invocation.getArgs()[1];
            BoundSql boundSql = ms.getBoundSql(parameter);
            sql = buildSql(ms.getConfiguration(), boundSql);
        } catch (Exception e) {
            PrintUtils.failedFmt("Failed to build SQL in MybatisSqlPrintInterceptor: {}", e.getMessage());
        }

        PrintUtils.performance("SQL - {}", sql, start, 100);

        return result;
    }

    private String buildSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("\\s+", " ");

        if (parameterMappings == null || parameterMappings.isEmpty() || parameterObject == null) {
            return sql;
        }

        for (ParameterMapping mapping : parameterMappings) {
            if (mapping.getMode() != ParameterMode.OUT) {
                Object value;
                String propertyName = mapping.getProperty();
                if (boundSql.hasAdditionalParameter(propertyName)) {
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (configuration.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
                    value = parameterObject;
                } else {
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    value = metaObject.getValue(propertyName);
                }

                String formattedValue = Matcher.quoteReplacement(formatValue(value));
                sql = sql.replaceFirst("\\?", formattedValue);
            }
        }
        return sql;
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof String) {
            return "'" + value + "'";
        }

        if (value instanceof Date) {
            return "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value) + "'";
        }

        return value.toString();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {}
}