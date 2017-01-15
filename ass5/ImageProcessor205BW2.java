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


public class ImageProcessor205BW2 {
    private static double scale=3;
    private static int width;
    private static int height;
	public static int[][] ProcessImage( int [][] inputPixels){
		width = inputPixels.length;
		height = inputPixels[0].length;
        
        int[][] newI=new int[(int)(scale*width)][(int)(scale*height)];
        for (int x = 0; x < (int)(scale*(width-1)); x++)
            for (int y = 0; y < (int)(scale*(height-1)); y++)
                newI[x][y]=Bilinear(inputPixels,(double)x/scale,(double)y/scale);

		/* Placeholder: invert the intensity values */
		//for (int x = 0; x < width; x++)
		//	for (int y = 0; y < height; y++)
		//		inputPixels[x][y] = 255-inputPixels[x][y];

		return newI;
	}
	

    private static int Bilinear(int[][] I, double x, double y){
        int x0=(int)x;
        int y0=(int)y;
        int x1=x0+1;
        int y1=y0+1;
        double xs=x-x0;
        double ys=y-y0;
        double p0=I[x0][y0]*(1-xs)+I[x1][y0]*xs;
        double p1=I[x0][y1]*(1-xs)+I[x1][y1]*xs;
        return (int)(p0*(1-ys)+p1*ys);
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
			System.out.printf("Usage: ImageProcessor205BW <input image> <output name> <scale>\n");
			return;
		}
		
		String input_filename = args[0];
		if (args.length==3)
            scale=Double.parseDouble(args[2]);
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

