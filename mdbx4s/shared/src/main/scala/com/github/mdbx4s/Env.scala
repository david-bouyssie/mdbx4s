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

import java.io.File

import com.github.mdbx4s.bindings._

/**
 * An environment handle.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @author <a href="http://castortech.com">Alain Picard</a>
 */
object Env {

  private[mdbx4s] val LIB: ILibraryWrapper = LibraryWrapper.getWrapper()
  //def version: String = "" + JNI.MDBX_VERSION_MAJOR + JNI.MDBX_VERSION_MINOR //$NON-NLS-1$

  /*def pushMemoryPool(size: Int): Unit = {
    NativeBuffer.pushMemoryPool(size)
  }

  def popMemoryPool(): Unit = {
    NativeBuffer.popMemoryPool()
  }*/
}

//private(
//  private val envPtr: P,
//  private val readOnly: Boolean
//)
class Env private(private[mdbx4s] var envPtr: Env.LIB.EnvHandle) extends AutoCloseable {

  /*private var keyCmpCallback = null
  private var dataCmpCallback = null
  private var keyComparator = null
  private var dataComparator = null*/

  /**
   * Create an environment handle and open it at the same time with default
   * values.
   *
   * @param path
   * directory in which the database files reside. This directory must
   * already exist and be writable.
   */
  def this(path: File) {
    this(Env.LIB.mdbx_env_create())
    open(path)
  }

  def this() {
    this(Env.LIB.mdbx_env_create())
  }

  def isClosed(): Boolean = { envPtr == null }

  @inline
  private def _checkEnvNotClosed(): Unit = {
    if (envPtr == null) throw new MdbxException("the environment is closed")
  }
  @inline
  private def _checkTxnThenEnv(txn: Transaction): Unit = {
    require(txn != null, "txn is null")
    require(!txn.isClosed(), "transaction is closed")
    _checkEnvNotClosed()
  }

  override def close(): Unit = {
    if (envPtr != null) {
      Env.LIB.mdbx_env_close(envPtr)
      envPtr = null
    }
    /*if (keyCmpCallback != null) {
      keyCmpCallback.dispose
      keyCmpCallback = null
    }
    if (dataCmpCallback != null) {
      dataCmpCallback.dispose
      dataCmpCallback = null
    }*/
  }

  def open(path: File): Unit = {
    open(path, EnvConfig())
  }

  def open(path: File, flags: EnvFlags.Flag*): Unit = {
    val config = EnvConfig()
    config.setFlags(flags: _*)
    open(path, config)
  }

  /**
   * <p>
   * Open the environment.
   * </p>
   *
   * If this function fails, #mdb_env_close() must be called to discard the #MDB_env handle.
   *
   * @param path The directory in which the database files reside. This directory must already exist and be writable.
   * @param config The configuration of this environment.
   */
  def open(path: File, config: EnvConfig): Unit = {
    require(path != null, "path is null")
    require(config != null, "config is null")

    try {
      Env.LIB.mdbx_env_set_maxdbs(envPtr, config.maxDbs)
      Env.LIB.mdbx_env_set_mapsize(envPtr, config.mapSize)
      Env.LIB.mdbx_env_set_maxreaders(envPtr, config.maxReaders)

      Env.LIB.mdbx_env_open(envPtr, path, config.flagsMask, config.mode)
    } catch {
      case t: Throwable =>
        this.close()
        throw t
    }
  }

  /**
   * <p>
   * Copy an MDBX environment to the specified path.
   * </p>
   * This function may be used to make a backup of an existing environment. No
   * lockfile is created, since it gets recreated at need. This call can trigger
   * significant file size growth if run in parallel with write transactions,
   * because it employs a read-only transaction.
   *
   * @param path
   * The directory in which the copy will reside. This directory must
   * already exist and be writable but must otherwise be empty.
   */
  def copy(path: File): Unit = {
    copy(path, CopyFlags.MDBX_CP_DEFAULTS)
  }

  def copy(path: File, flags: CopyFlags.Flag*): Unit = {
    require(path != null, "path is null")
    _checkEnvNotClosed()

    Env.LIB.mdbx_env_copy(envPtr, path, MaskedFlag.mask(flags))
  }

  /**
   * <p>
   * Flush the data buffers to disk.
   * </p>
   * Data is always written to disk when mdb_txn_commit() is called, but the
   * operating system may keep it buffered. MDBX always flushes the OS buffers
   * upon commit as well, unless the environment was opened with
   * MDBX_SAFE_NOSYNC, MDBX_UTTERLY_NOSYNC or in part MDBX_NOMETASYNC.
   *
   */
  def sync(): Unit = {
    _checkEnvNotClosed()
    Env.LIB.mdbx_env_sync_ex(envPtr, true, false)
  }

  /**
   * Kept for backward compatibility with older versions
   *
   */
  def sync(force: Boolean): Unit = {
    _checkEnvNotClosed()
    Env.LIB.mdbx_env_sync_ex(envPtr, force, false)
  }

  /**
   * Flush the data buffers to disk.
   *
   * @param force  force a synchronous flush. Otherwise then will run in polling mode,
   * i.e. it will check the thresholds that were set with mdbx_env_set_syncbytes()
   * and/or mdbx_env_set_syncperiod() and perform flush if at least one of the thresholds is reached.
   * flushes will be omitted, and with
   * If env is open with MDBX_SAFE_NOSYNC or MDBX_UTTERLY_NOSYNC, flush will be asynchronous.
   *
   * @param nonblock Don't wait if write transaction is running by other thread.
   */
  def sync(force: Boolean, nonblock: Boolean): Unit = {
    _checkEnvNotClosed()
    Env.LIB.mdbx_env_sync_ex(envPtr, force, nonblock)
  }

  /** The shortcut for mdbx_env_sync_ex() with the `force=false` and `nonblock=true` arguments. */
  def syncPoll(): Unit = {
    _checkEnvNotClosed()
    Env.LIB.mdbx_env_sync_ex(envPtr, false, true)
  }

  /**
   * <p>
   * Set the size of the memory map to use for this environment.
   * </p>
   *
   * The size should be a multiple of the OS page size. The default is 10485760
   * bytes. The size of the memory map is also the maximum size of the database.
   * The value should be chosen as large as possible, to accommodate future growth
   * of the database. This function should be called after mdb_env_create() and
   * before mdb_env_open(). It may be called at later times if no transactions
   * are active in this process. Note that the library does not check for this
   * condition, the caller must ensure it explicitly.
   *
   * The new size takes effect immediately for the current process but will not be
   * persisted to any others until a write transaction has been committed by the
   * current process. Also, only mapsize increases are persisted into the environment.
   *
   * If the mapsize is increased by another process, MDBX silently and
   * transparently adopt these changes at next transaction start. However,
   * mdbx_txn_begin() will return MDBX_UNABLE_EXTEND_MAPSIZE if new
   * mapping size could not be applied for current process (for instance if address space is busy).
   * Therefore, in the case of MDBX_UNABLE_EXTEND_MAPSIZE error
   * you need close and reopen the environment to resolve error.
   *
   * Actual values may be different than your have specified because of
   * rounding to specified database page size, the system page size and/or the
   * size of the system virtual memory management unit. You can get actual values
   * by calling mdbx_env_sync_ex() or by using the tool `mdbx_chk` with the `-v` option.
   *
   * Legacy mdbx_env_set_mapsize() correspond to calling mdbx_env_set_geometry()
   * with the arguments `size_lower`, `size_now`, `size_upper` equal to the `size` and `-1` (i.e. default)
   * for all other parameters.
   *
   * Any attempt to set a size smaller than the space already consumed by the
   * environment will be silently changed to the current size of the used space.
   *
   * @param size The size in bytes
   */
  /*def setMapSize(size: Long): Unit = {
    _checkEnvNotClosed()
    Env.LIB.mdbx_env_set_mapsize(envPtr, size)
  }*/

  // TODO: implement me?
  /*def setGeometry(lower: Long, now: Long, upper: Long, growthStep: Long, shrinkThreshold: Long, pageSize: Long): Unit = {
    checkErrorCode(this, mdbx_env_set_geometry(pointer, lower, now, upper, growthStep, shrinkThreshold, pageSize))
  }*/

  def getMaxDbs(): Int = {
    _checkEnvNotClosed()
    Env.LIB.mdbx_env_get_maxdbs(envPtr)
  }

  /**
   * <p>
   * Set the maximum number of named databases for the environment.
   * </p>
   *
   *
   * This function is only needed if multiple databases will be used in the
   * environment. Simpler applications that use the environment as a single
   * unnamed database can ignore this option. This function may only be called
   * after #mdb_env_create() and before #mdb_env_open().
   *
   * @param size The maximum number of databases
   */
  /*def setMaxDbs(maxDbs: Int): Unit = {
    _checkEnvNotClosed()
    Env.LIB.mdbx_env_set_maxdbs(envPtr, maxDbs)
  }*/

  def getMaxReaders(): Long = {
    _checkEnvNotClosed()
    Env.LIB.mdbx_env_get_maxreaders(envPtr)
  }

  /**
   * <p>
   * Set the maximum number of threads/reader slots for the environment.
   * </p>
   *
   * This defines the number of slots in the lock table that is used to track
   * readers in the the environment. The default is 126. Starting a read-only
   * transaction normally ties a lock table slot to the current thread until the
   * environment closes or the thread exits. If MDBX_NOTLS is in use, mdb_txn_begin()
   * instead ties the slot to the MDB_txn object until it or the MDB_env object
   * is destroyed. This function may only be called after mdb_env_create() and
   * before mdb_env_open().
   *
   * @param size
   * The maximum number of reader lock table slots
   */
  /*def setMaxReaders(maxReaders: Int): Unit = {
    _checkEnvNotClosed()
    Env.LIB.mdbx_env_set_maxreaders(envPtr, maxReaders)
  }*/

  def getMaxKeySize(): Long = {
    _checkEnvNotClosed()
    Env.LIB.mdbx_env_get_maxkeysize_ex(envPtr, getFlags())
  }
  /*def getMaxKeySize(): Long = mdbx_env_get_maxkeysize(pointer)*/

  def getFlags(): Int = {
    _checkEnvNotClosed()
    Env.LIB.mdbx_env_get_flags(envPtr)
  }

  /*
  def addFlags(flags: Int): Unit = {
    checkErrorCode(this, mdbx_env_set_flags(pointer, flags, 1))
  }

  def removeFlags(flags: Int): Unit = {
    checkErrorCode(this, mdbx_env_set_flags(pointer, flags, 0))
  }

  /**
   * @return pointer to user context
   */
  def getUserContext: Long = mdbx_env_get_userctx(pointer)

  /**
   * Sets the user context to the supplied context native object
   *
   * @param ctx
   */
  def setUserContext(ctx: NativeObject): Unit = {
    if (ctx != null) mdbx_env_set_userctx(pointer, ctx.pointer)
  }

  /**
   * @return Information about the MDBX environment.
   * @deprecated Use {@link Env# info ( Transaction )} instead.
   */
  @deprecated def info: EnvInfo = {
    val rc = new JNI.MDBX_envinfo
    mdbx_env_info(pointer, rc, JNI.SIZEOF_ENVINFO)
    new EnvInfo(rc)
  }*/

  /**
   * Version of the method that runs within a transaction.
   * Replaces previous version from {@link Env}
   *
   * @return Information about the MDBX environment.
   */
  def info(txn: Transaction): EnvInfo = {
    _checkTxnThenEnv(txn)

    Env.LIB.mdbx_env_info_ex(envPtr, txn.txnPtr)
  }

  /*
  /**
   * @return Statistics about the MDBX environment.
   * @deprecated Use {@link Env# stat ( Transaction )} instead.
   */
  @deprecated def stat: Stat = {
    val rc = new JNI.MDBX_stat
    mdbx_env_stat(pointer, rc, JNI.SIZEOF_STAT)
    new Stat(rc)
  }*/

  /**
   * Version of the method that runs within a transaction.
   * Replaces previous version from {@link Env}
   *
   * @return Statistics about the MDBX environment.
   */
  def stat(txn: Transaction): Stat = {
    _checkTxnThenEnv(txn)

    Env.LIB.mdbx_env_stat_ex(envPtr, txn.txnPtr)
  }

/*

  /**
   * @return Percent full for the whole Db environment.
   * @deprecated Use version with {@link Transaction} argument instead. This one can at times return erratic
   *             results.
   */
  @deprecated def percentageFull: Float = {
    val stat2 = stat
    val info2 = info
    if (stat2.ms_psize == 0) return 0.0f
    val nbrPages = info2.getMapSize / stat2.ms_psize
    (info2.getLastPgNo / nbrPages.toFloat) * 100
  }

  def percentageFull(txn: Transaction): Float = {
    val stat2 = stat(txn)
    val info2 = info(txn)
    if (stat2.ms_psize == 0) return 0.0f
    val nbrPages = info2.getMapSize / stat2.ms_psize
    (info2.getLastPgNo / nbrPages.toFloat) * 100
  }*/

  /**
   * <p>
   * Create a transaction for use with the environment.
   * </p>
   * <p/>
   * The transaction handle may be discarded using #mdb_txn_abort() or
   * #mdb_txn_commit().
   *
   * A transaction and its cursors must only be used by a single thread, and a
   * thread may only have a single transaction at a time. If MDB_NOTLS is in use,
   * this does not apply to read-only transactions. Cursors may not span
   * transactions.
   *
   * @param parent
   * If this parameter is non-NULL, the new transaction will be a
   * nested transaction, with the transaction indicated by \b parent as
   * its parent. Transactions may be nested to any level. A parent
   * transaction and its cursors may not issue any other operations
   * than mdb_txn_commit and mdb_txn_abort while it has active child
   * transactions.
   *
   * @param readOnly This transaction will not perform any write operations.
   *
   * @return transaction handle
   * @note A transaction and its cursors must only be used by a single thread, and
   *       a thread may only have a single transaction at a time.
   *       If NOTLS is in use, this does not apply to read-only transactions.
   * @note Cursors may not span transactions.
   */
  def createChildTransaction(parentTxnPtr: Env.LIB.TxnHandle, readOnly: Boolean = false): Transaction = {
    require(parentTxnPtr != null, "parentTxnPtr is null")
    _createTransaction(parentTxnPtr, readOnly)
  }

  def createTransaction(readOnly: Boolean = false): Transaction = _createTransaction(null, readOnly)

  private[mdbx4s] def _createTransaction(parentTxnPtr: Env.LIB.TxnHandle, readOnly: Boolean): Transaction = {
    _checkEnvNotClosed()
    val txnPtr = Env.LIB.mdbx_txn_begin(envPtr, parentTxnPtr, if (readOnly) EnvFlags.MDBX_RDONLY.getMask() else 0)
    new Transaction(this, readOnly, txnPtr)
  }

  /*def createChildTransaction(parent: Transaction, readOnly: Boolean, ctx: NativeObject): Transaction = {
    val txpointer = new Array[Long](1)
    checkErrorCode(this, mdbx_txn_begin_ex(pointer, if (parent == null) 0
    else parent.pointer, if (readOnly) MDBX_RDONLY
    else 0, txpointer, ctx.pointer
    )
    )
    new Transaction(this, txpointer(0))
  }*/

  /**
   * <p>
   * Open a database in the environment.
   * </p>
   *
   * A database handle denotes the name and parameters of a database,
   * independently of whether such a database exists. The database handle may be
   * discarded by calling #mdb_dbi_close(). The old database handle is returned if
   * the database was already open. The handle may only be closed once. The
   * database handle will be private to the current transaction until the
   * transaction is successfully committed. If the transaction is aborted the
   * handle will be closed automatically. After a successful commit the handle
   * will reside in the shared environment, and may be used by other transactions.
   * This function must not be called from multiple concurrent transactions. A
   * transaction that uses this function must finish (either commit or abort)
   * before any other transaction may use this function.
   *
   * To use named databases (with name != NULL), #mdb_env_set_maxdbs() must be
   * called before opening the environment. Database names are kept as keys in the
   * unnamed database.
   *
   * @param tx
   * A transaction handle.
   * @param name
   * The name of the database to open. If only a single database is
   * needed in the environment, this value may be NULL.
   * @param flags
   * Special options for this database. This parameter must be set to 0
   * or by bitwise OR'ing together one or more of the values described
   * here.
   * <ul>
   * <li>REVERSEKEY Keys are
   * strings to be compared in reverse order, from the end of the
   * strings to the beginning. By default, Keys are treated as strings
   * and compared from beginning to end.
   * <li>DUPSORT Duplicate
   * keys may be used in the database. (Or, from another perspective,
   * keys may have multiple data items, stored in sorted order.) By
   * default keys must be unique and may have only a single data item.
   * <li>INTEGERKEY Keys are
   * binary integers in native byte order. Setting this option requires
   * all keys to be the same size, typically sizeof(int) or
   * sizeof(size_t).
   * <li>DUPFIXED This flag
   * may only be used in combination with DUPSORT. This option
   * tells the library that the data items for this database are all
   * the same size, which allows further optimizations in storage and
   * retrieval. When all data items are the same size, the
   * #MDB_GET_MULTIPLE and #MDB_NEXT_MULTIPLE cursor operations may be
   * used to retrieve multiple items at once.
   * <li>DUPSORT This
   * option specifies that duplicate data items are also integers, and
   * should be sorted as such.
   * <li>REVERSEDUP This
   * option specifies that duplicate data items should be compared as
   * strings in reverse order.
   * <li>CREATE Create the
   * named database if it doesn't exist. This option is not allowed in
   * a read-only transaction or a read-only environment.
   *
   * @return A database handle.
   */
  private[mdbx4s] def _openDatabase(txnPtr: Env.LIB.TxnHandle, name: String, flags: DbiFlags.Flag*): Database = {
    if (txnPtr == null) return openDatabase(name, flags: _*)

    _checkEnvNotClosed()

    // checkArgNotNull(name, "name");
    val flagsMask = MaskedFlag.mask(flags)
    val dbiPtr = Env.LIB.mdbx_dbi_open(txnPtr, name, flagsMask)

    new Database(this, name, dbiPtr)
  }

  def openDatabase(txn: Transaction, name: String, flags: DbiFlags.Flag*): Database = {
    require(txn != null, "txn is null")

    _openDatabase(txn.txnPtr, name, flags: _*)
  }

  /*def openDatabase(tx: Transaction, name: String, config: DatabaseConfig): Database = {
    checkArgNotNull(config, "config")
    if (tx == null) return openDatabase(name, config)
    checkArgNotNull(tx, "tx")
    val flags = setFlags(config)
    if (config.getKeyComparator != null || config.getDataComparator != null) return openDatabase(tx, name, flags, config.getKeyComparator, config.getDataComparator)
    openDatabase(tx, name, flags)
  }*/

  def openDatabase(): Database = openDatabase(null, DbiFlags.MDBX_CREATE)

  //def openDatabase(keyComp: Comparator[Array[Byte]], dataComp: Comparator[Array[Byte]]): Database = openDatabase(null, Constants.CREATE, keyComp, dataComp)

  def openDatabase(name: String): Database = openDatabase(name, DbiFlags.MDBX_CREATE)

  //def openDatabase(name: String, keyComp: Comparator[Array[Byte]], dataComp: Comparator[Array[Byte]]): Database = openDatabase(name, Constants.CREATE, keyComp, dataComp)

  def openDatabase(name: String, flags: DbiFlags.Flag*): Database = {
    val tx = createTransaction()
    try _openDatabase(tx.txnPtr, name, flags: _*)
    finally tx.commit()
  }

  /*def openDatabase(name: String, flags: collection.Seq[DbiFlags.Flag], keyComp: Comparator[Array[Byte]], dataComp: Comparator[Array[Byte]]): Database = {
    val tx = createTransaction
    try openDatabase(tx, name, flags, keyComp, dataComp)
    finally tx.close()
  }*/

  /*def openDatabase(name: String, config: DatabaseConfig): Database = {
    val tx = createTransaction
    try openDatabase(tx, name, config)
    finally tx.close()
  }*/

  /*def openDatabase(tx: Transaction, name: String, flags: collection.Seq[DbiFlags.Flag], keyComp: Comparator[Array[Byte]], dataComp: Comparator[Array[Byte]]): Database = {
    if (tx == null) return openDatabase(name, flags, keyComp, dataComp)
    var keyCmpAddr = 0L
    var dataCmpAddr = 0L
    checkArgNotNull(tx, "tx")
    val dbi = new Array[Long](1)
    if (keyComp != null) {
      keyCmpCallback = new Nothing(this, "compareKey", 2)
      keyCmpAddr = keyCmpCallback.getAddress
      keyComparator = keyComp
    }
    if (dataComp != null) {
      dataCmpCallback = new Nothing(dataComp.getClass, "compareData", 2)
      dataCmpAddr = dataCmpCallback.getAddress
      dataComparator = dataComp
    }
    checkErrorCode(this, mdbx_dbi_open_ex(tx.pointer, name, flags, dbi, keyCmpAddr, dataCmpAddr))
    new Database(this, dbi(0), name)
  }*/

  /*def compareKey(o1: Long, o2: Long): Long = {
    val v1 = new Value
    map_val(o1, v1)
    val v2 = new Value
    map_val(o2, v2)
    val key1 = v1.toByteArray
    val key2 = v2.toByteArray
    keyComparator.compare(key1, key2)
  }

  def compareData(o1: Long, o2: Long): Long = {
    val v1 = new Value
    map_val(o1, v1)
    val v2 = new Value
    map_val(o2, v2)
    val key1 = v1.toByteArray
    val key2 = v2.toByteArray
    dataComparator.compare(key1, key2)
  }

  def openSecondaryDatabase(primary: Database, name: String): SecondaryDatabase = openSecondaryDatabase(primary, name, Constants.CREATE)

  def openSecondaryDatabase(primary: Database, name: String, flags: DbiFlags.Flag*): SecondaryDatabase = {
    val tx = createTransaction
    try openSecondaryDatabase(tx, primary, name, flags)
    finally tx.close()
  }

  def openSecondaryDatabase(tx: Transaction, primary: Database, name: String, flags: DbiFlags.Flag*): SecondaryDatabase = {
    if (tx == null) return openSecondaryDatabase(primary, name, flags)
    checkArgNotNull(tx, "tx")
    checkArgNotNull(primary, "primary")
    val dbi = new Array[Long](1)
    checkErrorCode(this, mdbx_dbi_open(tx.pointer, name, flags, dbi))
    val config = new SecondaryDbConfig
    val secDb = new SecondaryDatabase(this, primary, dbi(0), name, config)
    if (associateDbs(tx, primary, secDb)) secDb
    else throw new MDBXException("Error associating databases")
  }

  def openSecondaryDatabase(primary: Database, name: String, config: SecondaryDbConfig): SecondaryDatabase = {
    val tx = createTransaction
    try openSecondaryDatabase(tx, primary, name, config)
    finally tx.close()
  }

  def openSecondaryDatabase(tx: Transaction, primary: Database, name: String, config: SecondaryDbConfig): SecondaryDatabase = {
    if (tx == null) return openSecondaryDatabase(primary, name, config)
    checkArgNotNull(tx, "tx")
    checkArgNotNull(primary, "primary")
    checkArgNotNull(config, "config")
    val flags = setFlags(config)
    val dbi = new Array[Long](1)
    checkErrorCode(this, mdbx_dbi_open(tx.pointer, name, flags, dbi))
    val secDb = new SecondaryDatabase(this, primary, dbi(0), name, config)
    if (associateDbs(tx, primary, secDb)) secDb
    else throw new MDBXException("Error associating databases")
  }

  private def associateDbs(tx: Transaction, primary: Database, secondary: SecondaryDatabase) = {
    var succeeded = false
    try {
      primary.associate(tx, secondary)
      succeeded = true
    } finally if (!succeeded) try primary.close()
    catch {
      case t: Throwable =>

      // Ignore it -- there is already an exception in flight.
    }
    succeeded
  }

  private def setFlags(config: DatabaseConfig) = {
    var flags = 0
    if (config.isReverseKey) flags |= Constants.REVERSEKEY
    if (config.isDupSort) flags |= Constants.DUPSORT
    if (config.isIntegerKey) flags |= Constants.INTEGERKEY
    if (config.isDupFixed) flags |= Constants.DUPFIXED
    if (config.isIntegerDup) flags |= Constants.INTEGERDUP
    if (config.isReverseDup) flags |= Constants.REVERSEDUP
    if (config.isCreate) flags |= Constants.CREATE
    if (config.isAccede) flags |= Constants.DBACCEDE
    flags
  }*/

  /**
   * Obtain the DBI names.
   *
   * <p>
   * This method is only compatible with Envs that use named databases.
   * If an unnamed Dbi is being used to store data, this method will
   * attempt to return all such keys from the unnamed database.
   *
   * @return a list of DBI names (never null)
   */
  private def _getDatabaseNames(): collection.Seq[Array[Byte]] = {

    val anonymousDbi = openDatabase()

    var txn: Transaction = null
    var cursor: Cursor = null
    try {
      txn = createTransaction(readOnly = true)
      cursor = anonymousDbi.openCursor(txn)

      var entryOpt = cursor.first()
      if (entryOpt.isEmpty) return Seq.empty[Array[Byte]]

      val result = new collection.mutable.ArrayBuffer[Array[Byte]]

      do {
        result += entryOpt.get._1
        entryOpt = cursor.next()
      } while (entryOpt.nonEmpty)

      result

    } finally {
      if (txn != null) txn.close()
      if (cursor != null) cursor.close()
    }
  }

  // TODO: set default to StandardCharsets.UTF_8???
  def getDatabaseNames(charset: java.nio.charset.Charset = java.nio.charset.Charset.defaultCharset()): collection.Seq[String] = {
    _getDatabaseNames().map { bytes =>
      new String(bytes, charset)
    }
  }
}
