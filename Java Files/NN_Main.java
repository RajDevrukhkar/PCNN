import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Scanner;

public class NN_Main {
    
    int pcnn = 0;//1 = PCNN, 0 = FCNN
    public static void main(String args[])
    {
        double input[][], target[][];
        input = null;
        target = null;
        
        System.out.println("Enter Data Set: ");
        Scanner s = new Scanner(System.in);
        int ds = s.nextInt();
        System.out.println("How any Hidden Nodes?: ");
        int hn = s.nextInt();
        System.out.println("PCNN?(1=True, 2=False): ");
        int pcn = s. nextInt();
        
        
        String inp = "";
        String tar = "";
        if(ds == 1)
        {
            inp = "input1.ser";
            tar = "target1.ser";
        }
        else
        {
            inp = "input2.ser";
            tar = "target2.ser";
        }
        try
        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(inp));
            input = (double[][]) in.readObject();
            in.close();
            
            in = new ObjectInputStream(new FileInputStream(tar));
            target = (double[][]) in.readObject();
            in.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        NeuralNetwork nn = new NeuralNetwork(input, target, hn);
        
        if(pcn == 1)nn.pcnn = true;
        else
            if(pcn==2) nn.pcnn = false;
        
        nn.train();
        int br = 2;
        
        while(true)
        {
            System.out.println("Press 2 to Test/1 to Quit: ");
            br = s.nextInt();
            
            if(br == 1) break;
//            nn.test(input, target);            
        }
        
    }
}
