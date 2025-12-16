package com.mayanshe.nosocomiumonline.infrastructure.cqrs;

import com.mayanshe.nosocomiumonline.application.service.bus.DefaultCommandBus;
import com.mayanshe.nosocomiumonline.application.service.bus.DefaultQueryBus;
import com.mayanshe.nosocomiumonline.shared.cqrs.Command;
import com.mayanshe.nosocomiumonline.shared.cqrs.CommandHandler;
import com.mayanshe.nosocomiumonline.shared.cqrs.Query;
import com.mayanshe.nosocomiumonline.shared.cqrs.QueryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.GenericTypeResolver;

import jakarta.annotation.PostConstruct;
import java.util.Map;

/**
 * 处理器注册表。
 * 负责扫描 Spring 上下文中的 Handler，并将其注册到 CommandBus 和 QueryBus 中。
 * 泛型解析在启动时进行。
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class HandlerRegistry {

    private final ApplicationContext applicationContext;
    private final DefaultCommandBus commandBus;
    private final DefaultQueryBus queryBus;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void registerHandlers() {
        // Register CommandHandlers
        Map<String, CommandHandler> commandHandlers = applicationContext.getBeansOfType(CommandHandler.class);
        for (CommandHandler handler : commandHandlers.values()) {
            Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(handler.getClass(),
                    CommandHandler.class);
            if (typeArguments != null && typeArguments.length > 0) {
                Class<? extends Command> commandClass = (Class<? extends Command>) typeArguments[0];
                log.info("Registering CommandHandler {} for Command {}", handler.getClass().getSimpleName(),
                        commandClass.getSimpleName());
                commandBus.register(commandClass, handler);
            } else {
                log.warn("Could not resolve generic type for CommandHandler: {}", handler.getClass().getName());
            }
        }

        // Register QueryHandlers
        Map<String, QueryHandler> queryHandlers = applicationContext.getBeansOfType(QueryHandler.class);
        for (QueryHandler handler : queryHandlers.values()) {
            Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(handler.getClass(), QueryHandler.class);
            if (typeArguments != null && typeArguments.length > 0) {
                Class<? extends Query> queryClass = (Class<? extends Query>) typeArguments[0];
                log.info("Registering QueryHandler {} for Query {}", handler.getClass().getSimpleName(),
                        queryClass.getSimpleName());
                queryBus.register(queryClass, handler);
            } else {
                log.warn("Could not resolve generic type for QueryHandler: {}", handler.getClass().getName());
            }
        }
    }
}
