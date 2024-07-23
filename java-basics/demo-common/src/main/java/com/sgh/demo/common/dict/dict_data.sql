-- auto-generated definition
CREATE TABLE dict_data
(
    id             bigint AUTO_INCREMENT
        PRIMARY KEY,
    dict_name      varchar(64)                        NULL COMMENT '目录中文名',
    dict_code      varchar(64)                        NOT NULL COMMENT '目录编码',
    dict_data_name varchar(32)                        NOT NULL COMMENT '词条中文名',
    dict_data_code varchar(32)                        NOT NULL COMMENT '词条编码',
    comments       varchar(128)                       NULL COMMENT '备注',
    sort           int                                NULL COMMENT '排序(小数在前)',
    create_time    datetime DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time    datetime DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
)
    COMMENT '数据字典';

CREATE INDEX dict_data_dict_code_index
    ON dict_data (dict_code);

CREATE INDEX dict_data_dict_data_code_index
    ON dict_data (dict_data_code);

CREATE INDEX dict_data_dict_data_name_index
    ON dict_data (dict_data_name);

CREATE INDEX dict_data_dict_name_index
    ON dict_data (dict_name);

