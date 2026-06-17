/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.tool

import junit.framework.TestCase.assertFalse
import org.junit.Test
import org.oewntk.model.DataModel
import org.oewntk.model.LibDummyNanoModel.model1
import org.oewntk.model.LibDummyNanoModel.model3
import org.oewntk.model.ModelEquals.checkDiffs
import org.oewntk.model.Tracing
import org.oewntk.tool.Diffs.findDiffs
import org.oewntk.tool.Diffs.structure
import java.io.PrintStream
import kotlin.test.assertNotEquals

class TestDiffs {

    @Test
    fun testDiffModels() {
        assertFalse(model1 === model3)
        assertFalse(model1 == model3)
        assertNotEquals(model1, model3)
        checkDiffs(model1, model3, ps = ps)
    }

    @Test
    fun testFindDiffModels() {
        assertFalse(model1 === model3)
        assertFalse(model1 == model3)
        assertNotEquals(model1, model3)
        findDiffs(model1, model3, ps = ps)
    }

    @Test
    fun testStructure() {
        structure(DataModel(model1), ps = ps)
    }

    companion object {

        private val silent = if (System.getProperties().containsKey("VERBOSE")) false
        else if (System.getProperties().containsKey("SILENT")) true
        else true

        private val ps: PrintStream = if (!silent) Tracing.psInfo else Tracing.psNull

        //    @JvmStatic
        //    @BeforeClass
        //    fun init() {
        //    }
    }
}
