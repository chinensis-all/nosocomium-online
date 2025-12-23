package com.mayanshe.nosocomiumonline.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 临时凭证管理 (Stub due to missing dependency).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CosCredentialManager {

    public Object getCredential() {
        throw new UnsupportedOperationException("STS SDK not available");
    }
}
