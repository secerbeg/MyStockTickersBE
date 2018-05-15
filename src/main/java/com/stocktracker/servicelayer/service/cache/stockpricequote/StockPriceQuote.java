package com.stocktracker.servicelayer.service.cache.stockpricequote;

import com.stocktracker.servicelayer.service.cache.common.AsyncCacheEntryState;
import com.stocktracker.servicelayer.service.stocks.StockPriceQuoteContainer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * This class contains the information for a Stock Price Quote.
 * Information is gathered about the stock company from IEXTrading and some information from a Quote from IEXTrading
 * along with the most recent (last price) stock price.
 *
 * Created by mike on 12/10/2016.
 */
@Component
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
public class StockPriceQuote implements StockPriceQuoteContainer
{
    private String tickerSymbol;
    /*private String companyName;
    private String sector;
    private String industry;
    */
    private BigDecimal lastPrice;
    private Timestamp lastPriceChange;
    private AsyncCacheEntryState stockPriceQuoteCacheState;
    //private AsyncCacheEntryState stockQuoteCacheState;
    private Timestamp expirationTime;
    //private BigDecimal openPrice;
    private String error;

    @Override
    public AsyncCacheEntryState getStockPriceQuoteCacheState()
    {
        return stockPriceQuoteCacheState;
    }

    @Override
    public void setStockPriceQuoteCacheState( final AsyncCacheEntryState asyncCacheEntryState )
    {
        this.stockPriceQuoteCacheState = asyncCacheEntryState;
    }

    /*
    public AsyncCacheEntryState getStockQuoteCacheState()
    {
        return stockQuoteCacheState;
    }

    public void setStockQuoteCacheState( final AsyncCacheEntryState stockQuoteCacheState )
    {
        Objects.requireNonNull( stockQuoteCacheState, "Argument stockQuoteCacheState cannot be null" );
        this.stockQuoteCacheState = stockQuoteCacheState;
    }
    */

//    /**
//     * Set the stock quote status.
//     * @param stockQuoteEntityCacheState State of the {@code stockQuoteEntity}
//     * @param stockQuoteEntity
//     */
//    @Override
//    public void setStockQuoteEntity( final AsyncCacheEntryState stockQuoteEntityCacheState,
//                                     final StockQuoteEntity stockQuoteEntity )
//    {
//        Objects.requireNonNull( stockQuoteEntityCacheState, "Argument stockQuoteEntityCacheState cannot be null" );
//        /*
//         * will be null while it's being retrieved in the background.
//         */
//        if ( stockQuoteEntity != null )
//        {
//            this.openPrice = stockQuoteEntity.getOpenPrice();
//        }
//        this.stockQuoteCacheState = stockQuoteEntityCacheState;
//    }

    @Override
    public String getTickerSymbol()
    {
        return tickerSymbol;
    }

    @Override
    public void setTickerSymbol( final String tickerSymbol )
    {
        this.tickerSymbol = tickerSymbol;
    }

    @Override
    public BigDecimal getLastPrice()
    {
        return lastPrice;
    }

    @Override
    public void setLastPrice( final BigDecimal lastPrice )
    {
        this.lastPrice = lastPrice;// .divide( new BigDecimal( 1 ), 2,  BigDecimal.ROUND_HALF_UP  );
        if ( lastPrice != null )
        {
            lastPrice.setScale( 2, RoundingMode.HALF_UP );
        }
    }

    /*@Override
    public Timestamp getLastPriceChange()
    {
        return lastPriceChange;
    }

    @Override
    public void setLastPriceChange( Timestamp lastPriceChange )
    {
        this.lastPriceChange = lastPriceChange;
    }

    @Override
    public void setOpenPrice( final BigDecimal openPrice )
    {
        this.openPrice = openPrice;
    }

    @Override
    public BigDecimal getOpenPrice()
    {
        return this.openPrice;
    }

    @Override
    public String getCompanyName()
    {
        return companyName;
    }

    @Override
    public void setCompanyName( final String companyName )
    {
        this.companyName = companyName;
    }

    @Override
    public String getSector()
    {
        return this.sector;
    }

    @Override
    public void setSector( final String sector )
    {
        this.sector = sector;
    }

    @Override
    public String getIndustry()
    {
        return this.industry;
    }

    @Override
    public void setIndustry( final String industry )
    {
        this.industry = industry;
    }*/

    @Override
    public Timestamp getExpirationTime()
    {
        return expirationTime;
    }

    @Override
    public void setExpirationTime( final Timestamp expirationTime )
    {
        this.expirationTime = expirationTime;
    }

    /**
     * This method is called by the cache when a new {@code StockPriceQuote} is received.
     * @param cacheState
     * @param stockPriceQuote
     */
    @Override
    public void setStockPriceQuote( final AsyncCacheEntryState cacheState, final StockPriceQuote stockPriceQuote )
    {
        this.stockPriceQuoteCacheState = cacheState;
        BeanUtils.copyProperties( stockPriceQuote, this );
    }
    public String getError()
    {
        return error;
    }

    public void setError( final String error )
    {
        this.error = error;
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
        final StockPriceQuote that = (StockPriceQuote) o;
        return Objects.equals( tickerSymbol, that.tickerSymbol );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( tickerSymbol );
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder( "StockPriceQuoteEntity{" );
        sb.append( "tickerSymbol='" ).append( tickerSymbol ).append( '\'' );
        //sb.append( ", companyName'=" ).append( companyName ).append( '\'' );
        sb.append( ", lastPrice=" ).append( lastPrice );
        sb.append( ", lastPriceChange=" ).append( lastPriceChange );
        sb.append( ", openPrice=" ).append( lastPrice );
        sb.append( ", stockPriceQuoteCacheState=" ).append( stockPriceQuoteCacheState );
        //sb.append( ", stockQuoteCacheState=" ).append( stockQuoteCacheState );
        sb.append( ", expirationTime=" ).append( expirationTime );
        //sb.append( ", industry'=" ).append( industry ).append( '\'' );
        //sb.append( ", sector'=" ).append( sector ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}