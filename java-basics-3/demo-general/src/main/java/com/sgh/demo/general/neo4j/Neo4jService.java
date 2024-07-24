package com.sgh.demo.common.neo4j;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * Neo4j 示例
 *
 * @author Song gh on 2023/8/4.
 */
@Service
public class Neo4jService {

    @Resource
    private com.sgh.demo.common.neo4j.Neo4jPersonRepository neo4JRepository;

    /** 创建新节点 */
    void createNode(String name) {
        neo4JRepository.save(new com.sgh.demo.common.neo4j.Neo4jPerson(name));
    }

    /** 建立节点关系 */
    public void buildUpRelationships(String name1, String name2, Integer type) {
        // 查询节点, 没有则新建
        com.sgh.demo.common.neo4j.Neo4jPerson person1 = neo4JRepository.findFirstByName(name1);
        if (person1 == null) {
            person1 = new com.sgh.demo.common.neo4j.Neo4jPerson(name1);
            neo4JRepository.save(person1);
        }
        com.sgh.demo.common.neo4j.Neo4jPerson person2 = neo4JRepository.findFirstByName(name2);
        if (person2 == null) {
            person2 = new com.sgh.demo.common.neo4j.Neo4jPerson(name2);
            neo4JRepository.save(person2);
        }
        // 建立关系
        if (type == 1) {
            person1.worksWith(person2);
            person2.worksWith(person1);
        } else if (type == 2) {
            person1.addOutgoing(person2);
            person2.addIncoming(person1);
        } else if (type == 3) {
            person2.addOutgoing(person1);
            person1.addIncoming(person2);
        }
        neo4JRepository.save(person1);
        neo4JRepository.save(person2);
    }
}
