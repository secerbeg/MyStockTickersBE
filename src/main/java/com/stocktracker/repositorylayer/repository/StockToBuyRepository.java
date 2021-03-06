package com.stocktracker.repositorylayer.repository;

import com.stocktracker.repositorylayer.entity.StockToBuyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Created by mike on 10/24/2017.
 */
public interface StockToBuyRepository extends JpaRepository<StockToBuyEntity,UUID>
{
    /**
     * Get all of the stocks to buy records for a customer
     * @param customerUuid
     * @return
     */
    Page<StockToBuyEntity> findByCustomerUuid( final Pageable pageRequest, final UUID customerUuid );

    /**
     * Get all of the stocks to buy records for a customer and ticker symbol.
     * @param customerUuid
     * @return
     */
    Page<StockToBuyEntity> findByCustomerUuidAndTickerSymbol( final Pageable pageRequest, final UUID customerUuid,
                                                              final String tickerSymbol );

    /**
     * Get all of the stocks to buy for the customer and the ticker symbol
     *
     * @param customerUuid
     * @param tickerSymbol
     * @return
     */
    StockToBuyEntity findByCustomerUuidAndTickerSymbol( final UUID customerUuid,
                                                        final String tickerSymbol );

    @Override
    @Transactional
    @Modifying
    StockToBuyEntity save( StockToBuyEntity stockToBuyEntity );

    @Override
    @Transactional
    @Modifying
    <S extends StockToBuyEntity> S saveAndFlush( S s );

    @Override
    @Transactional
    @Modifying
    void delete( StockToBuyEntity stockToBuyEntity );

    @Override
    @Transactional
    @Modifying
    void deleteInBatch( Iterable<StockToBuyEntity> iterable );
}
