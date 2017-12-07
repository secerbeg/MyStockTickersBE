package com.stocktracker.servicelayer.service;

import com.stocktracker.common.MyLogger;
import com.stocktracker.common.exceptions.AccountNotFoundException;
import com.stocktracker.repositorylayer.entity.AccountEntity;
import com.stocktracker.repositorylayer.entity.PortfolioEntity;
import com.stocktracker.repositorylayer.repository.AccountRepository;
import com.stocktracker.repositorylayer.repository.PortfolioRepository;
import com.stocktracker.weblayer.dto.AccountDTO;
import com.stocktracker.weblayer.dto.PortfolioDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * This service communicates between the Web layer and Repositories using the Domain Model
 * It is a stateless instance that provides business level services
 *
 * Created by mike on 12/4/2017.
 */
@Service
@Transactional
public class AccountService extends BaseService<AccountEntity, AccountDTO> implements MyLogger
{
    private PortfolioService portfolioService;
    private AccountRepository accountRepository;
    private PortfolioRepository portfolioRepository;

    /**
     * Get the account by id request
     * @param customerId
     * @param accountId
     * @return
     */
    public AccountDTO getAccountById( final int customerId, final int accountId )
    {
        final String methodName = "getAccountById";
        logMethodBegin( methodName, customerId, accountId );
        this.validateAccountId( customerId, accountId );
        AccountEntity accountEntity = accountRepository.findOne( accountId );
        if ( accountEntity == null )
        {
            throw new AccountNotFoundException( customerId, accountId );
        }
        AccountDTO accountDTO = this.entityToDTO( accountEntity );
        logMethodEnd( methodName, accountDTO );
        return accountDTO;
    }

    /**
     * Create the account for the customer.
     * @param customerId
     * @param accountDTO
     * @return AccountDTO
     */
    public AccountDTO createAccount( final int customerId, final AccountDTO accountDTO )
    {
        final String methodName = "createAccount";
        logMethodBegin( methodName, customerId, accountDTO );
        AccountEntity accountEntity = this.dtoToEntity( accountDTO );
        accountEntity = this.accountRepository.save( accountEntity );
        AccountDTO returnAccountDTO = this.entityToDTO( accountEntity );
        logMethodEnd( methodName, returnAccountDTO );
        return returnAccountDTO;
    }

    /**
     * Delete the account for the customer.
     * @param customerId
     * @param accountId
     */
    public void deleteAccount( final int customerId, final int accountId )
    {
        final String methodName = "createAccount";
        logMethodBegin( methodName, customerId, accountId );
        this.validateAccountId( customerId, accountId );
        this.accountRepository.delete( accountId );
        logMethodEnd( methodName );
    }

    /**
     * Updates the database with the information in {@code accountDTO}
     * @param customerId
     * @param accountDTO
     */
    public AccountDTO updateAccount( final int customerId, final AccountDTO accountDTO )
    {
        final String methodName = "updateAccount";
        logMethodBegin( methodName, customerId, accountDTO );
        this.validateAccountId( customerId, accountDTO.getId() );
        AccountEntity accountEntity = this.dtoToEntity( accountDTO );
        accountEntity = this.accountRepository.save( accountEntity );
        AccountDTO returnAccountDTO = this.entityToDTO( accountEntity );
        logMethodEnd( methodName, returnAccountDTO );
        return returnAccountDTO;
    }

    /**
     * Determines if the account exists for {@code accountId}
     * @param customerId
     * @param accountId
     * @throws AccountNotFoundException
     */
    private void validateAccountId( final int customerId, final int accountId )
    {
        AccountEntity accountEntity = this.accountRepository.findById( accountId );
        if ( accountEntity == null )
        {
            throw new AccountNotFoundException( customerId, accountId ) ;
        }
    }

    @Override
    protected AccountDTO entityToDTO( final AccountEntity entity )
    {
        Objects.requireNonNull( entity );
        AccountDTO accountDTO = AccountDTO.newInstance();
        BeanUtils.copyProperties( entity, accountDTO );
        return accountDTO;
    }

    @Override
    protected AccountEntity dtoToEntity( final AccountDTO dto )
    {
        Objects.requireNonNull( dto );
        AccountEntity accountEntity = AccountEntity.newInstance();
        BeanUtils.copyProperties( dto, accountEntity );
        return accountEntity;
    }

    @Autowired
    public void setPortfolioRepository( final PortfolioRepository portfolioRepository )
    {
        this.portfolioRepository = portfolioRepository;
    }

    @Autowired
    public void setAccountRepository( final AccountRepository accountRepository )
    {
        this.accountRepository = accountRepository;
    }

    @Autowired
    public void setPortfolioService( final PortfolioService portfolioService )
    {
        this.portfolioService = portfolioService;
    }
}