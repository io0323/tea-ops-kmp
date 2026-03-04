package com.teaops.shared.presenter.production

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.teaops.shared.domain.entity.AlertLevel
import com.teaops.shared.domain.entity.ProcessingStep

/**
 * 生産監視画面のUI状態。
 */
data class ProductionMonitorUiState(
  val currentStep: ProcessingStep,
  val remainingSeconds: Long,
  val currentTemperature: Double,
  val qualityScore: Int,
  val warningMessage: String,
  val alertLevel: AlertLevel,
  val progressPercent: Int,
  val progressLabel: String,
  val isDelayed: Boolean
)

/**
 * 茶葉加工ステータスを監視するメイン画面。
 */
@Composable
fun ProductionMonitorScreen(
  uiState: ProductionMonitorUiState,
  onNextStep: () -> Unit,
  modifier: Modifier = Modifier
) {
  val animatedBackground by animateColorAsState(
    targetValue = when {
      uiState.alertLevel == AlertLevel.CRITICAL -> Color(0xFFB00020)
      uiState.alertLevel == AlertLevel.CAUTION -> Color(0xFFFFC107)
      else -> Color(0xFFF4F6F8)
    },
    animationSpec = tween(durationMillis = 600)
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(animatedBackground)
      .padding(20.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    StepStatusCard(
      stepName = uiState.currentStep.stepName,
      remainingSeconds = uiState.remainingSeconds,
      qualityScore = uiState.qualityScore,
      warningMessage = uiState.warningMessage,
      progressPercent = uiState.progressPercent,
      progressLabel = uiState.progressLabel,
      isDelayed = uiState.isDelayed
    )

    TemperatureGauge(
      current = uiState.currentTemperature,
      target = uiState.currentStep.targetTemperature,
      modifier = Modifier
        .fillMaxWidth()
        .height(260.dp)
    )

    Button(
      onClick = onNextStep,
      modifier = Modifier
        .fillMaxWidth()
        .height(64.dp),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
    ) {
      Text(
        text = "次の工程へ",
        style = MaterialTheme.typography.titleMedium,
        color = Color.White
      )
    }
  }
}

/**
 * 現在工程と残り時間を大きく表示するカード。
 */
@Composable
private fun StepStatusCard(
  stepName: String,
  remainingSeconds: Long,
  qualityScore: Int,
  warningMessage: String,
  progressPercent: Int,
  progressLabel: String,
  isDelayed: Boolean
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp)
  ) {
    Column(
      modifier = Modifier.padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = "現在工程: $stepName",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "残り ${remainingSeconds.coerceAtLeast(0)} 秒",
        style = MaterialTheme.typography.titleLarge
      )
      Text(
        text = "品質スコア: ${qualityScore.coerceIn(0, 100)}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
      )
      Text(
        text = "工程進捗: ${progressPercent.coerceIn(0, 100)}% ($progressLabel)",
        style = MaterialTheme.typography.titleMedium
      )
      Text(
        text = if (isDelayed) "進捗状態: 遅延" else "進捗状態: 定常",
        style = MaterialTheme.typography.bodyLarge,
        color = if (isDelayed) Color(0xFFB00020) else Color(0xFF1B5E20),
        fontWeight = FontWeight.SemiBold
      )
      Text(
        text = warningMessage,
        style = MaterialTheme.typography.bodyLarge
      )
    }
  }
}

/**
 * 温度の現在値と目標値を比較表示する半円ゲージ。
 */
@Composable
private fun TemperatureGauge(
  current: Double,
  target: Double,
  modifier: Modifier = Modifier
) {
  val maxValue = (target * 1.4).coerceAtLeast(80.0)
  val progress = (current / maxValue).coerceIn(0.0, 1.0).toFloat()
  val gaugeColor = when {
    current > target + 5.0 -> Color(0xFFD32F2F)
    current > target -> Color(0xFFFFA000)
    else -> Color(0xFF2E7D32)
  }

  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val stroke = 28.dp.toPx()
      val startAngle = 180f
      val sweepBackground = 180f
      val sweepProgress = sweepBackground * progress
      val centerY = size.height * 0.9f
      val radius = size.minDimension * 0.36f

      drawArc(
        color = Color(0xFFCFD8DC),
        startAngle = startAngle,
        sweepAngle = sweepBackground,
        useCenter = false,
        topLeft = Offset(size.width / 2f - radius, centerY - radius),
        size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
        style = Stroke(width = stroke, cap = StrokeCap.Round)
      )

      drawArc(
        color = gaugeColor,
        startAngle = startAngle,
        sweepAngle = sweepProgress,
        useCenter = false,
        topLeft = Offset(size.width / 2f - radius, centerY - radius),
        size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
        style = Stroke(width = stroke, cap = StrokeCap.Round)
      )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "${current.toInt()}°C",
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "目標 ${target.toInt()}°C",
        style = MaterialTheme.typography.titleMedium
      )
    }
  }
}
