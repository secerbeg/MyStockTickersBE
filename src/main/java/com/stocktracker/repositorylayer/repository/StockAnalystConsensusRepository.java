package com.stocktracker.repositorylayer.repository;

import com.stocktracker.repositorylayer.entity.StockAnalystConsensusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by mike on 5/7/2017.
 */
public interface StockAnalystConsensusRepository extends JpaRepository<StockAnalystConsensusEntity, Integer>
{
    /**
     * Get all of the stock analytics records for a customer
     * @param customerId
     * @return
     */
    List<StockAnalystConsensusEntity> findByCustomerIdOrderByTickerSymbol( final int customerId );
}