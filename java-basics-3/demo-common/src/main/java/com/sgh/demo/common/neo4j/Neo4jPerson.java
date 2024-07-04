package com.sgh.demo.common.neo4j;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** Neo4j 示例节点 */
@Node
@Data
public class Neo4jPerson {

    /**
     * Neo4j doesn't REALLY have bidirectional relationships. It just means when querying
     * to ignore the direction of the relationship.
     */
    @Relationship("Teammate")
    public Set<Neo4jPerson> teammates;

    /** 存放终点(起点为当前节点) */
    @Relationship(value = "Direction", direction = Relationship.Direction.OUTGOING)
    public Set<Neo4jPerson> outgoing;

    /** 存放起点(终点为当前节点) */
    @Relationship(value = "Direction", direction = Relationship.Direction.INCOMING)
    public Set<Neo4jPerson> incoming;

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public Neo4jPerson() {
    }

    public Neo4jPerson(String name) {
        this.name = name;
    }

    public void worksWith(Neo4jPerson person) {
        if (teammates == null) {
            teammates = new HashSet<>();
        }
        teammates.add(person);
    }

    public void addOutgoing(Neo4jPerson person) {
        if (outgoing == null) {
            outgoing = new HashSet<>();
        }
        outgoing.add(person);
    }

    public void addIncoming(Neo4jPerson person) {
        if (incoming == null) {
            incoming = new HashSet<>();
        }
        incoming.add(person);
    }

    public String toString() {
        return this.name + "'s teammates => "
                + Optional.ofNullable(this.teammates).orElse(
                        Collections.emptySet()).stream()
                .map(Neo4jPerson::getName)
                .collect(Collectors.toList());
    }
}