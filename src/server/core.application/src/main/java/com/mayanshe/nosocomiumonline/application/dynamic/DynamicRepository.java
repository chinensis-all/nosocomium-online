package com.mayanshe.nosocomiumonline.application.dynamic;

import com.mayanshe.nosocomiumonline.shared.valueobject.Ascending;
import com.mayanshe.nosocomiumonline.shared.valueobject.Direction;
import com.mayanshe.nosocomiumonline.shared.valueobject.KeyValue;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 动态仓库接口。
 * <p>
 * 为动态实体提供对数据库的抽象访问。
 */
public interface DynamicRepository {
    /**
     * 插入一条记录。
     *
     * @param entity 实体对象
     * @param type   实体类型
     *               @return 新插入记录的 ID
     */
    long insert(Object entity, Type type);

    /**
     * 更新一条记录。
     *
     * @param entity 实体对象
     * @param type   实体类型
     *               @return 受影响的行数
     */
    long update(Object entity, Type type);

    /**
     * 根据 ID 删除一条记录。
     *
     * @param id   记录 ID
     * @param type 实体类型
     *             @return 受影响的行数
     */
    long deleteById(Long id, Type type);

    /**
     * 根据 ID 软删除一条记录（设置 deleted_at 字段）。
     *
     * @param id   记录 ID
     * @param type 实体类型
     * @return 受影响的行数
     */
    long softDeleteById(Long id, Type type);

    /**
     * 根据 ID 查询一条记录。
     *
     * @param id   记录 ID
     * @param type 实体类型
     * @return 数据映射
     */
    Object findById(Long id, Type type);

    /**
     * 根据条件搜索记录。
     *
     * @param criteria 查询条件
     * @param limit    限制
     * @param offset   偏移
     * @param type     实体类型
     * @return 记录列表
     */
    List<Object> search(Map<String, Object> criteria, int limit, int offset, Type type);

    /**
     * 统计符合条件的记录数。
     *
     * @param criteria 查询条件
     * @param type     实体类型
     * @return 记录数
     */
    long count(Map<String, Object> criteria, Type type);

    /**
     * 偏移量分页搜索。
     * 仅适用于主键ID
     *
     * @param criteria  查询条件
     * @param ascending 排序顺序
     * @param direction 方向
     * @param id        偏移量 ID
     * @param limit     限制
     * @param type      实体类型
     * @return 记录列表
     */
    List<Object> keysetSearch(Map<String, Object> criteria, Ascending ascending, Direction direction, Long id, int limit, Type type);
}
