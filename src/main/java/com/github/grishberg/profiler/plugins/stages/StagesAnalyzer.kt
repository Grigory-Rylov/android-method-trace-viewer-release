package com.github.grishberg.profiler.plugins.stages

import com.github.grishberg.android.profiler.core.ProfileData

class StagesAnalyzer {

    fun analyze(
        stages: Stages,
        methodsAvailability: MethodsAvailability,
        methods: List<ProfileData>
    ): List<WrongStage> {
        val result = mutableListOf<WrongStage>()
        stages.init()

        for (method in methods) {
            stages.updateCurrentStage(method)

            if (!methodsAvailability.isMethodAvailable(method)) {
                continue
            }

            val methodStage = stages.getMethodsStage(method)
            if (stages.shouldMethodStageBeLaterThenCurrent(methodStage)) {
                result.add(WrongStage(method, stages.currentStage, methodStage))
            }
            if (methodStage == null) {
                // method without stage, maybe new
                result.add(WrongStage(method, stages.currentStage, null))
            }
        }
        return result
    }
}
