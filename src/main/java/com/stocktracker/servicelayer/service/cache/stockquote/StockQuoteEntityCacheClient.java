package com.stocktracker.servicelayer.service.cache.stockquote;

import com.stocktracker.common.TradingHours;
import com.stocktracker.repositorylayer.entity.StockQuoteEntity;
import com.stocktracker.servicelayer.service.StockQuoteEntityService;
import com.stocktracker.servicelayer.service.cache.common.AsyncCacheDBEntityClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This interface is used by any class that need information from the {@code StockQuoteEntityCache}.
 */
@Service
public class StockQuoteEntityCacheClient extends AsyncCacheDBEntityClient<String,
                                                                          StockQuoteEntity,
                                                                          StockQuoteEntityCacheEntry,
                                                                          StockQuoteEntityServiceExecutor,
                                                                          StockQuoteEntityCache,
                                                                          StockQuoteEntityCacheDataReceiver,
                                                                          StockQuoteEntityService>
{
    private StockQuoteEntityCache stockQuoteEntityCache;
    private StockQuoteEntityService stockQuoteEntityService;

    /**
     * Stock quote information is very static so update it if it hasn't been updated before or if the data is older
     * than a year.
     * @param entity
     * @return
     */
    @Override
    protected boolean isCurrent( final StockQuoteEntity entity )
    {
        boolean current = false;
        if ( TradingHours.isInSession() )
        {
            /*
             * Need to retrieve at the beginning of the session to get the open price.
             */
            if ( entity.getUpdateDate().getTime() < TradingHours.getTodaysSessionStartTime() )
            {
                current = false;
            }
            else
            {
                current = true;
            }
        }
        else
        {
            /*
             * If not in session, we need to get the closing values so any time after 4:00pm EST
             */
            if ( TradingHours.isAfterSessionEnd( entity.getUpdateDate().getTime() ))
            {
                current = true;
            }
            else
            {
                current = false;
            }
        }
        return current;
    }

    /**
     * Returns the {@code StockQuoteEntityCache} instance.
     * @return
     */
    @Override
    protected StockQuoteEntityCache getCache()
    {
        return this.stockQuoteEntityCache;
    }

    /**
     * Create the {@code StockQuoteEntity} object which is the data that is cached.
     * @return
     */
    @Override
    protected StockQuoteEntity createCachedDataObject()
    {
        return this.context.getBean( StockQuoteEntity.class );
    }

    /**
     * Getter for the {@code StockQuoteEntityService}.
     * @return
     */
    @Override
    protected StockQuoteEntityService getEntityService()
    {
        return this.stockQuoteEntityService;
    }

    /**
     * DI for the cache.
     * @param stockQuoteEntityCache
     */
    @Autowired
    public void setStockQuoteEntityCache( final StockQuoteEntityCache stockQuoteEntityCache )
    {
        this.stockQuoteEntityCache = stockQuoteEntityCache;
    }

    /**
     * DI for the entity service.
     * @param stockQuoteEntityService
     */
    @Autowired
    public void setStockQuoteEntityService( final StockQuoteEntityService stockQuoteEntityService )
    {
        this.stockQuoteEntityService = stockQuoteEntityService;
    }

}
