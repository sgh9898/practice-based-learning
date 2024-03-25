package com.collin.demo.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface Neo4jPersonRepository extends Neo4jRepository<Neo4jPerson, Long> {

    Neo4jPerson findFirstByName(String name);

    List<Neo4jPerson> findAllByTeammatesName(String name);

}