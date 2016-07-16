package org.amityregion5.onslaught.client.gui;

import java.awt.geom.Rectangle2D;
import java.util.function.Supplier;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;

public abstract class GuiElement implements Disposable {
	
	protected Supplier<Rectangle2D.Float> rectangleSupplier;
	
	public void setRectangleSupplier(Supplier<Rectangle2D.Float> rectangleSupplier) {
		this.rectangleSupplier = rectangleSupplier;
	}
	
	protected Rectangle2D.Float getRectangle() {
		return rectangleSupplier.get();
	}
	
	protected float getX(){
		return getRectangle().x;
	}
	
	protected float getY(){
		return getRectangle().y;
	}
	
	protected float getW(){
		return getRectangle().width;
	}
	
	protected float getH(){
		return getRectangle().height;
	}
	
	public abstract void draw(SpriteBatch batch, ShapeRenderer shapeRender);
	
	@Override
	public void dispose() {}
}
