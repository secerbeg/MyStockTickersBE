package com.stocktracker.servicelayer.service;

import com.stocktracker.common.JSONDateConverter;
import com.stocktracker.repositorylayer.entity.StockToBuyEntity;
import com.stocktracker.repositorylayer.repository.StockToBuyRepository;
import com.stocktracker.servicelayer.service.stocks.StockPriceQuoteService;
import com.stocktracker.weblayer.dto.StockToBuyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * This service class manages the transactions with the STOCK_TO_BUY table.
 */
@Service
public class StockToBuyEntityService extends StockInformationEntityService<StockToBuyEntity,
                                                                           StockToBuyDTO,
                                                                           StockToBuyRepository>
{
    @Autowired()
    private StockToBuyRepository stockToBuyRepository;

    @Autowired
    private StockQuoteEntityService stockQuoteEntityService;

    @Autowired
    private StockPriceQuoteService stockPriceQuoteService;

    /**
     * Searches for the one entry for the customer UUID and ticker symbol -- this is a unique combination.
     * @param customerUuid
     * @return
     */
    public StockToBuyDTO getByCustomerUuidAndTickerSymbol( @NotNull final UUID customerUuid,
                                                           @NotNull final String tickerSymbol )
    {
        final String methodName = "getStockToBuyListForCustomerUuid";
        logMethodBegin( methodName, customerUuid, tickerSymbol );
        Objects.requireNonNull( customerUuid, "customerUuid cannot be null" );
        Objects.requireNonNull( tickerSymbol, "tickerSymbol cannot be null" );
        final StockToBuyEntity stockToBuyEntity = this.stockToBuyRepository
                                                      .findByCustomerUuidAndTickerSymbol( customerUuid, tickerSymbol );
        StockToBuyDTO stockToBuyDTO = null;
        if ( stockToBuyEntity != null )
        {
            stockToBuyDTO = this.entityToDTO( stockToBuyEntity );
        }
        logMethodEnd( methodName, stockToBuyDTO );
        return stockToBuyDTO;
    }

    /**
     * Get the list of all stock to buy for the customer
     * @param customerUuid
     * @return
     */
    public Page<StockToBuyDTO> getStockToBuyListForCustomerUuid( final Pageable pageRequest,
                                                                 @NotNull final UUID customerUuid )
    {
        final String methodName = "getStockToBuyListForCustomerUuid";
        logMethodBegin( methodName, pageRequest, customerUuid );
        Objects.requireNonNull( customerUuid, "customerUuid cannot be null" );
        Page<StockToBuyEntity> stockToBuyEntities = this.stockToBuyRepository
                                                        .findByCustomerUuid( pageRequest, customerUuid );
        Page<StockToBuyDTO> stockToBuyDTOs = this.entitiesToDTOs( pageRequest, stockToBuyEntities );
        logMethodEnd( methodName, "Found " + stockToBuyEntities.getContent().size() + " to buy" );
        return stockToBuyDTOs;
    }

    /**
     * Get stocks to buy for customer id and ticker symbol.
     * @param pageRequest
     * @param customerUuid
     * @param tickerSymbol
     * @return
     */
    public Page<StockToBuyDTO> getStockToBuyListForCustomerUuidAndTickerSymbol( @NotNull final Pageable pageRequest,
                                                                                @NotNull final UUID customerUuid,
                                                                                @NotNull final String tickerSymbol )
    {
        final String methodName = "getStockToBuyListForCustomerUuidAndTickerSymbol";
        logMethodBegin( methodName, pageRequest, customerUuid, tickerSymbol );
        Objects.requireNonNull( customerUuid, "customerUuid cannot be null" );
        Objects.requireNonNull( tickerSymbol, "tickerSymbol cannot be null" );
        Page<StockToBuyEntity> stockToBuyEntities = this.stockToBuyRepository
                                                        .findByCustomerUuidAndTickerSymbol( pageRequest, customerUuid,
                                                                                            tickerSymbol );
        Page<StockToBuyDTO> stockToBuyDTOs = this.entitiesToDTOs( pageRequest, stockToBuyEntities );
        logMethodEnd( methodName, "Found " + stockToBuyEntities.getContent().size() + " to buy" );
        return stockToBuyDTOs;
    }

    /**
     * Converts the entity to a dto and gets cached stock information.
     * @param stockToBuyEntity
     * @return
     */
    @Override
    public StockToBuyDTO entityToDTO( final StockToBuyEntity stockToBuyEntity )
    {
        Objects.requireNonNull( stockToBuyEntity );
        final StockToBuyDTO stockToBuyDTO = super.entityToDTO( stockToBuyEntity );
        stockToBuyDTO.setCompleted( stockToBuyEntity.getCompleted().equalsIgnoreCase( "Y" ) );
        stockToBuyDTO.setBuyAfterDate( JSONDateConverter.toY4MMDD( stockToBuyEntity.getBuyAfterDate() ) );
        return stockToBuyDTO;
    }

    /**
     * Converts the entities to DTOs and gets cached stock quote and stock price information.
     * @param entities
     * @return
     */
    @Override
    public List<StockToBuyDTO> entitiesToDTOs( final Iterable<StockToBuyEntity> entities )
    {
        final List<StockToBuyDTO> dtos = super.entitiesToDTOs( entities );
        this.stockQuoteEntityService
            .setStockQuoteInformation( dtos );
        this.stockPriceQuoteService
            .setStockPriceQuotes( dtos );
        return dtos;
    }

    @Override
    protected StockToBuyEntity dtoToEntity( final StockToBuyDTO stockToBuyDTO )
    {
        Objects.requireNonNull( stockToBuyDTO );
        StockToBuyEntity stockToBuyEntity = super.dtoToEntity( stockToBuyDTO );
        if ( stockToBuyDTO.getBuyAfterDate() != null )
        {
            stockToBuyEntity.setBuyAfterDate( JSONDateConverter.toTimestamp( stockToBuyDTO.getBuyAfterDate() ));
        }
        stockToBuyEntity.setCompleted( stockToBuyDTO.isCompleted() ? "Y" : "N" );
        return stockToBuyEntity;
    }

    @Override
    protected StockToBuyDTO createDTO()
    {
        return this.context.getBean( StockToBuyDTO.class );
    }

    @Override
    protected StockToBuyEntity createEntity()
    {
        return this.context.getBean( StockToBuyEntity.class );
    }

    @Override
    protected StockToBuyRepository getRepository()
    {
        return this.stockToBuyRepository;
    }

}
