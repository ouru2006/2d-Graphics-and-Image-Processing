/* ImageProcessor205.java
   CSC 205 - Spring 2016
   Image Processor Template
   

   B. Bird - 10/15/2015
*/

import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class MedianCut {
    private static int width;
    private static int height;
    private static int Kmax=64;

	public static Color[][] ProcessImage( Color[][] inputPixels){
		width = inputPixels.length;
		height = inputPixels[0].length;
        Color[] Cr=FindRepresentativeColors(inputPixels);

		return QuantizeImage(inputPixels, Cr);
	}
    private static Color[] FindRepresentativeColors(Color[][] I){
    	Color[] Cr;
        Hashtable<Color,Integer> C = new Hashtable<Color,Integer>();
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				if (!C.containsKey(I[x][y])){
					C.put(I[x][y], 1);
				}else{
					int cnt=C.get(I[x][y]);
					C.put(I[x][y], cnt++);
				}
		if(C.size()<=Kmax){
			Iterator<Color> it = C.keySet().iterator();
			int i=0;
			Cr=new Color[C.size()];
	        while(it.hasNext()) {      	
	        	Cr[i]=it.next();
	        	System.out.println(Cr[i]);
	        	i++;
	        }
	        
		}else{
			
			//creat a colorBox list where the boxes will be stored
			ArrayList<ColorBox> colorBoxes = new ArrayList<ColorBox>(); 
			//Create a color box b0 at level 0 that contains all image colors C
			ColorBox b0 = new ColorBox(C,0); 
			colorBoxes.add(b0);
			int k = 1; //we have one box now
			boolean done = false;

			while(k<Kmax && !done){
				ColorBox next = findBoxToSplit(colorBoxes);
				if(next != null){
					ColorBox [] boxes = splitBox(next);
					if(colorBoxes.remove(next)){} //finds and removes an element in one
					//replaced with the two smaller boxes that make it up 
					colorBoxes.add(boxes[0]);
					colorBoxes.add(boxes[1]);	
					k++; //we have one more box
				}else{
					done = true;
				}
			}

			//Determine the average color inside each color box in the list
			Cr = new Color [colorBoxes.size()];
			for(int i=0; i<Cr.length; i++){
				ColorBox cb = (ColorBox)colorBoxes.get(i);
				Cr[i] = averageColor(cb);
			}
			
		}
		return Cr ;
    }
	
    private static Color averageColor(ColorBox bx) {
		Hashtable<Color,Integer> cols = bx.C;
		Iterator<Color> it = cols.keySet().iterator();
		int n=0;
		int rs=0;
		int gs=0;
		int bs=0;
		while(it.hasNext()){
			Color mc = it.next();
			int k=cols.get(mc);
			n+=k;
			rs+=mc.getRed()*k;
			gs+=mc.getGreen()*k;
			bs+=mc.getBlue()*k;
		}
		int avgRed = (int)((float)(rs/n));
		int avgGreen = (int)((float)(gs/n));
		int avgBlue = (int)((float)(bs/n));

		return new Color(avgRed,avgGreen,avgBlue);
	}
	private static ColorBox[] splitBox(ColorBox bx) {
		//Splits the color box b at the median plane perpendicular to its longest
		//dimension and returns a pair of new color boxes.
		int m = bx.level; //store the current 'level'
		int d = findMaxDimension(bx); //the dimension to split along

		//get the median only counting along the longest RGB dimension
		Hashtable<Color, Integer> cols = bx.C;
		Iterator<Color> it = cols.keySet().iterator();
		int c = 0;
		while(it.hasNext()){
			Color mc =  it.next();
			if(d==0) c +=mc.getRed();
			else if(d==1)c+= mc.getGreen();
			else c+=mc.getBlue();
		}

		float median = c/(float)(cols.size());

		//the two Hashmaps to contain all the colours in the original box
		Hashtable<Color, Integer> left = new Hashtable<Color, Integer>();
		Hashtable<Color, Integer> right = new Hashtable<Color, Integer>();

		Iterator<Color> itr = cols.keySet().iterator();
		while(itr.hasNext()){
			Color mc = itr.next();
			//putting each colour in the appropriate box
			int a=0;
			if(d==0) a=mc.getRed();
			else if(d==1)a= mc.getGreen(); 
			else a=mc.getBlue();
			if(a <= median){
				left.put(mc,cols.get(mc));
			}else{
				right.put(mc,cols.get(mc));
			}
		}

		ColorBox [] toReturn = new ColorBox [2];
		toReturn[0] = new ColorBox(left, m+1); //the 'level' has increased 
		toReturn[1] = new ColorBox(right, m+1);

		return toReturn;
	}
	private static int findMaxDimension(ColorBox bx) {
		int [] dims = new int [3];
		//the length of each is measured as the (max value - min value)
		dims[0] = bx.rmax - bx.rmin;
		dims[1] = bx.gmax - bx.gmin;
		dims[2] = bx.bmax - bx.bmin;

		int sizeMax = findMinMax(dims,1);
		if(sizeMax == dims[0]){
			return 0;//red
		}else if(sizeMax == dims[1]){
			return 1;//green
		}else{
			return 2;//blue
		}
	}
	private static int findMinMax(int [] f, int k){
		if(f.length>0){
			int m = f[0];
			for(int i =1; i<f.length; i++){
				//if k is 0 the minimum is required. Otherwise return the maximum. 
				if(k==0){
					if(m>f[i])m=f[i];
				}else{
					if(m<f[i])m=f[i];
				}
			} 
			return m;
		}else{
			return 0;
		}

	}
	private static ColorBox findBoxToSplit(ArrayList<ColorBox> listOfBoxes){
		//Searches listOfBoxes for a box to split and returns this box,
		//or null if no splittable box can be found.
		
		//Let Bs be the set of all color boxes that can be split (i. e., contain at
		//		least 2 different colors):
		ArrayList<ColorBox> Bs = new ArrayList<ColorBox>();

		for(int i = 0; i<listOfBoxes.size(); i++){
			ColorBox cb = (ColorBox)listOfBoxes.get(i);
			//only boxes containing more than one colour can be split 
			if(cb.C.size() > 1){
				Bs.add(cb);
			}	
		}

		if(Bs.size() == 0){
			return null; //a null will trigger the end of the subdividing loop
		}else{

			//use the 'level' of each box to ensure they are divided in the correct order.
			//the box with the lowest level is returned.

			ColorBox minBox = (ColorBox)Bs.get(0);
			int minLevel = minBox.level;

			for(int i=1; i<Bs.size(); i++){
				ColorBox test = (ColorBox)Bs.get(i);
				if(minLevel > test.level){
					minLevel = test.level;
					minBox = test;
				}
			}

			return minBox;

		}

	}
    private static Color[][] QuantizeImage(Color[][] I, Color[] Cr){
    	Color[][] I1 =new Color[width][height];
    	
    	for(int x=0;x<width;x++)
    		for(int y=0;y<height;y++){
    			int n=0;
    			double close=getDistance(I[x][y].getRed()-Cr[0].getRed(),I[x][y].getGreen()-Cr[0].getGreen(),I[x][y].getBlue()-Cr[0].getBlue());
    			for(int i=1;i<Cr.length;i++){
    				double tempd=getDistance(I[x][y].getRed()-Cr[i].getRed(),I[x][y].getGreen()-Cr[i].getGreen(),I[x][y].getBlue()-Cr[i].getBlue());
    				if(close>tempd){
    					close=tempd;
    					n=i;
    				} 					
    			}
    			I1[x][y]=Cr[n];
    		}
        return I1;
    }
    private static double getDistance(int dr,int dg,int db){
    	return Math.sqrt(dr*dr+dg*dg+db*db);
    	
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
	
	public static void main(String[] args){
		if (args.length < 1){
			System.out.printf("Usage: ImageProcessor205BW <input image>\n");
			return;
		}
		
		String input_filename = args[0];
		
		if (!input_filename.toLowerCase().endsWith(".png"))
			ErrorExit("Input file must be a PNG image.\n");
			
		String output_filename = null;
        
		if (args.length > 1)
			output_filename = args[1];
		else
			output_filename = input_filename.substring(0,input_filename.length()-4)+"_output.png";
        if(args.length==3)
            Kmax=Integer.parseInt(args[2]);
		

		Color[][] inputPixels = load_image(input_filename);
		
		
		Color[][] resultPixels = ProcessImage(inputPixels);
		
		save_image(resultPixels, output_filename);
		
	}
	/* Prints an error message and exits (intended for user errors) */
	private static void ErrorExit(String errorMessage, Object... formatArgs){
		System.err.printf("ERROR: " + errorMessage + "\n",formatArgs);
		System.exit(0);
	}



}
class ColorBox{

	int rmin,rmax,gmin,gmax,bmin,bmax;
	Hashtable<Color,Integer> C;
	int level;

	//constructor takes the colours contained by this box and its level of "depth"
	ColorBox(Hashtable<Color,Integer> C, int level){
		this.C = C;
		this.level = level;

		//3 temporary arrays used for getting the min/max of each RGB channel
		int [] reds = new int [C.size()];
		int [] greens = new int [C.size()];
		int [] blues = new int [C.size()];

		Iterator<Color> it = C.keySet().iterator();
		int index = 0;

		while(it.hasNext()){
			Color c = it.next();
			reds[index] = c.getRed();
			greens[index] = c.getGreen();
			blues[index] = c.getBlue();
			index++;
		}

		//we need the min/max to determine which axis to split along
		rmin = findMinMax(reds,0);
		rmax = findMinMax(reds,1);
		gmin = findMinMax(greens,0);
		gmax = findMinMax(greens,1);
		bmin = findMinMax(blues,0);
		bmax = findMinMax(blues,1);
	}
	
	private int findMinMax(int [] f, int k){
		if(f.length>0){
			int m = f[0];
			for(int i =1; i<f.length; i++){
				//if k is 0 the minimum is required. Otherwise return the maximum. 
				if(k==0){
					if(m>f[i])m=f[i];
				}else{
					if(m<f[i])m=f[i];
				}
			} 
			return m;
		}else{
			return 0;
		}

	}

}

