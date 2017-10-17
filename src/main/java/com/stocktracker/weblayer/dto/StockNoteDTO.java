package com.stocktracker.weblayer.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by mike on 5/7/2017.
 */
public class StockNoteDTO
{
    private Integer id;
    private Integer customerId;
    private String notes;
    private String notesDate;
    private String notesSourceName;
    private Integer notesSourceId;
    private Integer notesRating;
    private Boolean publicInd;
    private Byte bullOrBear;
    private String actionTaken;
    private Integer actionTakenShares;
    private String dateCreated;
    private String dateModified;
    /**
     * The list of stocks for the notes 1 to M relationship.
     * Name of the methods for this cannot be the same as the StockNoteEntity or copy properties classes will
     * copy the StockNoteStockEntity instance into this list -- they are the wrong type!!!
     */
    private List<StockNoteStockDTO> stocks;

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes( String notes )
    {
        this.notes = notes;
    }

    public String getNotesDate()
    {
        return notesDate;
    }

    public void setNotesDate( String notesDate )
    {
        this.notesDate = notesDate;
    }

    public String getNotesSourceName()
    {
        return notesSourceName;
    }

    public void setNotesSourceName( String notesSourceName )
    {
        this.notesSourceName = notesSourceName;
    }

    public Integer getNotesSourceId()
    {
        return notesSourceId;
    }

    public void setNotesSourceId( Integer notesSourceId )
    {
        this.notesSourceId = notesSourceId;
    }

    public String getDateCreated()
    {
        return dateCreated;
    }

    public void setDateCreated( String dateCreated )
    {
        this.dateCreated = dateCreated;
    }

    public String getDateModified()
    {
        return dateModified;
    }

    public void setDateModified( String dateModified )
    {
        this.dateModified = dateModified;
    }

    public Integer getNotesRating()
    {
        return notesRating;
    }

    public void setNotesRating( Integer notesRating )
    {
        this.notesRating = notesRating;
    }

    public Boolean getPublicInd()
    {
        return publicInd;
    }

    public void setPublicInd( Boolean publicInd )
    {
        this.publicInd = publicInd;
    }

    public Integer getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId( Integer customerId )
    {
        this.customerId = customerId;
    }

    public Byte getBullOrBear()
    {
        return bullOrBear;
    }

    public void setBullOrBear( Byte bullOrBear )
    {
        this.bullOrBear = bullOrBear;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !(o instanceof StockNoteDTO) )
        {
            return false;
        }
        final StockNoteDTO that = (StockNoteDTO) o;
        return Objects.equals( id, that.id );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id );
    }

    public List<StockNoteStockDTO> getStocks()
    {
        if ( stocks == null )
        {
            stocks = new ArrayList<>();
        }
        return stocks;
    }

    public void setStocks( List<StockNoteStockDTO> stocks )
    {
        this.stocks = stocks;
    }

    public String getActionTaken()
    {
        return actionTaken;
    }

    public void setActionTaken( final String actionTaken )
    {
        this.actionTaken = actionTaken;
    }

    public Integer getActionTakenShares()
    {
        return actionTakenShares;
    }

    public void setActionTakenShares( final Integer actionTakenShares )
    {
        this.actionTakenShares = actionTakenShares;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder( "StockNoteDTO{" );
        sb.append( "id=" ).append( id );
        sb.append( ", customerId=" ).append( customerId );
        sb.append( ", notes='" ).append( notes ).append( '\'' );
        sb.append( ", notesDate='" ).append( notesDate ).append( '\'' );
        sb.append( ", notesSourceName='" ).append( notesSourceName ).append( '\'' );
        sb.append( ", notesSourceId=" ).append( notesSourceId );
        sb.append( ", notesRating=" ).append( notesRating );
        sb.append( ", publicInd=" ).append( publicInd );
        sb.append( ", bullOrBear=" ).append( bullOrBear );
        sb.append( ", actionTaken=" ).append( actionTaken );
        sb.append( ", actionTakenShares=" ).append( actionTakenShares );
        sb.append( ", dateCreated=" ).append( dateCreated );
        sb.append( ", dateModified=" ).append( dateModified );
        sb.append( ", stocks=" ).append( stocks );
        sb.append( '}' );
        return sb.toString();
    }
}
