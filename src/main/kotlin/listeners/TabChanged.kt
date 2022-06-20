package listeners

import GoldenRatioPlugin
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.vfs.VirtualFile


class TabChanged : FileEditorManagerListener {
    override fun selectionChanged(event: FileEditorManagerEvent) {
        if (!GoldenRatioPlugin.isAutoEnabled) return
        val manager = event.manager as? FileEditorManagerImpl ?: return
        val editor = event.newEditor ?: return
        GoldenRatioPlugin.applyGoldenRatio(manager, editor)
    }

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        if (!GoldenRatioPlugin.isAutoEnabled) return
        val manager = source as? FileEditorManagerImpl ?: return
        val editor = manager.selectedEditor ?: return
        GoldenRatioPlugin.applyGoldenRatio(manager, editor)
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        if (!GoldenRatioPlugin.isAutoEnabled) return
        val manager = source as? FileEditorManagerImpl ?: return
        val editor = manager.selectedEditor ?: return
        GoldenRatioPlugin.applyGoldenRatio(manager, editor)
    }
}
