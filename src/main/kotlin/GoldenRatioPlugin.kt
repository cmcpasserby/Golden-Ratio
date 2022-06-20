import com.intellij.openapi.Disposable
import com.intellij.openapi.components.*
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.impl.EditorsSplitters
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.ui.Splitter
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.registry.Registry
import com.intellij.ui.ClientProperty
import com.intellij.ui.DrawUtil
import com.intellij.util.SmartList
import com.intellij.util.animation.JBAnimator
import com.intellij.util.animation.animation
import java.awt.Component
import kotlin.math.sqrt

private val ratio = (1f + sqrt(5f)) / 2f
private val leftRatio = 1f / ratio
private val rightRatio = 1f - leftRatio

@State(
    name = "GoldenRationSettings",
    storages = [Storage(value = "GoldenRatio.settings.xml", roamingType = RoamingType.DEFAULT)]
)
class GoldenRatioPlugin : PersistentStateComponent<GoldenRatioPlugin.State> {
    data class State(var autoEnabled: Boolean = true)

    private var myState = State()

    @Suppress("UnstableApiUsage")
    private val activeAnimators = SmartList<JBAnimator>()

    override fun getState(): State {
        return myState
    }

    override fun loadState(state: State) {
        myState = state
    }

    fun applyRatio(manager: FileEditorManagerImpl, editor: FileEditor) {
        activeAnimators.forEach { Disposer.dispose(it) }
        activeAnimators.clear()

        val project = manager.project
        val splitters = getSplittersForEditor(manager, editor.component)

        val closedSet = HashSet<Splitter>()

        for ((it, first) in splitters) {
            val value = if (first) leftRatio else rightRatio / splitters.size
            closedSet.add(it)
            setProportion(project, it, value)
        }

        for (it in getAllSplitters(manager)) {
            if (it in closedSet) continue
            setProportion(project, it, 0.5f)
        }
    }

    @Suppress("UnstableApiUsage")
    private fun setProportion(disposable: Disposable, splitter: Splitter, value: Float) {
        if (!Registry.`is`("ide.experimental.ui.animations") || DrawUtil.isSimplifiedUI()) {
            splitter.proportion = value
            return
        }

        val animator = JBAnimator(disposable).also { activeAnimators.add(it) }
        animator.animate(
            animation(splitter.proportion, value, splitter::setProportion).apply {
                duration = 350
                runWhenExpiredOrCancelled {
                    Disposer.dispose(animator)
                    activeAnimators.remove(animator)
                }
            }
        )
    }

    companion object {
        private val INSTANCE: GoldenRatioPlugin
            get() = service()

        var isAutoEnabled: Boolean
            get() = INSTANCE.myState.autoEnabled
            set(value) {
                INSTANCE.myState.autoEnabled = value
            }

        fun applyGoldenRatio(manager: FileEditorManagerImpl, editor: FileEditor) = INSTANCE.applyRatio(manager, editor)
    }
}

private fun getSplittersForEditor(manager: FileEditorManagerImpl, editor: Component): List<Pair<Splitter, Boolean>> {
    val mainSplitters = manager.mainSplitters

    val list = mutableListOf<Pair<Splitter, Boolean>>()
    var comp = editor as Component?

    while (comp != mainSplitters && comp != null) {
        val parent = comp.parent

        if (parent is Splitter && ClientProperty.isTrue(parent, EditorsSplitters.SPLITTER_KEY)) {
            list.add(Pair(parent, parent.firstComponent == comp))
        }

        comp = parent
    }

    return list
}

private fun getAllSplitters(manager: FileEditorManagerImpl): Set<Splitter> {
    val set = HashSet<Splitter>()

    for (editor in manager.allEditors) {
        val splitters = getSplittersForEditor(manager, editor.component).map { it.first }
        set.addAll(splitters)
    }

    return set
}
