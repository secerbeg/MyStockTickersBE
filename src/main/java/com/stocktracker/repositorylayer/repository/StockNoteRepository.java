package com.stocktracker.repositorylayer.repository;

import com.stocktracker.repositorylayer.entity.StockEntity;
import com.stocktracker.repositorylayer.entity.StockNoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

/**
 * Created by mike on 5/7/2017.
 */
public interface StockNoteRepository extends PagingAndSortingRepository<StockNoteEntity,Integer>,
                                             QueryByExampleExecutor<StockEntity>
{
    /**
     * Get all of the stock notes for the customer and order by descending notes date
     * @param customerId
     * @return
     */
    Page<StockNoteEntity> findByCustomerIdOrderByNotesDateDesc( final Pageable pageRequest,
                                                                final int customerId );

    /**
     * Get all of the notes for a customer and ticker symbol.
     * @param customerId
     * @param tickerSymbol
     * @return
     */
    Page<StockNoteEntity> findByCustomerIdAndTickerSymbolOrderByNotesDateDesc( final Pageable pageRequest,
                                                                               final int customerId,
                                                                               final String tickerSymbol );
}
