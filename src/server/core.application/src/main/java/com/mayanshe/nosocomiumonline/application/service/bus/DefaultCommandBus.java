package com.mayanshe.nosocomiumonline.application.service.bus;

import com.mayanshe.nosocomiumonline.shared.cqrs.Command;
import com.mayanshe.nosocomiumonline.shared.cqrs.CommandBus;
import com.mayanshe.nosocomiumonline.shared.cqrs.CommandHandler;
import com.mayanshe.nosocomiumonline.shared.cqrs.exceptions.HandlerNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认命令总线实现。
 * 负责维护 Command 与 CommandHandler 的映射关系，并分发命令。
 */
@Component
public class DefaultCommandBus implements CommandBus {

    private final Map<Class<? extends Command>, CommandHandler> registry = new ConcurrentHashMap<>();

    /**
     * 注册命令处理器。
     *
     * @param commandClass 这里的 Command 类型
     * @param handler      对应的处理器
     * @param <C>          Command 类型
     */
    public <C extends Command> void register(Class<C> commandClass, CommandHandler<C, ?> handler) {
        registry.put(commandClass, handler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends Command, R> R dispatch(C command) {
        CommandHandler<C, R> handler = registry.get(command.getClass());
        if (handler == null) {
            throw new HandlerNotFoundException("No handler found for command: " + command.getClass().getName());
        }
        return handler.handle(command);
    }
}
