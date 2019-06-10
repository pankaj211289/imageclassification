import java.io.File;
import java.io.IOException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

public class Test {

    NeuralNetwork network = null;
    
	public static void main(String[] args) throws IOException {
		NeuralNetwork network = new NeuralNetwork();
		MultiLayerNetwork layerNetwork = network.init();
		
		network.trainNetwork(layerNetwork);
		
		do {
			File image = new File("img/screenshots.jpeg");
			network.recognize(layerNetwork, image);
		} while (true);
		
//		do {
//			File image = new File("img/screenshots.jpeg");
//			network.detect(layerNetwork, image);
//		} while (true);
		
		
//		do {
//			File image = new File("img/screenshots.jpeg");
//	        BufferedImage bufferedImage = ImageIO.read(image);
//			network.recognize(layerNetwork, bufferedImage);
//		} while (true);
	}
}
