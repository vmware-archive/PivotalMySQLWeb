package com.pivotal.pcf.mysqlweb.beans;

public class Result
{
    private String command;
    private String message;

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
}
