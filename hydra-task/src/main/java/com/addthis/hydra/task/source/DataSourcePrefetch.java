/*
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
package com.addthis.hydra.task.source;

import java.util.LinkedList;

import com.addthis.bundle.core.Bundle;
import com.addthis.codec.Codec;
import com.addthis.hydra.task.run.TaskRunConfig;

/**
 * This {@link TaskDataSource source} <span class="hydra-summary">prefetches bundles from an underlying data source</span>.
 *
 * @user-reference
 * @hydra-name prefetch
 */
public final class DataSourcePrefetch extends TaskDataSource {

    /**
     * Number of bundles to prefetch.
     */
    @Codec.Set(codable = true)
    protected int size;

    /**
     * Underlying data source. This field is required.
     */
    @Codec.Set(codable = true, required = true)
    protected TaskDataSource source;

    protected final LinkedList<Bundle> prefetch = new LinkedList<Bundle>();


    public DataSourcePrefetch setup(TaskDataSource source, int size) {
        this.source = source;
        this.size = size;
        return this;
    }

    @Override
    public void open(TaskRunConfig config) {
        source.open(config);
    }

    @Override
    public void close() {
        source.close();
    }

    private boolean prefetch() {
        Bundle next = source.next();
        while (prefetch.size() < size && (next = source.next()) != null) {
            prefetch.add(next);
        }
        return prefetch.size() > 0;
    }

    @Override
    public synchronized Bundle next() {
        return prefetch() ? prefetch.removeFirst() : null;
    }

    @Override
    public synchronized Bundle peek() {
        return prefetch() ? prefetch.getFirst() : null;
    }
}
