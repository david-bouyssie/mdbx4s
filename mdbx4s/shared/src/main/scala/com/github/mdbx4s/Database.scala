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

object Database {
  case class FlagsAndState private(flags: Int, state: DbiState.Value)

}

class Database private[mdbx4s](
  val env: Env,
  val name: String,
  private[mdbx4s] var dbiPtr: Env.LIB.DbiHandle
) extends AutoCloseable {
  //private var secondaries = null

  def isClosed(): Boolean = { dbiPtr == null }

  @inline
  private def _checkDbiNotClosed(): Unit = {
    if (dbiPtr == null) throw new MdbxException("the database is closed")
  }

  @inline
  private def _checkTxnThenDbi(txn: Transaction): Unit = {
    require(txn != null, "txn is null")
    require(!txn.isClosed(), "transaction is closed")
    _checkDbiNotClosed()
  }

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
  override def close(): Unit = {
    if (dbiPtr != null) {
      Env.LIB.mdbx_dbi_close(env.envPtr, dbiPtr)
      dbiPtr = null
    }
  }

  /**
   * @return Statistics for a database.
   */
  def stat(): Stat = {
    _checkDbiNotClosed()
    val txn = env.createTransaction(readOnly = true)
    try stat(txn)
    finally txn.close()
  }

  def stat(txn: Transaction): Stat = {
    _checkTxnThenDbi(txn)

    Env.LIB.mdbx_dbi_stat(txn.txnPtr, dbiPtr)
  }

  def drop(delete: Boolean): Unit = {
    val txn = env.createTransaction()
    try drop(txn, delete)
    finally txn.commit()
  }

  /**
   * <p>
   * Empty or delete+close a database.
   * </p>
   *
   * @param tx
   * transaction handle
   * @param delete
   * false to empty the DB, true to delete it from the environment and
   * close the DB handle.
   */
  def drop(txn: Transaction, delete: Boolean): Unit = {
    _checkTxnThenDbi(txn)

    Env.LIB.mdbx_drop(txn.txnPtr, dbiPtr, delete)

    if (delete) dbiPtr = null
  }

  //def getSecondaries(): List[SecondaryDatabase] = secondaries

  def get(key: Array[Byte]): Option[Array[Byte]] = {
    require(key != null, "key is null")

    val txn = env.createTransaction(readOnly = true)
    try get(txn, key)
    finally txn.close()
  }

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

  /**
   * <p>
   * Get items from a database.
   * </p>
   *
   * This function retrieves key/data pairs from the database. The address and
   * length of the data associated with the specified \b key are returned in the
   * structure to which \b data refers. If the database supports duplicate keys
   * (DUPSORT}) then the first data item for the key will be returned.
   *  Retrieval of other items requires the use of mdb_cursor_get().
   *
   * @param tx
   * transaction handle
   * @param key
   * The key to search for in the database
   * @return The data corresponding to the key or null if not found
   */
  def get(txn: Transaction, key: Array[Byte]): Option[Array[Byte]] = {
    require(key != null, "key is null")

    this._get(txn, key)
  }

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

  @inline
  private[mdbx4s] def _get(txn: Transaction, key: Array[Byte]): Option[Array[Byte]] = {
    _checkTxnThenDbi(txn)
    Env.LIB.mdbx_get(txn.txnPtr, dbiPtr, key)
  }

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

  /**
   * <p>
   * Store items into a database.
   * </p>
   *
   * This function stores key/data pairs in the database. The default behavior is
   * to enter the new key/data pair, replacing any previously existing key if
   * duplicates are disallowed, or adding a duplicate data item if duplicates are
   * allowed (DUPSORT).
   *
   * @param tx
   * transaction handle
   * @param key
   * The key to store in the database
   * @param value
   * The value to store in the database
   * @param flags
   * Special options for this operation. This parameter must be set to
   * 0 or by bitwise OR'ing together one or more of the values
   * described here.
   * <ul>
   * <li>NODUPDATA - enter the
   * new key/data pair only if it does not already appear in the
   * database. This flag may only be specified if the database was
   * opened with DUPSORT. The
   * function will return #MDB_KEYEXIST if the key/data pair already
   * appears in the database.
   * <li>NOOVERWRITE - enter
   * the new key/data pair only if the key does not already appear in
   * the database. The function will return KEYEXIST if the key
   * already appears in the database, even if the database supports
   * duplicates (DUPSORT). The
   * \b data parameter will be set to point to the existing item.
   * <li>RESERVE - reserve
   * space for data of the given size, but don't copy the given data.
   * Instead, return a pointer to the reserved space, which the caller
   * can fill in later - before the next update operation or the
   * transaction ends. This saves an extra memcpy if the data is being
   * generated later. MDBX does nothing else with this memory, the
   * caller is expected to modify all of the space requested.
   * <li>APPEND - append the
   * given key/data pair to the end of the database. No key comparisons
   * are performed. This option allows fast bulk loading when keys are
   * already known to be in the correct order. Loading unsorted keys
   * with this flag will cause data corruption.
   * <li>APPENDDUP - as above,
   * but for sorted dup data.
   * </ul>
   * @return the existing value if it was a dup insert attempt.
   */
  def put(txn: Transaction, key: Array[Byte], value: Array[Byte], flags: PutFlags.Flag*): Option[Array[Byte]] = {
    require(key != null, "key is null")
    require(value != null, "value is null")

    _put(txn, key, value, flags)
  }

  def put(txn: Transaction, key: Array[Byte], value: Array[Byte]): Option[Array[Byte]] = put(txn, key, value, PutFlags.MDBX_UPSERT)

  def put(key: Array[Byte], value: Array[Byte]): Option[Array[Byte]] = put(key, value, PutFlags.MDBX_UPSERT)

  def put(key: Array[Byte], value: Array[Byte], flags: PutFlags.Flag*): Option[Array[Byte]] = {

    require(key != null, "key is null")
    require(value != null, "value is null")

    val txn = env.createTransaction()
    try _put(txn, key, value, flags)
    finally txn.commit()
  }

  @inline
  private def _put(txn: Transaction, key: Array[Byte], value: Array[Byte], flags: collection.Seq[PutFlags.Flag]): Option[Array[Byte]] = {
    _checkTxnThenDbi(txn)
    Env.LIB.mdbx_put(txn.txnPtr, dbiPtr, key, value, MaskedFlag.mask(flags))
  }

  /*
  private def put(txn: Transaction, keySlice: Value, valueSlice: Value, flags: Int) = {
    checkSize(env, keySlice)
    if ((flags & MDBX_DUPSORT) != 0) checkSize(env, valueSlice)
    val hasSec = getSecondaries != null
    val valueSlices = new HashSet[Value]
    //do we already have an entry under this key, if so secondary will need to be replaced (delete + put)
    if (hasSec && (flags & MDBX_NOOVERWRITE) == 0 && (flags & MDBX_NODUPDATA) == 0) try {
      val cursor = openCursor(txn)
      try {
        val key = keySlice.toByteArray
        val entry = cursor.get(CursorOp.SET, key)
        if (entry != null) {
          val valueBuffer = NativeBuffer.create(entry.getValue)
          valueSlices.add(Value.create(valueBuffer))
        }
      } finally if (cursor != null) cursor.close()
    }
    val rc = mdbx_put(txn.pointer, pointer, keySlice, valueSlice, flags)
    if (((flags & MDBX_NOOVERWRITE) != 0 || (flags & MDBX_NODUPDATA) != 0) && rc == MDBX_KEYEXIST) { // Return the existing value if it was a dup insert attempt.
      valueSlice.toByteArray
    }
    else { // If the put failed, throw an exception..
      if (rc != 0) throw new MDBXException("put failed", rc)
      if (!valueSlices.isEmpty) deleteSecondaries(txn, keySlice, valueSlices)
      putSecondaries(txn, keySlice, valueSlice)
      null
    }
  }*/

  /*protected def putSecondaries(txn: Transaction, keySlice: Value, valueSlice: Value): Unit = {
    if (secondaries != null) {
      import scala.collection.JavaConversions._
      for (secDb <- secondaries) {
        val secConfig = secDb.getConfig.asInstanceOf[SecondaryDbConfig]
        val pKey = keySlice.toByteArray
        val secKey = secConfig.getKeyCreator.createSecondaryKey(secDb, pKey, valueSlice.toByteArray)
        secDb.internalPut(txn, secKey, pKey)
      }
    }
  }*/

  def replace(key: Array[Byte], value: Array[Byte]): Option[Array[Byte]] = replace(key, value, PutFlags.MDBX_UPSERT)

  def replace(key: Array[Byte], value: Array[Byte], flags: PutFlags.Flag*): Option[Array[Byte]] = {
    require(key != null, "key is null")
    require(value != null, "value is null")

    val txn = env.createTransaction()
    try _replace(txn, key, value, flags)
    finally txn.commit()
  }

  def replace(txn: Transaction, key: Array[Byte], value: Array[Byte]): Option[Array[Byte]] = {
    require(txn != null, "tx is null")
    require(!txn.isClosed(), "transaction is closed")
    require(key != null, "key is null")
    require(value != null, "value is null")

    replace(txn, key, value, PutFlags.MDBX_UPSERT)
  }

  def replace(txn: Transaction, key: Array[Byte], value: Array[Byte], flags: PutFlags.Flag*): Option[Array[Byte]] = {
    require(txn != null, "tx is null")
    require(!txn.isClosed(), "transaction is closed")
    require(key != null, "key is null")
    require(value != null, "value is null")

    _replace(txn, key, value, flags)
  }

  private def _replace(txn: Transaction, key: Array[Byte], value: Array[Byte], flags: collection.Seq[PutFlags.Flag]): Option[Array[Byte]] = {
    _checkTxnThenDbi(txn)

    val newValue = Env.LIB.mdbx_replace(txn.txnPtr, dbiPtr, key, value, MaskedFlag.mask(flags))
    Some(newValue)

    /*checkArgNotNull(key, "key")
    checkArgNotNull(value, "value")
    if (tx == null) return replace(key, value, flags)
    checkArgNotNull(txn, "txn")
    val keyBuffer = NativeBuffer.create(key)
    try {
      val valueBuffer = NativeBuffer.create(value)
      try replace(txn, keyBuffer, valueBuffer, flags)
      finally valueBuffer.delete()
    } finally keyBuffer.delete()*/
  }

  /*private def replace(txn: Transaction, keyBuffer: NativeBuffer, valueBuffer: NativeBuffer, flags: Int) = replace(txn, new Value(keyBuffer), new Value(valueBuffer), flags)

  private def replace(txn: Transaction, keySlice: Value, valueSlice: Value, flags: Int) = {
    checkSize(env, keySlice)
    if ((flags & MDBX_DUPSORT) != 0) checkSize(env, valueSlice)
    val hasSec = getSecondaries != null
    val valueSlices = new HashSet[Value]
    if (hasSec && (flags & MDBX_NOOVERWRITE) == 0 && (flags & MDBX_NODUPDATA) == 0) try {
      val cursor = openCursor(tx)
      try {
        val key = keySlice.toByteArray
        val entry = cursor.get(CursorOp.SET, key)
        if (entry != null) {
          val valueBuffer = NativeBuffer.create(entry.getValue)
          valueSlices.add(Value.create(valueBuffer))
        }
      } finally if (cursor != null) cursor.close()
    }
    //allocate buffer with 50% larger than new value which should be enough to avoid retry
    var oldValBuffer = NativeBuffer.create((valueSlice.iov_len * 1.5).toLong)
    try {
      val oldValSlice = new Value(oldValBuffer)
      var didResize = false
      var rc = 0
      while ( {true}) {
        rc = mdbx_replace(txn.pointer, pointer, keySlice, valueSlice, oldValSlice, flags)
        if (rc == MDBX_RESULT_TRUE) { //we have a dirty old value that couldn't fit in old value buffer
          if (didResize) { //been here already, bail out.
            throw new MDBXException("put failed, falling into loop", rc)
          }
          didResize = true
          oldValBuffer.delete() //release existing buffer

          oldValBuffer = NativeBuffer.create(oldValSlice.iov_len)
        }
        else break //todo: break is not supported
      }
      if (rc != 0) throw new MDBXException("put failed", rc)
      if (!valueSlices.isEmpty) deleteSecondaries(txn, keySlice, valueSlices)
      putSecondaries(txn, keySlice, valueSlice)
      oldValSlice.toByteArray
    } finally oldValBuffer.delete()
  }*/

  def delete(key: Array[Byte]): Boolean = {
    require(key != null, "key is null")

    _delete(key, None)
  }

  def delete(key: Array[Byte], value: Array[Byte]): Boolean = {
    require(key != null, "key is null")
    require(value != null, "value is null")

    val txn = env.createTransaction()
    try _delete(txn, key,Some(value))
    finally txn.commit()
  }

  private def _delete(key: Array[Byte], value: Option[Array[Byte]]): Boolean = {
    val txn = env.createTransaction()
    try _delete(txn, key, value)
    finally txn.commit()
  }

  def delete(txn: Transaction, key: Array[Byte]): Boolean = {
    require(key != null, "key is null")

    _delete(txn, key, None)
  }

  /**
   * <p>
   * Removes key/data pairs from the database.
   * </p>
   * If the database does not support sorted duplicate data items
   * (DUPSORT) the value parameter is
   * ignored. If the database supports sorted duplicates and the data parameter is
   * NULL, all of the duplicate data items for the key will be deleted. Otherwise,
   * if the data parameter is non-NULL only the matching data item will be
   * deleted. This function will return false if the specified key/data pair is
   * not in the database.
   *
   * @param tx
   * Transaction handle.
   * @param key
   * The key to delete from the database.
   * @param value
   * The value to delete from the database
   * @return true if the key/value was deleted.
   */
  def delete(txn: Transaction, key: Array[Byte], value: Array[Byte]): Boolean = {
    require(key != null, "key is null")
    require(value != null, "value is null")

    this._delete(txn, key, Option(value))
  }

  private def _delete(txn: Transaction, key: Array[Byte], value: Option[Array[Byte]]): Boolean = {
    _checkTxnThenDbi(txn)

    Env.LIB.mdbx_del(txn.txnPtr, dbiPtr, key, value)
    /*checkArgNotNull(txn, "txn")
    checkArgNotNull(key, "key")
    if (tx == null) return delete(key, value)
    checkArgNotNull(txn, "txn")
    val keyBuffer = NativeBuffer.create(key)
    try {
      val valueBuffer = NativeBuffer.create(value)
      try delete(txn, keyBuffer, valueBuffer)
      finally if (valueBuffer != null) valueBuffer.delete()
    } finally keyBuffer.delete()*/
  }

  /*private def delete(txn: Transaction, keyBuffer: NativeBuffer, valueBuffer: NativeBuffer) = delete(txn, new Value(keyBuffer), Value.create(valueBuffer))

  private def delete(txn: Transaction, keySlice: Value, valueSlice: Value): Boolean = {
    checkSize(env, keySlice)
    val hasSec = getSecondaries != null
    val valueSlices = new HashSet[Value]
    //here we just have a key w/o value, so all values must be deleted.
    //for secondaries where the value is the key, all such values must be retrieved and deleted
    if (valueSlice == null && hasSec) try {
      val cursor = openCursor(tx)
      try {
        val key = keySlice.toByteArray
        var entry = cursor.get(CursorOp.SET, key)
        while ( {entry != null}) {
          val valueBuffer = NativeBuffer.create(entry.getValue)
          valueSlices.add(Value.create(valueBuffer))
          if (getConfig(tx).isDupSort) entry = cursor.get(CursorOp.NEXT_DUP, key, entry.getValue)
          else entry = null
        }
      } finally if (cursor != null) cursor.close()
    }
    else valueSlices.add(valueSlice)
    val rc = mdbx_del(txn.pointer, pointer, keySlice, valueSlice)
    if (rc == MDBX_NOTFOUND) return false
    checkErrorCode(env, rc)
    deleteSecondaries(txn, keySlice, valueSlices)
    true
  }*/

  /*
  protected def deleteSecondaries(txn: Transaction, keySlice: Value, valueSlices: Set[Value]): Unit = {
    if (secondaries != null) {
      import scala.collection.JavaConversions._
      for (secDb <- secondaries) {
        val secConfig = secDb.getConfig.asInstanceOf[SecondaryDbConfig]
        val pKey = keySlice.toByteArray
        import scala.collection.JavaConversions._
        for (valueSlice <- valueSlices) {
          val secKey = secConfig.getKeyCreator.createSecondaryKey(secDb, pKey, valueSlice.toByteArray)
          secDb.delete(txn, secKey, pKey)
        }
      }
    }
  }*/

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
   * @param txn transaction handle
   * @return A new cursor handle
   */
  def openCursor(txn: Transaction): Cursor = {
    _checkTxnThenDbi(txn)

    val cursorPtr = Env.LIB.mdbx_cursor_open(txn.txnPtr, dbiPtr)

    new Cursor(env, cursorPtr, txn, this)
  }

  /*def openSecondaryCursor(txn: Transaction): SecondaryCursor = {
    checkArgNotNull(txn, "txn")
    val cursor = new Array[Long](1)
    checkErrorCode(env, mdbx_cursor_open(txn.pointer, pointer, cursor))
    new SecondaryCursor(env, cursor(0), txn, this)
  }

  def getFlags: Int = {
    val txn = env.createTransaction
    try getFlags(txn)
    finally txn.commit()
  }*/

  def getFlags(txn: Transaction): Int = {
    Env.LIB.mdbx_dbi_flags(txn.txnPtr, dbiPtr)
  }

  def getFlagsState(txn: Transaction): Database.FlagsAndState = {
    _checkTxnThenDbi(txn)

    val (flags, state) = Env.LIB.mdbx_dbi_flags_ex(txn.txnPtr, dbiPtr)

    val stateAsEnum = DbiState.valueOf(state)
    Database.FlagsAndState(flags, stateAsEnum)
  }

  /*
  def getDupsortDepthMask(txn: Transaction): Int = {
    val mask = new Array[Long](1)
    checkErrorCode(env, mdbx_dbi_dupsort_depthmask(txn.pointer, pointer, mask))
    mask(0).toInt
  }*/

  def getThenIncrementSequence(txn: Transaction, increment: Long): Long = {
    _checkTxnThenDbi(txn)

    val prevValue = Env.LIB.mdbx_dbi_sequence(txn.txnPtr, dbiPtr, increment)

    prevValue
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

  /*private[mdbx4s] def associate(txn: Transaction, secondary: SecondaryDatabase): Unit = {
    if (secondaries == null) secondaries = new ArrayList[SecondaryDatabase]
    secondaries.add(secondary)
  }*/

}
