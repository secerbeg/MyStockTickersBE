package com.stocktracker.servicelayer.tradeit.types;

/**
 * This class contains the position details for a single stock.
 * @see https://www.trade.it/documentation#PositionService
 * @author michael.earl 1/17/2018
 */
public class TradeItPosition
{
    private double costbasis;
    private String holdingType;
    private double lastPrice;
    private double quantity;
    private String symbol;
    private String symbolClass;
    private double todayGainLossDollar;
    private double todayGainLossPercentage;
    private double totalGainLossDollar;
    private double totalGainLossPercentage;

    /**
     * The total base cost of the security [+, null]. If null this field must not be displayed to the user.
     */
    public double getCostbasis()
    {
        return costbasis;
    }

    public void setCostbasis( double costbasis )
    {
        this.costbasis = costbasis;
    }

    /**
     * "LONG" or "SHORT"
     */
    public String getHoldingType()
    {
        return holdingType;
    }

    public void setHoldingType( String holdingType )
    {
        this.holdingType = holdingType;
    }

    /**
     * The last traded price of the security [+ / null]. If null last price must be fetched from a quote provider.
     */
    public double getLastPrice()
    {
        return lastPrice;
    }

    public void setLastPrice( double lastPrice )
    {
        this.lastPrice = lastPrice;
    }

    /**
     * The total quantity held. It's a double to support cash and Mutual Funds [+]
     */
    public double getQuantity()
    {
        return quantity;
    }

    public void setQuantity( double quantity )
    {
        this.quantity = quantity;
    }

    /**
     * The ticker symbol or CUSIP for fixed income positions. Symbols for Class A or B shares use dot annotation
     * (BRK.A). For Option, we follow the OCC symbology format (GWRRF 141122P00019500).
     */
    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol( String symbol )
    {
        this.symbol = symbol;
    }

    /**
     * The type of security: BUY_WRITES, CASH, COMBO, EQUITY_OR_ETF, FIXED_INCOME, FUTURE, FX, MULTILEG, MUTUAL_FUNDS,
     * OPTION, SPREADS, UNKNOWN
     */
    public String getSymbolClass()
    {
        return symbolClass;
    }

    public void setSymbolClass( String symbolClass )
    {
        this.symbolClass = symbolClass;
    }

    /**
     * The total gain/loss in dollars for the day for the position [+ / - / null]. If null this field must not be
     * displayed to the user.
     */
    public double getTodayGainLossDollar()
    {
        return todayGainLossDollar;
    }

    public void setTodayGainLossDollar( double todayGainLossDollar )
    {
        this.todayGainLossDollar = todayGainLossDollar;
    }

    /**
     * The percentage gain/loss for the day for the position [+ / - / null]. If null this field must not be displayed to the user.
     */
    public double getTodayGainLossPercentage()
    {
        return todayGainLossPercentage;
    }

    public void setTodayGainLossPercentage( double todayGainLossPercentage )
    {
        this.todayGainLossPercentage = todayGainLossPercentage;
    }

    /**
     * The total gain/loss in dollars for the position [+ / - / null]. If null this field must not be displayed to the user.
     */
    public double getTotalGainLossDollar()
    {
        return totalGainLossDollar;
    }

    public void setTotalGainLossDollar( double totalGainLossDollar )
    {
        this.totalGainLossDollar = totalGainLossDollar;
    }

    /**
     * The total percentage of gain/loss for the position [+ / - / null]. If null this field must not be displayed to the user.
     */
    public double getTotalGainLossPercentage()
    {
        return totalGainLossPercentage;
    }

    public void setTotalGainLossPercentage( double totalGainLossPercentage )
    {
        this.totalGainLossPercentage = totalGainLossPercentage;
    }
}