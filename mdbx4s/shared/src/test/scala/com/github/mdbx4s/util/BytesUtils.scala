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
package com.github.mdbx4s.util

import java.nio.charset.StandardCharsets

object BytesUtils {

  def serializeString(str: String): Array[Byte] = str.getBytes(StandardCharsets.UTF_8)

  def getLong(b: Array[Byte]): Long = getLong(b, 0)

  def getLong(b: Array[Byte], offset: Int): Long = (b(offset + 0) & 0xFFL) << 56 | (b(offset + 1) & 0xFFL) << 48 | (b(offset + 2) & 0xFFL) << 40 | (b(offset + 3) & 0xFFL) << 32 | (b(offset + 4) & 0xFFL) << 24 | (b(offset + 5) & 0xFFL) << 16 | (b(offset + 6) & 0xFFL) << 8 | (b(offset + 7) & 0xFFL) << 0

  def fromLong(n: Long): Array[Byte] = {
    val b = new Array[Byte](8)
    setLong(b, n)
    b
  }

  private def setLong(b: Array[Byte], n: Long): Unit = {
    setLong(b, n, 0)
  }

  private def setLong(b: Array[Byte], n: Long, offset: Int): Unit = {
    b(offset + 0) = (n >>> 56).toByte
    b(offset + 1) = (n >>> 48).toByte
    b(offset + 2) = (n >>> 40).toByte
    b(offset + 3) = (n >>> 32).toByte
    b(offset + 4) = (n >>> 24).toByte
    b(offset + 5) = (n >>> 16).toByte
    b(offset + 6) = (n >>> 8).toByte
    b(offset + 7) = (n >>> 0).toByte
  }

  /*
  def longToBytes(x: Long): Array[Byte] = {
    val buffer = java.nio.ByteBuffer.allocate(Long.BYTES)
    buffer.putLong(x)
    buffer.array
  }*/
}
