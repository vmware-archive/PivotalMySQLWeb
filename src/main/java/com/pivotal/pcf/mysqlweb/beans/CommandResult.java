package com.pivotal.pcf.mysqlweb.beans;

public class CommandResult
{
    private String command;
    private String message;
    private int rows;
    private String elapsedTime;

    public void setCommand(String command)
    {
        this.command = command;
    }

    public String getCommand()
    {
        return command;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }


    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }


    public String getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    @Override
    public String toString() {
        return "CommandResult [command=" + command + ", message=" + message
                + ", rows=" + rows + ", elapsedTime=" + elapsedTime + "]";
    }

}

