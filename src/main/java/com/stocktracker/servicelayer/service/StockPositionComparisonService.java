package com.stocktracker.servicelayer.service;

import com.stocktracker.common.MyLogger;
import com.stocktracker.common.SetComparator;
import com.stocktracker.repositorylayer.entity.LinkedAccountEntity;
import com.stocktracker.repositorylayer.entity.StockPositionEntity;
import com.stocktracker.repositorylayer.entity.VersionedEntity;
import com.stocktracker.servicelayer.tradeit.apiresults.GetPositionsAPIResult;
import com.stocktracker.servicelayer.tradeit.types.TradeItPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class StockPositionComparisonService implements MyLogger
{
    private StockPositionService stockPositionService;

    @Async( "stockPositionEvaluatorThreadPool" )
    public void comparePositions( final LinkedAccountEntity linkedAccountEntity,
                                  final List<StockPositionEntity> stockPositionEntities,
                                  final GetPositionsAPIResult getPositionsAPIResult )
    {
        final String methodName = "comparePositions";
        logMethodBegin( methodName );
        /*
         * First we need to convert the getPositionsAPIResult to StockPositionEntities so that we can perform
         * set comparisons
         */
        final List<MyStockPositionEntity> tradeItStockPositionEntities = this.convertTradeItPositionsToStockPositions( linkedAccountEntity,
                                                                                                                       stockPositionEntities,
                                                                                                                       getPositionsAPIResult );
        /*
         * Next we need to convert the list of StockPositionEntities to a list of MyStockPositionEntities because we need these
         * entities to use an equals method implementation that uses the ticker symbol like the TradeItPositions so that the
         * set operations will work as they use the equals method.
         */
        final List<MyStockPositionEntity> myStockPositionEntities = this.convertStockPositionEntitiesToMyStockPositionEntities( stockPositionEntities );

        /*
         * Create the comparator, and compare the to lists and identify inserts, updates, and deletes
         */
        final SetComparator<MyStockPositionEntity>.SetComparatorResults comparatorResults =
            new SetComparator<MyStockPositionEntity>().compareSets( tradeItStockPositionEntities, myStockPositionEntities );

        /*
         * Apply the set differences to the database.
         */
        this.addEntities( comparatorResults.getNewItems() );
        this.deleteEntities( comparatorResults.getDeletedItems() );
        this.updateEntities( comparatorResults.getMatchingItems() );
        logMethodEnd( methodName );
    }

    /**
     * Update stock position entities.
     * @param matchingItems
     */
    private void updateEntities( final Set<MyStockPositionEntity> matchingItems )
    {
    }

    /**
     * Delete the stock positions entities.
     * @param stockPositionEntities
     */
    private void deleteEntities( final Set<MyStockPositionEntity> stockPositionEntities )
    {
        stockPositionEntities.forEach( myStockPositionEntity -> this.stockPositionService
                                                                    .deleteEntity( myStockPositionEntity ) );
    }

    /**
     * Add the entities to the database.
     * @param stockPositionEntities
     */
    private void addEntities( final Set<MyStockPositionEntity> stockPositionEntities )
    {
        stockPositionEntities.forEach( stockPositionEntity -> this.stockPositionService
                                                                  .saveEntity( stockPositionEntity ) );
    }

    /**
     * Converts the list of {@code StockPositionEntity} instances into a list of {@codde MyStockPositionEntity}.
     * @param stockPositionEntities
     * @return
     */
    private List<MyStockPositionEntity> convertStockPositionEntitiesToMyStockPositionEntities( final List<StockPositionEntity>
                                                                                               stockPositionEntities )
    {
        final List<MyStockPositionEntity> returnPositions = new ArrayList<>();
        stockPositionEntities.forEach( stockPositionEntity -> returnPositions.add( new MyStockPositionEntity( stockPositionEntity )));
        return returnPositions;
    }

    /**
     * Converts the TradeIt Position instances into StockPositionEntity instances.
     * @param linkedAccountEntity The linked account is the parent account of a {@code StockPositionEntity} so we need to
     *                            assign that parent instance value to add to the database.
     * @param stockPositionEntities This list contains the database entries for the stock positions.  It is used to obtain
     *                              the primary key value for those stocks that match the TradeIt it result so that they
     *                              new values from the TradeIt results can be updated in the database.
     * @param getPositionsAPIResult The TradeIt get positions results that contains the stock positions.
     * @return
     */
    private List<MyStockPositionEntity> convertTradeItPositionsToStockPositions( final LinkedAccountEntity linkedAccountEntity,
                                                                                 final List<StockPositionEntity> stockPositionEntities,
                                                                                 final GetPositionsAPIResult getPositionsAPIResult )
    {
        final List<MyStockPositionEntity> tradeItStockPositionEntities = new ArrayList<>();
        final Map<String,StockPositionEntity> databaseStockPositionMap = this.createStockPositionMap( stockPositionEntities );
        for ( final TradeItPosition tradeItPosition: getPositionsAPIResult.getPositions() )
        {
            final StockPositionEntity databaseStockPositionEntity = databaseStockPositionMap.get( tradeItPosition.getSymbol() );
            /*
             * The the entities that are not found in the map will be added to the database.
             */
            if ( databaseStockPositionEntity == null )
            {
                final MyStockPositionEntity stockPositionEntity = new MyStockPositionEntity( tradeItPosition );
                stockPositionEntity.setLinkedAccountByLinkedAccountId( linkedAccountEntity );
                tradeItStockPositionEntities.add( stockPositionEntity );
            }
            /*
             * The entities that match, will be updated so we need to preserve the database entity values and update
             * the values from the TradeIt position
             */
            else
            {
                final MyStockPositionEntity stockPositionEntity = new MyStockPositionEntity( databaseStockPositionEntity );
                stockPositionEntity.setValues( tradeItPosition );
                tradeItStockPositionEntities.add( stockPositionEntity );
            }
        }
        return tradeItStockPositionEntities;
    }

    /**
     * Creates a map from the list of {@code StockPositionEntity}
     * @param stockPositionEntities
     * @return
     */
    private Map<String,StockPositionEntity> createStockPositionMap( final List<StockPositionEntity> stockPositionEntities )
    {
        final Map<String,StockPositionEntity> stockPositionMap = new HashMap<>();
        stockPositionEntities.forEach( stockPositionEntity -> stockPositionMap.put( stockPositionEntity.getTickerSymbol(),
                                                                                    stockPositionEntity ));
        return stockPositionMap;
    }

    /**
     * The equals and hashCode method in {@code StockPositionEntity} are based on the id not
     * the ticker symbol. We need to change the equals to the tickerSymbol as that is the key to the
     * TradeIt positions.  The equals method is used in the Set operations.
     */
    private class MyStockPositionEntity extends StockPositionEntity
    {
        public MyStockPositionEntity( final StockPositionEntity stockPositionEntity )
        {
            super( stockPositionEntity );
        }

        public MyStockPositionEntity( final TradeItPosition tradeItPosition )
        {
            super( tradeItPosition );
        }

        @Override
        public boolean equals( final Object o )
        {
            return this.getTickerSymbol().equals( ((StockPositionEntity)o).getTickerSymbol() );
        }

        @Override
        public int hashCode()
        {
            return this.getTickerSymbol().hashCode();
        }
    }

    @Autowired
    public void setStockPositionService( final StockPositionService stockPositionService )
    {
        this.stockPositionService = stockPositionService;
    }
}
