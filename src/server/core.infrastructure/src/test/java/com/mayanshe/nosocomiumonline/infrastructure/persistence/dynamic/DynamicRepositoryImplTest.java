package com.mayanshe.nosocomiumonline.infrastructure.persistence.dynamic;

import com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper.OutboxMapper; // Ensure MyBatis config loads
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MybatisTest
@Import(DynamicRepositoryImpl.class) // Import the implementation as component
class DynamicRepositoryImplTest {

    @Autowired
    private DynamicRepositoryImpl dynamicRepository;

    @Test
    void testDynamicOperations() {
        // Create table
        dynamicRepository.count("event_outbox", null);
        // We reuse event_outbox for testing simply because it exists in schema.
        // Or we rely on H2 in-memory creation.
        // Given CrudConfig requires a table, let's assume 'event_outbox' works or
        // create a test table via script?
        // @MybatisTest usually rolls back.
        // But event_outbox is complex (has JSON).
        // Let's rely on basic assertions if table exists.
    }
}
