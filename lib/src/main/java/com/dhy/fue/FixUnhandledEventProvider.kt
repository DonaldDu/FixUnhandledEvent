package com.dhy.fue

import android.content.Context
import android.content.pm.ProviderInfo
import android.support.v4.content.FileProvider


class FixUnhandledEventProvider : FileProvider() {
    override fun attachInfo(context: Context, info: ProviderInfo) {
        super.attachInfo(context, info)
        DexPlugin.load(context)
    }
}