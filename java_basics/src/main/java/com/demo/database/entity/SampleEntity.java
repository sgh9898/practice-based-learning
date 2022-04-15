package com.demo.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 演示类 Entity
 *
 * @author Song gh on 2022/04/14.
 */
@Entity
@Getter
@Setter
@Table(name = "sample_entity")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SampleEntity {

    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 名称 */
    private String name;

}

