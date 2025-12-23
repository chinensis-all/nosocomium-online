package com.mayanshe.nosocomiumonline.infrastructure.util;

import com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper.PageMapper;
import com.mayanshe.nosocomiumonline.shared.valueobject.Pagination;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Pager: 分页工具类
 *
 * @author zhangxihai
 */
public final class Pager {
    private Pager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 自定义分页查询（不带回调）
     *
     * @param mapper    分页Mapper
     * @param page      当前页码
     * @param pageSize  每页大小
     * @param condition 查询条件
     * @param <M>       Mapper类型
     * @param <P>       数据类型
     * @return 分页结果
     */
    public static <M extends PageMapper<P>, P> Pagination<P> paginate(M mapper, Map<String, Object> condition, long page, int pageSize) {
        page = Math.max(page, 1);
        pageSize = Math.max(pageSize, 1);

        long total = mapper.count(condition);

        if (total == 0) {
            return new Pagination<P>(page, pageSize, total, List.of());
        }

        long offset = (page - 1) * pageSize;

        List<P> items = mapper.search(condition, offset, pageSize);

        return new Pagination<P>(page, pageSize, total, items);
    }

    /**
     * 自定义分页查询（带回调）
     *
     * @param mapper    分页Mapper
     * @param page      当前页码
     * @param pageSize  每页大小
     * @param condition 查询条件
     * @param callback  回调函数，用于处理每个源对象
     * @param <M>       Mapper类型
     * @param <P>       源数据类型
     * @param <T>       目标数据类型
     * @return 分页结果
     */
    public static <M extends PageMapper<P>, P, T> Pagination<T> paginate(M mapper, Map<String, Object> condition, Callback<P, T> callback, long page, int pageSize) {
        page = Math.max(page, 1);
        pageSize = Math.max(pageSize, 1);

        long total = mapper.count(condition);

        if (total == 0) {
            return new Pagination<T>(page, pageSize, total, List.of());
        }

        long offset = (page - 1) * pageSize;

        List<P> sources = mapper.search(condition, offset, pageSize);
        List<T> items = sources.stream().map(source -> {
            try {
                return callback.handle(source);
            } catch (Exception e) {
                throw new RuntimeException("Error processing source object", e);
            }
        }).toList();


        return new Pagination<T>(page, pageSize, total, items);
    }

    /**
     * 自定义分页查询（不带回调）
     *
     * @param mapper    分页Mapper
     * @param page      当前页码
     * @param pageSize  每页大小
     * @param condition 查询条件
     * @param <M>       Mapper类型
     * @param <P>       数据类型
     * @return 分页结果
     */
    public static <M extends PageMapper<P>, P> Pagination<P> paginate(M mapper, Map<String, Object> condition, Set<String> sorts, long page, int pageSize) {
        page = Math.max(page, 1);
        pageSize = Math.max(pageSize, 1);

        long total = mapper.count(condition);

        if (total == 0) {
            return new Pagination<P>(page, pageSize, total, List.of());
        }

        long offset = (page - 1) * pageSize;

        List<P> items = mapper.searchWithSort(condition, sorts, offset, pageSize);

        return new Pagination<P>(page, pageSize, total, items);
    }

    /**
     * 自定义分页查询（带回调）
     *
     * @param mapper    分页Mapper
     * @param page      当前页码
     * @param pageSize  每页大小
     * @param condition 查询条件
     * @param callback  回调函数，用于处理每个源对象
     * @param <M>       Mapper类型
     * @param <P>       源数据类型
     * @param <T>       目标数据类型
     * @return 分页结果
     */
    public static <M extends PageMapper<P>, P, T> Pagination<T> paginate(M mapper, Map<String, Object> condition, Set<String> sorts, Callback<P, T> callback, long page, int pageSize) {
        page = Math.max(page, 1);
        pageSize = Math.max(pageSize, 1);

        long total = mapper.count(condition);

        if (total == 0) {
            return new Pagination<T>(page, pageSize, total, List.of());
        }

        long offset = (page - 1) * pageSize;

        List<P> sources = mapper.searchWithSort(condition, sorts, offset, pageSize);
        List<T> items = sources.stream().map(source -> {
            try {
                return callback.handle(source);
            } catch (Exception e) {
                throw new RuntimeException("Error processing source object", e);
            }
        }).toList();


        return new Pagination<T>(page, pageSize, total, items);
    }

    @FunctionalInterface
    public interface Callback<P, T> {
        T handle(P source) throws Exception; // 允许抛出受检异常
    }
}
