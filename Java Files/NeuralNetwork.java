import java.util.ArrayList;
import java.util.Arrays;

public class NeuralNetwork {

    int numInputLayers = 1, numHiddenLayers = 1, numOutputLayers = 1;
    int numInputNodes = 1, numHiddenNodes = 1, numOutputNodes = 1;
    int numEpochs = 1, numSamples = 1;
    String layerActivationFn[];
    ArrayList<Connection> connectionsIH;
    ArrayList<Connection> connectionsHO;
    ArrayList<Connection> biasConnectsH;
    ArrayList<Connection> biasConnectsO;
    double input[][], output[][], target[][];//Samples x attriutes
    double alpha = 0.1;//Learning Rate
    ArrayList<Double> MSEs = new ArrayList<>();
    double weightThreshold = 0.01;
    double MSE_Threshold = 0.00001;
    double dropThreshold = 0.005;
    boolean pcnn = true;

    NeuralNetwork(double input[][], double target[][], int noHidNodes) {
        
        this.numSamples = input.length;
        System.out.println("-----------"+this.numSamples+" "+input.length);

        this.numInputLayers = 1;//default
        this.numHiddenLayers = 1;//default
        this.numOutputLayers = 1;//default

        this.numInputNodes = input[0].length;
        this.numHiddenNodes = noHidNodes;
        this.numOutputNodes = target[0].length;

        this.input = new double[this.numSamples][this.numInputNodes];
        this.target = new double[this.numSamples][this.numOutputNodes];

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                this.input[i][j] = input[i][j];
            }
        }

        for (int i = 0; i < target.length; i++) {
            for (int j = 0; j < target[0].length; j++) {
                this.target[i][j] = target[i][j];
            }
        }

        connectionsIH = new ArrayList<>();
        connectionsHO = new ArrayList<>();
        biasConnectsH = new ArrayList<>();
        biasConnectsO = new ArrayList<>();


        randomInit(connectionsIH, this.numInputNodes, this.numHiddenNodes);
        //displayArrayList(connectionsIH, "IH Connects");
        randomInit(connectionsHO, this.numHiddenNodes, this.numOutputNodes);
        //displayArrayList(connectionsHO, "HO Connects");
        randomInit(biasConnectsH, 1, this.numHiddenNodes);
        //displayArrayList(biasConnectsH, "HBias Connects");
        randomInit(biasConnectsO, 1, this.numOutputNodes);
        //displayArrayList(biasConnectsO, "OBias Connects");

        this.layerActivationFn = new String[2];
        this.layerActivationFn[0] = "sigmoid";
        this.layerActivationFn[1] = "sigmoid";

        this.output = new double[this.numSamples][this.numOutputNodes];
        for (int i = 0; i < output.length; i++) {
            Arrays.fill(this.output[i], 0);
        }
    }

    static void randomInit(ArrayList<Connection> conn, int a, int b) {
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
               Connection tmp = new Connection(i,j,Math.random()-0.5);
               //Connection tmp = new Connection(i, j, 0.5);
               conn.add(tmp);
            }
        }
    }

    void train() {
        
        long startTime = System.currentTimeMillis();
        long m1 = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory();
        long m2 = 0;
        long exeTime = 0;
        numEpochs = 1000;
        double mse = 0;
        
        double oldWeights[][] = new double[2][2];
        for (int i = 0; i < oldWeights.length; i++) {
            Arrays.fill(oldWeights[i], 0);
        }

        System.out.println("##########    NETWORK ARCHITECTURE    ##########");
        System.out.println("Input Nodes: "+this.numInputNodes);
        System.out.println("Hidden Nodes: "+this.numHiddenNodes);
        System.out.println("Output Nodes: "+this.numOutputNodes+"\n\n");
        
        for (int epoch = 1; epoch <= numEpochs; epoch++) {
            //System.out.println("-------------------------------->Epoch: "+epoch);
            for (int sample = 0; sample < numSamples; sample++) {
                //System.out.println("------------------>Sample: "+sample);
                double inH[] = this.input[sample];//Input to hidden layer
                double outH[] = new double[this.numHiddenNodes];//Output at Hidden Layer

                double inO[] = new double[this.numHiddenNodes];// should be = outH[]
                double outO[] = new double[this.numOutputNodes];//Final output
                double tarO[] = this.target[sample];//Target for this sample.

                //Calculate Output at Hidden Layer
                //displayOneArray(inH, "Input to Hidden: ");
                //displayArrayList(connectionsIH, "Connections I->H: ");
                //displayArrayList(biasConnectsH, "Bias->H: ");
                outH = cal_outputArr(connectionsIH, inH, this.numHiddenNodes, biasConnectsH);
                //displayOneArray(outH, "Output at Hidden: ");

                //displayArrayList(connectionsHO, "Connections H->O: ");
                //displayArrayList(biasConnectsO, "Bias->O: ");
                for (int i = 0; i < this.numHiddenNodes; i++) {
                    inO[i] = outH[i];
                }

                //Calculate Output at Outputlayer
                outO = cal_outputArr(connectionsHO, inO, this.numOutputNodes, biasConnectsO);

                //displayOneArray(outO, "Output at Output: ");
                //Update the weights at output layer
                //Calculate error at output.
                double errorO[] = new double[this.numOutputNodes];
                
                //You can add this to NN_Static_Func
                for (int i = 0; i < this.numOutputNodes; i++) {
                    errorO[i] = (outO[i] - tarO[i]) * outO[i] * (1 - outO[i]);
                }
                
                //displayOneArray(tarO, "Target Output: ");
                //displayOneArray(errorO, "Error: ");
                
                oldWeights = new double[this.numHiddenNodes][this.numOutputNodes];
                for (int i = 0; i < this.connectionsHO.size(); i++) {
                    oldWeights[this.connectionsHO.get(i).from][this.connectionsHO.get(i).to] = connectionsHO.get(i).weight;
                }

                //displayArrayList(connectionsHO, "Previous Connections HO");
                updateWeights(this.connectionsHO, inO, errorO, this.alpha);
                //displayArrayList(connectionsHO, "New Connections HO");

                //Update the weights at hidden layer
                double errorH[] = new double[this.numHiddenNodes];
                
                for (int i = 0; i < this.numHiddenNodes; i++) {
                    double sum = 0;
                    for (int j = 0; j < this.numOutputNodes; j++) {
                        sum = sum + (oldWeights[i][j] * errorO[j]);
                    }
                    errorH[i] = sum * outH[i] * (1 - outH[i]);
                }
                
                //displayOneArray(errorH, "Error at Hid: ");

                //displayArrayList(connectionsIH, "Previous Connections IH");
                updateWeights(this.connectionsIH, inH, errorH, this.alpha);
                //displayArrayList(connectionsIH, "Next Connections IH");

                mse = NN_Static_Func.cal_MSE(outO, tarO);
//                System.out.println("MSE: "+mse);
                
//                if((sample == numSamples-1)||(sample == 0)||(sample == 50859)||(sample == 50858))
//                {
//                    System.out.println("Sample: "+sample);
//                    displayOneArray(outO, "OUTPUT:");
//                    displayOneArray(tarO, "TARGET:");
//                }
                
//                System.out.println("E:"+epoch+" S:"+sample+" M:"+mse);
                //Drop connections for PCNN
                if(pcnn)
                {
                    dropConn();
                }
            }
//            if(mse<this.MSE_Threshold){
//                exeTime= System.currentTimeMillis() - startTime;
//                System.out.println("Number of Epochs: "+epoch);
//                System.out.println("MSE: "+mse);
//                break;
//            }
//            if (checkWeights(oldWeights, this.connectionsHO)) {
//                exeTime= System.currentTimeMillis() - startTime;
//                System.out.println("Number of Epochs: "+epoch);
//                System.out.println("MSE: "+mse);
//                break;
//            }
        }
        System.out.println("MSE: "+mse);
        exeTime= System.currentTimeMillis() - startTime;
        System.out.println(exeTime + " milliseconds");
        m2 = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Space: "+((float)(m2-m1)/(float)(1024*1024)));
    }

    void test(double input[][], double target[][]) {
        int numSamples = input.length;
        int confusionMatrix[][] = new int[this.numOutputNodes][this.numOutputNodes];
        displayArrayList(connectionsIH, "Conn I-->H");
        displayArrayList(connectionsHO, "Conn H-->O");

        for (int sample = 0; sample < numSamples; sample++) {
            double inH[] = this.input[sample];//Input to hidden layer
            double outH[] = new double[this.numHiddenNodes];//Output at Hidden Layer

            double inO[] = new double[this.numHiddenNodes];// should be = outH[]
            double outO[] = new double[this.numOutputNodes];//Final output
            double tarO[] = this.target[sample];//Target for this sample.

            displayOneArray(inH, "In at H: ");
            outH = cal_outputArr(connectionsIH, inH, this.numHiddenNodes, biasConnectsH);
            displayOneArray(outH, "Out at H: ");
            
            for (int i = 0; i < this.numHiddenNodes; i++) {
                inO[i] = outH[i];
            }

            outO = cal_outputArr(connectionsHO, inO, this.numOutputNodes, biasConnectsO);
            displayOneArray(tarO, "Target: ");
            displayOneArray(outO, "Out at O: ");

            
            int index = maxOut(outO);
            //System.out.println(index);
            if( tarO[index] == 1)
            {
                confusionMatrix[index][index]++;
            }
            else
            {
                confusionMatrix[index][indexTar(tarO)]++;
            }
        }
        
        System.out.println("\nConfusion matrix:");
        for(int i=0;i<confusionMatrix.length;i++)
        {
            for(int j=0;j<confusionMatrix[0].length;j++)
            {
                System.out.print(confusionMatrix[i][j]+" ");
            }
            System.out.println();
        }
    }

    int indexTar(double tar[])
    {
        int i;
        for(i=0;i<tar.length;i++)
        {
            if(tar[i] == 1)
                break;
        }
        return i;
    }
    
    int maxOut(double in[])
    {
        double max = 0;
        int index = -1;
        System.out.println("OUTPUT");
        for(int i=0;i<in.length;i++)
        {
            System.out.print(in[i]+" ");
            if(max<in[i])
            {
                index = i;
                max = in[i];
            }
        }
        
        return index;
    }
    
    boolean checkWeights(double[][] oldWeights, ArrayList<Connection> connWts) {
        boolean brk = true;

        for (int i = 0; i < connWts.size(); i++) {
            if (Math.abs(oldWeights[connWts.get(i).from][connWts.get(i).to] - connWts.get(i).weight) < this.weightThreshold) {
                brk = false;
                break;
            }
        }

        return brk;
    }

    void dropConn() {
        for (int i = 0; i < this.connectionsHO.size(); i++) {
            if (connectionsHO.get(i).weight < dropThreshold) {
                connectionsHO.remove(i);
            }
        }
        for (int i = 0; i < this.connectionsIH.size(); i++) {
            if (Math.abs(connectionsIH.get(i).weight) < dropThreshold);
            {
                connectionsIH.remove(i);
            }
        }

    }

    void displayArrayList(ArrayList<Connection> a, String name) {
        System.out.println("\nArrayList " + name + " " + a.size());
        for (int i = 0; i < a.size(); i++) {
            a.get(i).display();
        }
    }

    void displayOneArray(double a[], String name) {
        System.out.println("\nArray " + name);
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + " ");
        }
        System.out.println();
    }

    double[] cal_outputArr(ArrayList<Connection> arr, double[] input, int num, ArrayList<Connection> bias) {
        double net[] = new double[num];
        Arrays.fill(net, 0);
        
        for (int i = 0; i < arr.size(); i++) {
            net[arr.get(i).to] += arr.get(i).weight * input[arr.get(i).from];
        }

        for (int i = 0; i < bias.size(); i++) {
            net[bias.get(i).to] -= bias.get(i).weight;
        }

        for (int i = 0; i < num; i++) {
            net[i] = (double) 1 / (double) (1 + Math.pow(Math.E, (-1) * net[i]));
        }
        return net;
    }

    void updateWeights(ArrayList<Connection> wt, double[] in, double[] error, double alpha) {
        for (int i = 0; i < wt.size(); i++) {
            wt.get(i).weight = wt.get(i).weight - (alpha * error[wt.get(i).to] * in[wt.get(i).from]);
        }
    }
}
