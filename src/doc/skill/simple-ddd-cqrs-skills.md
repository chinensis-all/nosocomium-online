# DDD + CQRS 简明技能列表

本列表基于 `MediaController` 及其相关实现的架构分析提炼而成，旨在帮助开发者快速理解并应用项目中的核心设计模式。

## 1. 领域驱动设计 (DDD) 核心实践

- **聚合根 (Aggregate Root)**: `Media.java` 作为聚合根，保证了领域对象内部的一致性边界。
- **实体 (Entity)**: 具有唯一标识（`AggregateId`）的对象，如 `Media` 实例。其状态随时间变化，但在生命周期内保持身份。
- **值对象 (Value Object)**: 描述实体的属性，没有独立身份。例如 `MediaType`、`BucketType`。
- **领域事件 (Domain Event)**: 记录领域中发生的显著变动。
    - **定义**: `MediaUploadedEvent`, `MediaDeletedEvent`。
    - **处理**: 在聚合根方法（如 `upload()`, `delete()`）中调用 `registerEvent()` 注册。
- **领域服务与应用服务**:
    - **职责划分**: 业务逻辑下沉到领域对象（`Media`），协调逻辑（事务控制、存储调用）留在应用层 Handler（`DeleteMediaCommandHandler`）。
- **持久化映射 (PO/Entity)**: 使用 `MediaEntity` (PO) 对应数据库表，通过 `Media` 聚合根进行业务操作，解耦业务模型与存储结构。

## 2. 命令查询职责分离 (CQRS) 实现

- **命令 (Command)**: 代表写操作，意图改变系统状态。
    - **实现**: `UploadMediaCommand`, `DeleteMediaCommand` (Record 类型)。
    - **分发**: 通过 `CommandBus` 解析并路由到对应的 `CommandHandler`。
- **查询 (Query)**: 代表读操作，不改变系统状态。
    - **实现**: `GetMediaDetailQuery`, `GetMeidaPageQuery`。
    - **分发**: 通过 `QueryBus` 分发给 `QueryHandler`。
- **读写分离与 Repository 设计**:
    - **Command 端**: 使用 `MediaRepository` 加载聚合根进行更新。
    - **Query 端**: 使用 `MediaQueryRepository` 直接查询并返回 DTO (如 `MediaDto`)，跳过复杂领域转换。

## 3. 领域关联接口设计原则

- **CQRS 适配器**: Controller 仅负责将 HTTP 请求转换为 Command 或 Query 对象。
- **松耦合**: 接口不直接暴露领域实体，返回数据统一使用 `MediaDto`。

## 4. 关键机制与组件

- **Outbox 模式**: `OutboxRepository` 用于持久化领域事件，确保事务内事件发送的一致性。
- **领域事件收集器**: `DomainEventCollector.drain()` 从聚合根所在的上下文中提取并清理已执行的事件。
- **对象存储抽象**: `ObjectStorageService` 抽离第三方存储逻辑，增强可移植性。

## 5. Knife4j API 文档定义

- **Tag**: `@Tag(name = "...", description = "...")` 用于控制器分类。
- **Operation**: `@Operation(summary = "...", description = "...")` 描述具体接口功能。
- **Parameter**: `@Parameter(description = "...", example = "...")` 定义参数详情与示例值。
- **Schema**: `@Schema(allowableValues = {...})` 约束枚举或允许的特定值范围。
