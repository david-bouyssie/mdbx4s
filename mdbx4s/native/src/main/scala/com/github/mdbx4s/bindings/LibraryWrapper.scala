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
import scala.scalanative.unsafe._
import scala.scalanative.unsigned._

import com.github.mdbx4s._

object LibraryWrapper {

  private lazy val singleton = new LibraryWrapper()
  def getWrapper(): ILibraryWrapper = singleton

  implicit class struct_mdbx_stat_ops(val p: Ptr[mdbx.struct_mdbx_stat]) extends AnyVal {
    def psize: UInt = p._1
    //def psize_=(value: UInt): Unit = { p._1 = value }
    def depth: UInt = p._2
    //def depth_=(value: UInt): Unit = { p._2 = value }
    def branch_pages: CSize = p._3
    //def branch_pages_=(value: CSize): Unit = { p._3 = value }
    def leaf_pages: CSize = p._4
    //def leaf_pages_=(value: CSize): Unit = { p._4 = value }
    def overflow_pages: CSize = p._5
    //def overflow_pages_=(value: CSize): Unit = { p._5 = value }
    def entries: CSize = p._6
    //def entries_=(value: CSize): Unit = { p._6 = value }
  }

  implicit class struct_mdbx_mi_geo_ops(val p: Ptr[mdbx.struct_mdbx_mi_geo]) extends AnyVal {
    def lower: CSize = p._1   /**< Lower limit for datafile size */
    //def lower_=(value: CSize): Unit = { p._1 = value }
    def upper: CSize = p._2   /**< Upper limit for datafile size */
    //def upper_=(value: CSize): Unit = { p._2 = value }
    def current: CSize = p._3 /**< Current datafile size */
    //def current_=(value: CSize): Unit = { p._3 = value }
    def shrink: CSize = p._4  /**< Shrink threshold for datafile */
    //def shrink_=(value: CSize): Unit = { p._4 = value }
    def grow: CSize = p._5    /**< Growth step for datafile */
    //def grow_=(value: CSize): Unit = { p._5 = value }
  }

  implicit class struct_mdbx_env_meta_ops(val p: Ptr[mdbx.struct_mdbx_env_meta]) extends AnyVal {
    def meta0_txn_id: CSize = p._1
    //def meta0_txn_id_=(value: CSize): Unit = { p._1 = value }
    def meta0_sign: CSize = p._2
    //def meta0_sign_=(value: CSize): Unit = { p._2 = value }
    def meta1_txn_id: CSize = p._3
    //def meta1_txn_id_=(value: CSize): Unit = { p._3 = value }
    def meta1_sign: CSize = p._4
    //def meta1_sign_=(value: CSize): Unit = { p._4 = value }
    def meta2_txn_id: CSize = p._5
    //def meta2_txn_id_=(value: CSize): Unit = { p._5 = value }
    def meta2_sign: CSize = p._6
    //def meta2_sign_=(value: CSize): Unit = { p._6 = value }
  }

  implicit class struct_mdbx_mi_bootid_ops(val p: Ptr[mdbx.struct_mdbx_mi_bootid]) extends AnyVal {
    def current_x: CSize = p._1
    //def current_x_=(value: CSize): Unit = { p._1 = value }
    def current_y: CSize = p._2
    //def current_y_=(value: CSize): Unit = { p._2 = value }
    def meta0_x: CSize = p._3
    //def meta0_x_=(value: CSize): Unit = { p._3 = value }
    def meta0_y: CSize = p._4
    //def meta0_y_=(value: CSize): Unit = { p._4 = value }
    def meta1_x: CSize = p._5
    //def meta1_x_=(value: CSize): Unit = { p._5 = value }
    def meta1_y: CSize = p._6
    //def meta1_y_=(value: CSize): Unit = { p._6 = value }
    def meta2_x: CSize = p._7
    //def meta2_x_=(value: CSize): Unit = { p._7 = value }
    def meta2_y: CSize = p._8
    //def meta2_y_=(value: CSize): Unit = { p._8 = value }
  }

  /*
    uint64_t mi_mapsize;             /**< Size of the data memory map */
    uint64_t mi_last_pgno;           /**< Number of the last used page */
    uint64_t mi_recent_txnid;        /**< ID of the last committed transaction */
    uint64_t mi_latter_reader_txnid; /**< ID of the last reader transaction */
    uint64_t mi_self_latter_reader_txnid; /**< ID of the last reader transaction */
   */
  implicit class struct_mdbx_env_info_ops(val p: Ptr[mdbx.struct_mdbx_env_info]) extends AnyVal {
    def geo: struct_mdbx_mi_geo_ops = p.asInstanceOf[Ptr[mdbx.struct_mdbx_mi_geo]] // struct_mdbx_mi_geo_ops
    //def geo_=(value: mdbx.struct_mdbx_mi_geo): Unit = { p._1 = value }
    def mapsize: CSize = p._2
    //def mapsize_=(value: CSize): Unit = { p._2 = value }
    def last_pgno: CSize = p._3
    //def last_pgno_=(value: CSize): Unit = { p._3 = value }
    def recent_txnid: CSize = p._4
    //def recent_txnid_=(value: CSize): Unit = { p._4 = value }
    def latter_reader_txnid: CSize = p._5
    //def latter_reader_txnid_=(value: CSize): Unit = { p._4 = value }
    def self_latter_reader_txnid: CSize = p._6
    //def self_latter_reader_txnid_=(value: CSize): Unit = { p._4 = value }
    def meta: mdbx.struct_mdbx_env_meta = p._7
    //def meta_=(value: mdbx.struct_mdbx_env_meta): Unit = { p._1 = value }
    def maxreaders: CUnsignedInt = p._8
    //def maxreaders_=(value: UInt): Unit = { p._5 = value }
    def numreaders: CUnsignedInt = p._9
    //def numreaders_=(value: UInt): Unit = { p._6 = value }
    def dxb_pagesize: CUnsignedInt = p._10
    //def dxb_pagesize_=(value: UInt): Unit = { p._5 = value }
    def sys_pagesize: CUnsignedInt = p._11
    //def sys_pagesize_=(value: UInt): Unit = { p._6 = value }
    def bootid: mdbx.struct_mdbx_mi_bootid = p._12 // struct_mdbx_mi_geo_ops
    //def bootid_=(value: mdbx.struct_mdbx_mi_geo): Unit = { p._1 = value }
    def unsync_volume: CSize = p._13
    def autosync_threshold: CSize = p._14
    def since_sync_seconds16dot16: CUnsignedInt = p._15
    def autosync_period_seconds16dot16: CUnsignedInt = p._16
    def since_reader_check_seconds16dot16: CUnsignedInt = p._17
    def mode: CUnsignedInt = p._18
    def pgop_stat: mdbx.struct_mdbx_mi_pgop_stat = p._19
  }

  implicit class struct_mdbx_txn_info_ops(val p: Ptr[mdbx.struct_mdbx_txn_info]) extends AnyVal {
    /** The ID of the transaction. For a READ-ONLY transaction, this corresponds
     * to the snapshot being read. */
    def id: CSize = p._1

    /** For READ-ONLY transaction: the lag from a recent MVCC-snapshot, i.e. the
     * number of committed transaction since read transaction started.
     * For WRITE transaction (provided if `scan_rlt=true`): the lag of the oldest
     * reader from current transaction (i.e. at least 1 if any reader running). */
    def reader_lag: CSize = p._2

    /** Used space by this transaction, i.e. corresponding to the last used
     * database page. */
    def space_used: CSize = p._3

    /** Current size of database file. */
    def space_limit_soft: CSize = p._4

    /** Upper bound for size the database file, i.e. the value `size_upper`
     * argument of the appropriate call of \ref mdbx_env_set_geometry(). */
    def space_limit_hard: CSize = p._5

    /** For READ-ONLY transaction: The total size of the database pages that were
     * retired by committed write transactions after the reader's MVCC-snapshot,
     * i.e. the space which would be freed after the Reader releases the
     * MVCC-snapshot for reuse by completion read transaction.
     * For WRITE transaction: The summarized size of the database pages that were
     * retired for now due Copy-On-Write during this transaction. */
    def space_retired: CSize = p._6

    /** For READ-ONLY transaction: the space available for writer(s) and that
     * must be exhausted for reason to call the Handle-Slow-Readers callback for
     * this read transaction.
     * For WRITE transaction: the space inside transaction
     * that left to `MDBX_TXN_FULL` error. */
    def space_leftover: CSize = p._7

    /** For READ-ONLY transaction (provided if `scan_rlt=true`): The space that
     * actually become available for reuse when only this transaction will be
     * finished.
     * For WRITE transaction: The summarized size of the dirty database
     * pages that generated during this transaction. */
    def space_dirty: CSize = p._8
  }

  implicit class struct_mdbx_commit_latency_ops(val p: Ptr[mdbx.struct_mdbx_commit_latency]) extends AnyVal {
    /** \brief Duration of preparation (commit child transactions, update
     * sub-databases records and cursors destroying). */
    def preparation: CUnsignedInt = p._1
    /** \brief Duration of GC/freeDB handling & updation. */
    def gc: CUnsignedInt = p._2
    /** \brief Duration of internal audit if enabled. */
    def audit: CUnsignedInt = p._3
    /** \brief Duration of writing dirty/modified data pages to a filesystem,
     * i.e. the summary duration of a `write()` syscalls during commit. */
    def write: CUnsignedInt = p._4
    /** \brief Duration of syncing written data to the disk/storage, i.e.
     * the duration of a `fdatasync()` or a `msync()` syscall during commit. */
    def sync: CUnsignedInt = p._5
    /** \brief Duration of transaction ending (releasing resources). */
    def ending: CUnsignedInt = p._6
    /** \brief The total duration of a commit. */
    def whole: CUnsignedInt = p._7
  }

  // The mdbx API

  /*
  @JniClass(flags = STRUCT) class MDBX_version_info {
    @JniField(cast = "uint8_t") var major = 0
    @JniField(cast = "uint8_t") var minor = 0
    @JniField(cast = "uint16_t") var release = 0
    @JniField(cast = "uint32_t") var revision = 0
    @JniField(accessor = "git.datetime", cast = "char *") var git_datetime: Array[Char] = null
    @JniField(accessor = "git.tree", cast = "char *") var git_tree: Array[Char] = null
    @JniField(accessor = "git.commit", cast = "char *") var git_commit: Array[Char] = null
    @JniField(accessor = "git.describe", cast = "char *") var git_describe: Array[Char] = null
    @JniField(accessor = "sourcery", cast = "char *") var sourcery: Array[Char] = null

    //		@JniField(cast = "mdbx_version_info.git *")
    //		public git git = new git();
    @SuppressWarnings(Array("nls")) override def toString: String = "{" + //$NON-NLS-1$
      "major=" + major + ", minor=" + minor + ", release=" + release + ", revision=" + revision + //					", git=" + git +
      '}'

    @SuppressWarnings(Array("nls")) def getVersionString: String = "" + major + '.' + minor + '.' + release + '.' + revision
  }*/

  //	@JniField(flags = { CONSTANT })
  //	static public mdbx_version_info mdbx_version;

  // Build Info
  /*@JniClass(flags = STRUCT) class MDBX_build_info {
    @JniField(cast = "char *") var datetime: Array[Char] = null
    @JniField(cast = "char *") var target: Array[Char] = null
    @JniField(cast = "char *") var options: Array[Char] = null
    @JniField(cast = "char *") var compiler: Array[Char] = null
    @JniField(cast = "char *") var flags: Array[Char] = null

    @SuppressWarnings(Array("nls")) override def toString: String = "{" + "datetime=" + new String(datetime) + ", target=" + new String(target) + ", options=" + new String(options) + ", compiler=" + new String(compiler) + ", flags=" + new String(flags) + '}'
  }*/

  // Transaction Info
  /*@JniClass(flags = Array(STRUCT, TYPEDEF)) class MDBX_txn_info {
    @JniField(cast = "uint64_t") var txn_id = 0L
    @JniField(cast = "uint64_t") var txn_reader_lag = 0L
    @JniField(cast = "uint64_t") var txn_space_used = 0L
    @JniField(cast = "uint64_t") var txn_space_limit_soft = 0L
    @JniField(cast = "uint64_t") var txn_space_limit_hard = 0L
    @JniField(cast = "uint64_t") var txn_space_retired = 0L
    @JniField(cast = "uint64_t") var txn_space_leftover = 0L
    @JniField(cast = "uint64_t") var txn_space_dirty = 0L

    @SuppressWarnings(Array("nls")) override def toString: String = "{" + "txn_id=" + txn_id + ", txn_reader_lag=" + txn_reader_lag + ", txn_space_used=" + txn_space_used + ", txn_space_limit_soft=" + txn_space_limit_soft + ", txn_space_limit_hard=" + txn_space_limit_hard + ", txn_space_retired=" + txn_space_retired + ", txn_space_leftover=" + txn_space_leftover + ", txn_space_dirty=" + txn_space_dirty + '}'
  }*/

  // Commit Latency
  /*@JniClass(flags = STRUCT) class MDBX_commit_latency {
    @JniField(cast = "uint32_t") var preparation = 0 // Duration of preparation (commit child transactions, update sub-databases records and cursors destroying).

    @JniField(cast = "uint32_t") var gc = 0 //Duration of GC/freeDB handling & updation.

    @JniField(cast = "uint32_t") var audit = 0 //Duration of internal audit if enabled.

    @JniField(cast = "uint32_t") var write = 0 //Duration of writing dirty/modified data pages.

    @JniField(cast = "uint32_t") var sync = 0 //Duration of syncing written data to the dist/storage.

    @JniField(cast = "uint32_t") var ending = 0 //Duration of transaction ending (releasing resources).

    @JniField(cast = "uint32_t") var whole = 0 //The total duration of a commit

    @SuppressWarnings(Array("nls")) override def toString: String = "{" + "preparation=" + preparation + ", gc=" + gc + ", audit=" + audit + ", write=" + write + ", sync=" + sync + ", ending=" + ending + ", whole=" + whole + '}'
  }*/

  // Canary
  /*@JniClass(flags = STRUCT) class MDBX_canary {
    @JniField(cast = "uint64_t") var x = 0L
    @JniField(cast = "uint64_t") var y = 0L
    @JniField(cast = "uint64_t") var z = 0L
    @JniField(cast = "uint64_t") var v = 0L

    @SuppressWarnings(Array("nls")) override def toString: String = "{" + "x=" + x + ", y=" + y + ", z=" + z + ", v=" + v + '}'
  }*/

  // Environment Flags
  /*

  //====================================================//
  // MDBX_constants

  // MDBX environment options (MDBX_option_t)

  /** Controls the maximum number of named databases for the environment. */
  @JniField(flags = Array(CONSTANT)) var MDBX_opt_max_db = 0

  /** Defines the maximum number of threads/reader slots for all processes interacting with the database. */
  @JniField(flags = Array(CONSTANT)) var MDBX_opt_max_readers = 0

  /**
   * Controls interprocess/shared threshold to force flush the data buffers to disk, if MDBX_SAFE_NOSYNC is
   * used.
   */
  @JniField(flags = Array(CONSTANT)) var MDBX_opt_sync_bytes = 0

  /**
   * Controls interprocess/shared relative period since the last unsteady commit to force flush the data
   * buffers to disk, if MDBX_SAFE_NOSYNC is used.
   */
  @JniField(flags = Array(CONSTANT)) var MDBX_opt_sync_period = 0

  /**
   * Controls the in-process limit to grow a list of reclaimed/recycled page's numbers for finding a sequence
   * of contiguous pages for large data items.
   */
  @JniField(flags = Array(CONSTANT)) var MDBX_opt_rp_augment_limit = 0

  /** Controls the in-process limit to grow a cache of dirty pages for reuse in the current transaction. */
  @JniField(flags = Array(CONSTANT)) var MDBX_opt_loose_limit = 0

  /** Controls the in-process limit of a pre-allocated memory items for dirty pages. */
  @JniField(flags = Array(CONSTANT)) var MDBX_opt_dp_reserve_limit = 0

  /** Controls the in-process limit of dirty pages for a write transaction. */
  @JniField(flags = Array(CONSTANT)) var MDBX_opt_txn_dp_limit = 0

  /** Controls the in-process initial allocation size for dirty pages list of a write transaction. Default is 1024. */
  @JniField(flags = Array(CONSTANT)) var MDBX_opt_txn_dp_initial = 0

  /** Controls the in-process how maximal part of the dirty pages may be spilled when necessary. */
  @JniField(flags = Array(CONSTANT)) var MDBX_opt_spill_max_denominator = 0

  /** Controls the in-process how minimal part of the dirty pages should be spilled when necessary. */
  @JniField(flags = Array(CONSTANT)) var MDBX_opt_spill_min_denominator = 0

  /** Controls the in-process how much of the parent transaction dirty pages will be spilled while start each child transaction. */
  @JniField(flags = Array(CONSTANT)) var MDBX_opt_spill_parent4child_denominator = 0

  /** Controls the in-process threshold of semi-empty pages merge. */
  @JniField(flags = Array(CONSTANT)) var MDBX_opt_merge_threshold_16dot16_percent = 0

  // ====================================================//
  // Database Flags

  /** Default (flag == 0). */
  @JniField(flags = Array(CONSTANT)) var MDBX_DB_DEFAULTS = 0

  /** Use reverse string comparison for keys. */
  @JniField(flags = Array(CONSTANT)) var MDBX_REVERSEKEY = 0

  /** Use sorted duplicates, i.e. allow multi-values for a keys. */
  @JniField(flags = Array(CONSTANT)) var MDBX_DUPSORT = 0

  /**
   * Numeric keys in native byte order either uint32_t or uint64_t. The keys must all be of the same size and
   * must be aligned while passing as arguments.
   */
  @JniField(flags = Array(CONSTANT)) var MDBX_INTEGERKEY = 0

  /** With MDBX_DUPSORT; sorted dup items have fixed size. The data values must all be of the same size. */
  @JniField(flags = Array(CONSTANT)) var MDBX_DUPFIXED = 0

  /**
   * With MDBX_DUPSORT and with MDBX_DUPFIXED; dups are fixed size like MDBX_INTEGERKEY -style integers. The
   * data values must all be of the same size and must be aligned while passing as arguments.
   */
  @JniField(flags = Array(CONSTANT)) var MDBX_INTEGERDUP = 0

  /** With MDBX_DUPSORT; use reverse string comparison for data values. */
  @JniField(flags = Array(CONSTANT)) var MDBX_REVERSEDUP = 0

  /** Create DB if not already existing. */
  @JniField(flags = Array(CONSTANT)) var MDBX_CREATE = 0

  /** Opens an existing sub-database created with unknown flags. */
  @JniField(flags = Array(CONSTANT)) var MDBX_DB_ACCEDE = 0

  // ====================================================//
  // Transaction Flags
  @JniField(flags = Array(CONSTANT)) var MDBX_TXN_READWRITE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_TXN_RDONLY = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_TXN_RDONLY_PREPARE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_TXN_TRY = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_TXN_NOMETASYNC = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_TXN_NOSYNC = 0

  // DBI State Flags
  @JniField(flags = Array(CONSTANT)) var MDBX_DBI_DIRTY = 0 //DB was written in this txn

  @JniField(flags = Array(CONSTANT)) var MDBX_DBI_STALE = 0 //Named-DB record is older than txnID

  @JniField(flags = Array(CONSTANT)) var MDBX_DBI_FRESH = 0 //Named-DB handle opened in this txn

  @JniField(flags = Array(CONSTANT)) var MDBX_DBI_CREAT = 0 //Named-DB handle created in this txn


  // Copy Flags
  @JniField(flags = Array(CONSTANT)) var MDBX_CP_DEFAULTS = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_CP_COMPACT = 0 //Copy with compactification: Omit free space from copy and renumber all pages sequentially

  @JniField(flags = Array(CONSTANT)) var MDBX_CP_FORCE_DYNAMIC_SIZE = 0 //Force to make resizeable copy, i.e. dynamic size instead of fixed


  // Write Flags
  @JniField(flags = Array(CONSTANT)) var MDBX_UPSERT = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_NOOVERWRITE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_NODUPDATA = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_CURRENT = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_ALLDUPS = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_RESERVE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_APPEND = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_APPENDDUP = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_MULTIPLE = 0

  // enum MDBX_cursor_op:
  @JniField(flags = Array(CONSTANT)) var MDBX_FIRST = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_FIRST_DUP = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_GET_BOTH = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_GET_BOTH_RANGE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_GET_CURRENT = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_GET_MULTIPLE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_LAST = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_LAST_DUP = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_NEXT = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_NEXT_DUP = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_NEXT_MULTIPLE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_NEXT_NODUP = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_PREV = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_PREV_DUP = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_PREV_NODUP = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_SET = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_SET_KEY = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_SET_RANGE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_PREV_MULTIPLE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_SET_LOWERBOUND = 0

  // Return Codes
  @JniField(flags = Array(CONSTANT)) var MDBX_EINVAL = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_EACCESS = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_ENODATA = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_ENOMEM = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_EROFS = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_ENOSYS = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_EIO = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_EPERM = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_EINTR = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_SUCCESS = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_KEYEXIST = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_NOTFOUND = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_PAGE_NOTFOUND = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_CORRUPTED = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_PANIC = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_VERSION_MISMATCH = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_INVALID = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_MAP_FULL = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_DBS_FULL = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_READERS_FULL = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_TXN_FULL = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_CURSOR_FULL = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_PAGE_FULL = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_INCOMPATIBLE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_BAD_RSLOT = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_BAD_TXN = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_BAD_VALSIZE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_BAD_DBI = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_PROBLEM = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_BUSY = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_EMULTIVAL = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_EBADSIGN = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_WANNA_RECOVERY = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_EKEYMISMATCH = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_TOO_LARGE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_THREAD_MISMATCH = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_RESULT_FALSE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_RESULT_TRUE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_FIRST_LMDB_ERRCODE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_UNABLE_EXTEND_MAPSIZE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_LAST_LMDB_ERRCODE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_FIRST_ADDED_ERRCODE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_TXN_OVERLAPPING = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_LAST_ADDED_ERRCODE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_ENOFILE = 0
  @JniField(flags = Array(CONSTANT)) var MDBX_EREMOTE = 0
*/


  /*
  // Environment Info
  @JniClass(flags = Array(STRUCT, TYPEDEF)) class MDBX_envinfo { //		public MiGeo mi_geo = new MiGeo();
    @JniField(accessor = "mi_geo.lower", cast = "uint64_t") var mi_geo_lower = 0L /* lower limit for datafile size */
    @JniField(accessor = "mi_geo.upper", cast = "uint64_t") var mi_geo_upper = 0L /* upper limit for datafile size */
    @JniField(accessor = "mi_geo.current", cast = "uint64_t") var mi_geo_current = 0L /* current datafile size */
    @JniField(accessor = "mi_geo.shrink", cast = "uint64_t") var mi_geo_shrink = 0L /* shrink treshold for datafile */
    @JniField(accessor = "mi_geo.grow", cast = "uint64_t") var mi_geo_grow = 0L /* growth step for datafile */
    @JniField(cast = "uint64_t") var mi_mapsize = 0L /* Size of the data memory map */
    @JniField(cast = "uint64_t") var mi_last_pgno = 0L /* ID of the last used page */
    @JniField(cast = "uint64_t") var mi_recent_txnid = 0L /* ID of the last committed transaction */
    @JniField(cast = "uint64_t") var mi_latter_reader_txnid = 0L /* ID of the last reader transaction */
    @JniField(cast = "uint64_t") var mi_self_latter_reader_txnid = 0L /* ID of the last reader transaction of caller process */
    @JniField(cast = "uint64_t") var mi_meta0_txnid = 0L
    @JniField(cast = "uint64_t") var mi_meta0_sign = 0L
    @JniField(cast = "uint64_t") var mi_meta1_txnid = 0L
    @JniField(cast = "uint64_t") var mi_meta1_sign = 0L
    @JniField(cast = "uint64_t") var mi_meta2_txnid = 0L
    @JniField(cast = "uint64_t") var mi_meta2_sign = 0L
    @JniField(cast = "uint32_t") var mi_maxreaders = 0L /* max reader slots in the environment */
    @JniField(cast = "uint32_t") var mi_numreaders = 0L /* max reader slots used in the environment */
    @JniField(cast = "uint32_t") var mi_dxb_pagesize = 0L /* database pagesize */
    @JniField(cast = "uint32_t") var mi_sys_pagesize = 0L /* system pagesize */
    @JniField(accessor = "mi_bootid.current.x", cast = "uint64_t") var mi_bootid_current_x = 0L
    @JniField(accessor = "mi_bootid.current.y", cast = "uint64_t") var mi_bootid_current_y = 0L
    @JniField(accessor = "mi_bootid.meta0.x", cast = "uint64_t") var mi_bootid_meta0_x = 0L
    @JniField(accessor = "mi_bootid.meta0.y", cast = "uint64_t") var mi_bootid_meta0_y = 0L
    @JniField(accessor = "mi_bootid.meta1.x", cast = "uint64_t") var mi_bootid_meta1_x = 0L
    @JniField(accessor = "mi_bootid.meta1.y", cast = "uint64_t") var mi_bootid_meta1_y = 0L
    @JniField(accessor = "mi_bootid.meta2.x", cast = "uint64_t") var mi_bootid_meta2_x = 0L
    @JniField(accessor = "mi_bootid.meta2.y", cast = "uint64_t") var mi_bootid_meta2_y = 0L
    @JniField(cast = "uint64_t") var mi_unsync_volume = 0L
    @JniField(cast = "uint64_t") var mi_autosync_threshold = 0L
    @JniField(cast = "uint32_t") var mi_since_sync_seconds16dot16 = 0L
    @JniField(cast = "uint32_t") var mi_autosync_period_seconds16dot16 = 0L
    @JniField(cast = "uint32_t") var mi_since_reader_check_seconds16dot16 = 0L
    @JniField(cast = "uint32_t") var mi_mode = 0L
    @JniField(accessor = "mi_pgop_stat.newly", cast = "uint64_t") var mi_pgop_stat_newly = 0L
    @JniField(accessor = "mi_pgop_stat.cow", cast = "uint64_t") var mi_pgop_stat_cow = 0L
    @JniField(accessor = "mi_pgop_stat.clone", cast = "uint64_t") var mi_pgop_stat_clone = 0L
    @JniField(accessor = "mi_pgop_stat.split", cast = "uint64_t") var mi_pgop_stat_split = 0L
    @JniField(accessor = "mi_pgop_stat.merge", cast = "uint64_t") var mi_pgop_stat_merge = 0L
    @JniField(accessor = "mi_pgop_stat.spill", cast = "uint64_t") var mi_pgop_stat_spill = 0L
    @JniField(accessor = "mi_pgop_stat.unspill", cast = "uint64_t") var mi_pgop_stat_unspill = 0L
    @JniField(accessor = "mi_pgop_stat.wops", cast = "uint64_t") var mi_pgop_stat_wops = 0L

    @SuppressWarnings(Array("nls")) override def toString: String = "{" + "mi_geo_lower=" + mi_geo_lower + ", mi_geo_upper=" + mi_geo_upper + ", mi_geo_current=" + mi_geo_current + ", mi_geo_shrink=" + mi_geo_shrink + ", mi_geo_grow=" + mi_geo_grow + ", mi_mapsize=" + mi_mapsize + ", mi_last_pgno=" + mi_last_pgno + ", mi_recent_txnid=" + mi_recent_txnid + ", mi_latter_reader_txnid=" + mi_latter_reader_txnid + ", mi_self_latter_reader_txnid=" + mi_self_latter_reader_txnid + ", mi_meta0_txnid=" + mi_meta0_txnid + ", mi_meta0_sign=" + mi_meta0_sign + ", mi_meta1_txnid=" + mi_meta1_txnid + ", mi_meta1_sign=" + mi_meta1_sign + ", mi_meta2_txnid=" + mi_meta2_txnid + ", mi_meta2_sign=" + mi_meta2_sign + ", mi_maxreaders=" + mi_maxreaders + ", mi_numreaders=" + mi_numreaders + ", mi_dxb_pagesize=" + mi_dxb_pagesize + ", mi_sys_pagesize=" + mi_sys_pagesize + ", mi_bootid_current_x=" + mi_bootid_current_x + ", mi_bootid_current_y=" + mi_bootid_current_y + ", mi_bootid_meta0_x=" + mi_bootid_meta0_x + ", mi_bootid_meta0_y=" + mi_bootid_meta0_y + ", mi_bootid_meta1_x=" + mi_bootid_meta1_x + ", mi_bootid_meta1_y=" + mi_bootid_meta1_y + ", mi_bootid_meta2_x=" + mi_bootid_meta2_x + ", mi_bootid_meta2_y=" + mi_bootid_meta2_y + ", mi_unsync_volume=" + mi_unsync_volume + ", mi_autosync_threshold=" + mi_autosync_threshold + ", mi_since_sync_seconds16dot16=" + mi_since_sync_seconds16dot16 + ", mi_autosync_period_seconds16dot16=" + mi_autosync_period_seconds16dot16 + ", mi_since_reader_check_seconds16dot16=" + mi_since_reader_check_seconds16dot16 + ", mi_mode=" + mi_mode + ", mi_pgop_stat_newly=" + mi_pgop_stat_newly + ", mi_pgop_stat_cow=" + mi_pgop_stat_cow + ", mi_pgop_stat_clone=" + mi_pgop_stat_clone + ", mi_pgop_stat_split=" + mi_pgop_stat_split + ", mi_pgop_stat_merge=" + mi_pgop_stat_merge + ", mi_pgop_stat_spill=" + mi_pgop_stat_spill + ", mi_pgop_stat_unspill=" + mi_pgop_stat_unspill + ", mi_pgop_stat_wops=" + mi_pgop_stat_wops + '}'
  }*/

  /*
  // Stats Info
  @JniClass(flags = Array(STRUCT, TYPEDEF)) class MDBX_stat {
    @JniField(cast = "uint32_t") var ms_psize = 0L
    @JniField(cast = "uint32_t") var ms_depth = 0L
    @JniField(cast = "uint64_t") var ms_branch_pages = 0L
    @JniField(cast = "uint64_t") var ms_leaf_pages = 0L
    @JniField(cast = "uint64_t") var ms_overflow_pages = 0L
    @JniField(cast = "uint64_t") var ms_entries = 0L
    @JniField(cast = "uint64_t") var ms_mod_txnid = 0L

    @SuppressWarnings(Array("nls")) override def toString: String = "{" + "ms_branch_pages=" + ms_branch_pages + ", ms_psize=" + ms_psize + ", ms_depth=" + ms_depth + ", ms_leaf_pages=" + ms_leaf_pages + ", ms_overflow_pages=" + ms_overflow_pages + ", ms_entries=" + ms_entries + ", ms_mod_txnid=" + ms_mod_txnid + '}'
  }
  */
}

class LibraryWrapper private() extends ILibraryWrapper {

  sealed trait ICursorHandle
  type CursorHandle = Ptr[Byte] with ICursorHandle

  sealed trait IDbiHandle
  type DbiHandle = Ptr[CUnsignedInt] with IDbiHandle // CUnsignedInt intentionally wrapped

  sealed trait IEnvHandle
  type EnvHandle = Ptr[Byte] with IEnvHandle

  sealed trait ITxnHandle
  type TxnHandle = Ptr[Byte] with ITxnHandle

  private[mdbx4s] val LIB = mdbx

  //def CONSTANTS: ILibraryConstants = mdbx_constants
  //import mdbx_constants._

  //def MDBX_VERSION_MAJOR: Int = LIB.MDBX_VERSION_MAJOR
  //def MDBX_VERSION_MINOR: Int = LIB.MDBX_VERSION_MINOR

  private val uIntSize = sizeof[UInt].toInt
  private def _wrapUInt(uint: UInt): Ptr[Byte] = {
    val byteArray = scala.scalanative.runtime.ByteArray.alloc(uIntSize)
    val byteArrayPtr = byteArray.at(0)
    val uintPtr = byteArrayPtr.asInstanceOf[Ptr[UInt]]
    uintPtr.update(0, uint)

    byteArrayPtr
  }
  private def _unwrapUInt(wrappedUInt: Ptr[CUnsignedInt]): UInt = {
    val uintPtr = wrappedUInt //.asInstanceOf[Ptr[UInt]]
    uintPtr(0)
  }

  // TODO: put this version in sqlite4s/CUtils
  private def _bytesToCString(bytes: Array[Byte])(implicit z: Zone): CString = {
    if (bytes == null) return null

    val nBytes = bytes.length
    val cstr: Ptr[CChar] = z.alloc((nBytes + 1).toULong)

    /*var c = 0
    while (c < nBytes) {
      cstr.update(c, bytes(c))
      c += 1
    }
    cstr.update(c, 0.toByte) // NULL CHAR*/

    // TODO: find a way to cast bytes to a bytesPtr and then use CUtils.strcpy?
    val bytesPtr = bytes.asInstanceOf[scalanative.runtime.ByteArray].at(0)
    scalanative.libc.string.memcpy(cstr, bytesPtr, nBytes.toULong)
    cstr(nBytes) = 0.toByte // NULL CHAR

    cstr
  }

  //@inline implicit private def _castPointer(ptr: Pointer): LIB.KVPtr = ptr.asInstanceOf[LIB.KVPtr]
  //@inline implicit private def _pointer2uint(ptr: Pointer): UInt = _unwrapUInt(ptr)

  /*def throwSystemException(rc: Int): Unit = {
    require(rc < 134, s"Unknown system error code $rc") // FIXME: try to retrieve the MAX_ERRNO value

    val cstr = scala.scalanative.libc.string.strerror(rc)
    assert(cstr != null)

    val msg = fromCString(cstr)

    throw new LmdbNativeException.SystemException(rc, msg)

    ()
  }*/

  import scala.scalanative.posix.errno._
  import com.github.mdbx4s.ReturnCode._

  protected val errorBuilderByErrorCode: LongMap[String => MdbxNativeException] = new LongMap[String => MdbxNativeException] ++ Seq(
    EINVAL -> { rcMsg: String => new MdbxNativeException(EINVAL, rcMsg, "Invalid Parameter") },
    EACCES -> { rcMsg: String => new MdbxNativeException(EACCES, rcMsg, "Access Denied") },
    ENODATA -> { rcMsg: String => new MdbxNativeException(ENODATA, rcMsg, "Handle EOF") },
    ENOMEM -> { rcMsg: String => new MdbxNativeException(ENOMEM, rcMsg, "Out of Memory") },
    EROFS -> { rcMsg: String => new MdbxNativeException(EROFS, rcMsg, "File Read Only") },
    ENOSYS -> { rcMsg: String => new MdbxNativeException(ENOSYS, rcMsg, "Not Supported") },
    EIO -> { rcMsg: String => new MdbxNativeException(EIO, rcMsg, "Write Fault") },
    EPERM -> { rcMsg: String => new MdbxNativeException(EPERM, rcMsg, "Invalid Function") },
    EINTR -> { rcMsg: String => new MdbxNativeException(EINTR, rcMsg, "Cancelled") },
    ENOENT -> { rcMsg: String => new MdbxNativeException(ENOENT, rcMsg, "?? no description") }, // MDBX_ENOFILE
    // FIXME: ENOTBLK not exported by SN
    //ENOTBLK -> { rcMsg: String => new MdbxNativeException(ENOTBLK, rcMsg, "?? no description") }, // MDBX_EREMOTE
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

  def mdbx_build_info(): BuildInfo = {
    val build_info_struct = mdbx.mdbx_build
    BuildInfo(
      fromCString(build_info_struct._1),
      fromCString(build_info_struct._2),
      fromCString(build_info_struct._3),
      fromCString(build_info_struct._4),
      fromCString(build_info_struct._5)
    )
  }

  def mdbx_cursor_close(cursor: CursorHandle): Unit = LIB.mdbx_cursor_close(cursor)
  def mdbx_cursor_count(cursor: CursorHandle): Long = {
    val countp = stackalloc[CSize]()
    checkRc(LIB.mdbx_cursor_count(cursor, countp))
    (!countp).toLong
  }
  def mdbx_cursor_del(cursorPtr: CursorHandle, flags: Int): Unit = checkRc(LIB.mdbx_cursor_del(cursorPtr, flags.toUInt))
  def mdbx_cursor_get(cursorPtr: CursorHandle, key: Array[Byte], cursorOp: Int): Option[Array[Byte]] = {
    val keyPtr = stackalloc[mdbx.KVType]()
    bytes2ptr(key, keyPtr)

    val valuePtr = stackalloc[mdbx.KVType]()
    val gotResult = _mdbx_cursor_get(cursorPtr, keyPtr, valuePtr, cursorOp)
    if (!gotResult) return None

    Some(ptr2bytes(valuePtr))
  }
  def mdbx_cursor_seek(cursorPtr: CursorHandle, cursorOp: Int): Option[Tuple2[Array[Byte],Array[Byte]]] = {
    val keyPtr = stackalloc[mdbx.KVType]()
    val valuePtr = stackalloc[mdbx.KVType]()
    val gotResult = _mdbx_cursor_get(cursorPtr, keyPtr, valuePtr, cursorOp)
    if (!gotResult) return None

    Some((ptr2bytes(keyPtr),ptr2bytes(valuePtr)))
  }
  def _mdbx_cursor_get(cursorPtr: CursorHandle, k: mdbx.KVPtr, v: mdbx.KVPtr, cursorOp: Int): Boolean = {
    val rc = LIB.mdbx_cursor_get(cursorPtr, k, v, cursorOp.toUInt)
    if (rc == MDBX_NOTFOUND) return false

    checkRc(rc)

    true
  }

  def mdbx_cursor_open(txnPtr: TxnHandle, dbiPtr: DbiHandle): CursorHandle = {
    val cursorPtr = stackalloc[Ptr[Byte]]()
    checkRc(LIB.mdbx_cursor_open(txnPtr, _unwrapUInt(dbiPtr), cursorPtr))
    (!cursorPtr).asInstanceOf[CursorHandle]
  }
  def mdbx_cursor_put(cursorPtr: CursorHandle, key: Array[Byte], value: Array[Byte], flags: Int): Option[Array[Byte]] = {
    val keyPtr = stackalloc[mdbx.KVType]()
    bytes2ptr(key, keyPtr)

    val valuePtr = stackalloc[mdbx.KVType]()
    bytes2ptr(value, valuePtr)

    val isPutOK = _mdbx_cursor_put(cursorPtr, keyPtr, valuePtr, flags)
    if (isPutOK) return None

    // Return the existing value (meaning it was a dup insert attempt)
    Some(ptr2bytes(valuePtr))
  }
  private def _mdbx_cursor_put(cursorPtr: CursorHandle, key: mdbx.KVPtr, data: mdbx.KVPtr, flags: Int): Boolean = {
    val rc = LIB.mdbx_cursor_put(cursorPtr, key, data, flags.toUInt)

    isPutOK(rc, flags)
  }
  def mdbx_cursor_renew(txnPtr: TxnHandle, cursorPtr: CursorHandle): Unit = checkRc(LIB.mdbx_cursor_renew(txnPtr, cursorPtr))

  /* --- Wrappers for Dbi functions --- */
  def mdbx_dbi_close(envPtr: EnvHandle, dbiPtr: DbiHandle): Unit = LIB.mdbx_dbi_close(envPtr, _unwrapUInt(dbiPtr))

  def mdbx_dbi_flags(txnPtr: TxnHandle, dbiPtr: DbiHandle): Int = {
    val flagsPtr = stackalloc[UInt]()
    checkRc(LIB.mdbx_dbi_flags(txnPtr, _unwrapUInt(dbiPtr), flagsPtr))
    (!flagsPtr).toInt
  }

  def mdbx_dbi_flags_ex(txnPtr: TxnHandle, dbiPtr: DbiHandle): (Int,Int) = {
    val flagsPtr = stackalloc[UInt]()
    val statePtr = stackalloc[UInt]()
    checkRc(LIB.mdbx_dbi_flags_ex(txnPtr, !dbiPtr, flagsPtr, statePtr))
    ((!flagsPtr).toInt, (!statePtr).toInt)
  }

  def mdbx_dbi_open(txnPtr: TxnHandle, name: String, flags: Int): DbiHandle = {
    val dbiPtr = stackalloc[UInt]()

    Zone { implicit z =>
      val nameAsCStr = toCString(name)
      //val nameAsBytes = if (name == null) null else name.getBytes(java.nio.charset.StandardCharsets.UTF_8)
      //val nameAsCStr = if (name == null) null else _bytesToCString(nameAsBytes)
      checkRc(LIB.mdbx_dbi_open(txnPtr, nameAsCStr, flags.toUInt, dbiPtr))
    }

    _wrapUInt(!dbiPtr).asInstanceOf[DbiHandle]
  }

  def mdbx_dbi_sequence(txnPtr: TxnHandle, dbiPtr: DbiHandle, increment: Long): Long = {
    val resultPtr = stackalloc[CUnsignedLong]()
    val rc = LIB.mdbx_dbi_sequence(txnPtr, _unwrapUInt(dbiPtr), resultPtr, increment.toULong)

    checkRc(rc)

    (!resultPtr).toLong
  }

  def mdbx_dbi_stat(txnPtr: TxnHandle, dbiPtr: DbiHandle): Stat = {
    val statPtr = stackalloc[LIB.struct_mdbx_stat]()
    checkRc(LIB.mdbx_dbi_stat(txnPtr, _unwrapUInt(dbiPtr), statPtr, sizeof[LIB.struct_mdbx_stat]))

    _mdbx_stat_struct_2_object(statPtr)
  }

  @inline
  private def _mdbx_stat_struct_2_object(statPtr: Ptr[LIB.struct_mdbx_stat]): Stat = {

    val stat: LibraryWrapper.struct_mdbx_stat_ops = statPtr

    Stat(
      stat.psize.toInt,
      stat.depth.toInt,
      stat.branch_pages.toLong,
      stat.leaf_pages.toLong,
      stat.overflow_pages.toLong,
      stat.entries.toLong
    )
  }

  def mdbx_del(txnPtr: TxnHandle, dbiPtr: DbiHandle, key: Array[Byte], value: Option[Array[Byte]]): Boolean = {
    val keyPtr = stackalloc[mdbx.KVType]()
    bytes2ptr(key, keyPtr)

    if (value.isEmpty) {
      _mdbx_del(txnPtr, dbiPtr, keyPtr, null)
    } else {
      val valuePtr = stackalloc[mdbx.KVType]()
      bytes2ptr(value.get, valuePtr)
      _mdbx_del(txnPtr, dbiPtr, keyPtr, valuePtr)
    }

  }

  private def _mdbx_del(txnPtr: TxnHandle, dbiPtr: DbiHandle, key: mdbx.KVPtr, data: mdbx.KVPtr): Boolean = {
    val rc = LIB.mdbx_del(txnPtr, _unwrapUInt(dbiPtr), key, data)
    if (rc == MDBX_NOTFOUND) return false

    checkRc(rc)

    true
  }

  def mdbx_drop(txnPtr: TxnHandle, dbiPtr: DbiHandle, del: Boolean): Unit = {
    checkRc(LIB.mdbx_drop(txnPtr, _unwrapUInt(dbiPtr), del))
  }

  def mdbx_env_close(envPtr: EnvHandle): Unit = checkRc(LIB.mdbx_env_close(envPtr))
  def mdbx_env_close_ex(envPtr: EnvHandle, dontSync: Boolean): Unit = checkRc(LIB.mdbx_env_close_ex(envPtr, dontSync))
  def mdbx_env_copy(envPtr: EnvHandle, path: java.io.File, flags: Int): Unit = {
    val rc = Zone { implicit z =>
      LIB.mdbx_env_copy(envPtr, toCString(path.getAbsolutePath), flags.toUInt)
    }
    checkRc(rc)
  }
  def mdbx_env_create(): EnvHandle = {
    val envPtr = stackalloc[Ptr[Byte]]()
    checkRc(LIB.mdbx_env_create(envPtr))

    (!envPtr).asInstanceOf[EnvHandle]
  }
  //def mdbx_env_get_fd(@In env: Pointer, @In fd: Pointer): Int
  def mdbx_env_get_flags(envPtr: EnvHandle): Int = {
    val flagsPtr = stackalloc[CUnsignedInt]()
    checkRc(LIB.mdbx_env_get_flags(envPtr, flagsPtr))

    (!flagsPtr).toInt
  }

  def mdbx_env_get_maxreaders(envPtr: EnvHandle): Int = {
    val maxReadersPtr = stackalloc[CUnsignedInt]()
    checkRc(LIB.mdbx_env_get_maxreaders(envPtr, maxReadersPtr))

    (!maxReadersPtr).toInt
  }
  def mdbx_env_get_maxkeysize_ex(envPtr: EnvHandle, flags: Int): Int = LIB.mdbx_env_get_maxkeysize_ex(envPtr, flags.toUInt)

  def mdbx_env_get_maxdbs(envPtr: EnvHandle): Int = {
    val maxDbsPtr = stackalloc[CUnsignedInt]()
    checkRc(LIB.mdbx_env_get_maxdbs(envPtr, maxDbsPtr))

    (!maxDbsPtr).toInt
  }

  //def mdb_env_get_path(@In env: Pointer, path: String): Int
  // FIXME: implement me
 /*def mdbx_env_info(envPtr: Pointer): EnvInfo = {
    val infoPtr = stackalloc[LIB.struct_lmdb_env_info]()
    checkRc(LIB.mdbx_env_info(envPtr, infoPtr))

    val info: LibraryWrapper.struct_lmdb_env_info_ops = infoPtr

    // FIXME: find a way to get the memory address as a Long value
    val mapAddress = 0L //if (info.mapaddr == null) 0L else info.mapaddr.address

    EnvInfo(
      mapAddress,
      info.mapsize.toLong,
      info.last_pgno.toLong,
      info.last_txnid.toLong,
      info.maxreaders.toInt,
      info.numreaders.toInt
    )
  }*/

  def mdbx_env_info_ex(envPtr: EnvHandle, txnPtr: TxnHandle): EnvInfo = {
    val infoPtr = stackalloc[LIB.struct_mdbx_env_info]()

    checkRc(LIB.mdbx_env_info_ex(envPtr, txnPtr, infoPtr, sizeof[LIB.struct_mdbx_env_info]))

    val info: LibraryWrapper.struct_mdbx_env_info_ops = infoPtr

    val env_geo = info.geo

    EnvInfo(
      geo = EnvInfo.Geo(
        lower = env_geo.lower.toLong,
        upper = env_geo.upper.toLong,
        current = env_geo.current.toLong,
        shrink = env_geo.shrink.toLong,
        grow = env_geo.grow.toLong
      ),
      mapSize = info.mapsize.toLong,
      lastPgNo = info.last_pgno.toLong,
      recentTxnId = info.recent_txnid.toLong,
      latterReaderTxnId = info.latter_reader_txnid.toLong,
      selfLatterReaderTxnId = info.self_latter_reader_txnid.toLong,
      //meta: EnvInfo#Meta,
      maxReaders = info.maxreaders.toInt,
      numReaders = info.numreaders.toInt,
      dxbPageSize = info.dxb_pagesize.toInt,
      sysPageSize = info.sys_pagesize.toInt,
      //bootId: EnvInfo#BootId,
      unsyncVolume = info.unsync_volume.toLong,
      autosyncThreshold = info.autosync_threshold.toLong,
      sinceSyncSeconds16dot16 = info.since_sync_seconds16dot16.toInt,
      autosyncPeriodSeconds16dot16 = info.autosync_period_seconds16dot16.toInt,
      sinceReaderCheckSeconds16dot16 = info.since_reader_check_seconds16dot16.toInt,
      mode = info.mode.toInt
      //pgopStat: EnvInfo#PgopStat
    )
  }

  def mdbx_env_open(envPtr: EnvHandle, path: java.io.File, flags: Int, mode: Int): Unit = {
    val rc = Zone { implicit z =>
      LIB.mdbx_env_open(envPtr, toCString(path.getAbsolutePath), flags, mode)
    }
    checkRc(rc)
  }
  //def mdb_env_set_flags(@In env: Pointer, flags: Int, onoff: Int): Int
  def mdbx_env_set_mapsize(envPtr: EnvHandle, size: Long): Unit = checkRc(LIB.mdbx_env_set_mapsize(envPtr, size.toULong))
  def mdbx_env_set_maxdbs(envPtr: EnvHandle, dbs: Int): Unit = checkRc(LIB.mdbx_env_set_maxdbs(envPtr, dbs))
  def mdbx_env_set_maxreaders(envPtr: EnvHandle, readers: Int): Unit = checkRc(LIB.mdbx_env_set_maxreaders(envPtr, readers))
  def mdbx_env_stat(envPtr: EnvHandle): Stat = {
    val statPtr = stackalloc[LIB.struct_mdbx_stat]()
    checkRc(LIB.mdbx_env_stat(envPtr, statPtr, sizeof[LIB.struct_mdbx_stat]))

    _mdbx_stat_struct_2_object(statPtr)
  }

  def mdbx_env_stat_ex(envPtr: EnvHandle, txnPtr: TxnHandle): Stat = {
    val statPtr = stackalloc[LIB.struct_mdbx_stat]()
    checkRc(LIB.mdbx_env_stat_ex(envPtr, txnPtr, statPtr, sizeof[LIB.struct_mdbx_stat]))

    _mdbx_stat_struct_2_object(statPtr)
  }

  def mdbx_env_sync_ex(envPtr: EnvHandle, force: Boolean, nonblock: Boolean): Unit = {
    checkRc(LIB.mdbx_env_sync_ex(envPtr, force, nonblock))
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
    val pageSizePtr = stackalloc[CLong]()
    val totalPagesPtr = stackalloc[CLong]()
    val availPagesPtr = stackalloc[CLong]()
    LIB.mdbx_get_sysraminfo(pageSizePtr,totalPagesPtr,availPagesPtr)
    RamInfo(!pageSizePtr, !totalPagesPtr, !availPagesPtr)
  }

  def mdbx_get(txnPtr: TxnHandle, dbiPtr: DbiHandle, key: Array[Byte]): Option[Array[Byte]] = {
    val keyPtr = stackalloc[mdbx.KVType]()
    bytes2ptr(key, keyPtr)

    val valuePtr = stackalloc[mdbx.KVType]()
    val gotResult = _mdbx_get(txnPtr, dbiPtr, keyPtr, valuePtr)
    if (!gotResult) return None

    Some(ptr2bytes(valuePtr))
  }

  private def _mdbx_get(txnPtr: TxnHandle, dbiPtr: DbiHandle, key: mdbx.KVPtr, data: mdbx.KVPtr): Boolean = {
    val rc = LIB.mdbx_get(txnPtr, _unwrapUInt(dbiPtr), key, data)
    if (rc == MDBX_NOTFOUND) return false

    checkRc(rc)

    true
  }

  def mdbx_put(txnPtr: TxnHandle, dbiPtr: DbiHandle, key: Array[Byte], value: Array[Byte], flags: Int): Option[Array[Byte]] = {
    val keyPtr = stackalloc[mdbx.KVType]()
    bytes2ptr(key, keyPtr)

    val valuePtr = stackalloc[mdbx.KVType]()
    bytes2ptr(value, valuePtr)

    val isPutOK = _mdbx_put(txnPtr, dbiPtr, keyPtr, valuePtr, flags)
    if (isPutOK) return None

    // Return the existing value (meaning it was a dup insert attempt)
    Some(ptr2bytes(valuePtr))
  }

  private def _mdbx_put(txnPtr: TxnHandle, dbiPtr: DbiHandle, key: mdbx.KVPtr, data: mdbx.KVPtr, flags: Int): Boolean = {
    val rc = LIB.mdbx_put(txnPtr, _unwrapUInt(dbiPtr), key, data, flags)

    isPutOK(rc, flags)
  }

  def mdbx_replace(txn: TxnHandle, dbiPtr: DbiHandle, key: Array[Byte], new_value: Array[Byte], flags: Int): Array[Byte] = {
    val keyPtr = stackalloc[mdbx.KVType]()
    bytes2ptr(key, keyPtr)

    val newValuePtr = stackalloc[mdbx.KVType]()
    bytes2ptr(new_value, newValuePtr)

    val oldValuePtr = stackalloc[mdbx.KVType]()

    val rc = LIB.mdbx_replace(txn, _unwrapUInt(dbiPtr), keyPtr, newValuePtr, oldValuePtr, flags)
    checkRc(rc)

    ptr2bytes(oldValuePtr)
  }

  // TODO: delete me
  /*def mdbx_set_compare[T >: Null](txn: Txn[T, Pointer], dbiPtr: Pointer, comparator: Comparator[T]): AnyRef = {

    val kvFactory = txn.keyValFactory

    val ccb: CFuncPtr2[Ptr[Byte],Ptr[Byte],Int] = { (keyA: Ptr[Byte], keyB: Ptr[Byte]) =>
      kvFactory.compareKeys(keyA, keyB, comparator)
    }

    checkRc(LIB.mdb_set_compare(txn.pointer, dbiPtr, ccb))

    ccb
  }*/

  def mdbx_strerror(rc: Int): String = fromCString(LIB.mdbx_strerror(rc))

  /* --- Wrappers for TXN functions --- */
  def mdbx_txn_abort(txnPtr: TxnHandle): Unit = {
    //println("Txn is being aborted")
    LIB.mdbx_txn_abort(txnPtr)
  }

  def mdbx_txn_begin(env: EnvHandle, txnParentPtr: TxnHandle, flags: Int): TxnHandle = {
    val txnPtrPtr = stackalloc[Ptr[Byte]]()

    //println(s"Txn is beginning (is parent null = ${txnParentPtr == null}, flags = $flags)")
    val rc = LIB.mdbx_txn_begin(env, txnParentPtr, flags, txnPtrPtr)
    checkRc(rc)

    //println("Txn has begun")
    (!txnPtrPtr).asInstanceOf[TxnHandle]
  }

  def mdbx_txn_commit(txnPtr: TxnHandle): Unit = {
    //println("Txn is being committed")
    checkRc(LIB.mdbx_txn_commit(txnPtr))
  }

  def mdbx_txn_commit_ex(txnPtr: TxnHandle): CommitLatency = {
    val latencyPtr = stackalloc[mdbx.struct_mdbx_commit_latency]()

    checkRc(LIB.mdbx_txn_commit_ex(txnPtr, latencyPtr))

    val latency_struct: LibraryWrapper.struct_mdbx_commit_latency_ops = latencyPtr

    CommitLatency(
      latency_struct.preparation.toInt,
      latency_struct.gc.toInt,
      latency_struct.audit.toInt,
      latency_struct.write.toInt,
      latency_struct.sync.toInt,
      latency_struct.ending.toInt,
      latency_struct.whole.toInt
    )
  }

  //def mdb_txn_env(txnPtr: TxnHandle): Pointer = LIB.mdb_txn_env(txn)
  def mdbx_txn_id(txnPtr: TxnHandle): Long = LIB.mdbx_txn_id(txnPtr)
  def mdbx_txn_info(txnPtr: TxnHandle, scanRlt: Boolean): TxnInfo = {
    val infoPtr = stackalloc[mdbx.struct_mdbx_txn_info]()

    checkRc(LIB.mdbx_txn_info(txnPtr, infoPtr, scanRlt))

    val info_struct: LibraryWrapper.struct_mdbx_txn_info_ops = infoPtr

    TxnInfo(
      info_struct.id.toLong,
      info_struct.reader_lag.toLong,
      info_struct.space_used.toLong,
      info_struct.space_limit_soft.toLong,
      info_struct.space_limit_hard.toLong,
      info_struct.space_retired.toLong,
      info_struct.space_leftover.toLong,
      info_struct.space_dirty.toLong
    )
  }
  def mdbx_txn_renew(txnPtr: TxnHandle): Unit = checkRc(LIB.mdbx_txn_renew(txnPtr))
  def mdbx_txn_reset(txnPtr: TxnHandle): Unit = LIB.mdbx_txn_reset(txnPtr)

  def mdbx_version(): Version = {
    /*val major = stackalloc[CInt]()
    val minor = stackalloc[CInt]()
    val patch = stackalloc[CInt]()

    val versionAsStr: CString = lmdb.mdb_version(major, minor, patch)*/
    // FIXME: can't retrieve the version information
    val version_struct = LIB.mdbx_version

    Version(0,0,0,0)
    //Version(version_struct._1.toInt, version_struct._2.toInt, version_struct._3.toInt, version_struct._4.toInt)
  }

  // Error methods
  /*def mdbx_strerror(rc: Int): String = fromCString(LIB.mdbx_strerror(rc))

  @JniMethod(cast = "char *") def mdbx_strerror_r(errnum: Int, @JniArg(cast = "char *") buf: Long, @JniArg(cast = "size_t") buflen: Long): Long

  @JniMethod(conditional = "defined(_WIN32) || defined(_WIN64)", cast = "char *") def mdbx_strerror_ANSI2OEM(errnum: Int): Long

  @JniMethod(conditional = "defined(_WIN32) || defined(_WIN64)", cast = "char *") def mdbx_strerror_r_ANSI2OEM(errnum: Int, @JniArg(cast = "char *") buf: Long, @JniArg(cast = "size_t") buflen: Long): Long
*/

  //	@JniMethod
  //	public static final native int mdbx_env_set_hsr(
  //			@JniArg(cast = "MDBX_env *") long env,

  //			MDBX_hsr_func *hsr_callback)

  /*
  // ENV methods
  @JniMethod def mdbx_env_create(@JniArg(cast = "MDBX_env **", flags = Array(NO_IN)) penv: Array[Long]): Int

  @JniMethod def mdbx_env_open(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "const char *") pathname: String, @JniArg(cast = "unsigned") flags: Int, @JniArg(cast = "mdbx_mode_t") mode: Int): Int

  @JniMethod def mdbx_env_copy(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "const char *") path: String, @JniArg(cast = "unsigned") flags: Int): Int

  @JniMethod def mdbx_env_copy2fd(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "mdbx_filehandle_t") fd: Long, @JniArg(cast = "unsigned int") flags: Int): Int

  @JniMethod def mdbx_env_stat(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "MDBX_stat *", flags = Array(NO_IN)) stat: JNI.MDBX_stat, @JniArg(cast = "size_t") bytes: Long): Int

  @JniMethod def mdbx_env_stat_ex(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long, @JniArg(cast = "MDBX_stat *", flags = Array(NO_IN)) stat: JNI.MDBX_stat, @JniArg(cast = "size_t") bytes: Long): Int

  @JniMethod def mdbx_env_info(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "MDBX_envinfo *", flags = Array(NO_IN)) info: JNI.MDBX_envinfo, @JniArg(cast = "size_t") bytes: Long): Int

  @JniMethod def mdbx_env_info_ex(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long, @JniArg(cast = "MDBX_envinfo *", flags = Array(NO_IN)) info: JNI.MDBX_envinfo, @JniArg(cast = "size_t") bytes: Long): Int

  @JniMethod def mdbx_env_close(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long): Int

  @JniMethod def mdbx_env_close_ex(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, dont_sync: Int): Int

  @JniMethod def mdbx_env_sync(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long): Int

  @JniMethod def mdbx_env_sync_ex(
    @JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, force: Int, //_Bool
    nonblock: Int
  ): Int

  @JniMethod def mdbx_env_sync_poll(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long): Int

  @JniMethod def mdbx_env_set_flags(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "unsigned") flags: Int, @JniArg(cast = "int") onoff: Int): Int

  @JniMethod def mdbx_env_get_flags(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "unsigned *") flags: Array[Long]): Int

  @JniMethod def mdbx_env_get_path(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "const char **", flags = Array(NO_IN)) path: Array[Long]): Int

  @JniMethod def mdbx_env_get_fd(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "mdbx_filehandle_t *") fd: Array[Long]): Int

  @JniMethod def mdbx_env_set_geometry(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "intptr_t") size_lower: Long, @JniArg(cast = "intptr_t") size_now: Long, @JniArg(cast = "intptr_t") size_upper: Long, @JniArg(cast = "intptr_t") growth_step: Long, @JniArg(cast = "intptr_t") shrink_threshold: Long, @JniArg(cast = "intptr_t") pagesize: Long): Int

  @JniMethod def mdbx_env_set_mapsize(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "size_t") size: Long): Int

  @JniMethod def mdbx_env_get_maxreaders(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "unsigned *") readers: Array[Long]): Int

  @JniMethod def mdbx_env_set_maxreaders(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "unsigned") readers: Int): Int

  @JniMethod def mdbx_env_get_maxdbs(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "unsigned *") flags: Array[Long]): Int

  @JniMethod def mdbx_env_set_maxdbs(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "uint32_t") dbs: Long): Int

  @JniMethod def mdbx_env_get_maxkeysize(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long): Int

  @JniMethod(cast = "void *") def mdbx_env_get_userctx(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long): Long

  @JniMethod def mdbx_env_set_userctx(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "void *") ctx: Long): Int

  //TODO: MDBX_assert_func

  //TODO: mdbx_env_set_assert

  // TXN methods
  @JniMethod def mdbx_txn_begin(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) parent: Long, @JniArg(cast = "unsigned") flags: Long, @JniArg(cast = "MDBX_txn **", flags = Array(NO_IN)) txn: Array[Long]): Int

  @JniMethod def mdbx_txn_begin_ex(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) parent: Long, @JniArg(cast = "unsigned") flags: Long, @JniArg(cast = "MDBX_txn **", flags = Array(NO_IN)) txn: Array[Long], @JniArg(cast = "void *") ctx: Long): Int

  /**
   * Marks transaction as broken.
   *
   * Function keeps the transaction handle and corresponding locks, but makes impossible to perform any
   * operations within a broken transaction. Broken transaction must then be aborted explicitly later.
   *
   * @param txn
   * @return
   */
  @JniMethod def mdbx_txn_break(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long): Int

  @JniMethod(cast = "MDBX_env *") def mdbx_txn_env(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long): Long

  @JniMethod def mdbx_txn_flags(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long): Int

  @JniMethod(cast = "uint64_t") def mdbx_txn_id(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long): Long

  @JniMethod def mdbx_txn_commit(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long): Int

  @JniMethod def mdbx_txn_commit_ex(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long, @JniArg(cast = "MDBX_commit_latency *") latency: JNI.MDBX_commit_latency): Int

  @JniMethod def mdbx_txn_abort(@JniArg(cast = "MDBX_txn *") txn: Long): Unit

  @JniMethod def mdbx_txn_reset(@JniArg(cast = "MDBX_txn *") txn: Long): Unit

  @JniMethod def mdbx_txn_renew(@JniArg(cast = "MDBX_txn *") txn: Long): Int

  /**
   * Return information about the MDBX transaction.
   *
   * @param txn
   * A transaction handle returned by mdbx_txn_begin()
   * @param info
   * The address of an MDBX_txn_info structure where the information will be copied.
   * @param scanRlt
   * The boolean flag controls the scan of the read lock table to provide complete information. Such
   * scan is relatively expensive and you can avoid it if corresponding fields are not needed. See
   * description of MDBX_txn_info.
   * @return A non-zero error value on failure and 0 on success.
   */
  @JniMethod def mdbx_txn_info(@JniArg(cast = "MDBX_txn *") txn: Long, @JniArg(cast = "MDBX_txn_info *", flags = Array(NO_IN)) info: JNI.MDBX_txn_info, @JniArg(cast = "int") scanRlt: Int): Int

  @JniMethod def mdbx_txn_set_userctx(@JniArg(cast = "MDBX_txn *") txn: Long, @JniArg(cast = "void *") ctx: Long): Int

  @JniMethod(cast = "void *") def mdbx_txn_get_userctx(@JniArg(cast = "MDBX_txn *") txn: Long): Long

  // DBI methods
  @JniMethod def mdbx_dbi_open(@JniArg(cast = "MDBX_txn *") txn: Long, @JniArg(cast = "const char *") name: String, @JniArg(cast = "unsigned") flags: Int, @JniArg(cast = "uint32_t *") dbi: Array[Long]): Int

  @JniMethod def mdbx_dbi_open_ex(@JniArg(cast = "MDBX_txn *") txn: Long, @JniArg(cast = "const char *") name: String, @JniArg(cast = "unsigned") flags: Int, @JniArg(cast = "uint32_t *") dbi: Array[Long], @JniArg(cast = "int(*)(const MDBX_val *, const MDBX_val *)", flags = ArgFlag.POINTER_ARG) keycmp: Long, @JniArg(cast = "int(*)(const MDBX_val *, const MDBX_val *)", flags = ArgFlag.POINTER_ARG) datacmp: Long): Int

  @JniMethod def mdbx_dbi_stat(@JniArg(cast = "MDBX_txn *") txn: Long, @JniArg(cast = "uint32_t") dbi: Long, @JniArg(cast = "MDBX_stat *", flags = Array(NO_IN)) stat: JNI.MDBX_stat, @JniArg(cast = "size_t") bytes: Long): Int

  @JniMethod def mdbx_dbi_flags(@JniArg(cast = "MDBX_txn *") txn: Long, @JniArg(cast = "uint32_t") dbi: Long, @JniArg(cast = "unsigned *") flags: Array[Long]): Int

  @JniMethod def mdbx_dbi_flags_ex(@JniArg(cast = "MDBX_txn *") txn: Long, @JniArg(cast = "uint32_t") dbi: Long, @JniArg(cast = "unsigned *") flags: Array[Long], @JniArg(cast = "unsigned *") state: Array[Long]): Int

  @JniMethod def mdbx_dbi_close(@JniArg(cast = "MDBX_env *") env: Long, @JniArg(cast = "uint32_t") dbi: Long): Unit

  @JniMethod def mdbx_dbi_dupsort_depthmask(@JniArg(cast = "MDBX_txn *") txn: Long, @JniArg(cast = "uint32_t") dbi: Long, @JniArg(cast = "uint32_t *", flags = Array(NO_IN)) mask: Array[Long]): Int

  @JniMethod def mdbx_dbi_sequence(@JniArg(cast = "MDBX_txn *") txn: Long, @JniArg(cast = "uint32_t") dbi: Long, @JniArg(cast = "uint64_t *", flags = Array(NO_IN)) result: Array[Long], @JniArg(cast = "uint64_t") increment: Long): Int

  // CRUD methods
  @JniMethod def mdbx_get(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long, @JniArg(cast = "uint32_t") dbi: Long, @JniArg(cast = "MDBX_val *", flags = Array(NO_OUT)) key: JNI.MDBX_val, @JniArg(cast = "MDBX_val *") data: JNI.MDBX_val): Int

  @JniMethod def mdbx_get_ex(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long, @JniArg(cast = "uint32_t") dbi: Long, @JniArg(cast = "MDBX_val *") key: JNI.MDBX_val, @JniArg(cast = "MDBX_val *") data: JNI.MDBX_val, @JniArg(cast = "size_t *") values_count: Array[Long]): Int


  //	public static final native int mdbx_get_attr(
  //			@JniArg(cast = "MDBX_txn *", flags={NO_OUT}) long txn,
  //			@JniArg(cast = "uint32_t") long dbi,
  //			@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val key,
  //			@JniArg(cast = "MDBX_val *") MDBX_val data,
  //			@JniArg(cast = "uint_fast64_t *") long[] pattr);

  @JniMethod def mdbx_get_equal_or_great(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long, @JniArg(cast = "uint32_t") dbi: Long, @JniArg(cast = "MDBX_val *") key: JNI.MDBX_val, @JniArg(cast = "MDBX_val *") data: JNI.MDBX_val): Int

  @JniMethod def mdbx_put(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long, @JniArg(cast = "uint32_t") dbi: Long, @JniArg(cast = "MDBX_val *", flags = Array(NO_OUT)) key: JNI.MDBX_val, @JniArg(cast = "MDBX_val *") data: JNI.MDBX_val, @JniArg(cast = "unsigned") flags: Int): Int


  //	public static final native int mdbx_put_attr(


  //			@JniArg(cast = "uint_fast64_t *") long[] attr,
  //			@JniArg(cast = "unsigned") int flags);

  @JniMethod def mdbx_replace(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long, @JniArg(cast = "uint32_t") dbi: Long, @JniArg(cast = "MDBX_val *", flags = Array(NO_OUT)) key: JNI.MDBX_val, @JniArg(cast = "MDBX_val *", flags = Array(NO_OUT)) new_data: JNI.MDBX_val, @JniArg(cast = "MDBX_val *") old_data: JNI.MDBX_val, @JniArg(cast = "unsigned") flags: Int): Int

  @JniMethod def mdbx_del(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long, @JniArg(cast = "uint32_t") dbi: Long, @JniArg(cast = "MDBX_val *", flags = Array(NO_OUT)) key: JNI.MDBX_val, @JniArg(cast = "MDBX_val *", flags = Array(NO_OUT)) data: JNI.MDBX_val): Int

  @JniMethod def mdbx_drop(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long, @JniArg(cast = "uint32_t") dbi: Long, del: Int): Int

  @JniMethod def mdbx_canary_get(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long, @JniArg(cast = "MDBX_canary *") canary: JNI.MDBX_canary): Int

  @JniMethod def mdbx_canary_put(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long, @JniArg(cast = "MDBX_canary *", flags = Array(NO_OUT)) canary: JNI.MDBX_canary): Int


  // Cursor methods
  @JniMethod def mdbx_cursor_open(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long, @JniArg(cast = "uint32_t") dbi: Long, @JniArg(cast = "MDBX_cursor **", flags = Array(NO_IN)) cursor: Array[Long]): Int

  @JniMethod def mdbx_cursor_close(@JniArg(cast = "MDBX_cursor *", flags = Array(NO_OUT)) cursor: Long): Unit

  @JniMethod def mdbx_cursor_renew(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long, @JniArg(cast = "MDBX_cursor *", flags = Array(NO_OUT)) cursor: Long): Int

  @JniMethod def mdbx_cursor_bind(@JniArg(cast = "MDBX_txn *", flags = Array(NO_OUT)) txn: Long, @JniArg(cast = "MDBX_cursor *", flags = Array(NO_OUT)) cursor: Long, @JniArg(cast = "uint32_t") dbi: Long): Int

  @JniMethod def mdbx_cursor_copy(@JniArg(cast = "MDBX_cursor *", flags = Array(NO_OUT)) src: Long, @JniArg(cast = "MDBX_cursor *") dest: Long): Int

  @JniMethod def mdbx_cursor_get(@JniArg(cast = "MDBX_cursor *", flags = Array(NO_OUT)) cursor: Long, @JniArg(cast = "MDBX_val *") key: JNI.MDBX_val, @JniArg(cast = "MDBX_val *") data: JNI.MDBX_val, @JniArg(cast = "MDBX_cursor_op", flags = Array(NO_OUT)) op: Int): Int


  //	public static final native int mdbx_cursor_get_attr(
  //			@JniArg(cast = "MDBX_cursor *", flags={NO_OUT}) long cursor,
  //			@JniArg(cast = "MDBX_val *") MDBX_val key,

  //			@JniArg(cast = "uint_fast64_t *") long[] pattr,
  //			@JniArg(cast = "MDBX_cursor_op", flags={NO_OUT}) int op);

  @JniMethod def mdbx_cursor_put(@JniArg(cast = "MDBX_cursor *", flags = Array(NO_OUT)) cursor: Long, @JniArg(cast = "MDBX_val *") key: JNI.MDBX_val, @JniArg(cast = "MDBX_val *") data: JNI.MDBX_val, @JniArg(cast = "unsigned") flags: Int): Int


  //	public static final native int mdbx_cursor_put_attr(


  //			@JniArg(cast = "MDBX_val *", flags={NO_OUT}) MDBX_val data,
  //			@JniArg(cast = "uint_fast64_t") long attr,


  @JniMethod(cast = "MDBX_txn *") def mdbx_cursor_txn(@JniArg(cast = "MDBX_cursor *", flags = Array(NO_OUT)) cursor: Long): Long

  @JniMethod(cast = "uint32_t") def mdbx_cursor_dbi(@JniArg(cast = "MDBX_cursor *", flags = Array(NO_OUT)) cursor: Long): Long

  @JniMethod def mdbx_cursor_del(@JniArg(cast = "MDBX_cursor *", flags = Array(NO_OUT)) cursor: Long, @JniArg(cast = "unsigned") flags: Int): Int

  @JniMethod def mdbx_cursor_count(@JniArg(cast = "MDBX_cursor *", flags = Array(NO_OUT)) cursor: Long, @JniArg(cast = "size_t *") countp: Array[Long]): Int

  @JniMethod def mdbx_cursor_eof(@JniArg(cast = "MDBX_cursor *", flags = Array(NO_OUT)) cursor: Long): Int

  @JniMethod def mdbx_cursor_on_first(@JniArg(cast = "MDBX_cursor *", flags = Array(NO_OUT)) cursor: Long): Int

  @JniMethod def mdbx_cursor_on_last(@JniArg(cast = "MDBX_cursor *", flags = Array(NO_OUT)) cursor: Long): Int

  @JniMethod(cast = "MDBX_cursor *") def mdbx_cursor_create(@JniArg(cast = "void *") ctx: Long): Long

  @JniMethod(cast = "void *") def mdbx_cursor_get_userctx(@JniArg(cast = "MDBX_cursor *", flags = Array(NO_OUT)) env: Long): Long

  @JniMethod def mdbx_cursor_set_userctx(@JniArg(cast = "MDBX_cursor *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "void *") ctx: Long): Int

  // Compare methods (and K2V and V2K functions)
  @JniMethod def mdbx_cmp(@JniArg(cast = "MDBX_txn *") txn: Long, @JniArg(cast = "uint32_t") dbi: Long, @JniArg(cast = "MDBX_val *", flags = Array(NO_OUT)) a: JNI.MDBX_val, @JniArg(cast = "MDBX_val *", flags = Array(NO_OUT)) b: JNI.MDBX_val): Int

  @JniMethod def mdbx_dcmp(@JniArg(cast = "MDBX_txn *") txn: Long, @JniArg(cast = "uint32_t") dbi: Long, @JniArg(cast = "MDBX_val *", flags = Array(NO_OUT)) a: JNI.MDBX_val, @JniArg(cast = "MDBX_val *", flags = Array(NO_OUT)) b: JNI.MDBX_val): Int

  @JniMethod(cast = "int *") def mdbx_get_keycmp(@JniArg(cast = "unsigned") flags: Int): Long

  @JniMethod(cast = "int *") def mdbx_get_datacmp(@JniArg(cast = "unsigned") flags: Int): Long

  @JniMethod(cast = "uint64_t") def mdbx_key_from_jsonInteger(@JniArg(cast = "const int64_t") json_integer: Long): Long

  @JniMethod(cast = "uint64_t") def mdbx_key_from_double(@JniArg(cast = "const double") ieee754_64bit: Double): Long

  @JniMethod(cast = "uint64_t") def mdbx_key_from_ptrdouble(@JniArg(cast = "const double *const") ieee754_64bit: Array[Long]): Long

  @JniMethod(cast = "uint32_t") def mdbx_key_from_float(@JniArg(cast = "const float") ieee754_32bit: Float): Int

  @JniMethod(cast = "uint32_t") def mdbx_key_from_ptrfloat(@JniArg(cast = "const float *const") ieee754_32bit: Array[Long]): Int

  @JniMethod(cast = "uint64_t") def mdbx_key_from_int64(@JniArg(cast = "const int64_t") i64: Long): Long

  @JniMethod(cast = "uint32_t") def mdbx_key_from_int32(@JniArg(cast = "const int32_t") i32: Int): Int

  @JniMethod(cast = "int64_t") def mdbx_jsonInteger_from_key(@JniArg(flags = Array(BY_VALUE)) `val`: JNI.MDBX_val): Long

  @JniMethod def mdbx_double_from_key(@JniArg(flags = Array(BY_VALUE)) `val`: JNI.MDBX_val): Double

  @JniMethod def mdbx_float_from_key(@JniArg(flags = Array(BY_VALUE)) `val`: JNI.MDBX_val): Float

  @JniMethod(cast = "int32_t") def mdbx_int32_from_key(@JniArg(flags = Array(BY_VALUE)) `val`: JNI.MDBX_val): Int

  @JniMethod(cast = "int64_t") def mdbx_int64_from_key(@JniArg(flags = Array(BY_VALUE)) `val`: JNI.MDBX_val): Long

  // Reader methods
  @JniMethod def mdbx_reader_check(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "int *") dead: Array[Int]): Int

  @JniMethod def mdbx_reader_list(@JniArg(cast = "MDBX_env *", flags = Array(NO_OUT)) env: Long, @JniArg(cast = "int(*)(void *, int, int, mdbx_pid_t, mdbx_tid_t, uint64_t, uint64_t, size_t, size_t)", flags = ArgFlag.POINTER_ARG) func: Long, @JniArg(cast = "void *") ctx: Long): Int


  // Extra operation methods

  //TODO: MDBX_msg_func

  //TODO: mdbx_reader_list

  @JniMethod(cast = "size_t") def mdbx_default_pagesize: Long


  //TODO: mdbx_dkey

  @JniMethod def mdbx_env_set_syncbytes(@JniArg(cast = "MDBX_env *") env: Long, @JniArg(cast = "size_t") bytes: Long): Int

  //TODO: MDBX_oom_func

  //TODO: mdbx_env_set_oomfunc


  //TODO: debug and page visit functions

  @JniMethod def mdbx_is_dirty(@JniArg(cast = "MDBX_txn *") txn: Long, @JniArg(cast = "const void *") ptr: Long): Int


  */

  // --- Conversion utilities for keys and values --- //
  def bytes2ptr(buffer: Array[Byte], kvPtr: mdbx.KVPtr): Unit = {
    val bufferLen = buffer.length

    kvPtr._2 = bufferLen

    if (bufferLen > 0) {
      kvPtr._1 = buffer.asInstanceOf[scala.scalanative.runtime.ByteArray].at(0)
    }
  }

  def ptr2bytes(kvPtr: mdbx.KVPtr): Array[Byte] = {
    val size = kvPtr._2

    val byteArray = scala.scalanative.runtime.ByteArray.alloc(size.toInt) // allocate memory
    val byteArrayPtr = byteArray.at(0)

    scala.scalanative.libc.string.memcpy(byteArrayPtr, kvPtr._1, size.toULong)

    byteArray.asInstanceOf[Array[Byte]]
  }
}
