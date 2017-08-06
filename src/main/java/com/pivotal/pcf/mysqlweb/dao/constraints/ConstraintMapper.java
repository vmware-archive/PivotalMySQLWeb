/*
PivotalMySQLWeb

Copyright (c) 2017-Present Pivotal Software, Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.pivotal.pcf.mysqlweb.dao.constraints;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConstraintMapper implements RowMapper<Constraint> {
    @Override
    public Constraint mapRow(ResultSet resultSet, int i) throws SQLException {
        Constraint constraint = new Constraint();
        constraint.setCatalog(resultSet.getString("CONSTRAINT_CATALOG"));
        constraint.setSchemaName(resultSet.getString("CONSTRAINT_SCHEMA"));
        constraint.setConstraintName(resultSet.getString("CONSTRAINT_NAME"));
        constraint.setTableName(resultSet.getString("TABLE_NAME"));
        constraint.setConstraintType(resultSet.getString("CONSTRAINT_TYPE"));
        return constraint;
    }
}
