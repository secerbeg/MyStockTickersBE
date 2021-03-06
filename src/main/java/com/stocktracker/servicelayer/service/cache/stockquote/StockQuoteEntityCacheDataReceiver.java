package com.stocktracker.servicelayer.service.cache.stockquote;

import com.stocktracker.repositorylayer.entity.StockQuoteEntity;
import com.stocktracker.servicelayer.service.cache.common.AsyncCacheDataReceiver;
import com.stocktracker.servicelayer.service.cache.common.AsyncCacheEntryState;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * This class implements the {@code AsyncCacheDataReceiver} interface in order to interact with the {@code StockQuoteAsyncCache}.
 * This class will contain the results of requesting a stock quote from the cache.  It will then be used to by the
 * {@code StockQuoteEntityService} to set the quote values on the target target class that is requesting stock quote
 * information.
 */
@Component
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
public class StockQuoteEntityCacheDataReceiver implements AsyncCacheDataReceiver<String,StockQuoteEntity,String>
{
    private String tickerSymbol;
    private StockQuoteEntity stockQuoteEntity;
    private AsyncCacheEntryState cacheState;
    private String error;
    private Timestamp dataExpiration;

    @Override
    public void setCacheKey( final String key )
    {
        this.tickerSymbol = key;
    }

    /**
     * Key used to retrieve the {@code StockQuoteEntity} from the database.
     * @return
     */
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

    /**
     * Set the StockQuoteEntity data.
     * @param stockQuoteEntity
     */
    @Override
    public void setCachedData( final StockQuoteEntity stockQuoteEntity )
    {
        this.stockQuoteEntity = stockQuoteEntity;
    }

    @Override
    public StockQuoteEntity getCachedData()
    {
        return this.stockQuoteEntity;
    }

    /**
     * Set the cache data state.
     * @param cacheState
     */
    @Override
    public void setCacheState( final AsyncCacheEntryState cacheState )
    {
        this.cacheState = cacheState;
    }

    @Override
    public AsyncCacheEntryState getCacheState()
    {
        return this.cacheState;
    }

    /**
     * Set the error value form the async sourcehronous fetch process.
     * @param error
     */
    @Override
    public void setCacheError( final String error )
    {
        this.error = error;
    }

    @Override
    public String getCacheError()
    {
        return this.error;
    }

    @Override
    public void setExpirationTime( final Timestamp dataExpiration )
    {
        this.dataExpiration = dataExpiration;
    }

    @Override
    public Timestamp getExpirationTime()
    {
        return this.dataExpiration;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder( "StockQuoteEntityCacheDataReceiver{" );
        sb.append( "tickerSymbol='" ).append( tickerSymbol ).append( '\'' );
        sb.append( ", stockQuoteEntity=" ).append( stockQuoteEntity );
        sb.append( ", cacheState=" ).append( cacheState );
        sb.append( ", error='" ).append( error ).append( '\'' );
        sb.append( ", dataExpiration=" ).append( dataExpiration );
        sb.append( '}' );
        return sb.toString();
    }
}
