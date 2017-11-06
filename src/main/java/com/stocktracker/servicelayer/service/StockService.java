package com.stocktracker.servicelayer.service;

import com.stocktracker.common.JSONDateConverter;
import com.stocktracker.common.MyLogger;
import com.stocktracker.common.exceptions.StockNotFoundInDatabaseException;
import com.stocktracker.repositorylayer.common.BooleanUtils;
import com.stocktracker.repositorylayer.entity.StockEntity;
import com.stocktracker.repositorylayer.repository.StockRepository;
import com.stocktracker.servicelayer.service.stockinformationprovider.StockQuote;
import com.stocktracker.servicelayer.service.stockinformationprovider.StockQuoteCache;
import com.stocktracker.servicelayer.service.stockinformationprovider.StockQuoteFetchMode;
import com.stocktracker.servicelayer.service.stockinformationprovider.StockQuoteState;
import com.stocktracker.servicelayer.service.stockinformationprovider.StockTickerQuote;
import com.stocktracker.weblayer.dto.StockDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * Created by mike on 9/11/2016.
 */
@Service
@Transactional
public class StockService extends BaseService<StockEntity, StockDTO> implements MyLogger
{
    private StockRepository stockRepository;
    private StockQuoteCache stockQuoteCache;

    public interface StockCompanyNameContainer
    {
        String getTickerSymbol();
        void setCompanyName( final String companyName );
        String getCompanyName();
    }

    /**
     * To be implemented by any class containing a ticker symbol and a stock price to
     * beu use with the {@code updateStockPrice} methods
     */
    public interface LastPriceContainer
    {
        String getTickerSymbol();
        void setLastPrice( final BigDecimal stockPrice );
        BigDecimal getLastPrice();
        void setLastPriceChange( final Timestamp lastPriceChange );
        Timestamp getLastPriceChange();
    }

    /**
     * Defines the necessary methods to contain a stock quote and work with the StockQuoteCache
     */
    public interface StockQuoteContainer extends StockCompanyNameContainer, LastPriceContainer
    {
        void setStockQuoteState( final StockQuoteState stockQuoteState );
        StockQuoteState getStockQuoteState();
    }

    /**
     * Transforms {@code Page<ENTITY>} objects into {@code Page<DTO>} objects.
     *
     * @param pageRequest The information of the requested page.
     * @param source      The {@code Page<ENTITY>} object.
     * @param updateStockPrices If true, the last stock price will be updated
     * @return The created {@code Page<DTO>} object.
     */
    private Page<StockDTO> mapStockEntityPageIntoStockDTOPage( final Pageable pageRequest, Page<StockEntity> source,
                                                               final boolean updateStockPrices )
    {
        final String methodName = "mapStockEntityPageIntoStockDTOPage";
        Objects.requireNonNull( pageRequest, "pageRequest cannot be null" );
        Objects.requireNonNull( source, "source cannot be null" );
        logMethodBegin( methodName );
        List<StockDTO> stockDTOs = this.entitiesToDTOs( source.getContent() );
        if ( updateStockPrices )
        {
            for ( StockDTO stockDTO : stockDTOs )
            {
                this.setStockQuoteInformation( stockDTO, StockQuoteFetchMode.ASYNCHRONOUS );
            }
        }
        logMethodEnd( methodName );
        return new PageImpl<>( stockDTOs, pageRequest, source.getTotalElements() );
    }

    /**
     * Get a page of StockDomainEntities's
     * @param pageRequest
     * @return
     */
    public Page<StockDTO> getPage( final Pageable pageRequest, final boolean withStockPrices )
    {
        final String methodName = "getPage";
        logMethodBegin( methodName, pageRequest );
        Objects.requireNonNull( pageRequest, "pageRequest cannot be null" );
        /*
         * Get the page from the database
         */
        Page<StockEntity> stockEntities = stockRepository.findAll( pageRequest );
        /*
         * Map from Entity to DomainEntity
         */
        Page<StockDTO> stockDTOPage = mapStockEntityPageIntoStockDTOPage( pageRequest, stockEntities, withStockPrices );
        logMethodEnd( methodName );
        return stockDTOPage;
    }

    /**
     * Get companies matching either the company name or ticker symbol strings
     * @param pageRequest
     * @param companiesLike
     * @param withStockPrices
     * @return
     */
    public Page<StockDTO> getCompaniesLike( final Pageable pageRequest, final String companiesLike,
                                           final boolean withStockPrices )
    {
        final String methodName = "getCompaniesLike";
        logMethodBegin( methodName, pageRequest, companiesLike );
        Objects.requireNonNull( pageRequest, "pageRequest cannot be null" );
        Objects.requireNonNull( companiesLike, "companiesLike cannot be null" );
        /*
         * Get the page from the database
         */
        Page<StockEntity> stockEntities = stockRepository.findByCompanyNameIsLikeOrTickerSymbolIsLike(
            pageRequest, companiesLike + "%", companiesLike + "%" );
        /*
         * Map from Entity to DomainEntity
         */
        Page<StockDTO> stockDTOPage = mapStockEntityPageIntoStockDTOPage( pageRequest, stockEntities, withStockPrices );
        logMethodEnd( methodName );
        return stockDTOPage;
    }

    /**
     * Gets a single stock for the {@code tickerSymbol} from the stock cache or the database if necessary.
     * If the stock price is not current, a request will be made to update the price asynchronously.  In this case,
     * the {@code cachedStock.stockQuoteState = StockQuoteState.STALE or StockQuoteState.NOT_FOUND}.  In either case,
     * a subsequent request will need to be made to retrieve the updated stock price while the background tasks obtains
     * the update from the stock quote service.
     *
     * @param tickerSymbol
     * @return
     * @throws StockNotFoundInDatabaseException if {@code tickerSymbol} is not found in the stock table
     */
    public StockDTO getStock( final String tickerSymbol )
    {
        final String methodName = "getStock";
        logMethodBegin( methodName, tickerSymbol );
        Objects.requireNonNull( tickerSymbol, "tickerSymbol cannot be null" );
        StockEntity stockEntity = this.stockRepository.findOne( tickerSymbol );
        StockDTO stockDTO = null;
        if ( stockEntity != null )
        {
            stockDTO = this.entityToDTO( stockEntity );
        }
        logMethodEnd( methodName, stockDTO );
        return stockDTO;
    }

    /**
     * Gets a stock quote from the {@code StockQuoteCache}.
     * If the fetch mode is SYNCHRONOUS, this method will block and wait while the quote is retrieved.
     * If the fetch mode is ASYNCHRONOUS and the stock quote is not found or is stale, this block will return without
     * a complete quote.  The quote can be obtained by a subsequent request.  If the stock quote is found and is current,
     * the behaviour is that of the SYNCHRONOUS mode and this method will return with a complete stock quote.
     * @param tickerSymbol
     * @param stockQuoteFetchMode
     * @return
     * @throws StockNotFoundInDatabaseException
     */
    public StockQuote getStockQuote( final String tickerSymbol,
                                     final StockQuoteFetchMode stockQuoteFetchMode )
        throws StockNotFoundInDatabaseException
    {
        final String methodName = "getStockQuote";
        logMethodBegin( methodName, tickerSymbol );
        Objects.requireNonNull( tickerSymbol, "tickerSymbol cannot be null" );
        StockQuote stockQuote = this.stockQuoteCache.getStockQuote( tickerSymbol, stockQuoteFetchMode );
        logMethodEnd( methodName, stockQuote );
        return stockQuote;
    }

    /**
     * Determines if the {@code tickerSymbol exists}
     * @param tickerSymbol
     * @return
     */
    public boolean isStockExistsInDatabase( final String tickerSymbol )
    {
        final String methodName = "isStockExistsInDatabase";
        logMethodBegin( methodName, tickerSymbol );
        Objects.requireNonNull( tickerSymbol, "tickerSymbol cannot be null" );
        boolean exists = stockRepository.exists( tickerSymbol );
        logMethodEnd( methodName, exists );
        return exists;
    }

    /**
     * Add a Yahoo stock to the database
     * @param stockQuote
     * @return {@code StockDTO} the stock that was added to the database
     */
    public StockDTO saveStock( final StockQuote stockQuote )
    {
        final String methodName = "saveStock";
        logMethodBegin( methodName, stockQuote );
        Objects.requireNonNull( stockQuote, "yahooStock cannot be null" );
        StockDTO stockDTO = stockQuoteToStockDTO( stockQuote );
        this.saveStock( stockDTO );
        logMethodEnd( methodName, stockDTO );
        return stockDTO;
    }

    /**
     * Converts a {@code CachedStockQuote} into a {@code StockDTO}
     * @param stockQuote
     * @return
     */
    private StockDTO stockQuoteToStockDTO( final StockQuote stockQuote )
    {
        StockDTO stockDTO = StockDTO.newInstance();
        stockDTO.setTickerSymbol( stockQuote.getTickerSymbol() );
        stockDTO.setCompanyName( stockQuote.getCompanyName() );
        stockDTO.setUserEntered( false );
        stockDTO.setExchange( stockQuote.getExchange() );
        stockDTO.setLastPriceChange( stockQuote.getLastPriceChange() );
        stockDTO.setLastPrice( stockQuote.getLastPrice() );
        stockDTO.setStockQuoteState( stockQuote.getStockQuoteState() );
        stockDTO.setCreatedBy( 1 );
        return stockDTO;
    }

    /**
     * Add a stock to the database
     * @param stockDTO
     * @return
     */
    public StockDTO saveStock( final StockDTO stockDTO )
    {
        final String methodName = "saveStock";
        logMethodBegin( methodName, stockDTO );
        Objects.requireNonNull( stockDTO, "stockDTO cannot be null" );
        StockEntity stockEntity = this.dtoToEntity( stockDTO );
        stockEntity = this.stockRepository.save( stockEntity );
        StockDTO returnStockDTO = this.entityToDTO( stockEntity );
        //this.setStockQuoteInformation( returnStockDTO );
        logMethodEnd( methodName, returnStockDTO );
        return returnStockDTO;
    }

    /**
     * Delete the stock
     * @param tickerSymbol
     */
    public void deleteStock( final String tickerSymbol )
    {
        final String methodName = "deleteStock";
        logMethodBegin( methodName, tickerSymbol );
        Objects.requireNonNull( tickerSymbol, "tickerSymbol cannot be null" );
        this.stockRepository.delete( tickerSymbol );
        logMethodEnd( methodName );
    }

    /**
     * Get the last price for the stock
     * @param tickerSymbol
     * @return Null if there are any exceptions
     */
    public BigDecimal getStockPrice( final String tickerSymbol )
    {
        final String methodName = "getStockPrice";
        logMethodBegin( methodName, tickerSymbol );
        Objects.requireNonNull( tickerSymbol, "tickerSymbol cannot be null" );
        BigDecimal stockPrice = null;
        StockQuote stockQuote = this.getStockQuote( tickerSymbol, StockQuoteFetchMode.SYNCHRONOUS );
        if ( !stockQuote.getStockQuoteState().isNotFound() )
        {
            stockPrice = this.getStockQuote( tickerSymbol, StockQuoteFetchMode.SYNCHRONOUS ).getLastPrice();
        }
        logMethodBegin( methodName, stockPrice );
        return stockPrice;
    }

    /**
     * Updates the database stock table with the new price information
     * @param tickerSymbol
     * @param stockTickerQuote
     */
    private void updateStockPrice( final String tickerSymbol, final StockTickerQuote stockTickerQuote )
    {
        StockEntity stockEntity = this.getStockEntity( tickerSymbol );
        stockEntity.setLastPrice( stockTickerQuote.getLastPrice() );
        stockEntity.setLastPriceChange( stockTickerQuote.getLastPriceChange() );
        this.stockRepository.save( stockEntity );
    }

    /**
     * Retrieves a {@code StockEntity} from the database
     * @param tickerSymbol
     * @return
     */
    private StockEntity getStockEntity( final String tickerSymbol )
    {
        return stockRepository.findOne( StringUtils.truncate( tickerSymbol, StockEntity.TICKER_SYMBOL_LEN ) );
    }

    /**
     * Using the ticker symbol of {@code container}, obtains and sets the stock's company name on the {@code container}
     * @param container
     */
    public void setCompanyName( final StockCompanyNameContainer container )
    {
        final String methodName = "setCompanyName";
        Objects.requireNonNull( container.getTickerSymbol(), "container.getTickerSymbol() returns null" );
        logMethodBegin( methodName, container.getTickerSymbol() );
        StockEntity stockEntity = getStockEntity( container.getTickerSymbol() );
        final String companyName = stockEntity.getCompanyName();
        container.setCompanyName( companyName );
        logMethodEnd( methodName, companyName );
    }

    /**
     * Convenience method to set the stock price.  Simply calls {@code updateStockPrice}.
     * Provided for searches for setting a stock price.
     */
    public void setStockPrice( final LastPriceContainer container )
    {
        this.updateStockPrice( container );
    }

    /**
     * Using the ticker symbol of {@code container}, obtains and sets the stock's company name on the {@code container}
     * @param container
     */
    public void updateStockPrice( final LastPriceContainer container )
    {
        final String methodName = "updateStockPrice";
        Objects.requireNonNull( container.getTickerSymbol(), "container.getTickerSymbol() returns null" );
        logMethodBegin( methodName, container.getTickerSymbol() );
        StockEntity stockEntity = getStockEntity( container.getTickerSymbol() );
        final BigDecimal stockPrice = stockEntity.getLastPrice();
        container.setLastPrice( stockPrice );
        logMethodEnd( methodName, stockPrice );
    }

    /**
     * Makes a call to Yahoo to get the current stock information including the price, last price change, and company
     * name.  This information that was retrieved from Yahoo, is also saved back to the database stock table.
     * @param containers
     * @param stockQuoteFetchMode SYNCHRONOUS or ASYNCHRONOUS
     * @throws IOException
     */
    public void setStockQuoteInformation( final List<StockQuoteContainer> containers,
                                          final StockQuoteFetchMode stockQuoteFetchMode )
        throws IOException
    {
        final String methodName = "setStockInformation";
        logMethodBegin( methodName, stockQuoteFetchMode );
        Objects.requireNonNull( containers, "stockDomainEntities cannot be null" );
        for ( StockQuoteContainer container : containers )
        {
            this.setStockQuoteInformation( container, stockQuoteFetchMode );
        }
        logMethodEnd( methodName );
    }

    /**
     * Obtains the stock information from the stock cache with the current stock information including the price,
     * last price change, and company name.
     * This information that was retrieved from the cache, is also saved back to the database stock table.
     * @param container The container to populate with the stock quote information.
     * @param stockQuoteFetchMode SYNCHRONOUS or ASYNCHRONOUS
     */
    public void setStockQuoteInformation( final StockQuoteContainer container,
                                          final StockQuoteFetchMode stockQuoteFetchMode )
    {
        final String methodName = "setStockInformation";
        logMethodBegin( methodName, container.getTickerSymbol(), stockQuoteFetchMode );
        Objects.requireNonNull( container, "container cannot be null" );
        Objects.requireNonNull( container.getTickerSymbol(), "container.getTickerSymbol() returns null" );
        StockQuote stockQuote = this.stockQuoteCache.getStockQuote( container.getTickerSymbol(), stockQuoteFetchMode );
        container.setStockQuoteState( stockQuote.getStockQuoteState() );
        if ( stockQuote.getStockQuoteState().isCurrent() ||
             stockQuote.getStockQuoteState().isStale() )
        {
            /*
             * Update the stock table with the new information
             */
            container.setCompanyName( stockQuote.getCompanyName() );
            container.setLastPrice( stockQuote.getLastPrice() );
            container.setStockQuoteState( stockQuote.getStockQuoteState() );
            container.setLastPriceChange( stockQuote.getLastPriceChange() );
        }
        else
        {
            logWarn( methodName, "Could not get stock information from the stock cache for {0}",
                     container.getTickerSymbol() );
        }
        logMethodEnd( methodName, container );
    }

    /**
     * Saves the stock information in {@code container} to the stock table.
     * @param stockQuote
     */
    public void persistStockQuote( final StockQuote stockQuote )
    {
        final String methodName = "persistStockQuote";
        Objects.requireNonNull( stockQuote, "container cannot be null" );
        Objects.requireNonNull( stockQuote.getTickerSymbol(), "container.getTickerSymbol() returns null" );
        StockEntity stockEntity = this.getStockEntity( stockQuote.getTickerSymbol() );
        if ( stockEntity == null )
        {
            stockEntity.setCompanyName( stockQuote.getCompanyName() );
        }
        stockEntity.setLastPrice( stockQuote.getLastPrice() );
        stockEntity.setLastPriceChange( stockQuote.getLastPriceChange() );
        this.stockRepository.save( stockEntity );
        logMethodEnd( methodName, stockQuote );
    }

    @Override
    protected StockDTO entityToDTO( final StockEntity stockEntity )
    {
        Objects.requireNonNull( stockEntity );
        StockDTO stockDTO = StockDTO.newInstance();
        BeanUtils.copyProperties( stockEntity, stockDTO );
        stockDTO.setUserEntered( BooleanUtils.fromCharToBoolean( stockEntity.getUserEntered() ));
        return stockDTO;
    }

    @Override
    protected StockEntity dtoToEntity( final StockDTO stockDTO )
    {
        Objects.requireNonNull( stockDTO );
        StockEntity stockEntity = StockEntity.newInstance();
        BeanUtils.copyProperties( stockDTO, stockEntity );
        stockEntity.setUserEntered( BooleanUtils.fromBooleanToChar( stockDTO.isUserEntered() ));
        return stockEntity;
    }

    @Autowired
    public void setStockRepository( final StockRepository stockRepository )
    {
        this.stockRepository = stockRepository;
    }

    @Autowired
    public void setStockQuoteCache( final StockQuoteCache stockQuoteCache )
    {
        this.stockQuoteCache = stockQuoteCache;
    }

}
