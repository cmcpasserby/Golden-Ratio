package listeners

import GoldenRatioPlugin
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.vfs.VirtualFile


class TabChanged : FileEditorManagerListener {
    override fun selectionChanged(event: FileEditorManagerEvent) = apply(event.manager)

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) = apply(source)

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) = apply(source)

    private fun apply(source: FileEditorManager) {
        if (!GoldenRatioPlugin.INSTANCE.autoEnabled) return
        val manager = source as? FileEditorManagerImpl ?: return
        val editor = manager.selectedEditor ?: return
        GoldenRatioPlugin.INSTANCE.applyRatio(manager, editor)
    }
}
