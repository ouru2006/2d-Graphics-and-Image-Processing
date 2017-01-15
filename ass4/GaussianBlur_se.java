import java.awt.Color;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;


public class GaussianBlur_se {
    private static int[] h;
    private static double f[];
    private static double r = 2.5;
	public static int[][] ProcessImage( int [][] inputPixels){
		int width = inputPixels.length;
		int height = inputPixels[0].length;
        double []Gx=new double[(int)(2*r)];
        double []Gy=new double[(int)(2*r)];
        int [][]copy=new int[width][height];
        int rs=(int)(r);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++){
                
                copy[x][y]=inputPixels[x][y];
            }
        double sum=0;
        for(int i=-rs;i<=rs;i++){
            
                Gx[i+rs]=Math.pow(Math.E,-i *i/(2*r*r));
                Gy[i+rs]=Math.pow(Math.E,-i *i/(2*r*r));
                sum=sum+Gx[i+rs];
        }
        for(int i=-rs;i<=rs;i++){
                Gx[i+rs]=Gx[i+rs]/sum;
                Gy[i+rs]=Gy[i+rs]/sum;
            }
        
        for (int x = rs; x < width-rs; x++)
            for (int y = rs; y < height-rs; y++){
                int wsum=0;
                for(int i=-rs;i<=rs;i++)
                    wsum+=Gx[i+rs]*copy[x+i][y];
                
                
                inputPixels[x][y]=(int)(wsum);
                if(inputPixels[x][y]<0)inputPixels[x][y]=0;
                if(inputPixels[x][y]>255)inputPixels[x][y]=255;
            }
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++){
                
                copy[x][y]=inputPixels[x][y];
            }
        for (int x = rs; x < width-rs; x++)
            for (int y = rs; y < height-rs; y++){
                int wsum=0;
                for(int i=-rs;i<=rs;i++)
                    wsum+=Gy[i+rs]*copy[x][y+i];
                
                
                inputPixels[x][y]=(int)(wsum);
                if(inputPixels[x][y]<0)inputPixels[x][y]=0;
                if(inputPixels[x][y]>255)inputPixels[x][y]=255;
            }
        
        
 
        
  
		/* Placeholder: invert the intensity values */
        //for (int x = 0; x < width; x++)
			//for (int y = 0; y < height; y++)
				//inputPixels[x][y] = 255-inputPixels[x][y];
		return inputPixels;
	}
	
	
	private static Color[][] load_image(String image_filename){
		BufferedImage inputImage = null;
		try{
			System.err.printf("Reading image from %s\n",image_filename);
			inputImage = ImageIO.read(new File(image_filename));
		} catch(java.io.IOException e){
			ErrorExit("Unable to open %s: %s\n",image_filename,e.getMessage());
		}
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		Color[][] imagePixels = new Color[width][height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				imagePixels[x][y] = new Color(inputImage.getRGB(x,y));
		System.err.printf("Read a %d by %d image\n",width,height);
		return imagePixels;
	}


	
	
	private static void save_image(Color[][] imagePixels, String image_filename){
		int width = imagePixels.length;
		int height = imagePixels[0].length;
		BufferedImage outputImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				outputImage.setRGB(x,y,imagePixels[x][y].getRGB());
		
		try{
			ImageIO.write(outputImage, "png", new File(image_filename));
		}catch(java.io.IOException e){
			ErrorExit("Unable to write %s: %s\n",image_filename,e.getMessage());
		}
		System.err.printf("Wrote a %d by %d image\n",width,height);
	}
	
	
	/* Convert a 2d array of Color objects to a 2d array of intensities, in the range [0,255]
	   by averaging */
	private static int[][] ColoursToIntensities(Color[][] inputPixels){
		int width = inputPixels.length;
		int height = inputPixels[0].length;
		int[][] intensities = new int[width][height];
		for (int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				intensities[x][y] = (inputPixels[x][y].getRed()+inputPixels[x][y].getGreen()+inputPixels[x][y].getBlue())/3;
		return intensities;
	}

	
	private static Color[][] IntensitiesToColours(int[][] intensities){
		int width = intensities.length;
		int height = intensities[0].length;
		Color[][] outputPixels = new Color[width][height];
		for (int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				outputPixels[x][y] = new Color(intensities[x][y],intensities[x][y],intensities[x][y]);
		return outputPixels;
	}



	
	public static void main(String[] args){
		if (args.length < 1){
			System.out.printf("Usage: ImageProcessor205BW <input image> <output name> <radians>\n");
			return;
		}
		
		String input_filename = args[0];
		
		if (!input_filename.toLowerCase().endsWith(".png"))
			ErrorExit("Input file must be a PNG image.\n");
        if (args.length == 3)
            r=Double.parseDouble(args[2]);
		String output_filename = null;
		if (args.length > 1)
			output_filename = args[1];
		else
			output_filename = input_filename.substring(0,input_filename.length()-4)+"_output.png";
			
		

		Color[][] inputPixels = load_image(input_filename);
		
		int[][] inputIntensities = ColoursToIntensities(inputPixels);
				
		int[][] resultIntensities = ProcessImage(inputIntensities);
		
		Color[][] resultPixels = IntensitiesToColours(resultIntensities);
		
		save_image(resultPixels, output_filename);
		
	}
	/* Prints an error message and exits (intended for user errors) */
	private static void ErrorExit(String errorMessage, Object... formatArgs){
		System.err.printf("ERROR: " + errorMessage + "\n",formatArgs);
		System.exit(0);
	}
	/* Throws a runtime error (intended for logic errors) */
	private static void ErrorAbort(String errorMessage, Object... formatArgs){
		throw new Error(String.format(errorMessage,formatArgs));
	}

}