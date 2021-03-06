package com.stocktracker.servicelayer.service;

import com.stocktracker.common.exceptions.DuplicateEntityException;
import com.stocktracker.common.exceptions.EntityVersionMismatchException;
import com.stocktracker.common.exceptions.VersionedEntityNotFoundException;
import com.stocktracker.repositorylayer.common.VersionedEntity;
import com.stocktracker.weblayer.dto.common.VersionedDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Base Service class for managing database entities.
 * @param <EK> The entity key type.
 * @param <E> The database entity type.
 * @param <DK> The DTO key.
 * @param <D> The DTO type.
 * @param <R> The entity repository type.
 */
public abstract class VersionedEntityService<EK extends Serializable,
                                             E extends VersionedEntity<EK>,
                                             DK extends Serializable,
                                             D extends VersionedDTO<DK>,
                                             R extends JpaRepository<E, EK>>
    extends BaseEntityService<EK,E,DK,D,R>
{
    protected StockTagService stockTagService;

    /**
     * Converts the DTO to and Entity and saves it to the database.
     * @param dto
     * @return Entity that was saved to the database.
     * @throws EntityVersionMismatchException
     * @throws DuplicateEntityException
     */
    public D saveDTO( final D dto  )
        throws EntityVersionMismatchException,
               DuplicateEntityException
    {
        final String methodName = "saveDTO";
        logMethodBegin( methodName, dto );
        Objects.requireNonNull( dto, "dto cannot be null" );

        this.preSaveDTO( dto );
        final E entity = this.dtoToEntity( dto );
        final E savedEntity = this.saveEntity( entity );
        final D returnDTO = this.entityToDTO( savedEntity );
        this.postSaveDTO( returnDTO );
        logMethodEnd( methodName, returnDTO );
        return returnDTO;
    }

    /**
     * This method is called by {@code saveDTO} before converting the DTO to an entity.
     * Subclasses can put custom logic in this method that need to be including in the DTO before being converted to
     * and Entity.
     * @param dto
     */
    protected void preSaveDTO( final D dto )
    {
    }

    /**
     * This method is called after a successful update of an Entity and after the entity been converted into a DTO.
     * This method is called before returning the added DTO to the caller.  Subclasses can override this method to
     * made any changes to the DTO before it is sent back to the client.
     * @param dto
     */
    protected void postSaveDTO( final D dto )
    {
    }

    /**
     * Add a list of DTOs
     * @param dtos
     * @throws DuplicateEntityException
     */
    public List<D> addDTOs( final List <D> dtos )
        throws DuplicateEntityException
    {
        final String methodName = "addDTO";
        logMethodBegin( methodName, dtos );
        Objects.requireNonNull( dtos, "dtos cannot be null" );
        final List<D> returnDTOs = new ArrayList<>();
        for ( D dto : dtos )
        {
            final D newDTO = addDTO( dto );
            returnDTOs.add( newDTO );
        }
        logMethodEnd( methodName );
        return returnDTOs;
    }

    /**
     * Adds the DTO to the database.
     * Performs the following:
     * 1. Converts {@code dto} to an entity.
     * 2. Adds the entity to the database.
     * 3. Converts the entity added to the database back into a DTO
     * 4. Returns the DTO.
     * @param dto
     * @return DTO
     * @throws EntityVersionMismatchException
     * @throws DuplicateEntityException
     * @throws VersionedEntityNotFoundException
     */
    public D addDTO( final D dto )
        throws EntityVersionMismatchException,
               DuplicateEntityException,
               VersionedEntityNotFoundException
    {
        final String methodName = "addDTO";
        logMethodBegin( methodName, dto );
        Objects.requireNonNull( dto, "dto cannot be null" );
        this.preAddDTO( dto );
        final E entity = this.dtoToEntity( dto );
        final E returnEntity = this.addEntity( entity );
        final D returnDTO = this.entityToDTO( returnEntity );
        this.postAddDTO( returnDTO );
        logMethodEnd( methodName, returnDTO );
        return returnDTO;
    }

    /**
     * This method is called by {@code addDTO} before converting the DTO to an entity.
     * Subclasses can put custom logic in this method that need to be including in the DTO before being converted to
     * and Entity.
     * @param dto
     */
    protected void preAddDTO( final D dto )
    {
    }

    /**
     * This method is called after a successful update of an Entity and after the entity been converted into a DTO.
     * This method is called before returning the added DTO to the caller.  Subclasses can override this method to
     * made any changes to the DTO before it is sent back to the client.
     * @param dto
     */
    protected void postAddDTO( final D dto )
    {
    }

    /**
     * Get the DTO for the entity matching key.
     * @param key
     * @return
     * @throws VersionedEntityNotFoundException
     */
    public D getDTO( final EK key )
        throws VersionedEntityNotFoundException
    {
        final String methodName = "getDTO";
        logMethodBegin( methodName, key );
        Objects.requireNonNull( key, "key cannot be null" );
        final E entity = getEntity( key );
        if ( entity == null )
        {
            throw new VersionedEntityNotFoundException( key );
        }
        final D dto = this.entityToDTO( entity );
        logMethodEnd( methodName, dto );
        return dto;
    }

    /**
     * Get a single entity.
     * @param entityKey The primary key.
     * @return The entity.
     * @throws VersionedEntityNotFoundException If the entity is not found by EK
     */
    public E getEntity( final EK entityKey )
        throws VersionedEntityNotFoundException
    {
        final String methodName = "getEntity";
        logMethodBegin( methodName, entityKey );
        Objects.requireNonNull( entityKey, "EK cannot be null" );
        final Optional<E> entity = this.getRepository()
                                       .findById( entityKey );
        if ( !entity.isPresent() )
        {
            throw new VersionedEntityNotFoundException( entityKey );
        }
        logMethodEnd( methodName, entity.get() );
        return entity.get();
    }

    /**
     * Deletes a collection of entities.
     * @param deletedEntities
     * @throws VersionedEntityNotFoundException
     */
    public void deleteEntities( final Collection<E> deletedEntities )
        throws VersionedEntityNotFoundException
    {
        final String methodName = "deleteEntities";
        Objects.requireNonNull( deletedEntities, "deletedEntities cannot be null" );
        logMethodBegin( methodName, deletedEntities.size() + " entities" );
        for ( final E entity: deletedEntities )
        {
            logDebug( methodName, "Deleting entity (0}", entity );
            this.deleteEntity( entity.getId() );
        }
        logMethodEnd( methodName );
    }

    /**
     * Delete the entity.
     * @param entityKey
     * @throws VersionedEntityNotFoundException
     */
    public void deleteEntity( final EK entityKey )
        throws VersionedEntityNotFoundException
    {
        final String methodName = "deleteEntity";
        logMethodBegin( methodName, entityKey );
        Objects.requireNonNull( entityKey, "entityKey cannot be null" );
        final E entity = this.getEntity( entityKey );
        this.deleteEntity( entity );
        logMethodEnd( methodName );
    }

    /**
     * Delete the entity.
     * @param entity
     * @throws VersionedEntityNotFoundException
     */
    public void deleteEntity( final E entity )
        throws VersionedEntityNotFoundException
    {
        final String methodName = "deleteEntity";
        logMethodBegin( methodName, entity );
        Objects.requireNonNull( entity, "entity cannot be null" );
        this.getRepository()
            .delete( entity );
        logMethodEnd( methodName );
    }

    /**
     * Inserts or updates the {@code entity} depending on if it exists already or not.
     * @param entity
     * @throws EntityVersionMismatchException
     * @throws DuplicateEntityException
     * @throws VersionedEntityNotFoundException
     */
    public E mergeEntity( final E entity )
        throws EntityVersionMismatchException,
               DuplicateEntityException,
               VersionedEntityNotFoundException
    {
        if ( entity.getId() == null || !this.isExists( entity ) )
        {
            return this.addEntity( entity );
        }
        else
        {
            return this.saveEntity( entity );
        }
    }

    /**
     * Adds all of the entities in {@code entities}
     * @param entities
     * @throws EntityVersionMismatchException
     * @throws DuplicateEntityException
     * @throws VersionedEntityNotFoundException
     */
    public List<E> addEntities( final Collection<E> entities )
        throws EntityVersionMismatchException,
               DuplicateEntityException,
               VersionedEntityNotFoundException
    {
        final String methodName = "addEntities";
        Objects.requireNonNull( entities, "entities cannot be null" );
        logMethodBegin( methodName, "Adding {0} entities", entities.size() );
        final List<E> returnEntities = new ArrayList<>();
        for ( final E entity: entities )
        {
            logDebug( methodName, "Adding entity (0}", entity );
            final E newEntity = this.addEntity( entity );
            returnEntities.add(  newEntity );
        }
        logMethodEnd( methodName );
        return returnEntities;
    }

    /**
     * Add the entity to the database.
     * @param entity
     * @return
     * @throws EntityVersionMismatchException When the entity already exists.
     * @throws DuplicateEntityException
     * @throws VersionedEntityNotFoundException
     */
    public E addEntity( final E entity )
        throws EntityVersionMismatchException,
               DuplicateEntityException,
               VersionedEntityNotFoundException
    {
        final String methodName = "addDTO";
        logMethodBegin( methodName, entity );
        Objects.requireNonNull( entity, "entity cannot be null" );
        if ( entity.getId() != null && isExists( entity ) )
        {
            final E currentEntity = this.getEntity( entity.getId() );
            throw new DuplicateEntityException( currentEntity );
        }
        this.preAddEntity( entity );
        final E returnEntity;
        try
        {
            returnEntity = this.save( entity );
        }
        catch( DataIntegrityViolationException e )
        {
            throw new DuplicateEntityException( entity, e );
        }
        this.postAddEntity( entity );
        logMethodEnd( methodName, returnEntity );
        return returnEntity;
    }

    /**
     * This method is called just before calling the repository to add(insert) the entity.
     * Subclasses can override this method to add custom logic before inserting into the database.
     * @param entity
     */
    protected void preAddEntity( final E entity )
        throws VersionedEntityNotFoundException
    {
    }

    /**
     * This method is called after a successful insert into the database of the entity.
     * @param entity
     */
    protected void postAddEntity( final E entity )
    {
    }

    /**
     * Saves the entity to the database.
     * @param entity
     * @return
     * @throws EntityVersionMismatchException
     * @throws DuplicateEntityException
     */
    public E saveEntity( final E entity )
        throws DuplicateEntityException
    {
        final String methodName = "saveEntity";
        logMethodBegin( methodName, entity );
        Objects.requireNonNull( entity, "entity cannot be null" );
        this.preSaveEntity( entity );
        final E savedEntity = this.save( entity );
        this.postSaveEntity( entity );
        logMethodEnd( methodName, savedEntity );
        return savedEntity;
    }

    /**
     * This method is called just before calling the repository to save(update) the entity.
     * Subclasses can override this method to add custom logic before inserting into the database.
     * @param entity
     */
    protected void preSaveEntity( final E entity )
    {
    }

    /**
     * This method is called after a successful update of the database of the entity.
     * @param entity
     */
    protected void postSaveEntity( final E entity )
    {
    }

    /**
     * Private method used by add and update methods to update the database.
     * @param entity
     * @return
     * @throws DuplicateEntityException
     */
    @Retryable
    (
        value = {CannotAcquireLockException.class},
        maxAttempts = 3,
        backoff = @Backoff( delay = 1000)
    )
    private E save( final E entity )
        throws DuplicateEntityException
    {
        final String methodName = "save";
        logMethodBegin( methodName, entity );
        final E savedEntity;
        try
        {
            savedEntity = this.getRepository()
                              .save( entity );
        }
        catch( javax.persistence.OptimisticLockException |
               org.springframework.orm.ObjectOptimisticLockingFailureException e )
        {
            E currentEntity = this.getEntity( entity.getId() );
            throw new EntityVersionMismatchException( currentEntity, e );
        }
        catch( org.springframework.dao.DataIntegrityViolationException e )
        {
            throw new DuplicateEntityException( entity, e );
        }
        logMethodEnd( methodName, savedEntity );
        return savedEntity;
    }

    /**
     * Determined if the entity exists.
     * @param entity
     * @return
     */
    public boolean isExists( final E entity )
    {
        return this.isExists( entity.getId() );
    }

    /**
     * Determines if the key exists.
     * @param key
     * @return
     */
    public boolean isExists( final EK key )
    {
        return this.getRepository()
                   .findById( key )
                   .isPresent();
    }


    @Autowired
    public void setStockTagService( final StockTagService stockTagService )
    {
        this.stockTagService = stockTagService;
    }

}
