package com.github.grishberg.profiler.plugins.stages

import com.github.grishberg.profiler.core.ProfileData

interface MethodNavigationAction {
    fun onProfileDataSelected(method: ProfileData)

}
