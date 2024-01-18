package com.heyanle.closure.closure

import com.heyanle.closure.closure.auth.AuthRepository
import com.heyanle.closure.closure.game.GameRepository
import com.heyanle.closure.closure.quota.QuotaRepository
import com.heyanle.closure.preferences.android.AndroidPreferenceStore
import com.heyanle.closure.utils.CoroutineProvider

/**
 * Created by heyanlin on 2024/1/18 17:04.
 */
class ClosureController(
    private val preferenceStore: AndroidPreferenceStore,
    private val gameRepository: GameRepository,
    private val quotaRepository: QuotaRepository,
    private val authRepository: AuthRepository,
) {

    private val scope = CoroutineProvider.mainScope

    private val usernamePref = preferenceStore.getString("username", "")
    val username = usernamePref.stateIn(scope)

    private val passwordPref = preferenceStore.getString("password", "")
    val password = passwordPref.stateIn(scope)

    data class State(
        val username: String,
        val password: String,
        val token: String,

        val isLogging: Boolean = false,
        val isRegistering: Boolean = false,

    )





    fun login(username: String, password: String){

    }

    fun register(username: String, password: String){

    }






}