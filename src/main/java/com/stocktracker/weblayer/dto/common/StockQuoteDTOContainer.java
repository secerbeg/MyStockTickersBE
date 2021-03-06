package com.stocktracker.weblayer.dto.common;

import com.stocktracker.repositorylayer.entity.StockQuoteEntity;
import com.stocktracker.servicelayer.service.cache.common.AsyncCacheDataContainer;
import com.stocktracker.servicelayer.service.stocks.TickerSymbolContainer;
import com.stocktracker.weblayer.dto.StockQuoteDTO;

/**
 * This interface defines methods for DTO's that contain a {@code StockQuoteDTO} instance.
 */
public interface StockQuoteDTOContainer extends TickerSymbolContainer
{
    /**
     * Set the stock quote DTO.
     * @param stockQuoteDTO
     */
    void setStockQuoteDTO( final StockQuoteDTO stockQuoteDTO );

    /**
     * Get the stock quote DTO.
     * @return
     */
    StockQuoteDTO getStockQuoteDTO();
}
