/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.metamodel.mongodb;

import org.apache.metamodel.data.AbstractDataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.Column;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;

final class MongoDbDataSet extends AbstractDataSet {

    private static final Logger logger = LoggerFactory.getLogger(MongoDbDataSet.class);

    private final MongoCursor<Document> _iterator;
    private final boolean _queryPostProcessed;

    private boolean _closed;
    private volatile Document _document;

    public MongoDbDataSet(FindIterable<Document> resultSet, Column[] columns, boolean queryPostProcessed) {
        super(columns);
        _iterator = resultSet.iterator();
        _queryPostProcessed = queryPostProcessed;
        _closed = false;
    }

    public boolean isQueryPostProcessed() {
        return _queryPostProcessed;
    }

    @Override
    public void close() {
        super.close();
        _closed = true;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (!_closed) {
            logger.warn("finalize() invoked, but DataSet is not closed. Invoking close() on {}", this);
            close();
        }
    }

    @Override
    public boolean next() {
    	if (_iterator.hasNext()){
    		_document = _iterator.next();
    		return true;	
    	}else {
    		_document = null;
    		return false;
    	}
    	
    }

    @Override
    public Row getRow() {
        return MongoDBUtils.toRow(_document, getHeader());
    }

}
