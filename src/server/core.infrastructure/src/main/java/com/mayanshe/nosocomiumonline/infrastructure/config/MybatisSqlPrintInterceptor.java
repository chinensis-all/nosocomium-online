package com.mayanshe.nosocomiumonline.infrastructure.config;

import com.mayanshe.nosocomiumonline.shared.utils.PrintUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * MybatisSqlPrintInterceptor: MyBatis SQL 打印拦截器
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {
                MappedStatement.class, Object.class, org.apache.ibatis.session.RowBounds.class,
                org.apache.ibatis.session.ResultHandler.class
        }),
        @Signature(type = Executor.class, method = "update", args = {
                MappedStatement.class, Object.class
        })
})
public class MybatisSqlPrintInterceptor implements Interceptor {

    private static final long DEFAULT_LIMIT_MS = 15;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = invocation.proceed();

        long end = System.currentTimeMillis();

        try {
            MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
            Object parameter = invocation.getArgs()[1];
            BoundSql boundSql = ms.getBoundSql(parameter);
            String sql = buildSql(ms.getConfiguration(), boundSql);

            PrintUtils.print("SQL: {}", sql, start, DEFAULT_LIMIT_MS);
        } catch (Exception ignored) {
        }

        return result;
    }

    private String buildSql(Configuration configuration, BoundSql boundSql) {
        String sql = boundSql.getSql().replaceAll("\\s+", " ");
        List<ParameterMapping> mappings = boundSql.getParameterMappings();
        Object parameterObject = boundSql.getParameterObject();

        if (mappings == null || mappings.isEmpty()) {
            return sql;
        }

        for (ParameterMapping mapping : mappings) {
            Object value;
            String property = mapping.getProperty();

            if (boundSql.hasAdditionalParameter(property)) {
                value = boundSql.getAdditionalParameter(property);
            } else if (parameterObject == null) {
                value = null;
            } else if (configuration.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
                value = parameterObject;
            } else {
                value = org.apache.ibatis.reflection.MetaObject
                        .forObject(parameterObject, configuration.getObjectFactory(),
                                configuration.getObjectWrapperFactory(), configuration.getReflectorFactory())
                        .getValue(property);
            }

            sql = sql.replaceFirst("\\?", formatValue(value));
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
            return "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) value) + "'";
        }
        return value.toString();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
