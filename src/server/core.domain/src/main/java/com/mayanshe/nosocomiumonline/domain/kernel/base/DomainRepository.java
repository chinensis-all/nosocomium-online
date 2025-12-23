package com.mayanshe.nosocomiumonline.domain.kernel.base;

import java.util.Optional;

/**
 * 领域仓库接口
 * @param <Aggregate> 聚合根类型
 */
public interface DomainRepository<Aggregate extends AggregateRoot> {

    Optional<Aggregate> load(Long id);

    void save(Aggregate aggregate);

    void destroy(Long id);
}
