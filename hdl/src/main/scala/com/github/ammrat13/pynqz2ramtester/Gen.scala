package com.github.ammrat13.pynqz2ramtester

import spinal.core.ClockDomainConfig
import spinal.core.LOW

import spinal.core.SpinalConfig
import spinal.core.Verilog

/** Generates code for [[PynqZ2RamTester]]
  *
  * Uses a hard-coded `SpinalConfig`. Compiles to Verilog and outputs in
  * `build/`, and sets the resets to active-low.
  */
object Gen extends App {
  SpinalConfig(
    mode = Verilog,
    targetDirectory = "build/",
    defaultConfigForClockDomains = ClockDomainConfig(
      resetActiveLevel = LOW,
      softResetActiveLevel = LOW
    )
  ).generate(new PynqZ2RamTester)
}
