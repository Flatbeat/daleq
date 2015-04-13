/*
 * Copyright 2012 brands4friends, Private Sale GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.brands4friends.daleq.core.internal.types;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.brands4friends.daleq.core.DaleqBuildException;
import de.brands4friends.daleq.core.FieldType;
import de.brands4friends.daleq.core.FieldTypeReference;
import de.brands4friends.daleq.core.TableType;
import de.brands4friends.daleq.core.TableTypeReference;

@ThreadSafe
public class CachingTableTypeRepository implements TableTypeRepository {

    private final Cache<TableTypeReference, TableType> cache;
    private final List<TableTypeResolver> resolvers;

    public CachingTableTypeRepository() {
        resolvers = new CopyOnWriteArrayList<TableTypeResolver>(Lists.newArrayList(
                new ClassBasedTableTypeResolver()
        ));
        cache = CacheBuilder.newBuilder().build(cacheLoader());
    }

    @Override
    public TableType get(final TableTypeReference tableRef) {
        try {
            TableType tableType =  cache.get(tableRef);
            if(tableType instanceof ExceptionTableType) {
                throw new DaleqBuildException("No TableTypeResolver registered for " + tableRef);
            }
            return tableType;
        } catch (ExecutionException e) {
            throw new DaleqBuildException("No TableTypeResolver registered for " + tableRef);
        }
    }

    private CacheLoader<TableTypeReference, TableType> cacheLoader() {
        return new CacheLoader<TableTypeReference, TableType>() {

            @Override
            public TableType load(final TableTypeReference tableRef) throws Exception {
                try {
                    TableTypeResolver resolver = findTableTypeResolver(tableRef);
                    return resolver.resolve(tableRef);
                } catch (NoSuchElementException ex) {
                    return new ExceptionTableType();
                }
            }

            private TableTypeResolver findTableTypeResolver(final TableTypeReference tableRef) {
                return Iterables.find(resolvers, new Predicate<TableTypeResolver>() {
                    @Override
                    public boolean apply(@Nullable final TableTypeResolver resolver) {
                        if (resolver == null) {
                            return false;
                        }
                        return resolver.canResolve(tableRef);
                    }
                });
            }
        };
    }

    private class ExceptionTableType implements TableType {

        @Override
        public String getName() {
            throw new DaleqBuildException("No TableTypeResolver registered.");
        }

        @Override
        public List<FieldType> getFields() {
            throw new DaleqBuildException("No TableTypeResolver registered.");
        }

        @Override
        public FieldType findFieldBy(FieldTypeReference fieldRef) {
            throw new DaleqBuildException("No TableTypeResolver registered.");
        }

    }

}
