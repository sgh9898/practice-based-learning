package com.demo.elasticsearch.repository;

import com.demo.elasticsearch.document.SampleIndex;
import com.demo.elasticsearch.dto.SampleIndexQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ES 演示类 repository
 *
 * @author Song gh on 2022/4/15.
 */
public interface SampleIndexRepository extends ElasticsearchRepository<SampleIndex, String>, SampleIndexRepositoryCustom {

}

interface SampleIndexRepositoryCustom {

    /** 分页查询(分页参数在 dto 中) */
    Page<SampleIndex> pagination(SampleIndexQueryDto dto);

    /** 分页查询 */
    Page<SampleIndex> pagination(SampleIndexQueryDto dto, Pageable pageable);
}
