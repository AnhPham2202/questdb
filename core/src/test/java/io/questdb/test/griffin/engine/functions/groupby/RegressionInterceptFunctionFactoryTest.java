/*******************************************************************************
 *     ___                  _   ____  ____
 *    / _ \ _   _  ___  ___| |_|  _ \| __ )
 *   | | | | | | |/ _ \/ __| __| | | |  _ \
 *   | |_| | |_| |  __/\__ \ |_| |_| | |_) |
 *    \__\_\\__,_|\___||___/\__|____/|____/
 *
 *  Copyright (c) 2014-2019 Appsicle
 *  Copyright (c) 2019-2024 QuestDB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/

package io.questdb.test.griffin.engine.functions.groupby;

import io.questdb.test.AbstractCairoTest;
import org.junit.Test;

public class RegressionInterceptFunctionFactoryTest extends AbstractCairoTest {

    @Test
    public void testRegrInterceptAllNull() throws Exception {
        assertMemoryLeak(() -> assertSql(
                "regr_intercept\nnull\n", "select regr_intercept(y, x) from (select cast(null as double) x, cast(null as double) y from long_sequence(100))"
        ));
    }

    @Test
    public void testRegrInterceptNoValues() throws Exception {
        assertMemoryLeak(() -> {
            ddl("create table tbl1(x int, y int)");
            assertSql(
                    "regr_intercept\nnull\n", "select regr_intercept(x, y) from tbl1"
            );
        });
    }

    @Test
    public void testRegrInterceptAllSameValues() throws Exception {
        assertMemoryLeak(() -> {
            ddl("create table tbl1 as (select 17.2151921 x, 17.2151921 y from long_sequence(100))");
            assertSql(
                    "regr_intercept\nnull\n", "select regr_intercept(x, y) from tbl1"
            );
        });
    }

    @Test
    public void testRegrInterceptDoubleValues() throws Exception {
        assertMemoryLeak(() -> {
            ddl("create table tbl1 as (select cast(x as double) x, cast(x as double) y from long_sequence(100))");
            assertSql(
                    "regr_intercept\n0.0\n", "select regr_intercept(x, y) from tbl1"
            );
        });
    }

    @Test
    public void testRegrInterceptWithNullValues() throws Exception {
        assertMemoryLeak(() -> {
            ddl("create table tbl1(x double, y double)");
            insert("insert into 'tbl1' VALUES (null, null)");
            insert("insert into 'tbl1' select x, x as y from long_sequence(100)");
            assertSql(
                    "regr_intercept\n0.0\n", "select regr_intercept(x, y) from tbl1"
            );
        });
    }

    @Test
    public void testRegrInterceptFloatValues() throws Exception {
        assertMemoryLeak(() -> {
            ddl("create table tbl1 as (select cast(x as float) x, cast(x as float) y from long_sequence(100))");
            assertSql(
                    "regr_intercept\n0.0\n", "select regr_intercept(x, y) from tbl1"
            );
        });
    }

    @Test
    public void testRegrInterceptIntValues() throws Exception {
        assertMemoryLeak(() -> {
            ddl("create table tbl1 as (select cast(x as int) x, cast(x as int) y from long_sequence(100))");
            assertSql(
                    "regr_intercept\n0.0\n", "select regr_intercept(x, y) from tbl1"
            );
        });
    }

    @Test
    public void testRegrInterceptOneColumnAllNull() throws Exception {
        assertMemoryLeak(() -> assertSql(
                "regr_intercept\nnull\n", "select regr_intercept(x, y) from (select cast(null as double) x, x as y from long_sequence(100))"
        ));
    }

    @Test
    public void testRegrInterceptOneValue() throws Exception {
        assertMemoryLeak(() -> {
            ddl("create table tbl1(x int, y int)");
            insert("insert into 'tbl1' VALUES (17.2151920, 17.2151920)");
            assertSql(
                    "regr_intercept\nnull\n", "select regr_intercept(x, y) from tbl1"
            );
        });
    }

    @Test
    public void testRegrInterceptNoOverflow() throws Exception {
        assertMemoryLeak(() -> {
            ddl("create table tbl1 as (select 100000000 x, 100000000 y from long_sequence(1000000))");
            assertSql(
                    "regr_intercept\nnull\n", "select regr_intercept(x, y) from tbl1"
            );
        });
    }

    @Test
    public void testRegrInterceptSomeNull() throws Exception {
        assertMemoryLeak(() -> {
            ddl("create table tbl1 as (select cast(x as double) x, cast(x as double) y from long_sequence(100))");
            insert("insert into 'tbl1' VALUES (null, null)");
            assertSql(
                    "regr_intercept\n0.0\n", "select regr_intercept(x, y) from tbl1"
            );
        });
    }

    @Test
    public void testRegrInterceptWithNonZeroIntercept() throws Exception {
        assertMemoryLeak(() -> {
            ddl("create table tbl1 as (select x, 2 * x + 5 as y from long_sequence(100))");
            assertSql(
                    "regr_intercept\n-2.5\n", "select regr_intercept(x, y) from tbl1"
            );
        });
    }

}