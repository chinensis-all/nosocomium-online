package com.mayanshe.nosocomiumonline.shared.contract;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * IdGenerator: 标识生成器接口
 * @author zhangxihai
 */
public final class IdGenerator {

    private static final Snowflake SNOWFLAKE;

    static {
        // 示例：从环境变量或配置读取（实际生产中建议Spring注入）
        long workerId = Long.parseLong(System.getenv("SNOWFLAKE_WORKER_ID") != null ?
                System.getenv("SNOWFLAKE_WORKER_ID") : "0");
        long datacenterId = Long.parseLong(System.getenv("SNOWFLAKE_DATACENTER_ID") != null ?
                System.getenv("SNOWFLAKE_DATACENTER_ID") : "0");
        SNOWFLAKE = IdUtil.getSnowflake(workerId, datacenterId);
    }

    private IdGenerator() {
        throw new UnsupportedOperationException();
    }

    public static long nextId() {
        return SNOWFLAKE.nextId();
    }

    public static String nextIdStr() {
        return SNOWFLAKE.nextIdStr();
    }
}
