package com.mayanshe.nosocomiumonline.application.service.bus;

import com.mayanshe.nosocomiumonline.shared.cqrs.Query;
import com.mayanshe.nosocomiumonline.shared.cqrs.QueryBus;
import com.mayanshe.nosocomiumonline.shared.cqrs.QueryHandler;
import com.mayanshe.nosocomiumonline.shared.cqrs.exceptions.HandlerNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认查询总线实现。
 * 负责维护 Query 与 QueryHandler 的映射关系，并分发查询。
 */
@Component
public class DefaultQueryBus implements QueryBus {

    private final Map<Class<? extends Query>, QueryHandler> registry = new ConcurrentHashMap<>();

    /**
     * 注册查询处理器。
     *
     * @param queryClass 查询类型
     * @param handler    对应的处理器
     * @param <Q>        Query 类型
     */
    public <Q extends Query> void register(Class<Q> queryClass, QueryHandler<Q, ?> handler) {
        registry.put(queryClass, handler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Q extends Query, R> R ask(Q query) {
        QueryHandler<Q, R> handler = registry.get(query.getClass());
        if (handler == null) {
            throw new HandlerNotFoundException("No handler found for query: " + query.getClass().getName());
        }
        return handler.handle(query);
    }
}
