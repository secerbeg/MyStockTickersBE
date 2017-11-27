package com.stocktracker.weblayer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.stocktracker.common.JSONDateConverter;
import com.stocktracker.common.JSONMoneySerializer;
import com.stocktracker.servicelayer.service.StockNoteSourceService;
import com.stocktracker.servicelayer.service.StockQuoteService;
import com.stocktracker.servicelayer.service.stockinformationprovider.StockTickerQuote;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class StockAnalystConsensusDTO extends StockTickerQuote implements StockQuoteService.StockQuoteContainer,
                                                                          StockNoteSourceService.StockNoteSourceDTOContainer
{
    /*
     * Entity (DB columns)
     */
    private Integer id;
    private Integer customerId;
    private String comments;
    private Integer analystStrongBuyCount;
    private Integer analystBuyCount;
    private Integer analystHoldCount;
    private Integer analystUnderPerformCount;
    private Integer analystSellCount;
    private String analystSentimentDate;
    @JsonSerialize( using = JSONMoneySerializer.class )
    private BigDecimal avgAnalystPriceTarget;
    @JsonSerialize( using = JSONMoneySerializer.class )
    private BigDecimal lowAnalystPriceTarget;
    @JsonSerialize( using = JSONMoneySerializer.class )
    private BigDecimal highAnalystPriceTarget;
    private String analystPriceDate;
    private Integer notesSourceId;
    private String notesSourceName;

    /*
     * Calculated columns
     */
    private String companyName;

    public static StockAnalystConsensusDTO newInstance()
    {
        return new StockAnalystConsensusDTO();
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

    public void setCustomerId( Integer customerId )
    {
        this.customerId = customerId;
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments( String comments )
    {
        this.comments = comments;
    }

    public Integer getAnalystBuyCount()
    {
        return analystBuyCount;
    }

    public void setAnalystBuyCount( Integer analystBuyCount )
    {
        this.analystBuyCount = analystBuyCount;
    }

    public Integer getAnalystSellCount()
    {
        return analystSellCount;
    }

    public void setAnalystSellCount( Integer analystSellCount )
    {
        this.analystSellCount = analystSellCount;
    }

    public Integer getAnalystHoldCount()
    {
        return analystHoldCount;
    }

    public void setAnalystHoldCount( Integer analystHoldCount )
    {
        this.analystHoldCount = analystHoldCount;
    }

    public BigDecimal getAvgAnalystPriceTarget()
    {
        return avgAnalystPriceTarget;
    }

    public void setAvgAnalystPriceTarget( BigDecimal avgAnalystPriceTarget )
    {
        this.avgAnalystPriceTarget = avgAnalystPriceTarget;
    }

    public BigDecimal getLowAnalystPriceTarget()
    {
        return lowAnalystPriceTarget;
    }

    public void setLowAnalystPriceTarget( BigDecimal lowAnalystPriceTarget )
    {
        this.lowAnalystPriceTarget = lowAnalystPriceTarget;
    }

    public BigDecimal getHighAnalystPriceTarget()
    {
        return highAnalystPriceTarget;
    }

    public void setHighAnalystPriceTarget( BigDecimal highAnalystPriceTarget )
    {
        this.highAnalystPriceTarget = highAnalystPriceTarget;
    }

    @Override
    public String getCompanyName()
    {
        return this.companyName;
    }

    @Override
    public void setCompanyName( final String companyName )
    {
        this.companyName = companyName;
    }

    public Integer getAnalystStrongBuyCount()
    {
        return analystStrongBuyCount;
    }

    public void setAnalystStrongBuyCount( Integer analystStrongBuyCount )
    {
        this.analystStrongBuyCount = analystStrongBuyCount;
    }

    public Integer getAnalystUnderPerformCount()
    {
        return analystUnderPerformCount;
    }

    public void setAnalystUnderPerformCount( Integer analystUnderPerformCount )
    {
        this.analystUnderPerformCount = analystUnderPerformCount;
    }

    public String getAnalystSentimentDate()
    {
        return analystSentimentDate;
    }

    public void setAnalystSentimentDate( final Timestamp analystSentimentDate )
    {
        if ( analystSentimentDate == null )
        {
            this.analystSentimentDate = null;
        }
        else
        {
            this.analystSentimentDate = JSONDateConverter.toY4MMDD( analystSentimentDate );
        }
    }

    public void setAnalystSentimentDate( final String analystSentimentDate )
    {
        this.analystSentimentDate = analystSentimentDate;
    }

    public String getAnalystPriceDate()
    {
        return analystPriceDate;
    }

    public void setAnalystPriceDate( final String analystPriceDate )
    {
        this.analystPriceDate = analystPriceDate;
    }

    public void setAnalystPriceDate( final Timestamp analystPriceDate )
    {
        if ( analystPriceDate == null )
        {
            this.analystPriceDate = null;
        }
        else
        {
            this.analystPriceDate = JSONDateConverter.toY4MMDD( analystPriceDate );
        }
    }

    public Integer getNotesSourceId()
    {
        return notesSourceId;
    }

    public void setNotesSourceId( final Integer notesSourceId )
    {
        this.notesSourceId = notesSourceId;
    }

    public String getNotesSourceName()
    {
        return notesSourceName;
    }

    public void setNotesSourceName( final String notesSourceName )
    {
        this.notesSourceName = notesSourceName;
    }


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final StockAnalystConsensusDTO that = (StockAnalystConsensusDTO) o;

        return id.equals( that.id );
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder( "StockAnalystConsensusDTO{" );
        sb.append( "id=" ).append( id );
        sb.append( ", customerId=" ).append( customerId );
        sb.append( ", comments='" ).append( comments ).append( '\'' );
        sb.append( ", analystStrongBuyCount=" ).append( analystStrongBuyCount );
        sb.append( ", analystBuyCount=" ).append( analystBuyCount );
        sb.append( ", analystHoldCount=" ).append( analystHoldCount );
        sb.append( ", analystUnderPerformCount=" ).append( analystUnderPerformCount );
        sb.append( ", analystSellCount=" ).append( analystSellCount );
        sb.append( ", analystSentimentDate='" ).append( analystSentimentDate ).append( '\'' );
        sb.append( ", avgAnalystPriceTarget=" ).append( avgAnalystPriceTarget );
        sb.append( ", lowAnalystPriceTarget=" ).append( lowAnalystPriceTarget );
        sb.append( ", highAnalystPriceTarget=" ).append( highAnalystPriceTarget );
        sb.append( ", analystPriceDate='" ).append( analystPriceDate ).append( '\'' );
        sb.append( ", notesSourceId=" ).append( notesSourceId );
        sb.append( ", notesSourceName='" ).append( notesSourceName ).append( '\'' );
        sb.append( ", companyName='" ).append( companyName ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}
