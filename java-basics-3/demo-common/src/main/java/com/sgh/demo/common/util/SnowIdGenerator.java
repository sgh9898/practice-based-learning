package com.sgh.demo.common.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * [实体类主键生成器] 雪花Id
 * <pre>
 * 1. 使用时在实体类主键应用以下注解, 注意其中 "strategy" 需要对应本生成器的位置
 * {@code
 *         @Id
 *         @GeneratedValue(generator = "snowId")
 *         @GenericGenerator(name = "snowId", strategy = "com.xxx.xxx.SnowIdGenerator")
 * }
 * 2. 主键重复时 repository.save() 会抛出 {@link org.springframework.dao.DataIntegrityViolationException} 异常,
 * 需要注意的是此时实体类主键已被赋值, 重试前需要将主键重置为 null, 可以使用以下方法
 * {@code
 *         while (true) {
 *             try {
 *                 aiModelRepository.save(aiModel);
 *                 break;
 *             } catch (DataIntegrityViolationException e) {
 *                 aiModel.setId(null);
 *             }
 *         }
 * } </pre>
 *
 * @author Song gh
 * @version 2024/07/03
 */
public class SnowIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        return SnowIdGenerator.uniqueLong();
    }
}