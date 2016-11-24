package com.crowsofwar.avatar.client.gui;

import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import org.lwjgl.input.Mouse;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.google.common.collect.EvictingQueue;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class SkillsGui extends GuiScreen {
	
	private final List<AbilityCard> cards;
	private ScaledResolution res;
	
	private int scroll;
	private int startScroll, startX, lastX;
	private Queue<Integer> recentVelocity = EvictingQueue.create(10);
	
	private float maxX;
	
	private boolean wasMouseDown;
	
	private final BendingController controller;
	
	public SkillsGui(BendingController controller) {
		this.controller = controller;
		this.cards = new ArrayList<>();
		for (BendingAbility ability : controller.getAllAbilities()) {
			cards.add(new AbilityCard(ability));
		}
		lastX = Mouse.getX();
	}
	
	@Override
	public void initGui() {
		this.res = new ScaledResolution(mc);
	}
	
	//@formatter:off
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		// Draw Gui Background
		pushMatrix();
			// Don't need to negate GUI scale...
			// Already at 1600x900
//			GlStateManager.translate(-300, -300, 0);
		
			int zoomPixels = 300;
			
			float scaleX = width / 1600f, scaleY = height / 900f, scale = scaleX > scaleY ? scaleX : scaleY;
			GlStateManager.scale(width / 1600f, height / 900f, 1);
			GlStateManager.translate(scroll / 30f, 0, 0);
			GlStateManager.translate(-zoomPixels, -zoomPixels, 0);
			GlStateManager.scale((1600 + 2f * zoomPixels) / 1600, (900 + 2f * zoomPixels) / 900, 1);
			
			ResourceLocation background = AvatarUiTextures.bgAir;
			BendingType type = controller.getType();
			if (type == BendingType.EARTHBENDING) background = AvatarUiTextures.bgEarth;
			if (type == BendingType.FIREBENDING) background = AvatarUiTextures.bgFire;
			if (type == BendingType.WATERBENDING) background = AvatarUiTextures.bgWater;
			
			mc.renderEngine.bindTexture(background);
//			drawTexturedModalRect(0, 0, 256, 256, width, height);
			drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 1600, 900, 1600, 900);
		popMatrix();
		
		for (int i = 0; i < cards.size(); i++) {
			maxX = cards.get(i).render(res, i, scroll) - (float) scroll / res.getScaleFactor();
		}
		// maxX is now the last card's maxX
		
	}
	//@formatter:on
	
	@Override
	public void updateScreen() {
		int currentVelocity = 0;
		if (Mouse.isButtonDown(0)) {
			if (!wasMouseDown) {
				wasMouseDown = true;
				startScroll = scroll;
				startX = getMouseX();
			}
			
			currentVelocity = Mouse.getX() - lastX;
			
		} else {
			wasMouseDown = false;
		}
		
		float avg = 0;
		
		int i = 0;
		Iterator<Integer> it = recentVelocity.iterator();
		while (it.hasNext()) {
			int velocity = it.next();
			double mult = (Math.pow(1.2, i)) / (Math.pow(1.2, recentVelocity.size()) - 1);
			avg += velocity * 1.2 * mult;
			i++;
		}
		
		scroll += (avg + currentVelocity) / 2;
		// Positive: scroll left, Negative: scroll right
		if (scroll > 50) {
			scroll = 50;
			currentVelocity = 0;
		}
		if (scroll < -maxX - 50) {
			scroll = (int) (-maxX - 50);
			currentVelocity = 0;
		}
		
		lastX = Mouse.getX();
		recentVelocity.add(currentVelocity);
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		// scroll += Mouse.getDWheel() / 3;
		recentVelocity.add(Mouse.getDWheel() / 1);
	}
	
	private int getMouseX() {
		return Mouse.getX();
	}
	
	public int getMouseScroll() {
		return scroll + getMouseX();
	}
	
}
