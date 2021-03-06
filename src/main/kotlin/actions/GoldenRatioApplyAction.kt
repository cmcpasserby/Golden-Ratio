package actions

import GoldenRatioPlugin
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.project.DumbAwareAction
import GRBundle.message

class GoldenRatioApplyAction : DumbAwareAction(message("actions.applyText")) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val manager = FileEditorManager.getInstance(project) as? FileEditorManagerImpl ?: return
        val editor = manager.selectedEditor ?: return
        GoldenRatioPlugin.INSTANCE.applyRatio(manager, editor)
    }
}
