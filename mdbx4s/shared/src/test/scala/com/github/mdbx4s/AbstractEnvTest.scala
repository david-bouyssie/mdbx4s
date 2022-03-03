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

import scala.collection.mutable.ArrayBuffer

import com.github.mdbx4s.util.BytesUtils.{serializeString => bytes}
import com.github.mdbx4s.PutFlags.MDBX_NOOVERWRITE
import utest._

abstract class AbstractEnvTest extends AbstractTestSuite {

  val tests = Tests {
    utest.test("testCRUD") { testCRUD() }
    utest.test("testBackup") { testBackup() }
    utest.test("testBackupCompact") { testBackupCompact() }
    utest.test("testEnvInfo") { testEnvInfo() }
    utest.test("testStat") { testStat() }
    utest.test("testMaxKeySize") { testMaxKeySize() }
  }

  @throws[Exception]
  def testCRUD(): Unit = {
    val path = createTempDirectory(thisClassName, true)
    val env = new Env()

    try {
      env.open(path)
      /*Env.pushMemoryPool(10)
      Env.pushMemoryPool(10)
      Env.popMemoryPool()
      Env.popMemoryPool()*/
      val db = env.openDatabase()
      try doTest(env, db, forceSync = false)
      finally {
        if (db != null) db.close()
      }
    } finally if (env != null) env.close()
  }

  @throws[Exception]
  def testBackup(): Unit = {
    val path = createTempDirectory(thisClassName, true)
    val backupDir = createTempDirectory(thisClassName+"_backup", true)
    val backupFile = new java.io.File(backupDir,"mdbx.dat")

    var env = new Env()
    try {
      env.open(path)
      val db = env.openDatabase()
      try {
        db.put(Array[Byte](1), Array[Byte](1))
        env.copy(backupFile)
      } finally if (db != null) db.close()
    } finally if (env != null) env.close()

    env = new Env()
    try {
      env.open(backupFile)
      val db = env.openDatabase()
      try {
        val value = db.get(Array[Byte](1)).get
        assert(value(0).toInt == 1)
      } finally if (db != null) db.close()
    } finally if (env != null) env.close()
  }

  @throws[Exception]
  def testBackupCompact(): Unit = {
    val path = createTempDirectory(thisClassName, true)
    val backupDir = createTempDirectory(thisClassName+"_backup", true)
    val backupFile = new java.io.File(backupDir,"mdbx.dat")

    var env = new Env()
    try {
      env.open(path)
      val db = env.openDatabase()
      try db.put(Array[Byte](1), Array[Byte](1))
      finally if (db != null) db.close()
    } finally if (env != null) env.close()

    env = new Env()
    try {
      env.open(path)
      env.copy(backupFile, CopyFlags.MDBX_CP_COMPACT)
    } finally if (env != null) env.close()

    env = new Env
    try {
      env.open(backupFile)
      val db = env.openDatabase()
      try {
        val value = db.get(Array[Byte](1)).get
        assert(value(0).toInt == 1)
      } finally if (db != null) db.close()
    } finally if (env != null) env.close()
  }

  @throws[Exception]
  def testEnvInfo(): Unit = {
    val path = createTempDirectory(thisClassName, true)
    val env = new Env()

    try {
      // env.addFlags(CREATE);
      env.open(path, EnvConfig(mapSize = 1048576L))

      val db = env.openDatabase()
      try {
        db.put(Array[Byte](1), Array[Byte](1))
        //val info = env.info()
        //assertNotNull(info)
      } finally if (db != null) db.close()
    } finally if (env != null) env.close()
  }

  @throws[Exception]
  def testStat(): Unit = {
    val path = createTempDirectory(thisClassName, true)
    val env = new Env()

    try {
      env.open(path, EnvConfig(mapSize = 1048576L))

      val db = env.openDatabase()
      try {
        db.put(Array[Byte](1), Array[Byte](1))
        db.put(Array[Byte](2), Array[Byte](1))
        val txn = env.createTransaction(readOnly = true)
        val stat = env.stat(txn)
        txn.close()
        println(stat)
        assert(stat.entries == 2)
        assert(stat.pageSize != 0)
        assert(stat.overflowPages == 0)
        assert(stat.depth == 1)
        assert(stat.leafPages == 1)
      } finally if (db != null) db.close()
    } finally if (env != null) env.close()
  }

  @throws[Exception]
  def testMaxKeySize(): Unit = {
    val path = createTempDirectory(thisClassName, true)
    val env = new Env()
    try {
      env.open(path)
      println("env.getMaxKeySize=" + env.getMaxKeySize())
      //assert(env.getMaxKeySize() == 1980L)
    } finally {
      if (env != null) env.close()
    }
  }

  private def doTest(env: Env, db: Database, forceSync: Boolean = true): Unit = {
    assert(db.put(bytes("Tampa"), bytes("green")).isEmpty)
    assert(db.put(bytes("London"), bytes("red")).isEmpty)
    assert(db.put(bytes("New York"), bytes("gray")).isEmpty)
    assert(db.put(bytes("New York"), bytes("blue")).isEmpty)
    assertArrayEquals(db.put(bytes("New York"), bytes("silver"), MDBX_NOOVERWRITE).get, bytes("blue"))
    assertArrayEquals(db.get(bytes("Tampa")).get, bytes("green"))
    assertArrayEquals(db.get(bytes("London")).get, bytes("red"))
    assertArrayEquals(db.get(bytes("New York")).get, bytes("blue"))

    var tx = env.createTransaction(readOnly = true)
    val cursor = db.openCursor(tx)

    try {
      // Lets verify cursoring works..
      val keys = new ArrayBuffer[String]
      val values = new ArrayBuffer[String]

      var entryOpt = cursor.first()
      while (entryOpt.isDefined) {
        val (key, value) = entryOpt.get
        keys += bytes2string(key)
        values += bytes2string(value)

        entryOpt = cursor.next()
      }

      assert(Array("London", "New York", "Tampa") sameElements keys)
      assert(Array("red", "blue", "green") sameElements values)
    } finally {
      if (tx != null) tx.close()
      if (cursor != null) cursor.close()
    }

    assert(db.delete(bytes("New York")))
    assert(db.get(bytes("New York")).isEmpty)
    // We should not be able to delete it again.
    assert(!db.delete(bytes("New York")))

    // put /w readonly transaction should fail.
    tx = env.createTransaction(readOnly = true)
    try {
      db.put(tx, bytes("New York"), bytes("silver"))
      throw new MdbxException("Expected MdbxNativeException")
    } catch {
      case e: MdbxNativeException =>
        assert(e.getErrorCode > 0)
    } finally if (tx != null) tx.close()

    try {
      env.sync(forceSync)
    } catch {
      case e: MdbxNativeException =>
        println("can't sync env because " + e.getMessage)
    }
  }
}
