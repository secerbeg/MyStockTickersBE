package com.stocktracker.weblayer.dto;

import com.stocktracker.common.JSONDateConverter;
import com.stocktracker.servicelayer.service.YahooStockService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;

public class StockToBuyDTO implements YahooStockService.YahooStockContainer
{
    /*
     * Entity (DB columns)
     */
    private Integer id;
    private Integer customerId;
    private String tickerSymbol;
    private String comments;
    private BigDecimal buySharesBelow;
    private BigDecimal stockPrice;

    /*
     * Calculated columns
     */
    private BigDecimal lastPrice;
    private String lastPriceChange;
    private String companyName;

    public static StockToBuyDTO newInstance()
    {
        return new StockToBuyDTO();
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

    public String getTickerSymbol()
    {
        return tickerSymbol;
    }

    public void setTickerSymbol( String tickerSymbol )
    {
        this.tickerSymbol = tickerSymbol;
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments( String comments )
    {
        this.comments = comments;
    }

    public BigDecimal getBuySharesBelow()
    {
        return buySharesBelow;
    }

    public void setBuySharesBelow( BigDecimal buySharesBelow )
    {
        this.buySharesBelow = buySharesBelow;
    }

    public BigDecimal getStockPrice()
    {
        return stockPrice;
    }

    public void setStockPrice( BigDecimal stockPrice )
    {
        this.stockPrice = stockPrice;
    }

    @Override
    public void setLastPriceChangeTimestamp( final Timestamp lastPriceChange )
    {
        try
        {
            this.lastPriceChange = JSONDateConverter.toString( lastPriceChange ) ;
        }
        catch ( ParseException e )
        {
            this.lastPriceChange = null;
        }
    }

    @Override
    public Timestamp getLastPriceChangeTimestamp()
    {
        Timestamp returnValue = null;
        if ( this.lastPriceChange != null )
        {
            try
            {
                returnValue = JSONDateConverter.toTimestamp( this.lastPriceChange );
            }
            catch ( ParseException e )
            {
                e.printStackTrace();
            }
        }
        return returnValue;
    }

    public String getLastPriceChange()
    {
        return lastPriceChange;
    }

    public void setLastPriceChange( final String lastPriceChange )
    {
        this.lastPriceChange = lastPriceChange;
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

    public BigDecimal getLastPrice()
    {
        return lastPrice;
    }

    public void setLastPrice( final BigDecimal lastPrice )
    {
        this.lastPrice = lastPrice;
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

        final StockToBuyDTO that = (StockToBuyDTO) o;

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
        final StringBuilder sb = new StringBuilder( "StockAnalyticsDTO{" );
        sb.append( "id=" ).append( id );
        sb.append( ", customerId=" ).append( customerId );
        sb.append( ", tickerSymbol='" ).append( tickerSymbol ).append( '\'' );
        sb.append( ", comments='" ).append( comments ).append( '\'' );
        sb.append( ", buySharesBelow=" ).append( buySharesBelow );
        sb.append( ", stockPrice=" ).append( stockPrice );
        sb.append( ", lastPrice=" ).append( stockPrice );
        sb.append( ", lastPriceChange=" ).append( lastPriceChange );
        sb.append( ", companyName='" ).append( companyName ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}