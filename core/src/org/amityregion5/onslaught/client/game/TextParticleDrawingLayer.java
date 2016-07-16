package org.amityregion5.onslaught.client.game;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.common.game.model.IEntityModel;
import org.amityregion5.onslaught.common.game.model.IParticle;
import org.amityregion5.onslaught.common.game.model.particle.TextParticle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * A particle drawing layer for a text particle
 * @author sergeys
 *
 */
public class TextParticleDrawingLayer implements IDrawingLayer {

	/**
	 * An instance of a text particle drawing layer to cut down on objects created
	 */
	public static final TextParticleDrawingLayer instance = new TextParticleDrawingLayer();

	public TextParticleDrawingLayer() {}

	@Override
	public void draw(IEntityModel<?> em, SpriteBatch batch, ShapeRenderer shapeRenderer, Rectangle cullRect) {}

	@Override
	public void draw(IParticle p, SpriteBatch batch, ShapeRenderer shapeRenderer, Rectangle cullRect) 
	{
		//Get the text particle
		TextParticle model = (TextParticle) p;
		//Get the batch color
		Color c = batch.getColor();

		//Get the downscale value
		float downScale = 100;

		//Downscale the matrix
		batch.setProjectionMatrix(batch.getProjectionMatrix().cpy().scl(1 / downScale));

		//Create a glyph layout
		GlyphLayout glyph = new GlyphLayout(Onslaught.instance.mainFont, model.getText());
		//Draw the text
		Onslaught.instance.mainFont.draw(batch, glyph, model.getX() * downScale - glyph.width / 2, model.getY() * downScale - glyph.height / 2);

		//Reset the projection matrix
		batch.setProjectionMatrix(shapeRenderer.getProjectionMatrix());

		batch.setColor(c);
	}
}
