import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class NeuralNetwork {

	public static final int NUM_OUTPUTS = 10;
	public static final int BATCH_SIZE = 64;
	
	private static final int NUM_CHANNELS = 1;
	private static final int NUM_ITERATIONS = 1;
	private static final int SEED = 123;
	private static final int EPOCHS = 6;
	
	public MultiLayerNetwork init() {
		MultiLayerConfiguration configuration = networkConfiguration();
		MultiLayerNetwork network = new MultiLayerNetwork(configuration);
		network.init();
		
		return network;
	}
	
	public void trainNetwork(MultiLayerNetwork network) throws IOException {
		DataSetIterator numbersTrainData = new MnistDataSetIterator(BATCH_SIZE, 10000, true);
		DataSetIterator numbersValidationData = new MnistDataSetIterator(BATCH_SIZE, 1000, true);
		
		//-------------
		INDArray dataBuffer = numbersTrainData.next().getFeatures();
		int[] img = dataBuffer.data().asInt();
		System.out.println(img);
		int[][] imgArr = new int[28][28];
		
		for(int i = 0; i < 28; i++) {
			int startIndex = 28*i;
			imgArr[i] = Arrays.copyOfRange(img, startIndex, startIndex + 28);
		}
		new ImageUtil().drawImage(imgArr);
		//------------------
		
		for(int epoch = 1; epoch <= EPOCHS; epoch++) {
			// train the network using training data
			System.out.printf("Starting epoch %d, samples: %d", epoch, numbersTrainData.numExamples());
			numbersTrainData.reset();
			network.fit(numbersTrainData);
			
			// evaluate performance using validation data
			numbersValidationData.reset();			
			evaluate(network, numbersValidationData);
		}
	}
	
	public MultiLayerConfiguration networkConfiguration() {
		return new NeuralNetConfiguration.Builder()
				.seed(SEED).weightInit(WeightInit.XAVIER)
				.iterations(NUM_ITERATIONS)
				.regularization(true).l2(0.0005).learningRate(.01)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.updater(new Nesterovs(0.9))
				.list()
				.layer(0, new ConvolutionLayer.Builder(5, 5) 
						.stride(1, 1)
						.nIn(NUM_CHANNELS)
						.nOut(20)
						.activation(Activation.IDENTITY)
						.build())
				.layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
						.kernelSize(2, 2)
						.stride(2, 2)
						.build())
				.layer(2, new ConvolutionLayer.Builder(5, 5).stride(1, 1)
						.nOut(50)
						.activation(Activation.IDENTITY)
						.build())
				.layer(3, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
						.kernelSize(2, 2)
						.stride(2, 2)
						.build())
				.layer(4, new DenseLayer.Builder()
						.activation(Activation.RELU)
						.nOut(500)
						.build())
				.layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
						.activation(Activation.SOFTMAX)
						.nOut(NUM_OUTPUTS)
						.build())
				.setInputType(InputType.convolutionalFlat(28, 28, 1))
				.backprop(true)
				.pretrain(false).build();
	}

	public void evaluate(MultiLayerNetwork network, DataSetIterator testData) {
		if(testData == null || testData.numExamples() == 0) {
			return;
		}
		
		System.out.println("\nEvaluate model....");
		long timeStart = System.currentTimeMillis();
		
		Evaluation evaluation = new Evaluation(NUM_OUTPUTS);
		int samples = 0;
		
		while (testData.hasNext()) {
			DataSet ds = testData.next();
			if(ds.getFeatureMatrix() != null) {
				INDArray output = network.output(ds.getFeatureMatrix(), false);
				evaluation.eval(ds.getLabels(), output);
				samples += ds.numExamples();
			}
		}
		
		long timeStop = System.currentTimeMillis();
		
		System.out.println(evaluation.stats());
		System.out.printf("Number of samples: %d, processing time [ms]: %d", samples, timeStop - timeStart);
	}

	public INDArray recognize(MultiLayerNetwork network, BufferedImage image) {
		float[] normalizeImage = new ImageUtil().toMnistArray(image);

		INDArray input = Nd4j.create(normalizeImage);
		INDArray output = network.output(input);

		return output;
	}
	
	public INDArray recognize(MultiLayerNetwork network, File file) {
		INDArray input = normalizeImage(file);
		INDArray output = network.output(input);

		return output;
	}
	
	// Normalize image
	public INDArray normalizeImage(File f) {
		
		// convert to numerical matrix
		NativeImageLoader loader = new NativeImageLoader(28, 28, 1);
		
		// put image into INDArray
		INDArray image = null;
		try {
			image = loader.asMatrix(f);
		} catch (IOException e) {}
		
		// Define scaling
		DataNormalization scalar = new ImagePreProcessingScaler(0, 255);
		
		// Scaling image
		scalar.transform(image);
		INDArray flaten = image.reshape(new int[]{1, 784});
		
		int[] binData = new ImageUtil().binarize(flaten.data().asInt());
		
		return Nd4j.create(new ImageUtil().toFloatArray(binData));
	}
}
