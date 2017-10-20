package com.stocktracker.repositorylayer.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table( name = "stock_summary", schema = "stocktracker", catalog = "" )
public class StockSummaryEntity
{
    private Integer id;
    private Integer customerId;
    private String tickerSymbol;
    private String comments;
    private Integer analystStrongBuyCount;
    private Integer analystBuyCount;
    private Integer analystHoldCount;
    private Integer analystUnderPerformCount;
    private Integer analystSellCount;
    private Timestamp lastAnalystSentimentDate;
    private Timestamp nextCatalystDate;
    private String nextCatalystDesc;
    private BigDecimal avgAnalystPriceTarget;
    private BigDecimal lowAnalystPriceTarget;
    private BigDecimal highAnalystPriceTarget;
    private BigDecimal buySharesBelow;
    private Timestamp lastAnalystPriceDate;
    private Timestamp createDate;
    private Timestamp updateDate;

    public static StockSummaryEntity newInstance()
    {
        return new StockSummaryEntity();
    }

    @Id
    @Column( name = "id", nullable = false )
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    public Integer getId()
    {
        return id;
    }

    public void setId( final Integer id )
    {
        this.id = id;
    }

    @Basic
    @Column( name = "customer_id" )
    public Integer getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId( final Integer customerId )
    {
        this.customerId = customerId;
    }

    @Basic
    @Column( name = "ticker_symbol" )
    public String getTickerSymbol()
    {
        return tickerSymbol;
    }

    public void setTickerSymbol( final String tickerSymbol )
    {
        this.tickerSymbol = tickerSymbol;
    }

    @Basic
    @Column( name = "comments", nullable = true, length = 45 )
    public String getComments()
    {
        return comments;
    }

    public void setComments( final String comments )
    {
        this.comments = comments;
    }

    @Basic
    @Column( name = "analyst_buy_count", nullable = true )
    public Integer getAnalystBuyCount()
    {
        return analystBuyCount;
    }

    public void setAnalystBuyCount( final Integer analystBuyCount )
    {
        this.analystBuyCount = analystBuyCount;
    }

    @Basic
    @Column( name = "analyst_sell_count", nullable = true )
    public Integer getAnalystSellCount()
    {
        return analystSellCount;
    }

    public void setAnalystSellCount( final Integer analystSellCount )
    {
        this.analystSellCount = analystSellCount;
    }

    @Basic
    @Column( name = "analyst_hold_count", nullable = true )
    public Integer getAnalystHoldCount()
    {
        return analystHoldCount;
    }

    public void setAnalystHoldCount( final Integer analystHoldCount )
    {
        this.analystHoldCount = analystHoldCount;
    }

    @Basic
    @Column( name = "analyst_strong_buy_count", nullable = true )
    public Integer getAnalystStrongBuyCount()
    {
        return analystStrongBuyCount;
    }

    public void setAnalystStrongBuyCount( final Integer analystStrongBuyCount )
    {
        this.analystStrongBuyCount = analystStrongBuyCount;
    }

    @Basic
    @Column( name = "analyst_under_perform_count", nullable = true )
    public Integer getAnalystUnderPerformCount()
    {
        return analystUnderPerformCount;
    }

    public void setAnalystUnderPerformCount( final Integer analystUnderPerformCount )
    {
        this.analystUnderPerformCount = analystUnderPerformCount;
    }


    @Basic
    @Column( name = "next_catalyst_date", nullable = true )
    public Timestamp getNextCatalystDate()
    {
        return nextCatalystDate;
    }

    public void setNextCatalystDate( final Timestamp nextCatalystDate )
    {
        this.nextCatalystDate = nextCatalystDate;
    }

    @Basic
    @Column( name = "next_catalyst_desc", nullable = true, length = 30 )
    public String getNextCatalystDesc()
    {
        return nextCatalystDesc;
    }

    public void setNextCatalystDesc( final String nextCatalystDesc )
    {
        this.nextCatalystDesc = nextCatalystDesc;
    }

    @Basic
    @Column( name = "avg_analyst_price_target", nullable = true, precision = 2 )
    public BigDecimal getAvgAnalystPriceTarget()
    {
        return avgAnalystPriceTarget;
    }

    public void setAvgAnalystPriceTarget( final BigDecimal avgAnalystPriceTarget )
    {
        this.avgAnalystPriceTarget = avgAnalystPriceTarget;
    }

    @Basic
    @Column( name = "low_analyst_price_target", nullable = true, precision = 2 )
    public BigDecimal getLowAnalystPriceTarget()
    {
        return lowAnalystPriceTarget;
    }

    public void setLowAnalystPriceTarget( final BigDecimal lowAnalystPriceTarget )
    {
        this.lowAnalystPriceTarget = lowAnalystPriceTarget;
    }

    @Basic
    @Column( name = "high_analyst_price_target", nullable = true, precision = 2 )
    public BigDecimal getHighAnalystPriceTarget()
    {
        return highAnalystPriceTarget;
    }

    public void setHighAnalystPriceTarget( final BigDecimal highAnalystPriceTarget )
    {
        this.highAnalystPriceTarget = highAnalystPriceTarget;
    }

    @Basic
    @Column( name = "buy_shares_below", nullable = true, precision = 2 )
    public BigDecimal getBuySharesBelow()
    {
        return buySharesBelow;
    }

    public void setBuySharesBelow( final BigDecimal buySharesBelow )
    {
        this.buySharesBelow = buySharesBelow;
    }

    @Basic
    @Column( name = "create_date", nullable = false )
    public Timestamp getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate( final Timestamp createDate )
    {
        this.createDate = createDate;
    }

    @Basic
    @Column( name = "update_date", nullable = true )
    public Timestamp getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate( final Timestamp updateDate )
    {
        this.updateDate = updateDate;
    }

    @Basic
    @Column( name = "last_analyst_sentiment_date", nullable = true )
    public Timestamp getLastAnalystSentimentDate()
    {
        return lastAnalystSentimentDate;
    }

    public void setLastAnalystSentimentDate( final Timestamp lastAnalystSentimentDate )
    {
        this.lastAnalystSentimentDate = lastAnalystSentimentDate;
    }

    @Basic
    @Column( name = "last_analyst_price_date", nullable = true )
    public Timestamp getLastAnalystPriceDate()
    {
        return lastAnalystPriceDate;
    }

    public void setLastAnalystPriceDate( final Timestamp lastAnalystPriceDate )
    {
        this.lastAnalystPriceDate = lastAnalystPriceDate;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !(o instanceof StockSummaryEntity) )
        {
            return false;
        }
        final StockSummaryEntity that = (StockSummaryEntity) o;
        return Objects.equals( id, that.id );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id );
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder( "StockSummaryEntity{" );
        sb.append( "id=" ).append( id );
        sb.append( ", customerId=" ).append( customerId );
        sb.append( ", tickerSymbol='" ).append( tickerSymbol ).append( '\'' );
        sb.append( ", comments='" ).append( comments ).append( '\'' );
        sb.append( ", analystStrongBuyCount=" ).append( analystStrongBuyCount );
        sb.append( ", analystBuyCount=" ).append( analystBuyCount );
        sb.append( ", analystHoldCount=" ).append( analystHoldCount );
        sb.append( ", analystUnderPerformCount=" ).append( analystUnderPerformCount );
        sb.append( ", analystSellCount=" ).append( analystSellCount );
        sb.append( ", lastAnalystSentimentDate=" ).append( lastAnalystSentimentDate );
        sb.append( ", nextCatalystDate=" ).append( nextCatalystDate );
        sb.append( ", nextCatalystDesc='" ).append( nextCatalystDesc ).append( '\'' );
        sb.append( ", avgAnalystPriceTarget=" ).append( avgAnalystPriceTarget );
        sb.append( ", lowAnalystPriceTarget=" ).append( lowAnalystPriceTarget );
        sb.append( ", highAnalystPriceTarget=" ).append( highAnalystPriceTarget );
        sb.append( ", buySharesBelow=" ).append( buySharesBelow );
        sb.append( ", lastAnalystPriceDate=" ).append( lastAnalystPriceDate );
        sb.append( ", createDate=" ).append( createDate );
        sb.append( ", updateDate=" ).append( updateDate );
        sb.append( '}' );
        return sb.toString();
    }

}