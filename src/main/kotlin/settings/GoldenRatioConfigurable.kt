package settings

import GoldenRatioPlugin
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.SearchableConfigurable
import javax.swing.JComponent
import GRBundle.message
import com.intellij.application.options.editor.CheckboxDescriptor
import com.intellij.application.options.editor.checkBox
import com.intellij.ui.dsl.builder.*

class GoldenRatioConfigurable : SearchableConfigurable, Configurable.NoScroll {
    companion object {
        const val ID = "settings.GoldenRatioConfigurable"
    }

    private val settings
        get() = GoldenRatioPlugin.INSTANCE

    private val autoEnabled
        get() = CheckboxDescriptor(message("settings.autoLabel"), settings::autoEnabled)

    private val myPanel = panel {
        group(message("settings.group.generalSettings")) {
            row { checkBox(autoEnabled) }
            row(message("settings.ratioLabelPrefix")) {
                spinner(0.0..100.0, step = 0.001)
                    .bind(
                        {(it.value as Double).toFloat()}, {it, value -> it.value = value.toDouble() },
                        MutableProperty(settings::ratio::get, settings::ratio::set)
                    )
                    .gap(RightGap.SMALL)
                label(message("settings.ratioLabelSuffix", settings.defaultRatio))
            }
        }
    }

    override fun getId(): String = ID

    override fun getDisplayName(): String = "GoldenRatio"

    override fun getHelpTopic(): String? = null // TODO

    override fun createComponent(): JComponent = myPanel

    override fun isModified(): Boolean = myPanel.isModified()

    override fun reset() = myPanel.reset()

    override fun apply() = myPanel.apply()
}