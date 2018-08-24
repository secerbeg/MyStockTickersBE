package com.stocktracker.servicelayer.service.stocks;

import com.stocktracker.common.exceptions.StockNotFoundException;
import com.stocktracker.servicelayer.service.BaseService;
import com.stocktracker.servicelayer.service.cache.common.AsyncCacheDataRequestException;
import com.stocktracker.servicelayer.service.cache.common.AsyncCacheFetchMode;
import com.stocktracker.servicelayer.service.cache.stockpricequote.StockPriceQuoteCache;
import com.stocktracker.servicelayer.service.cache.stockpricequote.StockPriceQuoteCacheClient;
import com.stocktracker.servicelayer.service.cache.stockpricequote.StockPriceQuoteCacheDataReceiver;
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
    @Autowired
    private StockPriceQuoteCache stockPriceQuoteCache;
    @Autowired
    private StockPriceQuoteCacheClient stockPriceQuoteCacheClient;

    /**
     * Set the stock price quotes for the list of dto containers.
     * @param containers
     */
    public void setStockPriceQuotes( final List<? extends StockPriceQuoteDTOContainer> containers )
    {
        final String methodName = "setStockPriceQuotes";
        Objects.requireNonNull( containers, "containers argument cannot be null" );
        logMethodBegin( methodName, containers.size() + " containers" );
        this.stockPriceQuoteCacheClient
            .updateContainers( containers );
        logMethodEnd( methodName );
    }

    /**
     * Set the stock price quote on all container entries.
     * @param containers
     */
    public void setStockPriceQuote( final List<? extends StockPriceQuoteDTOContainer> containers )
    {
        final String methodName = "setStockPriceQuote - list";
        logMethodBegin( methodName, "size " + containers.size() );
        containers.forEach( stockPriceQuoteContainer -> setStockPriceQuote( stockPriceQuoteContainer ));
        logMethodEnd( methodName );
    }

    /**
     * Obtains the stock information from the stock cache with the current stock information including the price,
     * last price change, and company name.
     * This information that was retrieved from the cache, is also saved back to the database stock table.
     * @param container
     */
    public void setStockPriceQuote( final StockPriceQuoteDTOContainer container )
    {
        final String methodName = "setStockPriceQuote";
        logMethodBegin( methodName, container.getTickerSymbol() );
        Objects.requireNonNull( container, "container cannot be null" );
        Objects.requireNonNull( container.getTickerSymbol(), "container.getTickerSymbol() returns null" );
        final StockPriceQuoteCacheDataReceiver stockPriceQuoteCacheDataReceiver = this.context.getBean( StockPriceQuoteCacheDataReceiver.class );
        stockPriceQuoteCacheDataReceiver.setCacheKey( container.getTickerSymbol() );
        stockPriceQuoteCacheDataReceiver.setAsyncKey( container.getTickerSymbol() );
        this.stockPriceQuoteCacheClient
            .asynchronousGetCachedData( stockPriceQuoteCacheDataReceiver );
        setStockPriceQuote( container, stockPriceQuoteCacheDataReceiver );
        logMethodEnd( methodName, container );
    }

    /**
     * Sets the {@code StockPriceQuoteDTO} value on the {@code container}.
     * @param container
     * @param stockPriceQuoteCacheDataReceiver
     */
    private void setStockPriceQuote( final StockPriceQuoteDTOContainer container,
                                     final StockPriceQuoteCacheDataReceiver stockPriceQuoteCacheDataReceiver )
    {
        final String methodName = "setStockPriceQuote";
        logMethodBegin( methodName, container, stockPriceQuoteCacheDataReceiver );
        final StockPriceQuoteDTO stockPriceQuoteDTO = this.context.getBean( StockPriceQuoteDTO.class );
        if ( stockPriceQuoteCacheDataReceiver.getCachedData() != null )
        {
            BeanUtils.copyProperties( stockPriceQuoteCacheDataReceiver.getCachedData(), stockPriceQuoteDTO );
            stockPriceQuoteDTO.setCacheError( stockPriceQuoteCacheDataReceiver.getCacheError() );
            stockPriceQuoteDTO.setExpirationTime( stockPriceQuoteCacheDataReceiver.getExpirationTime() );
        }
        stockPriceQuoteDTO.setCacheState( stockPriceQuoteCacheDataReceiver.getCacheState() );
        logMethodEnd( methodName, stockPriceQuoteDTO );
        container.setStockPriceQuote( stockPriceQuoteDTO );
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
                                                                        .synchronousGet( tickerSymbol, tickerSymbol );
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
     * @throws AsyncCacheDataRequestException
     */
    public StockPriceQuoteDTO getAsynchronousStockPriceQuote( final String tickerSymbol )
        throws AsyncCacheDataRequestException
    {
        final String methodName = "getAynchronousStockPriceQuote";
        logMethodBegin( methodName, tickerSymbol );
        Objects.requireNonNull( tickerSymbol, "tickerSymbol cannot be null" );
        Assert.isTrue( !tickerSymbol.equalsIgnoreCase( "null" ), "ticker symbol cannot be 'null'" );
        final StockPriceQuoteCacheEntry stockPriceQuoteCacheEntry = this.stockPriceQuoteCache
                                                                         .asynchronousGet( tickerSymbol, tickerSymbol );
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
     */
    public StockPriceQuoteDTO getSynchronousStockPriceQuote( final String tickerSymbol )
    {
        final String methodName = "getSynchronousStockPriceQuote";
        logMethodBegin( methodName, tickerSymbol );
        Objects.requireNonNull( tickerSymbol, "tickerSymbol cannot be null" );
        Assert.isTrue( !tickerSymbol.equalsIgnoreCase( "null" ), "ticker symbol cannot be 'null'" );
        final StockPriceQuoteCacheDataReceiver stockPriceQuoteCacheDataReceiver = this.context.getBean( StockPriceQuoteCacheDataReceiver.class );
        stockPriceQuoteCacheDataReceiver.setCacheKey( tickerSymbol );
        stockPriceQuoteCacheDataReceiver.setAsyncKey( tickerSymbol );
        this.stockPriceQuoteCacheClient
            .synchronousGetCachedData( stockPriceQuoteCacheDataReceiver );
        final StockPriceQuoteDTO stockPriceQuoteDTO = this.context.getBean( StockPriceQuoteDTO.class );
        BeanUtils.copyProperties( stockPriceQuoteCacheDataReceiver.getCachedData(), stockPriceQuoteDTO );
        stockPriceQuoteDTO.setCacheError( stockPriceQuoteCacheDataReceiver.getCacheError() );
        stockPriceQuoteDTO.setCacheState( stockPriceQuoteCacheDataReceiver.getCacheState() );
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
        final String methodName = "handleStockPriceQuoteCacheEntry";
        logMethodBegin( methodName, tickerSymbol, stockPriceQuoteCacheEntry );
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
        logMethodEnd( methodName, stockPriceQuoteDTO );
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
        final String methodName = "stockPriceCacheEntryToStockPriceDTO";
        logMethodBegin( methodName, tickerSymbol, stockPriceQuoteCacheEntry );
        final StockPriceQuoteDTO stockPriceQuoteDTO = this.context.getBean( StockPriceQuoteDTO.class );
        stockPriceQuoteDTO.setTickerSymbol( tickerSymbol );
        if ( stockPriceQuoteCacheEntry.getCachedData() != null )
        {
            stockPriceQuoteDTO.setLastPrice( stockPriceQuoteCacheEntry.getCachedData().getLastPrice() );
            stockPriceQuoteDTO.setExpirationTime( stockPriceQuoteCacheEntry.getCachedData().getExpirationTime() );
        }
        stockPriceQuoteDTO.setCacheState( stockPriceQuoteCacheEntry.getCacheState() );
        return stockPriceQuoteDTO;
    }
}
