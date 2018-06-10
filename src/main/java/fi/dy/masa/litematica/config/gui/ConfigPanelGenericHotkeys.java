package fi.dy.masa.litematica.config.gui;

import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import fi.dy.masa.litematica.config.Hotkeys;
import fi.dy.masa.litematica.event.InputEventHandler;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.gui.ConfigPanelHotkeysBase;
import fi.dy.masa.malilib.config.gui.button.ConfigButtonHotkey;

public class ConfigPanelGenericHotkeys extends ConfigPanelHotkeysBase
{
    public ConfigPanelGenericHotkeys(LitematicaConfigPanel parent)
    {
        super("Generic Hotkeys", parent);
    }

    @Override
    protected IConfigValue[] getConfigs()
    {
        return new IConfigValue[0];
    }

    @Override
    protected void onSettingsChanged()
    {
        InputEventHandler.getInstance().updateUsedKeys();
    }

    @Override
    public void addOptions(ConfigPanelHost host)
    {
        this.clearOptions();

        int x = 10;
        int y = 10;
        int i = 0;
        int labelWidth = this.getMaxLabelWidth(Hotkeys.values()) + 10;

        for (Hotkeys hotkey : Hotkeys.values())
        {
            this.addLabel(i, x, y + 7, labelWidth, 8, 0xFFFFFFFF, hotkey.getName());
            this.addConfigComment(x, y + 2, labelWidth, 10, hotkey.getComment());

            this.addButton(new ConfigButtonHotkey(i + 1, x + labelWidth + 30, y, 150, 20, hotkey, this), this.getConfigListener());

            i += 2;
            y += 21;
        }
    }
}
