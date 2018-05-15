package com.stocktracker.servicelayer.service.stocks;

import com.stocktracker.common.exceptions.StockNotFoundException;
import com.stocktracker.servicelayer.service.BaseService;
import com.stocktracker.servicelayer.service.cache.common.AsyncCacheFetchMode;
import com.stocktracker.servicelayer.service.cache.stockpricequote.StockPriceQuote;
import com.stocktracker.servicelayer.service.cache.stockpricequote.StockPriceQuoteCache;
import com.stocktracker.servicelayer.service.cache.stockpricequote.StockPriceQuoteCacheEntry;
import com.stocktracker.weblayer.dto.StockPriceQuoteDTO;
import com.stocktracker.weblayer.dto.common.StockPriceQuoteDTOContainer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * This class provides services for StockQuote and StockQuoteDTO class.
 */
@Service
public class StockPriceQuoteService extends BaseService
{
    private StockPriceQuoteCache stockPriceQuoteCache;

    /**
     * Set the stock price quote on all container entries.
     * @param containers
     * @param asyncCacheFetchMode
     */
    public void setStockPriceQuote( final List<? extends StockPriceQuoteDTOContainer> containers,
                                    final AsyncCacheFetchMode asyncCacheFetchMode )
    {
        containers.forEach( stockPriceQuoteContainer -> setStockPriceQuote( stockPriceQuoteContainer, asyncCacheFetchMode ) );
    }

    /**
     * Obtains the stock information from the stock cache with the current stock information including the price,
     * last price change, and company name.
     * This information that was retrieved from the cache, is also saved back to the database stock table.
     * @param container
     * @param asyncCacheFetchMode
     */
    public void setStockPriceQuote( final StockPriceQuoteDTOContainer container,
                                    final AsyncCacheFetchMode asyncCacheFetchMode )
    {
        final String methodName = "setStockPriceQuote";
        logMethodBegin( methodName, container.getTickerSymbol(), asyncCacheFetchMode );
        Objects.requireNonNull( container, "container cannot be null" );
        Objects.requireNonNull( container.getTickerSymbol(), "container.getTickerSymbol() returns null" );
        StockPriceQuoteCacheEntry stockPriceQuoteCacheEntry = null;
        try
        {
            if ( asyncCacheFetchMode.isSynchronous() )
            {
                stockPriceQuoteCacheEntry = this.stockPriceQuoteCache
                                                .synchronousGet( container.getTickerSymbol() );
            }
            else
            {
                stockPriceQuoteCacheEntry = this.stockPriceQuoteCache
                                                .asynchronousGet( container.getTickerSymbol() );
            }
            final StockPriceQuoteDTO stockPriceQuoteDTO = this.context.getBean( StockPriceQuoteDTO.class );
            if ( stockPriceQuoteCacheEntry.getCachedData() != null )
            {
                BeanUtils.copyProperties( stockPriceQuoteCacheEntry.getCachedData(), stockPriceQuoteDTO );
                stockPriceQuoteDTO.setCacheError( stockPriceQuoteCacheEntry.getCachedData().getCacheError() );
            }
            stockPriceQuoteDTO.setCacheState( stockPriceQuoteCacheEntry.getCacheState() );
            container.setStockPriceQuote( stockPriceQuoteDTO );
            /*
            switch ( stockPriceQuoteCacheEntry.getCacheState() )
            {
                case CURRENT:
                    //container.setLastPrice( stockPriceQuoteCacheEntry.getCachedData().getLastPrice() );
                    break;

                case NOT_FOUND:
                    // the container is marked as not found.
                    break;

                case STALE:
                    if ( asyncCacheFetchMode.isSynchronous() )
                    {
                        StockPriceQuote stockPriceQuote
                            = stockPriceQuoteCacheEntry.getFetchSubject()
                                                       .doOnError( throwable ->
                                                                       container
                                                                           .setStockPriceQuoteCacheState( FAILURE ) )
                                                       .blockingFirst();
                        container.setLastPrice( stockPriceQuote.getLastPrice() );
                    }
                    break;
            }
            */
        }
        catch( StockNotFoundException e )
        {
            // ignore, just don't update the stock price.
        }
        logMethodEnd( methodName, container );
    }

    /**
     * Updates the quote cache with the last price.
     *
     * @param tickerSymbol
     * @param lastPrice
     * @throws StockNotFoundException
     */
    public void updateStockPrice( final String tickerSymbol, final BigDecimal lastPrice )
        throws StockNotFoundException
    {
        final String methodName = "updateStockPrice";
        logMethodBegin( methodName, tickerSymbol, lastPrice );
        this.stockPriceQuoteCache
            .updateStockPrice( tickerSymbol, lastPrice );
        logMethodEnd( methodName );
    }

    /**
     * Get the last price for the stock synchronously.
     *
     * @param tickerSymbol
     * @return The last stock price for the {@code tickerSymbol}
     * @throws StockNotFoundException
     */
    public BigDecimal getLastPrice( final String tickerSymbol )
        throws StockNotFoundException
    {
        final String methodName = "getStockPriceQuote";
        logMethodBegin( methodName, tickerSymbol );
        Objects.requireNonNull( tickerSymbol, "tickerSymbol cannot be null" );
        final StockPriceQuoteCacheEntry stockPriceQuoteCacheEntry = this.stockPriceQuoteCache
            .synchronousGet( tickerSymbol );
        BigDecimal stockPrice = stockPriceQuoteCacheEntry.getCachedData().getLastPrice();
        logMethodBegin( methodName, stockPrice );
        return stockPrice;
    }

    /**
     * Gets a stock quote from the {@code StockQuoteCache} asynchronously.  A valid result may be returned if the
     * cache entry is not STALE. Otherwise, a {@code StockPriceQuoteDTO} will be returned with the stale information
     * if there is any and appropriate cache states that can be sent back to the client so that it will make a
     * subsequent request to get the updated stock quote.
     *
     * @param tickerSymbol
     * @return
     * @throws StockNotFoundException
     */
    public StockPriceQuoteDTO getAsynchronousStockPriceQuote( final String tickerSymbol )
        throws StockNotFoundException
    {
        final String methodName = "getAynchronousStockPriceQuote";
        logMethodBegin( methodName, tickerSymbol );
        Objects.requireNonNull( tickerSymbol, "tickerSymbol cannot be null" );
        Assert.isTrue( !tickerSymbol.equalsIgnoreCase( "null" ), "ticker symbol cannot be 'null'" );
        final StockPriceQuoteCacheEntry stockPriceQuoteCacheEntry = this.stockPriceQuoteCache
                                                                         .asynchronousGet( tickerSymbol );
        final StockPriceQuoteDTO stockPriceQuoteDTO = handleStockPriceQuoteCacheEntry( tickerSymbol, stockPriceQuoteCacheEntry );
        logMethodEnd( methodName, stockPriceQuoteDTO );
        return stockPriceQuoteDTO;

    }

    /**
     * Gets a stock price quote from the {@code StockPriceQuoteCache}.
     * This method will block and wait while the quote is retrieved.
     *
     * @param tickerSymbol
     * @return
     * @throws StockNotFoundException
     */
    public StockPriceQuoteDTO getSynchronousStockPriceQuote( final String tickerSymbol )
        throws StockNotFoundException
    {
        final String methodName = "getSynchronousStockPriceQuote";
        logMethodBegin( methodName, tickerSymbol );
        Objects.requireNonNull( tickerSymbol, "tickerSymbol cannot be null" );
        Assert.isTrue( !tickerSymbol.equalsIgnoreCase( "null" ), "ticker symbol cannot be 'null'" );
        final StockPriceQuoteCacheEntry stockPriceQuoteCacheEntry = this.stockPriceQuoteCache
                                                                        .synchronousGet( tickerSymbol );
        final StockPriceQuoteDTO stockPriceQuoteDTO = this.handleStockPriceQuoteCacheEntry( tickerSymbol, stockPriceQuoteCacheEntry );
        logMethodEnd( methodName, stockPriceQuoteDTO );
        return stockPriceQuoteDTO;
    }

    /**
     * Handles the result of retrieving the {@code StockPriceQuote} from the cache.
     * @param tickerSymbol
     * @param stockPriceQuoteCacheEntry
     * @return
     */
    private StockPriceQuoteDTO handleStockPriceQuoteCacheEntry( final String tickerSymbol,
                                                                 final StockPriceQuoteCacheEntry stockPriceQuoteCacheEntry )
    {
        StockPriceQuoteDTO stockPriceQuoteDTO = null;
        switch ( stockPriceQuoteCacheEntry.getCacheState() )
        {
            case FAILURE:
                throw new StockNotFoundException( tickerSymbol, stockPriceQuoteCacheEntry.getFetchThrowable() );

            case NOT_FOUND:
                stockPriceQuoteDTO = stockPriceCacheEntryToStockPriceDTO( tickerSymbol, stockPriceQuoteCacheEntry );
                break;

            case CURRENT:
            case STALE:
                stockPriceQuoteDTO = stockPriceCacheEntryToStockPriceDTO( tickerSymbol, stockPriceQuoteCacheEntry );
        }
        return stockPriceQuoteDTO;
    }

    /**
     * Create the StockDTO from the stock price cache entry.
     * @param tickerSymbol
     * @param stockPriceQuoteCacheEntry
     * @return
     */
    private StockPriceQuoteDTO stockPriceCacheEntryToStockPriceDTO( final String tickerSymbol,
                                                                    final StockPriceQuoteCacheEntry stockPriceQuoteCacheEntry )
    {
        final StockPriceQuoteDTO stockPriceQuoteDTO = this.context.getBean( StockPriceQuoteDTO.class );
        stockPriceQuoteDTO.setTickerSymbol( tickerSymbol );
        stockPriceQuoteDTO.setLastPrice( stockPriceQuoteCacheEntry.getCachedData().getLastPrice() );
        stockPriceQuoteDTO.setCacheState( stockPriceQuoteCacheEntry.getCacheState() );
        stockPriceQuoteDTO.setExpirationTime( stockPriceQuoteCacheEntry.getExpirationTime() );
        return stockPriceQuoteDTO;
    }

    /**
     * Converts the StockPriceQuote into a StockPriceQuoteDTO.
     * @param stockPriceQuote
     * @return
     */
    private StockPriceQuoteDTO stockPriceQuoteToDTO( final StockPriceQuote stockPriceQuote )
    {
        final StockPriceQuoteDTO stockPriceQuoteDTO = this.context.getBean( StockPriceQuoteDTO.class );
        BeanUtils.copyProperties( stockPriceQuote, stockPriceQuote );
        return stockPriceQuoteDTO;
    }

    @Autowired
    public void setStockPriceQuoteCache( final StockPriceQuoteCache stockPriceQuoteCache )
    {
        this.stockPriceQuoteCache = stockPriceQuoteCache;
    }
}
