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

/**
 * A cursor handle.
 *
 */
class Cursor private[mdbx4s](
  val env: Env,
  private var cursorPtr: Env.LIB.CursorHandle,
  private val tx: Transaction,
  private val db: Database
) extends  AutoCloseable {

  def database: Database = { // long dbi = mdb_cursor_dbi(pointer());
    db
  }

  def transaction: Transaction = { // long txn = mdb_cursor_txn(pointer());
    tx
  }

  def isClosed(): Boolean = { cursorPtr == null }

  @inline
  private def _checkCursorNotClosed(): Unit = {
    if (cursorPtr == null) throw new MdbxException("the database is closed")
  }

  /**
   * <p>
   * Close a cursor handle.
   * </p>
   *
   * The cursor handle will be freed and must not be used again after this call. Its transaction must still be
   * live if it is a write-transaction.
   */
  override def close(): Unit = {
    if (cursorPtr != null) {
      Env.LIB.mdbx_cursor_close(cursorPtr)
      cursorPtr = null
    }
  }

  /**
   * <p>
   * Renew a cursor handle.
   * </p>
   *
   * A cursor is associated with a specific transaction and database. Cursors that are only used in read-only
   * transactions may be re-used, to avoid unnecessary malloc/free overhead. The cursor may be associated with
   * a new read-only transaction, and referencing the same database handle as it was created with. This may be
   * done whether the previous transaction is live or dead.
   *
   * @param tx
   * transaction handle
   */
  def renew(tx: Transaction): Unit = {
    require(tx != null, "tx is null")
    require(!tx.isClosed(), "transaction is closed")
    _checkCursorNotClosed()
    Env.LIB.mdbx_cursor_renew(tx.txnPtr, cursorPtr)
  }

  /**
   * <p>
   * Retrieve by cursor.
   * </p>
   * This function retrieves key/data pairs from the database. The address and length of the key are returned
   * in the object to which \b key refers (except for the case of the #MDB_SET option, in which the \b key
   * object is unchanged), and the address and length of the data are returned in the object to which \b data
   *
   * @param op A cursor seek operation SeekOp.Op
   * @return tuple representing the key value pair (None if requested position not found)
   */
  def seek(op: CursorSeekOp.Op): Option[Tuple2[Array[Byte],Array[Byte]]] = {
    _checkCursorNotClosed()
    Env.LIB.mdbx_cursor_seek(cursorPtr, op.getCode())
  }

  /**
   * Position at first key/data item.
   *
   * @return tuple representing the key value pair (None if requested position not found)
   */
  def first(): Option[Tuple2[Array[Byte],Array[Byte]]] = seek(CursorSeekOp.MDBX_FIRST)


  /**
   * Position at next key/data item.
   *
   * @return tuple representing the key value pair (None if requested position not found)
   */
  def next(): Option[Tuple2[Array[Byte],Array[Byte]]] = seek(CursorSeekOp.MDBX_NEXT)

  def get(op: CursorGetOp.Op, key: Array[Byte]): Option[Array[Byte]] = {
    _checkCursorNotClosed()
    Env.LIB.mdbx_cursor_get(cursorPtr, key, op.getCode())
  }

  /*def get(op: CursorOp, key: Array[Byte], value: Array[Byte]): Entry = {
    checkArgNotNull(op, "op")
    val keyBuffer = NativeBuffer.create(key)
    val valBuffer = NativeBuffer.create(value)
    try {
      val keyValue = if (keyBuffer != null) new Value(keyBuffer)
      else new Value
      val valValue = if (valBuffer != null) new Value(valBuffer)
      else new Value
      val rc = mdbx_cursor_get(pointer, keyValue, valValue, op.getValue)
      if (rc == MDBX_NOTFOUND) return null
      checkErrorCode(env, rc)
      new Entry(keyValue.toByteArray, valValue.toByteArray)
    } finally {
      if (keyBuffer != null) keyBuffer.delete()
      if (valBuffer != null) valBuffer.delete()
    }
  }*/

  /*def get(op: CursorOp, key: DatabaseEntry, value: DatabaseEntry): OperationStatus = {
    checkArgNotNull(op, "op")
    val keyBuffer = NativeBuffer.create(key.getData)
    val valBuffer = NativeBuffer.create(value.getData)
    try {
      val keyValue = if (keyBuffer != null) new Value(keyBuffer)
      else new Value
      val valValue = if (valBuffer != null) new Value(valBuffer)
      else new Value
      val rc = mdbx_cursor_get(pointer, keyValue, valValue, op.getValue)
      if (rc == MDBX_NOTFOUND) return OperationStatus.NOTFOUND
      checkErrorCode(env, rc)
      key.setData(keyValue.toByteArray)
      value.setData(valValue.toByteArray)
      OperationStatus.SUCCESS
    } finally {
      if (keyBuffer != null) keyBuffer.delete()
      if (valBuffer != null) valBuffer.delete()
    }
  }*/

  /**
   * <p>
   * Store by cursor.
   * </p>
   *
   * @param key
   * The key operated on.
   * @param value
   * The data operated on.
   * @param flags
   * Options for this operation. This parameter must be set to 0 or one of the values described here.
   * <ul>
   * <li>CURRENT} - replace the item at the current cursor
   * position. The \b key parameter must still be provided, and must match it. If using sorted
   * duplicates (DUPSORT) the data item must still sort into
   * the same place. This is intended to be used when the new data is the same size as the old.
   * Otherwise it will simply perform a delete of the old record followed by an insert.
   * <li>NODUPDATA - enter the new key/data pair only if it
   * does not already appear in the database. This flag may only be specified if the database was
   * opened with DUPSORT. The function will return
   * KEYEXIST if the key/data pair already appears in
   * the database.
   * <li>NOOVERWRITE - enter the new key/data pair only if
   * the key does not already appear in the database. The function will return
   * KEYEXIST if the key already appears in the
   * database, even if the database supports duplicates (KEYEXIST).
   *
   * <li>RESERVE} - reserve space for data of the given size,
   * but don't copy the given data. Instead, return a pointer to the reserved space, which the caller
   * can fill in later. This saves an extra memcpy if the data is being generated later.
   * <li>APPEND - append the given key/data pair to the end
   * of the database. No key comparisons are performed. This option allows fast bulk loading when
   * keys are already known to be in the correct order. Loading unsorted keys with this flag will
   * cause data corruption.
   * <li>APPENDDUP - as above, but for sorted dup data.
   * <li>MULTIPLE - store multiple contiguous data elements
   * in a single request. This flag may only be specified if the database was opened with
   * DUPFIXED. The \b data argument must be an array of two
   * MDB_vals. The mv_size of the first MDB_val must be the size of a single data element. The
   * mv_data of the first MDB_val must point to the beginning of the array of contiguous data
   * elements. The mv_size of the second MDB_val must be the count of the number of data elements to
   * store. On return this field will be set to the count of the number of elements actually written.
   * The mv_data of the second MDB_val is unused.
   * </ul>
   * @return the value that was stored
   */
  def put(key: Array[Byte], value: Array[Byte], flags: PutFlags.Flag*): Option[Array[Byte]] = {
    require(key != null, "key is null")
    require(value != null, "value is null")

    Env.LIB.mdbx_cursor_put(cursorPtr, key, value, MaskedFlag.mask(flags))
  }

  /*private def _put(keyBuffer: NativeBuffer, valueBuffer: NativeBuffer, flags: Int) = _put(new Value(keyBuffer), new Value(valueBuffer), flags)

  private def _put(keySlice: Value, valueSlice: Value, flags: Int) = {
    val hasSec = db.getSecondaries != null
    val valueSlices = new HashSet[Value]
    if (hasSec && (flags & MDBX_NOOVERWRITE) == 0 && (flags & MDBX_NODUPDATA) == 0) try {
      val cursor = db.openCursor(tx)
      try {
        val key = keySlice.toByteArray
        val entry = cursor.get(CursorOp.SET, key)
        if (entry != null) {
          val valueBuffer = NativeBuffer.create(entry.getValue)
          valueSlices.add(Value.create(valueBuffer))
        }
      } finally if (cursor != null) cursor.close()
    }
    val rc = mdbx_cursor_put(pointer, keySlice, valueSlice, flags)
    if (((flags & MDBX_NOOVERWRITE) != 0 || (flags & MDBX_NODUPDATA) != 0) && rc == MDBX_KEYEXIST) { // Return the existing value if it was a dup insert attempt.
      valueSlice.toByteArray
    }
    else { // If the put failed, throw an exception..
      checkErrorCode(env, rc)
      if (!valueSlices.isEmpty) db.deleteSecondaries(tx, keySlice, valueSlices)
      db.putSecondaries(tx, keySlice, valueSlice)
      valueSlice.toByteArray
    }
  }*/

  /**
   * <p>
   * Delete current key/data pair.
   * </p>
   *
   * This function deletes the key/data pair to which the cursor refers.
   */
  def delete(): Unit = {
    _checkCursorNotClosed()
    Env.LIB.mdbx_cursor_del(cursorPtr, 0)
    /*val hasSec = db.getSecondaries != null
    var entry = null
    if (hasSec) entry = get(CursorOp.GET_CURRENT)
    val rc = mdbx_cursor_del(pointer, 0)
    checkErrorCode(env, rc)
    if (hasSec) {
      import scala.collection.JavaConversions._
      for (secDb <- db.getSecondaries) {
        val secConfig = secDb.getConfig.asInstanceOf[SecondaryDbConfig]
        val pKey = entry.getKey
        val secKey = secConfig.getKeyCreator.createSecondaryKey(secDb, pKey, entry.getValue)
        secDb.delete(tx, secKey, pKey)
      }
    }*/
  }

  /**
   * <p>
   * Delete current key/data pair.
   * </p>
   *
   * This function deletes all of the data items for the current key.
   *
   * May only be called if the database was opened with DUPSORT.
   */
  def deleteIncludingDups(): Unit = {
    _checkCursorNotClosed()
    Env.LIB.mdbx_cursor_del(cursorPtr, PutFlags.MDBX_NODUPDATA.getMask())
  }

  /**
   * <p>
   * Return count of duplicates for current key.
   * </p>
   *
   * This call is only valid on databases that support sorted duplicate data items (DUPSORT).
   *
   * @return count of duplicates for current key
   */
  def count(): Long = {
    _checkCursorNotClosed()
    Env.LIB.mdbx_cursor_count(cursorPtr)
  }


}