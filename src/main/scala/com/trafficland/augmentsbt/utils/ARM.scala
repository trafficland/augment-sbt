package com.trafficland.augmentsbt.utils

object ARM {
  def auto[C <: AutoCloseable](c: C) = new ScalaARM(c)
  class ScalaARM[C <: AutoCloseable](c: C) {
    def map[T](f: C => T): T = {
      try {
        f(c)
      } finally {
        c.close()
      }
    }
  }
}
