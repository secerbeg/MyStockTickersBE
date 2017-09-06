package com.stocktracker.repositorylayer.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table( name = "stock_note_stock", schema = "stocktracker", catalog = "" )
@IdClass( StockNoteStockEntityPK.class )
public class StockNoteStockEntity
{
    private Integer customerId;
    private Integer stockNoteId;
    private String tickerSymbol;
    private BigDecimal stockPrice;

    @Id
    @Basic
    @Column( name = "stock_note_id", nullable = false )
    public Integer getStockNoteId()
    {
        return stockNoteId;
    }

    public void setStockNoteId( final Integer stockNoteId )
    {
        this.stockNoteId = stockNoteId;
    }

    @Id
    @Basic
    @Column( name = "ticker_symbol", nullable = false, length = 5 )
    public String getTickerSymbol()
    {
        return tickerSymbol;
    }

    public void setTickerSymbol( final String tickerSymbol )
    {
        this.tickerSymbol = tickerSymbol;
    }

    @Basic
    @Column( name = "stock_price", nullable = false, precision = 2 )
    public BigDecimal getStockPrice()
    {
        return stockPrice;
    }

    public void setStockPrice( final BigDecimal stockPrice )
    {
        this.stockPrice = stockPrice;
    }

    @Id
    @Column( name = "customer_id" )
    public Integer getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId( final Integer customerId )
    {
        this.customerId = customerId;
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

        final StockNoteStockEntity that = (StockNoteStockEntity) o;

        if ( stockNoteId != null
             ? !stockNoteId.equals( that.stockNoteId )
             : that.stockNoteId != null )
        {
            return false;
        }
        if ( tickerSymbol != null
             ? !tickerSymbol.equals( that.tickerSymbol )
             : that.tickerSymbol != null )
        {
            return false;
        }
        if ( stockPrice != null
             ? !stockPrice.equals( that.stockPrice )
             : that.stockPrice != null )
        {
            return false;
        }
        if ( customerId != null
             ? !customerId.equals( that.customerId )
             : that.customerId != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = customerId != null
                     ? customerId.hashCode()
                     : 0;
        result = 31 * result + (stockNoteId != null
                                ? stockNoteId.hashCode()
                                : 0);
        result = 31 * result + (tickerSymbol != null
                                ? tickerSymbol.hashCode()
                                : 0);
        result = 31 * result + (stockPrice != null
                                ? stockPrice.hashCode()
                                : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "StockNoteStockEntity{" +
               "stockNoteId=" + stockNoteId +
               ", tickerSymbol='" + tickerSymbol + '\'' +
               ", stockPrice=" + stockPrice +
               ", customerId=" + customerId +
               '}';
    }
}
