package com.dhy.fue

import android.content.Context
import dalvik.system.DexClassLoader
import org.apache.commons.io.FileUtils
import java.io.File
import java.lang.reflect.Array
import java.lang.reflect.Field

object DexPlugin {
    fun load(context: Context) {
        val name = "OnUnhandledKeyEventListener.dex"
        val file = File(context.filesDir, name)
        if (!file.exists()) {
            val input = context.assets.open(name)
            FileUtils.copyInputStreamToFile(input, file)
        }
        try {
            loadPatch(context, file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 加载并安装补丁
     */
    @Throws(Exception::class)
    private fun loadPatch(context: Context, file: File) {
        val pathListField = getPathListField()

        val parentLoader = context.classLoader
        val parentPathList = pathListField.get(parentLoader)!!

        val extraLoader = createDexClassLoader(context, file, parentLoader)
        val extraPathList = pathListField.get(extraLoader)

        val dexElementsField = getDexElementsField(parentPathList)

        val parentElements = dexElementsField.get(parentPathList)!!
        val extraElements = dexElementsField.get(extraPathList)!!

        val newElements = insertElements(parentElements, extraElements)
        dexElementsField.set(parentPathList, newElements)
    }

    private fun createDexClassLoader(context: Context, file: File, parent: ClassLoader): ClassLoader {
        val optPath = context.getDir("opt", Context.MODE_PRIVATE).absolutePath
        return DexClassLoader(file.absolutePath, optPath, null, parent)
    }

    /**
     * 将补丁插入系统DexElements[]最前端，生成一个新的DexElements[]
     */
    private fun insertElements(parentElements: Any, extraElements: Any): Any {
        val pLen = Array.getLength(parentElements)
        val eLen = Array.getLength(extraElements)
        val newElements = Array.newInstance(parentElements.javaClass.componentType!!, pLen + eLen)
        System.arraycopy(extraElements, 0, newElements, 0, eLen)
        System.arraycopy(parentElements, 0, newElements, eLen, pLen)
        return newElements
    }

    private fun getPathListField(): Field {
        val c = Class.forName("dalvik.system.BaseDexClassLoader")
        return c.getDeclaredField("pathList").apply { isAccessible = true }
    }

    private fun getDexElementsField(pathList: Any): Field {
        val c = pathList.javaClass
        return c.getDeclaredField("dexElements").apply { isAccessible = true }
    }
}
