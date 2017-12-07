package com.stocktracker.repositorylayer.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 *
 * Created by Mike on December 4th, 2017
 */
@Entity
@Table( name = "account", schema = "stocktracker", catalog = "" )
public class AccountEntity
{
    private Integer id;
    private String name;
    private String loginToken;
    private String brokerage;
    private Timestamp createDate;
    private Timestamp updateDate;
    private CustomerEntity customerByCustomerId;

    public static AccountEntity newInstance()
    {
        return new AccountEntity();
    }

    @Id
    @Column( name = "id" )
    public Integer getId()
    {
        return id;
    }

    public void setId( final Integer id )
    {
        this.id = id;
    }

    @Basic
    @Column( name = "name" )
    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    @Basic
    @Column( name = "login_token" )
    public String getLoginToken()
    {
        return loginToken;
    }

    public void setLoginToken( final String loginToken )
    {
        this.loginToken = loginToken;
    }

    @Basic
    @Column( name = "brokerage" )
    public String getBrokerage()
    {
        return brokerage;
    }

    public void setBrokerage( final String brokerage )
    {
        this.brokerage = brokerage;
    }

    @Basic
    @Column( name = "create_date" )
    public Timestamp getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate( final Timestamp createDate )
    {
        this.createDate = createDate;
    }

    @Basic
    @Column( name = "update_date" )
    public Timestamp getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate( final Timestamp updateDate )
    {
        this.updateDate = updateDate;
    }

    @ManyToOne
    @JoinColumn( name = "customer_id", referencedColumnName = "id", nullable = false )
    public CustomerEntity getCustomerByCustomerId()
    {
        return customerByCustomerId;
    }

    public void setCustomerByCustomerId( final CustomerEntity customerByCustomerId )
    {
        this.customerByCustomerId = customerByCustomerId;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !(o instanceof AccountEntity) )
        {
            return false;
        }

        final AccountEntity that = (AccountEntity) o;

        return getId().equals( that.getId() );
    }

    @Override
    public int hashCode()
    {
        return getId().hashCode();
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder( "AccountEntity{" );
        sb.append( "id=" ).append( id );
        sb.append( ", name='" ).append( name ).append( '\'' );
        sb.append( ", loginToken='" ).append( loginToken ).append( '\'' );
        sb.append( ", brokerage='" ).append( brokerage ).append( '\'' );
        sb.append( ", createDate=" ).append( createDate );
        sb.append( ", updateDate=" ).append( updateDate );
        sb.append( ", customerByCustomerId=" ).append( customerByCustomerId );
        sb.append( '}' );
        return sb.toString();
    }
}