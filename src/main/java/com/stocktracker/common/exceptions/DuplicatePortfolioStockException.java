package com.stocktracker.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is thrown when an attempt is made to add a portfolio stock table entry that already exists
 * Created by mike on 11/9/2016.
 */
@ResponseStatus(value= HttpStatus.CONFLICT)
public class DuplicatePortfolioStockException extends RuntimeException
{
    public DuplicatePortfolioStockException( final String customerId, final String portfolioId, final String tickerSymbol )
    {
        super( String.format( "A portfolio stock entry already exists for customer Id: %s portfolioId: %s tickerSymbol: %s",
                              customerId, portfolioId, tickerSymbol ));
    }
}
