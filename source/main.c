/*===========================================
        GRRLIB (GX Version)
        - Template Code -

        Minimum Code To Use GRRLIB
============================================*/
#include <grrlib.h>
#include <wiiuse/wpad.h>

#include <stdlib.h>
#include <math.h>
#include <stdbool.h>
#include <time.h>

#include "maze.h"
#include "vars.h"
#include "control.h"
#include "player.h"
#include "hud.h"

void draw();
void update();

u32 t = 0;

int main(int argc, char **argv) {

        // Initialise the Graphics & Video subsystem
        GRRLIB_Init();

        // Initialise the Wiimotes
        WPAD_Init();
        
        initVars();
        initMazeGrid();
        initFonts();
        firstSetupPlayers(1);

        startNewLevel();

        // Loop forever
        while(1) {
                WPAD_ScanPads();  // Scan the Wiimotes

                // If [HOME] was pressed on the first Wiimote, break out of the loop
                if (WPAD_ButtonsDown(0) & WPAD_BUTTON_HOME && WPAD_ButtonsDown(1) & WPAD_BUTTON_HOME &&
                        WPAD_ButtonsDown(2) & WPAD_BUTTON_HOME && WPAD_ButtonsDown(3) & WPAD_BUTTON_HOME) break;

                update();
                draw();
                
                // Render the frame buffer to the TV
                GRRLIB_Render();  
        }

        GRRLIB_FreeTexture(tex_BMfont3);
        GRRLIB_FreeTexture(tex_BMfont5);
        GRRLIB_Exit(); // Be a good boy, clear the memory allocated by GRRLIB

        exit(0);  // Use exit() to exit a program, do not use 'return' from main()
}

void draw(){
        // Clear the screen
        GRRLIB_FillScreen(GRRLIB_BLACK);
        
        showMaze();
        showPlayer();
        showScores();
}

void update(){
        updateControls();
        updatePlayers();
}