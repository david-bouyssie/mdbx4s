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

class TransactedDatabase private[mdbx4s](
  val env: Env,
  val transaction: Transaction,
  val database: Database
) extends AutoCloseable {

  def isClosed(): Boolean = database.isClosed()

  /**
   * <p>
   * Close a database handle. Normally unnecessary.
   * </p>
   *
   * Use with care:
   *
   * This call is not mutex protected. Handles should only be closed by a single
   * thread, and only if no other threads are going to reference the database
   * handle or one of its cursors any further. Do not close a handle if an
   * existing transaction has modified its database. Doing so can cause
   * misbehavior from database corruption to errors like
   * BAD_VALSIZE (since the DB name is gone).
   */
  override def close(): Unit = database.close()

  /**
   * @return Statistics for a database.
   */
  def stat(): Stat = database.stat(transaction)

  def drop(delete: Boolean): Unit = database.drop(transaction, delete)

  def get(key: Array[Byte]): Option[Array[Byte]] = database.get(transaction, key)

  /*def getEx(key: Array[Byte]): EntryCount = {
    checkArgNotNull(key, "key")
    val txn = env.createTransaction
    try getEx(txn, key)
    finally txn.commit()
  }

  def getEqOrGE(key: Array[Byte]): Entry = {
    checkArgNotNull(key, "key")
    val txn = env.createTransaction
    try getEqOrGE(txn, key)
    finally txn.commit()
  }*/


  /*
  def getEx(txn: Transaction, key: Array[Byte]): EntryCount = {
    checkArgNotNull(key, "key")
    if (tx == null) return getEx(key)
    checkArgNotNull(txn, "tx")
    val keyBuffer = NativeBuffer.create(key)
    try getEx(txn, keyBuffer)
    finally keyBuffer.delete()
  }

  /**
   * <p>
   * Get items from a database.
   * </p>
   * Get equal or greater item from a database. This is equivalent to using a cursor with MDBX_SET_LOWERBOUND.
   * <br />
   * Briefly this function does the same as mdbx_get() with a few differences: <br />
   *
   * <ol>
   * <li>Return equal or great (due comparison function) key-value pair, but not only exactly matching with the
   * key.</li>
   * <li>On success return MDBX_SUCCESS if key found exactly, and MDBX_RESULT_TRUE otherwise. Moreover, for
   * databases with MDBX_DUPSORT flag the data argument also will be used to match over multi-value/duplicates,
   * and MDBX_SUCCESS will be returned only when BOTH the key and the data match exactly.</li>
   * <li>Updates BOTH the key and the data for pointing to the actual key-value pair inside the database.
   * <p>
   * </li>
   * </ol>
   *
   * @param tx
   * transaction handle
   * @param key
   * The key to search for in the database
   * @return Entry representing the key value pair
   */
  @SuppressWarnings(Array("nls")) def getEqOrGE(txn: Transaction, key: Array[Byte]): Entry = {
    checkArgNotNull(key, "key")
    if (tx == null) return getEqOrGE(key)
    checkArgNotNull(txn, "txn")
    val keyBuffer = NativeBuffer.create(key)
    try getEqOrGE(txn, keyBuffer)
    finally keyBuffer.delete()
  }*/

  //private def get(txn: Transaction, keyBuffer: NativeBuffer) = get(txn, new Value(keyBuffer))

  /*private def getEx(txn: Transaction, keyBuffer: NativeBuffer) = getEx(txn, new Value(keyBuffer))

  private def getEqOrGE(txn: Transaction, keyBuffer: NativeBuffer) = getEqOrGE(txn, new Value(keyBuffer))*/

  /*
  private[mdbxjni] def getEx(tx: Transaction, key: Value): EntryCount = {
    val value = new Value
    val valCnt = new Array[Long](1)
    val rc = mdbx_get_ex(txn.pointer, pointer, key, value, valCnt)
    if (rc == MDBX_NOTFOUND) return null
    checkErrorCode(env, rc)
    new EntryCount(key.toByteArray, value.toByteArray, valCnt(0))
  }

  private[mdbxjni] def getEqOrGE(tx: Transaction, key: Value): Entry = {
    val value = new Value
    val rc = mdbx_get_equal_or_great(txn.pointer, pointer, key, value)
    if (rc == MDBX_NOTFOUND) return null
    checkErrorCode(env, rc)
    new Entry(key.toByteArray, value.toByteArray)
  }*/

  def put(key: Array[Byte], value: Array[Byte]): Option[Array[Byte]] = {
    database.put(transaction, key, value, PutFlags.MDBX_UPSERT)
  }

  def put(key: Array[Byte], value: Array[Byte], flags: PutFlags.Flag*): Option[Array[Byte]] = {
    database.put(transaction, key, value, flags: _*)
  }

  def replace(key: Array[Byte], value: Array[Byte]): Option[Array[Byte]] = {
    database.replace(transaction, key, value, PutFlags.MDBX_UPSERT)
  }

  def replace(key: Array[Byte], value: Array[Byte], flags: PutFlags.Flag*): Option[Array[Byte]] = {
    database.replace(transaction, key, value, flags: _*)
  }


  def delete(key: Array[Byte]): Boolean = {
    database.delete(transaction, key)
  }

  def delete(key: Array[Byte], value: Array[Byte]): Boolean = {
    database.delete(transaction, key, value)
  }

  /**
   * <p>
   * Create a cursor handle.
   * </p>
   *
   * A cursor is associated with a specific transaction and database. A cursor
   * cannot be used when its database handle is closed. Nor when its transaction
   * has ended, except with #mdb_cursor_renew(). It can be discarded with
   * #mdb_cursor_close(). A cursor in a write-transaction can be closed before its
   * transaction ends, and will otherwise be closed when its transaction ends. A
   * cursor in a read-only transaction must be closed explicitly, before or after
   * its transaction ends. It can be reused with #mdb_cursor_renew() before
   * finally closing it.
   *
   * @note Earlier documentation said that cursors in every transaction were
   *       closed when the transaction committed or aborted.
   * @return A new cursor handle
   */
  def openCursor(): Cursor = database.openCursor(transaction)

  def getFlags(): Int = database.getFlags(transaction)

  def getFlagsState(): Database.FlagsAndState = database.getFlagsState(transaction)

  /*
  def getDupsortDepthMask(txn: Transaction): Int = {
    val mask = new Array[Long](1)
    checkErrorCode(env, mdbx_dbi_dupsort_depthmask(txn.pointer, pointer, mask))
    mask(0).toInt
  }*/

  def getThenIncrementSequence(increment: Long): Long = {
    database.getThenIncrementSequence(transaction, increment)
  }

  /*def getConfig: DatabaseConfig = {
    val txn = env.createTransaction
    try getConfig(txn)
    finally txn.commit()
  }

  def getConfig(txn: Transaction): DatabaseConfig = {
    val flags = getFlags(txn)
    new DatabaseConfig(flags)
  }*/

}
