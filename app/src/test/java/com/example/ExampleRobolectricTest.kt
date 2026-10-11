package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.SampleData
import com.example.viewmodel.AiCalculatorForm
import com.example.viewmodel.LandMapViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("LandMap AI", appName)
    }

    @Test
    fun `verify sample properties loaded`() {
        val properties = SampleData.sampleProperties
        assertTrue(properties.isNotEmpty())
        val first = properties.first()
        assertTrue(first.askingPriceLakhs > 0)
        assertTrue(first.plotSizeSqFt > 0)
    }

    @Test
    fun `verify AI price estimation calculates value`() {
        val vm = LandMapViewModel()
        val state = vm.uiState.value
        assertNotNull(state.aiEstimation)
        assertTrue(state.aiEstimation!!.estimatedTotalAvgLakhs > 0)
    }
}
