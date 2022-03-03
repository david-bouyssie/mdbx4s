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

import scala.scalanative.unsafe._
import scala.scalanative.unsigned._

@link("mdbx")
@extern
object mdbx {

  /* --- Some C types --- */
  type KVType = CStruct2[Ptr[Byte],CLong]
  //sealed trait IKeyValPointer
  type KVPtr = Ptr[KVType] //with IKeyValPointer

  // typedef int(MDBX_cmp_func)(const MDBX_val *a, const MDBX_val *b) MDBX_CXX17_NOEXCEPT
  type cfuncptr_mdbx_cmp = CFuncPtr2[Ptr[Byte], Ptr[Byte], CInt]

  type struct_mdbx_build_info = CStruct5[CString,CString,CString,CString,CString]
  type struct_mdbx_git_info = CStruct4[CString,CString,CString,CString]
  type struct_mdbx_version_info = CStruct6[CUnsignedChar,CUnsignedChar,CUnsignedShort,CUnsignedInt,struct_mdbx_git_info,CString]

  type struct_mdbx_stat = CStruct7[UInt,UInt,CSize,CSize,CSize,CSize,CSize]
  //type struct_lmdb_env_info = CStruct6[Ptr[Byte],CSize,CSize,CSize,UInt,UInt]

  type struct_mdbx_mi_geo = CStruct5[
    CSize, // lower /**< Lower limit for datafile size */
    CSize, // upper /**< Upper limit for datafile size */
    CSize, // current /**< Current datafile size */
    CSize, // shrink /**< Shrink threshold for datafile */
    CSize  // grow /**< Growth step for datafile */
  ]
  type struct_mdbx_env_meta = CStruct6[CSize, CSize, CSize, CSize, CSize, CSize]
  type struct_mdbx_mi_bootid = CStruct8[CSize, CSize, CSize, CSize, CSize, CSize, CSize, CSize]
  type struct_mdbx_mi_pgop_stat = CStruct8[CSize, CSize, CSize, CSize, CSize, CSize, CSize, CSize]

  type struct_mdbx_env_info = CStruct19[
    struct_mdbx_mi_geo,
    CSize,
    CSize,
    CSize,
    CSize,
    CSize,
    struct_mdbx_env_meta,
    CUnsignedInt,
    CUnsignedInt,
    CUnsignedInt,
    CUnsignedInt,
    struct_mdbx_mi_bootid,
    CSize,
    CSize,
    CUnsignedInt,
    CUnsignedInt,
    CUnsignedInt,
    CUnsignedInt,
    struct_mdbx_mi_pgop_stat
  ]

  type struct_mdbx_txn_info = CStruct8[CSize, CSize, CSize, CSize, CSize, CSize, CSize, CSize]
  type struct_mdbx_commit_latency = CStruct7[CUnsignedInt, CUnsignedInt, CUnsignedInt, CUnsignedInt, CUnsignedInt, CUnsignedInt, CUnsignedInt]

  /* --- Bindings for version and build information --- */
  val mdbx_build: struct_mdbx_build_info = extern
  val mdbx_version: struct_mdbx_version_info = extern

  /* --- Bindings for Cursor functions --- */
  def mdbx_cursor_close(cursor: Ptr[Byte]): Unit = extern
  def mdbx_cursor_count(cursor: Ptr[Byte], countp: Ptr[CSize]): CInt = extern
  def mdbx_cursor_del(cursor: Ptr[Byte], flags: CUnsignedInt): CInt = extern
  def mdbx_cursor_get(cursor: Ptr[Byte], k: KVPtr, v: KVPtr, cursorOp: CUnsignedInt): CInt = extern
  def mdbx_cursor_open(txn: Ptr[Byte], dbi: CUnsignedInt, cursorPtr: Ptr[Ptr[Byte]]): CInt = extern
  def mdbx_cursor_put(cursor: Ptr[Byte], key: KVPtr, data: KVPtr, flags: CUnsignedInt): CInt = extern
  def mdbx_cursor_renew(txn: Ptr[Byte], cursor: Ptr[Byte]): CInt = extern

  /* --- Bindings for Dbi functions --- */
  def mdbx_dbi_close(env: Ptr[Byte], dbi: CUnsignedInt): Unit = extern
  def mdbx_dbi_flags(txn: Ptr[Byte], dbi: CUnsignedInt, flags: Ptr[CUnsignedInt]): CInt = extern
  def mdbx_dbi_flags_ex(txn: Ptr[Byte], dbi: CUnsignedInt, flags: Ptr[CUnsignedInt], state: Ptr[CUnsignedInt]): CInt = extern
  def mdbx_dbi_open(txn: Ptr[Byte], name: CString, flags: CUnsignedInt, dbiPtr: Ptr[UInt]): CInt = extern
  def mdbx_dbi_open_ex(txn: Ptr[Byte], name: CString, flags: CUnsignedInt, dbiPtr: Ptr[UInt],
                       keycmp: cfuncptr_mdbx_cmp, datacmp: cfuncptr_mdbx_cmp): CInt = extern
  def mdbx_dbi_sequence(txn: Ptr[Byte], dbi: CUnsignedInt, result: Ptr[CUnsignedLong], increment: CUnsignedLong): CInt = extern
  def mdbx_dbi_stat(txn: Ptr[Byte], dbi: UInt, stat: Ptr[struct_mdbx_stat], nbytes: CSize): CInt = extern

  /* --- Bindings for misc. functions --- */
  def mdbx_del(txn: Ptr[Byte], dbi: UInt, key: KVPtr, data: KVPtr): CInt = extern
  def mdbx_drop(txn: Ptr[Byte], dbi: UInt, del: CBool): CInt = extern

  /* --- Bindings for Env functions --- */
  def mdbx_env_close(env: Ptr[Byte]): CInt = extern
  def mdbx_env_close_ex(env: Ptr[Byte], dont_sync: CBool): CInt = extern
  def mdbx_env_copy(env: Ptr[Byte], path: CString, flags: CUnsignedInt): CInt = extern
  def mdbx_env_create(envPtr: Ptr[Ptr[Byte]]): CInt = extern
  def mdbx_env_get_fd(env: Ptr[Byte], fd: Ptr[Byte]): CInt = extern
  def mdbx_env_get_flags(env: Ptr[Byte], flags: Ptr[CUnsignedInt]): CInt = extern
  def mdbx_env_get_maxdbs(env: Ptr[Byte], dbs: Ptr[CUnsignedInt]): CInt = extern
  def mdbx_env_get_maxkeysize_ex(env: Ptr[Byte], flags: CUnsignedInt): CInt = extern
  def mdbx_env_get_maxreaders(env: Ptr[Byte], readers: Ptr[CUnsignedInt]): CInt = extern
  def mdbx_env_get_path(env: Ptr[Byte], path: CString): CInt = extern
  def mdbx_env_info(env: Ptr[Byte], info: Ptr[Byte], nbytes: CSize): CInt = extern
  def mdbx_env_info_ex(env: Ptr[Byte], txn: Ptr[Byte], info: Ptr[struct_mdbx_env_info], nbytes: CSize): CInt = extern
  def mdbx_env_open(env: Ptr[Byte], path: CString, flags: CInt, mode: CInt): CInt = extern
  def mdbx_env_set_flags(env: Ptr[Byte], flags: CInt, onoff: CInt): CInt = extern
  def mdbx_env_set_geometry(env: Ptr[Byte], size_lower: CSize, size_now: CSize, size_upper: CSize, growth_step: CSize, shrink_threshold: CSize, pagesize: CSize): CInt = extern
  def mdbx_env_set_mapsize(env: Ptr[Byte], size: CSize): CInt = extern
  // TODO: implement mdbx_env_set_option
  def mdbx_env_set_maxdbs(env: Ptr[Byte], dbs: CInt): CInt = extern
  def mdbx_env_set_maxreaders(env: Ptr[Byte], readers: CInt): CInt = extern
  def mdbx_env_stat(env: Ptr[Byte], stat: Ptr[struct_mdbx_stat], nbytes: CSize): CInt = extern
  def mdbx_env_stat_ex(env: Ptr[Byte], txn: Ptr[Byte], stat: Ptr[struct_mdbx_stat], nbytes: CSize): CInt = extern
  def mdbx_env_sync_ex(env: Ptr[Byte], force: CBool, nonblock: CBool): CInt = extern

  /* --- Bindings for global limits functions --- */
  def mdbx_limits_pgsize_min(): CLong = extern
  def mdbx_limits_pgsize_max(): CLong = extern
  def mdbx_limits_dbsize_min(page_size: CLong): CLong = extern
  def mdbx_limits_dbsize_max(page_size: CLong): CLong = extern
  def mdbx_limits_txnsize_max(page_size: CLong): CLong = extern
  def mdbx_limits_keysize_max(page_size: CLong, flags: CInt): CLong = extern
  def mdbx_limits_valsize_max(page_size: CLong, flags: CInt): CLong = extern
  def mdbx_get_sysraminfo(page_size: Ptr[CLong], total_pages: Ptr[CLong], avail_pages: Ptr[CLong]): CInt = extern

  /* --- Bindings for GET/PUT/REPLACE functions --- */
  def mdbx_get(txn: Ptr[Byte], dbi: UInt, key: KVPtr, data: KVPtr): CInt = extern
  def mdbx_put(txn: Ptr[Byte], dbi: UInt, key: KVPtr, data: KVPtr, flags: CInt): CInt = extern
  def mdbx_replace(txn: Ptr[Byte], dbi: UInt, key: KVPtr, new_data: KVPtr, old_data: KVPtr, flags: CInt): CInt = extern

  /* --- Bindings for misc. functions --- */
  def mdbx_reader_check(env: Ptr[Byte], dead: CInt): CInt = extern
  // mdbx_set_compare => use mdbx_dbi_open_ex instead
  //def mdbx_set_compare(txn: Ptr[Byte], dbi: UInt, cb: CFuncPtr2[Ptr[Byte], Ptr[Byte], Int]): Int = extern
  def mdbx_strerror(errnum: CInt): CString = extern
  def mdbx_strerror_r(errnum: CInt, buf: CString, buflen: CSize): CString = extern // thread-safe alternative to mdbx_strerror

  /* --- Bindings for Txn functions --- */
  def mdbx_txn_abort(txn: Ptr[Byte]): Unit = extern
  def mdbx_txn_begin(env: Ptr[Byte], parentTx: Ptr[Byte], flags: Int, txPtr: Ptr[Ptr[Byte]]): CInt = extern
  def mdbx_txn_commit(txn: Ptr[Byte]): CInt = extern
  def mdbx_txn_commit_ex(txn: Ptr[Byte], latency: Ptr[struct_mdbx_commit_latency]): CInt = extern
  //def mdbx_txn_env(txn: Ptr[Byte]): Ptr[Byte] = extern
  def mdbx_txn_id(txn: Ptr[Byte]): CLong = extern
  def mdbx_txn_info(txn: Ptr[Byte], info: Ptr[struct_mdbx_txn_info], scan_rlt: CBool): CInt = extern
  def mdbx_txn_renew(txn: Ptr[Byte]): CInt = extern
  def mdbx_txn_reset(txn: Ptr[Byte]): Unit = extern

}
