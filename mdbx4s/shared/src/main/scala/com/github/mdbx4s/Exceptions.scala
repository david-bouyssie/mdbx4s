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
 * Superclass for all MDBX custom exceptions.
 * Constructs an instance with the provided detailed message and cause.
 *
 * @param errorMessage the detail message
 * @param errorCause   original cause
 */
@SerialVersionUID(1L)
class MdbxException(private val errorMessage: String, private val errorCause: Throwable = null) extends Exception(errorMessage, errorCause) {
  override def getCause(): Throwable = errorCause
}


/**
 * Constructs an instance with the provided detailed message.
 *
 * @param resultCode  the result code.
 * @param resultCodeDescription  the result code description.
 * @param message the detailed message.
 */
@SerialVersionUID(1L)
class MdbxNativeException private[mdbx4s](
  /**
   * Result code returned by the LMDB C function.
   */
  private val resultCode: Int,
  private val resultCodeDescription: String,
  private val message: String
) extends MdbxException({
  if (resultCodeDescription == null) s"$message (RC=$resultCode)"
  else s"$message: $resultCodeDescription (RC=$resultCode)"
}) {

  def this(resultCode: Int, message: String) = {
    this(resultCode, null, message)
  }

  /**
   * Obtain the LMDB C-side result code.
   *
   * @return the C-side result code
   */
  final def getErrorCode(): Int = resultCode
}
