package package18;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalToggleButtonUI;

//import package18.Plotter6165i.PlotterPane;

/**
 * @author 46ac4334
 *
 */
public class Plotter6165i extends JInternalFrame {

	private class ControlPanel extends JInternalFrame {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		private int counter = 0;

		public ControlPanel(final String string) {
			super(string);
			this.init();
		}

		private void init() {
			this.setResizable(false);
			this.addComponentListener(new ComponentListener() {

				@Override
				public void componentHidden(final ComponentEvent e) {
				}

				@Override
				public void componentMoved(final ComponentEvent e) {
				}

				/**
				 * Assume that when the user resizes the component, the new size becomes the new
				 * preferred size. When components are re-arranged by the
				 * <code>arrangeWindows()</code> method, they retain their sizes.
				 */
				@Override
				public void componentResized(final ComponentEvent e) {
					final var component = e.getComponent();
					component.setPreferredSize(component.getSize());
				}

				@Override
				public void componentShown(final ComponentEvent e) {
				}
			});

		}

	}

	public class LegendItem {
		private final Font font;
		private final double h;
		private final String text;
		private final double w;
		private final double x;
		private final double y;

		public LegendItem(final String text0, final Font font0) {
			this.font = font0;
			this.text = text0;
			this.x = Double.NaN;
			this.y = Double.NaN;
			this.w = Double.NaN;
			this.h = Double.NaN;
		}

		public LegendItem(final String text2, final Font font2, final double x, final double y, final double w,
				final double h) {
			this.font = font2;
			this.text = text2;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
	}

	/**
	 * @author bakis
	 *
	 */
	public interface Painter1 {

		void apply(Graphics2D g, int width, int height);

	}

	/**
	 * @author 46ac4334
	 *
	 */
	public class PlotterPane extends JPanel
			implements MouseMotionListener, MouseListener, MouseWheelListener, ComponentListener {

		public static final int ALL = 5;

		public static final int BOT = 4;

		public static final int LEFT = 3;

		private static final int NONE = 0;

		public static final int RIGHT = 1;

		private static final long serialVersionUID = 1L;

		public static final int TOP = 2;

		/**
		 * Bounds the plot. Anything outside this box is legend, label, title, etc.
		 */
		private Shape boundingBox = null;

		private final boolean boxSelected = false;

		private final BasicStroke boxStroke = new BasicStroke(0.5f);

		private final JTextArea captionArea = new JTextArea("Edit the Caption here.");

		public List<Color> colors = new ArrayList<>();

		private Rectangle2D.Double dataBounds;

		private final int dragSide = PlotterPane.NONE;

		private boolean drawMarkers = false;

		public boolean dummy = true;

		public List<Color> fillColors = new ArrayList<>();

		private Insets insets;

		public Path2D.Double logPath;

//		public final Color majorGridColor = Color.getHSBColor(.5f, .5f, .75f);
		public final Color majorGridColor = Color.getHSBColor(.5f, .25f, .9f);

		private Color[] markerColors = new Color[] { Color.getHSBColor(0f, 1f, .85f), Color.getHSBColor(.33f, 1f, .85f),
				Color.getHSBColor(.67f, 1f, .85f) };

		private Color[] markerLineColors;

		private double markerRadius = 5;

		private final List<Shape> markers = new ArrayList<>();

		// private AffineTransform pathToUnitBoxTransform;

		private int mouseDownButton;

		private Point mouseDownPoint;

		private final Insets oldInsets = new Insets(0, 0, 0, 0);

		private AffineTransform oldPan;

		/**
		 * If true, an old transient object is on screen.
		 */
		public boolean oldShape = false;

		private double oldTx = 0;

		private double oldTy = 0;

		private final List<Painter1> painters = new ArrayList<>();

		private final AffineTransform pan = AffineTransform.getTranslateInstance(0, 0);

		private Shape path = null;

		public List<Shape> paths = new ArrayList<>();

		private Color plotColor = Color.getHSBColor(0f, 0f, 0f);

		private Stroke plotStroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

		/**
		 * Transforms the plot path from its original unit square to the area over which
		 * it is to be plotted.
		 */
		public AffineTransform plotTransform;

		private AffineTransform plotTransform0;

		@SuppressWarnings("unused")
		private final double s = 1;

		private AffineTransform scaleInstance;

		private final Color selectedColor = Color.getHSBColor(.75f, 1f, 1f);

		private Shape selectedMarker;

		private int selectedMarkerIndex;

		private final BasicStroke selectedStroke = new BasicStroke(2);

		public List<Shape> semilogPaths = new ArrayList<>();

		boolean showMainPlotPath = true;

		private final Stroke stroke2 = new BasicStroke(1.4f);

		private final Stroke stroke3 = new BasicStroke(1.0f);

		public List<Stroke> strokes = new ArrayList<>();

		private double sx = 1;

		private double sy = 1;

		private final JPopupMenu textAreaPopup = new JPopupMenu();

		// public List<JToggleButton> toggleButtons = new ArrayList<>();
		public List<JToggleButton> toggleButtons = new ArrayList<>();

		private AffineTransform transform;

		public Color transientColor = Color.WHITE;

		public Shape transientShape = null;

		public String transientText;

		public int transientTextX;

		public int transientTextY;

		private AffineTransform translateInstance;

		private int tx = 0;

		private int ty = 0;

		public List<Boolean> visibilities = new ArrayList<>();

		private int xTickHeight = 15;

		private boolean zoomX = false;

		private boolean zoomY = false;

		/**
		 * Constructor.
		 */
		public PlotterPane() {
			this.setPreferredSize(Plotter6165i.this.getPlotterPaneDefaultDimension());
			this.setBackground(Color.WHITE);
			this.insets = Plotter6165i.this.insets;
			this.addMouseMotionListener(this);
			this.addMouseListener(this);
			this.addMouseWheelListener(this);
			this.addComponentListener(this);
			this.setLayout(null);
			this.captionArea.setLineWrap(true);
			this.captionArea.setWrapStyleWord(true);
			this.captionArea.setComponentPopupMenu(this.textAreaPopup);
			this.captionArea.setEditable(true);
			final var fontItem = new JMenuItem("Font");
			this.textAreaPopup.add(fontItem);
			final ActionListener actionListener = arg0 -> PlotterPane.this.chooseFontFor(PlotterPane.this.captionArea);
			fontItem.addActionListener(actionListener);
			final var sizeItem = new JMenuItem("Font Size");
			this.textAreaPopup.add(sizeItem);
			sizeItem.addActionListener(arg0 -> PlotterPane.this.chooseFontSizeFor(this.captionArea));
//			this.add(this.captionArea);
		}

		/**
		 * @param arg0
		 */
		public PlotterPane(final boolean arg0) {
			super(arg0);
			this.setPreferredSize(Plotter6165i.this.getPlotterPaneDefaultDimension());
		}

		/**
		 * @param arg0
		 */
		public PlotterPane(final LayoutManager arg0) {
			super(arg0);
			this.setPreferredSize(Plotter6165i.this.getPlotterPaneDefaultDimension());
		}

		/**
		 * @param arg0
		 * @param isDoubleBuffered
		 */
		public PlotterPane(final LayoutManager arg0, final boolean isDoubleBuffered) {
			super(arg0, isDoubleBuffered);
			this.setPreferredSize(Plotter6165i.this.getPlotterPaneDefaultDimension());
		}

		protected void chooseFontFor(final JTextArea textArea) {
			final var ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			final var availableFontFamilyNames = ge.getAvailableFontFamilyNames();
			final var selectedFont = JOptionPane.showInternalInputDialog(this,
					"Choose Font (Currently " + textArea.getFont().getFontName() + "):", "Font Selector",
					JOptionPane.PLAIN_MESSAGE, null, availableFontFamilyNames, availableFontFamilyNames[0]);
			if (selectedFont != null) {
				final var oldFont = textArea.getFont();
				final var b = oldFont.getStyle();
				final var c = oldFont.getSize();
				final var newFont = new Font((String) selectedFont, b, c);
				textArea.setFont(newFont);
			}
		}

		protected void chooseFontSizeFor(final JTextArea textArea) {
			final Object selectedSize = JOptionPane.showInternalInputDialog(this,
					"Set Font Size (currently " + textArea.getFont().getSize() + "):", "Font Size",
					JOptionPane.PLAIN_MESSAGE);
			if (selectedSize != null) {
				final var size = Float.parseFloat((String) selectedSize);
				final var oldFont = textArea.getFont();
				final var newFont = oldFont.deriveFont(size);
				textArea.setFont(newFont);
			}
		}

		@Override
		public void componentHidden(final ComponentEvent e) {
		}

		@Override
		public void componentMoved(final ComponentEvent e) {
		}

		@Override
		public void componentResized(final ComponentEvent e) {
			this.repaint();
			final var n = this.getComponentCount();
			for (var i = 0; i < n; ++i) {
				this.getComponent(i).invalidate();
				this.getComponent(i).repaint();
			}
		}

		@Override
		public void componentShown(final ComponentEvent e) {
		}

		/**
		 * @param g
		 * @param pathIterator2
		 * @param sigDig
		 */
		private void drawXTickLabels(final Graphics2D g, final PathIterator pathIterator2, final int sigDig) {

			while (!pathIterator2.isDone()) {
				final var coords = new double[6];
				pathIterator2.currentSegment(coords);
				final var format = "%." + String.valueOf(sigDig) + "g";
				final var tickText = Plotter6165i.this.xDateOrigin == Long.MIN_VALUE
						? String.format(format, coords[0]).replaceFirst("(\\.\\d*)0+($|e)", "$1$2")
						: Plotter6165i.getDateinstance().format(new Date(
								Plotter6165i.this.xDateOrigin + (long) coords[0] * Plotter6165i.MILLIS_PER_DAY));
				final var fontMetrics = g.getFontMetrics();
				final var stringBounds = fontMetrics.getStringBounds(tickText, g);
				final var labelPoint = new Point2D.Double(coords[0], coords[1]);
				this.plotTransform.transform(labelPoint, labelPoint);
				g.drawString(tickText, (float) (labelPoint.x - stringBounds.getCenterX()),
						(float) (this.getHeight() - this.insets.bottom - stringBounds.getMinY()));
				pathIterator2.next();
				pathIterator2.next();
			}
		}

		/**
		 * @param g
		 * @param pathIteratorY
		 * @param sigDig
		 */
		private void drawYTickLabels(final Graphics2D g, final PathIterator pathIteratorY, final int sigDig) {
			while (!pathIteratorY.isDone()) {
				final var coords = new double[6];// pathIteratorY stores coords in this array.
				pathIteratorY.currentSegment(coords);
				final var format = "%." + String.valueOf(sigDig) + "g";
				final var yLabelValue = Plotter6165i.this.semiLog ? Math.pow(1e1, coords[1]) : coords[1];
				final var tickText = String.format(format, yLabelValue).replaceFirst("(\\.\\d*)0+($|e)", "$1$2") + " ";
				final var fontMetrics = g.getFontMetrics();
				final var stringBounds = fontMetrics.getStringBounds(tickText, g);
				final var labelPoint = new Point2D.Double(coords[0], coords[1]);
				this.plotTransform.transform(labelPoint, labelPoint);
				g.drawString(tickText, (float) (this.insets.left - stringBounds.getMaxX()),
						(float) (labelPoint.y - stringBounds.getCenterY()));
				pathIteratorY.next();
				pathIteratorY.next();
			}
		}

		/**
		 * @return the captionArea
		 */
		public JTextArea getCaptionArea() {
			return this.captionArea;
		}

		public Container getCommentField() {
			return this.captionArea;
		}

		/**
		 * @return the markerColors
		 */
		public Color[] getMarkerColors() {
			return this.markerColors;
		}

		/**
		 * @return the markerLineColors
		 */
		public Color[] getMarkerLineColors() {
			return this.markerLineColors;
		}

		/**
		 * @return the markerRadius
		 */
		public double getMarkerRadius() {
			return this.markerRadius;
		}

		/**
		 * @return the mouseDownPoint
		 */
		public Point getMouseDownPoint() {
			return this.mouseDownPoint;
		}

		/**
		 * @return the plotColor
		 */
		public Color getPlotColor() {
			return this.plotColor;
		}

		/**
		 * @return the plotStroke
		 */
		public Stroke getPlotStroke() {
			return this.plotStroke;
		}

//		private AffineTransform getPlotTransform(final double s, final AffineTransform pan) {
//			final AffineTransform plotTransform = new AffineTransform(this.transform);
//			if (Plotter6165i.this.pathToUnitBoxTransform == null) {
//				Methods2.stacktraceAll("pathToUnitBoxTransform is null");
//			} else {
//				plotTransform.concatenate(Plotter6165i.this.pathToUnitBoxTransform);
//			}
//			plotTransform.scale(s, s);
//			if (pan != null) {
//				plotTransform.concatenate(this.pan);
//			}
//			return plotTransform;
//		}

		private AffineTransform getPlotTransform(final double sx2, final double sy2, final AffineTransform pan2) {
			final var plotTransform = new AffineTransform(
					this.transform == null ? new AffineTransform() : this.transform);
			if (Plotter6165i.this.pathToUnitBoxTransform == null) {
				throw new RuntimeException("pathToUnitBoxTransform is null");
			} else {
				plotTransform.concatenate(Plotter6165i.this.pathToUnitBoxTransform);
			}
			plotTransform.scale(sx2, sy2);
			if (this.pan != null) {
				plotTransform.concatenate(this.pan);
			}
			return plotTransform;
		}

		/**
		 * @return the xTickHeight
		 */
		public int getxTickHeight() {
			return this.xTickHeight;
		}

		/**
		 * @return the drawMarkers
		 */
		public boolean isDrawMarkers() {
			return this.drawMarkers;
		}

		@Override
		public void mouseClicked(final MouseEvent mouseEvent) {
		}

		@Override
		public void mouseDragged(final MouseEvent mouseEvent) {

			final var x = mouseEvent.getX();
			final var y = mouseEvent.getY();
			final var oldX = this.mouseDownPoint.x;
			final var oldY = this.mouseDownPoint.y;
			final var dx = (x - oldX) / this.plotTransform.getScaleX();
			final var dy = (y - oldY) / this.plotTransform.getScaleY();
			if (this.mouseDownButton == 1) {
				this.pan.setToTranslation(this.oldPan.getTranslateX() + dx, this.oldPan.getTranslateY() + dy);
				this.repaint();
			}
			if (this.boxSelected) {
				switch (this.dragSide) {
				case ALL:
					this.tx = x - oldX;
					this.ty = y - oldY;
					this.pan.setToTranslation(this.tx + this.oldTx, this.ty + this.oldTy);
					// this.insets.left = (this.oldInsets.left + x) - oldX;
					// this.insets.top = (this.oldInsets.top + y) - oldY;
					// this.insets.right = (this.oldInsets.right - x) + oldX;
					// this.insets.bottom = (this.oldInsets.bottom - y) + oldY;
					break;
				case LEFT:
					this.insets.left = this.oldInsets.left + x - oldX;
					this.repaint();
					break;
				case TOP:
					this.insets.top = this.oldInsets.top + y - oldY;
					this.repaint();
					break;
				case RIGHT:
					this.insets.right = this.oldInsets.right - x + oldX;
					this.repaint();
					break;
				case BOT:
					this.insets.bottom = this.oldInsets.bottom - y + oldY;
					this.repaint();
					break;
				case NONE:
					break;
				}
			}
		}

		@Override
		public void mouseEntered(final MouseEvent mouseEvent) {
		}

		@Override
		public void mouseExited(final MouseEvent mouseEvent) {
		}

		@Override
		public void mouseMoved(final MouseEvent mouseEvent) {
			final var point = mouseEvent.getPoint();
			final var oldSelectedMarker = this.selectedMarker;
			this.selectedMarker = null;
			this.selectedMarkerIndex = -1;
			if (this.markers != null) {
				var index = 0;
				this.setToolTipText(null);
				for (final Shape marker : this.markers) {
					if (marker != null && point != null && marker.contains(point)) {
						this.selectedMarker = marker;
						final var tipTextAvailable = Plotter6165i.this.tips != null
								&& Plotter6165i.this.tips.size() > index && Plotter6165i.this.tips.get(index) != null;
						this.setToolTipText(
								tipTextAvailable ? Plotter6165i.this.tips.get(index) : String.format("%,d", index));
						this.selectedMarkerIndex = index;
						break;
					}
					++index;
				}
			}
			if (this.selectedMarker != oldSelectedMarker) {
				this.repaint();
			}
		}

		@Override
		public void mousePressed(final MouseEvent mouseEvent) {
			this.mouseDownPoint = mouseEvent.getPoint();
			this.mouseDownButton = mouseEvent.getButton();
			this.oldPan = new AffineTransform(this.pan);
			this.oldInsets.left = this.insets.left;
			this.oldInsets.right = this.insets.right;
			this.oldInsets.top = this.insets.top;
			this.oldInsets.bottom = this.insets.bottom;
			this.oldTx = this.pan.getTranslateX();
			this.oldTy = this.pan.getTranslateY();
		}

		@Override
		public void mouseReleased(final MouseEvent arg0) {
			this.zoom(0, arg0.getPoint());
		}

		@Override
		public void mouseWheelMoved(final MouseWheelEvent mouseWheelEvent) {
			final var modifiersEx = mouseWheelEvent.getModifiersEx();
			if ((modifiersEx & InputEvent.CTRL_DOWN_MASK) == 0) {
				final var point = mouseWheelEvent.getPoint();
				this.zoom(mouseWheelEvent.getWheelRotation(), point);
			}
		}

		@Override
		public void paint(final Graphics g0) {
			this.transientShape = null;
			super.paint(g0);
			final var g = (Graphics2D) g0;
			this.insets = new Insets(Plotter6165i.this.top, Plotter6165i.this.left, Plotter6165i.this.bottom,
					Plotter6165i.this.right);
			this.painter(g, this.getWidth(), this.getHeight());
			if (this.painters != null) {
				for (final Painter1 painter : this.painters) {
					painter.apply(g, this.getWidth(), this.getHeight());
				}
			}
		}

		/**
		 * Assumes that the shape to be painted, {@link #path}, is scaled to fit in a
		 * unit box.
		 *
		 * @param g the Graphics context on which to paint.
		 * @param w the overall width of the plot, including margins and legends.
		 * @param h the overall height of the plot, including margins and legends.
		 */
		private void painter(final Graphics2D g, final int width, final int height) {
			try {

				/*
				 * Remove any existing old or extraneous components such as text fields or
				 * labels.
				 */
				for (var i = 0; i < this.getComponentCount(); ++i) {
					this.remove(i);
				}
				super.paint(g);

				/*
				 * Width and height of the available plotting area in pixels.
				 */
				final var w = width - this.insets.left - this.insets.right;
				final var h = height - this.insets.top - this.insets.bottom;

				/*
				 * Scaling factors to transform a unit square to the plot window.
				 */
				final double sx = w < 0 ? 0 : w;
				final double sy = h < 0 ? 0 : h;
				this.captionArea.setOpaque(true);
				if (Plotter6165i.this.isShowCaption()) {
					this.add(this.captionArea);
				}

				this.captionArea.setBounds(this.insets.left, height - this.insets.bottom + this.xTickHeight, w,
						this.insets.bottom - this.xTickHeight);

				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				/*
				 * Create a transform which maps a unit square to the plot window
				 */
				this.scaleInstance = AffineTransform.getScaleInstance(sx, -sy);
				this.translateInstance = AffineTransform.getTranslateInstance(this.insets.left, h + this.insets.top);
				this.transform = this.translateInstance;
				this.transform.concatenate(this.scaleInstance);
				/*
				 * "transform" now maps the unit square to the available plotting area, leaving
				 * the margins specified in "insets".
				 */

				this.boundingBox = this.transform.createTransformedShape(new Rectangle2D.Double(0, 0, 1, 1));

				g.setStroke(this.boxSelected ? this.selectedStroke : this.boxStroke);
				g.setColor(this.boxSelected ? this.selectedColor : this.majorGridColor);
				g.draw(this.boundingBox);

				/*
				 * Create a transform which maps the Shape object to the plot window
				 */
				if (this.path != null || true) {

					this.plotTransform = this.getPlotTransform(this.sx, this.sy, this.pan);
					this.plotTransform0 = this.getPlotTransform(1, 1, null);

					final var transformedXGrid = this.plotTransform.createTransformedShape(Plotter6165i.this.xGrid);
					final var transformedXGrid2 = this.plotTransform.createTransformedShape(Plotter6165i.this.xGrid2);
					final var transformedXGrid3 = this.plotTransform.createTransformedShape(Plotter6165i.this.xGrid3);
					final var transformedYGrid = this.plotTransform.createTransformedShape(Plotter6165i.this.yGrid);
					final var transformedYGrid2 = this.plotTransform.createTransformedShape(Plotter6165i.this.yGrid2);
					final var transformedYGrid3 = this.plotTransform.createTransformedShape(Plotter6165i.this.yGrid3);

					final var oldColor = g.getColor();
					g.setColor(Color.BLACK);

					final var xBounds = Plotter6165i.this.xGrid.getBounds2D();
					final var minX = xBounds.getMinX();
					final var maxX = xBounds.getMaxX();
					final var sigDigX = (int) (2.4
							+ Math.log10(Math.max(Math.abs(minX), Math.abs(maxX)) / (maxX - minX)));
					this.drawXTickLabels(g, Plotter6165i.this.xGrid2.getPathIterator(null), sigDigX);

					if (Plotter6165i.this.xTicks2 != null) {
						if (Plotter6165i.this.xTicks2.length < 2) {
							this.drawXTickLabels(g, Plotter6165i.this.xGrid3.getPathIterator(null), sigDigX);
						}
						if (Plotter6165i.this.xTicks2.length + Plotter6165i.this.xTicks3.length < 2) {
							this.drawXTickLabels(g, Plotter6165i.this.xGrid.getPathIterator(null), sigDigX);
						}
					}

					final var yBounds = Plotter6165i.this.yGrid.getBounds2D();
					final var minY = yBounds.getMinY();
					final var maxY = yBounds.getMaxY();
					final var maxAbsY = Math.max(Math.abs(minY), Math.abs(maxY));
					final var yRange = maxY - minY;
					final var sigDigY = (int) (2.4 + Math.log10(maxAbsY / yRange));// significant digits for Y tick
																					// labels
					this.drawYTickLabels(g, Plotter6165i.this.yGrid2.getPathIterator(null), sigDigY);
					if (Plotter6165i.this.yTicks2 != null) {
						if (Plotter6165i.this.yTicks2.length < 2) {
							this.drawYTickLabels(g, Plotter6165i.this.yGrid3.getPathIterator(null), sigDigY);
						}
						if (Plotter6165i.this.yTicks2.length + Plotter6165i.this.yTicks3.length < 2) {
							this.drawYTickLabels(g, Plotter6165i.this.yGrid.getPathIterator(null), sigDigY);
						}
					}

					g.setColor(oldColor);
					g.setClip(this.boundingBox);

					g.draw(transformedXGrid);

					g.draw(transformedYGrid);

					g.setStroke(this.stroke3);
					g.draw(transformedXGrid3);

					g.draw(transformedYGrid3);

//					g.setColor(g.getColor().darker());
					g.setStroke(this.stroke2);
					g.draw(transformedXGrid2);
					g.draw(transformedYGrid2);

					final Shape pSrc = Plotter6165i.this.semiLog ? (Shape) this.logPath : this.path;
					final var transformedShape = this.plotTransform.createTransformedShape(pSrc);
					/*
					 * If toggleButton 0 is selected, then plot either the main path or its
					 * logarithm, depending on the value of Plotter6165i.this.semiLog
					 */
					final var selected = this.toggleButtons.get(0).isSelected();
					if (selected) {
						g.setStroke(this.plotStroke);
						g.setColor(this.plotColor);
						if (transformedShape != null) {

							if (this.showMainPlotPath) {
								g.draw(transformedShape);
							}

							if (this.drawMarkers) {
								final var pathIterator = transformedShape.getPathIterator(null);
								final var coords = new double[2];
								var i = 0;
								final var n = this.markerColors.length;
								this.markers.clear();
								while (!pathIterator.isDone()) {
									pathIterator.currentSegment(coords);
									final var j = i++ % n;
									g.setColor(this.markerColors[j]);
									final var r = this.markerRadius /*- (0.7f * j)*/;
									final Shape marker = new Ellipse2D.Double(coords[0] - r, coords[1] - r, 2 * r,
											2 * r);
									this.markers.add(marker);
									g.fill(marker);
									if (this.markerLineColors != null && this.markerLineColors[j] != null) {
										g.setColor(this.markerLineColors[j]);
									}
									g.draw(marker);
									pathIterator.next();
								}
								if (this.selectedMarker != null) {
									final var j = this.selectedMarkerIndex % n;
									g.setColor(this.markerColors[j]);
									g.fill(this.selectedMarker);
									g.draw(this.selectedMarker);
								}
							}
						}

					}
				}

				g.setClip(this.boundingBox);
				for (var i = 0; i < this.paths.size(); ++i) {
					final var pSrc = Plotter6165i.this.semiLog ? this.semilogPaths.get(i) : this.paths.get(i);
					if (pSrc == null || this.plotTransform == null) {
						continue;
					}

					final var selected = this.toggleButtons.get(i + 1).isSelected();

					if (this.visibilities.get(i) && selected) {
						final var transformedShape = this.plotTransform.createTransformedShape(pSrc);
						g.setStroke(this.strokes.get(i));
						final var c = this.colors.get(i);
						if (c != null) {
							g.setColor(c);
							g.draw(transformedShape);
						}
						if (this.fillColors.get(i) != null) {
							g.setColor(this.fillColors.get(i));
							g.fill(transformedShape);
						}
					}
				}

				g.setClip(null);
				if (Plotter6165i.this.textItems != null) {
					final var iterator = Plotter6165i.this.textItems2.iterator();
					while (iterator.hasNext()) {
						final var next = iterator.next();
						next.plotText(g, this.plotTransform);
					}
				}
				if (Plotter6165i.this.plotTitle != null && !Plotter6165i.this.plotTitle.isEmpty()) {
					this.plotTheTitle(g, w);
				}

				for (final LegendItem li : Plotter6165i.this.legendItems) {
					final var area = new JTextArea(li.text);
					area.setEditable(false);
					if (li.font != null) {
						area.setFont(li.font);
					}
					final var fontMetrics = g.getFontMetrics(area.getFont());

					final var atop = fontMetrics.getHeight() / 2;
					final var aleft = fontMetrics.charWidth(' ');
					final var abottom = fontMetrics.getHeight() / 3;
					final var aright = aleft;
					area.setBorder(BorderFactory.createEmptyBorder(atop, aleft, abottom, aright));
					final var split = li.text.split("(?m)^");// enable multiline mode, match beginning of a line.
					var maxWidth = 0;
					var h1 = atop + abottom;
					for (final String s : split) {
						final var stringWidth = fontMetrics.stringWidth(s);
						h1 += fontMetrics.getHeight();
						if (stringWidth > maxWidth) {
							maxWidth = stringWidth;
						}
					}
					for (var i = split.length - 1; i >= 0; --i) {
						if (split[i].trim().isEmpty()) {
							h1 -= fontMetrics.getHeight();
						} else {
							break;
						}
					}
					maxWidth += aleft + aright;
					this.add(area);
					area.setOpaque(true);
					area.setBackground(Color.WHITE);
					area.setWrapStyleWord(true);
					area.setLineWrap(true);
					area.setInheritsPopupMenu(true);
//					area.setOpaque(true);
					final var textAreaLeftx = (int) (Double.isNaN(li.x) ? this.insets.left + w - maxWidth
							: this.plotTransform0.transform(new Point2D.Double(li.x, 0), null).getX());
					final var textAreaTopy = (int) (Double.isNaN(li.y) ? this.insets.top
							: this.plotTransform0.transform(new Point2D.Double(0, li.y), null).getY());
					final var textAreaWidth = (int) (Double.isNaN(li.w) ? maxWidth
							: li.w * this.plotTransform0.getScaleX());
					final var textAreaHeight = (int) (Double.isNaN(li.h) ? h1
							: -li.h * this.plotTransform0.getScaleY());
					area.setBounds(textAreaLeftx, textAreaTopy, textAreaWidth, textAreaHeight);
				}
				super.paintChildren(g);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * @param g The graphics context
		 * @param w The net width of the plot area, excluding margins.
		 */
		private void plotTheTitle(final Graphics2D g, final int w) {
			var x = 0D;
			var fitDone = false;
			Rectangle2D stringBounds = null;
			var font = Plotter6165i.this.titleFont;
			var count = 0;
			do {
				++count;
				if (!fitDone) {
					/*
					 * If title has not been fitted to the allocated space, reduce the font size by
					 * 0.9 and try again.
					 */
					final var oldSize = font.getSize2D();
					font = font.deriveFont(oldSize * 0.9f);
				}
				final var fontMetrics = g.getFontMetrics(font);
				stringBounds = fontMetrics.getStringBounds(Plotter6165i.this.plotTitle, g);
				x = w / 2 - stringBounds.getCenterX() + Plotter6165i.this.left;
				fitDone = count > 100
						|| x > 0 && x + stringBounds.getWidth() < Plotter6165i.this.left + w + Plotter6165i.this.right;
			} while (!fitDone);
			final var y = Plotter6165i.this.top / 2 - stringBounds.getCenterY();
			final var oldFont = g.getFont();
			final var oldColor = g.getColor();
			g.setFont(font);
			g.setColor(Plotter6165i.this.titleColor);
			g.drawString(Plotter6165i.this.plotTitle, (float) x, (float) y);
			g.setColor(oldColor);
			g.setFont(oldFont);
		}

		/**
		 * Reset the zoom and pan to original values.
		 *
		 */
		private void resetZoom() {

			try {
				/*
				 * Reset the scale factor s and the pan transform:
				 */
//				this.s = 1;
				this.sx = 1;
				this.sy = 1;
				this.pan.setToIdentity();

				/*
				 * The new transform.
				 */
				final var plotTransform3 = this.getPlotTransform(this.sx, this.sy, this.pan);

				/*
				 * Calculate new bounds for the data in the window, after zooming
				 */
				final Point2D upperLeftPoint = new Point2D.Double(this.insets.left, this.insets.top);
				final var dataAtUpperLeft = plotTransform3.inverseTransform(upperLeftPoint, null);
				final Point2D bottomRightPoint = new Point2D.Double(this.getWidth() - this.insets.right,
						this.getHeight() - this.insets.bottom);
				final var dataAtBottomRight = plotTransform3.inverseTransform(bottomRightPoint, null);
				this.dataBounds = new Rectangle2D.Double(dataAtUpperLeft.getX(), dataAtUpperLeft.getY(),
						dataAtBottomRight.getX() - dataAtUpperLeft.getX(),
						dataAtBottomRight.getY() - dataAtUpperLeft.getY());

				/*
				 * Create a grid to cover the zoomed data in the window
				 */
				Plotter6165i.this.createGrid(this.dataBounds, true);

			} catch (final NoninvertibleTransformException e) {
				e.printStackTrace();
			}

			this.repaint();
		}

		public void setCaptionFont(final Font font) {
			this.captionArea.setFont(font);
		}

		public void setCaptionText(final String captionText) {
			this.captionArea.setText(captionText);
		}

		/**
		 * @param drawMarkers the drawMarkers to set
		 */
		public void setDrawMarkers(final boolean drawMarkers) {
			this.drawMarkers = drawMarkers;
		}

		/**
		 * @param markerColors the markerColors to set
		 */
		public void setMarkerColors(final Color[] markerColors) {
			this.markerColors = markerColors;
		}

		/**
		 * @param markerLineColors the markerLineColors to set
		 */
		public void setMarkerLineColors(final Color[] markerLineColors) {
			this.markerLineColors = markerLineColors;
		}

		/**
		 * @param markerRadius the markerRadius to set
		 */
		public void setMarkerRadius(final double markerRadius) {
			this.markerRadius = markerRadius;
		}

		public void setPlotColor(final Color color) {
			this.plotColor = color;
		}

		/**
		 * @param stroke the plotStroke to set
		 */
		public void setPlotStroke(final Stroke stroke) {
			this.plotStroke = stroke;
		}

		/**
		 * @param xTickHeight the xTickHeight to set
		 */
		public void setxTickHeight(final int xTickHeight) {
			this.xTickHeight = xTickHeight;
		}

		/**
		 * Zooms in or out in response to mouse wheel rotation.
		 *
		 * @param wheelRotation
		 * @param point
		 */
		private void zoom(final int wheelRotation, final Point point) {

			try {
				/*
				 * Get the current plot transform:
				 */
//				final AffineTransform plotTransform1 = this.getPlotTransform(this.s, this.pan);
				final var plotTransform1 = this.getPlotTransform(this.sx, this.sy, this.pan);
				/*
				 * "plotTransform1" now maps the given shape into the plot window.
				 */

				/*
				 * The data coordinates currently under the mouse pointer
				 */
				final var dataAtMouse1 = point == null ? null : plotTransform1.inverseTransform(point, null);

				/*
				 * Update the scale factor s:
				 */
//				this.s *= Math.pow(1.1, -wheelRotation);
				if (this.zoomX) {
					this.sx *= Math.pow(1.1, -wheelRotation);
				}
				if (this.zoomY) {
					this.sy *= Math.pow(1.1, -wheelRotation);
				}

				/*
				 * Apply new zoom and old pan:
				 */
//				final AffineTransform plotTransform2 = this.getPlotTransform(this.s, this.pan);
				final var plotTransform2 = this.getPlotTransform(this.sx, this.sy, this.pan);

				/*
				 * If new scaling but old pan were applied, the data under the mouse after zoom
				 * would be this:
				 */
				final var newDataAtMouse = point == null ? null : plotTransform2.inverseTransform(point, null);

				final var noNewDataAtMouse = newDataAtMouse == null;
				/*
				 * The additional amount to translate to bring the same data coordinates under
				 * the mouse as before the zoom:
				 */
				final var noDataAtMouse1 = dataAtMouse1 == null;
				final var dx = this.zoomX
						? noNewDataAtMouse || noDataAtMouse1 ? 0 : newDataAtMouse.getX() - dataAtMouse1.getX()
						: 0;
				final var dy = this.zoomY
						? noNewDataAtMouse || noDataAtMouse1 ? 0 : newDataAtMouse.getY() - dataAtMouse1.getY()
						: 0;
				this.pan.translate(dx, dy);

				/*
				 * The new transform.
				 */

				final var plotTransform3 = this.getPlotTransform(this.sx, this.sy, this.pan);

				/*
				 * Calculate new bounds for the data in the window, after zooming
				 */
				final Point2D upperLeftPoint = new Point2D.Double(this.insets.left, this.insets.top);
				final var dataAtUpperLeft = plotTransform3.inverseTransform(upperLeftPoint, null);
				final Point2D bottomRightPoint = new Point2D.Double(this.getWidth() - this.insets.right,
						this.getHeight() - this.insets.bottom);
				final var dataAtBottomRight = plotTransform3.inverseTransform(bottomRightPoint, null);
				this.dataBounds = new Rectangle2D.Double(dataAtUpperLeft.getX(), dataAtUpperLeft.getY(),
						dataAtBottomRight.getX() - dataAtUpperLeft.getX(),
						dataAtBottomRight.getY() - dataAtUpperLeft.getY());

				/*
				 * Create a grid to cover the zoomed data in the window
				 */
				Plotter6165i.this.createGrid(this.dataBounds, true);

				if (Plotter6165i.roundrangeDebug) {
					System.out.println(String.format("81930de1 dataBounds: %s", this.dataBounds));
				}

			} catch (final NoninvertibleTransformException e) {
				e.printStackTrace();
			}

			this.repaint();
		}

		public void zoomNone() {
			this.zoomX = false;
			this.zoomY = false;
		}

		public void zoomXandY() {
			this.zoomX = this.zoomY = true;
		}

		public void zoomXOnly() {
			this.zoomX = true;
			this.zoomY = false;
		}

		public void zoomYOnly() {
			this.zoomX = false;
			this.zoomY = true;
		}

	}

	/**
	 * @author Raimo Bakis
	 *
	 */
	public class TextItem {

		private boolean above = false;
		private double angle;
		private final Color color;
		private final Font font;
		private final String text;
		private final double x;
		private final double y;

		public TextItem(final String string, final double x, final double y, final Font font, final Color color) {
			this.text = string;
			this.x = x;
			this.y = y;
			this.font = font;
			this.color = color;
		}

		public void plotText(final Graphics2D g, final AffineTransform t) {
			if (t == null) {
				return;
			}
			final var plotPoint = t.transform(new Point2D.Double(this.x, this.y), null);
			final var oldColor = g.getColor();
			g.setColor(this.color);
			final var oldFont = g.getFont();
			g.setFont(this.font);
			final var fontMetrics = g.getFontMetrics();
			final var stringBounds = fontMetrics.getStringBounds(this.text, g);
			if (this.angle != 0) {
				final var alpha = -Math.toRadians(this.angle);
				g.rotate(alpha, plotPoint.getX(), plotPoint.getY());
				g.drawString(this.text, (float) (plotPoint.getX() - stringBounds.getCenterX()),
						(float) (plotPoint.getY()
								- (this.above ? stringBounds.getMaxY() * 1.2 : stringBounds.getCenterY())));
				g.rotate(-alpha, plotPoint.getX(), plotPoint.getY());

			} else {
				g.drawString(this.text, (float) (plotPoint.getX() - stringBounds.getCenterX()),
						(float) (plotPoint.getY()
								- (this.above ? stringBounds.getMaxY() * 1.2 : stringBounds.getCenterY())));
			}

			g.setFont(oldFont);
			g.setColor(oldColor);
		}

		public void setAbove(final boolean b) {
			this.above = b;
		}

		public void setAngle(final double angle) {
			this.angle = angle;
		}

	}

	private static final DateFormat DATE_INSTANCE = DateFormat.getDateInstance(DateFormat.SHORT);

	private static final LinearToSemilogPathConverter lINEAR_TO_SEMILOG_PATH_CONVERTER = new LinearToSemilogPathConverter(
			0.5);

	public static final long MILLIS_PER_DAY = 1000 * 60 * 60 * 24;

	private static final MetalToggleButtonUI NEW_UI = new MetalToggleButtonUI() {

		@Override
		protected void paintButtonPressed(final Graphics g, final AbstractButton b) {
			final var g2 = (Graphics2D) g;
			if (b.isContentAreaFilled()) {
				final var x1 = 0F;
				final float y1 = 4 * b.getHeight() / 4;
				final Color color1 = MetalLookAndFeel.getControlHighlight();
				final var x2 = 0F;
				final var y2 = 0F;// (7 * b.getHeight()) / 4;
				final var color2 = this.getSelectColor();
				final var cyclic = true;
				final var gradientPaint = new GradientPaint(x1, y1, color1, x2, y2, color2, cyclic);
				g2.setPaint(gradientPaint);
				g2.fillRect(0, 0, b.getWidth(), b.getHeight());
			}
			if (b.isBorderPainted()) {
				g2.setStroke(new BasicStroke(1));
				final var left = 1;
				final var right = b.getWidth() - 2;
				final var bottom = b.getHeight() - 2;
				final var top = 1;
				g2.setColor(Color.black);
				g2.drawLine(left, top, right, top);// top
				g2.drawLine(left, bottom, left, top);// left
				g2.setColor(Color.white);
				g2.drawLine(left, bottom, right, bottom);// bottom
				g2.drawLine(right, bottom, right, top);// right
			}
		}

		@Override
		protected void paintFocus(final Graphics g, final AbstractButton b, final Rectangle viewRect,
				final Rectangle textRect, final Rectangle iconRect) {
			// This version does not paint a focus indicator.
		}

	};

	private static boolean roundrangeDebug = false;

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @return the dateinstance
	 */
	public static DateFormat getDateinstance() {
		return Plotter6165i.DATE_INSTANCE;
	}

	/**
	 * Return the values of the first and last major tick marks, divided by the
	 * major tick interval, and the base-10 logarithm of the size of the the major
	 * tick interval.
	 *
	 * @param x0 The data value at the start of the interval
	 * @param x1 The data value at the end of the interval
	 * @return an array of three values:
	 */
	public static int[] getRoundRange(final double x0, final double x1) {

		final var reverse = x1 < x0;

		/*
		 * The lower end of the range
		 */
		final var xa = reverse ? x1 : x0;

		/*
		 * The upper end of the range
		 */
		final var xb = reverse ? x0 : x1;

		final var interval = xb - xa;

		/*
		 * The smallest power of 10 that fits in the interval at least 10 times
		 */
		final var i = (int) Math.ceil(Math.log10(interval)) - 2;

		/*
		 * Interval between major tick marks
		 */
		final var unit2 = Math.pow(10, i);

		/*
		 * The value at the first major tick mark divided by the major tick interval.
		 */
		final var floor2 = (int) Math.floor(xa / unit2);

		/*
		 * The value at the last major tick mark divided by the major tick interval.
		 */
		final var ceil2 = (int) Math.ceil(xb / unit2);

		if (Plotter6165i.roundrangeDebug) {
			System.out.println(String.format("81930de0 x0 = %.5g, x1 = %.5g, floor2 = %,d, ceil2 = %,d, i = %,d", x0,
					x1, floor2, ceil2, i));
		}

		/*
		 * Return the values of the first and last major tick marks, divided by the
		 * major tick interval, and the base-10 logarithm of the size of the the major
		 * tick interval.
		 */
		return new int[] { floor2, ceil2, i };
	}

	/**
	 * @return The current timestamp in format yyymmddHHMMSSLLL
	 */
	public static String getTimeStamp() {
		return String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL", System.currentTimeMillis());
	}

	/**
	 * @return the roundrangeDebug
	 */
	public static boolean isRoundrangeDebug() {
		return Plotter6165i.roundrangeDebug;
	}

	/**
	 * @param roundrangeDebug the roundrangeDebug to set
	 */
	public static void setRoundrangeDebug(final boolean roundrangeDebug) {
		Plotter6165i.roundrangeDebug = roundrangeDebug;
	}

	/**
	 * inset of plot window from the bottom edge of the pane
	 */
	private int bottom = 40;

	private final JPanel buttonPane = new JPanel();

	private final Font contorlPanelFont1 = new Font("Dialog", Font.BOLD, 24);

	private final ControlPanel controlPanel = new ControlPanel("Manage Plots");

	private final Insets insets = new Insets(this.top, this.left, this.bottom, this.getRight());

	/**
	 * inset of plot window from the left edge of the pane
	 */
	private int left = 60;

	private final List<LegendItem> legendItems = new ArrayList<>();

	private final JMenuItem menuItemZoomOff = new JMenuItem("Zoom OFF ✓");

	private final JMenuItem menuItemZoomXandY = new JMenuItem("Zoom X and Y");

	private final JMenuItem menuItemZoomXOnly = new JMenuItem("Zoom X only");

	private final JMenuItem menuItemZoomYOnly = new JMenuItem("Zoom Y only");

	private Rectangle2D pathBounds = null;

	private AffineTransform pathToUnitBoxTransform;

	private final PlotterPane plotterPane = new PlotterPane();

	/**
	 * The default size of the plotter pane.
	 */
	private Dimension plotterPaneDefaultDimension = new Dimension(865, 600);

	private final JPopupMenu plotterPopupMenu = new JPopupMenu();

	public String plotTitle = null;

	/**
	 * inset of plot window from the right edge of the pane
	 */
	private int right = 20;

	private boolean semiLog = false;

	private boolean showCaption = true;

	private final List<TextItem> textItems = new ArrayList<>();

	List<TextItem> textItems2 = Collections.synchronizedList(this.textItems);

	public List<String> tips = new ArrayList<>();

	private final Color titleColor = Color.BLACK;

	private final Font titleFont = new Font(Font.SERIF, Font.BOLD, 24);

	/**
	 * inset of plot window from the top edge of the pane
	 */
	private int top = 40;

	private long xDateOrigin = Long.MIN_VALUE;

	private final Path2D xGrid = new Path2D.Double();

	private final Path2D xGrid2 = new Path2D.Double();

	private final Path2D xGrid3 = new Path2D.Double();

	private double[] xTicks;

	private double[] xTicks2;

	private double[] xTicks3;

	private final Path2D yGrid = new Path2D.Double();

	private final Path2D yGrid2 = new Path2D.Double();

	private final Path2D yGrid3 = new Path2D.Double();

	private double[] yTicks;

	private double[] yTicks2;

	private double[] yTicks3;

	/**
	 * Default constructor
	 */
	public Plotter6165i() {
		super();
		this.init();
	}

	/**
	 * @param arg0
	 */
	public Plotter6165i(final String arg0) {
		super(arg0);
		this.init();
	}

	public void addLegend(final String text, final double x, final double y, final double w, final double h) {
		this.legendItems.add(new LegendItem(text, this.plotterPane.getFont(), x, y, w, h));
	}

	public LegendItem addLegend(final String text, final double x, final double y, final double w, final double h,
			final Font font) {
		final var legendItem = new LegendItem(text, font, x, y, w, h);
		this.legendItems.add(legendItem);
		return legendItem;
	}

	public void addPainter(final Painter1 painter) {
		this.plotterPane.painters.add(painter);
	}

	public void addPlotPath(final Color color, final Shape path, final Stroke stroke) {
		this.addPlotPath(stroke, path, color);
	}

	public void addPlotPath(final Color color, final Stroke stroke, final Shape path) {
		this.addPlotPath(stroke, path, color);
	}

	public void addPlotPath(final Shape path, final Color color, final Stroke stroke) {
		this.addPlotPath(stroke, path, color);
	}

	public void addPlotPath(final Shape path, final Stroke stroke, final Color color) {
		this.addPlotPath(stroke, path, color);
	}

	public void addPlotPath(final Stroke stroke, final Color color, final Shape path) {
		this.addPlotPath(stroke, path, color);
	}

	public void addPlotPath(final Stroke stroke, final Shape path, final Color color) {
		this.addPlotPath(stroke, path, color, null);
	}

	public void addPlotPath(final Stroke stroke, final Shape path, final Color color, final Color fillColor) {
		if (this.plotterPane.path == null) {
			this.setMainPlotPath(path);
			this.plotterPane.plotColor = color;
			this.plotterPane.plotStroke = stroke;
		} else {
			++this.controlPanel.counter;
			final var newButton = new JToggleButton(String.format("%d", this.controlPanel.counter), true);
			newButton.setFont(this.contorlPanelFont1);
			newButton.addActionListener(e -> Plotter6165i.this.plotterPane.repaint());
			newButton.setUI(Plotter6165i.NEW_UI);
			newButton.setBorderPainted(true);
			final var toggleButtonPopup = new JPopupMenu();
			final var zoomItem = new JMenuItem("zoom to fullscale");
			zoomItem.addActionListener(e -> Plotter6165i.this.zoomFullscale(path));
			toggleButtonPopup.add(zoomItem);
			newButton.setComponentPopupMenu(toggleButtonPopup);
			this.plotterPane.toggleButtons.add(newButton);
			this.buttonPane.add(newButton);
			this.controlPanel.pack();
			this.plotterPane.paths.add(path);
			this.plotterPane.semilogPaths.add(this.getSemiLogPath(path));
			this.plotterPane.strokes.add(stroke);
			this.plotterPane.colors.add(color);
			this.plotterPane.visibilities.add(true);
			this.plotterPane.fillColors.add(fillColor);
		}
	}

	public TextItem addText(final String string, final double x, final double y, final Font font, final Color color) {
		final var textItem = new TextItem(string, x, y, font, color);
		this.textItems2.add(textItem);
		return textItem;
	}

	public TextItem addText(final String string, final double x, final double y, final Font font, final Color color,
			final double angle) {
		final var textItem = new TextItem(string, x, y, font, color);
		textItem.setAngle(angle);
		this.textItems2.add(textItem);
		return textItem;
	}

	public TextItem addTextAbove(final String string, final double x, final double y, final Font font,
			final Color color) {
		final var textItem = new TextItem(string, x, y, font, color);
		textItem.setAbove(true);
		this.textItems2.add(textItem);
		return textItem;
	}

	/**
	 * Concatenate the <code>textLines</code> to one string with line separators.
	 * Then invoke {@link #addLegend(String, double, double, double, double, Font)}.
	 * The {@link #plotterPane} painter method then creates a text area for each
	 * legend item, and sets its text to the text lines.
	 *
	 * @param textLines The lines of text to be displayed
	 * @param font      The font for the lines of text
	 * @param x         The
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public LegendItem addTextBox(final List<String> textLines, final Font font, final double x, final double y,
			final double w, final double h) {
		final var textB = new StringBuilder();
		final var lineSeparator = System.lineSeparator();
		final var textLinesIterator = textLines.iterator();
		if (textLinesIterator.hasNext()) {
			textB.append(textLinesIterator.next());
		}
		while (textLinesIterator.hasNext()) {
			textB.append(lineSeparator);
			textB.append(textLinesIterator.next());
		}
		return this.addLegend(textB.toString(), x, y, w, h, font);
	}

	public void addTitle(final String string) {
		this.plotTitle = string;
	}

	protected void copyImageToFile() throws IOException {
		final var imageWidth = this.plotterPane.getWidth();
		final var imageHeight = this.plotterPane.getHeight();
		final var bi = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		this.plotterPane.painter((Graphics2D) bi.getGraphics(), bi.getWidth(), bi.getHeight());
		if (this.plotterPane.painters != null) {
			for (final Painter1 painter : this.plotterPane.painters) {
				painter.apply((Graphics2D) bi.getGraphics(), bi.getWidth(), bi.getHeight());
			}
		}
		final var outFileName = Plotter6165i.getTimeStamp() + Plotter6165i.this.getTitle() + ".png";

		final var outFileName2 = (String) JOptionPane.showInternalInputDialog(this.plotterPane,
				"Edit the filename for this plot:", "", JOptionPane.QUESTION_MESSAGE, null, null, outFileName);
		if (outFileName2 == null) {
			return;
		}
		final var outDir = new File("data", "out");
		outDir.mkdirs();
		final var outFile = new File(outDir,
				outFileName2 == null || outFileName2.isBlank() ? outFileName : outFileName2);
		ImageIO.write(bi, "png", outFile);
	}

	private void createGrid(final Rectangle2D gridBounds, final boolean zooming) {
		if (Plotter6165i.roundrangeDebug) {// if debugging, print message with unique id "81930de4"
			System.out.println(String.format("81930de4 gridBounds: %s", gridBounds.toString()));
		}
		try {

			/*
			 * Each RoundRange array contains three elements. The last is log10 of the
			 * interval between consecutive tick marks, for example -1 if the tick marks are
			 * 0.1 units apart. The others are the beginning and end of the range in terms
			 * of multiples of that interval.
			 */
			final var xRoundRange = Plotter6165i.getRoundRange(gridBounds.getX(),
					gridBounds.getX() + gridBounds.getWidth());
			final var yRoundRange = Plotter6165i.getRoundRange(gridBounds.getY(),
					gridBounds.getY() + gridBounds.getHeight());
			final var xUnit = Math.pow(10., xRoundRange[2]);
			final var yUnit = Math.pow(10., yRoundRange[2]);

			final var x = xRoundRange[0] * xUnit;
			final var y = yRoundRange[0] * yUnit;
			final var w = xRoundRange[1] * xUnit - x;
			final var h = yRoundRange[1] * yUnit - y;
			if (Plotter6165i.roundrangeDebug) {
				System.out.println(String.format("81930de2 x = %.5g, y = %.5g, w = %.5g, h = %.5g", x, y, w, h));
			}

			final Rectangle2D roundBounds = new Rectangle2D.Double(x, y, w, h);
			if (Plotter6165i.roundrangeDebug) {
				System.out.println(String.format("81930de4 roundBounds = %s", roundBounds.toString()));
			}

			if (!zooming || this.pathToUnitBoxTransform == null) {
				this.pathToUnitBoxTransform = AffineTransform.getScaleInstance(1. / roundBounds.getWidth(),
						1. / roundBounds.getHeight());
				this.pathToUnitBoxTransform
						.concatenate(AffineTransform.getTranslateInstance(-roundBounds.getX(), -roundBounds.getY()));
			}
			if (Plotter6165i.roundrangeDebug) {
				System.out.println(
						String.format("81930de5 pathToUnitBoxTransform = %s", this.pathToUnitBoxTransform.toString()));
			}

			final var xTickcount = (int) (1.5 + xRoundRange[1] - xRoundRange[0]);
			this.xTicks = new double[xTickcount];
			var xTick2count = 0;
			var xTick3count = 0;
			for (var i = 0; i < this.xTicks.length; ++i) {
				final var j = xRoundRange[0] + i;
				if (j % 5 == 0) {
					if (j % 10 == 0) {
						++xTick2count;
					} else {
						++xTick3count;
					}
				}
				this.xTicks[i] = j * xUnit;
			}

			this.xTicks2 = new double[xTick2count];
			this.xTicks3 = new double[xTick3count];
			var k2 = 0;
			var k3 = 0;
			for (var i = 0; i < this.xTicks.length; ++i) {
				final var j = xRoundRange[0] + i;
				if (j % 5 == 0) {
					if (j % 10 == 0) {
						this.xTicks2[k2++] = j * xUnit;
					} else {
						this.xTicks3[k3++] = j * xUnit;
					}
				}
			}

			final var yTickcount = (int) (1.5 + yRoundRange[1] - yRoundRange[0]);
			this.yTicks = new double[yTickcount];
			var yTick2count = 0;
			var yTick3count = 0;
			for (var i = 0; i < this.yTicks.length; ++i) {
				final var j = yRoundRange[0] + i;
				if (j % 5 == 0) {
					if (j % 10 == 0) {
						++yTick2count;
					} else {
						++yTick3count;
					}
				}
				if (this.semiLog) {
					final var j2 = j % 10;
					final var jj = j + (j2 == 0 ? 0 : 10 * Math.log10(j2) - j2);
					this.yTicks[i] = jj * yUnit;
				} else {
					this.yTicks[i] = j * yUnit;
				}
			}

			this.yTicks2 = new double[yTick2count];
			this.yTicks3 = new double[yTick3count];
			k2 = 0;
			k3 = 0;
			for (var i = 0; i < this.yTicks.length; ++i) {
				final var j = yRoundRange[0] + i;
				if (j % 5 == 0) {
					if (j % 10 == 0) {
						this.yTicks2[k2++] = j * yUnit;
					} else {
						if (this.semiLog) {
							final var jj = j + (10 * Math.log10(5) - 5);
							this.yTicks3[k3] = jj * yUnit;
						} else {
							this.yTicks3[k3] = j * yUnit;
						}
						k3++;
					}
				}
			}

			this.xGrid.reset();
			this.xGrid2.reset();
			this.xGrid3.reset();
			final var yFactor = Math.pow(10, yRoundRange[2]);
			if (Plotter6165i.roundrangeDebug) {
				System.out.println(String.format("81930de6 yFactor = %.5g", yFactor));
			}
			final var y0 = yRoundRange[0] * yFactor;
			final var y1 = yRoundRange[1] * yFactor;

			if (Plotter6165i.roundrangeDebug) {
				System.out.println(String.format("81930de3 yRoundRange[0] = %,d, yRoundRange[1] = %,d", yRoundRange[0],
						yRoundRange[1]));
			}

//			for (final double xTick : this.xTicks) {
//				this.xGrid.moveTo(xTick, y0 / 10);
//				this.xGrid.lineTo(xTick, y1 / 10);
//			}
//			for (final double xTick : this.xTicks2) {
//				this.xGrid2.moveTo(xTick, y0 / 10);
//				this.xGrid2.lineTo(xTick, y1 / 10);
//			}
//			for (final double xTick : this.xTicks3) {
//				this.xGrid3.moveTo(xTick, y0 / 10);
//				this.xGrid3.lineTo(xTick, y1 / 10);
//			}

			for (final double xTick : this.xTicks) {
				this.xGrid.moveTo(xTick, y0);
				this.xGrid.lineTo(xTick, y1);
			}
			for (final double xTick : this.xTicks2) {
				this.xGrid2.moveTo(xTick, y0);
				this.xGrid2.lineTo(xTick, y1);
			}
			for (final double xTick : this.xTicks3) {
				this.xGrid3.moveTo(xTick, y0);
				this.xGrid3.lineTo(xTick, y1);
			}

			this.yGrid.reset();
			this.yGrid2.reset();
			this.yGrid3.reset();
			final var x0 = this.xTicks[0];
			final var x1 = this.xTicks[this.xTicks.length - 1];
			for (final double yTick : this.yTicks) {
				this.yGrid.moveTo(x0, yTick);
				this.yGrid.lineTo(x1, yTick);
			}
			for (final double yTick : this.yTicks2) {
				this.yGrid2.moveTo(x0, yTick);
				this.yGrid2.lineTo(x1, yTick);
			}
			for (final double yTick : this.yTicks3) {
				this.yGrid3.moveTo(x0, yTick);
				this.yGrid3.lineTo(x1, yTick);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the bottom
	 */
	public int getBottom() {
		return this.bottom;
	}

	/**
	 * @return the left
	 */
	public int getLeft() {
		return this.left;
	}

	public Shape getMainPlotPath() {
		return this.plotterPane.path;
	}

	/**
	 * @return the x value at right edge of the plot area.
	 */
	public double getMaxX() {

		return this.pathBounds == null ? Double.NaN : this.pathBounds.getMaxX();
	}

	/**
	 * @return the x value at left edge of the plot area.
	 */
	public double getMaxY() {

		return this.pathBounds == null ? Double.NaN : this.pathBounds.getMaxY();
	}

	/**
	 * @return the x value at left edge of the plot area.
	 */
	public double getMinX() {

		return this.pathBounds == null ? Double.NaN : this.pathBounds.getMinX();
	}

	/**
	 * @return the x value at bpttom of the plot area.
	 */
	public double getMinY() {

		return this.pathBounds == null ? Double.NaN : this.pathBounds.getMinY();
	}

	/**
	 * @return the pathBounds
	 */
	public Rectangle2D getPathBounds() {
		return this.pathBounds;
	}

	/**
	 * @return the plotterPane
	 */
	public PlotterPane getPlotterPane() {
		return this.plotterPane;
	}

	/**
	 * @return the plotterPaneDefaultDimension
	 */
	public Dimension getPlotterPaneDefaultDimension() {
		return this.plotterPaneDefaultDimension;
	}

	/**
	 * @return the plotterPopupMenu
	 */
	public JPopupMenu getPlotterPopupMenu() {
		return this.plotterPopupMenu;
	}

	/**
	 * @return the right
	 */
	public int getRight() {
		return this.right;
	}

	/**
	 * @param path
	 * @return
	 */
	private Shape getSemiLogPath(final Shape path) {
		if (path == null) {
			return null;
		}
		return path instanceof java.awt.geom.Path2D.Double
				? Plotter6165i.lINEAR_TO_SEMILOG_PATH_CONVERTER.apply((java.awt.geom.Path2D.Double) path)
				: path;
	}

	/**
	 * @return the top
	 */
	public int getTop() {
		return this.top;
	}

	public long getXDateOrigin() {
		return this.xDateOrigin;
	}

	/**
	 * @return the xTickHeight
	 */
	public int getxTickHeight() {
		return this.plotterPane.xTickHeight;
	}

	/**
	 * @param desktop
	 */
	private void init() {
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setResizable(true);
		this.setContentPane(this.plotterPane);
		this.plotterPane.setPreferredSize(this.plotterPaneDefaultDimension);
		this.plotterPane.setComponentPopupMenu(this.plotterPopupMenu);

		final var toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.setInitialDelay(1);
		toolTipManager.setDismissDelay(10000);

		final var menuItemSave = new JMenuItem("Save image to file");
		menuItemSave.addActionListener(arg0 -> {
			try {
				Plotter6165i.this.copyImageToFile();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		});
		this.plotterPopupMenu.add(menuItemSave);

		final JMenuItem menuItemToggleLog = new JMenuItem("Toggle semiLog");
		menuItemToggleLog.addActionListener(e -> {
			Plotter6165i.this.semiLog = !Plotter6165i.this.semiLog;
			this.pathBounds = this.plotterPane.logPath.getBounds2D();
			Plotter6165i.this.zoomFullscale(this.semiLog ? this.plotterPane.logPath : this.plotterPane.path);
			this.createGrid(this.plotterPane.dataBounds == null ? this.pathBounds : this.plotterPane.dataBounds, false);

			Plotter6165i.this.repaint();
		});

		this.plotterPopupMenu.add(menuItemToggleLog);

		this.buttonPane.setBackground(Color.getHSBColor(.436f, .35f, .7f));
		final var label = new JLabel("Component Visibilities");
		label.setFont(this.contorlPanelFont1);
		label.setAlignmentX(.5f);
		label.setForeground(new Color(0f, 0f, 0f, .333f));
		this.buttonPane.add(label);
		final var menuItemManagePlots = new JMenuItem("Plot Manager");
		menuItemManagePlots.addActionListener(e -> {

			if (this.controlPanel.getDesktopPane() == null) {
				/*
				 * If the control panel has not yet been placed on a desktop:
				 */
				final var desktopPane2 = Plotter6165i.this.getDesktopPane();
				if (desktopPane2 != null) {
					desktopPane2.add(this.controlPanel);
//					this.controlPanel.getContentPane().setLayout(new FlowLayout());
					this.controlPanel.setVisible(false);
				} else {
				}
			}

			final var desktopPane = this.controlPanel.getDesktopPane();
			if (desktopPane != null) {

				final var scrollPane = new JScrollPane(this.buttonPane);
				this.controlPanel.setContentPane(scrollPane);
				this.buttonPane.setLayout(new FlowLayout());
				this.controlPanel.setLocation(this.getLocation());
				this.controlPanel.setClosable(true);
				this.controlPanel.setPreferredSize(new Dimension(300, 400));
				this.controlPanel.pack();
				final var verticalScrollBar = scrollPane.getVerticalScrollBar();
				this.buttonPane.setPreferredSize(
						new Dimension(scrollPane.getSize().width - verticalScrollBar.getWidth(), 800));
				this.controlPanel.setTitle(this.getTitle());
				this.controlPanel.pack();
				final var aFlag = true;
				this.controlPanel.setVisible(aFlag);
				if (aFlag) {
					this.controlPanel.moveToFront();
				}
				desktopPane.doLayout();
				this.controlPanel.repaint();
				desktopPane.repaint();
			}
		});
		this.plotterPopupMenu.add(menuItemManagePlots);

		this.menuItemZoomXOnly.addActionListener(arg0 -> {
			this.menuItemZoomXOnly.setText("Zoom X only ✓");
			this.menuItemZoomXandY.setText("Zoom X and Y");
			this.menuItemZoomYOnly.setText("Zoom Y only");
			this.menuItemZoomOff.setText("Zoom OFF");
			this.plotterPane.zoomXOnly();
		});
		this.plotterPopupMenu.add(this.menuItemZoomXOnly);

		this.menuItemZoomYOnly.addActionListener(arg0 -> {
			this.menuItemZoomXOnly.setText("Zoom X only");
			this.menuItemZoomXandY.setText("Zoom X and Y");
			this.menuItemZoomYOnly.setText("Zoom Y only ✓");
			this.menuItemZoomOff.setText("Zoom OFF");
			this.plotterPane.zoomYOnly();
		});
		this.plotterPopupMenu.add(this.menuItemZoomYOnly);

		this.menuItemZoomXandY.addActionListener(arg0 -> {
			this.menuItemZoomXOnly.setText("Zoom X only");
			this.menuItemZoomXandY.setText("Zoom X and Y ✓");
			this.menuItemZoomYOnly.setText("Zoom Y only");
			this.menuItemZoomOff.setText("Zoom OFF");
			this.plotterPane.zoomXandY();
		});
		this.plotterPopupMenu.add(this.menuItemZoomXandY);

		this.menuItemZoomOff.addActionListener(arg0 -> {
			this.menuItemZoomXOnly.setText("Zoom X only");
			this.menuItemZoomXandY.setText("Zoom X and Y");
			this.menuItemZoomYOnly.setText("Zoom Y only");
			this.menuItemZoomOff.setText("Zoom OFF ✓");
			this.plotterPane.zoomNone();
		});
		this.plotterPopupMenu.add(this.menuItemZoomOff);

		final var menuItemReset = new JMenuItem("Reset zoom and pan");
		menuItemReset.addActionListener(arg0 -> {
			try {
				this.plotterPane.resetZoom();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		});
		this.plotterPopupMenu.add(menuItemReset);

		this.plotterPopupMenu.addSeparator();
		final var menuItemQuit = new JMenuItem("QUIT");
		menuItemQuit.addActionListener(arg0 -> Plotter6165i.this.shutdown());
		this.plotterPopupMenu.add(menuItemQuit);

		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(final ComponentEvent e) {
			}

			@Override
			public void componentMoved(final ComponentEvent e) {
			}

			/**
			 * Assume that when the user resizes the component, the new size is then also
			 * the new preferred size. When components are re-arranged by the
			 * <code>arrangeWindows()</code> method, they retain their sizes.
			 */
			@Override
			public void componentResized(final ComponentEvent e) {
				final var component = e.getComponent();
				component.setPreferredSize(component.getSize());
			}

			@Override
			public void componentShown(final ComponentEvent e) {
			}
		});
	}

	public boolean isMainPlotPathVisible() {
		final var result = this.getPlotterPane().showMainPlotPath;
		return result;
	}

	public boolean isPlotPathVisible(final Shape path) {
		final var indexOf = this.getPlotterPane().paths.indexOf(path);
		if (indexOf < 0) {
			return false;
		} else {
			final boolean was = this.getPlotterPane().visibilities.get(indexOf);
			return was;
		}
	}

	/**
	 * @return the semiLog
	 */
	public boolean isSemiLog() {
		return this.semiLog;
	}

	/**
	 * @return the showCaption
	 */
	public boolean isShowCaption() {
		return this.showCaption;
	}

	public Painter1 removePainter(final Painter1 painter) {
		if (this.plotterPane.painters == null) {
			return null;
		}
		final var result = painter;
		final var remove = this.plotterPane.painters.remove(painter);
		return remove ? result : null;
	}

	public Shape removePlotPath(final java.awt.geom.Path2D.Double path) {
		Shape oldPath = null;
		if (this.plotterPane.paths != null) {
			final var indexOf = this.plotterPane.paths.indexOf(path);
			if (indexOf >= 0) {
				oldPath = this.plotterPane.paths.remove(indexOf);
				this.plotterPane.strokes.remove(indexOf);
				this.plotterPane.colors.remove(indexOf);
				this.plotterPane.fillColors.remove(indexOf);
				this.controlPanel.remove(this.plotterPane.toggleButtons.get(indexOf));
				this.plotterPane.toggleButtons.remove(indexOf);
			}
			this.controlPanel.repaint();
		}

		return oldPath;
	}

	public void removeText(final TextItem textItem) {
		this.textItems2.remove(textItem);
	}

	public void removeTextBox(final LegendItem textBox) {
		if (textBox != null) {
			this.legendItems.remove(textBox);
		}
	}

	/**
	 * @param bottom the bottom to set
	 */
	public void setBottom(final int bottom) {
		this.bottom = bottom;
	}

	public void setCaptionFont(final Font font) {
		this.plotterPane.setCaptionFont(font);
	}

	public void setCaptionText(final String captionText) {
		this.plotterPane.setCaptionText(captionText);
	}

	public void setDrawMarkers(final boolean b) {
		this.plotterPane.setDrawMarkers(b);
	}

	/**
	 * @param left the left to set
	 */
	public void setLeft(final int left) {
		this.left = left;
	}

	public void setMainPlotPath(final Shape path) {
		this.plotterPane.path = path;
		this.plotterPane.logPath = (java.awt.geom.Path2D.Double) this.getSemiLogPath(path);
		if (this.pathBounds == null || true) {
			this.pathBounds = this.plotterPane.path.getBounds2D();
			this.plotterPane.dataBounds = (java.awt.geom.Rectangle2D.Double) this.pathBounds;
			this.createGrid(this.plotterPane.dataBounds == null ? this.pathBounds : this.plotterPane.dataBounds, false);
		} else {
			this.createGrid(this.plotterPane.dataBounds == null ? this.pathBounds : this.plotterPane.dataBounds, true);
		}

		final var newButton = new JToggleButton(String.format("%d", 0), true);
		newButton.setFont(this.contorlPanelFont1);
		newButton.addActionListener(e -> Plotter6165i.this.plotterPane.repaint());
		newButton.setUI(Plotter6165i.NEW_UI);
		newButton.setBorderPainted(true);
		this.plotterPane.toggleButtons.add(newButton);
		this.buttonPane.add(newButton);

		final var toggleButtonPopup = new JPopupMenu();
		final var zoomItem = new JMenuItem("zoom to fullscale");
		zoomItem.addActionListener(
				e -> Plotter6165i.this.zoomFullscale(this.semiLog ? this.plotterPane.logPath : this.plotterPane.path));
		toggleButtonPopup.add(zoomItem);
		newButton.setComponentPopupMenu(toggleButtonPopup);

		this.controlPanel.pack();

	}

	public boolean setMainPlotPathVisible(final boolean show) {
		final var result = this.getPlotterPane().showMainPlotPath;
		this.getPlotterPane().showMainPlotPath = show;
		return result;
	}

	/**
	 * @param markerColors the markerColors to set
	 */
	public void setMarkerColors(final Color[] markerColors) {
		this.getPlotterPane().setMarkerColors(markerColors);
	}

	/**
	 * @param markerLineColors the markerLineColors to set
	 */
	public void setMarkerLineColors(final Color[] markerLineColors) {
		this.getPlotterPane().markerLineColors = markerLineColors;
	}

	/**
	 * @param markerRadius the markerRadius to set
	 */
	public void setMarkerRadius(final double markerRadius) {
		this.getPlotterPane().markerRadius = markerRadius;
	}

	/**
	 * @param pathBounds the pathBounds to set
	 */
	public void setPathBounds(final Rectangle2D pathBounds) {
		this.pathBounds = pathBounds;
		this.plotterPane.dataBounds = (Rectangle2D.Double) pathBounds;
		this.createGrid(this.plotterPane.dataBounds == null ? this.pathBounds : this.plotterPane.dataBounds, true);
	}

	public boolean setPlotPathVisible(final Shape path, final boolean b) {
		final var indexOf = this.getPlotterPane().paths.indexOf(path);
		if (indexOf < 0) {
			return false;
		} else {
			final boolean was = this.getPlotterPane().visibilities.get(indexOf);
			this.getPlotterPane().visibilities.set(indexOf, b);
			return was;
		}
	}

	/**
	 * @param plotterPaneDimension
	 *
	 *                             the plotterPaneDimension to set
	 */
	public void setPlotterPaneDimension(final Dimension plotterPaneDimension) {
		this.plotterPaneDefaultDimension = plotterPaneDimension;
	}

	public void setPlotTitle(final String string) {
		this.plotTitle = string;
	}

	public void setPlotWindowDimension(final Dimension plotWindowDimension) {
		final var plotterPane = this.getPlotterPane();
		final var insets = plotterPane.getInsets();
		final var preferredDimension = new Dimension(plotWindowDimension.width + insets.left + insets.right,
				plotWindowDimension.height + insets.bottom + insets.top);
		plotterPane.setPreferredSize(preferredDimension);
	}

	/**
	 * @param right the right to set
	 */
	public void setRight(final int right) {
		this.right = right;
	}

	public void setSemiLog(final boolean b) {
		this.semiLog = b;
	}

	/**
	 * @param showCaption the showCaption to set
	 */
	public void setShowCaption(final boolean showCaption) {
		this.showCaption = showCaption;
	}

	/**
	 * @param top the top to set
	 */
	public void setTop(final int top) {
		this.top = top;
	}

	public void setXDateOrigin(final long originMillis) {
		this.xDateOrigin = originMillis;
	}

	/**
	 * @param xTickHeight
	 */
	public void setxTickHeight(final int xTickHeight) {
		this.plotterPane.setxTickHeight(xTickHeight);
	}

	protected void shutdown() {
		this.controlPanel.dispose();
		this.dispose();
	}

	protected void zoomFullscale(final Shape path) {
		final Rectangle2D bounds2d = path.getBounds2D();
		this.pathToUnitBoxTransform = AffineTransform.getScaleInstance(1. / bounds2d.getWidth(),
				1. / bounds2d.getHeight());
		this.pathToUnitBoxTransform
				.concatenate(AffineTransform.getTranslateInstance(-bounds2d.getX(), -bounds2d.getY()));
		this.plotterPane.resetZoom();
		this.repaint();
	}

}
