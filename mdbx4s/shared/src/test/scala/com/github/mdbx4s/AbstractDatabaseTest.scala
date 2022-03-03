/*
 * Copyright 2022 David Bouyssié
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
package com.github.mdbx4s

import com.github.mdbx4s.util.BytesUtils

import java.io.IOException
import utest._

abstract class AbstractDatabaseTest extends AbstractTestSuite {

  private var env: Env = null
  private var db: Database = null

  @throws[IOException]
  override def utestBeforeEach(path: Seq[String]): Unit = {
    val tempDir = createTempDirectory(thisClassName, true)

    val envConfig = EnvConfig(mapSize = 16 * 4096)
    env = new Env()
    env.open(tempDir, envConfig)

    db = env.openDatabase()
  }

  override def utestAfterEach(path: Seq[String]): Unit = {
    db.close()
    env.close()
  }

  val tests = Tests {
    utest.test("testCleanupFullDb") { testCleanupFullDb() }
    utest.test("testDrop") { testDrop() }
    utest.test("testStat") { testStat() }
  }

  /**
   * Should trigger MDB_MAP_FULL if entries wasn't deleted.
   */
  def testCleanupFullDb(): Unit = {
    for (i <- 0 until 100) { // twice the size of page size
      db.put(BytesUtils.fromLong(i), new Array[Byte](2 * 4096))
      db.delete(BytesUtils.fromLong(i))
    }
  }

  def testDrop(): Unit = {
    val bytes: Array[Byte] = Array(1, 2, 3)
    db.put(bytes, bytes)

    var value = db.get(bytes)
    assertArrayEquals(value.get, bytes)

    // empty
    db.drop(false)
    value = db.get(bytes)
    assert(value.isEmpty)

    db.put(bytes, bytes)
    db.drop(true)

    intercept[MdbxException] {
      db.get(bytes)
      //fail("db has been closed")
    }
  }

  def testStat(): Unit = {
    db.put(Array[Byte](1), Array[Byte](1))
    db.put(Array[Byte](2), Array[Byte](1))
    val stat = db.stat()
    System.out.println(stat)
    assert(stat.entries == 2L)
    assert(stat.pageSize != 0)
    assert(stat.overflowPages == 0)
    assert(stat.depth == 1L)
    assert(stat.leafPages == 1L)
  }

}
