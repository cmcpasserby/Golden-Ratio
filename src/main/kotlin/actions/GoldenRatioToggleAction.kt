package actions

import GoldenRatioPlugin
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.project.DumbAwareToggleAction

class GoldenRatioToggleAction : DumbAwareToggleAction() {
    override fun isSelected(e: AnActionEvent): Boolean = GoldenRatioPlugin.isAutoEnabled

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        GoldenRatioPlugin.isAutoEnabled = state

        if (state) {
            val project = e.project ?: return
            val manager = FileEditorManager.getInstance(project) as? FileEditorManagerImpl ?: return
            val editor = manager.selectedEditor ?: return
            GoldenRatioPlugin.applyGoldenRatio(manager, editor)
        }
    }
}