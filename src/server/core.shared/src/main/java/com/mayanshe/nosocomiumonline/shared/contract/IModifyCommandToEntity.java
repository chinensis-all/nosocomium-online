package com.mayanshe.nosocomiumonline.shared.contract;

/**
 * Contract for converting a Modify Command to an Entity.
 *
 * @param <M> The content of the command.
 * @param <E> The Entity type.
 */
public interface IModifyCommandToEntity<M, E> {
    /**
     * Updates an existing entity from a modify command.
     *
     * @param command The modify command.
     * @param entity  The existing entity to update.
     */
    void updateEntity(M command, E entity);
}
