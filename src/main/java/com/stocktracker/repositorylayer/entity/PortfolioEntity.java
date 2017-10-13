package com.stocktracker.repositorylayer.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Created by mike on 9/4/2016.
 */
@Entity
@Table( name = "portfolio", schema = "stocktracker", catalog = "" )
public class PortfolioEntity
{
    private Integer id;
    private String name;
    private Integer customerId;
    private Timestamp createDate;

    /**
     * Create a new instance from a PortfolioDTO
     * @return
     */
    public static PortfolioEntity newInstance()
    {
        PortfolioEntity portfolioEntity = new PortfolioEntity();
        return portfolioEntity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "id", nullable = false )
    public Integer getId()
    {
        return id;
    }

    public void setId( final Integer id )
    {
        this.id = id;
    }

    @Basic
    @Column( name = "name", nullable = false, length = 20 )
    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    @Basic
    @Column( name = "customer_id", nullable = false )
    public Integer getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId( Integer customerId )
    {
        this.customerId = customerId;
    }

    /**
     * Can be null as default value will be set by the DB.
     * @return
     */
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

    /*
    @OneToMany
    @JoinColumn( name = "portfolio_id", referencedColumnName = "id", nullable = false )
    public Collection<PortfolioStockEntity> getPortfolioStocksById()
    {
        return portfolioStocksById;
    }

    public void setPortfolioStocksById( final Collection<PortfolioStockEntity> portfolioStocksById )
    {
        this.portfolioStocksById = portfolioStocksById;
    }
    */

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final PortfolioEntity that = (PortfolioEntity) o;
        return getId() == that.getId() &&
               Objects.equals( name, that.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getId(), name );
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder( "PortfolioEntity{" );
        sb.append( "id=" ).append( id );
        sb.append( ", name='" ).append( name ).append( '\'' );
        sb.append( ", customerId=" ).append( customerId );
        sb.append( ", createDate=" ).append( createDate );
        //sb.append( ", portfolioStocksById=" ).append( portfolioStocksById );
        sb.append( '}' );
        return sb.toString();
    }
}
