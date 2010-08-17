/* Copyright (C) 2010 SpringSource
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.datastore.engine;

import org.springframework.datastore.mapping.MappingContext;
import org.springframework.datastore.mapping.PersistentEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A Persister specified to PersistentEntity instances
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public abstract class EntityPersister implements Persister, EntityInterceptorAware {
    private PersistentEntity persistentEntity;
    private MappingContext mappingContext;
    protected List<EntityInterceptor> interceptors = new ArrayList<EntityInterceptor>();

    public EntityPersister(MappingContext mappingContext, PersistentEntity entity) {
        this.persistentEntity = entity;
        this.mappingContext = mappingContext;
    }

    public void addEntityInterceptor(EntityInterceptor interceptor) {
        if(interceptor != null) {
            this.interceptors.add(interceptor);
        }
    }

    public void setEntityInterceptors(List<EntityInterceptor> interceptors) {
        if(interceptors!=null) this.interceptors = interceptors;
    }

    /**
     * @return The MappingContext instance
     */
    public MappingContext getMappingContext() {
        return mappingContext;
    }

    /**
     * @return The PersistentEntity instance
     */
    public PersistentEntity getPersistentEntity() {
        return persistentEntity;
    }

    public Class getType() {
        return persistentEntity.getJavaClass();
    }

    /**
     * Obtains an objects identifer
     * @param obj The object
     * @return The identifier or null if it doesn't have one
     */
    public Serializable getObjectIdentifier(Object obj) {
        return (Serializable) new EntityAccess(getPersistentEntity(), obj).getIdentifier();
    }
    /**
     * Persists an object returning the identifier
     *
     * @param obj The object to persist
     * @return The identifer
     */
    public final Serializable persist(Object obj) {
        if(!persistentEntity.isInstance(obj)) throw new IllegalArgumentException("Object ["+obj+"] is not an instance supported by the persister for class ["+getType().getName()+"]");

        return persistEntity(getPersistentEntity(), new EntityAccess(getPersistentEntity(), obj));
    }

    public List<Serializable> persist(Iterable objs) {
        return persistEntities(getPersistentEntity(), objs);
    }

    public List<Object> retrieveAll(Iterable<Serializable> keys) {
        return retrieveAllEntities(getPersistentEntity(), keys);
    }

    protected abstract List<Object> retrieveAllEntities(PersistentEntity persistentEntity, Iterable<Serializable> keys);

    protected abstract List<Serializable> persistEntities(PersistentEntity persistentEntity, Iterable objs);

    public final Object retrieve(Serializable key) {
        if(key == null) return null;
        return retrieveEntity(getPersistentEntity(), key);
    }

    /**
     * Retrieve a PersistentEntity for the given mappingContext and key
     *
     * @param persistentEntity The entity
     * @param key The key
     * @return The object or null if it doesn't exist
     */
    protected abstract Object retrieveEntity(PersistentEntity persistentEntity, Serializable key);

    /**
     * Persist the given persistent entity
     *
     * @param persistentEntity The PersistentEntity
     * @param entityAccess An object that allows easy access to the entities properties
     * @return The generated key
     */
    protected abstract Serializable persistEntity(PersistentEntity persistentEntity, EntityAccess entityAccess);

    public final void delete(Iterable objects) {
        if(objects != null) {
            deleteEntities(getPersistentEntity(), objects);
        }
    }

    public void delete(Object obj) {
        if(obj != null) {
            deleteEntity(getPersistentEntity(), obj);
        }
    }

    protected abstract void deleteEntity(PersistentEntity persistentEntity, Object obj);

    protected abstract void deleteEntities(PersistentEntity persistentEntity, Iterable objects);
}

