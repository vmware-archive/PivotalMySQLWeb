package com.pivotal.pcf.mysqlweb.main;

public class PivotalMySQLWebException extends Exception
{
    public PivotalMySQLWebException()
    {
    }

    public PivotalMySQLWebException(final Throwable cause)
    {
        super(cause);
    }

    public PivotalMySQLWebException
            (final String msg,
             final Throwable cause)
    {
        super(msg, cause);
    }

    public PivotalMySQLWebException(final String msg)
    {
        super(msg);
    }
}
