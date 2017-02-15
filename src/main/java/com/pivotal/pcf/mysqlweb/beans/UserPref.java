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
package com.pivotal.pcf.mysqlweb.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:preferences.properties")
public class UserPref
{
    @Value("${recordsToDisplay}")
    private int recordsToDisplay;

    @Value("${maxRecordsinSQLQueryWindow}")
    private int maxRecordsinSQLQueryWindow;

    @Value("${autoCommit}")
    private String autoCommit;

    @Value("${historySize}")
    private int historySize;

    public UserPref()
    {
    }

    public int getRecordsToDisplay() {
        return recordsToDisplay;
    }

    public void setRecordsToDisplay(int recordsToDisplay) {
        this.recordsToDisplay = recordsToDisplay;
    }

    public int getMaxRecordsinSQLQueryWindow() {
        return maxRecordsinSQLQueryWindow;
    }

    public void setMaxRecordsinSQLQueryWindow(int maxRecordsinSQLQueryWindow) {
        this.maxRecordsinSQLQueryWindow = maxRecordsinSQLQueryWindow;
    }

    public String getAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(String autoCommit) {
        this.autoCommit = autoCommit;
    }


    public int getHistorySize() {
        return historySize;
    }

    public void setHistorySize(int historySize) {
        this.historySize = historySize;
    }

    @Override
    public String toString() {
        return "UserPref [recordsToDisplay=" + recordsToDisplay
                + ", maxRecordsinSQLQueryWindow=" + maxRecordsinSQLQueryWindow
                + ", autoCommit=" + autoCommit + ", historySize=" + historySize
                + "]";
    }



}
