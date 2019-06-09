import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;

import javax.imageio.ImageIO;

public class ImageUtil {

	private static final int SIZE_X = 28;
	private static final int SIZE_Y = 28;
	private static final int SCALE_SIZE_X = 20;
	private static final int SCALE_SIZE_Y = SCALE_SIZE_X;
	
	public float[] toMnistArray(BufferedImage img) {
		// Binarize Image
		BufferedImage binaryImage = binarize(convertToGray(img));
		
		// Resize image to 20x20
		BufferedImage scaledImage = resize(binaryImage, SCALE_SIZE_X, SCALE_SIZE_Y);
		int[][] scaledImageMatrix = grayscaleImageToPixelMatrix(scaledImage);
		
		// Calculate center of gravity of Image
		int[][] targetScaledImageMatrix = scaleAndCenterToTarget(scaledImageMatrix);
		
		// Center image using center of gravity
		int[][] contrastNormalizedImageMatrix = normalizeContrast(targetScaledImageMatrix);
		
		return toFloatArray(contrastNormalizedImageMatrix);
	}
	
	public void drawImage(BufferedImage image) {
		File imageFile = new File("." + File.separator + "img/screenshots.jpeg");
		try {
			ImageIO.write(image, "jpeg", imageFile);
		} catch (IOException e) {}
	}
	
	public void drawImage(int[][] matrix) {
		File imageFile = new File("." + File.separator + "img/screenshots123.jpeg");
		int width = matrix[0].length;
		int height = matrix.length;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		
		for(int i = 0; i < width; i ++) {
			for(int j = 0; j < height; j++) {
				image.setRGB(i, j, matrix[i][j]);
			}
		}
		
		try {
			ImageIO.write(image, "jpeg", imageFile);
		} catch (IOException e) {}
	}
	
	public BufferedImage convertToGray(BufferedImage img) {
		int alpha, red, green, blue;
		int brightness;
		int newPixel;
		
		BufferedImage grayedImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		for(int i = 0; i < img.getWidth(); i++) {
			for(int j = 0; j < img.getHeight(); j++) {
				
				alpha = new Color(img.getRGB(i, j)).getAlpha();
				red = new Color(img.getRGB(i, j)).getRed();
				green = new Color(img.getRGB(i, j)).getGreen();
				blue = new Color(img.getRGB(i, j)).getBlue();
				
				// Calculate luminance/brightness
				brightness = (int)(0.2126 * red + 0.7152 * green + 0.0722 * blue);
				
				newPixel = colorToRGB(alpha, brightness, brightness, brightness);
				grayedImage.setRGB(i, j, newPixel);
			}
		}
		
		return grayedImage;
	}
	
	public BufferedImage binarize(BufferedImage img) {
		int red, newPixel;
		
		int threshold = calOtsuThreshold(img);
		BufferedImage binarized = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				// Get pixels
				red = new Color(img.getRGB(i, j)).getRed();
				int alpha = new Color(img.getRGB(i, j)).getAlpha();
				if (red > threshold) {
					newPixel = 255;
				} else {
					newPixel = 0;
				}
				newPixel = colorToRGB(alpha, newPixel, newPixel, newPixel);
				binarized.setRGB(i, j, newPixel);
			}
		}
		return binarized;
	} 
	
	public int[][] scaleAndCenterToTarget(int[][] sourceImageMatrix) {
		double[] centerOfGravity = calcCenterOfGravity(sourceImageMatrix);
		int centerX = (int) Math.round(centerOfGravity[0]);
		int centerY = (int) Math.round(centerOfGravity[1]);
		// center could be too far to one side. best effort:
		int maxCenterDeltaX = (SIZE_X - SCALE_SIZE_X) / 2;
		int maxCenterDeltaY = (SIZE_Y - SCALE_SIZE_Y) / 2;

		if (centerX < SCALE_SIZE_X / 2 - maxCenterDeltaX) {
			centerX = SCALE_SIZE_X / 2 - maxCenterDeltaX;
		} else if (centerX > SCALE_SIZE_X / 2 + maxCenterDeltaX) {
			centerX = SCALE_SIZE_X / 2 + maxCenterDeltaX;
		}
		if (centerY < SCALE_SIZE_Y / 2 - maxCenterDeltaY) {
			centerY = SCALE_SIZE_Y / 2 - maxCenterDeltaY;
		} else if (centerY > SCALE_SIZE_Y / 2 + maxCenterDeltaY) {
			centerY = SCALE_SIZE_Y / 2 + maxCenterDeltaY;
		}
		int translateX = SIZE_X / 2 - centerX;
		int translateY = SIZE_Y / 2 - centerY;
		int[][] targetImageMatrix = new int[SIZE_X][SIZE_Y];
		for (int x = 0; x < SIZE_X; x++) {
			for (int y = 0; y < SIZE_Y; y++) {
				int sourceImageMatrixX = x - translateX;
				int sourceImageMatrixY = y - translateY;
				if (sourceImageMatrixX >= 0 && sourceImageMatrixX < SCALE_SIZE_X && sourceImageMatrixY >= 0
						&& sourceImageMatrixY < SCALE_SIZE_Y) {
					targetImageMatrix[x][y] = sourceImageMatrix[sourceImageMatrixX][sourceImageMatrixY];
				} else {
					targetImageMatrix[x][y] = 255;
				}
			}
		}
		return targetImageMatrix;
	}
	
	public int[][] normalizeContrast(int[][] targetScaledImageMatrix) {
		int[][] contrastNormalizedImageMatrix = new int[SIZE_X][SIZE_Y];
		int min = 255;

		for (int x = 0; x < SIZE_X; x++) {
			for (int y = 0; y < SIZE_Y; y++) {
				if (targetScaledImageMatrix[x][y] < min) {
					min = targetScaledImageMatrix[x][y];
				}
			}
		}
		for (int x = 0; x < SIZE_X; x++) {
			for (int y = 0; y < SIZE_Y; y++) {
				contrastNormalizedImageMatrix[x][y] = 255 - ((255-targetScaledImageMatrix[x][y])*255 / (255-min));
			}
		}
		return contrastNormalizedImageMatrix;
	}
	
	public BufferedImage toBufferedImage(int[][] imageMatrix) {
		int width = imageMatrix.length;
		int height = imageMatrix[0].length;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y,
						(255 << 24) | (imageMatrix[x][y] << 16) | (imageMatrix[x][y] << 8) | imageMatrix[x][y]);
			}
		}
		return image;
	}
	
	public int[][] grayscaleImageToPixelMatrix(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		int[][] matrix = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int p = img.getRGB(x, y);
				matrix[x][y] = p & 0xff;
			}
		}
		return matrix;
	}
	
	//Converts R, G, B, Alpha to standard 8 bit
	private int colorToRGB(int alpha, int red, int green, int blue) {
		int newPixel = 0;
		newPixel += alpha;
		newPixel = newPixel << 8;
		newPixel += red;
		newPixel = newPixel << 8;
		newPixel += green;
		newPixel = newPixel << 8;
		newPixel += blue;
		return newPixel;
	}
	
	private int[] imageHistogram(BufferedImage img) {
		int[] histogram = new int[256];
		for(int i = 0; i < histogram.length; i++) {
			histogram[i] = 0;
		}
		
		for(int i = 0; i < img.getWidth(); i++) {
			for(int j = 0; j < img.getHeight(); j++) {
				int col = new Color(img.getRGB(i, j)).getRed();
				histogram[col]++;
			}
		}
		
		return histogram;
	}
	
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
	
	private int calOtsuThreshold(BufferedImage img) {
		int[] histogram = imageHistogram(img);
		
		int total = img.getHeight() * img.getWidth();
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

	public BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage resizedImage = new BufferedImage(newW, newH, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g2d = resizedImage.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resizedImage;
	}
	
	private double[] calcCenterOfGravity(int[][] picture) {
		MathContext mc = MathContext.DECIMAL32;
		BigDecimal zero = new BigDecimal(0, mc);
		BigDecimal summedGrayScaleValues = zero;
		BigDecimal summedRowGrayScaleValues = zero;
		BigDecimal summedColumnGrayScaleValues = zero;
		for (int rowIndex = 0; rowIndex < picture.length; rowIndex++) {
			int[] row = picture[rowIndex];
			for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
				int grayScaleWeight = 255 - row[columnIndex];
				if (grayScaleWeight > 0) {
					summedRowGrayScaleValues = summedRowGrayScaleValues
							.add(BigDecimal.valueOf(grayScaleWeight * rowIndex), mc);
					summedColumnGrayScaleValues = summedColumnGrayScaleValues
							.add(BigDecimal.valueOf(grayScaleWeight * columnIndex), mc);
					summedGrayScaleValues = summedGrayScaleValues.add(BigDecimal.valueOf(grayScaleWeight), mc);
				}
			}
		}
		BigDecimal rowAverage = summedGrayScaleValues.longValue() > 0
				? summedRowGrayScaleValues.divide(summedGrayScaleValues, mc) : zero;
		BigDecimal columnAverage = summedGrayScaleValues.longValue() > 0
				? summedColumnGrayScaleValues.divide(summedGrayScaleValues, mc) : zero;
		double[] center = new double[] { rowAverage.doubleValue(), columnAverage.doubleValue() };
		return center;
	}
	
	private float[] toFloatArray(int[][] intarray) {
		float[] doublearray = new float[intarray.length * intarray[0].length];

		for (int i = 0; i < intarray.length; i++) {
			for (int j = 0; j < intarray[0].length; j++) {
				int index = intarray.length * i + j;
				doublearray[index] = convertToFloat(intarray[j][i]);
			}
		}
		return doublearray;
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
}
