import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;

public class DataSetSerializer {
    
   public static void main(String args[])
   {
       double i1[][] = new double[245057][3];
       double t1[][] = new double[245057][2];
       
       double i2[][] = new double[583][10];
       double t2[][] = new double[583][2];
       
       try
       {
            BufferedReader br = new BufferedReader(new FileReader("Skin_NonSkin.txt"));
            int counter = 0;
            String in = "";
            while((in = br.readLine())!=null)
            {
                String tmp[] = in.split("\t");
                i1[counter][0] = Double.parseDouble(tmp[0]);
                i1[counter][1] = Double.parseDouble(tmp[1]);
                i1[counter][2] = Double.parseDouble(tmp[2]);
                
                if(tmp[3].equals("2"))
                {
                    t1[counter][0] = 0;
                    t1[counter][1] = 1;
                }
                else
                {
                    if(tmp[3].equals("1"))
                    {
                        t1[counter][0] = 1;
                        t1[counter][1] = 0;
                    }
                }                
                counter++;
            }
            
            br.close();
            
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("input1.ser"));
            out.writeObject(i1);
            out.flush();
            out.close();
            
            out = new ObjectOutputStream(new FileOutputStream("target1.ser"));
            out.writeObject(t1);
            out.flush();
            out.close();
            
            br = new BufferedReader(new FileReader("Indian_Liver_Patient_Dataset.csv"));
            counter = 0;
            while((in = br.readLine())!=null)
            {
                String tmp[] = in.split(",");
                
//                System.out.println(counter);
//                for(int j=0;j<tmp.length;j++)
//                {
//                    System.out.print(j+":"+tmp[j]+"   ");
//                }
//                System.out.println();
                for(int i=0;i<10;i++)
                {
                    i2[counter][i] = Double.parseDouble(tmp[i]);
                }
                
                if(tmp[10].equals("1"))
                {
                    t2[counter][0] = 1;
                    t2[counter][1] = 0;
                }
                else
                {
                    if(tmp[10].equals("2"))
                    {
                        t2[counter][0] = 0;
                        t2[counter][1] = 1;
                    }
                }
                
                
                counter++;
            }
            
            br.close();
            
            out = new ObjectOutputStream(new FileOutputStream("input2.ser"));
            out.writeObject(i2);
            out.flush();
            out.close();
            
            out = new ObjectOutputStream(new FileOutputStream("target2.ser"));
            out.writeObject(t2);
            out.flush();
            out.close();
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }
   }    
}
