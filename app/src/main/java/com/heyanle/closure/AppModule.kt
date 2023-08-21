package com.heyanle.closure

import android.app.Application
import com.google.gson.Gson
import com.heyanle.closure.appcenter.AppCenterUpdateController
import com.heyanle.closure.base.hekv.HeKV
import com.heyanle.closure.base.preferences.PreferenceStore
import com.heyanle.closure.base.preferences.android.AndroidPreferenceStore
import com.heyanle.closure.base.preferences.hekv.HeKVPreferenceStore
import com.heyanle.closure.base.preferences.mmkv.MMKVPreferenceStore
import com.heyanle.closure.base.theme.ThemeController
import com.heyanle.closure.closure.ClosureController
import com.heyanle.closure.net.Net
import com.heyanle.closure.closure.items.ItemsController
import com.heyanle.closure.closure.repository.InstanceRepository
import com.heyanle.closure.closure.stage.StageController
import com.heyanle.closure.utils.getFilePath
import com.heyanle.injekt.api.InjektModule
import com.heyanle.injekt.api.InjektScope
import com.heyanle.injekt.api.addAlias
import com.heyanle.injekt.api.addSingletonFactory
import com.heyanle.injekt.api.get
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * Created by HeYanLe on 2023/7/29 20:15.
 * https://github.com/heyanLE
 */


object RootModule : InjektModule {
    override fun InjektScope.registerInjectables() {
        addSingletonFactory {
            Gson()
        }
        addSingletonFactory {

            Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
        }
    }

}

// Controller
class ControllerModule(private val application: Application) : InjektModule {
    override fun InjektScope.registerInjectables() {
        addSingletonFactory {
            application
        }

        addSingletonFactory {
            Net()
        }

        addSingletonFactory {
            get<Net>().game
        }
        addSingletonFactory {
            get<Net>().auth
        }
        addSingletonFactory {
            get<Net>().common
        }
        addSingletonFactory {
            ThemeController(get(), get())
        }
        addSingletonFactory {
            AppCenterUpdateController()
        }

        addSingletonFactory {
            ItemsController(application, get(), get())
        }

        addSingletonFactory {
            StageController(application, get(), get())
        }

//        addSingletonFactory {
//            ClosureLogControllerFactory(application, get())
//        }
//
//        addSingletonFactory {
//            GameInfoRepositoryFactory(application, get())
//        }

        addSingletonFactory {
            ClosureController(application, get(), get(), get(), get())
        }
        addSingletonFactory {
            InstanceRepository(application, get())
        }

    }
}

class PreferencesModule(private val application: Application) : InjektModule {
    override fun InjektScope.registerInjectables() {
        addSingletonFactory {
            AndroidPreferenceStore(application)
        }
        addSingletonFactory {
            MMKVPreferenceStore(application)
        }

        addSingletonFactory {
            HeKV(application.getFilePath(), "global")
        }
        addSingletonFactory {
            HeKVPreferenceStore(get())
        }
        // 默认使用 sp
        addAlias<AndroidPreferenceStore, PreferenceStore>()

    }
}