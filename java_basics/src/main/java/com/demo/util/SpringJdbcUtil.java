package com.demo.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * jdbc分页查询
 *
 * @author sunt
 * @date 2018年8月8日 下午3:41:00
 */
public class SpringJdbcUtil {

    protected JdbcTemplate jdbcTemplate;

    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public SpringJdbcUtil() {
    }

    public SpringJdbcUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public SpringJdbcUtil(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public SpringJdbcUtil(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    /**
     * oracle sql分页查询,基于jdbctemplate
     * 使用JPA的Page
     *
     * @param sql       sql语句
     * @param pageable  从1开始  类似new PageRequest(1, 20) 见#{org.springframework.data.domain.PageRequest}
     * @param rowMapper 每行数据转java对象所参考的类，参考SpringJdbc
     * @param objects   查询条件
     */
    @Transactional
    public <T> Page<T> queryForList(String sql, Pageable pageable, RowMapper<T> rowMapper, Object... objects) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql must be not null");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("pageable must be not null");
        }

        String countSql = "select count(1) from (" + sql + ")";
        long total = this.jdbcTemplate.queryForObject(countSql, Long.class, objects);
        List<T> list = new ArrayList<>();
        if (total > 0) {
            Object[] params = Arrays.copyOf(objects, objects.length + 2);
            params[params.length - 2] = pageable.getOffset();//end
            params[params.length - 1] = (pageable.getPageNumber() - 1) * pageable.getPageSize();//start 从0开始
            String querySql = "select * from (select t.*, rownum rn from (" + sql + ") t where rownum <= ? ) where rn>? ";
            list = this.jdbcTemplate.query(querySql, rowMapper, params);

        }
        Page<T> pageList = new PageImpl<>(list, pageable, total);
        return pageList;
    }

    @Transactional
    public Page<Map<String, Object>> queryListForMySql(NamedParameterJdbcTemplate npJdbcTemplate, String sql,
                                                       Pageable pageable, Map<String, Object> params) {
        String countSql = "select count(1) from (" + sql + ") t";
        long total = npJdbcTemplate.queryForObject(countSql, params, Long.class);
        List<Map<String, Object>> list = new ArrayList<>();
        if (total > 0) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put("sstartt", (pageable.getPageNumber() - 1) * pageable.getPageSize());
            params.put("llengthh", pageable.getPageSize());
            String querySql = "select a.* from (" + sql + ")a limit :sstartt,:llengthh";
            list = npJdbcTemplate.queryForList(querySql, params);
        }
        return new PageImpl<>(list, pageable, total);
    }


    /**
     * oracle sql分页查询,基于NamedParameterJdbcTemplate
     *
     * @param sql       sql语句
     * @param pageable  从1开始  类似new PageRequest(1, 20) 见#{org.springframework.data.domain.PageRequest}
     * @param rowMapper 每行数据转java对象所参考的类，参考SpringJdbc
     * @param params    查询条件
     */
    @Transactional
    public <T> Page<T> queryForList(String sql, Pageable pageable, RowMapper<T> rowMapper, Map<String, Object> params) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql must be not null");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("pageable must be not null");
        }
        String countSql = "select count(1) from (" + sql + ")";
        long total = this.namedParameterJdbcTemplate.queryForObject(countSql, params, Long.class);
        List<T> list = new ArrayList<>();
        if (total > 0) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put("sstartt", (pageable.getPageNumber() - 1) * pageable.getPageSize());
            params.put("eendd", pageable.getOffset());
            String querySql = "select * from (select t.*, rownum rn from (" + sql + ") t where rownum<=:eendd ) where rn>:sstartt ";
            list = this.namedParameterJdbcTemplate.query(querySql, params, rowMapper);
        }
        Page<T> pageList = new PageImpl<>(list, pageable, total);
        return pageList;
    }

    /**
     * TODO 需要测试
     * mysql sql分页查询,基于NamedParameterJdbcTemplate
     * 注意：查询条件不要包含start和end
     *
     * @param sql       sql语句
     * @param pageable  从1开始  类似new PageRequest(1, 20) 见#{org.springframework.data.domain.PageRequest}
     * @param rowMapper 每行数据转java对象所参考的类，参考SpringJdbc
     * @param params    查询条件
     */
    @Transactional
    public <T> Page<T> queryListForMySql(String sql, Pageable pageable, RowMapper<T> rowMapper, Map<String, Object> params) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql must be not null");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("pageable must be not null");
        }
        String countSql = "select count(1) from (" + sql + ") sss";
        long total = this.namedParameterJdbcTemplate.queryForObject(countSql, params, Long.class);
        List<T> list = new ArrayList<>();
        if (total > 0) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put("sstartt", (pageable.getPageNumber() - 1) * pageable.getPageSize());
            params.put("llengthh", pageable.getPageSize());
            String querySql = sql + " limit :sstartt,:llengthh";// "select * from (select t.*, rownum rn from ("+sql+") t where rownum<=:eendd ) where rn>:sstartt ";
            list = this.namedParameterJdbcTemplate.query(querySql, params, rowMapper);
        }
        Page<T> pageList = new PageImpl<>(list, pageable, total);
        return pageList;
    }

    @Transactional
    public Page<Map<String, Object>> queryListForMySql(String sql, Pageable pageable, Map<String, Object> params) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql must be not null");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("pageable must be not null");
        }
        String countSql = "select count(1) from (" + sql + ") sss";
        long total = this.namedParameterJdbcTemplate.queryForObject(countSql, params, Long.class);
        List<Map<String, Object>> list = new ArrayList<>();
        if (total > 0) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put("sstartt", (pageable.getPageNumber() - 1) * pageable.getPageSize());
            params.put("llengthh", pageable.getPageSize());
            String querySql = sql + " limit :sstartt,:llengthh";// "select * from (select t.*, rownum rn from ("+sql+") t where rownum<=:eendd ) where rn>:sstartt ";
            list = this.namedParameterJdbcTemplate.queryForList(querySql, params);
        }
        Page<Map<String, Object>> pageList = new PageImpl<>(list, pageable, total);
        return pageList;
    }


    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }


}
