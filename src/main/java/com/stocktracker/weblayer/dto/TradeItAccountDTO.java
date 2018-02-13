package com.stocktracker.weblayer.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.stocktracker.common.JSONTimestampDateTimeSerializer;
import com.stocktracker.repositorylayer.entity.VersionedEntity;

import java.sql.Timestamp;

/**
 * This class defines the data that will be sent to the client when requesting information for a Customer
 * Created by mike on 5/15/2016.
 */
public class TradeItAccountDTO implements VersionedEntity<Integer>
{
    private Integer id;
    private Integer customerId;
    private String name;
    private String brokerage;
    private Timestamp authTimestamp;
    private Integer version;

    /**
     * Creates a new instance from (@code accountEntity)
     * @return
     */
    public static final TradeItAccountDTO newInstance()
    {
        TradeItAccountDTO tradeItAccountDTO = new TradeItAccountDTO();
        return tradeItAccountDTO;
    }

    private TradeItAccountDTO()
    {
    }

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public Integer getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId( final Integer customerId )
    {
        this.customerId = customerId;
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public String getBrokerage()
    {
        return brokerage;
    }

    public void setBrokerage( final String brokerage )
    {
        this.brokerage = brokerage;
    }

    @JsonSerialize( using = JSONTimestampDateTimeSerializer.class )
    public Timestamp getAuthTimestamp()
    {
        return authTimestamp;
    }

    public void setAuthTimestamp( final Timestamp authTimestamp )
    {
        this.authTimestamp = authTimestamp;
    }

    @Override
    public Integer getVersion()
    {
        return version;
    }

    public void setVersion( final Integer version )
    {
        this.version = version;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder( "TradeItAccountDTO{" );
        sb.append( "id=" ).append( id );
        sb.append( ", customerId=" ).append( customerId );
        sb.append( ", name='" ).append( name ).append( '\'' );
        sb.append( ", brokerage='" ).append( brokerage ).append( '\'' );
        sb.append( ", authTimestamp='" ).append( authTimestamp ).append( '\'' );
        sb.append( ", version=" ).append( version );
        sb.append( '}' );
        return sb.toString();
    }
}
