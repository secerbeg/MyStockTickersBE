package com.stocktracker.common;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * The dates/times that come from the web layer are strings in the format of yyyy-MM-dd'T'HH:mm:ss.SSS'Z'.
 * This class provides methods of converting from this string formation to a date object format.
 */
public class JSONDateConverter
{
    public static Timestamp toTimestamp( final String jsonUTCDate )
    {
        SimpleDateFormat dateFormat;
        if ( jsonUTCDate.contains( "T" ) )
        {
            dateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
        }
        else
        {
            dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        }
        dateFormat.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        Date parsedDate = null;
        Timestamp timestamp = null;
        try
        {
            parsedDate = dateFormat.parse( jsonUTCDate );
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        }
        catch ( ParseException e )
        {
            e.printStackTrace();
        }
        return timestamp;
    }

    /**
     * Converts a timestamp into a YYYY-MM-DD string
     * @param utcTimestamp
     * @return null if {@code utcTimestamp} is null
     */
    public static String toY4MMDD( final Timestamp utcTimestamp )
    {
        if ( utcTimestamp == null )
        {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        return dateFormat.format( utcTimestamp );
    }

    /**
     * Converts a timestamp into a full JSON date and time string
     * @param utcTimestamp
     * @return null if {@code utcTimestamp} is null
     */
    public static String toDateAndTime( final Timestamp utcTimestamp )
    {
        if ( utcTimestamp == null )
        {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
        return dateFormat.format( utcTimestamp );
    }

}
