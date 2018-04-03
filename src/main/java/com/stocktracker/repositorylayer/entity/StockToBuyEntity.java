package com.stocktracker.repositorylayer.entity;

import com.stocktracker.servicelayer.service.StockNoteSourceEntityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;

@Component
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
@Entity
@Table( name = "stock_to_buy", schema = "stocktracker", catalog = "" )
public class StockToBuyEntity implements StockNoteSourceEntityService.StockNoteSourceEntityContainer,
                                         VersionedEntity<Integer>
{
    private static final int COMMENTS_LEN = 4096;
    private Integer id;
    private Integer customerId;
    private String tickerSymbol;
    private String comments;
    private BigDecimal buySharesUpToPrice;
    private BigDecimal stockPriceWhenCreated;
    private String completed;
    private Integer notesSourceId;
    private Integer version;
    private StockNoteSourceEntity stockNoteSourceByNotesSourceId;
    private Timestamp buyAfterDate;
    private Timestamp createDate;
    private Timestamp updateDate;

    @Id
    @Column( name = "id", nullable = false )
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    public Integer getId()
    {
        return id;
    }

    public void setId( final Integer id )
    {
        this.id = id;
    }

    @Basic
    @Column( name = "customer_id" )
    public Integer getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId( final Integer customerId )
    {
        this.customerId = customerId;
    }

    @Basic
    @Column( name = "ticker_symbol" )
    public String getTickerSymbol()
    {
        return tickerSymbol;
    }

    public void setTickerSymbol( final String tickerSymbol )
    {
        this.tickerSymbol = tickerSymbol == null ? null : StringUtils.truncate( tickerSymbol, StockCompanyEntity.TICKER_SYMBOL_LEN );
    }

    @Basic
    @Column( name = "comments", nullable = true, length = 4096 )
    public String getComments()
    {
        return comments;
    }

    public void setComments( final String comments )
    {
        this.comments = comments == null ? null : StringUtils.truncate( comments, COMMENTS_LEN );
    }

    @Basic
    @Column( name = "buy_shares_up_to_price", nullable = true, precision = 2 )
    public BigDecimal getBuySharesUpToPrice()
    {
        return buySharesUpToPrice;
    }

    public void setBuySharesUpToPrice( final BigDecimal buySharesBelow )
    {
        this.buySharesUpToPrice = buySharesBelow;
    }

    @Basic
    @Column( name = "stock_price_when_created", nullable = true, precision = 2 )
    public BigDecimal getStockPriceWhenCreated()
    {
        return stockPriceWhenCreated;
    }

    public void setStockPriceWhenCreated( final BigDecimal stockPrice )
    {
        this.stockPriceWhenCreated = stockPrice;
    }

    @Basic
    @Column( name = "create_date", nullable = false )
    public Timestamp getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate( final Timestamp createDate )
    {
        this.createDate = createDate;
    }

    @Basic
    @Column( name = "update_date", nullable = true )
    public Timestamp getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate( final Timestamp updateDate )
    {
        this.updateDate = updateDate;
    }

    @Basic
    @Column( name = "completed", nullable = false, length = 1 )
    public String getCompleted()
    {
        return completed;
    }

    public void setCompleted( final String completed )
    {
        this.completed = completed;
    }

    @Basic
    @Column( name = "buy_after_date", nullable = true )
    public Timestamp getBuyAfterDate()
    {
        return buyAfterDate;
    }

    public void setBuyAfterDate( final Timestamp buyAfterDate )
    {
        this.buyAfterDate = buyAfterDate;
    }

    @Basic
    @Column( name = "notes_source_id", nullable = true, insertable = false, updatable = false )
    public Integer getNotesSourceId()
    {
        return notesSourceId;
    }

    public void setNotesSourceId( final Integer notesSourceId )
    {
        this.notesSourceId = notesSourceId;
    }

    @ManyToOne
    @JoinColumn( name = "notes_source_id", referencedColumnName = "id" )
    public StockNoteSourceEntity getStockNoteSourceByNotesSourceId()
    {
        return stockNoteSourceByNotesSourceId;
    }

    public void setStockNoteSourceByNotesSourceId( final StockNoteSourceEntity stockNoteSourceByNotesSourceId )
    {
        this.stockNoteSourceByNotesSourceId = stockNoteSourceByNotesSourceId;
    }

    @Transient
    @Override
    public Optional<StockNoteSourceEntity> getNotesSourceEntity()
    {
        return Optional.ofNullable( this.stockNoteSourceByNotesSourceId );
    }

    @Transient
    @Override
    public void setNotesSourceEntity( final StockNoteSourceEntity stockNoteSourceEntity )
    {
        this.stockNoteSourceByNotesSourceId = stockNoteSourceEntity;
    }

    @Transient
    @Override
    public Optional<Integer> getStockNoteSourceId()
    {
        return Optional.ofNullable( this.stockNoteSourceByNotesSourceId == null
                                    ? null
                                    : this.stockNoteSourceByNotesSourceId.getId() );
    }

    @Basic
    @Column( name = "version" )
    public Integer getVersion()
    {
        return version;
    }

    public void setVersion( final Integer version )
    {
        this.version = version;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !(o instanceof StockToBuyEntity) )
        {
            return false;
        }
        final StockToBuyEntity that = (StockToBuyEntity) o;
        return Objects.equals( id, that.id );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id );
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder( "StockToBuyEntity{" );
        sb.append( "id=" ).append( id );
        sb.append( ", customerId=" ).append( customerId );
        sb.append( ", tickerSymbol='" ).append( tickerSymbol ).append( '\'' );
        sb.append( ", comments='" ).append( comments ).append( '\'' );
        sb.append( ", notesSourceId=" ).append( notesSourceId );
        sb.append( ", buySharesUpToPrice=" ).append( buySharesUpToPrice );
        sb.append( ", stockPriceWhenCreated=" ).append( stockPriceWhenCreated );
        sb.append( ", completed='" ).append( completed ).append( '\'' );
        sb.append( ", buyAfterDate=" ).append( buyAfterDate );
        sb.append( ", stockNoteSourceByNotesSourceId=" ).append( stockNoteSourceByNotesSourceId );
        sb.append( ", version=" ).append( version );
        sb.append( ", createDate=" ).append( createDate );
        sb.append( ", updateDate=" ).append( updateDate );
        sb.append( '}' );
        return sb.toString();
    }

}
