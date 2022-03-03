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
import java.nio.ByteBuffer
import java.util.Random
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

import com.github.mdbx4s.EnvFlags.MDBX_LIFORECLAIM
import com.github.mdbx4s.util.BytesUtils
import utest._

abstract class AbstractInsertTest extends AbstractTestSuite {

  private val I_CNT = 10
  private val J_CNT = 100

  private var env: Env = null
  private var db: Database = null

  @throws[IOException]
  override def utestBeforeEach(path: Seq[String]): Unit = {
    val tempDir = createTempDirectory(thisClassName, true)

    val envConfig = EnvConfig(
      maxDbs = 2,
      mapSize = 16 * 1024 * 1024 // 16 MB,
    )
    envConfig.setFlags(MDBX_LIFORECLAIM)

    env = new Env()

    //Env.pushMemoryPool(1024 * 512)
    env.open(tempDir, envConfig)

    db = env.openDatabase("primary")
    //b = env.openDatabase("primary", new KeyComparator(), null)
    //db = env.openDatabase("primary", UnsignedBytes.lexicographicalComparator, null)
  }

  override def utestAfterEach(path: Seq[String]): Unit = {
    db.close()
    //Env.popMemoryPool()
    env.close()
  }

  val tests = Tests {
    utest.test("A_uuidRandom_100Bytes") { A_uuidRandom_100Bytes() }
    utest.test("B_longSequential_100Bytes") { B_longSequential_100Bytes() }
    utest.test("C_longSequential_1kBytes") { C_longSequential_1kBytes() }
    utest.test("D_longSequential_1kBytes_withUpdate") { D_longSequential_1kBytes_withUpdate() }
    utest.test("E_longSequential_1kBytes_withGet") { E_longSequential_1kBytes_withGet() }
  }

  def A_uuidRandom_100Bytes(): Unit = {
    System.out.println("Starting uuidRandom_100Bytes")
    for (i <- 0 until I_CNT) {
      //			long start = System.nanoTime();
      //			System.out.println("start trans " + i);
      val tx = env.createTransaction()
      try {
        for (j <- 0 until J_CNT) {
          val uuid = UUID.randomUUID()
          ///					System.out.println("\t putting " + uuid);
          db.put(tx, UuidAdapter.getBytesFromUUID(uuid), new Array[Byte](100))
        }
        val latency = tx.commitWithLatency()
        // System.out.println("Latency:" + latency);
        // assertTrue(true)
      } finally if (tx != null) tx.close()
      // System.out.println("Completed " + i + " in " + TimeUtils.elapsedSinceNano(start));
    }
  }

  def B_longSequential_100Bytes(): Unit = {
    System.out.println("Starting longSequential_100Bytes")

    val along = new AtomicLong()
    for (i <- 0 until I_CNT) {
      val tx = env.createTransaction()
      try {
        for (j <- 0 until J_CNT) {
          val l = along.getAndIncrement
          db.put(tx, longToBytes(l), new Array[Byte](100))
        }
        val latency = tx.commitWithLatency()
        //assertTrue(true)
      } finally if (tx != null) tx.close()
    }
  }

  def C_longSequential_1kBytes(): Unit = {
    System.out.println("Starting longSequential_1kBytes")
    val along = new AtomicLong()
    for (i <- 0 until I_CNT) {
      // long start = System.nanoTime();
      // System.out.println("start trans " + i);
      val tx = env.createTransaction()
      try {
        for (j <- 0 until J_CNT) {
          val l = along.getAndIncrement()
          db.put(tx, longToBytes(l), new Array[Byte](1024))
        }
        tx.commit()
        //assertTrue(true)
      } finally if (tx != null) tx.close()
      //			System.out.println("Completed " + i + " in " + TimeUtils.elapsedSinceNano(start));
    }
  }

  def D_longSequential_1kBytes_withUpdate(): Unit = {
    System.out.println("Starting longSequential_1kBytes_withUpdate")
    var along = new AtomicLong()
    val random = new Random()

    var start = System.nanoTime()
    for (i <- 0 until I_CNT / 2) {
      //			System.out.println("start trans " + i);
      val tx = env.createTransaction()
      try {
        val entries = new scala.collection.mutable.HashMap[Long, Array[Byte]]
        for (j <- 0 until J_CNT) {
          val l = along.getAndIncrement()
          val b = new Array[Byte](1024)
          random.nextBytes(b)
          db.put(tx, longToBytes(l), b)
          entries.put(l, b)
        }
        tx.commit()

        entries.foreach({ entry =>
          //assertArrayEquals(entry.getKey, entry.getValue)
          assertArrayEquals(db.get(longToBytes(entry._1)).get, entry._2)
        })

      } finally if (tx != null) tx.close()
    }

    System.out.println("Completed inserts in " + util.TimeUtils.elapsedSinceNano(start))
    along = new AtomicLong()
    start = System.nanoTime()
    for (i <- 0 until I_CNT / 2) {
      val tx = env.createTransaction()
      try {
        for (j <- 0 until J_CNT) {
          val l = along.getAndIncrement()
          val b = new Array[Byte](1024)
          random.nextBytes(b)
          db.put(tx, longToBytes(l), b)
        }
        tx.commit()
        //assertTrue(true)
      } finally if (tx != null) tx.close()
    }
    System.out.println("Completed updates in " + util.TimeUtils.elapsedSinceNano(start))
  }

  def E_longSequential_1kBytes_withGet(): Unit = {
    System.out.println("Starting longSequential_1kBytes_withGet")
    val along = new AtomicLong()
    val random = new Random()
    for (i <- 0 until I_CNT) {
      val tx = env.createTransaction()
      try {
        val entries = new scala.collection.mutable.HashMap[Long, Array[Byte]]
        for (j <- 0 until J_CNT) {
          val l = along.getAndIncrement
          val b = new Array[Byte](1024)
          random.nextBytes(b)
          db.put(tx, longToBytes(l), b)
          entries.put(l, b)
        }
        tx.commit()

        entries.foreach({ entry =>
          assertArrayEquals(db.get(longToBytes(entry._1)).get, entry._2)
        })

      } finally if (tx != null) tx.close()
    }
  }

  /*
  def F_longSequential_3kBytes_withGetParallel(): Unit = {
    System.out.println("Starting longSequential_3kBytes_withGet")
    val along = new AtomicLong
    val random = new Random
    for (i <- 0 until I_CNT) {
      try {
        val tx = env.createWriteTransaction
        try {
          val entries = new HashMap[Long, Array[Byte]]
          for (j <- 0 until J_CNT) {
            val l = along.getAndIncrement
            val b = new Array[Byte](3072)
            random.nextBytes(b)
            db.put(tx, longToBytes(l), b)
            entries.put(l, b)
          }
          tx.commit()
          entries.entrySet.parallelStream.forEach((entry: util.Map.Entry[Long, Array[Byte]]) => {
            def foo(entry: util.Map.Entry[Long, Array[Byte]]) = {
              assertArrayEquals(db.get(longToBytes(entry.getKey)), entry.getValue)
            }

            foo(entry)
          }
          )
        } finally if (tx != null) tx.close()
      }
    }
  }*/

  @inline
  def longToBytes(x: Long): Array[Byte] = BytesUtils.fromLong(x)

  object UuidAdapter {
    def getBytesFromUUID(uuid: UUID): Array[Byte] = {
      val bb = ByteBuffer.wrap(new Array[Byte](16))
      bb.putLong(uuid.getMostSignificantBits)
      bb.putLong(uuid.getLeastSignificantBits)
      bb.array
    }

    def getUUIDFromBytes(bytes: Array[Byte]): UUID = {
      val byteBuffer = ByteBuffer.wrap(bytes)
      val high = byteBuffer.getLong
      val low = byteBuffer.getLong
      new UUID(high, low)
    }
  }

}
