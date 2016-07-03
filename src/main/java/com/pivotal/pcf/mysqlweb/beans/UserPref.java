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

    /*
    public UserPref(int recordsToDisplay, int maxRecordsinSQLQueryWindow, String autoCommit, int historySize) {
        this.recordsToDisplay = recordsToDisplay;
        this.maxRecordsinSQLQueryWindow = maxRecordsinSQLQueryWindow;
        this.autoCommit = autoCommit;
        this.historySize = historySize;
    }
*/
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
