package ar.com.ticket_printer.printer;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 *
 * @author adji A hack to get the pageLen!!!
 */
public class VirtualGraphics extends Graphics2D {

    static class Limits {

        int height = 0;
        int width = 0;

        synchronized private void extend(int x0, int y0, int x1, int y1) {
            if (this.height < y0) {
                this.height = y0;
            }
            if (this.height < y1) {
                this.height = y1;
            }
            if (this.width < x0) {
                this.width = x0;
            }
            if (this.width < x1) {
                this.width = x1;
            }
        }

        synchronized private int getHeightLimit() {
            return height;
        }

        synchronized private int getWidthLimit() {
            return width;
        }

        private void extend(Rectangle r) {
            extend(r.x, r.y, r.x + r.width, r.y + r.height);
        }
    }
    private final Limits limit = new Limits();
    private final FontRenderContext fontRenderContext = new FontRenderContext(new AffineTransform(1, 0, 0, 1, 0, 0), false, false);

    public int getHeightLimit() {
        return limit.getHeightLimit();
    }

    public int getWidthLimit() {
        return limit.getWidthLimit();
    }

    public VirtualGraphics() {
    }

    static private void debug(String message, Object... args) {
        //Log some debug.
    }

    @Override
    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
        debug("----------> draw3DRect %d %d %d %d", x, y, width, height);
        limit.extend(x, y, x + width, y + height);
        //delegate.draw3DRect(x, y, width, height, raised);
    }

    @Override
    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
        debug("----------> fill3DRect %d %d %d %d", x, y, width, height);
        limit.extend(x, y, x + width, y + height);
        //delegate.fill3DRect(x, y, width, height, raised);
    }

    @Override
    public void draw(Shape s) {
        debug("----------> draw %s", s.getBounds());
        limit.extend(s.getBounds());
        //limit.extend(s.getBounds2D());
        //delegate.draw(s);
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        int height = img.getHeight(new ImageObserver() {
            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                debug("----------> drawImage observer %d %d %d %d", x, y, width, height);
                return true;
            }
        });
        debug("----------> drawImage %d", height);
        //obs.imageUpdate(img, null, x, y, width, height);
        return true;
        //return delegate.drawImage(img, xform, obs);
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        debug("----------> drawImage %d %d %d %d", img.getWidth(), img.getHeight(), x, y);
        //delegate.drawImage(img, op, x, y);
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        debug("----------> drawRenderedImage %d %d", img.getHeight(), img.getWidth());
        //delegate.drawRenderedImage(img, xform);
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        debug("----------> drawRenderableImage %d %d", img.getHeight(), img.getWidth());
        //delegate.drawRenderableImage(img, xform);
    }

    @Override
    public void drawString(String str, int x, int y) {
        debug("----------> drawString %d %d", x, y);
        //delegate.drawString(str, x, y);
    }

    @Override
    public void drawString(String str, float x, float y) {
        debug("----------> drawString %f %f", x, y);
        //delegate.drawString(str, x, y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        debug("----------> drawString %d %d", x, y);
        //delegate.drawString(iterator, x, y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        debug("----------> drawString %f %f", x, y);
        //delegate.drawString(iterator, x, y);
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        debug("----------> drawGlyphVector %f %f", x, y);
        //delegate.drawGlyphVector(g, x, y);
    }

    @Override
    public void fill(Shape s) {
        debug("----------> fill %s", s);
        //delegate.fill(s);
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        debug("----------> hit %s %s", rect, s);
        return true;
        //return delegate.hit(rect, s, onStroke);
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        debug("----------> getDeviceConfiguration");
        return null;
        //return delegate.getDeviceConfiguration();
    }

    @Override
    public void setComposite(Composite comp) {
        debug("----------> setComposite");
        //delegate.setComposite(comp);
    }

    @Override
    public void setPaint(Paint paint) {
        debug("----------> setPaint");
        //delegate.setPaint(paint);
    }

    @Override
    public void setStroke(Stroke s) {
        debug("----------> setStroke");
        //delegate.setStroke(s);
    }

    @Override
    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        debug("----------> setRenderingHint");
        //delegate.setRenderingHint(hintKey, hintValue);
    }

    @Override
    public Object getRenderingHint(RenderingHints.Key hintKey) {
        debug("----------> getRenderingHint");
        return null;
        //return delegate.getRenderingHint(hintKey);
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        debug("----------> setRenderingHints");
        //delegate.setRenderingHints(hints);
    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        debug("----------> addRenderingHints");
        //delegate.addRenderingHints(hints);
    }

    @Override
    public RenderingHints getRenderingHints() {
        debug("----------> RenderingHints");
        return null;
        //return delegate.getRenderingHints();
    }

    @Override
    public void translate(int x, int y) {
        debug("----------> translate");
        //delegate.translate(x, y);
    }

    @Override
    public void translate(double tx, double ty) {
        debug("----------> translate");
        //delegate.translate(tx, ty);
    }

    @Override
    public void rotate(double theta) {
        debug("----------> rotate");
        //delegate.rotate(theta);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        debug("----------> rotate");
        //delegate.rotate(theta, x, y);
    }

    @Override
    public void scale(double sx, double sy) {
        debug("----------> scale");
        //delegate.scale(sx, sy);
    }

    @Override
    public void shear(double shx, double shy) {
        debug("----------> shear");
        //delegate.shear(shx, shy);
    }

    @Override
    public void transform(AffineTransform Tx) {
        debug("----------> transform");
        //delegate.transform(Tx);
    }

    @Override
    public void setTransform(AffineTransform Tx) {
        debug("----------> setTransform");
        //delegate.setTransform(Tx);
    }

    @Override
    public AffineTransform getTransform() {
        debug("----------> getTransform");
        return new AffineTransform(1, 0, 0, 1, 0, 0);
        //return delegate.getTransform();
    }

    @Override
    public Paint getPaint() {
        debug("----------> getPaint");
        return new Paint() {
            public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
                debug("----------> Paint createContext");
                return null;
            }

            public int getTransparency() {
                debug("----------> Paint getTransparency");
                return Paint.OPAQUE;
            }
        };
        //return delegate.getPaint();
    }

    @Override
    public Composite getComposite() {
        debug("----------> getComposite");
        return new Composite() {
            public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
                return new CompositeContext() {
                    public void dispose() {
                    }

                    public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
                    }
                };
            }
        };
        //return delegate.getComposite();
    }

    @Override
    public void setBackground(Color color) {
        debug("----------> setBackground");
        //delegate.setBackground(color);
    }

    @Override
    public Color getBackground() {
        debug("----------> getBackground");
        return new Color(0);
        //return delegate.getBackground();
    }

    @Override
    public Stroke getStroke() {
        debug("----------> getStroke");
        return new Stroke() {
            public Shape createStrokedShape(Shape p) {
                return p;
            }
        };
        //return delegate.getStroke();
    }

    @Override
    public void clip(Shape s) {
        debug("----------> clip");
        //delegate.clip(s);
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        debug("----------> FontRenderContext");
        return fontRenderContext;
        //return delegate.getFontRenderContext();
    }

    @Override
    public Graphics create() {
        debug("----------> create");
        return this;
//        return new VirtualGraphics((Graphics2D) delegate.create());
    }

    @Override
    public Graphics create(int x, int y, int width, int height) {
        debug("----------> create");
        return this;
        //return delegate.create(x, y, width, height);
    }

    @Override
    public Color getColor() {
        debug("----------> getColor");
        return new Color(0);
        //return delegate.getColor();
    }

    @Override
    public void setColor(Color c) {
        debug("----------> setColor");
        //delegate.setColor(c);
    }

    @Override
    public void setPaintMode() {
        debug("----------> setPaintMode");
        //delegate.setPaintMode();
    }

    @Override
    public void setXORMode(Color c1) {
        debug("----------> setXORMode");
        //delegate.setXORMode(c1);
    }

    @Override
    public Font getFont() {
        debug("----------> getFont");
        return new Font("NoneFont", Font.PLAIN, 10);
        //return delegate.getFont();
    }
    private Font currFont;

    @Override
    public void setFont(Font font) {
        debug("----------> setFont");
        currFont = font;
        //delegate.setFont(font);
    }

    @Override
    public FontMetrics getFontMetrics() {
        debug("----------> getFontMetrics");
        return new FontMetrics(new Font("NoneFont", Font.PLAIN, 10)) {
        };
        //return delegate.getFontMetrics();
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        debug("----------> getFontMetrics");
        return new FontMetrics(f) {
        };
        //return delegate.getFontMetrics(f);
    }

    @Override
    public Rectangle getClipBounds() {
        debug("----------> getClipBounds");
        return new Rectangle(0, 0, 1000, 1000);
        //return delegate.getClipBounds();
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        debug("----------> clipRect");
        //delegate.clipRect(x, y, width, height);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        debug("----------> setClip");
        //delegate.setClip(x, y, width, height);
    }

    @Override
    public Shape getClip() {
        debug("----------> getClip");
        return new Rectangle(0, 0, 1000, 1000);
        //return delegate.getClip();
    }

    @Override
    public void setClip(Shape clip) {
        debug("----------> setClip");
        //delegate.setClip(clip);
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        debug("----------> copyArea %d %d %d %d %d %d", x, y, width, height, dx, dy);
        //delegate.copyArea(x, y, width, height, dx, dy);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        debug("----------> drawLine %d %d %d %d", x1, y1, x2, y2);
        //delegate.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        debug("----------> fillRect %d %d %d %d", x, y, width, height);
        // Here the height is the total page length that I want to avoid.
        limit.extend(x, y, width, 0);
        //delegate.fillRect(x, y, width, height);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        debug("----------> drawRect %d %d %d %d", x, y, width, height);
        //delegate.drawRect(x, y, width, height);
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        debug("----------> clearRect %d %d %d %d", x, y, width, height);
        //delegate.clearRect(x, y, width, height);
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        debug("----------> drawRoundRect %d %d %d %d", x, y, width, height);
        //delegate.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        debug("----------> fillRoundRect %d %d %d %d", x, y, width, height);
        //delegate.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        debug("----------> drawOval %d %d %d %d", x, y, width, height);
        //delegate.drawOval(x, y, width, height);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        debug("----------> fillOval %d %d %d %d", x, y, width, height);
        //delegate.fillOval(x, y, width, height);
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        debug("----------> drawArc %d %d %d %d", x, y, width, height);
        //delegate.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        debug("----------> fillArc %d %d %d %d", x, y, width, height);
        //delegate.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        debug("----------> drawPolyline");
        //delegate.drawPolyline(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        debug("----------> drawPolygon");
        //delegate.drawPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawPolygon(Polygon p) {
        debug("----------> drawPolygon");
        //delegate.drawPolygon(p);
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        debug("----------> fillPolygon");
        //delegate.fillPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void fillPolygon(Polygon p) {
        debug("----------> fillPolygon");
        //delegate.fillPolygon(p);
    }

    @Override
    public void drawChars(char[] data, int offset, int length, int x, int y) {
        debug("----------> drawChars %d %d %d %d", offset, length, x, y);
        limit.extend(x, y, x, y);

        FontRenderContext frc = getFontRenderContext();
        TextLayout layout = new TextLayout(new String(data, offset, length), currFont, frc);
        Rectangle bounds = layout.getPixelBounds(frc, x, y);
        debug("----------> drawChars size %d %d %d %d", bounds.x, bounds.y, bounds.width, bounds.height);
        limit.extend(bounds);

        /*        int textwidth = (int) (currFont.getStringBounds(text, frc).getWidth());
         int textheight = (int) (currFont.getStringBounds(text, frc).getHeight());

         int height = currFont.getSize();
         int len = getFontMetrics(currFont).charsWidth(data, offset, length);
         debug("----------> drawChars str size %d %d ", len, height);
         */
        limit.extend(x, y, x, y);
        //delegate.drawChars(data, offset, length, x, y);
    }

    @Override
    public void drawBytes(byte[] data, int offset, int length, int x, int y) {
        debug("----------> drawBytes %d %d %d %d", offset, length, x, y);
        //delegate.drawBytes(data, offset, length, x, y);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        debug("----------> drawImage1 %d %d %d %d", x, y, img.getWidth(null), img.getHeight(null));
        //observer.imageUpdate(img, y, x, y, y, y);
        return true;
        //return delegate.drawImage(img, x, y, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        debug("----------> drawImage2 %d %d %d %d", x, y, width, height);
        limit.extend(0, 0, x + width, y + height);
        //observer.imageUpdate(img, y, x, y, y, y);
        return true;
        //return delegate.drawImage(img, x, y, width, height, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        debug("----------> drawImage3 %d %d %d %d", x, y, img.getWidth(null), img.getHeight(null));
        //observer.imageUpdate(img, y, x, y, y, y);
        return true;
        //return delegate.drawImage(img, x, y, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        debug("----------> drawImage4 %d %d %d %d", x, y, width, height);
        //observer.imageUpdate(img, y, x, y, y, y);
        return true;
        //return delegate.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        debug("----------> drawImage5 %d %d %d %d %d %d %d %d", dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
        //observer.imageUpdate(img, sy2, sx2, sy2, dy2, sy2);
        return true;
        //return delegate.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        debug("----------> drawImage6 %d %d %d %d %d %d %d %d", dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
        observer.imageUpdate(img, sy2, sx2, sy2, dy2, sy2);
        return true;
        //return delegate.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
    }

    @Override
    public void dispose() {
        debug("----------> dispose");
        //delegate.dispose();
    }
}
