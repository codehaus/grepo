/*
 * Copyright 2011 Grepo Committers.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.grepo.query.jpa;

import static com.mysema.query.types.PathMetadataFactory.forVariable;

import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.BeanPath;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

/**
 * Simple query dsl test entity.
 *
 * @author dguggi
 */
public class QTestEntity extends EntityPathBase<TestEntity> {

    private static final long serialVersionUID = 8426278567133534479L;

    public static final QTestEntity testEntity = new QTestEntity("testEntity");

    // Checkstyle:OFF
    public final StringPath username = createString("username");
    public final NumberPath<Long> id = createNumber("id", Long.class);
    public final NumberPath<Integer> type = createNumber("type", Integer.class);
    public final StringPath firstname = createString("firstname");

    // Checkstyle:ON

    public QTestEntity(String variable) {
        super(TestEntity.class, forVariable(variable));
    }

    public QTestEntity(BeanPath<? extends TestEntity> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QTestEntity(PathMetadata<?> metadata) {
        super(TestEntity.class, metadata);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }


}
