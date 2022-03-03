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

object MaskedFlag {

  /**
   * Fetch the integer mask for all presented flags.
   *
   * @param flags to mask (null or empty returns zero)
   * @return the integer mask for use in C
   */
  def mask(flags: collection.Seq[MaskedFlag#Flag]): Int = {
    if (flags == null || flags.isEmpty) return 0

    var result = 0
    for (flag <- flags; if flag != null) {
      result |= flag.getMask()
    }

    result
  }

  /**
   * Indicates whether the passed flag has the relevant masked flag high.
   *
   * @param flags to evaluate (usually produced by
   *              { @link #mask(org.lmdbjava.MaskedFlag...)}
   * @param test the flag being sought (required)
   * @return true if set.
   */
  def isSet(flags: Int, test: MaskedFlag#Flag): Boolean = {
    require(test != null, "test is null")
    (flags & test.getMask()) == test.getMask()
  }
}

/**
 * Indicates an enum that can provide integers for each of its values.
 */
sealed abstract class MaskedFlag extends Enumeration {

  protected final def Flag(value: Int): Flag = new Flag(value)
  class Flag(value: Int) extends Val(value, null) {

    /**
     * Obtains the integer value for this enum which can be included in a mask.
     *
     * @return the integer value for combination into a mask
     */
    def getMask(): Int = this.value

    def |(other: MaskedFlag#Flag): Int =  this.value | other.getMask()

    final def isInFlags(flags: Int): Boolean = (flags & this.value) == this.value
  }

  final def withMask(mask: Int): Flag = this.apply(mask).asInstanceOf[Flag]
}

/** Environment copy flags */
object CopyFlags extends MaskedFlag {
  type Flag = MaskedFlag#Flag

  /** Default copy configuration. */
  val MDBX_CP_DEFAULTS: Flag = Flag(0)

  /** Copy with compactification: Omit free space from copy and renumber all pages sequentially. */
  val MDBX_CP_COMPACT: Flag = Flag(1)

  /** Force to make resizeable copy, i.e.dynamic size instead of fixed */
  val MDBX_CP_FORCE_DYNAMIC_SIZE: Flag = Flag(2)
}

/**
 * Flags for use when opening a Dbi.
 */
object DbiFlags extends MaskedFlag {
  type Flag = MaskedFlag#Flag

  /**
   * Default DBI configuration.
   */
  val MDBX_DB_DEFAULTS: Flag = Flag(0)

  /**
   * Use reverse string comparison for keys.
   *
   * Keys are strings to be compared in reverse order, from the end of the
   * strings to the beginning. By default, keys are treated as strings and
   * compared from beginning to end.
   */
  val MDBX_REVERSEKEY: Flag = Flag(0x02)
  /**
   * Use sorted duplicates, i.e. allow multi-values for a key.
   *
   * Duplicate keys may be used in the database. Or, from another perspective,
   * keys may have multiple data items, stored in sorted order. By default keys
   * must be unique and may have only a single data item.
   */
  val MDBX_DUPSORT: Flag = Flag(0x04)
  /**
   * Numeric keys in native byte order: either unsigned int or size_t. The keys
   * must all be of the same size and must be aligned while passing as arguments.
   */
  val MDBX_INTEGERKEY: Flag = Flag(0x08)
  /**
   * With MDBX_DUPSORT, sorted dup items have fixed size.
   *
   * This flag may only be used in combination with #MDBX_DUPSORT. This
   * option tells the library that the data items for this database are all the
   * same size, which allows further optimizations in storage and retrieval.
   * When all data items are the same size, the SeekOp#MDBX_GET_MULTIPLE
   * and SeekOp#MDBX_NEXT_MULTIPLE cursor operations may be used to
   * retrieve multiple items at once.
   */
  val MDBX_DUPFIXED: Flag = Flag(0x10)
  /**
   * With MDBX_DUPSORT and with MDBX_DUPFIXED, dups are fixed size
   * like MDBX_INTEGERKEY -style integers.
   * The data values must all be of the same size and must be aligned while passing as arguments.
   *
   * This option specifies that duplicate data items are binary integers, similar to MDBX_INTEGERKEY keys.
   */
  val MDBX_INTEGERDUP: Flag = Flag(0x20)
  /**
   * With MDBX_DUPSORT, use reverse string comparison for data values.
   *
   * This option specifies that duplicate data items should be compared as
   * strings in reverse order.
   */
  val MDBX_REVERSEDUP: Flag = Flag(0x40)
  /**
   * Create the named database if it doesn't already exist.
   *
   * This option is not allowed in a read-only transaction or a read-only environment.
   */
  val MDBX_CREATE: Flag = Flag(0x40000)
  /**
   * Opens an existing sub-database created with unknown flags.
   *
   * The `MDBX_DB_ACCEDE` flag is intend to open a existing sub-database which
   * was created with unknown flags (MDBX_REVERSEKEY, \MDBX_DUPSORT,
   * MDBX_INTEGERKEY, MDBX_DUPFIXED, MDBX_INTEGERDUP and
   * MDBX_REVERSEDUP).
   *
   * In such cases, instead of returning the MDBX_INCOMPATIBLE error, the
   * sub-database will be opened with flags which it was created, and then an
   * application could determine the actual flags by  mdbx_dbi_flags(). */
  val MDBX_DB_ACCEDE: Flag = Flag(0x40000000)
}

/**
 * Flags for use when opening the Env.
 */
object EnvFlags extends MaskedFlag {
  type Flag = MaskedFlag#Flag

  /**
   * Default environment configuration (default robust and durable sync mode).
   */
  val MDBX_ENV_DEFAULTS: Flag = Flag(0)
  /**
   * No environment directory.
   */
  val MDBX_NOSUBDIR: Flag = Flag(0x4000)
  /**
   * Read only mode.
   */
  val MDBX_RDONLY: Flag = Flag(0x20000)
  /**
   * Open environment in exclusive/monopolistic mode (equivalent to MDB_NOLOCK in LMDB).
   */
  val MDBX_EXCLUSIVE: Flag = Flag(0x400000)
  /**
   * Using database/environment which already opened by another process(es).
   */
  val MDBX_ACCEDE: Flag = Flag(0x40000000)
  /**
   * Use writable mmap (map data into memory with write permission.).
   */
  val MDBX_WRITEMAP: Flag = Flag(0x80000)
  /**
   * Tie reader locktable slots to read-only transactions instead of to threads.
   */
  val MDBX_NOTLS: Flag = Flag(0x200000)
  /**
   * Aims to coalesce a Garbage Collection items.
   */
  val MDBX_COALESCE: Flag = Flag(0x2000000)
  /**
   * LIFO policy for recycling a Garbage Collection items.
   */
  val MDBX_LIFORECLAIM: Flag = Flag(0x4000000)
  /**
   * Debugging option, fill/perturb released pages.
   */
  val MDBX_PAGEPERTURB: Flag = Flag(0x8000000)
  /**
   * Don't do readahead (no effect on Windows).
   */
  val MDBX_NORDAHEAD: Flag = Flag(0x800000)
  /**
   * Don't initialize malloc'd memory before writing to datafile.
   */
  val MDBX_NOMEMINIT: Flag = Flag(0x1000000)
  /**
   *  Don't sync the meta-page after commit.
   */
  val MDBX_NOMETASYNC: Flag = Flag(0x40000)
  /**
   * Don't sync anything but keep previous steady commits.
   */
  val MDBX_SAFE_NOSYNC: Flag = Flag(0x10000)
  /**
   * Don't sync anything but keep previous steady commits.
   */
  val MDBX_UTTERLY_NOSYNC: Flag = Flag(MDBX_SAFE_NOSYNC.id | 0x100000)
}

/**
 * Flags for use when performing a "put" (write operation).
 */
object PutFlags extends MaskedFlag {
  type Flag = MaskedFlag#Flag

  /** Upsertion by default (without any other flags) */
  val MDBX_UPSERT: Flag = Flag(0)

  /**
   * For insertion: Don't write if the key already exists.
   */
  val MDBX_NOOVERWRITE: Flag = Flag(0x10)
  /**
   * Has effect only for MDB_DUPSORT databases.<br>
   * For upsertion: don't write if the key and data pair already exists.<br>
   * For deletion: remove all values for key.
   */
  val MDBX_NODUPDATA: Flag = Flag(0x20)
  /**
   * For upsertion: overwrite the current key/data pair.
   * MDBX allows this flag for \ref mdbx_put() for explicit overwrite/update without insertion.
   * For deletion: remove only single entry at the current cursor position.
   */
  val MDBX_CURRENT: Flag = Flag(0x40)
  /**
   * Has effect only for \ref MDBX_DUPSORT databases.
   * For deletion: remove all multi-values (aka duplicates) for given key.
   * For upsertion: replace all multi-values for given key with a new one. */
  val MDBX_ALLDUPS : Flag = Flag(0x80)
  /**
   * For upsertion: Just reserve space for data, don't copy it.
   * Return a pointer to the reserved space.
   */
  val MDBX_RESERVE: Flag = Flag(0x10000)
  /**
   * Data is being appended. Don't split full pages, continue on a new instead.
   */
  val MDBX_APPEND: Flag = Flag(0x20000)
  /**
   * Has effect only for MDBX_DUPSORT databases.
   * Duplicate data is being appended.
   * Don't split full pages, continue on a new instead.
   */
  val MDBX_APPENDDUP: Flag = Flag(0x40000)
  /**
   * Only for MDBX_DUPFIXED.
   * Store multiple data items in one call.
   */
  val MDBX_MULTIPLE: Flag = Flag(0x80000)

}

/**
 * Flags for use when working with transactions.
 */
object TxnFlags extends MaskedFlag {
  type Flag = MaskedFlag#Flag

  /**
   * Start read-write transaction.
   * Only one write transaction may be active at a time. Writes are fully
    serialized, which guarantees that writers can never deadlock. */
  val MDBX_TXN_READWRITE = Flag(0)

  /** Start read-only transaction.

  There can be multiple read-only transactions simultaneously that do not block each other and a write transactions. */
  val MDBX_TXN_RDONLY = Flag(EnvFlags.MDBX_RDONLY.getMask())

  /** Prepare but not start read-only transaction.

    Transaction will not be started immediately, but created transaction handle
    will be ready for use with \ref mdbx_txn_renew(). This flag allows to
    preallocate memory and assign a reader slot, thus avoiding these operations
    at the next start of the transaction.  */
  val MDBX_TXN_RDONLY_PREPARE = Flag(EnvFlags.MDBX_RDONLY | EnvFlags.MDBX_NOMEMINIT)

  /** Do not block when starting a write transaction.  */
  val MDBX_TXN_TRY = Flag(0x10000000)

  /** Exactly the same as \ref MDBX_NOMETASYNC but for this transaction only  */
  val MDBX_TXN_NOMETASYNC = Flag(EnvFlags.MDBX_NOMETASYNC.getMask())

  /** Exactly the same as \ref MDBX_SAFE_NOSYNC but for this transaction only  */
  val MDBX_TXN_NOSYNC = Flag(EnvFlags.MDBX_SAFE_NOSYNC.getMask())
}