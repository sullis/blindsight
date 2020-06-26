/*
 * Copyright 2020 Terse Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tersesystems.blindsight

import java.util.ServiceLoader

import com.tersesystems.blindsight.AST.BObject

/**
 * Resolves a [[com.tersesystems.blindsight.AST.BObject]] to an [[Argument]].
 *
 * {{{
 * val argument: Argument = ArgumentResolver(bobj("foo" -> "bar"))
 * }}}
 *
 * This is a service interface trait, which should be implemented the service loader pattern.
 */
trait ArgumentResolver {
  def resolve(bobject: BObject): Argument
}

object ArgumentResolver {

  def apply(bobject: BObject): Argument = {
    argumentResolver.resolve(bobject)
  }

  private val argumentResolverLoader = ServiceLoader.load(classOf[ArgumentResolver])

  private lazy val argumentResolver: ArgumentResolver = {
    import javax.management.ServiceNotFoundException
    val iter                       = argumentResolverLoader.iterator()
    var resolver: ArgumentResolver = null;
    while (iter.hasNext && resolver == null) {
      resolver = iter.next()
    }
    if (resolver == null) {
      throw new ServiceNotFoundException("No argument resolver found!")
    } else {
      resolver
    }
  }

  class Passthrough extends ArgumentResolver {
    override def resolve(bobject: BObject): Argument = new Argument(bobject)
  }

}