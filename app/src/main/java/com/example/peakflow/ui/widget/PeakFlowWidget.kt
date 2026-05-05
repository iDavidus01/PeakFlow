package com.example.peakflow.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.peakflow.MainActivity
import com.example.peakflow.R
import com.example.peakflow.data.MountainRepository

class PeakFlowWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { updateWidget(context, manager, it) }
    }

    companion object {
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, PeakFlowWidget::class.java))
            ids.forEach { updateWidget(context, manager, it) }
        }

        fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val repo = MountainRepository.getInstance(context)
            val mountains = repo.mountains.value.sortedBy { it.totalDifficulty }
            val conquered = repo.conqueredIds.value
            val stats = repo.userStats.value
            val next = mountains.firstOrNull { it.id !in conquered }

            val readiness = next?.let { m ->
                val missingPts = maxOf(0, m.condReq - stats.condition) +
                    maxOf(0, m.techReq - stats.technique) +
                    maxOf(0, m.acclReq - stats.acclimatization) +
                    maxOf(0, m.riskReq - stats.risk)
                maxOf(0, 100 - missingPts * 15)
            } ?: 100

            val views = RemoteViews(context.packageName, R.layout.widget_peakflow).apply {
                setTextViewText(
                    R.id.widget_mountain_name,
                    next?.name ?: context.getString(R.string.widget_all_conquered)
                )
                setTextViewText(R.id.widget_readiness, "$readiness%")

                val intent = Intent(context, MainActivity::class.java)
                val pending = PendingIntent.getActivity(
                    context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setOnClickPendingIntent(R.id.widget_root, pending)
            }

            manager.updateAppWidget(widgetId, views)
        }
    }
}
