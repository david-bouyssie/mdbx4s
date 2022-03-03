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

sealed abstract class CursorOpCode extends Enumeration {

  protected final def Op(value: Int): Op = new Op(value)
  class Op(value: Int) extends Val(value, null) {

    /**
     * Obtain the integer code for use by LMDB C API.
     *
     * @return the code
     */
    def getCode(): Int = this.value
  }

  final def withCode(mask: Int): Op = this.apply(mask).asInstanceOf[Op]
}

/**
 * Flags for use when performing a get operation.
 *
 * Unlike most other MDBX enums, this enum is not bit masked.
 */
object CursorGetOp extends CursorOpCode {
  type Op = CursorOpCode#Op

  /**
   * Position at specified key.
   */
  val MDBX_SET: Op = Op(15)

  /**
   * Position at specified key, return both key and data.
   */
  val MDBX_SET_KEY: Op = Op(16)

  /**
   * Position at first key greater than or equal to specified key.
   */
  val MDBX_SET_RANGE: Op = Op(17)
}

/**
 * Flags for use when performing a seek operation.
 *
 * Unlike most other MDBX enums, this enum is not bit masked.
 */
object CursorSeekOp extends CursorOpCode {
  type Op = CursorOpCode#Op

  /**
   * Position at first key/data item.
   */
  val MDBX_FIRST: Op = Op(0)

  /**
   * Position at first data item of current key.
   * Only for MDBX_DUPSORT.
   */
  val MDBX_FIRST_DUP: Op = Op(1)

  /**
   * Position at key/data pair.
   * Only for MDBX_DUPSORT.
   */
  val MDBX_GET_BOTH: Op = Op(2)

  /**
   * Position at given key and at first data greater than or equal to specified data.
   * Only for DbiFlags#MDBX_DUPSORT.
   */
  val MDBX_GET_BOTH_RANGE: Op = Op(3)

  /**
   * Return key/data at current cursor position.
   */
  val MDBX_GET_CURRENT: Op = Op(4)

  /**
   * Return key and up to a page of duplicate data items from current cursor position.
   * Move cursor to prepare for MDBX_NEXT_MULTIPLE.
   * Only for MDBX_DUPFIXED.
   */
  val MDBX_GET_MULTIPLE: Op = Op(5)

  /**
   * Position at last key/data item.
   */
  val MDBX_LAST: Op = Op(6)

  /**
   * Position at last data item of current key.
   * Only for MDBX_DUPSORT.
   */
  val MDBX_LAST_DUP: Op = Op(7)

  /**
   * Position at next data item.
   */
  val MDBX_NEXT: Op = Op(8)

  /**
   * Position at next data item of current key.
   * Only for MDBX_DUPSORT.
   */
  val MDBX_NEXT_DUP: Op = Op(9)

  /**
   * Return key and up to a page of duplicate data items from next cursor position.
   * Move cursor to prepare for MDBX_NEXT_MULTIPLE.
   * Only for MDBX_DUPFIXED.
   */
  val MDBX_NEXT_MULTIPLE: Op = Op(10)

  /**
   * Position at first data item of next key.
   */
  val MDBX_NEXT_NODUP: Op = Op(11)

  /**
   * Position at previous data item.
   * Only for MDBX_DUPSORT.
   */
  val MDBX_PREV: Op = Op(12)

  /**
   * Position at previous data item of current key.
   */
  val MDB_PREV_DUP: Op = Op(13)

  /**
   * Position at last data item of previous key.
   */
  val MDB_PREV_NODUP: Op = Op(14)

  /**
   * Position at previous page and return up to a page of duplicate data items.
   * Only for MDBX_DUPFIXED.
   */
  val MDBX_PREV_MULTIPLE: Op = Op(18)

  /**
   * Positions cursor at first key-value pair greater than or equal to
   * specified, return both key and data, and the return code depends on whether a exact match.
   *
   * For non DUPSORT-ed collections this work the same to \ref MDBX_SET_RANGE,
   * but returns \ref MDBX_SUCCESS if key found exactly or
   * \ref MDBX_RESULT_TRUE if greater key was found.
   *
   * For DUPSORT-ed a data value is taken into account for duplicates,
   * i.e. for a pairs/tuples of a key and an each data value of duplicates.
   * Returns \ref MDBX_SUCCESS if key-value pair found exactly or
   * \ref MDBX_RESULT_TRUE if the next pair was returned. */
  val MDBX_SET_LOWERBOUND: Op = Op(19)

  /**
   * Positions cursor at first key-value pair greater than specified,
   * return both key and data, and the return code depends on whether a
   * upper-bound was found.
   *
   * For non DUPSORT-ed collections this work the same to MDBX_SET_RANGE,
   * but returns MDBX_SUCCESS if the greater key was found or
   * MDBX_NOTFOUND otherwise.
   *
   * For DUPSORT-ed a data value is taken into account for duplicates,
   * i.e. for a pairs/tuples of a key and an each data value of duplicates.
   * Returns MDBX_SUCCESS if the greater pair was returned or
   * MDBX_NOTFOUND otherwise. */
  val MDBX_SET_UPPERBOUND: Op = Op(20)
}
