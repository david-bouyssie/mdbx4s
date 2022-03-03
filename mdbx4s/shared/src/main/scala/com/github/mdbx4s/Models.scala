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

import java.util.Comparator

case class BuildInfo private[mdbx4s](
  datetime: String,
  target: String,
  options: String,
  compiler: String,
  flags: String
) /*{

  override def toString: String = "{" + "datetime=" + datetime + ", target=" + target + ", options=" + options + ", compiler=" + compiler + ", flags=" + flags + "}"
}*/

case class CommitLatency private[mdbx4s](
  preparation: Int, // Duration of preparation (commit child transactions, update sub-databases records and cursors destroying).
  gc: Int, // Duration of GC/freeDB handling & updation.
  audit: Int, // Duration of internal audit if enabled.
  write: Int, // Duration of writing dirty/modified data pages.
  sync: Int, // Duration of syncing written data to the dist/storage.
  ending: Int, // Duration of transaction ending (releasing resources).
  whole: Int // The total duration of a commit
)

case class DatabaseConfig(
  /** @see JNI#MDBX_REVERSEKEY */
  reverseKey: Boolean = false,
  /** @see JNI#MDBX_REVERSEDUP */
  reverseDup: Boolean = false,
  /** @see JNI#MDBX_DUPSORT */
  dupSort: Boolean = false,
  /** @see JNI#MDBX_DUPFIXED */
  dupFixed: Boolean = false,
  /** @see JNI#MDBX_INTEGERKEY */
  integerKey: Boolean = false,
  /** @see JNI#MDBX_INTEGERDUP */
  integerDup: Boolean = false,
  /** @see JNI#MDBX_CREATE */
  create: Boolean = false,
  /** @see JNI#MDBX_ACCEDE */
  accede: Boolean = false,
  var keyComparator: Option[Comparator[Array[Byte]]]= None,
  var dataComparator: Option[Comparator[Array[Byte]]] = None
) {

  def this(flags: Int) {
    this(
      reverseKey = DbiFlags.MDBX_REVERSEKEY.isInFlags(flags),
      reverseDup = DbiFlags.MDBX_REVERSEDUP.isInFlags(flags),
      dupSort = DbiFlags.MDBX_DUPSORT.isInFlags(flags),
      dupFixed = DbiFlags.MDBX_DUPFIXED.isInFlags(flags),
      integerKey = DbiFlags.MDBX_INTEGERKEY.isInFlags(flags),
      integerDup = DbiFlags.MDBX_INTEGERDUP.isInFlags(flags),
      create = DbiFlags.MDBX_CREATE.isInFlags(flags),
      accede = DbiFlags.MDBX_DB_ACCEDE.isInFlags(flags)
    )
  }

}

/**
 *
 * @param mode The UNIX permissions to set on created files. This parameter is ignored on Windows.
 * @param flagsMask Special options for this environment. This parameter must be set
 * to 0 or by bitwise OR'ing together one or more of the values described here.
 * Flags set by mdb_env_set_flags() are also used.
 * */
case class EnvConfig(
  var maxReaders: Int = 126,
  var maxDbs: Int = 1,
  var mapSize: Long = 1024 * 1024,
  var mode: Int = Integer.parseInt("0644", 8), //this is octal
  var flagsMask: Int = 0
) {
  require(maxReaders > 0, "maxReaders must be greater than zero")
  require(maxDbs > 0, "maxReaders must be greater than zero")
  require(mapSize > 0, "maxReaders must be greater than zero")

  def setFlags(flags: EnvFlags.Flag*): Unit = {
    this.flagsMask = MaskedFlag.mask(flags)
  }

}

object EnvInfo {
  case class Geo private[mdbx4s](
    lower: Long, /* lower limit for datafile size */
    upper: Long, /* upper limit for datafile size */
    current: Long /* current datafile size */ ,
    shrink: Long, /* shrink treshold for datafile */
    grow: Long /* growth step for datafile */
  ) {
    //override def toString: String = "{" + "lower=" + lower + ", upper=" + upper + ", current=" + current + ", shrink=" + shrink + ", grow=" + grow + '}'
  }

  case class Meta private[mdbx4s](
    meta0TxnId: Long,
    meta0Sign: Long,
    meta1TxnId: Long,
    meta1Sign: Long,
    meta2TxnId: Long,
    meta2Sign: Long
  )

  case class Point private[mdbx4s](x: Long, y: Long) {
    // override def toString: String = "{" + "x=" + x + ", y=" + y + '}'
  }

  case class BootId private[mdbx4s](
    current: Point,
    meta0: Point,
    meta1: Point,
    meta2: Point
  ) {
    //override def toString: String = "{" + "current=" + current + ", meta0=" + meta0 + ", meta1=" + meta1 + ", meta2=" + meta2 + '}'
  }

  case class PgopStat private[mdbx4s](
    newly: Long,
    cow: Long,
    cloned: Long,
    split: Long,
    merge: Long,
    spill: Long,
    unspill: Long,
    wops: Long
  ) {
    //override def toString: String = "{" + "newly=" + newly + ", cow=" + cow + ", clone=" + clone + ", split=" + split + ", merge=" + merge + ", spill=" + spill + ", unspill=" + unspill + ", wops=" + wops + "}"
  }
}

case class EnvInfo private[mdbx4s](
  geo: EnvInfo.Geo,
  mapSize: Long,
  lastPgNo: Long,
  recentTxnId: Long,
  latterReaderTxnId: Long,
  selfLatterReaderTxnId: Long,
  //meta: EnvInfo#Meta,
  maxReaders: Int,
  numReaders: Int,
  dxbPageSize: Int,
  sysPageSize: Int,
  //bootId: EnvInfo#BootId,
  unsyncVolume: Long,
  autosyncThreshold: Long,
  sinceSyncSeconds16dot16: Int,
  autosyncPeriodSeconds16dot16: Int,
  sinceReaderCheckSeconds16dot16: Int,
  mode: Int
  //pgopStat: EnvInfo#PgopStat
) {

  override def toString: String = "EnvInfo(" + "geo=" + geo + ", mapSize=" + mapSize + ", lastPgNo=" + lastPgNo +
    ", lastTxnId=" + recentTxnId + ", latterReaderTxnId=" + latterReaderTxnId + ", SelfLatterReaderTxnId=" + selfLatterReaderTxnId +
    //", meta0TxnId=" + meta0TxnId + ", meta0Sign=" + meta0Sign + ", meta1TxnId=" + meta1TxnId +
    //", meta1Sign=" + meta1Sign + ", meta2TxnId=" + meta2TxnId + ", meta2Sign=" + meta2Sign +
    ", maxReaders=" + maxReaders + ", numReaders=" + numReaders + ", dxbPageSize=" + dxbPageSize +
    ", sysPageSize=" + sysPageSize + ", bootId=" + null + ", unsyncVolume=" + unsyncVolume +
    ", autosyncThreshold=" + autosyncThreshold + ", sinceSyncSeconds16dot16=" + sinceSyncSeconds16dot16 +
    ", autosyncPeriodSeconds16dot16=" + autosyncPeriodSeconds16dot16 +
    ", sinceReaderCheckSeconds16dot16=" + sinceReaderCheckSeconds16dot16 +
    ", mode=" + mode + ", pgopStat=" + null + ")"

}

case class RamInfo private[mdbx4s](pageSize: Long, totalPages: Long, availPages: Long) /*{
  override def toString: String = "RamInfo [" + "pageSize=" + pageSize + ", totalPages=" + totalPages + ", availPages=" + availPages + "]"
}*/

/**
 * Statistics, as returned by Env#stat() and Dbi#stat(org.lmdbjava.Txn).
 */
case class Stat private[mdbx4s](
  /**
   * Size of a database page. This is currently the same for all databases.
   */
  pageSize: Int,

  /**
   * Depth (height) of the B-tree.
   */
  depth: Int,

  /**
   * Number of internal (non-leaf) pages.
   */
  branchPages: Long,

  /**
   * Number of leaf pages.
   */
  leafPages: Long,

  /**
   * Number of overflow pages.
   */
  overflowPages: Long,

  /**
   * Number of data items.
   */
  entries: Long
) {
  override def toString: String = {
    s"Stat(pageSize=$pageSize, depth=$depth, branchPages=$branchPages, leafPages=$leafPages, overflowPages=$overflowPages, pageSize=$pageSize, entries=$entries)"
  }

  //override def toString: String = "Stat{" + "psize=" + pageSize + ", depth=" + depth + ", branchPages=" + branchPages + ", leafPages=" + leafPages + ", overflowPages=" + overflowPages + ", entries=" + entries + '}'
}

case class TxnInfo private[mdbx4s](
  id: Long,
  readerLag: Long,
  spaceUsed: Long,
  spaceLimitSoft: Long,
  spaceLimitHard: Long,
  spaceRetired: Long,
  spaceLeftover: Long,
  spaceDirty: Long
) {
  //override def toString: String = "TxnInfo [" + "id=" + id + ", readerLag=" + readerLag + ", spaceUsed=" + spaceUsed + ", spaceLimitSoft=" + spaceLimitSoft + ", spaceLimitHard=" + spaceLimitHard + ", spaceRetired=" + spaceRetired + ", spaceLeftover=" + spaceLeftover + ", spaceDirty=" + spaceDirty + "]"
}

/**
 * Immutable return value from #version().
 */
case class Version private[mdbx4s](
  /** Major version number. */
  major: Int,
  /** Minor version number. */
  minor: Int,
  /** Release number of Major.Minor */
  release: Int,
  /** Revision number of Release */
  revision: Int
)