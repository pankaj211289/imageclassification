import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageUtil {

	private int[] imageHistogram(int[] array) {
		int[] histogram = new int[256];
		for(int i = 0; i < histogram.length; i++) {
			histogram[i] = 0;
		}
		
		for(int i = 0; i < array.length; i++) {
			int val = array[i];
			histogram[val]++;
		}
		
		return histogram;
	}
	
	public int calThreshold(int[] arr) {
		int[] histogram = imageHistogram(arr);
		
		int total = 28 * 28;
		float sum = 0, varMax = 0, sumB = 0;
		int wB = 0, wF = 0, threshold = 0;
		
		for(int i = 0; i < 256; i++) {
			sum += i * histogram[i];
		}
		
		for(int i = 0; i < 256; i++) {
			wB += histogram[i];
			if(wB == 0) {
				continue;
			} 
			wF = total - wB;
			if(wF == 0) {
				break;
			}
			sumB += i * histogram[i];
			float mB = sumB/wB;
			float mF = (sum - sumB)/wF;
			float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);
			if (varBetween > varMax) {
				varMax = varBetween;
				threshold = i;
			}
		}
		return threshold;
	}
	
	public int[] binarize(int[] arr) {
		int[] binaryArr = new int[arr.length];
		int threshold = calThreshold(arr);
		for(int  i = 0; i < arr.length; i++) {
			if(arr[i] >  threshold) {
				binaryArr[i] = 255;
			} else {
				binaryArr[i] = 0;
			}
		}
		return binaryArr;
	}
	
	public float[] toFloatArray(int[] arr) {
		float[] doublearray = new float[arr.length];

		for (int i = 0; i < arr.length; i++) {
			doublearray[i] = convertToFloat(arr[i]);
		}
		return doublearray;
	}

	private float convertToFloat(int rgb) {
		return (255.0F - rgb) / 255.0F;
	}
	
	public static BufferedImage cropImage(File f) {
		BufferedImage rescaledImg = null;
		
		try {
			BufferedImage bufferedImage = ImageIO.read(f);
			
			int xmin = bufferedImage.getWidth();
			int xmax = 0;
			int ymin = bufferedImage.getHeight();
			int ymax = 0;
			
			int minRGBVal = getMinRGBVal(bufferedImage);
			int maxRGBVal = getMaxRGBVal(bufferedImage);
			int median = (minRGBVal + maxRGBVal)/2;
			
			// create binary matrix acc to image
			int[][] imgMatrix = new int[bufferedImage.getHeight()][bufferedImage.getWidth()];
			for(int j = 0; j < bufferedImage.getHeight(); j++) {
				for(int i = 0; i < bufferedImage.getWidth(); i++) {
					if(bufferedImage.getRGB(i, j) >= median) {
						imgMatrix[j][i] = 255;
					} else {
						imgMatrix[j][i] = 0;
					}
				}
			}
			
			// determine xmin, xmax, ymin, ymax
			for(int j = 0; j < imgMatrix.length; j++) {
				for(int i = 0; i < imgMatrix[j].length; i++) {
					if(imgMatrix[j][i] != 255) {
						if(xmin > i) {
							xmin = i;
						}
						
						if(ymin > j) {
							ymin = j;
						}
						
						if(xmax < i) {
							xmax = i;
						}
						
						if(ymax < j) {
							ymax = j;
						}
					}
				}
			}
			
			int maxWidth = 28;
			int maxHeight = 28;
			
			xmin = xmin > 2 ? xmin - 2 : (xmin > 1 ? xmin - 1 : xmin);
			ymin = ymin > 2 ? ymin - 2 : (ymin > 1 ? ymin - 1 : ymin);
			
			xmax = xmax < maxWidth - 2 ? xmax + 2 : (xmax < maxWidth - 1 ? xmax + 1 : xmax);
			ymax = ymax < maxHeight - 2 ? ymax + 2 : (ymax < maxHeight - 1 ? ymax + 1 : ymax);
			
			BufferedImage croppedImg = bufferedImage.getSubimage(xmin, ymin, xmax - xmin, ymax - ymin);
			
			rescaledImg = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
			Graphics2D g2d = rescaledImg.createGraphics();
			AffineTransform at = AffineTransform.getScaleInstance(28/(double)(xmax - xmin), 28/(double)(ymax - ymin));
			g2d.drawRenderedImage(croppedImg, at);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rescaledImg;
	}
	
	private static int getMinRGBVal(BufferedImage bufferedImage) {
		int min = Integer.MAX_VALUE;
		
		for(int j = 0; j < bufferedImage.getHeight(); j++) {
			for(int i = 0; i < bufferedImage.getWidth(); i++) {
				int currRGB = bufferedImage.getRGB(i, j);
				if(min > currRGB) {
					min = currRGB;
				}
			}
		}
		
		return min;
	}
	
	private static int getMaxRGBVal(BufferedImage bufferedImage) {
		int max = Integer.MIN_VALUE;
		
		for(int j = 0; j < bufferedImage.getHeight(); j++) {
			for(int i = 0; i < bufferedImage.getWidth(); i++) {
				int currRGB = bufferedImage.getRGB(i, j);
				if(max < currRGB) {
					max = currRGB;
				}
			}
		}
		
		return max;
	}
	
	public static void drawImage(BufferedImage image, String fileName) {
		File outImageFile = new File("." + File.separator + "img/" + fileName);
		try {
			ImageIO.write(image, "png", outImageFile);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
