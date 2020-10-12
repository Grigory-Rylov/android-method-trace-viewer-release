package com.github.grishberg.profiler.plugins

import com.github.grishberg.android.profiler.core.AnalyzerResult
import com.github.grishberg.android.profiler.core.ThreadItem
import com.github.grishberg.profiler.chart.stages.StagesFacade
import com.github.grishberg.profiler.common.AppLogger
import com.github.grishberg.profiler.common.CoroutinesDispatchers
import com.github.grishberg.profiler.common.settings.SettingsRepository
import com.github.grishberg.profiler.plugins.stages.MethodsAvailabilityImpl
import com.github.grishberg.profiler.plugins.stages.StageAnalyzerDialog
import com.github.grishberg.profiler.plugins.stages.StagesAnalyzer
import com.github.grishberg.profiler.plugins.stages.methods.StagesAnalyzerLogic
import com.github.grishberg.profiler.plugins.stages.methods.StagesLoadedAction
import com.github.grishberg.profiler.plugins.stages.methods.StagesRelatedToMethodsFactory
import com.github.grishberg.profiler.ui.dialogs.info.FocusElementDelegate
import kotlinx.coroutines.CoroutineScope
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JOptionPane

class PluginsFacade(
    private val frame: JFrame,
    private val stagesFacade: StagesFacade,
    private val focusElementDelegate: FocusElementDelegate,
    private val settings: SettingsRepository,
    private val logger: AppLogger,
    private val coroutineScope: CoroutineScope,
    private val dispatchers: CoroutinesDispatchers,
    private val stagesLoadedAction: StagesLoadedAction? = null
) {
    var currentThread: ThreadItem? = null
    var currentTraceProfiler: AnalyzerResult? = null


    fun createPluginsMenu(menuBar: JMenuBar) {
        val tools = JMenu("Tools")
        val stageAnalyzer = JMenuItem("Stage analyzer")
        tools.add(stageAnalyzer)
        menuBar.add(tools)
        stageAnalyzer.addActionListener {
            runStageAnalyzer()
        }

        tools.add(stagesFacade.clearStagesMenuItem)
    }

    private fun runStageAnalyzer() {
        if (currentTraceProfiler == null || currentThread == null) {
            JOptionPane.showMessageDialog(
                frame,
                "Open or record trace first".trimIndent(),
                "Stage Analyzer error",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        val methods = currentTraceProfiler?.data?.get(currentThread?.threadId) ?: return

        val ui = StageAnalyzerDialog(frame)
        val methodsAvailability = MethodsAvailabilityImpl()
        StagesAnalyzerLogic(
            StagesAnalyzer(),
            ui,
            settings,
            methods,
            focusElementDelegate,
            coroutineScope,
            dispatchers,
            stagesFacade.stagesList,
            stagesFacade.storedStages,
            StagesRelatedToMethodsFactory(methodsAvailability, logger),
            methodsAvailability,
            logger,
            stagesLoadedAction
        )
    }
}
