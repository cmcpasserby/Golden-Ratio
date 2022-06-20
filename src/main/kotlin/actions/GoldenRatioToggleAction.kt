package actions

import GoldenRatioPlugin
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.project.DumbAwareToggleAction
import GRBundle.message

class GoldenRatioToggleAction : DumbAwareToggleAction(message("actions.toggleText")) {
    override fun isSelected(e: AnActionEvent): Boolean = GoldenRatioPlugin.INSTANCE.autoEnabled

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        GoldenRatioPlugin.INSTANCE.autoEnabled = state

        if (state) {
            val project = e.project ?: return
            val manager = FileEditorManager.getInstance(project) as? FileEditorManagerImpl ?: return
            val editor = manager.selectedEditor ?: return
            GoldenRatioPlugin.INSTANCE.applyRatio(manager, editor)
        }
    }
}