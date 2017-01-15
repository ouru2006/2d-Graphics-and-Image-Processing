/* ImageProcessor205.java
   CSC 205 - Spring 2016
   Image Processor Template
   

   B. Bird - 10/15/2015
*/

import java.awt.Color;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;


public class EdgeSharpening {
    private static int[] h;
    private static double f[];
    public static int[][] ProcessImage( int [][] inputPixels){
        int width = inputPixels.length;
        int height = inputPixels[0].length;
        int[][] sharpen={
            {0, 1,  0},
            {1, -4, 1},
            {0, 1,  0}
        };
        int [][]copy=new int[width][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                copy[x][y]=inputPixels[x][y];
        
        
        for (int x = 1; x < width-1; x++)
            for (int y = 1; y < height-1; y++){
                int sum=0;
                for(int i=-1;i<=1;i++)
                    for(int j=-1;j<=1;j++){
                        sum=sum+sharpen[i+1][j+1]*copy[x+i][y+i];
                    }

                    
                inputPixels[x][y]=copy[x][y]-(int)(w*sum);
                
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

    private static double w=1;

	
	public static void main(String[] args){
		if (args.length < 1){
			System.out.printf("Usage: ImageProcessor205BW <input image> <output name> <w>\n");
			return;
		}
        if (args.length==3){
            w=Double.parseDouble(args[2]);
        }
		String input_filename = args[0];
		
		if (!input_filename.toLowerCase().endsWith(".png"))
			ErrorExit("Input file must be a PNG image.\n");
			
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

