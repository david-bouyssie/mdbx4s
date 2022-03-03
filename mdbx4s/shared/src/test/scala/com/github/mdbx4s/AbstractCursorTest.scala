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

abstract class AbstractCursorTest extends AbstractTestSuite {

  private var env: Env = null
  private var db: Database = null

  @throws[IOException]
  override def utestBeforeEach(path: Seq[String]): Unit = {
    val tempDir = createTempDirectory(thisClassName, true)
    env = new Env(tempDir)
    db = env.openDatabase()
  }

  override def utestAfterEach(path: Seq[String]): Unit = {
    db.close()
    env.close()
  }

  val tests = Tests {
    utest.test("testCursorPutGet") { testCursorPutGet() }
    utest.test("testCursorRenew") { testCursorRenew() }
  }

  def testCursorPutGet(): Unit = {
    val tx = env.createTransaction()
    try {
      val cursor = db.openCursor(tx)
      try cursor.put(BytesUtils.fromLong(1), BytesUtils.fromLong(1), PutFlags.MDBX_UPSERT)
      finally if (cursor != null) cursor.close()
      tx.commit()
    } finally if (tx != null) tx.close()

    assertArrayEquals(db.get(BytesUtils.fromLong(1)).get, BytesUtils.fromLong(1))
  }

  def testCursorRenew(): Unit = {

    val read = env.createTransaction(readOnly = true)
    val readCursor = db.openCursor(read)
    assert(readCursor.first().isEmpty)

    //read.abort()
    read.reset()

    val write = env.createTransaction()
    try {
      val cursor = db.openCursor(write)
      try cursor.put(BytesUtils.fromLong(1), BytesUtils.fromLong(1), PutFlags.MDBX_UPSERT)
      finally if (cursor != null) cursor.close()
      write.commit()
    } finally if (write != null) write.close()

    try {
      //read = env.createTransaction(readOnly = true)
      read.renew()

      readCursor.renew(read)
      assertArrayEquals(readCursor.first().get._1, BytesUtils.fromLong(1))
    } finally {
      if (readCursor != null) readCursor.close()
      read.abort()
    }
  }

}
