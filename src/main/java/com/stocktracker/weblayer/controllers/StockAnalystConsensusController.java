package com.stocktracker.weblayer.controllers;

import com.fasterxml.uuid.impl.UUIDUtil;
import com.stocktracker.common.MyLogger;
import com.stocktracker.common.exceptions.EntityVersionMismatchException;
import com.stocktracker.common.exceptions.StockCompanyNotFoundException;
import com.stocktracker.common.exceptions.StockNotFoundException;
import com.stocktracker.common.exceptions.EntityNotFoundException;
import com.stocktracker.servicelayer.service.StockAnalystConsensusEntityService;
import com.stocktracker.weblayer.dto.StockAnalystConsensusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

/**
 * This is the REST Controller for all Stock Analytics methods.
 */
@RestController
@CrossOrigin
public class StockAnalystConsensusController implements MyLogger
{
    private static final String CONTEXT_URL = "stockAnalystConsensus";
    private StockAnalystConsensusEntityService stockAnalystConsensusService;

    /**
     * Get all of the stock analyst consensus for a customer
     * @return
     */
    @RequestMapping( value = CONTEXT_URL + "/customerId/{customerId}",
                     method = RequestMethod.GET,
                     produces = {MediaType.APPLICATION_JSON_VALUE} )
    public List<StockAnalystConsensusDTO> getStockAnalystConsensusList( @PathVariable String customerId )
    {
        final String methodName = "getStockAnalystConsensusList";
        logMethodBegin( methodName, customerId );
        List<StockAnalystConsensusDTO> stockAnalystConsensusDTOs = this.stockAnalystConsensusService
                                                                       .getAllStockAnalystConsensus(
                                                                           UUIDUtil.uuid( customerId ));
        logMethodEnd( methodName, "stockAnalystConsensus size: " + stockAnalystConsensusDTOs.size() );
        return stockAnalystConsensusDTOs;
    }

    /**
     * Get all of the stock analyst consensus for a customer
     * @return
     */
    @RequestMapping( value = CONTEXT_URL + "/page/customerId/{customerId}",
                     method = RequestMethod.GET,
                     produces = {MediaType.APPLICATION_JSON_VALUE} )
    public Page<StockAnalystConsensusDTO> getStockAnalystConsensusPage( final Pageable pageRequest,
                                                                        @PathVariable String customerId )
    {
        final String methodName = "getStockAnalystConsensusPage";
        logMethodBegin( methodName, customerId );
        Page<StockAnalystConsensusDTO> stockAnalystConsensusDTOs = this.stockAnalystConsensusService
            .getStockAnalystConsensusPage( pageRequest, UUIDUtil.uuid( customerId ));
        logMethodEnd( methodName, "stockAnalystConsensus size: " + stockAnalystConsensusDTOs.getContent().size() );
        return stockAnalystConsensusDTOs;
    }

    /**
     * Get all of the stock analyst consensus for a customer id and ticker symbol
     * @return
     */
    @RequestMapping( value = CONTEXT_URL + "/page/tickerSymbol/{tickerSymbol}/customerId/{customerId}",
                     method = RequestMethod.GET,
                     produces = {MediaType.APPLICATION_JSON_VALUE} )
    public Page<StockAnalystConsensusDTO> getStockAnalystConsensusList( final Pageable pageRequest,
                                                                        @PathVariable String customerId,
                                                                        @PathVariable String tickerSymbol )
    {
        final String methodName = "getStockAnalystConsensusList";
        logMethodBegin( methodName, pageRequest, customerId, tickerSymbol );
        Page<StockAnalystConsensusDTO> stockAnalystConsensusDTOs = this.stockAnalystConsensusService
            .getStockAnalystConsensusListForCustomerUuidAndTickerSymbol( pageRequest,
                                                                       UUIDUtil.uuid( customerId ),
                                                                       tickerSymbol );
        logMethodEnd( methodName, "stockAnalystConsensus size: " + stockAnalystConsensusDTOs.getContent().size() );
        return stockAnalystConsensusDTOs;
    }

    /**
     * Get a single stock analyst consensus
     * @return
     */
    @RequestMapping( value = CONTEXT_URL + "/id/{stockAnalystConsensusId}/customerId/{customerId}",
                     method = RequestMethod.GET,
                     produces = {MediaType.APPLICATION_JSON_VALUE} )
    public StockAnalystConsensusDTO getStockAnalystConsensus( @PathVariable String stockAnalystConsensusId,
                                                              @PathVariable String customerId )
        throws EntityNotFoundException
    {
        final String methodName = "getStockAnalystConsensus";
        logMethodBegin( methodName, stockAnalystConsensusId, customerId );
        final StockAnalystConsensusDTO stockAnalystConsensusDTO = this.stockAnalystConsensusService
                                                                      .getStockAnalystConsensus(
                                                                          UUIDUtil.uuid( stockAnalystConsensusId ));
        logMethodEnd( methodName, stockAnalystConsensusDTO );
        return stockAnalystConsensusDTO;
    }

    /**
     * Deletes a stock summary entity
     * @param stockAnalystConsensusId
     * @return
     */
    @RequestMapping( value = CONTEXT_URL + "/id/{stockAnalystConsensusId}/customerId/{customerId}",
                     method = RequestMethod.DELETE,
                     produces = {MediaType.APPLICATION_JSON_VALUE} )
    public ResponseEntity<Void> deleteStockAnalystConsensus( @PathVariable String stockAnalystConsensusId,
                                                             @PathVariable String customerId )
        throws EntityNotFoundException
    {
        final String methodName = "deleteStockAnalystConsensus";
        logMethodBegin( methodName, customerId, stockAnalystConsensusId );
        this.stockAnalystConsensusService.deleteEntity( stockAnalystConsensusId );
        logMethodEnd( methodName );
        return new ResponseEntity<>( HttpStatus.OK );
    }

    /**
     * Create a stock summary entity.
     * @param stockAnalystConsensusDTO
     * @param customerId
     * @return
     * @throws StockNotFoundException
     * @throws EntityVersionMismatchException
     */
    @RequestMapping( value = CONTEXT_URL + "/customerId/{customerId}",
                     method = RequestMethod.POST )
    public ResponseEntity<StockAnalystConsensusDTO> addStockAnalystConsensus( @RequestBody StockAnalystConsensusDTO stockAnalystConsensusDTO,
                                                                              @PathVariable String customerId )
        throws StockNotFoundException,
               EntityVersionMismatchException, StockCompanyNotFoundException
    {
        final String methodName = "addStockAnalystConsensus";
        logMethodBegin( methodName, customerId, stockAnalystConsensusDTO );
        StockAnalystConsensusDTO newStockAnalystConsensusDTO = this.stockAnalystConsensusService
                                                                        .createStockAnalystConsensus(
                                                                            UUIDUtil.uuid( customerId ),
                                                                               stockAnalystConsensusDTO );
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation( ServletUriComponentsBuilder
                                     .fromCurrentRequest().path( "" )
                                     .buildAndExpand( newStockAnalystConsensusDTO ).toUri());
        logMethodEnd( methodName );
        return new ResponseEntity<>( newStockAnalystConsensusDTO, httpHeaders, HttpStatus.CREATED );
    }

    /**
     *
     * @param stockAnalystConsensusDTO
     * @param stockAnalystConsensusId
     * @param customerId
     * @return
     * @throws EntityVersionMismatchException
     */
    @CrossOrigin
    @RequestMapping( value = CONTEXT_URL + "/id/{stockAnalystConsensusId}/customerId/{customerId}",
                     method = RequestMethod.PUT )
    public ResponseEntity<StockAnalystConsensusDTO> updateStockAnalystConsensus( @RequestBody StockAnalystConsensusDTO stockAnalystConsensusDTO,
                                                                                 @PathVariable Integer stockAnalystConsensusId,
                                                                                 @PathVariable String customerId )
        throws EntityVersionMismatchException
    {
        final String methodName = "saveStockAnalystConsensus";
        logMethodBegin( methodName, stockAnalystConsensusId, customerId, stockAnalystConsensusDTO );
        /*
         * Save the stock
         */
        StockAnalystConsensusDTO returnStockAnalystConsensusDTO = this.stockAnalystConsensusService
                                                                      .saveStockAnalystConsensus( stockAnalystConsensusDTO );
        /*
         * send the response
         */
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation( ServletUriComponentsBuilder
                                     .fromCurrentRequest().path( "" )
                                     .buildAndExpand( returnStockAnalystConsensusDTO ).toUri());
        logMethodEnd( methodName, returnStockAnalystConsensusDTO );
        return new ResponseEntity<>( stockAnalystConsensusDTO, httpHeaders, HttpStatus.CREATED );
    }

    @Autowired
    public void setStockAnalystConsensusService( final StockAnalystConsensusEntityService stockAnalystConsensusService )
    {
        this.stockAnalystConsensusService = stockAnalystConsensusService;
    }
}
