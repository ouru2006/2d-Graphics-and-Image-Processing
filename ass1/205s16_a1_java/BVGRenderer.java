/*
 * Ru Ou
 * V00835123
 */

/* BVGRenderer.java

   BVG Renderer

   B. Bird - 01/03/2016
*/

import java.awt.Color;
import java.awt.Point;



public class BVGRenderer implements BVGRendererBase {
	public void CreateCanvas(Point dimensions, Color background_colour, int scale_factor){
		System.out.println("CreateCanvas " + dimensions + background_colour + scale_factor);
		this.scale=2*scale_factor;
		this.width = (int)(dimensions.x*scale);
		this.height =(int)(dimensions.y*scale);
		canvas = new PNGCanvas(width,height);
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				canvas.SetPixel(x,y, background_colour);		
	}
	/*
	 *by dot product to normal line, to find the next closest point
	 *if slope is over 45 degree, then swift x and y    
	 */
	public void RenderLine(Point endpoint1, Point endpoint2, Color colour, int thickness){
		Point point1=new Point((int)(endpoint1.x*scale),(int)(endpoint1.y*scale));
		Point point2=new Point((int)(endpoint2.x*scale),(int)(endpoint2.y*scale));	
		int Lx=point1.x-point2.x;
		int Ly=point1.y-point2.y;
		int x,y,x1,y1;
		boolean swift=false;
		if(Math.abs(Lx)>=Math.abs(Ly)){
			x=point1.x; y=point1.y;
			x1=point2.x;y1=point2.y;
		}else{
			x=point1.y; y=point1.x;
			x1=point2.y;y1=point2.x;
			Lx=x-x1;       Ly=y-y1;
			swift=true;
		}
		boolean func1,func2,func3,func4;
		func1=false;func2=false;func3=false;func4=false;
		if(x>=x1&&y>=y1) func1=true;
		else if(x>=x1&&y<=y1) func2=true;
		else if(x<=x1&&y<=y1) func3=true;
		else func4=true;
		F=0;
		
		while((x>=x1&&y>=y1)||(x>=x1&&y<=y1)||(x<=x1&&y<=y1)||(x<=x1&&y>=y1)){			
			if(!swift)DrawBox(x,y,thickness,colour);			
			else DrawBox(y,x,thickness,colour);
				
			if(x==x1&&y==y1) break;
			//(-,-) 
			if(x>=x1&&y>=y1&&func1){		
				if(Math.abs(F-Ly)<Math.abs(F-Ly+Lx)){
					F=F-Ly;
					x--;			
				}else{
					F=F-Ly+Lx;
					x--;
					y--;				
				}		
		 }
		//(-,+)
			else if(x>=x1&&y<=y1&&func2){			
				if(Math.abs(F-Ly)<=Math.abs(F-Ly-Lx)){
					F=F-Ly;
					x--;			
				}else{
					F=F-Ly-Lx;
					x--;
					y++;			
				}
			}			
			//(+,+)
			else if(x<=x1&&y<=y1&&func3){
				if(Math.abs(F+Ly)<Math.abs(F+(Ly-Lx))){
					F=F+Ly;
					x++;					
				}else{
					F=F+Ly-Lx;
					x++;
					y++;			
				}
			}			
		//(+,-)
			else if(x<=x1&&y>=y1&&func4){
				if(Math.abs(F+Ly)<Math.abs(F+Ly+Lx)){	
					F=F+Ly;
					x++;				
				}else{
					F=F+Ly+Lx;	
					x++;
					y--;				
				}
			}
			else break;			
		}
		System.out.println("RenderLine " + endpoint1 + endpoint2 + colour + thickness);
	}
	/*
	 * find the closest point to center by the function F=x^2+y^2-r^2
	 * draw the right part and left part together 
	 */
	public void RenderCircle(Point center, int radius, Color line_colour, int line_thickness){
		center.x=(int)(center.x*scale);
		center.y=(int)(center.y*scale);
		radius=(int)(radius*scale);
		
		int x=0;
		int y=radius;
		int xL=0;
		int yL=-radius;
		F=0;
		boolean position=true;		
		do{
			DrawBox(center.x+x,(center.y+y),line_thickness,line_colour);
			DrawBox(center.x+xL,(center.y+yL),line_thickness,line_colour);

			if(Math.abs(F+2*x+1)<=Math.abs(F+2*x-2*y+2)&&position){
				F=F+2*x+1;
				x++;			
				xL--;
			}else if(Math.abs(F-2*y+1)>=Math.abs(F+2*x-2*y+2)){
				F=F+(2*x-2*y+2);
				x++;
				y--;
				xL--;
				yL++;
			}else if(Math.abs(F-2*y+1)<=Math.abs(F-2*x-2*y+2)) {
				F=F-2*y+1;
				y--;
				yL++;
				position=false;
			}else if(Math.abs(F-2*x-2*y+2)<=Math.abs(F-2*x+1)){
				F=F-2*x-2*y+2;	
				x--;
				y--;
				xL++;	
				yL++;
			}else{
				F=F-2*x+1;	
				x--;
				xL++;
			}
			
		}while(x>0);	
		
		System.out.println("RenderCircle " + center + radius + line_colour + line_thickness);
	}
	/*
	 * fill the circle by finding path
	 * then call the renderCircle function
	 */
	public void RenderFilledCircle(Point center, int radius, Color line_colour, int line_thickness, Color fill_colour){
		System.out.println("RenderFilledCircle " + center + radius + line_colour + line_thickness + fill_colour);
		Point fill_center=new Point((int)(center.x*scale),(int)(center.y*scale));
		int fill_radius=(int)(radius*scale);	
		int x=0;
		int y=fill_radius;
		int xL=0;
		F=0;
		int fill_y=fill_radius;
		boolean position=true;
		
		do{	
			if(fill_y>y){
				fill_y=y;
				for(int i=xL+1;i<x;i++)canvas.SetPixel((fill_center.x+i),(fill_center.y+fill_y),fill_colour);
				}
			if(Math.abs(F+2*x+1)<=Math.abs(F+2*x-2*y+2)&&position){
				F=F+2*x+1;
				x++;			
				xL--;
			}else if(Math.abs(F-2*y+1)>=Math.abs(F+2*x-2*y+2)){
				F=F+(2*x-2*y+2);
				x++;
				y--;
				xL--;
			}else if(Math.abs(F-2*y+1)<=Math.abs(F-2*x-2*y+2)) {
				F=F-2*y+1;
				y--;
				position=false;
			}else if(Math.abs(F-2*x-2*y+2)<=Math.abs(F-2*x+1)){
				F=F-2*x-2*y+2;	
				x--;
				y--;
				xL++;
			}else{
				F=F-2*x+1;	
				x--;
				xL++;					
			}		
		}while(x>=0&&y>=-fill_radius);
		RenderCircle(center,radius,line_colour,line_thickness);
	}
	
	/*
	 *fill the triangle, by determine if the point is inside the triangle
	 *and call the renderLine function to draw the three sides 
	 */
	public void RenderTriangle(Point point1, Point point2, Point point3, Color line_colour, int line_thickness, Color fill_colour){
		Point fill_point1=new Point((int)(point1.x*scale),(int)(point1.y*scale));
		Point fill_point2=new Point((int)(point2.x*scale),(int)(point2.y*scale));
		Point fill_point3=new Point((int)(point3.x*scale),(int)(point3.y*scale));
		int Ax=fill_point2.x-fill_point1.x;
		int Ay=fill_point2.y-fill_point1.y;
		int Bx=fill_point3.x-fill_point2.x;
		int By=fill_point3.y-fill_point2.y;
		int Cx=fill_point3.x-fill_point1.x;
		int Cy=fill_point3.y-fill_point1.y;
		//n=(Ay,-Ax)
		int max_x;
		int max_y;
		int min_x;
		int min_y;
		if(fill_point1.x>=fill_point2.x){
			if(fill_point3.x>=fill_point1.x){max_x=fill_point3.x;min_x=fill_point2.x;}
			else {
				max_x=fill_point1.x;
				if(fill_point2.x>fill_point3.x)min_x=fill_point3.x;
				else min_x=fill_point2.x;
			}
		}	
		else{
			if(fill_point3.x>=fill_point2.x){max_x=fill_point3.x;min_x=fill_point1.x;}
			else {
				max_x=fill_point2.x;
				if(fill_point3.x>fill_point1.x)min_x=fill_point1.x;
				else min_x=fill_point3.x;
			}
			
		}
		if(fill_point1.y>=fill_point2.y){
			if(fill_point3.y>=fill_point1.y){max_y=fill_point3.y;min_y=fill_point2.y;}
			else {
				max_y=fill_point1.y;
				if(fill_point2.y>=fill_point3.y)min_y=fill_point3.y;
				else min_y=fill_point2.y;
			}
		}	
		else{
			if(fill_point3.y>=fill_point2.y){max_y=fill_point3.y;min_y=fill_point1.y;}
			else {
				max_y=fill_point2.y;
				if(fill_point3.y<=fill_point1.y)min_y=fill_point3.y;
				else min_y=fill_point1.y;
			}
		}
		
		for(int i=min_x+1;i<max_x;i++)
			for(int j=min_y+1;j<max_y;j++)
				if(((((i-fill_point1.x)*Ay-(j-fill_point1.y)*Ax)<0&&(Cx*Ay-Cy*Ax)<0)||(((i-fill_point1.x)*Ay-(j-fill_point1.y)*Ax)>0&&(Cx*Ay-Cy*Ax)>0))
				&&((((i-fill_point1.x)*Cy-(j-fill_point1.y)*Cx)<0&&(Ax*Cy-Ay*Cx)<0)||(((i-fill_point1.x)*Cy-(j-fill_point1.y)*Cx)>0&&(Ax*Cy-Ay*Cx)>0))
				&&((((i-fill_point2.x)*By-(j-fill_point2.y)*Bx)<0&&(-Ax*By+Ay*Bx)<0)||(((i-fill_point2.x)*By-(j-fill_point2.y)*Bx)>0&&(-Ax*By+Ay*Bx)>0)))
					canvas.SetPixel(i,j,fill_colour);		
			
		RenderLine(point1,point2,line_colour,line_thickness);
		RenderLine(point2,point3,line_colour,line_thickness);
		RenderLine(point1,point3,line_colour,line_thickness);
		System.out.println("RenderTriangle " + point1 + point2 + point3 + line_colour + line_thickness + fill_colour);
	}
	
	
	/*
	 * point D in triangle, AD=m1AB+m2AC; 
	 * m3=1-m1-m2
	 * */
	public void RenderGradientTriangle(Point point1, Point point2, Point point3, Color line_colour, int line_thickness, Color colour1, Color colour2, Color colour3){
		Point fill_point1=new Point((int)(point1.x*scale),(int)(point1.y*scale));
		Point fill_point2=new Point((int)(point2.x*scale),(int)(point2.y*scale));
		Point fill_point3=new Point((int)(point3.x*scale),(int)(point3.y*scale));
		int Ax=fill_point2.x-fill_point1.x;
		int Ay=fill_point2.y-fill_point1.y;
		int Bx=fill_point3.x-fill_point2.x;
		int By=fill_point3.y-fill_point2.y;
		int Cx=fill_point3.x-fill_point1.x;
		int Cy=fill_point3.y-fill_point1.y;
		//n=(Ay,-Ax)
		int max_x;
		int max_y;
		int min_x;
		int min_y;
		if(fill_point1.x>=fill_point2.x){
			if(fill_point3.x>=fill_point1.x){max_x=fill_point3.x;min_x=fill_point2.x;}
			else {
				max_x=fill_point1.x;
				if(fill_point2.x>fill_point3.x)min_x=fill_point3.x;
				else min_x=fill_point2.x;
			}
		}	
		else{
			if(fill_point3.x>=fill_point2.x){max_x=fill_point3.x;min_x=fill_point1.x;}
			else {
				max_x=fill_point2.x;
				if(fill_point3.x>fill_point1.x)min_x=fill_point1.x;
				else min_x=fill_point3.x;
			}
			
		}
		if(fill_point1.y>=fill_point2.y){
			if(fill_point3.y>=fill_point1.y){max_y=fill_point3.y;min_y=fill_point2.y;}
			else {
				max_y=fill_point1.y;
				if(fill_point2.y>=fill_point3.y)min_y=fill_point3.y;
				else min_y=fill_point2.y;
			}
		}	
		else{
			if(fill_point3.y>=fill_point2.y){max_y=fill_point3.y;min_y=fill_point1.y;}
			else {
				max_y=fill_point2.y;
				if(fill_point3.y<=fill_point1.y)min_y=fill_point3.y;
				else min_y=fill_point1.y;
			}
		}
		
		float m1;
		float m2;
		float m3;
	
		for(int i=min_x+1;i<max_x;i++)
			for(int j=min_y+1;j<max_y;j++){
				
					m1=(float)(Cy*(i-fill_point1.x)-Cx*(j-fill_point1.y))/(Ax*Cy-Ay*Cx);
					m2=(float)(-Ay*(i-fill_point1.x)+Ax*(j-fill_point1.y))/(Ax*Cy-Ay*Cx);
					m3=(float)(1-m1-m2);	
					if(m3<=1&&m3>=0&&m1<=1&&m1>=0&&m2<=1&&m2>=0){		
						System.out.println(m1+" "+m2+" "+m3);	
						Color fill_Gradient=new Color((int)(m1*colour1.getRed()+m2*colour2.getRed()+m3*colour3.getRed()),(int)(m1*colour1.getGreen()+m2*colour2.getGreen()+m3*colour3.getGreen()),(int)(m1*colour1.getBlue()+m2*colour2.getBlue()+m3*colour3.getBlue()));	
			
						canvas.SetPixel(i,j,fill_Gradient);	
					}
				}
						
		RenderLine(point1,point2,line_colour,line_thickness);
		RenderLine(point2,point3,line_colour,line_thickness);
		RenderLine(point1,point3,line_colour,line_thickness);
		//System.out.println("RenderGradientTriangle " + point1 + point2 + point3 + line_colour + line_thickness + colour1 + colour2 + colour3);
	}
	/*
	 *average a group of 4 pixels to each pixel
	 */
	public void AntiAliasing(){
		image=new PNGCanvas(width/2,height/2);
		for (int y = 0; y < height; y=y+2)
			for (int x = 0; x < width; x=x+2)		
					image.SetPixel(x/2, y/2, new Color((int)((canvas.GetPixel(x, y).getRed()+canvas.GetPixel(x+1, y).getRed()+canvas.GetPixel(x, y+1).getRed()+canvas.GetPixel(x+1, y+1).getRed())/4),
							(int)((canvas.GetPixel(x, y).getGreen()+canvas.GetPixel(x+1, y).getGreen()+canvas.GetPixel(x, y+1).getGreen()+canvas.GetPixel(x+1, y+1).getGreen())/4),
							(int)((canvas.GetPixel(x, y).getBlue()+canvas.GetPixel(x+1, y).getBlue()+canvas.GetPixel(x, y+1).getBlue()+canvas.GetPixel(x+1, y+1).getBlue())/4)));			
		return;
	}
	public void DrawBox(int x, int y, int thickness, Color color){
		int th=thickness/2;
		for(int i=x-th;i<x+(thickness-th);i++)
			for(int j=y-th;j<y+(thickness-th);j++)canvas.SetPixel(i,j,color);
		return;
	}
	public void SaveImage(String filename){
		AntiAliasing();
		image.SaveImage(filename);
	}
	
	private int width,height;
	private PNGCanvas canvas;
	private PNGCanvas image;
	private double scale;
	private int F;
}