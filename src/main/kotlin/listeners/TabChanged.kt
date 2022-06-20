package listeners

import GoldenRatioPlugin
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.vfs.VirtualFile


class TabChanged : FileEditorManagerListener {
    override fun selectionChanged(event: FileEditorManagerEvent) {
        if (!GoldenRatioPlugin.INSTANCE.autoEnabled) return
        val manager = event.manager as? FileEditorManagerImpl ?: return
        val editor = event.newEditor ?: return
        GoldenRatioPlugin.INSTANCE.applyRatio(manager, editor)
    }

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        if (!GoldenRatioPlugin.INSTANCE.autoEnabled) return
        val manager = source as? FileEditorManagerImpl ?: return
        val editor = manager.selectedEditor ?: return
        GoldenRatioPlugin.INSTANCE.applyRatio(manager, editor)
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        if (!GoldenRatioPlugin.INSTANCE.autoEnabled) return
        val manager = source as? FileEditorManagerImpl ?: return
        val editor = manager.selectedEditor ?: return
        GoldenRatioPlugin.INSTANCE.applyRatio(manager, editor)
    }
}
