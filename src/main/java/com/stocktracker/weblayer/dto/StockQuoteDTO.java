package com.stocktracker.weblayer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.stocktracker.common.JSONTimestampDateTimeSerializer;
import com.stocktracker.repositorylayer.entity.StockQuoteEntity;
import com.stocktracker.servicelayer.service.cache.common.AsyncCacheDataReceiver;
import com.stocktracker.servicelayer.service.cache.common.AsyncCacheEntryState;
import com.stocktracker.weblayer.dto.common.DatabaseEntityDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * This DTO contains the values obtained from a IEXTrading Quote and stored in the STOCK_QUOTE table.
 */
@Component
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
@Qualifier( "stockQuoteDTO")
public class StockQuoteDTO extends DatabaseEntityDTO<String>
                           implements AsyncCacheDataReceiver<String,StockQuoteEntity,String>

{
    private String tickerSymbol;
    private String companyName;
    private String calculationPrice;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal latestPrice;
    private String latestPriceSource;
    private String latestPriceTime;
    private Long latestUpdate;
    private BigDecimal latestVolume;
    private BigDecimal thirtyDayAvgVolume;
    private BigDecimal changeAmount;
    private BigDecimal delayedPrice;
    private Long delayedPriceTime;
    private BigDecimal previousClose;
    private BigDecimal changePercent;
    private BigDecimal marketCap;
    private BigDecimal peRatio;
    private BigDecimal week52High;
    private BigDecimal week52Low;
    private BigDecimal ytdChangePercent;
    private Timestamp lastQuoteRequestDate;
    private String discontinuedInd;

    private AsyncCacheEntryState cacheState;
    private String cacheError;
    @JsonSerialize( using = JSONTimestampDateTimeSerializer.class )
    private Timestamp expirationTime;

    public AsyncCacheEntryState getCacheState()
    {
        return this.cacheState;
    }

    @Override
    public void setCacheKey( final String cacheKey )
    {
        this.tickerSymbol = cacheKey;
    }

    @Override
    public String getCacheKey()
    {
        return this.tickerSymbol;
    }

    @Override
    public String getASyncKey()
    {
        return this.tickerSymbol;
    }

    @Override
    public void setAsyncKey( final String asyncKey )
    {
        this.tickerSymbol = asyncKey;
    }

    @Override
    public void setCachedData( final StockQuoteEntity stockQuoteEntity )
    {
        if ( stockQuoteEntity != null )
        {
            BeanUtils.copyProperties( stockQuoteEntity, this );
        }
    }

    @Override
    public StockQuoteEntity getCachedData()
    {
        return null;
    }

    @Override
    public void setCacheState( final AsyncCacheEntryState cacheState )
    {
        this.cacheState = cacheState;
    }

    @Override
    public void setCacheError( final String cacheError )
    {
        this.cacheError = cacheError;
    }

    @Override
    public String getCacheError()
    {
        return this.cacheError;
    }

    public Timestamp getExpirationTime()
    {
        return expirationTime;
    }

    public void setExpirationTime( final Timestamp expirationTime )
    {
        this.expirationTime = expirationTime;
    }


    public String getTickerSymbol()
    {
        return tickerSymbol;
    }

    public void setTickerSymbol( final String tickerSymbol )
    {
        this.tickerSymbol = tickerSymbol;
    }

    public String getCalculationPrice()
    {
        return calculationPrice;
    }

    public void setCalculationPrice( final String calculationPrice )
    {
        this.calculationPrice = calculationPrice;
    }

    public BigDecimal getOpenPrice()
    {
        return openPrice;
    }

    public void setOpenPrice( final BigDecimal openPrice )
    {
        this.openPrice = openPrice;
    }

    public BigDecimal getClosePrice()
    {
        return closePrice;
    }
    public void setClosePrice( final BigDecimal closePrice )
    {
        this.closePrice = closePrice;
    }

    public BigDecimal getHighPrice()
    {
        return highPrice;
    }
    public void setHighPrice( final BigDecimal highPrice )
    {
        this.highPrice = highPrice;
    }

    public BigDecimal getLowPrice()
    {
        return lowPrice;
    }
    public void setLowPrice( final BigDecimal lowPrice )
    {
        this.lowPrice = lowPrice;
    }

    public BigDecimal getLatestPrice()
    {
        return latestPrice;
    }
    public void setLatestPrice( final BigDecimal latestPrice )
    {
        this.latestPrice = latestPrice;
    }

    public String getLatestPriceSource()
    {
        return latestPriceSource;
    }
    public void setLatestPriceSource( final String latestPriceSource )
    {
        this.latestPriceSource = latestPriceSource;
    }

    public String getLatestPriceTime()
    {
        return latestPriceTime;
    }
    public void setLatestPriceTime( final String latestPriceTime )
    {
        this.latestPriceTime = latestPriceTime;
    }

    public Long getLatestUpdate()
    {
        return latestUpdate;
    }
    public void setLatestUpdate( final Long latestUpdate )
    {
        this.latestUpdate = latestUpdate;
    }

    public BigDecimal getLatestVolume()
    {
        return latestVolume;
    }
    public void setLatestVolume( final BigDecimal latestVolume )
    {
        this.latestVolume = latestVolume;
    }

    public BigDecimal getDelayedPrice()
    {
        return delayedPrice;
    }
    public void setDelayedPrice( final BigDecimal delayedPrice )
    {
        this.delayedPrice = delayedPrice;
    }

    public Long getDelayedPriceTime()
    {
        return delayedPriceTime;
    }
    public void setDelayedPriceTime( final Long delayedPriceTime )
    {
        this.delayedPriceTime = delayedPriceTime;
    }

    public BigDecimal getPreviousClose()
    {
        return previousClose;
    }
    public void setPreviousClose( final BigDecimal previousClose )
    {
        this.previousClose = previousClose;
    }

    public BigDecimal getChangePercent()
    {
        return changePercent;
    }
    public void setChangePercent( final BigDecimal changePercent )
    {
        this.changePercent = changePercent;
    }

    public BigDecimal getThirtyDayAvgVolume()
    {
        return thirtyDayAvgVolume;
    }
    public void setThirtyDayAvgVolume( final BigDecimal thirtyDayAvgVolume )
    {
        this.thirtyDayAvgVolume = thirtyDayAvgVolume;
    }

    public BigDecimal getMarketCap()
    {
        return marketCap;
    }
    public void setMarketCap( final BigDecimal marketCap )
    {
        this.marketCap = marketCap;
    }

    public BigDecimal getPeRatio()
    {
        return peRatio;
    }
    public void setPeRatio( final BigDecimal peRatio )
    {
        this.peRatio = peRatio;
    }

    public BigDecimal getWeek52High()
    {
        return week52High;
    }
    public void setWeek52High( final BigDecimal week52High )
    {
        this.week52High = week52High;
    }

    public BigDecimal getWeek52Low()
    {
        return week52Low;
    }
    public void setWeek52Low( final BigDecimal week52Low )
    {
        this.week52Low = week52Low;
    }

    public BigDecimal getYtdChangePercent()
    {
        return ytdChangePercent;
    }
    public void setYtdChangePercent( final BigDecimal ytdChangePercent )
    {
        this.ytdChangePercent = ytdChangePercent;
    }

    public Timestamp getLastQuoteRequestDate()
    {
        return lastQuoteRequestDate;
    }
    public void setLastQuoteRequestDate( final Timestamp lastQuoteRequestDate )
    {
        this.lastQuoteRequestDate = lastQuoteRequestDate;
    }

    public String getDiscontinuedInd()
    {
        return discontinuedInd;
    }
    public void setDiscontinuedInd( final String discontinuedInd )
    {
        this.discontinuedInd = discontinuedInd;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName( final String companyName )
    {
        this.companyName = companyName;
    }

    public BigDecimal getChangeAmount()
    {
        return changeAmount;
    }

    public void setChangeAmount( final BigDecimal changeAmount )
    {
        this.changeAmount = changeAmount;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder( "StockQuoteDTO{" );
        sb.append( "tickerSymbol='" ).append( tickerSymbol ).append( '\'' );
        sb.append( ", companyName='" ).append( companyName ).append( '\'' );
        sb.append( ", calculationPrice='" ).append( calculationPrice ).append( '\'' );
        sb.append( ", openPrice=" ).append( openPrice );
        sb.append( ", closePrice=" ).append( closePrice );
        sb.append( ", highPrice=" ).append( highPrice );
        sb.append( ", lowPrice=" ).append( lowPrice );
        sb.append( ", latestPrice=" ).append( latestPrice );
        sb.append( ", latestPriceSource='" ).append( latestPriceSource ).append( '\'' );
        sb.append( ", latestPriceTime=" ).append( latestPriceTime );
        sb.append( ", latestUpdate=" ).append( latestUpdate );
        sb.append( ", latestVolume=" ).append( latestVolume );
        sb.append( ", changeAmount=" ).append( changeAmount );
        sb.append( ", delayedPrice=" ).append( delayedPrice );
        sb.append( ", delayedPriceTime=" ).append( delayedPriceTime );
        sb.append( ", previousClose=" ).append( previousClose );
        sb.append( ", changePercent=" ).append( changePercent );
        sb.append( ", thirtyDayAvgVolume=" ).append( thirtyDayAvgVolume );
        sb.append( ", marketCap=" ).append( marketCap );
        sb.append( ", peRatio=" ).append( peRatio );
        sb.append( ", week52High=" ).append( week52High );
        sb.append( ", week52Low=" ).append( week52Low );
        sb.append( ", ytdChangePercent=" ).append( ytdChangePercent );
        sb.append( ", lastQuoteRequestDate=" ).append( lastQuoteRequestDate );
        sb.append( ", discontinuedInd='" ).append( discontinuedInd ).append( '\'' );
        sb.append( ", cacheState=" ).append( cacheState );
        sb.append( ", cacheError=" ).append( cacheError );
        sb.append( ", super=" ).append( super.toString() );
        sb.append( '}' );
        return sb.toString();
    }
}
