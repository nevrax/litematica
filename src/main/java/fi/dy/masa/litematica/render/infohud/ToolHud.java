package fi.dy.masa.litematica.render.infohud;

import java.util.List;
import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.materials.MaterialCache;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SubRegionPlacement;
import fi.dy.masa.litematica.selection.AreaSelection;
import fi.dy.masa.litematica.selection.Box;
import fi.dy.masa.litematica.selection.SelectionManager;
import fi.dy.masa.litematica.tool.OperationMode;
import fi.dy.masa.litematica.util.EntityUtils;
import fi.dy.masa.litematica.util.PositionUtils;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.BlockUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ToolHud extends InfoHud
{
    private static final ToolHud INSTANCE = new ToolHud();

    protected ToolHud()
    {
        super();
    }

    public static ToolHud getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected boolean shouldRender()
    {
        return Configs.Generic.TOOL_ITEM_ENABLED.getBooleanValue() && EntityUtils.isHoldingItem(this.mc.player, DataManager.getToolItem());
    }

    @Override
    protected HudAlignment getHudAlignment()
    {
        return (HudAlignment) Configs.Generic.TOOL_HUD_ALIGNMENT.getOptionListValue();
    }

    @Override
    protected void updateHudText()
    {
        OperationMode mode = DataManager.getOperationMode();
        List<String> lines = this.lineList;
        lines.clear();
        String str;
        String green = GuiBase.TXT_GREEN;
        String white = GuiBase.TXT_WHITE;
        String rst = GuiBase.TXT_RST;
        String strYes = green + I18n.format("litematica.label.yes") + rst;
        String strNo = GuiBase.TXT_RED + I18n.format("litematica.label.no") + rst;

        if (mode.getUsesAreaSelection())
        {
            SelectionManager sm = DataManager.getSelectionManager();
            AreaSelection selection = sm.getCurrentSelection();

            if (selection != null)
            {
                lines.add(I18n.format("litematica.hud.area_selection.selected_area", green + selection.getName() + rst));

                String strOr;
                BlockPos o = selection.getExplicitOrigin();

                if (o == null)
                {
                    o = selection.getEffectiveOrigin();
                    strOr = I18n.format("litematica.gui.label.origin.auto");
                }
                else
                {
                    strOr = I18n.format("litematica.gui.label.origin.manual");
                }
                int count = selection.getAllSubRegionBoxes().size();

                str = String.format("%d, %d, %d [%s]", o.getX(), o.getY(), o.getZ(), strOr);
                String strOrigin = I18n.format("litematica.hud.area_selection.origin", green + str + rst);
                String strBoxes = I18n.format("litematica.hud.area_selection.box_count", green + count + rst);

                lines.add(strOrigin + " - " + strBoxes);

                String subRegionName = selection.getCurrentSubRegionBoxName();
                Box box = selection.getSelectedSubRegionBox();

                if (subRegionName != null && box != null)
                {
                    lines.add(I18n.format("litematica.hud.area_selection.selected_sub_region", green + subRegionName + rst));
                    BlockPos p1 = box.getPos1();
                    BlockPos p2 = box.getPos2();

                    if (p1 != null && p2 != null)
                    {
                        BlockPos size = PositionUtils.getAreaSizeFromRelativeEndPositionAbs(p2.subtract(p1));
                        String strDim = green + String.format("%dx%dx%d", size.getX(), size.getY(), size.getZ()) + rst;
                        String strp1 = green + String.format("%d, %d, %d", p1.getX(), p1.getY(), p1.getZ()) + rst;
                        String strp2 = green + String.format("%d, %d, %d", p2.getX(), p2.getY(), p2.getZ()) + rst;
                        lines.add(I18n.format("litematica.hud.area_selection.dimensions_position", strDim, strp1, strp2));
                    }
                }

                if (mode.getUsesBlockPrimary())
                {
                    IBlockState state = mode.getPrimaryBlock();

                    if (state != null)
                    {
                        lines.add(I18n.format("litematica.tool_hud.block_1", this.getBlockString(state)));
                    }
                }

                if (mode.getUsesBlockSecondary())
                {
                    IBlockState state = mode.getSecondaryBlock();

                    if (state != null)
                    {
                        lines.add(I18n.format("litematica.tool_hud.block_2", this.getBlockString(state)));
                    }
                }

                str = green + Configs.Generic.SELECTION_MODE.getOptionListValue().getDisplayName() + rst;
                lines.add(I18n.format("litematica.hud.area_selection.selection_mode", str));
            }
        }
        else if (mode.getUsesSchematic())
        {
            SchematicPlacement schematicPlacement = DataManager.getSchematicPlacementManager().getSelectedSchematicPlacement();

            if (schematicPlacement != null)
            {
                str = I18n.format("litematica.hud.schematic_placement.selected_placement");
                lines.add(String.format("%s: %s%s%s", str, green, schematicPlacement.getName(), rst));

                str = I18n.format("litematica.hud.schematic_placement.sub_region_count");
                int count = schematicPlacement.getSubRegionCount();
                String strCount = String.format("%s: %s%d%s", str, green, count, rst);

                str = I18n.format("litematica.hud.schematic_placement.sub_regions_modified");
                String strTmp = schematicPlacement.isRegionPlacementModified() ? strYes : strNo;
                lines.add(strCount + String.format(" - %s: %s", str, strTmp));

                BlockPos or = schematicPlacement.getOrigin();
                str = String.format("%d, %d, %d", or.getX(), or.getY(), or.getZ());

                lines.add(I18n.format("litematica.hud.area_selection.origin", green + str + rst));

                SubRegionPlacement placement = schematicPlacement.getSelectedSubRegionPlacement();

                if (placement != null)
                {
                    String areaName = placement.getName();
                    str = I18n.format("litematica.hud.schematic_placement.selected_sub_region");
                    lines.add(String.format("%s: %s%s%s", str, green, areaName, rst));

                    str = I18n.format("litematica.hud.schematic_placement.sub_region_modified");
                    strTmp = placement.isRegionPlacementModifiedFromDefault() ? strYes : strNo;
                    lines.add(String.format("%s: %s", str, strTmp));
                }
            }
            else
            {
                String strTmp = "<" + I18n.format("litematica.label.none_lower") + ">";
                str = I18n.format("litematica.hud.schematic_placement.selected_placement");
                lines.add(String.format("%s: %s%s%s", str, white, strTmp, rst));
            }
        }

        str = I18n.format("litematica.hud.selected_mode");
        lines.add(String.format("%s [%s%d%s/%s%d%s]: %s%s%s", str, green, mode.ordinal() + 1, white,
                green, OperationMode.values().length, white, green, mode.getName(), rst));
    }

    protected String getBlockString(IBlockState state)
    {
        ItemStack stack = MaterialCache.getInstance().getItemForState(state);
        String strBlock;

        String green = GuiBase.TXT_GREEN;
        String rst = GuiBase.TXT_RST;

        strBlock = green + stack.getDisplayName() + rst;
        EnumFacing facing = BlockUtils.getFirstPropertyFacingValue(state);

        if (facing != null)
        {
            String gold = GuiBase.TXT_GOLD;
            String strFacing = gold + facing.getName().toLowerCase() + rst;
            strBlock += " - " + I18n.format("litematica.tool_hud.facing", strFacing);
        }

        return strBlock;
    }
}