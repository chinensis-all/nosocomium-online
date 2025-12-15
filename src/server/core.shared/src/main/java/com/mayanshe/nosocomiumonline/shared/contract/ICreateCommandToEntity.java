package com.mayanshe.nosocomiumonline.shared.contract;

/**
 * Contract for converting a Create Command to an Entity.
 *
 * @param <C> The content of the command (could be a DTO or Map).
 * @param <E> The Entity type.
 */
public interface ICreateCommandToEntity<C, E> {
    /**
     * Converts a create command to an entity.
     *
     * @param command The create command.
     * @return The entity.
     */
    E toEntity(C command);
}
