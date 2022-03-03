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

import java.io.IOException
import utest._

abstract class AbstractTransactionTest extends AbstractTestSuite {

  private var env: Env = null
  private var db: Database = null
  private val data = Array[Byte](1, 2, 3)

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
    utest.test("testCommit") { testCommit() }
    utest.test("testAbort") { testAbort() }
    utest.test("testResetRenew") { testResetRenew() }
  }

  def testCommit(): Unit = {
    var tx = env.createTransaction()
    try {
      db.put(tx, data, data)
      tx.commit()
    } finally if (tx != null) tx.close()

    assertArrayEquals(data, db.get(data).get)

    tx = env.createTransaction()
    try {
      db.delete(tx, data)
      tx.commit()
    } finally if (tx != null) tx.close()

    assert(db.get(data).isEmpty)
  }

  def testAbort(): Unit = {
    var tx = env.createTransaction()
    try db.put(tx, data, data)
    finally if (tx != null) tx.close()

    assert(db.get(data).isEmpty)

    db.put(data, data)
    tx = env.createTransaction()
    try db.delete(tx, data)
    finally if (tx != null) tx.close()
    assertArrayEquals(data, db.get(data).get)
  }

  def testResetRenew(): Unit = {
    db.put(data, data)
    val tx = env.createTransaction(readOnly = true)

    try {
      assertArrayEquals(data, db.get(tx, data).get)
      tx.reset()

      val e = intercept[MdbxException] {
        db.get(tx, data)
        //throw new Exception("should fail with MdbxException since transaction was reset")
      }

      tx.renew()
      assertArrayEquals(data, db.get(tx, data).get)
    } finally if (tx != null) tx.close()
  }
}
