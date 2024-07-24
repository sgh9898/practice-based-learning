package com.sgh.demo.common.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface Neo4jPersonRepository extends Neo4jRepository<com.sgh.demo.common.neo4j.Neo4jPerson, Long> {

    com.sgh.demo.common.neo4j.Neo4jPerson findFirstByName(String name);

    List<com.sgh.demo.common.neo4j.Neo4jPerson> findAllByTeammatesName(String name);

}