package com.mayanshe.nosocomiumonline.shared.contract;

/**
 * 将 Modify Command 转换为 Entity 的契约。
 *
 * @param <M> 命令的内容。
 * @param <E> 实体类型。
 */
public interface ModifyCommandToEntity<M, E> {
    /**
     * 根据修改命令更新现有实体。
     *
     * @param command 修改命令。
     * @param entity  要更新的现有实体。
     */
    void updateEntity(M command, E entity);
}
