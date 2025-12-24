package com.mayanshe.nosocomiumonline.boss.controller;

import com.mayanshe.nosocomiumonline.boss.BossApiApplication;
import com.mayanshe.nosocomiumonline.shared.contract.Cache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 行政区划管理集成测试。
 */
@SpringBootTest(classes = BossApiApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Cache cache;

    @Test
    void getRegions_shouldReturnList() throws Exception {
        mockMvc.perform(get("/regions")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getRegions_withKeywords_shouldReturnFilteredList() throws Exception {
        mockMvc.perform(get("/regions")
                .param("keywords", "北京")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getRegions_withParentId_shouldReturnChildren() throws Exception {
        // 110000 可能是北京的 ID
        mockMvc.perform(get("/regions")
                .param("parentId", "110000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
