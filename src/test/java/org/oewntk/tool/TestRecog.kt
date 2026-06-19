package org.oewntk.tool

import org.junit.Test
import org.oewntk.tool.Utils.recog

class TestRecog {

    @Test
    fun testRecog() {
        listOf("jest%1:10:00::", "force%1:07:00::", "force%1:07:01::", "force%1:19:00::", "05042508-n", "05201846-n", "11479041-n", "force,n", "row,n-1", "force", "row").forEach { input ->
            recog(input)?.let { c ->
                println("$input is $c")
            }
        }
    }
}