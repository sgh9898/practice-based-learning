package com.demo.excluded;


class Database {

//    /** 数据库查询 Elastic */
//    public JSONArray queryFromDatabase(String sql) {
//        // 连接数据库
//        JdbcTemplate jdbcTemplate = DataSourceUtil.getJdbcTemplate(
//                source.getType(), source.getUrl(), source.getAccount(), source.getPassword());
//        Connection connection;
//        JSONArray dataJsonArray = new JSONArray();
//        try {
//            connection = DataSourceUtils.getConnection(
//                    Objects.requireNonNull(jdbcTemplate.getDataSource()));
//            Statement statement;
//            ResultSet resultSet;
//
//            // 根据 sql 获取数据
//            statement = connection.createStatement();
//            resultSet = statement.executeQuery(sql);
//
//            // 处理查询结果
//            int columnCount = resultSet.getMetaData().getColumnCount();
//            while (resultSet.next()) {
//                // 单行数据转为 Json Object
//                JSONObject jsonObject = new JSONObject();
//                for (int i = 0; i <= columnCount; i++) {
//                    ResultSetMetaData metaData = resultSet.getMetaData();
//                    String key = metaData.getColumnName(i);
//                    String val = resultSet.getString(key);
//                    jsonObject.put(key, val);
//                }
//                // 存储单行数据
//                dataJsonArray.add(jsonObject);
//            }
//            // 关闭
//            Objects.requireNonNull(statement).close();
//            Objects.requireNonNull(resultSet).close();
//        } catch (Exception e) {
//            throw new RuntimeException("数据库查询失败，检查连接或sql语句");
//        } finally {
//            // 释放数据库连接
//            DataSourceUtil.releaseConnection(jdbcTemplate);
//        }
//        return dataJsonArray;
//    }


}
