public class NN_Static_Func {
    static double cal_weightedSum(double weights[], double inputs[], double bias, double biasWeight)
    {
        double result = 0;
        for(int i=0;i<weights.length;i++)
        {
            result = result + (inputs[i]*weights[i]);
        }
        result = result + (bias*biasWeight);
        
        return result;
    }
    
    static double cal_output(double netSum, String type)
    {
        if(type.equals("step"))//Step function
        {
            return cal_step(netSum);
        }
        if(type.equals("sign"))
        {
            return cal_sign(netSum);
        }
        if(type.equals("sigmoid"))
        {
            return cal_sigmoid(netSum);
        }
        return -1;//Fail
    }
    
    static int cal_step(double netSum)
    {
        if(netSum>=0)
            return 1;
        else
            return 0;
    }
    
    static int cal_sign(double netSum)
    {
        if(netSum>=0)
            return 1;
        else
            return -1;
    }
    
    static double cal_sigmoid(double netSum)
    {
        netSum = (-1)*netSum;
        return (1/(double)(1+Math.pow(Math.E,netSum)));
    }
    
    static double cal_MSE(double output[], double target[])
    {
       double mse = 0;
       int count = 0;
       for(int i=0;i<output.length;i++)
       {
           count++;
//           System.out.println("OUT: "+output[i]+"\tTAR:"+target[i]);
           mse = mse + Math.pow((output[i]-target[i]),2);
       }
       mse = (double)mse/(double)count;
       return mse;
    }
    
    static double cal_delta0(double out, double target)
    {
        return (out - target)*out*(1 - out);
    }
    
    static double cal_deltaH(double out, double target, double deltasAtO[][], double oldWeights[][])
    {
        return 0;
    }    
}