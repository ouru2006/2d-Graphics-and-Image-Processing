/* LSViewer.cpp

   B. Bird - 02/08/2016
*/
#include <iostream>
#include <vector>
#include <cmath>
#include <SDL2/SDL.h>
#include <SDL2/SDL2_gfxPrimitives.h>
#include <stack>
#include "LSystem.h"
#include "matrix.h"
//#include "colourRGB.h"
#include "transformed_renderer.h"

using namespace std;

static int WINDOW_SIZE_X = 800;
static int WINDOW_SIZE_Y = 600;
static const int h=10;


class A3Canvas{
public:

	A3Canvas(LSystem* L){
		LS_iterations = 0;
		this->L_system = L;
	}

	
	void frame_loop(SDL_Renderer* r){
		load_image(r);
		unsigned int last_frame = SDL_GetTicks();
		//unsigned int frame_number = 0;
		ls_string = L_system->GenerateSystemString(LS_iterations);
		cerr << "Drawing with " << LS_iterations << " iterations." << endl;
		cerr << "System string: " << ls_string << endl;
		while(1){

			//cout << "Frame " << frame_number << endl;
			unsigned int current_frame = SDL_GetTicks();
			unsigned int delta_ms = current_frame - last_frame;
			
			SDL_Event e;
			//Handle all queued events
			while(SDL_PollEvent(&e)){
				switch(e.type){
					case SDL_QUIT:
						//Exit immediately
						return;
					case SDL_WINDOWEVENT:
						if(e.window.event==SDL_WINDOWEVENT_RESIZED){
							WINDOW_SIZE_X = e.window.data1;
							WINDOW_SIZE_Y = e.window.data2;
							
							break;
						}
						break;
					case SDL_KEYDOWN:
						//e.key stores the key pressed
						handle_key_down(e.key.keysym.sym);
						ls_string = L_system->GenerateSystemString(LS_iterations);
						cerr << "Drawing with " << LS_iterations << " iterations." << endl;
						cerr << "System string: " << ls_string << endl;
						break;
					default:
						break;
				}
			}
			draw(r,delta_ms);
			
			
		}
		
	}
private:
	SDL_Texture* leaf,*stem,*background;
	SDL_Surface* bmp;
	string ls_string;
	int LS_iterations;
	LSystem* L_system;
	void handle_key_down(SDL_Keycode key){
		if (key == SDLK_UP){
			LS_iterations++;
		}else if (key == SDLK_DOWN){	
			LS_iterations--;
			if (LS_iterations < 0)
				LS_iterations = 0;
		}
	}
	
	
	inline Matrix3 Rotation(float radians){
		Matrix3 M;
		M.identity();
		M(0,0) = M(1,1) = cos(radians);
		M(1,0) = -(M(0,1) = sin(radians));
		return M;
	}
	inline Matrix3 Translation(float tx, float ty){
		Matrix3 M;
		M.identity();
		M(0,2) = tx;
		M(1,2) = ty;
		return M;
	}
	inline Matrix3 Scale(float sx, float sy){
		Matrix3 M;
		M.identity();
		M(0,0) = sx;
		M(1,1) = sy;
		return M;
	}

	void load_image(SDL_Renderer* renderer){
		bmp=SDL_LoadBMP("picture/leaf.bmp");
		leaf=SDL_CreateTextureFromSurface(renderer,bmp);
		SDL_FreeSurface(bmp);
		bmp=SDL_LoadBMP("picture/stem.bmp");
		stem=SDL_CreateTextureFromSurface(renderer,bmp);
		SDL_FreeSurface(bmp);
		bmp=SDL_LoadBMP("picture/background.bmp");
		background=SDL_CreateTextureFromSurface(renderer,bmp);
		SDL_FreeSurface(bmp);
	}
	void draw_leaf(TransformedRenderer& tr,int i){
		float vx[] = {0,1.0 ,1.25,   1,  0,  -1,-1.25,-1};
		float vy[] = {0,0.75,1.75,2.75,4.0,2.75, 1.75,0.75};
		int numVerts = 8;
		

		if(i==0){
			tr.fillPolygon(vx,vy,numVerts, 0,0,0, 30);
			tr.drawPolygon(vx,vy,numVerts, 0,0,0, 30);
			return;
		}
		tr.fillPolygon(vx,vy,numVerts, 64,224,0, 255);
		tr.drawPolygon(vx,vy,numVerts, 64,128,0, 255);
		//SDL_Rect m_pSrcRect={0,0,137,200};
		//SDL_Rect m_pTargetRect={-1,5,20,30};
		//tr.draw_image(leaf,m_pSrcRect,m_pTargetRect);
		
	}

	void draw_stem(TransformedRenderer& tr,int i){

		if(i==0){
			float vx[] = {-0.35,-0.35,0.35,0.35};
			float vy[] = {0,h/2,h/2,0};
			int numVerts = 4;
			tr.fillPolygon(vx,vy,numVerts, 0,0,0, 50);
			tr.drawPolygon(vx,vy,numVerts, 0,0,0, 50);
			return;
		}
		float vx[] = {-0.5,-0.5,0.5,0.5};
		float vy[] = {0,h,h,0};
		int numVerts = 4;
		tr.fillPolygon(vx,vy,numVerts, 139,90,43, 255);
		tr.drawPolygon(vx,vy,numVerts, 139,90,43, 255);
	}

	void draw(SDL_Renderer *renderer, float frame_delta_ms){
		

		//float frame_delta_seconds = frame_delta_ms/1000.0;



		SDL_Rect m_pSrcRect={0,0,800,600};
		SDL_Rect m_pTargetRect={0,0,WINDOW_SIZE_X,WINDOW_SIZE_Y};
		SDL_RenderCopy(renderer,background,&m_pSrcRect,&m_pTargetRect);
		SDL_RenderClear(renderer);

		SDL_RenderCopy(renderer,background,&m_pSrcRect,&m_pTargetRect);

		TransformedRenderer tr(renderer);
		
		Matrix3 viewportTransform;
		viewportTransform.identity();
		viewportTransform *= Translation(WINDOW_SIZE_X/2, WINDOW_SIZE_Y);
		viewportTransform *= Scale(1,-1);
		viewportTransform *= Scale(WINDOW_SIZE_X/100.0,WINDOW_SIZE_Y/100.0);
		
		TransformedRenderer tr_s(renderer);
		Matrix3 viewportTransform_shadow;
		viewportTransform_shadow.identity();
		viewportTransform_shadow *= Translation(WINDOW_SIZE_X/2, WINDOW_SIZE_Y);
		viewportTransform_shadow *= Rotation(-M_PI*89/180);
		viewportTransform_shadow *= Scale(1,-1);
		viewportTransform_shadow *= Scale(WINDOW_SIZE_X/100.0,WINDOW_SIZE_Y/100.0);
		tr_s.set_transform(viewportTransform_shadow);
		
		tr.set_transform(viewportTransform);
		stack<Matrix3> temp_stack;
		stack<Matrix3> temp_stack_s;
		int c;
		bool rotate;
		float r_s=0;
		//Replace this with actual drawing code...
		for(int i=0;i<ls_string.size();i++){
			c=ls_string[i];
			if(c=='L'){
				draw_leaf(tr_s,0);
				draw_leaf(tr,1);
				
			}else if(c=='T'){
				draw_stem(tr_s,0);
				draw_stem(tr,1);
				
				if(rotate){			
					viewportTransform *= Translation(-0, h*sqrt(3)/2);		
					tr.set_transform(viewportTransform);
					viewportTransform_shadow *= Translation(-0, h*sqrt(3)/4);		
					tr_s.set_transform(viewportTransform_shadow);
					rotate=false;							
				}else{							
					viewportTransform *= Translation(0, h);
					tr.set_transform(viewportTransform);
					viewportTransform_shadow *= Translation(0, h/2);
					tr_s.set_transform(viewportTransform_shadow);
				}
			}else if(c=='['){
				temp_stack.push(viewportTransform);
				temp_stack_s.push(viewportTransform_shadow);
			}else if(c==']'){
				viewportTransform=temp_stack.top();
				temp_stack.pop();
				tr.set_transform(viewportTransform);
				
				viewportTransform_shadow=temp_stack_s.top();
				temp_stack_s.pop();
				tr_s.set_transform(viewportTransform_shadow);
			}else if(c=='+'){
				r_s+=0.1;
				rotate=true;
				viewportTransform *= Rotation(-M_PI/6);
				tr.set_transform(viewportTransform);
				viewportTransform_shadow *= Rotation(-M_PI*5/180);
				tr_s.set_transform(viewportTransform_shadow);

			}else if(c=='-'){
				rotate=true;
				viewportTransform *= Rotation(M_PI/6);
				tr.set_transform(viewportTransform);
				viewportTransform_shadow *= Rotation(M_PI*5/180);
				tr_s.set_transform(viewportTransform_shadow);
			}else if(c=='s'){			
				viewportTransform *= Scale(0.9, 0.9);
				tr.set_transform(viewportTransform);
				
				viewportTransform_shadow *= Scale(0.9, 0.9);
				tr_s.set_transform(viewportTransform_shadow);

			}else if(c=='S'){
				viewportTransform *= Scale(1/(0.9), 1/(0.9));
				tr.set_transform(viewportTransform);
				
				viewportTransform_shadow *= Scale(1/(0.9), 1/(0.9));
				tr_s.set_transform(viewportTransform_shadow);
			}else if(c=='h'){
				viewportTransform *= Scale(0.9, 1);
				tr.set_transform(viewportTransform);

				viewportTransform_shadow *= Scale(0.9, 1);
				tr_s.set_transform(viewportTransform_shadow);
			}else if(c=='H'){
				viewportTransform *= Scale(1/(0.9), 1);
				tr.set_transform(viewportTransform);

				viewportTransform_shadow *= Scale(1/(0.9), 1);
				tr_s.set_transform(viewportTransform_shadow);
			}else if(c=='v'){
				viewportTransform *= Scale(1, 0.9);
				tr.set_transform(viewportTransform);

				viewportTransform_shadow *= Scale(1, 0.9);
				tr_s.set_transform(viewportTransform_shadow);
			}else if(c=='V'){
				viewportTransform *= Scale(1, 1/(0.9));
				tr.set_transform(viewportTransform);

				viewportTransform_shadow *= Scale(1, 1/(0.9));
				tr_s.set_transform(viewportTransform_shadow);
			}
		}
	
		SDL_RenderPresent(renderer);
	}
};

int main(int argc, char** argv){

	if (argc < 2){
		cerr << "Usage: " << argv[0] << " <input file>" << endl;
		return 0;
	}
	char* input_filename = argv[1];
	
	LSystem* L = LSystem::ParseFile(input_filename);
	if (!L){
		cerr << "Parsing failed." << endl;
		return 0;
	}

	SDL_Window* window = SDL_CreateWindow("CSC 205 A3",
                              SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED,
                              WINDOW_SIZE_X, WINDOW_SIZE_Y, 
							  SDL_WINDOW_SHOWN|SDL_WINDOW_RESIZABLE);
							  
	SDL_Renderer* renderer = SDL_CreateRenderer(window, -1, 0/*SDL_RENDERER_PRESENTVSYNC | SDL_RENDERER_ACCELERATED*/);

	//Initialize the canvas to solid green
	SDL_SetRenderDrawColor(renderer, 0, 255, 0, 255);
	SDL_RenderClear(renderer);
	SDL_RenderPresent(renderer);
	
	A3Canvas canvas(L);

	canvas.frame_loop(renderer);
	
	delete L;
	
	return 0;
}
