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

//====================================================//
//===                 MDBX constants               ===//
//====================================================//

// See: https://github.com/erthink/libmdbx/blob/python-bindings/python/libmdbx/mdbx.py.in

/* --- Main constants --- */
object Constants {
  /** The hard limit for DBI handles */
  val MDBX_MAX_DBI: Int = 32765

  /** The maximum size of a data item. */
  val MDBX_MAXDATASIZE: Int = 0x7fff0000

  /** The minimal database page size in bytes. */
  val MDBX_MIN_PAGESIZE: Int = 256

  /** The maximal database page size in bytes. */
  val MDBX_MAX_PAGESIZE: Int = 65536
}

/* --- Log levels --- */
object LogLevel {
  /** Critical conditions i.e. assertion failures. */
  val MDBX_LOG_FATAL = 0

  /** Enables logging for error conditions and \ref MDBX_LOG_FATAL */
  val MDBX_LOG_ERROR = 1

  /** Enables logging for warning conditions and \ref MDBX_LOG_ERROR ... \ref MDBX_LOG_FATAL */
  val MDBX_LOG_WARN = 2

  /** Enables logging for normal but significant condition and \ref MDBX_LOG_WARN ... \ref MDBX_LOG_FATAL */
  val MDBX_LOG_NOTICE = 3

  /** Enables logging for verbose informational and \ref MDBX_LOG_NOTICE ... \ref MDBX_LOG_FATAL */
  val MDBX_LOG_VERBOSE = 4

  /** Enables logging for debug-level messages and \ref MDBX_LOG_VERBOSE ... \ref MDBX_LOG_FATAL */
  val MDBX_LOG_DEBUG = 5

  /** Enables logging for trace debug-level messages and \ref MDBX_LOG_DEBUG ... \ref MDBX_LOG_FATAL */
  val MDBX_LOG_TRACE = 6

  /** Enables extra debug-level messages (dump pgno lists) and all other log-messages. */
  val MDBX_LOG_EXTRA = 7

  /** for \ref mdbx_setup_debug() only: Don't change current settings. */
  val MDBX_LOG_DONTCHANGE = -1
}

object DebugFlags {
  /** No debug. */
  val MDBX_DBG_NONE = 0

  /** Enable assertion checks. Requires build with \ref MDBX_DEBUG > 0 */
  val MDBX_DBG_ASSERT = 1

  /** Enable pages usage audit at commit transactions. Requires build with \ref MDBX_DEBUG > 0 */
  val MDBX_DBG_AUDIT = 2

  /** Enable small random delays in critical points. Requires build with \ref MDBX_DEBUG > 0 */
  val MDBX_DBG_JITTER = 4

  /** Include or not meta-pages in coredump files. May affect performance in \ref MDBX_WRITEMAP mode */
  val MDBX_DBG_DUMP = 8

  /** Allow multi-opening environment(s) */
  val MDBX_DBG_LEGACY_MULTIOPEN = 16

  /** Allow read and write transactions overlapping for the same thread */
  val MDBX_DBG_LEGACY_OVERLAP = 32

  /** for mdbx_setup_debug() only: Don't change current settings */
  val MDBX_DBG_DONTCHANGE = -1
}

/* --- MDBX environment options (MDBX_option_t) --- */
object EnvOption {

  /** Controls the maximum number of named databases for the environment. */
  val MDBX_opt_max_db: Int = 0

  /** Defines the maximum number of threads/reader slots for all processes interacting with the database. */
  val MDBX_opt_max_readers: Int = 1

  /**
   * Controls interprocess/shared threshold to force flush the data buffers to disk, if MDBX_SAFE_NOSYNC is used.
   */
  val MDBX_opt_sync_bytes: Int = 2

  /**
   * Controls interprocess/shared relative period since the last unsteady commit to force flush the data
   * buffers to disk, if MDBX_SAFE_NOSYNC is used.
   */
  val MDBX_opt_sync_period: Int = 3

  /**
   * Controls the in-process limit to grow a list of reclaimed/recycled page's numbers for finding a sequence
   * of contiguous pages for large data items.
   */
  val MDBX_opt_rp_augment_limit: Int = 4

  /** Controls the in-process limit to grow a cache of dirty pages for reuse in the current transaction. */
  val MDBX_opt_loose_limit: Int = 5

  /** Controls the in-process limit of a pre-allocated memory items for dirty pages. */
  val MDBX_opt_dp_reserve_limit: Int = 6

  /** Controls the in-process limit of dirty pages for a write transaction. */
  val MDBX_opt_txn_dp_limit: Int = 7

  /** Controls the in-process initial allocation size for dirty pages list of a write transaction. Default is 1024. */
  val MDBX_opt_txn_dp_initial: Int = 8

  /** Controls the in-process how maximal part of the dirty pages may be spilled when necessary. */
  val MDBX_opt_spill_max_denominator: Int = 9

  /** Controls the in-process how minimal part of the dirty pages should be spilled when necessary. */
  val MDBX_opt_spill_min_denominator: Int = 10

  /** Controls the in-process how much of the parent transaction dirty pages will be spilled while start each child transaction. */
  val MDBX_opt_spill_parent4child_denominator: Int = 11

  /** Controls the in-process threshold of semi-empty pages merge. */
  val MDBX_opt_merge_threshold_16dot16_percent: Int = 12
}

/* --- DBI state bits returted by \ref mdbx_dbi_flags_ex() --- */
object DbiState extends Enumeration {
  /** DB is in its initial state */
  val MDBX_DBI_INITIAL: Value = Value(0)
  /** DB was written in this txn */
  val MDBX_DBI_DIRTY: Value = Value(0x01)
  /** Named-DB record is older than txnID */
  val MDBX_DBI_STALE: Value = Value(0x02)
  /** Named-DB handle opened in this txn */
  val MDBX_DBI_FRESH: Value = Value(0x04)
  /** Named-DB handle created in this txn */
  val MDBX_DBI_CREAT: Value = Value(0x08)

  def valueOf(state: Int): DbiState.Value = {
    state match {
      case 0x00 => DbiState.MDBX_DBI_INITIAL
      case 0x01 => DbiState.MDBX_DBI_DIRTY
      case 0x02 => DbiState.MDBX_DBI_STALE
      case 0x04 => DbiState.MDBX_DBI_FRESH
      case 0x08 => DbiState.MDBX_DBI_CREAT
    }
  }
}

/* --- Return Codes --- */
object ReturnCode {

  /** Successful result */
  val MDBX_SUCCESS = 0

  /** Alias for \ref MDBX_SUCCESS */
  val MDBX_RESULT_FALSE = MDBX_SUCCESS

  /** Successful result with special meaning or a flag */
  val MDBX_RESULT_TRUE = -1

  /** key/data pair already exists */
  val MDBX_KEYEXIST = -30799

  /** The first LMDB-compatible defined error code */
  val MDBX_FIRST_LMDB_ERRCODE = MDBX_KEYEXIST

  /** key/data pair not found (EOF) */
  val MDBX_NOTFOUND = -30798

  /** Requested page not found - this usually indicates corruption */
  val MDBX_PAGE_NOTFOUND = -30797

  /** Database is corrupted (page was wrong type and so on) */
  val MDBX_CORRUPTED = -30796

  /** Environment had fatal error, i.e. update of meta page failed and so on. */
  val MDBX_PANIC = -30795

  /** DB file version mismatch with libmdbx */
  val MDBX_VERSION_MISMATCH = -30794

  /** File is not a valid MDBX file */
  val MDBX_INVALID = -30793

  /** Environment mapsize reached */
  val MDBX_MAP_FULL = -30792

  /** Environment maxdbs reached */
  val MDBX_DBS_FULL = -30791

  /** Environment maxreaders reached */
  val MDBX_READERS_FULL = -30790

  /** Transaction has too many dirty pages, i.e transaction too big */
  val MDBX_TXN_FULL = -30788

  /** Cursor stack too deep - this usually indicates corruption, i.e branch-pages loop */
  val MDBX_CURSOR_FULL = -30787

  /** Page has not enough space - internal error */
  val MDBX_PAGE_FULL = -30786

  /** Database engine was unable to extend mapping, e.g. since address space
   * is unavailable or busy. This can mean:
   *  - Database size extended by other process beyond to environment mapsize
   *    and engine was unable to extend mapping while starting read
   *    transaction. Environment should be reopened to continue.
   *  - Engine was unable to extend mapping during write transaction
   *    or explicit call of \ref mdbx_env_set_geometry(). */
  val MDBX_UNABLE_EXTEND_MAPSIZE = -30785

  /** Environment or database is not compatible with the requested operation
   * or the specified flags. This can mean:
   *  - The operation expects an \ref MDBX_DUPSORT / \ref MDBX_DUPFIXED
   *    database.
   *  - Opening a named DB when the unnamed DB has \ref MDBX_DUPSORT /
   *    \ref MDBX_INTEGERKEY.
   *  - Accessing a data record as a database, or vice versa.
   *  - The database was dropped and recreated with different flags. */
  val MDBX_INCOMPATIBLE = -30784

  /** Invalid reuse of reader locktable slot,
   * e.g. read-transaction already run for current thread */
  val MDBX_BAD_RSLOT = -30783

  /** Transaction is not valid for requested operation,
   * e.g. had errored and be must aborted, has a child, or is invalid */
  val MDBX_BAD_TXN = -30782

  /** Invalid size or alignment of key or data for target database,
   * either invalid subDB name */
  val MDBX_BAD_VALSIZE = -30781

  /** The specified DBI-handle is invalid
   * or changed by another thread/transaction */
  val MDBX_BAD_DBI = -30780

  /** Unexpected internal error, transaction should be aborted */
  val MDBX_PROBLEM = -30779

  /** The last LMDB-compatible defined error code */
  val MDBX_LAST_LMDB_ERRCODE = MDBX_PROBLEM

  /** Another write transaction is running or environment is already used while
   * opening with \ref MDBX_EXCLUSIVE flag */
  val MDBX_BUSY = -30778

  /** The first of MDBX-added error codes */
  val MDBX_FIRST_ADDED_ERRCODE = MDBX_BUSY

  /** The specified key has more than one associated value */
  val MDBX_EMULTIVAL = -30421

  /** Bad signature of a runtime object(s), this can mean:
   *  - memory corruption or double-free;
   *  - ABI version mismatch (rare case); */
  val MDBX_EBADSIGN = -30420

  /** Database should be recovered, but this could NOT be done for now
   * since it opened in read-only mode */
  val MDBX_WANNA_RECOVERY = -30419

  /** The given key value is mismatched to the current cursor position */
  val MDBX_EKEYMISMATCH = -30418

  /** Database is too large for current system,
   * e.g. could NOT be mapped into RAM. */
  val MDBX_TOO_LARGE = -30417

  /** A thread has attempted to use a not owned object,
   * e.g. a transaction that started by another thread. */
  val MDBX_THREAD_MISMATCH = -30416

  /** Overlapping read and write transactions for the current thread */
  val MDBX_TXN_OVERLAPPING = -30415

  /* The last of MDBX-added error codes */
  //val MDBX_LAST_ADDED_ERRCODE = MDBX_TXN_OVERLAPPING

}