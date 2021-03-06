package com.feed_the_beast.ftbquests.gui.quests;

import com.feed_the_beast.ftbquests.client.ClientQuestFile;
import com.feed_the_beast.ftbquests.quest.reward.Reward;
import com.feed_the_beast.ftbquests.quest.theme.property.ThemeProperties;
import com.feed_the_beast.mods.ftbguilibrary.icon.Color4I;
import com.feed_the_beast.mods.ftbguilibrary.utils.MouseButton;
import com.feed_the_beast.mods.ftbguilibrary.utils.TooltipList;
import com.feed_the_beast.mods.ftbguilibrary.widget.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ButtonReward extends Button {
	public final GuiQuests treeGui;
	public final Reward reward;

	public ButtonReward(Panel panel, Reward r) {
		super(panel, r.getTitle(), r.getIcon());
		treeGui = (GuiQuests) panel.getGui();
		reward = r;
		setSize(18, 18);
	}

	@Override
	public Component getTitle() {
		if (reward.isTeamReward()) {
			return super.getTitle().copy().withStyle(ChatFormatting.BLUE);
		}

		return super.getTitle();
	}

	@Override
	public void addMouseOverText(TooltipList list) {
		if (isShiftKeyDown() && isCtrlKeyDown()) {
			list.add(new TextComponent(reward.toString()).withStyle(ChatFormatting.DARK_GRAY));
		}

		if (reward.isTeamReward()) {
			if (reward.addTitleInMouseOverText()) {
				list.add(getTitle());
			}

			Object object = getIngredientUnderMouse();

			if (object instanceof WrappedIngredient && ((WrappedIngredient) object).tooltip) {
				Object ingredient = WrappedIngredient.unwrap(object);

				if (ingredient instanceof ItemStack && !((ItemStack) ingredient).isEmpty()) {
					List<Component> list1 = new ArrayList<>();
					GuiHelper.addStackTooltip((ItemStack) ingredient, list1);
					list1.forEach(list::add);
				}
			}

			list.blankLine();
			reward.addMouseOverText(list);
			list.add(new TranslatableComponent("ftbquests.reward.team_reward").withStyle(ChatFormatting.BLUE, ChatFormatting.UNDERLINE));
		} else {
			if (reward.addTitleInMouseOverText()) {
				list.add(getTitle());
			}

			reward.addMouseOverText(list);
		}
	}

	@Override
	public boolean mousePressed(MouseButton button) {
		if (isMouseOver()) {
			if (button.isRight() || getWidgetType() != WidgetType.DISABLED) {
				onClicked(button);
			}

			return true;
		}

		return false;
	}

	@Override
	public WidgetType getWidgetType() {
		if (!ClientQuestFile.exists() || !ClientQuestFile.INSTANCE.self.isComplete(reward.quest)) {
			return WidgetType.DISABLED;
		}

		return super.getWidgetType();
	}

	@Override
	public void onClicked(MouseButton button) {
		if (button.isLeft()) {
			if (ClientQuestFile.exists()) {
				reward.onButtonClicked(this, ClientQuestFile.INSTANCE.self.getClaimType(reward).canClaim());
			}
		} else if (button.isRight() && ClientQuestFile.exists() && ClientQuestFile.INSTANCE.canEdit()) {
			playClickSound();
			List<ContextMenuItem> contextMenu = new ArrayList<>();
			GuiQuests.addObjectMenuItems(contextMenu, getGui(), reward);
			getGui().openContextMenu(contextMenu);
		}
	}

	@Override
	@Nullable
	public Object getIngredientUnderMouse() {
		return reward.getIngredient();
	}

	@Override
	public void drawBackground(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
		if (isMouseOver()) {
			super.drawBackground(matrixStack, theme, x, y, w, h);
		}
	}

	@Override
	public void draw(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
		int bs = h >= 32 ? 32 : 16;
		GuiHelper.setupDrawing();
		drawBackground(matrixStack, theme, x, y, w, h);
		drawIcon(matrixStack, theme, x + (w - bs) / 2, y + (h - bs) / 2, bs, bs);

		if (treeGui.file.self == null) {
			return;
		} else if (treeGui.contextMenu != null) {
			//return;
		}

		matrixStack.pushPose();
		matrixStack.translate(0F, 0F, 200F);
		RenderSystem.enableBlend();
		boolean completed = false;

		if (treeGui.file.self.getClaimType(reward).isClaimed()) {
			ThemeProperties.CHECK_ICON.get().draw(matrixStack, x + w - 9, y + 1, 8, 8);
			completed = true;
		} else if (treeGui.file.self.isComplete(reward.quest)) {
			ThemeProperties.ALERT_ICON.get().draw(matrixStack, x + w - 9, y + 1, 8, 8);
		}

		matrixStack.popPose();

		if (!completed) {
			String s = reward.getButtonText();

			if (!s.isEmpty()) {
				matrixStack.pushPose();
				matrixStack.translate(x + 19F - theme.getStringWidth(s) / 2F, y + 15F, 200F);
				matrixStack.scale(0.5F, 0.5F, 1F);
				theme.drawString(matrixStack, s, 0, 0, Color4I.WHITE, Theme.SHADOW);
				matrixStack.popPose();
			}
		}
	}
}