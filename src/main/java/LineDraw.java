import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

public class LineDraw {
	private JPanel paintArea = null;
	private static NeuralNetwork network = null;
	private static MultiLayerNetwork layerNetwork = null;
	
    public static void main(String[] args) throws IOException {
        network = new NeuralNetwork();
        layerNetwork = network.init();
        
        network.trainNetwork(layerNetwork);
        
        new LineDraw();
    }

    public LineDraw() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                //set layout = main frame 1 row 2 columns
                int rows = 1, columns = 2;
                frame.setLayout(new GridLayout(rows, columns));
                
                JPanel[][] panelHolder = new JPanel[rows][columns];
                for(int i = 0; i < rows; i++) {
                    for(int j = 0; j < columns; j++) {
                        panelHolder[i][j] = new JPanel();
                        frame.add(panelHolder[i][j]);
                    }
                }
            	
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }
                
                paintArea = new TestPane();
                paintArea.setBackground(Color.WHITE);
                panelHolder[0][0].add(paintArea);
                
                //set layout = right frame 1 row 2 columns
                int panelRows = 2, panelColumns = 1;
                panelHolder[0][1].setLayout(new GridLayout(panelRows, panelColumns));
                
                JPanel[][] innerPanelHolder = new JPanel[panelRows][panelColumns];
                for(int i = 0; i < panelRows; i++) {
                    for(int j = 0; j < panelColumns; j++) {
                        innerPanelHolder[i][j] = new JPanel();
                        panelHolder[0][1].add(innerPanelHolder[i][j]);
                    }
                }
                
                // Display number with their progress
                innerPanelHolder[1][0].setLayout(new GridLayout(10, 1)); 
                
                JPanel[][] nestedInnerPanelHolder = new JPanel[10][1];
                for(int i = 0; i < 10; i++) {
                    for(int j = 0; j < 1; j++) {
                        nestedInnerPanelHolder[i][j] = new JPanel();
                        innerPanelHolder[1][0].add(nestedInnerPanelHolder[i][j]);
                    }
                }
                
                // Adds number with their progress bars
                JLabel lab0 = new JLabel("0", JLabel.LEFT);
                JProgressBar lab0Progress = new JProgressBar();
                lab0Progress.setValue(0);
                lab0Progress.setStringPainted(true); 
                nestedInnerPanelHolder[0][0].add(lab0);
                nestedInnerPanelHolder[0][0].add(lab0Progress);
                
                JLabel lab1 = new JLabel("1", JLabel.LEFT);
                JProgressBar lab1Progress = new JProgressBar();
                lab1Progress.setValue(0);
                lab1Progress.setStringPainted(true);
                nestedInnerPanelHolder[1][0].add(lab1);
                nestedInnerPanelHolder[1][0].add(lab1Progress);
                
                JLabel lab2 = new JLabel("2", JLabel.LEFT);
                JProgressBar lab2Progress = new JProgressBar();
                lab2Progress.setValue(0);
                lab2Progress.setStringPainted(true);
                nestedInnerPanelHolder[2][0].add(lab2);
                nestedInnerPanelHolder[2][0].add(lab2Progress);
                
                JLabel lab3 = new JLabel("3", JLabel.LEFT);
                JProgressBar lab3Progress = new JProgressBar();
                lab3Progress.setValue(0);
                lab3Progress.setStringPainted(true);
                nestedInnerPanelHolder[3][0].add(lab3);
                nestedInnerPanelHolder[3][0].add(lab3Progress);
                
                JLabel lab4 = new JLabel("4", JLabel.LEFT);
                JProgressBar lab4Progress = new JProgressBar();
                lab4Progress.setValue(0);
                lab4Progress.setStringPainted(true);
                nestedInnerPanelHolder[4][0].add(lab4);
                nestedInnerPanelHolder[4][0].add(lab4Progress);
                
                JLabel lab5 = new JLabel("5", JLabel.LEFT);
                JProgressBar lab5Progress = new JProgressBar();
                lab5Progress.setValue(0);
                lab5Progress.setStringPainted(true);
                nestedInnerPanelHolder[5][0].add(lab5);
                nestedInnerPanelHolder[5][0].add(lab5Progress);
                
                JLabel lab6 = new JLabel("6", JLabel.LEFT);
                JProgressBar lab6Progress = new JProgressBar();
                lab6Progress.setValue(0);
                lab6Progress.setStringPainted(true);
                nestedInnerPanelHolder[6][0].add(lab6);
                nestedInnerPanelHolder[6][0].add(lab6Progress);
                
                JLabel lab7 = new JLabel("7", JLabel.LEFT);
                JProgressBar lab7Progress = new JProgressBar();
                lab7Progress.setValue(0);
                lab7Progress.setStringPainted(true);
                nestedInnerPanelHolder[7][0].add(lab7);
                nestedInnerPanelHolder[7][0].add(lab7Progress);
                
                JLabel lab8 = new JLabel("8", JLabel.LEFT);
                JProgressBar lab8Progress = new JProgressBar();
                lab8Progress.setValue(0);
                lab8Progress.setStringPainted(true);
                nestedInnerPanelHolder[8][0].add(lab8);
                nestedInnerPanelHolder[8][0].add(lab8Progress);
                
                JLabel lab9 = new JLabel("9", JLabel.LEFT);
                JProgressBar lab9Progress = new JProgressBar();
                lab9Progress.setValue(0);
                lab9Progress.setStringPainted(true);
                nestedInnerPanelHolder[9][0].add(lab9);
                nestedInnerPanelHolder[9][0].add(lab9Progress);
                
                JButton genImage = new JButton("Guess Image");
                genImage.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        takeSnapShot(paintArea);
                        
                        File image = new File("img/screenshots.jpeg");
                        INDArray output = network.recognize(layerNetwork, image);
                        float[] f = output.data().asFloat();
                        
                        lab0Progress.setValue((int)(f[0] * 100));
                        lab1Progress.setValue((int)(f[1] * 100));
                        lab2Progress.setValue((int)(f[2] * 100));
                        lab3Progress.setValue((int)(f[3] * 100));
                        lab4Progress.setValue((int)(f[4] * 100));
                        lab5Progress.setValue((int)(f[5] * 100));
                        lab6Progress.setValue((int)(f[6] * 100));
                        lab7Progress.setValue((int)(f[7] * 100));
                        lab8Progress.setValue((int)(f[8] * 100));
                        lab9Progress.setValue((int)(f[9] * 100));
                    }
                });
                
                JButton erImage = new JButton("Erase Image");
                erImage.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ((TestPane) paintArea).resetPoints();
                        ((TestPane)paintArea).paintComponent(paintArea.getGraphics());
                        
                        lab0Progress.setValue(0);
                        lab1Progress.setValue(0);
                        lab2Progress.setValue(0);
                        lab3Progress.setValue(0);
                        lab4Progress.setValue(0);
                        lab5Progress.setValue(0);
                        lab6Progress.setValue(0);
                        lab7Progress.setValue(0);
                        lab8Progress.setValue(0);
                        lab9Progress.setValue(0);
                    }
                });
                
                innerPanelHolder[0][0].add(genImage);
                innerPanelHolder[0][0].add(erImage);
                
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