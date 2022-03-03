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
package com.github.mdbx4s.test

import java.io.IOException

import com.github.mdbx4s.bindings.ILibraryWrapper
import com.github.mdbx4s.util.BytesUtils.{serializeString => bytes}
import com.github.mdbx4s.EnvFlags.MDBX_ENV_DEFAULTS
import com.github.mdbx4s.{AbstractTestSuite, PutFlags}

import utest._


/**
 * Unit tests for the MDBX API.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
trait AbstractCRUDTest extends TestSuite with AbstractTestSuite {

  val libraryWrapper: ILibraryWrapper

  val tests = Tests {
    test("testCRUD") { testCRUD() }
  }

  @throws[IOException]
  def testCRUD(): Unit = {

    val sysRamInfo = libraryWrapper.mdbx_get_sysraminfo()
    println("System RamInfo:" + sysRamInfo)

    val minPgSize = libraryWrapper.mdbx_limits_pgsize_min()
    val maxPgSize = libraryWrapper.mdbx_limits_pgsize_max()
    println("Page size MinMax:" + minPgSize + '/' + maxPgSize)

    val pgSize = 4096
    val minDbSize = libraryWrapper.mdbx_limits_dbsize_min(pgSize)
    val maxDbSize = libraryWrapper.mdbx_limits_dbsize_max(pgSize)
    val maxTxnSize = libraryWrapper.mdbx_limits_txnsize_max(pgSize)
    val maxKeySize = libraryWrapper.mdbx_limits_keysize_max(pgSize, MDBX_ENV_DEFAULTS.getMask())
    val maxValSize = libraryWrapper.mdbx_limits_valsize_max(pgSize, MDBX_ENV_DEFAULTS.getMask())
    println("4k page size, Db MinMax:" + minDbSize + '/' + maxDbSize + ", max txn:" + maxTxnSize + ", max key:" + maxKeySize + ", max val:" + maxValSize)

    val tempDir = createTempDirectory(thisClassName, true)
    val env = new com.github.mdbx4s.Env()
    println("tempDir:" + tempDir)
    env.open(tempDir)

    val db = env.openDatabase("foo")
    println("after openDatabase")

    val origVal = bytes("green")
    assert(db.put(bytes("Tampa"), origVal).isEmpty)

    val oldVal = db.replace(bytes("Tampa"), bytes("orange"))
    assertArrayEquals(origVal, oldVal.get)
    assert(db.put(bytes("London"), bytes("red")).isEmpty)
    assert(db.put(bytes("New York"), bytes("gray")).isEmpty)
    assert(db.put(bytes("New York"), bytes("blue")).isEmpty)
    assert(db.get(bytes("New York")).get sameElements bytes("blue"))
    assertArrayEquals(db.put(bytes("New York"), bytes("silver"), PutFlags.MDBX_NOOVERWRITE).get, bytes("blue"))

    //assert(db.get(bytes("Tampa")).get sameElements bytes("orange"))
    assertArrayEquals(db.get(bytes("Tampa")).get, bytes("orange"))
    assertArrayEquals(db.get(bytes("London")).get, bytes("red"))
    assertArrayEquals(db.get(bytes("New York")).get, bytes("blue"))

    var tx = env.createTransaction()

    {
      val stat = env.stat(tx)
      println("Env stat:" + stat)
    }

    val cursor = db.openCursor(tx)
    val flagState = db.getFlagsState(tx)
    println("Db FlagState:" + flagState)

    //only for dupsort db
    //		int depthMask = db.getDupsortDepthMask(tx);
    //		System.out.println("Db depthMask:" + depthMask);
    val sequence = db.getThenIncrementSequence(tx, 1)
    println("Db sequence:" + sequence)

    val envInfo = env.info(tx)
    System.out.println("Tx env info:" + envInfo)

    val stat = env.stat(tx)
    System.out.println("Tx stat:" + stat)

    // Lets verify cursoring works..
    val keys = new scala.collection.mutable.ArrayBuffer[String]
    val values = new scala.collection.mutable.ArrayBuffer[String]
    var entryOpt = cursor.first()
    while (entryOpt.isDefined) {
      val (key, value) = entryOpt.get
      keys += bytes2string(key)
      values += bytes2string(value)

      entryOpt = cursor.next()
    }

    val commitWithLatency = tx.commitWithLatency()
    assert(Array("London", "New York", "Tampa") sameElements keys)
    assert(Array("red", "blue", "orange") sameElements values)
    assert(db.delete(bytes("New York")))
    assert(db.get(bytes("New York")).isEmpty)
    // We should not be able to delete it again.
    assert(!db.delete(bytes("New York")))

    // put /w readonly transaction should fail.
    tx = env.createTransaction(true)
    try {
      val txnInfo = tx.info(false)
      println("TxInfo:" + txnInfo)
      db.put(tx, bytes("New York"), bytes("silver"))

      throw new Exception("Expected MdbxNativeException")
    } catch {
      case e: com.github.mdbx4s.MdbxNativeException =>
        assert(e.getErrorCode() == 111 || e.getErrorCode() == 5) // EACCES=111 (Permission denied); Write Fault=5
    }

    //println("before db.close()")
    db.close()
    //println("before env.close()")
    env.close()
  }
}