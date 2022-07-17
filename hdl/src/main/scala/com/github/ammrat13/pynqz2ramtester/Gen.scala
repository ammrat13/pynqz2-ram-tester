package com.github.ammrat13.pynqz2ramtester

import spinal.core.SpinalConfig

/** Generates code for [[PynqZ2RamTester]]
  *
  * Generates `SpinalConfig` from command-line arguments:
  * {{{
  * Usage: SpinalCore [options]
  *
  *   --vhdl                   Select the VHDL mode
  *   --verilog                Select the Verilog mode
  *   -o, --targetDirectory <value>
  *                            Set the target directory
  * }}}
  */
object Gen extends App {
  SpinalConfig.shell(args).generate(new PynqZ2RamTester)
}
