LIB_DIR=./lib_osx
g++ -Wall -o LSViewer LSystem.cpp LSViewer.cpp -framework SDL2 -I. -L${LIB_DIR} -lSDL2_gfx
