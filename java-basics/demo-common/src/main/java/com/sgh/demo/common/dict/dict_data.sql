-- auto-generated definition
CREATE TABLE dict_data
(
    id             bigint AUTO_INCREMENT
        PRIMARY KEY,
    dict_name      varchar(64)  NULL COMMENT '目录中文名',
    dict_code      varchar(64)  NOT NULL COMMENT '目录编码',
    dict_data_name varchar(32)  NOT NULL COMMENT '词条中文名',
    dict_data_code varchar(32)  NOT NULL COMMENT '词条编码',
    comments       varchar(128) NULL COMMENT '备注'
)
    COMMENT '数据字典';

