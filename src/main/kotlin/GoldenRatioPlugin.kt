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

@State(
    name = "GoldenRationSettings",
    storages = [Storage(value = "GoldenRatio.settings.xml", roamingType = RoamingType.DEFAULT)]
)
class GoldenRatioPlugin : PersistentStateComponent<GoldenRatioPlugin.State> {
    val defaultRatio = (1f + sqrt(5f)) / 2f

    data class State(
        var autoEnabled: Boolean = true,
        var ratio: Float = (1f + sqrt(5f)) / 2f
    )

    private var myState = State()

    var autoEnabled: Boolean
        get() = myState.autoEnabled
        set(value) {
            myState.autoEnabled = value
        }

    var ratio: Float
        get() = myState.ratio
        set(value) {
            myState.ratio = value
        }

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

        val splitters = getSplittersForEditor(manager, editor.component)

        val closedSet = HashSet<Splitter>()

        val leftProportion = 1f / myState.ratio
        val rightProportion = 1f - leftProportion

        for ((it, first) in splitters) {
            val value = if (first) leftProportion else rightProportion / splitters.size
            closedSet.add(it)
            setProportion(it, value)
        }

        for (it in getAllSplitters(manager)) {
            if (it in closedSet) continue
            setProportion(it, 0.5f)
        }
    }

    @Suppress("UnstableApiUsage")
    private fun setProportion(splitter: Splitter, value: Float) {
        if (!Registry.`is`("ide.experimental.ui.animations") || DrawUtil.isSimplifiedUI()) {
            splitter.proportion = value
            return
        }

        val animator = JBAnimator().also { activeAnimators.add(it) }
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
        val INSTANCE: GoldenRatioPlugin
            get() = service()
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

    // TODO get all splitters for current window only
    for (editor in manager.allEditors) {
        val splitters = getSplittersForEditor(manager, editor.component).map { it.first }
        set.addAll(splitters)
    }

    return set
}
