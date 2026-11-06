package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Mnemosyne AI", appName)
  }

  @Test
  fun `evaluate cognitive divergence`() {
    val analysis = com.example.service.CognitiveEvaluator.evaluate(
      instantDescription = "Marc est arrivé avec 12 min de retard",
      imageContextDescription = "Tasse renversée sur la table",
      eveningDescription = "Marc avait un retard d'une heure et c'était une vraie catastrophe",
      dateTimeStr = "Aujourd'hui"
    )
    org.junit.Assert.assertTrue(analysis.exactitudeScore in 0..100)
    org.junit.Assert.assertNotNull(analysis.analyseGenerale)
    org.junit.Assert.assertNotNull(analysis.biaisCognitifDetecte)
  }

  @Test
  fun `test daily tracking calculation`() {
    val repository = com.example.data.MemoryRepository(ApplicationProvider.getApplicationContext())
    val memories = repository.memories.value
    val dailyList = com.example.model.DailyTrackingHelper.computeDailyEvaluations(memories)
    org.junit.Assert.assertTrue(dailyList.isNotEmpty())
    val metrics = com.example.model.DailyTrackingHelper.computeMetrics(dailyList)
    org.junit.Assert.assertTrue(metrics.globalAverageScore in 0..100)
    org.junit.Assert.assertTrue(metrics.totalEvaluatedDays >= 1)
  }
}
