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
 * A transaction handle.
 *
 * See: https://erthink.github.io/libmdbx/usage.html#autotoc_md48
 */
class Transaction private[mdbx4s](
  val env: Env,
  val isReadOnly: Boolean,
  private[mdbx4s] var txnPtr: Env.LIB.TxnHandle
) extends AutoCloseable {

  def isClosed(): Boolean = { txnPtr == null }

  override def close(): Unit = {
    abort()
  }

  /**
   * <p>
   * Return the transaction's ID.
   * </p>
   *
   * This returns the identifier associated with this transaction. For a read-only transaction, this
   * corresponds to the snapshot being read; concurrent readers will frequently have the same transaction ID.
   *
   * @return A transaction ID, valid if input is an active transaction.
   */
  def getId(): Long = {
    require(txnPtr != null, "transaction is closed")
    Env.LIB.mdbx_txn_id(txnPtr)
  }

  /**
   * <p>
   * Renew a read-only transaction.
   * </p>
   *
   * This acquires a new reader lock for a transaction handle that had been released by #mdb_txn_reset(). It
   * must be called before a reset transaction may be used again.
   */
  def renew(): Unit = {
    require(txnPtr != null, "transaction is closed")
    Env.LIB.mdbx_txn_renew(txnPtr)
  }

  def createTransactedDatabase(database: Database): TransactedDatabase = {
    new TransactedDatabase(env, this, database)
  }

  /**
   * <p>
   * Commit all the operations of a transaction into the database.
   * </p>
   *
   * The transaction handle is freed. It and its cursors must not be used again after this call, except with
   * #mdb_cursor_renew().
   *
   * @note Earlier documentation incorrectly said all cursors would be freed. Only write-transactions free
   *       cursors.
   *
   */
  def commit(): Unit = {
    require(txnPtr != null, "transaction is closed")
    Env.LIB.mdbx_txn_commit(txnPtr)
    txnPtr = null
  }

  def commitWithLatency(): CommitLatency = {
    require(txnPtr != null, "transaction is closed")

    val latency = Env.LIB.mdbx_txn_commit_ex(txnPtr)
    txnPtr = null

    latency
  }

  /**
   * <p>
   * Reset a read-only transaction.
   * </p>
   *
   * Abort the transaction like #mdb_txn_abort(), but keep the transaction handle. #mdb_txn_renew() may reuse
   * the handle. This saves allocation overhead if the process will start a new read-only transaction soon,
   * and also locking overhead if NOTLS is in use. The reader table
   * lock is released, but the table slot stays tied to its thread or #MDB_txn. Use mdb_txn_abort() to discard
   * a reset handle, and to free its lock table slot if NOTLS is in
   * use. Cursors opened within the transaction must not be used again after this call, except with
   * #mdb_cursor_renew(). Reader locks generally don't interfere with writers, but they keep old versions of
   * database pages allocated. Thus they prevent the old pages from being reused when writers commit new data,
   * and so under heavy load the database size may grow much more rapidly than otherwise.
   */
  def reset(): Unit = {
    require(txnPtr != null, "transaction is closed")
    Env.LIB.mdbx_txn_reset(txnPtr)
  }

  /**
   * <p>
   * Abandon all the operations of the transaction instead of saving them.
   * </p>
   *
   * The transaction handle is freed. It and its cursors must not be used again after this call, except with
   * #mdb_cursor_renew().
   *
   * @note Earlier documentation incorrectly said all cursors would be freed. Only write-transactions free
   *       cursors.
   */
  def abort(): Unit = {
    if (txnPtr != null) {
      Env.LIB.mdbx_txn_abort(txnPtr)
      txnPtr = null
    }
  }

  // TODO
  /*def broken(): Unit = {
    Env.LIB.mdbx_txn_break(txnPtr)
  }*/

  def info(scanRlt: Boolean): TxnInfo = {
    require(txnPtr != null, "transaction is closed")

    Env.LIB.mdbx_txn_info(txnPtr, scanRlt)
  }

  /*def getFlags(): Int = {
    require(txnPtr != null, "transaction is closed")
    Env.LIB.mdbx_txn_flags(txnPtr)
  }*/

  /*
  /**
   * @return pointer to user context
   */
  def getUserContext: Long = mdbx_txn_get_userctx(pointer)

  /**
   * Sets the user context to the supplied context native object
   *
   * @param ctx
   */
  def setUserContext(ctx: NativeObject): Unit = {
    if (ctx != null) mdbx_txn_set_userctx(pointer, ctx.pointer)
  }*/


}
