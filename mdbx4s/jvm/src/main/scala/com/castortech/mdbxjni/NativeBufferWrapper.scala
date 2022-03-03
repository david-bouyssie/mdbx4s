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

class NativeBufferWrapper(private[mdbxjni] val nativeBuffer: NativeBuffer) extends AnyVal {

  def isNull(): Boolean = { nativeBuffer == null }
  def pointer: Long = {
    if (nativeBuffer == null) 0L else nativeBuffer.pointer()
  }
  def capacity: Long = {
    if (nativeBuffer == null) 0L else nativeBuffer.capacity()
  }

  def delete(): Unit = {
    if (this.nativeBuffer != null) {
      this.nativeBuffer.delete()
    }
  }

  def toByteArray(): Array[Byte] = {
    if (nativeBuffer == null) return null
    nativeBuffer.toByteArray
  }
}
