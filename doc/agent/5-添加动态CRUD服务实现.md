1. 目标（Intent）

在 DDD + CQRS 架构中实现一个 DynamicCrudService，用于替代大量“无业务价值”的 Aggregate / Command / Query Handler。

DynamicCrudService 是 应用层工程能力，不是领域模型。

目标：

减少样板代码

覆盖稳定、简单的 CRUD 场景

保留 Query DTO

非核心对象不强制聚合根

2. 项目与模块约束（强制）
doc/data/schema.sql          # 只允许追加

src/server
├── core.shared              # 跨项目接口、抽象、utils
├── core.domain              # 领域模型（无 Spring）
├── core.application         # 用例 + DynamicCrudService
├── core.infrastructure     # Repository 实现、MapStruct
├── api.*                    # Controller
└── task.all                 # 定时任务


DynamicCrudService 必须在 core.application/dynamic

Repository 实现 只能在 core.infrastructure

跨项目接口/基类放 core.shared/{contracts,bases,utils}

3. Dynamic CRUD 核心规则
3.1 Entity（必须）

每个配置必须指定 Entity（POJO，用于持久化）

配置 key 使用 Entity 类名

3.2 Create / Modify Command（可选）
场景	接口
无 CreateCommand	create(Map<String,Object?> data)
有 CreateCommand	create(C command)
无 ModifyCommand	modify(Long id, Map<String,Object?> data)
有 ModifyCommand	modify(M command)
3.3 对象转换接口（core.shared/contracts）

必须定义并使用：

IMapToEntity<E>
ICreateCommandToEntity<E>
IModifyCommandToEntity<E>
IEntityToDto<D>


MapStruct 仅允许在 core.infrastructure

禁止在 domain / application 直接使用 MapStruct 实现

4. 查询模型（Dynamic Query）

Repository 只接受 Map<String,Object?>

Query 对象 必须继承 BaseQuery

BaseQuery 必须实现：

Map<String,Object?> toMap();

5. DynamicCrudService 必须提供的方法
Find(id)
Search(Map | Query)        # 必须限制返回数量
Paginate(Map | Query)
KeysetPaginate(Map | Query)
KVSearch(Map | Query)      # 返回 {code, value}

6. 性能约束（必须遵守）

❌ 禁止主链路频繁反射

✅ 启动期解析并缓存配置

✅ 泛型擦除后使用 MethodHandle / LambdaMetaFactory（如需）

❌ 禁止每次 CRUD 扫描字段

Keyset 分页必须基于唯一/单调索引

7. 测试要求（强制）

必须生成测试：

DynamicCrudService

Map → Entity 转换

Create / Modify 分支

Keyset 分页边界

非法/缺失配置

8. 架构立场（不可误解）

本系统是 DDD + CQRS

DynamicCrudService 是 应用层减法

核心领域仍使用 Aggregate / Command / Query

Dynamic CRUD 仅用于稳定、非核心对象

9. 输出期望

代码必须标注模块归属

不过度设计、不做 ORM

提供 README + 使用示例

所有关键路径可测试

最终提醒给 AI

DynamicCrudService ≠ ORM ≠ ActiveRecord ≠ Domain Model
它是 应用层工程能力，用于减少 DDD 样板代码


- 请全程用中文与我交流
- 请理解Spring Boot目录机构，不要创建错误的文件夹

---
### 10. 新增需求 (v2)

#### 10.1 Destroy(Long id)
需根据配置决定 **物理删除** 还是 **软删除**。
- 默认：物理删除
- 软删除：固定使用 `deleted_at` 字段 (BIGINT UNSIGNED)

#### 10.2 事件发布配置
支持配置是否在 创建/修改/删除 时自动发布事件。
- 默认：开启 (True)
- 开启时，在对应操作成功后发布集成事件

#### 10.3 缓存配置
- **Find** (默认关闭, TTL 600s, 键 `Tdo:id=`)
     - 更新/删除时需清除缓存
- **Paginate** (默认开启, TTL 60s, 键 `Paginate:Tdo:criteria=MD5`)
- **KeysetPaginate / Search / KVSearch**: 同 Paginate 策略

### 11. CrudConfig 我改成了以name获取config, 请修改后续