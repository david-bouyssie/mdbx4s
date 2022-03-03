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

import com.github.mdbx4s.MdbxException

/**
 * A helper base class which is used to track a pointer to a native
 * structure or class.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
private[mdbx4s] class NativeObject private[bindings](var self: Long) {
  if (self == 0L) throw new OutOfMemoryError("Failure allocating native heap memory")

  def pointer: Long = {
    checkAllocated()
    self
  }

  def isAllocated(): Boolean = {
    self != 0L
  }

  protected def checkAllocated(): Unit = {
    if (!isAllocated) throw new MdbxException("Native object has been freed.")
  }
}