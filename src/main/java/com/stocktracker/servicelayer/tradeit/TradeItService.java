package com.stocktracker.servicelayer.tradeit;

import com.stocktracker.common.MyLogger;
import com.stocktracker.common.exceptions.EntityVersionMismatchException;
import com.stocktracker.common.exceptions.LinkedAccountNotFoundException;
import com.stocktracker.common.exceptions.TradeItAccountNotFoundException;
import com.stocktracker.common.exceptions.TradeItAuthenticationException;
import com.stocktracker.repositorylayer.entity.LinkedAccountEntity;
import com.stocktracker.repositorylayer.entity.TradeItAccountEntity;
import com.stocktracker.servicelayer.service.TradeItAccountEntityService;
import com.stocktracker.servicelayer.tradeit.apicalls.AnswerSecurityQuestionAPICall;
import com.stocktracker.servicelayer.tradeit.apicalls.AuthenticateAPICall;
import com.stocktracker.servicelayer.tradeit.apicalls.CloseSessionAPICall;
import com.stocktracker.servicelayer.tradeit.apicalls.GetAccountOverviewAPICall;
import com.stocktracker.servicelayer.tradeit.apicalls.GetBrokersAPICall;
import com.stocktracker.servicelayer.tradeit.apicalls.GetOAuthAccessTokenAPICall;
import com.stocktracker.servicelayer.tradeit.apicalls.GetOAuthAccessTokenUpdateURLAPICall;
import com.stocktracker.servicelayer.tradeit.apicalls.GetPositionsAPICall;
import com.stocktracker.servicelayer.tradeit.apicalls.KeepSessionAliveAPICall;
import com.stocktracker.servicelayer.tradeit.apicalls.RequestOAuthPopUpURLAPICall;
import com.stocktracker.servicelayer.tradeit.apicalls.TradeItAPICallParameters;
import com.stocktracker.servicelayer.tradeit.apicalls.TradeItAPIRestCall;
import com.stocktracker.servicelayer.tradeit.apiresults.AnswerSecurityQuestionAPIResult;
import com.stocktracker.servicelayer.tradeit.apiresults.AuthenticateAPIResult;
import com.stocktracker.servicelayer.tradeit.apiresults.CloseSessionAPIResult;
import com.stocktracker.servicelayer.tradeit.apiresults.GetAccountOverViewAPIResult;
import com.stocktracker.servicelayer.tradeit.apiresults.GetBrokersAPIResult;
import com.stocktracker.servicelayer.tradeit.apiresults.GetOAuthAccessTokenAPIResult;
import com.stocktracker.servicelayer.tradeit.apiresults.GetOAuthAccessTokenUpdateURLAPIResult;
import com.stocktracker.servicelayer.tradeit.apiresults.GetPositionsAPIResult;
import com.stocktracker.servicelayer.tradeit.apiresults.KeepSessionAliveAPIResult;
import com.stocktracker.servicelayer.tradeit.apiresults.RequestOAuthPopUpURLAPIResult;
import com.stocktracker.servicelayer.tradeit.apiresults.TradeItAPIResult;
import com.stocktracker.weblayer.dto.TradeItAccountDTO;
import com.stocktracker.weblayer.dto.tradeit.AnswerSecurityQuestionDTO;
import com.stocktracker.weblayer.dto.tradeit.AuthenticateDTO;
import com.stocktracker.weblayer.dto.tradeit.CloseSessionDTO;
import com.stocktracker.weblayer.dto.tradeit.GetAccountOverviewDTO;
import com.stocktracker.weblayer.dto.tradeit.GetBrokersDTO;
import com.stocktracker.weblayer.dto.tradeit.GetOAuthAccessTokenDTO;
import com.stocktracker.weblayer.dto.tradeit.GetOAuthAccessTokenUpdateURLDTO;
import com.stocktracker.weblayer.dto.tradeit.KeepSessionAliveDTO;
import com.stocktracker.weblayer.dto.tradeit.RequestOAuthPopUpURLDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

/**
 * This service contains all of the REST calls to TradeIt
 */
@Service
public class TradeItService implements MyLogger
{
    private static final Logger logger = LoggerFactory.getLogger( TradeItService.class );

    @Autowired
    private ApplicationContext context;
    private TradeItAccountEntityService tradeItAccountEntityService;

    /**
     * Get the list of brokers supported by TradeIt
     * @return
     */
    public GetBrokersDTO getBrokers()
    {
        final String methodName = "getBrokers";
        logger.debug( methodName + ".begin" );
        final GetBrokersAPICall getBrokersAPICall = this.context.getBean( GetBrokersAPICall.class );
        GetBrokersAPIResult getBrokersAPIResult = getBrokersAPICall.execute( TradeItAPICallParameters.newMap() );
        final GetBrokersDTO getBrokersDTO = this.context.getBean( GetBrokersDTO.class );
        getBrokersDTO.setResults( getBrokersAPIResult );
        logger.debug( methodName + ".end " + getBrokersDTO );
        return getBrokersDTO;
    }

    /**
     * This method will request a URL to be used to popup a authentication window for the user to login to their
     * brokerage account.
     * @param broker
     * @return
     */
    public RequestOAuthPopUpURLDTO requestOAuthPopUpURL( @NotNull final String broker )
    {
        final String methodName = "requestOAuthPopUpURL";
        logMethodBegin( methodName, broker );
        Objects.requireNonNull( broker, "Broker cannot be null" );
        final RequestOAuthPopUpURLAPICall requestOAuthPopUpURLAPICall = this.context.getBean( RequestOAuthPopUpURLAPICall.class );
        /*
         * Create the method parameters
         */
        final TradeItAPICallParameters parameters = TradeItAPICallParameters.newMap()
                                                                              .addParameter( TradeItParameter.BROKER_PARAM, broker );
        /*
         * Execute the TradeIt API call
         */
        final RequestOAuthPopUpURLAPIResult requestOAuthPopUpURLAPIResult = requestOAuthPopUpURLAPICall.execute( parameters );
        /*
         * Get the result and convert it into a DTO.
         */
        final RequestOAuthPopUpURLDTO requestOAuthPopUpURLDTO = (RequestOAuthPopUpURLDTO )this.context.getBean( "requestOAuthPopUpURLDTO" );
        requestOAuthPopUpURLDTO.setResults( requestOAuthPopUpURLAPIResult );
        logMethodEnd( methodName, requestOAuthPopUpURLDTO );
        return requestOAuthPopUpURLDTO;
    }

    /**
     * Given the {@code oAuthVerifier} value returned from the user linking their broker account, this method
     * will obtain the user id and user token to authenticate the user to gain access to their broker account.
     * @param customerId
     * @param broker
     * @param oAuthVerifier
     * @return instance of GetOAuthAccessTokenAPIResult which contains the TradeItAccountDTO that was created if the TradeIt
     * getOAuthAccessToken was successful.
     * @throws EntityVersionMismatchException
     */
    public GetOAuthAccessTokenDTO getOAuthAccessToken( final int customerId,
                                                       @NotNull final String broker,
                                                       @NotNull final String accountName,
                                                       @NotNull final String oAuthVerifier )
        throws EntityVersionMismatchException
    {
        final String methodName = "getOAuthAccessToken";
        logMethodBegin( methodName, customerId, broker, accountName, oAuthVerifier );
        Objects.requireNonNull( broker, "broker cannot be null" );
        Objects.requireNonNull( accountName, "accountName cannot be null" );
        Objects.requireNonNull( oAuthVerifier, "oAuthVerifier cannot be null" );
        final GetOAuthAccessTokenAPICall getOAuthAccessTokenAPICall = this.context
                                                                          .getBean( GetOAuthAccessTokenAPICall.class );
        /*
         * Create the parameter map
         */
        final TradeItAPICallParameters parameters = TradeItAPICallParameters.newMap()
                                                                              .addParameter( TradeItParameter.OAUTH_VERIFIER_PARAM, oAuthVerifier )
                                                                              .addParameter( TradeItParameter.BROKER_PARAM, broker );
        /*
         * Execute the TradeIt API call
         */
        final GetOAuthAccessTokenAPIResult getOAuthAccessTokenAPIResult = getOAuthAccessTokenAPICall.execute( parameters );
        TradeItAccountDTO tradeItAccountDTO = null;
        /*
         * Check the results
         */
        if ( getOAuthAccessTokenAPIResult.isSuccessful() )
        {
            tradeItAccountDTO = this.tradeItAccountEntityService.createAccount( customerId, broker, accountName,
                                                                                getOAuthAccessTokenAPIResult.getUserId(),
                                                                                getOAuthAccessTokenAPIResult.getUserToken() );
        }
        /*
         * Create the return DTO
         */
        GetOAuthAccessTokenDTO getOAuthAccessTokenDTO = new GetOAuthAccessTokenDTO( getOAuthAccessTokenAPIResult );
        getOAuthAccessTokenDTO.setTradeItAccount( tradeItAccountDTO );
        logMethodEnd( methodName, getOAuthAccessTokenDTO );
        return getOAuthAccessTokenDTO;
    }

    /**
     * Get the URL to update the user's OAuth access token.
     * @param customerId
     * @param accountId
     * @return
     * @throws TradeItAccountNotFoundException
     * @throws TradeItAuthenticationException
     */
    public GetOAuthAccessTokenUpdateURLDTO getOAuthTokenUpdateURL( final int customerId, final int accountId )
        throws TradeItAccountNotFoundException,
               TradeItAuthenticationException
    {
        final String methodName = "getOAuthTokenUpdateURL";
        logMethodBegin( methodName, customerId, accountId );
        final TradeItAccountEntity tradeItAccountEntity = this.tradeItAccountEntityService
                                                              .getTradeItAccountEntity( customerId, accountId );
        final GetOAuthAccessTokenUpdateURLAPICall getOAuthAccessTokenUpdateURLAPICall = this.context
                                                                                            .getBean( GetOAuthAccessTokenUpdateURLAPICall.class );
        /*
         * Create the method parameter map
         */
        final TradeItAPICallParameters parameters = TradeItAPICallParameters.newMap()
                                                                              .addParameter( TradeItParameter.USER_ID_PARAM, tradeItAccountEntity.getUserId() )
                                                                              .addParameter( TradeItParameter.USER_TOKEN_PARAM, tradeItAccountEntity.getUserToken() )
                                                                              .addParameter( TradeItParameter.BROKER_PARAM, tradeItAccountEntity.getBrokerage() );
        /*
         * Make the call to TradeIt
         */
        final GetOAuthAccessTokenUpdateURLAPIResult getOAuthAccessTokenUpdateURLAPIResult = this.callTradeIt( tradeItAccountEntity,
                                                                                                              getOAuthAccessTokenUpdateURLAPICall,
                                                                                                              parameters );
        /*
         * Create the DTO result and return it.
         */
        final GetOAuthAccessTokenUpdateURLDTO getOAuthAccessTokenUpdateURLDTO = this.context
                                                                                    .getBean( GetOAuthAccessTokenUpdateURLDTO.class );
        getOAuthAccessTokenUpdateURLDTO.setResults( getOAuthAccessTokenUpdateURLAPIResult );
        logMethodEnd( methodName, getOAuthAccessTokenUpdateURLDTO );
        return getOAuthAccessTokenUpdateURLDTO;
    }

    /**
     * Using the {@code userId} and {@code userToken} from {@code getOAuthAccessToken}, these values will be authenticated
     * with TradeIt and if success, a 15 minute session token.  The account table auth_timestamp is set so that
     * we know when the authentication will expire which is after 15 minutes.
     * @param customerId
     * @param tradeItAccountId
     * @return
     * @throws TradeItAccountNotFoundException
     * @throws TradeItAuthenticationException
     */
    public AuthenticateDTO authenticate( final int customerId, final int tradeItAccountId )
        throws TradeItAccountNotFoundException,
               TradeItAuthenticationException
    {
        final String methodName = "authenticate";
        logMethodBegin( methodName, customerId, tradeItAccountId );
        TradeItAccountEntity tradeItAccountEntity = this.tradeItAccountEntityService
                                                        .getTradeItAccountEntity( customerId, tradeItAccountId );
        logDebug( methodName, "Evaluating account entity: {0}", tradeItAccountEntity );
        /*
         * Need to generate a UUID for the authentication process
         */
        if ( tradeItAccountEntity.getAuthUuid() == null )
        {
            String authUUID = UUID.randomUUID().toString();
            tradeItAccountEntity.setAuthUuid( authUUID );
            logDebug( methodName, "Setting UUID {0}", authUUID );
            tradeItAccountEntity = this.tradeItAccountEntityService
                                       .saveAccount( tradeItAccountEntity );
        }
        /*
         * Create the method parameter map
         */
        final TradeItAPICallParameters parameters = TradeItAPICallParameters.newMap()
                  .addParameter( TradeItParameter.AUTH_UUID, tradeItAccountEntity.getAuthUuid() )
                  .addParameter( TradeItParameter.USER_ID_PARAM, tradeItAccountEntity.getUserId() )
                  .addParameter( TradeItParameter.USER_TOKEN_PARAM, tradeItAccountEntity.getUserToken() )
                  .addParameter( TradeItParameter.BROKER_PARAM, tradeItAccountEntity.getBrokerage() );
        /*
         * Make the auth call
         */
        logDebug( methodName, "Calling TradeIt to authenticate account: {0}", tradeItAccountEntity );
        final AuthenticateAPICall authenticateAPICall = this.context.getBean( AuthenticateAPICall.class );
        final AuthenticateAPIResult authenticateAPIResult = this.callTradeIt( tradeItAccountEntity,
                                                                              authenticateAPICall,
                                                                              parameters );

        /*
         * Create the result DTO and extract the results from the authenticate call.
         */
        final AuthenticateDTO authenticateDTO = (AuthenticateDTO)this.context.getBean( "authenticateDTO" );
        authenticateDTO.setResults( authenticateAPIResult );
        logDebug( methodName, "Authenticate result: {0}", authenticateDTO );
        logDebug( methodName, "AuthToken result: {0}", authenticateDTO.getToken() );
        /*
         * Save the token as it is needed after the user answers the necessary security questions.
         */
        Objects.requireNonNull( authenticateAPIResult.getToken(), "The token cannot be null from the auth result" );
        tradeItAccountEntity.setAuthToken( authenticateAPIResult.getToken() );
        /*
         * Check the auth results
         */
        if ( authenticateAPIResult.isInformationNeeded() )
        {
            logDebug( methodName, "INFORMATION_NEEDED - Need to prompt security question." );
            this.tradeItAccountEntityService
                .saveAccount( tradeItAccountEntity );
        }
        else if ( authenticateDTO.isSuccessful() )
        {
            logDebug( methodName, "Authentication was successful" );
            /*
             * The timestamp is set and the UUID authentication.
             */
            tradeItAccountEntity.setAuthTimestamp( new Timestamp( System.currentTimeMillis() ) );
            this.tradeItAccountEntityService
                .saveAccount( tradeItAccountEntity );
        }
        logDebug( methodName, "authenticateAPIResult: {0}", authenticateAPIResult );
        logMethodEnd( methodName, authenticateDTO );
        return authenticateDTO;
    }

    /**
     * This method is called to send back the user's answer to the security question.
     * @param customerId
     * @param accountId
     * @param questionResponse The user's text response
     * @return
     * @throws TradeItAccountNotFoundException
     * @throws TradeItAuthenticationException
     * @throws LinkedAccountNotFoundException
     * @throws EntityVersionMismatchException
     */
    public AnswerSecurityQuestionDTO answerSecurityQuestion( final int customerId, final int accountId,
                                                             final String questionResponse )
        throws LinkedAccountNotFoundException,
               TradeItAccountNotFoundException,
               TradeItAuthenticationException,
               EntityVersionMismatchException
    {
        final String methodName = "answerSecurityQuestion";
        logMethodBegin( methodName, customerId, accountId, questionResponse );
        final TradeItAccountEntity tradeItAccountEntity = this.tradeItAccountEntityService
                                                              .getTradeItAccountEntity( customerId, accountId );
        final AnswerSecurityQuestionAPICall answerSecurityQuestionAPICall = this.context.getBean( AnswerSecurityQuestionAPICall.class );
        /*
         * Create the execute parameter map
         */
        final TradeItAPICallParameters parameters = TradeItAPICallParameters.newMap()
                    .addParameter( TradeItParameter.AUTH_UUID,
                                   tradeItAccountEntity.getAuthUuid() )
                    .addParameter( TradeItParameter.TOKEN_PARAM,
                                   tradeItAccountEntity.getAuthToken() )
                    .addParameter( TradeItParameter.SECURITY_ANSWER_PARAM,
                                   questionResponse );
        /*
         * Make the TradeIt API call
         */
        final AnswerSecurityQuestionAPIResult answerSecurityQuestionAPIResult = this.callTradeIt( tradeItAccountEntity,
                                                                                                  answerSecurityQuestionAPICall,
                                                                                                  parameters );
        /*
         * Create the return DTO, extract the results, and evaluate the result status.
         */
        final AnswerSecurityQuestionDTO answerSecurityQuestionDTO = this.context.getBean( AnswerSecurityQuestionDTO.class );
        answerSecurityQuestionDTO.setResults( answerSecurityQuestionAPIResult );
        if ( answerSecurityQuestionAPIResult.isSuccessful() )
        {
            /*
             * If successful, the list of accounts linked to the tradeit account are returned.
             * The accounts are compared against the database linked accounts and the database is synchronized
             * to the results returned by TradeIt.
             */
            this.tradeItAccountEntityService
                .synchronizeTradeItAccount( tradeItAccountEntity, answerSecurityQuestionDTO );
        }
        logDebug( methodName, "authenticateAPISuccessResult: {0}", answerSecurityQuestionAPIResult );
        logMethodEnd( methodName, answerSecurityQuestionDTO );
        return answerSecurityQuestionDTO;
    }

    /**
     * Calls TradeIt to keep the session alive for the customer account.
     * @param customerId
     * @param tradeItAccountId
     * @return
     * @throws TradeItAccountNotFoundException
     */
    public KeepSessionAliveDTO keepSessionAlive( final int customerId,
                                                 final int tradeItAccountId )
        throws TradeItAccountNotFoundException,
               LinkedAccountNotFoundException, TradeItAuthenticationException
    {
        final String methodName = "keepSessionAlive";
        logMethodBegin( methodName, customerId, tradeItAccountId );
        final TradeItAccountEntity tradeItAccountEntity = this.tradeItAccountEntityService
                                                              .getTradeItAccountEntity( customerId, tradeItAccountId );
        final KeepSessionAliveAPICall keepSessionAliveAPICall = this.context.getBean( KeepSessionAliveAPICall.class );
        /*
         * Create the parameter map
         */
        final TradeItAPICallParameters parameters = TradeItAPICallParameters.newMap()
                                                                              .addParameter( TradeItParameter.TOKEN_PARAM, tradeItAccountEntity.getAuthToken() );
        /*
         * Call TradeIt to keep the session alive.
         */
        final KeepSessionAliveAPIResult keepSessionAliveAPIResult = this.callTradeIt( tradeItAccountEntity,
                                                                                      keepSessionAliveAPICall,
                                                                                      parameters );
        /*
         * Evaluate the results and create the return DTO.
         */
        KeepSessionAliveDTO keepSessionAliveDTO = this.context.getBean( KeepSessionAliveDTO.class );
        keepSessionAliveDTO.setResults( keepSessionAliveAPIResult );
        if ( keepSessionAliveAPIResult.isSuccessful() )
        {
            logDebug( methodName, "Keep alive successful" );
            this.tradeItAccountEntityService
                .keepSessionAliveSuccess( keepSessionAliveDTO, tradeItAccountEntity, keepSessionAliveAPIResult );
        }
        else if ( keepSessionAliveAPIResult.isAuthenticationRequired() )
        {
            logDebug( methodName, "Authentication required, calling authenticate" );
            final AuthenticateDTO authenticateDTO = this.authenticate( customerId, tradeItAccountId );
            keepSessionAliveDTO = new KeepSessionAliveDTO( authenticateDTO );
        }
        logMethodEnd( methodName, keepSessionAliveDTO );
        return keepSessionAliveDTO;
    }

    /**
     * Calls TradeIt to close the sesssion for the customer account.
     * @param customerId
     * @param accountId
     * @return
     * @throws TradeItAccountNotFoundException
     */
    public CloseSessionDTO closeSession( final int customerId, final int accountId )
        throws TradeItAccountNotFoundException
    {
        final String methodName = "closeSessionDTO";
        logMethodBegin( methodName, customerId, accountId  );
        final TradeItAccountEntity tradeItAccountEntity = this.tradeItAccountEntityService
                                                              .getTradeItAccountEntity( customerId, accountId );
        /*
         * Create the api call and the return DTO.
         */
        final CloseSessionAPICall closeSessionAPICall = this.context.getBean( CloseSessionAPICall.class );
        final CloseSessionDTO closeSessionDTO = this.context.getBean( CloseSessionDTO.class );
        final CloseSessionAPIResult closeSessionAPIResult;
        try
        {
            /*
             * Create the api call parameters
             */
            final TradeItAPICallParameters parameters = TradeItAPICallParameters.newMap()
                                                                                  .addParameter( TradeItParameter.TOKEN_PARAM, tradeItAccountEntity.getAuthToken() );
            /*
             * Call the TradeIt API
             */
            closeSessionAPIResult = this.callTradeIt( tradeItAccountEntity, closeSessionAPICall, parameters );
            /*
             * Null out the auth timestamp so that we can identify that this account is not longer authenticated.
             */
            tradeItAccountEntity.setAuthTimestamp( null );
            this.tradeItAccountEntityService
                .saveAccount( tradeItAccountEntity );
            /*
             * Save the results.
             */
            closeSessionDTO.setResults( closeSessionAPIResult );
        }
        catch( TradeItAuthenticationException e )
        {
            /*
             * Don't care if this fails.
             */
            logError( methodName, e );
        }
        logMethodEnd( methodName, closeSessionDTO );
        return closeSessionDTO;
    }

    /**
     * Cycles through all of the linked accounts in {@code tradeItAccountEntity} and calls TradeIt to get the account
     * overview information and the positions.
     * @param tradeItAccountEntity
     */
    private void getAccountOverviewAndPositions( final TradeItAccountEntity tradeItAccountEntity )
    {
        /*
        final String methodName = "getAccountOverviewAndPositions";
        logMethodBegin( methodName, tradeItAccountEntity );
        tradeItAccountEntity.getLinkedAccountsById()
                            .forEach( linkedAccount -> this.getAccountOverviewAndPositions( linkedAccount.getAccountNumber(),
                                                                                            tradeItAccountEntity.getAuthToken() ));
        logMethodEnd( methodName );
        */
    }

    /**
     * Gets the account overview and positions for the {@code linkedAccount}.
     * @param accountNumber
     * @param authToken
     */
    public void getAccountOverviewAndPositions( final String accountNumber,
                                                final String authToken )
    {
        /*
        final String methodName = "getAccountOverviewAndPositions";
        logMethodBegin( methodName, accountNumber, authToken );
        GetAccountOverviewDTO getAccountOverviewDTO = this.getAccountAccountOverview( accountNumber, authToken );
        linkedAccount.setAccountOverviewValues( getAccountOverviewDTO );
        GetPositionsDTO getPositionsDTO = this.getPositions( tradeItAccountEntity.getCustomerId(),
                                                             tradeItAccountEntity.getId(),
                                                             linkedAccount.getAccountNumber() );
                                                             */
    }

    /**
     * Get the the stock positions for the account.
     * @param tradeItAccountEntity
     * @param linkedAccountEntity
     * @return
     * @throws TradeItAuthenticationException
     */
    public GetPositionsAPIResult getPositions( final TradeItAccountEntity tradeItAccountEntity,
                                               final LinkedAccountEntity linkedAccountEntity )
        throws TradeItAuthenticationException
    {
        final String methodName = "getPositions";
        logMethodBegin( methodName, tradeItAccountEntity.getId(), linkedAccountEntity.getId() );
        final GetPositionsAPICall getPositionsAPICall = this.context.getBean( GetPositionsAPICall.class );
        /*
         * Create the get positions parameters.
         */
        final TradeItAPICallParameters parameters = TradeItAPICallParameters.newMap()
                                                                              .addParameter( TradeItParameter.TOKEN_PARAM, tradeItAccountEntity.getAuthToken() )
                                                                              .addParameter( TradeItParameter.ACCOUNT_NUMBER_PARAM, linkedAccountEntity.getAccountNumber() );
        /*
         * Make the get positions API call.
         */
        final GetPositionsAPIResult getPositionsAPIResult = this.callTradeIt( tradeItAccountEntity,
                                                                              getPositionsAPICall,
                                                                              parameters );
        logMethodEnd( methodName, getPositionsAPIResult );
        return getPositionsAPIResult;
    }

    /**
     * This is called after receiving a {@code TradeItAuthenticationException}.  It will attempt to perform an
     * authentication call for the account identified by {@code accountId}
     * @param customerId
     * @param tradeItAccountId
     * @param e
     * @throws LinkedAccountNotFoundException
     * @throws TradeItAccountNotFoundException
     * @throws TradeItAuthenticationException
     */
    private void authenticateOnException( final int customerId,
                                          final int tradeItAccountId,
                                          final TradeItAuthenticationException e )
        throws LinkedAccountNotFoundException,
               TradeItAccountNotFoundException,
               TradeItAuthenticationException
    {
        final String methodName = "authenticateOnException";
        logMethodBegin( methodName, customerId, tradeItAccountId );
        /*
         * Need to re-authenticate the account
         */
        final AuthenticateDTO authenticateDTO = this.authenticate( customerId, tradeItAccountId );
        if ( authenticateDTO.isSuccessful() )
        {
            logDebug( methodName, "Authentication successful" );
        }
        else
        {
            logError( methodName, "Authentication unsuccessful, throwing exception", e );
            throw e;
        }
        logMethodEnd( methodName );
    }

    /**
     * Get the account overview from TradeIt for the account number.  This is a sub account of the account identified
     * by {@code accountId}.
     * @param customerId
     * @param tradeItAccountId
     * @param accountNumber The brokerage account number.
     * @param authToken The authorization token.,
     * @return
     */
    public GetAccountOverviewDTO getAccountOverview( final int customerId,
                                                     final int tradeItAccountId,
                                                     final String accountNumber,
                                                     final String authToken )
        throws TradeItAccountNotFoundException,
               TradeItAuthenticationException
    {
        final String methodName = "getAccountOverview";
        logMethodBegin( methodName, customerId, tradeItAccountId, accountNumber, authToken  );
        Objects.requireNonNull( accountNumber, "accountNumber cannot be null" );
        Objects.requireNonNull( accountNumber, "authToken cannot be null" );
        final TradeItAccountEntity tradeItAccountEntity = this.tradeItAccountEntityService
                                                              .getTradeItAccountEntity( customerId, tradeItAccountId );
        final GetAccountOverviewAPICall getAccountOverviewAPICall = this.context.getBean( GetAccountOverviewAPICall.class );
        /*
         * Create the parameter map for the get account overview fcall.
         */
        final TradeItAPICallParameters parameters = TradeItAPICallParameters.newMap()
                                                                              .addParameter( TradeItParameter.TOKEN_PARAM, authToken )
                                                                              .addParameter( TradeItParameter.ACCOUNT_NUMBER_PARAM, accountNumber );
        /*
         * Make the account overview API call to TradeIT
         */
        GetAccountOverViewAPIResult getAccountOverviewAPIResult = this.callTradeIt( tradeItAccountEntity,
                                                                                    getAccountOverviewAPICall,
                                                                                    parameters );
        /*
         * Create the return DTO.
         */
        final GetAccountOverviewDTO getAccountOverviewDTO = this.context.getBean( GetAccountOverviewDTO.class );
        getAccountOverviewDTO.setResults( getAccountOverviewAPIResult );
        logMethodEnd( methodName, getAccountOverviewDTO );
        return getAccountOverviewDTO;
    }

    /**
     * This is a wrapper method to execute an TradeIt API call. It provides common error evaluation and handling
     * for most TradeIt calls.
     * @param tradeItAPIRestCall
     * @param <T>
     * @return The result of the TradeIt API call.
     * @throws TradeItAuthenticationException
     */
    private <T extends TradeItAPIResult> T callTradeIt( final TradeItAccountEntity tradeItAccountEntity,
                                                        final TradeItAPIRestCall<T> tradeItAPIRestCall,
                                                        final TradeItAPICallParameters tradeItAPICallParameterMap )
        throws TradeItAuthenticationException
    {
        final String methodName = "callTradeIt";
        logMethodBegin( methodName, tradeItAccountEntity.getId(),
                                    tradeItAPIRestCall.getClass().getName(),
                                    tradeItAPICallParameterMap );
        T tradeItAPIResult = tradeItAPIRestCall.execute( tradeItAPICallParameterMap );
        logDebug( methodName, "tradeItAPIResult: {0}", tradeItAPIResult );
        if ( !tradeItAPIResult.isSuccessful() )
        {
            switch ( tradeItAPIResult.getTradeItCodeEnum() )
            {
                /*
                 * When the session is expired, the account will be re-authenticated and if successful, the TradeItCall
                 * will be made again.
                 */
                case SESSION_EXPIRED_ERROR:
                    this.handleSessionExpired( tradeItAccountEntity );
                    final AuthenticateAPIResult authenticateAPIResult = (AuthenticateAPIResult)tradeItAPIRestCall.execute( tradeItAPICallParameterMap );
                    logDebug( methodName, "authentication result: {0}", authenticateAPIResult );
                    /*
                     * Bail out with exception if we need the user to answer the security questions.
                     */
                    if ( tradeItAPIResult.isInformationNeeded() )
                    {
                        throw new TradeItAuthenticationException( authenticateAPIResult );
                    }
                    tradeItAPIResult = (T)authenticateAPIResult;
                    break;
            }
        }
        logMethodEnd( methodName, tradeItAPIResult );
        return tradeItAPIResult;
    }

    /**
     * This method is called when a TradeIt API call returns a session expired error code.
     * This method will make a call to re-authenticate the account. If the call does not result in a SUCCESS, like
     * INFORMATION_NEEDED is returned this this method will throw TradeItAuthenticationException to indicate that the
     * simple authentication call did not succeed as was expected.
     * @param tradeItAccountEntity The auth token is needed to authenticate and on successful authentication the
     *                             auth token and auth timestamp will be persisted.
     * @throws
     */
    private void handleSessionExpired( final TradeItAccountEntity tradeItAccountEntity )
        throws TradeItAuthenticationException
    {
        final String methodName = "handleSessionExpired";
        logMethodBegin( methodName, tradeItAccountEntity );
        final AuthenticateAPICall authenticateAPICall = this.context.getBean( AuthenticateAPICall.class );
        final TradeItAPICallParameters parameters = TradeItAPICallParameters.newMap()
            .addParameter( TradeItParameter.USER_ID_PARAM, tradeItAccountEntity.getUserId() )
            .addParameter( TradeItParameter.USER_TOKEN_PARAM, tradeItAccountEntity.getUserToken() )
            .addParameter( TradeItParameter.BROKER_PARAM, tradeItAccountEntity.getBrokerage() )
            .addParameter( TradeItParameter.AUTH_UUID, tradeItAccountEntity.getAuthToken() );
        final AuthenticateAPIResult authenticateAPIResult = authenticateAPICall.execute( parameters );
        logDebug( methodName, "Authentication result {0}", authenticateAPIResult );
        if ( authenticateAPIResult.isSuccessful() )
        {
            logDebug( methodName, "Authentication was successful" );
            tradeItAccountEntity.setAuthToken( authenticateAPIResult.getToken() );
            tradeItAccountEntity.setAuthTimestamp( new Timestamp( System.currentTimeMillis() ) );
            this.tradeItAccountEntityService
                .saveAccount( tradeItAccountEntity );
        }
        else
        {
            logDebug( methodName, "Authentication failed, throwing exception" );
            throw new TradeItAuthenticationException( authenticateAPIResult );
        }
        logMethodEnd( methodName );
    }

    @Autowired
    public void setTradeItAccountEntityService( final TradeItAccountEntityService tradeItAccountEntityService )
    {
        logInfo( "setAccountService", "Dependency Injection of " + tradeItAccountEntityService );
        this.tradeItAccountEntityService = tradeItAccountEntityService;
    }
}