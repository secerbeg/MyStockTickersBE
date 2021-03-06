package com.stocktracker.servicelayer.service;

import com.stocktracker.common.exceptions.DuplicateEntityException;
import com.stocktracker.common.exceptions.EntityVersionMismatchException;
import com.stocktracker.common.exceptions.LinkedAccountNotFoundException;
import com.stocktracker.common.exceptions.TradeItAccountNotFoundException;
import com.stocktracker.common.exceptions.VersionedEntityNotFoundException;
import com.stocktracker.repositorylayer.entity.LinkedAccountEntity;
import com.stocktracker.repositorylayer.entity.TradeItAccountEntity;
import com.stocktracker.repositorylayer.repository.TradeItAccountRepository;
import com.stocktracker.servicelayer.service.common.TradeItAccountComparisonService;
import com.stocktracker.servicelayer.tradeit.apiresults.KeepSessionAliveAPIResult;
import com.stocktracker.servicelayer.tradeit.types.LinkedAccount;
import com.stocktracker.weblayer.dto.LinkedAccountDTO;
import com.stocktracker.weblayer.dto.TradeItAccountDTO;
import com.stocktracker.weblayer.dto.tradeit.AuthenticateDTO;
import com.stocktracker.weblayer.dto.tradeit.KeepSessionAliveDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * This service communicates between the Web layer and Repositories using the Domain Model
 * It is a stateless instance that provides business level services
 *
 * Created by mike on 12/4/2017.
 */
@Service
@EnableCaching(proxyTargetClass = true)
public class TradeItAccountEntityService extends UuidEntityService<TradeItAccountEntity,
                                                                   TradeItAccountDTO,
                                                                   TradeItAccountRepository>
{
    @Autowired
    private TradeItAccountRepository tradeItAccountRepository;
    @Autowired
    private LinkedAccountEntityService linkedAccountEntityService;
    @Autowired
    private TradeItAccountComparisonService tradeItAccountComparisonService;

    /**
     * This method is called prior to inserting the {@code entity} into the table.
     * @param entity
     * @throws VersionedEntityNotFoundException
     */
    @Override
    protected void preAddEntity( final TradeItAccountEntity entity )
        throws VersionedEntityNotFoundException
    {
        super.preAddEntity( entity );
        /*
         * Set it to null on create, there were JSON conversion issues on create.
         */
        entity.setAuthTimestamp( null );
    }

    /**
     * This method is called during the GetOAuthAccessTokenAPIResult TradeIt call
     * @param customerUuid
     * @param broker
     * @param accountName
     * @param userId
     * @param userToken
     * @return
     * @throws EntityVersionMismatchException
     * @throws DuplicateEntityException
     */
    public TradeItAccountDTO createAccount( final UUID customerUuid, final String broker, final String accountName,
                                            final String userId, final String userToken )
        throws EntityVersionMismatchException,
               DuplicateEntityException
    {
        final String methodName = "createAccount";
        logMethodBegin( methodName, customerUuid, broker, accountName, userId, userToken );
        Objects.requireNonNull( customerUuid, "customerUuid cannot be null" );
        Objects.requireNonNull( broker, "broker cannot be null" );
        Objects.requireNonNull( accountName, "accountName cannot be null" );
        Objects.requireNonNull( userId, "userId cannot be null" );
        Objects.requireNonNull( userToken, "userToken cannot be null" );
        TradeItAccountEntity tradeItAccountEntity = this.context.getBean( TradeItAccountEntity.class );
        tradeItAccountEntity.setBrokerage( broker );
        tradeItAccountEntity.setName( accountName );
        tradeItAccountEntity.setUserId( userId );
        tradeItAccountEntity.setUserToken( userToken );
        tradeItAccountEntity.setCustomerUuid( customerUuid );
        tradeItAccountEntity.setTradeItAccountInd( true );
        tradeItAccountEntity = this.saveEntity( tradeItAccountEntity );
        logDebug( methodName, "saved entity: {0}", tradeItAccountEntity );
        TradeItAccountDTO tradeItAccountDTO = this.entityToDTO( tradeItAccountEntity );
        logMethodEnd( methodName, tradeItAccountDTO );
        return tradeItAccountDTO;
    }

    /**
     * Get the list of customer accounts.
     * @param customerUuid
     * @return
     */
    public List<TradeItAccountDTO> getAccounts( final UUID customerUuid )
    {
        final String methodName = "getAccounts";
        logMethodBegin( methodName, customerUuid );
        List<TradeItAccountEntity> accountEntities = this.tradeItAccountRepository.findByCustomerUuid( customerUuid );
        List<TradeItAccountDTO> tradeItAccountDTOS = this.entitiesToDTOs( accountEntities );
        logMethodEnd( methodName, tradeItAccountDTOS );
        return tradeItAccountDTOS;
    }

    /**
     * Get the TradeIt account by customer uuid and account name.
     * @param customerUuid
     * @param accountName
     * @return
     */
    public TradeItAccountEntity getEntity( final UUID customerUuid, final String accountName )
    {
        final String methodName = "getAccount";
        logMethodBegin( methodName, customerUuid, accountName );
        final TradeItAccountEntity tradeItAccountEntity = this.tradeItAccountRepository
                                                              .findByCustomerUuidAndName( customerUuid, accountName );
        logMethodEnd( methodName, tradeItAccountEntity );
        return tradeItAccountEntity;
    }

    /**
     * This method is called after a successful authentication call.  This is a wrapper method for the follow method
     * with the same name.
     * @param tradeItAccountUuid
     * @param authenticateDTO
     * @throws LinkedAccountNotFoundException
     * @throws TradeItAccountNotFoundException
     * @throws EntityVersionMismatchException
     * @throws DuplicateEntityException
     */
    public void synchronizeTradeItAccount( final UUID tradeItAccountUuid,
                                           final AuthenticateDTO authenticateDTO )
        throws LinkedAccountNotFoundException,
               TradeItAccountNotFoundException,
               EntityVersionMismatchException,
               DuplicateEntityException
    {
        final String methodName = "synchronizeTradeItAccount";
        logMethodBegin( methodName, tradeItAccountUuid, authenticateDTO );
        TradeItAccountEntity tradeItAccountEntity = null;
        try
        {
            tradeItAccountEntity = this.getEntity( tradeItAccountUuid );
        }
        catch( VersionedEntityNotFoundException e )
        {
            throw new TradeItAccountNotFoundException( tradeItAccountUuid );
        }
        this.synchronizeTradeItAccount( tradeItAccountEntity, authenticateDTO );
        logMethodEnd( methodName );
    }

    /**
     * This method is called when the user has been authenticated.
     *
     * Also, the LINKED_ACCOUNT table is updated (added/updated -- not deleted) to reflect the {@code LinkedAccount}
     * accounts returned from the authentication call.
     * @param tradeItAccountEntity
     * @param authenticateDTO The linked accounts will be set in this DTO from the linked accounts.
     * @throws LinkedAccountNotFoundException
     * @throws TradeItAccountNotFoundException
     * @throws EntityVersionMismatchException
     * @throws DuplicateEntityException
     */
    public void synchronizeTradeItAccount( final TradeItAccountEntity tradeItAccountEntity,
                                           final AuthenticateDTO authenticateDTO )
        throws LinkedAccountNotFoundException,
               EntityVersionMismatchException,
               DuplicateEntityException,
               TradeItAccountNotFoundException
    {
        final String methodName = "synchronizeTradeItAccount";
        logMethodBegin( methodName, tradeItAccountEntity, authenticateDTO );
        this.tradeItAccountComparisonService
            .compare( tradeItAccountEntity, authenticateDTO );
        /*
         * Gather up all of the linked accounts and add them to the authenticate DTO.
         */
        List<LinkedAccountDTO> linkedAccountDTOs = this.linkedAccountEntityService
                                                       .getLinkedAccountsForTradeItAccount( tradeItAccountEntity.getUuid() );
        authenticateDTO.setLinkedAccounts( linkedAccountDTOs );
        /*
         * Need to send back the updated account DTO
         */
        final TradeItAccountDTO tradeItAccountDTO = this.entityToDTO( tradeItAccountEntity );
        authenticateDTO.setTradeItAccount( tradeItAccountDTO );
        logMethodEnd( methodName, authenticateDTO );
    }

    /**
     * This method is called after a successful renewal of the user's authentication token.
     * {@code keepSessionAliveDTO} is populated with the TradeIt Account and linked account information.
     * @param tradeItAccountEntity
     * @param keepSessionAliveAPIResult
     * @throws TradeItAccountNotFoundException
     * @throws EntityVersionMismatchException
     * @throws DuplicateEntityException
     */
    public void keepSessionAliveSuccess( final KeepSessionAliveDTO keepSessionAliveDTO,
                                         final TradeItAccountEntity tradeItAccountEntity,
                                         final KeepSessionAliveAPIResult keepSessionAliveAPIResult )
        throws TradeItAccountNotFoundException,
               EntityVersionMismatchException,
               DuplicateEntityException
    {
        final String methodName = "keepSessionAliveSuccess";
        logMethodBegin( methodName, tradeItAccountEntity, keepSessionAliveAPIResult );
        tradeItAccountEntity.setAuthTimestamp( new Timestamp( System.currentTimeMillis() ) );
        final TradeItAccountEntity updatedTradeItAccountEntity = this.saveEntity( tradeItAccountEntity );
        final TradeItAccountDTO tradeItAccountDTO = this.entityToDTO( updatedTradeItAccountEntity );
        final List<LinkedAccountDTO> linkedAccountDTOs = this.linkedAccountEntityService
                                                             .getLinkedAccountsForTradeItAccount( updatedTradeItAccountEntity.getUuid() );
        keepSessionAliveDTO.setTradeItAccount( tradeItAccountDTO );
        keepSessionAliveDTO.setLinkedAccounts( linkedAccountDTOs );
        logMethodEnd( methodName, tradeItAccountDTO );
    }

    /**
     * Adds a new linked account to the {@code tradeItAccountEntity} and the database.
     * @param tradeItAccountEntity
     * @param linkedAccount The account information from TradeIt.
     * @throws EntityVersionMismatchException
     * @throws DuplicateEntityException
     */
    public LinkedAccountEntity addLinkedAccount( final TradeItAccountEntity tradeItAccountEntity,
                                                 final LinkedAccount linkedAccount )
        throws EntityVersionMismatchException,
               DuplicateEntityException
    {
        final String methodName = "addLinkedAccount";
        logMethodBegin( methodName, tradeItAccountEntity, linkedAccount );
        /*
         * Convert the account information return from TradeIt to a LinkedEntityAccount instance.
         */
        LinkedAccountEntity linkedAccountEntity = LinkedAccountEntity.newInstance( linkedAccount );
        /*
         * Set the bidirectional relationship
         */
        linkedAccountEntity.setTradeItAccountUuid( tradeItAccountEntity.getUuid() );
        tradeItAccountEntity.addLinkedAccount( linkedAccountEntity );
        /*
         * Save the linked account
         */
        linkedAccountEntity = this.linkedAccountEntityService
                                  .saveEntity( linkedAccountEntity );
        logMethodEnd( methodName, linkedAccountEntity );
        return linkedAccountEntity;
    }

    /**
     * Determines if the token has expired for the TradeItAccount.
     * @param tradeItAccountUuid
     * @return
     * @throws VersionedEntityNotFoundException
     */
    public boolean isTokenExpired( final UUID tradeItAccountUuid )
        throws VersionedEntityNotFoundException
    {
        return this.getEntity( tradeItAccountUuid ).isTokenExpired();
    }

    @Override
    protected TradeItAccountDTO createDTO()
    {
        return this.context.getBean( TradeItAccountDTO.class );
    }

    @Override
    protected TradeItAccountEntity createEntity()
    {
        return this.context.getBean( TradeItAccountEntity.class );
    }

    @Override
    protected TradeItAccountRepository getRepository()
    {
        return this.tradeItAccountRepository;
    }
}
