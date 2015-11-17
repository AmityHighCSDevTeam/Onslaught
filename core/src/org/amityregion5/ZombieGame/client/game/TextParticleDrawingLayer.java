package org.amityregion5.ZombieGame.client.game;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.IParticle;
import org.amityregion5.ZombieGame.common.game.model.particle.TextParticle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TextParticleDrawingLayer implements IDrawingLayer {
	
	public static final TextParticleDrawingLayer instance = new TextParticleDrawingLayer();
	
	public TextParticleDrawingLayer() {
	}

	@Override
	public void draw(IEntityModel<?> em, SpriteBatch batch, ShapeRenderer shapeRenderer) {}

	@Override
	public void draw(IParticle p, SpriteBatch batch, ShapeRenderer shapeRenderer) {
		TextParticle model = (TextParticle) p;
		Color c = batch.getColor();
		
		float downScale = 100;
		
		batch.setProjectionMatrix(batch.getProjectionMatrix().cpy().scl(1/downScale));
		
		batch.begin();
		
		GlyphLayout glyph = new GlyphLayout(ZombieGame.instance.mainFont, model.getText());
		ZombieGame.instance.mainFont.draw(batch, glyph, model.getX()*downScale - glyph.width/2, model.getY()*downScale - glyph.height/2);

		batch.end();
		
		batch.setProjectionMatrix(shapeRenderer.getProjectionMatrix());
		
		batch.setColor(c);
	}
}
