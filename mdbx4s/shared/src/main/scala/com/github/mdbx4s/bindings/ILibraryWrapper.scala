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

import java.io.File

import com.github.mdbx4s._

trait ILibraryWrapper {

  type CursorHandle >: Null
  type DbiHandle >: Null
  type EnvHandle >: Null
  type TxnHandle >: Null

  //def CONSTANTS: ILibraryConstants

  def mdbx_build_info(): BuildInfo

  /* --- Wrappers for Cursor functions --- */
  def mdbx_cursor_close(cursorPtr: CursorHandle): Unit
  def mdbx_cursor_count(cursorPtr: CursorHandle): Long
  def mdbx_cursor_del(cursorPtr: CursorHandle, flags: Int): Unit
  def mdbx_cursor_get(cursorPtr: CursorHandle, key: Array[Byte], cursorOp: Int): Option[Array[Byte]]
  def mdbx_cursor_seek(cursorPtr: CursorHandle, cursorOp: Int): Option[Tuple2[Array[Byte],Array[Byte]]]
  def mdbx_cursor_open(txnPtr: TxnHandle, dbiPtr: DbiHandle): CursorHandle
  def mdbx_cursor_put(cursorPtr: CursorHandle, key: Array[Byte], value: Array[Byte], flags: Int): Option[Array[Byte]]
  def mdbx_cursor_renew(txnPtr: TxnHandle, cursorPtr: CursorHandle): Unit

  /* --- Wrappers for Dbi functions --- */
  def mdbx_dbi_close(envPtr: EnvHandle, dbiPtr: DbiHandle): Unit
  def mdbx_dbi_flags(txnPtr: TxnHandle, dbiPtr: DbiHandle): Int
  def mdbx_dbi_flags_ex(txnPtr: TxnHandle, dbiPtr: DbiHandle): (Int,Int)
  def mdbx_dbi_open(txnPtr: TxnHandle, name: String, flags: Int): DbiHandle
  def mdbx_dbi_sequence(txnPtr: TxnHandle, dbiPtr: DbiHandle, increment: Long): Long
  def mdbx_dbi_stat(txnPtr: TxnHandle, dbiPtr: DbiHandle): Stat

  /* --- Wrappers for misc. functions --- */
  def mdbx_del(txnPtr: TxnHandle, dbiPtr: DbiHandle, key: Array[Byte], value: Option[Array[Byte]]): Boolean
  def mdbx_drop(txnPtr: TxnHandle, dbiPtr: DbiHandle, del: Boolean): Unit

  /* --- Wrappers for Env functions --- */
  def mdbx_env_get_flags(envPtr: EnvHandle): Int
  def mdbx_env_get_maxreaders(envPtr: EnvHandle): Int
  def mdbx_env_get_maxdbs(envPtr: EnvHandle): Int
  def mdbx_env_close(envPtr: EnvHandle): Unit
  def mdbx_env_copy(envPtr: EnvHandle, path: File, flags: Int): Unit
  def mdbx_env_create(): EnvHandle
  def mdbx_env_get_maxkeysize_ex(envPtr: EnvHandle, flags: Int): Int
  def mdbx_env_info_ex(envPtr: EnvHandle, txnPtr: TxnHandle): EnvInfo
  def mdbx_env_open(envPtr: EnvHandle, path: File, flags: Int, mode: Int): Unit
  def mdbx_env_set_mapsize(envPtr: EnvHandle, size: Long): Unit
  def mdbx_env_set_maxdbs(envPtr: EnvHandle, dbs: Int): Unit
  def mdbx_env_set_maxreaders(envPtr: EnvHandle, readers: Int): Unit
  def mdbx_env_stat(envPtr: EnvHandle): Stat
  def mdbx_env_stat_ex(envPtr: EnvHandle, txnPtr: TxnHandle): Stat
  def mdbx_env_sync_ex(envPtr: EnvHandle, force: Boolean, nonblock: Boolean): Unit

  /* --- Wrappers for global limits functions --- */
  def mdbx_limits_pgsize_min(): Long
  def mdbx_limits_pgsize_max(): Long
  def mdbx_limits_dbsize_min(page_size: Long): Long
  def mdbx_limits_dbsize_max(page_size: Long): Long
  def mdbx_limits_txnsize_max(page_size: Long): Long
  def mdbx_limits_keysize_max(page_size: Long, flags: Int): Long
  def mdbx_limits_valsize_max(page_size: Long, flags: Int): Long
  def mdbx_get_sysraminfo(): RamInfo

  /* --- Wrappers for GET/PUT/REPLACE functions --- */
  def mdbx_get(txnPtr: TxnHandle, dbiPtr: DbiHandle, key: Array[Byte]): Option[Array[Byte]]
  def mdbx_put(txnPtr: TxnHandle, dbiPtr: DbiHandle, key: Array[Byte], value: Array[Byte], flags: Int): Option[Array[Byte]]
  def mdbx_replace(txn: TxnHandle, dbiPtr: DbiHandle, key: Array[Byte], new_value: Array[Byte], flags: Int): Array[Byte]

  /* --- Wrappers for misc. functions --- */
  //def mdbx_set_compare[T >: Null](txn: Txn[T,AnyPointer], dbiPtr: AnyPointer, comparator: Comparator[T]): AnyRef // returns a comparator callback
  def mdbx_strerror(rc: Int): String

  /* --- Wrappers for Txn functions --- */
  def mdbx_txn_abort(txnPtr: TxnHandle): Unit
  def mdbx_txn_begin(envPtr: EnvHandle, parentTxnPtr: TxnHandle, flags: Int): TxnHandle
  def mdbx_txn_commit(txnPtr: TxnHandle): Unit
  def mdbx_txn_commit_ex(txnPtr: TxnHandle): CommitLatency
  //def mdbx_txn_env(txn: EnvHandle): Pointer
  def mdbx_txn_id(txnPtr: TxnHandle): Long
  def mdbx_txn_info(txnPtr: TxnHandle, scanRlt: Boolean): TxnInfo
  def mdbx_txn_renew(txnPtr: TxnHandle): Unit
  def mdbx_txn_reset(txnPtr: TxnHandle): Unit

  /* --- Wrapper for version function --- */
  def mdbx_version(): Version

  protected def isPutOK(rc: Int, flags: Int): Boolean = {
    if (rc != com.github.mdbx4s.ReturnCode.MDBX_KEYEXIST) {
      this.checkRc(rc)
      true
    } else {
      if (!MaskedFlag.isSet(flags, PutFlags.MDBX_NOOVERWRITE) && !MaskedFlag.isSet(flags, PutFlags.MDBX_NODUPDATA))
        this.checkRc(rc)

      //println("PutFlags.MDBX_NOOVERWRITE: " + MaskedFlag.isSet(flags, PutFlags.MDBX_NOOVERWRITE))
      false
    }
  }

  // --- Conversion utilities for keys and values --- //
  //def bytes2ptr(buffer: Array[Byte], ptr: AnyPointer): Unit
  //def ptr2bytes(ptr: AnyPointer): Array[Byte]

  //protected[mdbx4s] def checkRc(rc: Int): Unit

  //def throwSystemException(rc: Int): Unit

  protected def errorBuilderByErrorCode: scala.collection.mutable.LongMap[String => MdbxNativeException]

  protected[mdbx4s] def checkRc(rc: Int): Unit = {
    if (rc == 0) return

    val errorBuilderOpt = errorBuilderByErrorCode.get(rc.toLong)

    if (errorBuilderOpt.isEmpty)
      throw new MdbxNativeException(rc, "Unexpected error")

    val createError = errorBuilderOpt.get
    val mdbxError = createError(mdbx_strerror(rc))
    throw mdbxError

    /*rc match {
      case 0 => ()
      case MDBX_EINVAL => throw new MdbxNativeException(MDBX_EINVAL, rcMsg, "Invalid Parameter")
      case MDBX_EACCESS => throw new MdbxNativeException(MDBX_EACCESS, rcMsg, "Access Denied")
      case MDBX_ENODATA => throw new MdbxNativeException(MDBX_ENODATA, rcMsg, "Handle EOF")
      case MDBX_ENOMEM => throw new MdbxNativeException(MDBX_ENOMEM, rcMsg, "Out of Memory")
      case MDBX_EROFS => throw new MdbxNativeException(MDBX_EROFS, rcMsg, "File Read Only")
      case MDBX_ENOSYS => throw new MdbxNativeException(MDBX_ENOSYS, rcMsg, "Not Supported")
      case MDBX_EIO => throw new MdbxNativeException(MDBX_EIO, rcMsg, "Write Fault")
      case MDBX_EPERM => throw new MdbxNativeException(MDBX_EPERM, rcMsg, "Invalid Function")
      case MDBX_EINTR => throw new MdbxNativeException(MDBX_EINTR, rcMsg, "Cancelled")
      case MDBX_KEYEXIST => throw new MdbxNativeException(MDBX_KEYEXIST, rcMsg, "key/data pair already exists")
      case MDBX_NOTFOUND => throw new MdbxNativeException(MDBX_NOTFOUND, rcMsg, "key/data pair not found (EOF)")
      case MDBX_PAGE_NOTFOUND => throw new MdbxNativeException(MDBX_PAGE_NOTFOUND, rcMsg, "Requested page not found - this usually indicates corruption")
      case MDBX_CORRUPTED => throw new MdbxNativeException(MDBX_CORRUPTED, rcMsg, "Located page was wrong type")
      case MDBX_PANIC => throw new MdbxNativeException(MDBX_PANIC, rcMsg, "Update of meta page failed or environment had fatal error")
      case MDBX_VERSION_MISMATCH => throw new MdbxNativeException(MDBX_VERSION_MISMATCH, rcMsg, "DB file version mismatch with libmdbx")
      case MDBX_INVALID => throw new MdbxNativeException(MDBX_INVALID, rcMsg, "File is not a valid MDBX file")
      case MDBX_MAP_FULL => throw new MdbxNativeException(MDBX_MAP_FULL, rcMsg, "Environment mapsize reached")
      case MDBX_DBS_FULL => throw new MdbxNativeException(MDBX_DBS_FULL, rcMsg, "Environment maxdbs reached")
      case MDBX_READERS_FULL => throw new MdbxNativeException(MDBX_READERS_FULL, rcMsg, "Environment maxreaders reached")
      case MDBX_TXN_FULL => throw new MdbxNativeException(MDBX_TXN_FULL, rcMsg, "Transaction has too many dirty pages")
      case MDBX_CURSOR_FULL => throw new MdbxNativeException(MDBX_CURSOR_FULL, rcMsg, "Cursor stack too deep - internal error")
      case MDBX_PAGE_FULL => throw new MdbxNativeException(MDBX_PAGE_FULL, rcMsg, "Page has not enough space - internal error")
      case MDBX_INCOMPATIBLE => throw new MdbxNativeException(MDBX_INCOMPATIBLE, rcMsg, "Operation and DB incompatible, or DB type changed.")
      case MDBX_BAD_RSLOT => throw new MdbxNativeException(MDBX_BAD_RSLOT, rcMsg, "Invalid reuse of reader locktable slot")
      case MDBX_BAD_TXN => throw new MdbxNativeException(MDBX_BAD_TXN, rcMsg, "Transaction must abort, has a child, or is invalid")
      case MDBX_BAD_VALSIZE => throw new MdbxNativeException(MDBX_BAD_VALSIZE, rcMsg, "Unsupported size of key/DB name/data, or wrong DUPFIXED size")
      case MDBX_BAD_DBI => throw new MdbxNativeException(MDBX_BAD_DBI, rcMsg, "The specified DBI was changed unexpectedly")
      case MDBX_PROBLEM => throw new MdbxNativeException(MDBX_PROBLEM, rcMsg, "Unexpected problem - Transaction should abort")
      case MDBX_BUSY => throw new MdbxNativeException(MDBX_BUSY, rcMsg, "Another write transaction is running")
      case MDBX_EMULTIVAL => throw new MdbxNativeException(MDBX_EMULTIVAL, rcMsg, "The mdbx_put() or mdbx_replace() was called for key, that has more that one associated value.")
      case MDBX_EBADSIGN => throw new MdbxNativeException(MDBX_EBADSIGN, rcMsg, "Bad signature of a runtime object(s), this can mean: - memory corruption or double-free; - ABI version mismatch (rare case)")
      case MDBX_WANNA_RECOVERY => throw new MdbxNativeException(MDBX_WANNA_RECOVERY, rcMsg, "Database should be recovered, but this could NOT be done automatically right now (e.g. in readonly mode and so forth).")
      case MDBX_EKEYMISMATCH => throw new MdbxNativeException(MDBX_EKEYMISMATCH,rcMsg,  "The given key value is mismatched to the current cursor position, when mdbx_cursor_put() called with MDBX_CURRENT option.")
      case MDBX_TOO_LARGE => throw new MdbxNativeException(MDBX_TOO_LARGE, rcMsg, "Database is too large for current system, e.g. could NOT be mapped into RAM.")
      case MDBX_THREAD_MISMATCH => throw new MdbxNativeException(MDBX_THREAD_MISMATCH, rcMsg, "A thread has attempted to use a not owned object, e.g. a transaction that started by another thread.")
      case MDBX_RESULT_FALSE => throw new MdbxNativeException(MDBX_RESULT_FALSE, rcMsg, "Alias for Successful result")
      case MDBX_RESULT_TRUE => throw new MdbxNativeException(MDBX_RESULT_TRUE, rcMsg, "Successful result with special meaning or a flag")
      case MDBX_FIRST_LMDB_ERRCODE => throw new MdbxNativeException(MDBX_FIRST_LMDB_ERRCODE, rcMsg, "The first LMDB-compatible defined error code")
      case MDBX_UNABLE_EXTEND_MAPSIZE => throw new MdbxNativeException(MDBX_UNABLE_EXTEND_MAPSIZE, rcMsg, "Database engine was unable to extend mapping, e.g. since address space is unavailable or busy")
      case MDBX_LAST_LMDB_ERRCODE => throw new MdbxNativeException(MDBX_LAST_LMDB_ERRCODE, rcMsg, "The last LMDB-compatible defined error code")
      case MDBX_FIRST_ADDED_ERRCODE => throw new MdbxNativeException(MDBX_FIRST_ADDED_ERRCODE, rcMsg, "The first of MDBX-added error codes")
      case MDBX_TXN_OVERLAPPING => throw new MdbxNativeException(MDBX_TXN_OVERLAPPING, rcMsg, "Overlapping read and write transactions for the current thread")
      case MDBX_LAST_ADDED_ERRCODE => throw new MdbxNativeException(MDBX_LAST_ADDED_ERRCODE, rcMsg, "The last added error code")
      case MDBX_ENOFILE => throw new MdbxNativeException(MDBX_ENOFILE, rcMsg, "?? no description")
      case MDBX_EREMOTE => throw new MdbxNativeException(MDBX_EREMOTE, rcMsg, "?? no description")

      case _ => throw new MdbxNativeException(rc, "Unexpected error")
    }*/
  }

}
