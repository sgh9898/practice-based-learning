package com.sgh.demo.sharding.database.db.repository;

import com.sgh.demo.common.util.JdbcUtils;
import com.sgh.demo.sharding.database.pojo.query.DemoEntityShardingQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * * [数据库交互] 测试数据--分表
 *
 * @author Song gh
 * @version 2024/3/22
 */
@Repository
public class DemoEntityShardingRepositoryImpl implements DemoEntityShardingRepositoryCustom {

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /** 并表数据 */
    @Override
    public Page<Map<String, Object>> getMergedPage(DemoEntityShardingQueryDto dto) {
        String sql = "SELECT demo.id AS demoId, demo.name AS demoName, demo.comment AS demoComment, " +
                "       demo_copy.id AS copyId, demo_copy.name AS copyName, demo_copy.comment AS copyComment " +
                "FROM demo_entity_sharding_copy AS demo_copy " +
                "    INNER JOIN demo_entity_sharding AS demo ON demo.is_deleted = FALSE AND demo.id = demo_copy.demo_entity_id " +
                "WHERE demo_copy.is_deleted = FALSE " +
                "  AND demo_copy.id = :copyId";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("copyId", dto.getId());
        dto.checkPageable();
        return JdbcUtils.queryForPage(namedParameterJdbcTemplate, sql, null, paramsMap, dto.getPage() - 1, dto.getSize());
    }

    /** 检查分片审计 */
    @Override
    public List<Map<String, Object>> checkAuditor() {
        // 默认的分片审计开启时, where 语句必须包含分片键
        String sql = "SELECT * " +
                "FROM demo_entity_sharding_copy AS demo_copy " +
                "WHERE demo_copy.is_deleted = FALSE " +
//                "  AND demo_copy.demo_entity_id = 1 " +
                "LIMIT 20 ";
        Map<String, Object> paramsMap = new HashMap<>();
        return namedParameterJdbcTemplate.queryForList(sql, paramsMap);
    }
}
