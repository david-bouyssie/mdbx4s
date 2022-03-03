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

import com.github.mdbx4s.bindings.ILibraryWrapper

import java.io.{File, IOException}
import java.nio.charset.StandardCharsets
import scala.collection.mutable.ArrayBuffer
import utest.TestSuite

trait AbstractTestSuite extends TestSuite {

  def thisClassName: String
  val libraryWrapper: ILibraryWrapper
  //private val _shutdownHookUtil: IShutdownHookUtil = ShutdownHookUtil

  protected val _dirsToBeDeleted = new ArrayBuffer[File]()
  override def utestAfterAll(): Unit = {
    _dirsToBeDeleted.foreach { dir =>
      // Delete TEMP directory content
      val files = dir.listFiles()

      if (files != null ) {
        files.foreach(_deleteTempFileThenCheck)
      }

      // Delete TEMP directory
      _deleteTempFileThenCheck(dir)
    }
  }

  private[mdbx4s] def className(clazz: Class[_]): String = {
    val name = clazz.getName
    name.substring(name.lastIndexOf('.') + 1)
  }

  def bytes2string(value: Array[Byte]): String = {
    if (value == null) return null
    new String(value, StandardCharsets.UTF_8)
  }

  @inline
  def assertArrayEquals(arg1: Array[Byte], arg2: Array[Byte]): Unit = {
    utest.assert(arg1 sameElements arg2)
  }

  private val testRootDir = "./target/tests/test-data"

  @throws[IOException]
  private[mdbx4s] def createTestDirectory(name: String, assertNotExist: Boolean = false): File = {
    val testDir = new File(new File(testRootDir), name)

    if (assertNotExist) {
      assert(!testDir.exists(), s"directory '$testDir' already exists")
    }

    testDir.mkdirs()

    testDir
  }

  private val randomGen = new scala.util.Random()

  private[mdbx4s] def createTempDirectory(prefix: String, deleteDirOnExit: Boolean = true): File = {
    val dirName = s"${prefix}_${System.currentTimeMillis()}_${randomGen.nextInt(1000)}_tmpdir"

    val dir = createTestDirectory(dirName, true).getCanonicalFile

    if (deleteDirOnExit) {
      println("Created TEMP dir: " + dir)
      _dirsToBeDeleted += dir
      /*_shutdownHookUtil.addShutdownHook { () =>

        // Delete TEMP directory content
        val files = dir.listFiles()

        if (files != null ) {
          files.foreach(_deleteTempFileThenCheck)
        }

        // Delete TEMP directory
        _deleteTempFileThenCheck(dir)
      }*/
    }

    dir
  }

  private def _deleteTempFileThenCheck(file: File): Boolean = {
    val label = if (file.isDirectory) "dir" else "file"

    val wasDeleted = file.delete()
    if (wasDeleted) {
      if (file.isDirectory)
        println(s"Deleted TEMP $label: $file")
    }
    else println(s"Can't delete TEMP $label: $file")

    wasDeleted
  }
}
