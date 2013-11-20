/*
 * Copyright (c) 2013 original authors
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
package org.eigengo.monitor.agent.akka

import org.eigengo.monitor.{TestCounter, TestCounterInterface}
import org.specs2.mutable.SpecificationLike

class JavaApiActorCellMonitoringAspectSpec
  extends AbstractJavaApiActorCellMonitoringAspectSpec
  with SpecificationLike with ActorCellMonitoringAspectConfigurer with ActorCellMonitoringTaggingConvention {

  sequential
  import Aspects._
  import TestCounterInterface._

  "Counting the number of actors using the java api" should {
    configure("JavaApi.conf")
    // records the count of actors, grouped by simple class name
    "Record the actor count, and let us exclude an unnamed anonymous inner class actor" in {
      val unnamedGreetPrinterTags = getTags(unnamedGreetPrinter, unnamedGreetPrinterProps)
      val namedGreetPrinterTags = getTags(greetPrinter, greetPrinterProps)
      val greeterTags = getTags(greeter, greeterProps)

      Thread.sleep(100L)
      TestCounterInterface.foldlByAspect(actorCount)(takeLHS) must containAllOf(Seq(
        TestCounter(actorCount, 1, unnamedGreetPrinterTags),
        TestCounter(actorCount, 1, namedGreetPrinterTags),
        TestCounter(actorCount, 1, greeterTags)))

      system.shutdown()
      Thread.sleep(1000L)

      TestCounterInterface.foldlByAspect(actorCount)(takeLHS) must containAnyOf(Seq(
        TestCounter(actorCount, 0, unnamedGreetPrinterTags),
        TestCounter(actorCount, 0, namedGreetPrinterTags),
        TestCounter(actorCount, 0, greeterTags)))
    }

  }

}