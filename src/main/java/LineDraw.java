import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class LineDraw {
	JPanel paintArea = null;

    public static void main(String[] args) {
        new LineDraw();
    }

    public LineDraw() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new FlowLayout());
            	
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }
                
                paintArea = new TestPane();
                paintArea.setBackground(Color.WHITE);
                
                JButton genImage = new JButton("Create Image");
                genImage.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						takeSnapShot(paintArea);
					}
				});
                
                JButton erImage = new JButton("Erase Image");
                erImage.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						((TestPane) paintArea).resetPoints();
						((TestPane)paintArea).paintComponent(paintArea.getGraphics());
					}
				});

                frame.add(paintArea);
                frame.add(genImage);
                frame.add(erImage);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {

		private static final long serialVersionUID = 1L;
		private List<List<Point>> points;
		
		public void resetPoints() {
			points = new ArrayList<>(25);
			points.add(new ArrayList<>());
		}

        public TestPane() {
            points = new ArrayList<>(25);
            MouseAdapter ma = new MouseAdapter() {

                private List<Point> currentPath;

                @Override
                public void mousePressed(MouseEvent e) {
                    currentPath = new ArrayList<>(25);
                    currentPath.add(e.getPoint());

                    points.add(currentPath);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    Point dragPoint = e.getPoint();
                    currentPath.add(dragPoint);
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    currentPath = null;
                }
            };

            addMouseListener(ma);
            addMouseMotionListener(ma);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(300, 300);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            for (List<Point> path : points) {
                Point from = null;
                for (Point p : path) {
                    if (from != null) {
                    	g2d.setStroke(new BasicStroke(20));
                        g2d.drawLine(from.x, from.y, p.x, p.y);
                    }
                    from = p;
                }
            }
            g2d.dispose();
        }
    }
    
    void takeSnapShot(JPanel panel){
        BufferedImage bufImage = new BufferedImage(panel.getSize().width, panel.getSize().height,BufferedImage.TYPE_INT_RGB);
        panel.paint(bufImage.createGraphics());
        File imageFile = new File("." + File.separator + "img/screenshots.jpeg");
	    try{
	    	 Image tmp = bufImage.getScaledInstance(28, 28, Image.SCALE_SMOOTH);
	    	 BufferedImage resizedBufImg = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
	    	 Graphics2D graphics2d = resizedBufImg.createGraphics();
	    	 graphics2d.drawImage(tmp, 0, 0, null);
	    	 graphics2d.dispose();
	    	 
	         imageFile.createNewFile();
	         ImageIO.write(resizedBufImg, "jpeg", imageFile);
	    } catch(Exception ex){ }
     }

}