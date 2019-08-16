package com.dhy.fue

import android.content.Context
import android.content.pm.ProviderInfo
import android.os.Build
import android.support.v4.content.FileProvider


class FixUnhandledEventProvider : FileProvider() {
    override fun attachInfo(context: Context, info: ProviderInfo) {
        super.attachInfo(context, info)
        if (Build.VERSION.SDK_INT < 28) DexPlugin.load(context)
    }
}