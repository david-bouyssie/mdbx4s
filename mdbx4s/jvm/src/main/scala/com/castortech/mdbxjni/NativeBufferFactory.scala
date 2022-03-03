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
package com.castortech.mdbxjni

import scala.language.implicitConversions

object NativeBufferFactory {

  //private val NULL_NATIVE_BUFFER_WRAPPER: NativeBufferWrapper = null.asInstanceOf[NativeBufferWrapper]
  implicit def nativeBuffer2NativeBufferWrapper(nativeBuffer: NativeBuffer): NativeBufferWrapper = new NativeBufferWrapper(nativeBuffer)

  def create(capacity: Long): NativeBufferWrapper = {
    NativeBuffer.create(capacity)
  }

  def create(data: Array[Byte]): NativeBufferWrapper = {
    if (data == null) null.asInstanceOf[NativeBufferWrapper]
    else create(data, 0, data.length)
  }

  def create(data: String): NativeBufferWrapper = create(cbytes(data))

  private[mdbxjni] def cbytes(strvalue: String): Array[Byte] = {
    val value = strvalue.getBytes
    val rc = new Array[Byte](value.length + 1)
    System.arraycopy(value, 0, rc, 0, value.length)
    rc
  }

  def create(data: Array[Byte], offset: Int, length: Int): NativeBufferWrapper = {
    val rc = create(length.toLong)
    rc.nativeBuffer.write(0L, data, offset, length)
    rc
  }

  def create(pointer: Long, length: Int): NativeBufferWrapper = {
    NativeBuffer.create(pointer, length)
  }
}