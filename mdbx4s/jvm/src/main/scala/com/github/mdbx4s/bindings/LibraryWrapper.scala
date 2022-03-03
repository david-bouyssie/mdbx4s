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
package com.github.mdbx4s.bindings

import scala.collection.mutable.LongMap
import scala.language.implicitConversions

import com.castortech.mdbxjni.{JNI => LIB, KeyOrValue, NativeBufferFactory}
import com.github.mdbx4s._

object LibraryWrapper {
  private lazy val singleton = new LibraryWrapper()

  def getWrapper(): ILibraryWrapper = singleton
}

class LibraryWrapper private() extends ILibraryWrapper {

  sealed trait ICursorHandle
  type CursorHandle = NativeObject with ICursorHandle

  sealed trait IDbiHandle
  type DbiHandle = NativeObject with IDbiHandle // CUnsignedInt intentionally wrapped

  sealed trait IEnvHandle
  type EnvHandle = NativeObject with IEnvHandle

  sealed trait ITxnHandle
  type TxnHandle = NativeObject with ITxnHandle

  import com.github.mdbx4s.ReturnCode._

  protected val errorBuilderByErrorCode: LongMap[String => MdbxNativeException] = new LongMap[String => MdbxNativeException] ++ Seq(
    LIB.MDBX_EINVAL -> { rcMsg: String => new MdbxNativeException(LIB.MDBX_EINVAL, rcMsg, "Invalid Parameter") },
    LIB.MDBX_EACCESS -> { rcMsg: String => new MdbxNativeException(LIB.MDBX_EACCESS, rcMsg, "Access Denied") },
    LIB.MDBX_ENODATA -> { rcMsg: String => new MdbxNativeException(LIB.MDBX_ENODATA, rcMsg, "Handle EOF") },
    LIB.MDBX_ENOMEM -> { rcMsg: String => new MdbxNativeException(LIB.MDBX_ENOMEM, rcMsg, "Out of Memory") },
    LIB.MDBX_EROFS -> { rcMsg: String => new MdbxNativeException(LIB.MDBX_EROFS, rcMsg, "File Read Only") },
    LIB.MDBX_ENOSYS -> { rcMsg: String => new MdbxNativeException(LIB.MDBX_ENOSYS, rcMsg, "Not Supported") },
    LIB.MDBX_EIO -> { rcMsg: String => new MdbxNativeException(LIB.MDBX_EIO, rcMsg, "Write Fault") },
    LIB.MDBX_EPERM -> { rcMsg: String => new MdbxNativeException(LIB.MDBX_EPERM, rcMsg, "Invalid Function") },
    LIB.MDBX_EINTR -> { rcMsg: String => new MdbxNativeException(LIB.MDBX_EINTR, rcMsg, "Cancelled") },
    LIB.MDBX_ENOFILE -> { rcMsg: String => new MdbxNativeException(LIB.MDBX_ENOFILE, rcMsg, "?? no description") },
    LIB.MDBX_EREMOTE -> { rcMsg: String => new MdbxNativeException(LIB.MDBX_EREMOTE, rcMsg, "?? no description") },
    MDBX_KEYEXIST -> { rcMsg: String => new MdbxNativeException(MDBX_KEYEXIST, rcMsg, "key/data pair already exists") },
    MDBX_NOTFOUND -> { rcMsg: String => new MdbxNativeException(MDBX_NOTFOUND, rcMsg, "key/data pair not found (EOF)") },
    MDBX_PAGE_NOTFOUND -> { rcMsg: String => new MdbxNativeException(MDBX_PAGE_NOTFOUND, rcMsg, "Requested page not found - this usually indicates corruption") },
    MDBX_CORRUPTED -> { rcMsg: String => new MdbxNativeException(MDBX_CORRUPTED, rcMsg, "Located page was wrong type") },
    MDBX_PANIC -> { rcMsg: String => new MdbxNativeException(MDBX_PANIC, rcMsg, "Update of meta page failed or environment had fatal error") },
    MDBX_VERSION_MISMATCH -> { rcMsg: String => new MdbxNativeException(MDBX_VERSION_MISMATCH, rcMsg, "DB file version mismatch with libmdbx") },
    MDBX_INVALID -> { rcMsg: String => new MdbxNativeException(MDBX_INVALID, rcMsg, "File is not a valid MDBX file") },
    MDBX_MAP_FULL -> { rcMsg: String => new MdbxNativeException(MDBX_MAP_FULL, rcMsg, "Environment mapsize reached") },
    MDBX_DBS_FULL -> { rcMsg: String => new MdbxNativeException(MDBX_DBS_FULL, rcMsg, "Environment maxdbs reached") },
    MDBX_READERS_FULL -> { rcMsg: String => new MdbxNativeException(MDBX_READERS_FULL, rcMsg, "Environment maxreaders reached") },
    MDBX_TXN_FULL -> { rcMsg: String => new MdbxNativeException(MDBX_TXN_FULL, rcMsg, "Transaction has too many dirty pages") },
    MDBX_CURSOR_FULL -> { rcMsg: String => new MdbxNativeException(MDBX_CURSOR_FULL, rcMsg, "Cursor stack too deep - internal error") },
    MDBX_PAGE_FULL -> { rcMsg: String => new MdbxNativeException(MDBX_PAGE_FULL, rcMsg, "Page has not enough space - internal error") },
    MDBX_INCOMPATIBLE -> { rcMsg: String => new MdbxNativeException(MDBX_INCOMPATIBLE, rcMsg, "Operation and DB incompatible, or DB type changed.") },
    MDBX_BAD_RSLOT -> { rcMsg: String => new MdbxNativeException(MDBX_BAD_RSLOT, rcMsg, "Invalid reuse of reader locktable slot") },
    MDBX_BAD_TXN -> { rcMsg: String => new MdbxNativeException(MDBX_BAD_TXN, rcMsg, "Transaction must abort, has a child, or is invalid") },
    MDBX_BAD_VALSIZE -> { rcMsg: String => new MdbxNativeException(MDBX_BAD_VALSIZE, rcMsg, "Unsupported size of key/DB name/data, or wrong DUPFIXED size") },
    MDBX_BAD_DBI -> { rcMsg: String => new MdbxNativeException(MDBX_BAD_DBI, rcMsg, "The specified DBI was changed unexpectedly") },
    MDBX_PROBLEM -> { rcMsg: String => new MdbxNativeException(MDBX_PROBLEM, rcMsg, "Unexpected problem - Transaction should abort") },
    MDBX_BUSY -> { rcMsg: String => new MdbxNativeException(MDBX_BUSY, rcMsg, "Another write transaction is running") },
    MDBX_EMULTIVAL -> { rcMsg: String => new MdbxNativeException(MDBX_EMULTIVAL, rcMsg, "The mdbx_put() or mdbx_replace() was called for key, that has more that one associated value.") },
    MDBX_EBADSIGN -> { rcMsg: String => new MdbxNativeException(MDBX_EBADSIGN, rcMsg, "Bad signature of a runtime object(s), this can mean: - memory corruption or double-free; - ABI version mismatch (rare case)") },
    MDBX_WANNA_RECOVERY -> { rcMsg: String => new MdbxNativeException(MDBX_WANNA_RECOVERY, rcMsg, "Database should be recovered, but this could NOT be done automatically right now (e.g. in readonly mode and so forth).") },
    MDBX_EKEYMISMATCH -> { rcMsg: String => new MdbxNativeException(MDBX_EKEYMISMATCH, rcMsg, "The given key value is mismatched to the current cursor position, when mdbx_cursor_put() called with MDBX_CURRENT option.") },
    MDBX_TOO_LARGE -> { rcMsg: String => new MdbxNativeException(MDBX_TOO_LARGE, rcMsg, "Database is too large for current system, e.g. could NOT be mapped into RAM.") },
    MDBX_THREAD_MISMATCH -> { rcMsg: String => new MdbxNativeException(MDBX_THREAD_MISMATCH, rcMsg, "A thread has attempted to use a not owned object, e.g. a transaction that started by another thread.") },
    MDBX_RESULT_FALSE -> { rcMsg: String => new MdbxNativeException(MDBX_RESULT_FALSE, rcMsg, "Alias for Successful result") },
    MDBX_RESULT_TRUE -> { rcMsg: String => new MdbxNativeException(MDBX_RESULT_TRUE, rcMsg, "Successful result with special meaning or a flag") },
    //MDBX_FIRST_LMDB_ERRCODE -> { rcMsg: String => new MdbxNativeException(MDBX_FIRST_LMDB_ERRCODE, rcMsg, "The first LMDB-compatible defined error code") },
    MDBX_UNABLE_EXTEND_MAPSIZE -> { rcMsg: String => new MdbxNativeException(MDBX_UNABLE_EXTEND_MAPSIZE, rcMsg, "Database engine was unable to extend mapping, e.g. since address space is unavailable or busy") },
    //MDBX_LAST_LMDB_ERRCODE -> { rcMsg: String => new MdbxNativeException(MDBX_LAST_LMDB_ERRCODE, rcMsg, "The last LMDB-compatible defined error code") },
    //MDBX_FIRST_ADDED_ERRCODE -> { rcMsg: String => new MdbxNativeException(MDBX_FIRST_ADDED_ERRCODE, rcMsg, "The first of MDBX-added error codes") },
    MDBX_TXN_OVERLAPPING -> { rcMsg: String => new MdbxNativeException(MDBX_TXN_OVERLAPPING, rcMsg, "Overlapping read and write transactions for the current thread") }
    //MDBX_LAST_ADDED_ERRCODE -> { rcMsg: String => new MdbxNativeException(MDBX_LAST_ADDED_ERRCODE, rcMsg, "The last added error code") }
  ).map(kv => (kv._1.toLong, kv._2))

  implicit def nativeObject2pointer(nativeObject: NativeObject): Long = {
    if (nativeObject == null) 0L else nativeObject.pointer
  }

  def mdbx_build_info(): BuildInfo = {
    //val build_info_struct = JNI.MDBX_build_info
    BuildInfo(
      datetime = "",
      target = "",
      options = "",
      compiler = "",
      flags = ""
    )
  }

  def mdbx_cursor_close(cursor: CursorHandle): Unit = {
    LIB.mdbx_cursor_close(cursor)
  }
  def mdbx_cursor_count(cursor: CursorHandle): Long = {
    val countPtr = new Array[Long](1)
    checkRc(LIB.mdbx_cursor_count(cursor, countPtr))
    countPtr.head
  }
  def mdbx_cursor_del(cursorPtr: CursorHandle, flags: Int): Unit = {
    checkRc(LIB.mdbx_cursor_del(cursorPtr, flags))
  }
  def mdbx_cursor_get(cursorPtr: CursorHandle, key: Array[Byte], cursorOp: Int): Option[Array[Byte]] = {
    val keyBuffer = NativeBufferFactory.create(key)
    //val valBuffer = NativeBufferFactory.create(value)

    try {
      val keyPtr = if (keyBuffer.isNull()) new KeyOrValue() else new KeyOrValue(keyBuffer)

      /*val valValue = if (valBuffer != null) new Value(valBuffer)
      else new Value*/

      _mdbx_cursor_get(cursorPtr, keyPtr, new KeyOrValue(), cursorOp)

      Some(keyPtr.toByteArray)
    } finally {
      keyBuffer.delete()
    }
  }
  def mdbx_cursor_seek(cursorPtr: CursorHandle, cursorOp: Int): Option[Tuple2[Array[Byte],Array[Byte]]] = {
    val keyPtr = new KeyOrValue()
    val valuePtr = new KeyOrValue()
    val gotResult = _mdbx_cursor_get(cursorPtr, keyPtr, valuePtr, cursorOp)
    if (!gotResult) return None

    Some(keyPtr.toByteArray, valuePtr.toByteArray)
  }
  def _mdbx_cursor_get(cursorPtr: CursorHandle, k: KeyOrValue, v: KeyOrValue, cursorOp: Int): Boolean = {
    val rc = LIB.mdbx_cursor_get(cursorPtr, k, v, cursorOp)
    if (rc == MDBX_NOTFOUND) return false

    checkRc(rc)

    true
  }
  def mdbx_cursor_open(txnPtr: TxnHandle, dbiPtr: DbiHandle): CursorHandle = {
    val cursorPtr = new Array[Long](1)
    checkRc(LIB.mdbx_cursor_open(txnPtr, dbiPtr, cursorPtr))
    new NativeObject(cursorPtr.head).asInstanceOf[CursorHandle]
  }
  def mdbx_cursor_put(cursorPtr: CursorHandle, key: Array[Byte], value: Array[Byte], flags: Int): Option[Array[Byte]] = {
    val keyBuffer = NativeBufferFactory.create(key)

    try {
      val valBuffer = NativeBufferFactory.create(value)
      try {
        val valuePtr = new KeyOrValue(valBuffer)
        val isPutOK = _mdbx_cursor_put(cursorPtr, new KeyOrValue(keyBuffer), valuePtr, flags)
        if (isPutOK) return None

        // Return the existing value (meaning it was a dup insert attempt)
        Some(valuePtr.toByteArray)
      }
      finally {
        valBuffer.delete()
      }
    } finally {
      keyBuffer.delete()
    }
  }
  private def _mdbx_cursor_put(cursorPtr: CursorHandle, key: KeyOrValue, data: KeyOrValue, flags: Int): Boolean = {
    val rc = LIB.mdbx_cursor_put(cursorPtr, key, data, flags)

    isPutOK(rc, flags)
  }
  def mdbx_cursor_renew(txnPtr: TxnHandle, cursorPtr: CursorHandle): Unit = {
    checkRc(LIB.mdbx_cursor_renew(txnPtr, cursorPtr))
  }

  /* --- Wrappers for Dbi functions --- */
  def mdbx_dbi_close(envPtr: EnvHandle, dbiPtr: DbiHandle): Unit = {
    LIB.mdbx_dbi_close(envPtr, dbiPtr)
  }

  def mdbx_dbi_flags(txnPtr: TxnHandle, dbiPtr: DbiHandle): Int = {
    val flagsPtr = new Array[Long](1)
    checkRc(LIB.mdbx_dbi_flags(txnPtr, dbiPtr, flagsPtr))
    flagsPtr.head.toInt
  }

  def mdbx_dbi_flags_ex(txnPtr: TxnHandle, dbiPtr: DbiHandle): (Int,Int) = {
    val flagsPtr = new Array[Long](1)
    val statePtr = new Array[Long](1)
    checkRc(LIB.mdbx_dbi_flags_ex(txnPtr, dbiPtr, flagsPtr, statePtr))
    (flagsPtr.head.toInt, statePtr.head.toInt)
  }

  def mdbx_dbi_open(txnPtr: TxnHandle, name: String, flags: Int): DbiHandle = {
    val dbiPtr = new Array[Long](1)

    checkRc(LIB.mdbx_dbi_open(txnPtr, name, flags, dbiPtr))

    new NativeObject(dbiPtr.head).asInstanceOf[DbiHandle]
  }

  def mdbx_dbi_sequence(txnPtr: TxnHandle, dbiPtr: DbiHandle, increment: Long): Long = {
    val resultPtr = new Array[Long](1)
    val rc = LIB.mdbx_dbi_sequence(txnPtr, dbiPtr, resultPtr, increment)

    checkRc(rc)

    resultPtr.head
  }

  def mdbx_dbi_stat(txnPtr: TxnHandle, dbiPtr: DbiHandle): Stat = {
    val stat = new LIB.MDBX_stat()
    checkRc(LIB.mdbx_dbi_stat(txnPtr, dbiPtr, stat, LIB.SIZEOF_STAT))

    _mdbx_stat_struct_2_object(stat)
  }

  @inline
  private def _mdbx_stat_struct_2_object(stat: LIB.MDBX_stat): Stat = {
    Stat(
      stat.ms_psize.toInt,
      stat.ms_depth.toInt,
      stat.ms_branch_pages,
      stat.ms_leaf_pages,
      stat.ms_overflow_pages,
      stat.ms_entries
    )
  }

  def mdbx_del(txnPtr: TxnHandle, dbiPtr: DbiHandle, key: Array[Byte], value: Option[Array[Byte]]): Boolean = {

    val keyBuffer = NativeBufferFactory.create(key)
    try {
      val valBufferOpt = value.map(NativeBufferFactory.create)
      try {
        val keyPtr = new KeyOrValue(keyBuffer)
        val valuePtrOpt = valBufferOpt.map(new KeyOrValue(_))
        if (valuePtrOpt.isEmpty) {
          _mdbx_del(txnPtr, dbiPtr, keyPtr, null)
        } else {
          _mdbx_del(txnPtr, dbiPtr, keyPtr, valuePtrOpt.get)
        }
      }
      finally {
        if (valBufferOpt.nonEmpty) valBufferOpt.get.delete()
      }
    } finally {
      keyBuffer.delete()
    }
  }

  private def _mdbx_del(txnPtr: TxnHandle, dbiPtr: DbiHandle, key: KeyOrValue, data: KeyOrValue): Boolean = {
    val rc = LIB.mdbx_del(txnPtr, dbiPtr, key, data)
    if (rc == MDBX_NOTFOUND) return false

    checkRc(rc)

    true
  }

  def mdbx_drop(txnPtr: TxnHandle, dbiPtr: DbiHandle, del: Boolean): Unit = {
    checkRc(LIB.mdbx_drop(txnPtr, dbiPtr, if (del) 1 else 0))
  }

  def mdbx_env_close(envPtr: EnvHandle): Unit = checkRc(LIB.mdbx_env_close(envPtr))
  def mdbx_env_close_ex(envPtr: EnvHandle, dontSync: Boolean): Unit = {
    checkRc(LIB.mdbx_env_close_ex(envPtr, if (dontSync) 1 else 0))
  }
  def mdbx_env_copy(envPtr: EnvHandle, path: java.io.File, flags: Int): Unit = {
    val rc = LIB.mdbx_env_copy(envPtr, path.getAbsolutePath, flags)
    checkRc(rc)
  }
  def mdbx_env_create(): EnvHandle = {
    val envPtr = new Array[Long](1)
    checkRc(LIB.mdbx_env_create(envPtr))

    new NativeObject(envPtr.head).asInstanceOf[EnvHandle]
  }
  //def mdbx_env_get_fd(@In env: Pointer, @In fd: Pointer): Int
  def mdbx_env_get_flags(envPtr: EnvHandle): Int = {
    val flagsPtr = new Array[Long](1)
    checkRc(LIB.mdbx_env_get_flags(envPtr, flagsPtr))

    flagsPtr.head.toInt
  }

  def mdbx_env_get_maxreaders(envPtr: EnvHandle): Int = {
    val maxReadersPtr = new Array[Long](1)
    checkRc(LIB.mdbx_env_get_maxreaders(envPtr, maxReadersPtr))

    maxReadersPtr.head.toInt
  }
  // FIXME: mdbx_env_get_maxkeysize_ex is missing in MDBXJNI
  def mdbx_env_get_maxkeysize_ex(envPtr: EnvHandle, flags: Int): Int = {
    LIB.mdbx_env_get_maxkeysize(envPtr)
  }

  def mdbx_env_get_maxdbs(envPtr: EnvHandle): Int = {
    val maxDbsPtr = new Array[Long](1)
    checkRc(LIB.mdbx_env_get_maxdbs(envPtr, maxDbsPtr))

    maxDbsPtr.head.toInt
  }

  def mdbx_env_info_ex(envPtr: EnvHandle, txnPtr: TxnHandle): EnvInfo = {
    val info = new LIB.MDBX_envinfo
    val rc = LIB.mdbx_env_info_ex(envPtr, txnPtr, info, LIB.SIZEOF_ENVINFO)

    checkRc(rc)

    EnvInfo(
      geo = EnvInfo.Geo(
        lower = info.mi_geo_lower,
        upper = info.mi_geo_upper,
        current = info.mi_geo_current,
        shrink = info.mi_geo_shrink,
        grow = info.mi_geo_grow
      ),
      mapSize = info.mi_mapsize,
      lastPgNo = info.mi_last_pgno,
      recentTxnId = info.mi_recent_txnid,
      latterReaderTxnId = info.mi_latter_reader_txnid,
      selfLatterReaderTxnId = info.mi_self_latter_reader_txnid,
      //meta: EnvInfo#Meta,
      maxReaders = info.mi_maxreaders.toInt,
      numReaders = info.mi_numreaders.toInt,
      dxbPageSize = info.mi_dxb_pagesize.toInt,
      sysPageSize = info.mi_sys_pagesize.toInt,
      //bootId: EnvInfo#BootId,
      unsyncVolume = info.mi_unsync_volume,
      autosyncThreshold = info.mi_autosync_threshold,
      sinceSyncSeconds16dot16 = info.mi_since_sync_seconds16dot16.toInt,
      autosyncPeriodSeconds16dot16 = info.mi_autosync_period_seconds16dot16.toInt,
      sinceReaderCheckSeconds16dot16 = info.mi_since_reader_check_seconds16dot16.toInt,
      mode = info.mi_mode.toInt
      //pgopStat: EnvInfo#PgopStat
    )
  }

  def mdbx_env_open(envPtr: EnvHandle, path: java.io.File, flags: Int, mode: Int): Unit = {
    val rc = LIB.mdbx_env_open(envPtr, path.getAbsolutePath, flags, mode)
    checkRc(rc)
  }
  //def mdb_env_set_flags(@In env: Pointer, flags: Int, onoff: Int): Int
  def mdbx_env_set_mapsize(envPtr: EnvHandle, size: Long): Unit = checkRc(LIB.mdbx_env_set_mapsize(envPtr, size))
  def mdbx_env_set_maxdbs(envPtr: EnvHandle, dbs: Int): Unit = checkRc(LIB.mdbx_env_set_maxdbs(envPtr, dbs))
  def mdbx_env_set_maxreaders(envPtr: EnvHandle, readers: Int): Unit = checkRc(LIB.mdbx_env_set_maxreaders(envPtr, readers))
  def mdbx_env_stat(envPtr: EnvHandle): Stat = {
    val stat = new LIB.MDBX_stat
    checkRc(LIB.mdbx_env_stat(envPtr, stat, LIB.SIZEOF_STAT))

    _mdbx_stat_struct_2_object(stat)
  }

  def mdbx_env_stat_ex(envPtr: EnvHandle, txnPtr: TxnHandle): Stat = {
    val stat = new LIB.MDBX_stat
    checkRc(LIB.mdbx_env_stat_ex(envPtr, txnPtr, stat, LIB.SIZEOF_STAT))

    _mdbx_stat_struct_2_object(stat)
  }

  def mdbx_env_sync_ex(envPtr: EnvHandle, force: Boolean, nonblock: Boolean): Unit = {
    checkRc(LIB.mdbx_env_sync_ex(envPtr, if (force) 1 else 0, if (nonblock) 1 else 0))
  }

  // --- LIMITS --- //
  def mdbx_limits_pgsize_min(): Long = LIB.mdbx_limits_pgsize_min()
  def mdbx_limits_pgsize_max(): Long = LIB.mdbx_limits_pgsize_max()
  def mdbx_limits_dbsize_min(page_size: Long): Long = LIB.mdbx_limits_dbsize_min(page_size)
  def mdbx_limits_dbsize_max(page_size: Long): Long = LIB.mdbx_limits_dbsize_max(page_size)
  def mdbx_limits_txnsize_max(page_size: Long): Long = LIB.mdbx_limits_txnsize_max(page_size)
  def mdbx_limits_keysize_max(page_size: Long, flags: Int): Long = LIB.mdbx_limits_keysize_max(page_size, flags)
  def mdbx_limits_valsize_max(page_size: Long, flags: Int): Long = LIB.mdbx_limits_valsize_max(page_size, flags)
  def mdbx_get_sysraminfo(): RamInfo = {
    val pageSizePtr = new Array[Long](1)
    val totalPagesPtr = new Array[Long](1)
    val availPagesPtr = new Array[Long](1)
    checkRc(LIB.mdbx_get_sysraminfo(pageSizePtr, totalPagesPtr, availPagesPtr))

    RamInfo(pageSizePtr.head, totalPagesPtr.head, availPagesPtr.head)
  }

  def mdbx_get(txnPtr: TxnHandle, dbiPtr: DbiHandle, key: Array[Byte]): Option[Array[Byte]] = {
    val keyBuffer = NativeBufferFactory.create(key)
    try {
      val keyPtr = new KeyOrValue(keyBuffer)
      val valuePtr = new KeyOrValue()
      val gotResult = _mdbx_get(txnPtr, dbiPtr, keyPtr, valuePtr)
      if (!gotResult) return None

      Some(valuePtr.toByteArray)
    }
    finally keyBuffer.delete()
  }

  private def _mdbx_get(txnPtr: TxnHandle, dbiPtr: DbiHandle, key: KeyOrValue, data: KeyOrValue): Boolean = {
    val rc = LIB.mdbx_get(txnPtr, dbiPtr, key, data)
    if (rc == MDBX_NOTFOUND) return false

    checkRc(rc)

    true
  }

  def mdbx_put(txnPtr: TxnHandle, dbiPtr: DbiHandle, key: Array[Byte], value: Array[Byte], flags: Int): Option[Array[Byte]] = {

    val keyBuffer = NativeBufferFactory.create(key)

    try {
      val valBuffer = NativeBufferFactory.create(value)
      try {
        val valuePtr = new KeyOrValue(valBuffer)
        val isPutOK = _mdbx_put(txnPtr, dbiPtr, new KeyOrValue(keyBuffer), valuePtr, flags)
        if (isPutOK) return None

        // Return the existing value (meaning it was a dup insert attempt)
        Some(valuePtr.toByteArray)
      }
      finally {
        valBuffer.delete()
      }
    } finally {
      keyBuffer.delete()
    }

  }

  private def _mdbx_put(txnPtr: TxnHandle, dbiPtr: DbiHandle, key: KeyOrValue, data: KeyOrValue, flags: Int): Boolean = {
    val rc = LIB.mdbx_put(txnPtr, dbiPtr, key, data, flags)

    isPutOK(rc, flags)
  }

  def mdbx_replace(txn: TxnHandle, dbiPtr: DbiHandle, key: Array[Byte], new_value: Array[Byte], flags: Int): Array[Byte] = {

    val keyBuffer = NativeBufferFactory.create(key)

    try {
      val newValueBuffer = NativeBufferFactory.create(new_value)
      try {
        val newValuePtr = new KeyOrValue(newValueBuffer)
        // Allocate buffer with 50% larger than new value which should be enough to avoid retry
        val oldValueBuffer = NativeBufferFactory.create((newValuePtr.iov_len * 1.5).toLong)
        try {
          // FIXME: we should implement a retry strategy as done in MDBX JNI
          val oldValuePtr = new KeyOrValue(oldValueBuffer)
          val rc = LIB.mdbx_replace(txn, dbiPtr, new KeyOrValue(keyBuffer), newValuePtr, oldValuePtr, flags)
          checkRc(rc)

          // Return the old value
          oldValuePtr.toByteArray
        } finally {
          oldValueBuffer.delete()
        }
      }
      finally {
        newValueBuffer.delete()
      }
    } finally {
      keyBuffer.delete()
    }
  }

  def mdbx_strerror(rc: Int): String = _ptr2string(LIB.mdbx_strerror(rc))

  /* --- Wrappers for TXN functions --- */
  def mdbx_txn_abort(txnPtr: TxnHandle): Unit = {
    //println("Txn is being aborted")
    LIB.mdbx_txn_abort(txnPtr)
  }

  def mdbx_txn_begin(env: EnvHandle, txnParentPtr: TxnHandle, flags: Int): TxnHandle = {
    val txnPtr = new Array[Long](1)

    //println(s"Txn is beginning (is parent null = ${txnParentPtr == null}, flags = $flags)")
    val rc = LIB.mdbx_txn_begin(env, txnParentPtr, flags, txnPtr)
    checkRc(rc)

    //println("Txn has begun")
    new NativeObject(txnPtr.head).asInstanceOf[TxnHandle]
  }

  def mdbx_txn_commit(txnPtr: TxnHandle): Unit = {
    //println("Txn is being committed")
    checkRc(LIB.mdbx_txn_commit(txnPtr))
  }

  def mdbx_txn_commit_ex(txnPtr: TxnHandle): CommitLatency = {
    val latency_struct = new LIB.MDBX_commit_latency
    checkRc(LIB.mdbx_txn_commit_ex(txnPtr, latency_struct))

    CommitLatency(
      latency_struct.preparation,
      latency_struct.gc,
      latency_struct.audit,
      latency_struct.write,
      latency_struct.sync,
      latency_struct.ending,
      latency_struct.whole
    )
  }

  //def mdb_txn_env(txnPtr: TxnHandle): Pointer = LIB.mdb_txn_env(txn)
  def mdbx_txn_id(txnPtr: TxnHandle): Long = LIB.mdbx_txn_id(txnPtr)
  def mdbx_txn_info(txnPtr: TxnHandle, scanRlt: Boolean): TxnInfo = {
    val info_struct = new LIB.MDBX_txn_info
    checkRc(LIB.mdbx_txn_info(txnPtr, info_struct, if (scanRlt) 1 else 0))

    TxnInfo(
      info_struct.txn_id,
      info_struct.txn_reader_lag,
      info_struct.txn_space_used,
      info_struct.txn_space_limit_soft,
      info_struct.txn_space_limit_hard,
      info_struct.txn_space_retired,
      info_struct.txn_space_leftover,
      info_struct.txn_space_dirty
    )
  }
  def mdbx_txn_renew(txnPtr: TxnHandle): Unit = checkRc(LIB.mdbx_txn_renew(txnPtr))
  def mdbx_txn_reset(txnPtr: TxnHandle): Unit = LIB.mdbx_txn_reset(txnPtr)

  def mdbx_version(): Version = {
    // FIXME: can't retrieve the version information
    //val version_struct = LIB.mdbx_version

    Version(0,0,0,0)
  }

  private def _ptr2string(ptr: Long): String = {
    if (ptr == 0) return null

    new String(
      NativeBufferFactory.create(ptr, com.castortech.mdbxjni.JNI.strlen(ptr)).toByteArray,
      java.nio.charset.Charset.defaultCharset
    )
  }
}
