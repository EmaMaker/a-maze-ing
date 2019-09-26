#include "vars.h"
#include "hud.h"
#include "player.h"
#include <grrlib.h>
#include <grrlib/GRRLIB_private.h>

#include <wiiuse/wpad.h>
#include "gfx/BMfont5.h"
#include "gfx/BMfont3.h"
#include "gfx/BMfont3.c"

void initFonts(){
    tex_BMfont5 = GRRLIB_LoadTexture(BMfont5);
    GRRLIB_InitTileSet(tex_BMfont5, 8, 16, 0);
    tex_BMfont3 = GRRLIB_LoadTexture(BMfont3);
    GRRLIB_InitTileSet(tex_BMfont3, 16, 16, 16);
}

void showScores(){
    for(int i = 0; i < 4; i++){
        int x, y;
        switch(i){
            case 0:
                x = HUD_P_XOFF;
                y = HUD_P_YOFF;
            break;
            case 1:
                x = HUD_P_XOFF;
                y = HUD_P_YOFF * 3;
            break;
            case 2:
                x = M_XOFF + MWIDTH;
                y = HUD_P_YOFF;
            break;
            case 3:
                x = M_XOFF + MWIDTH;
                y = HUD_P_YOFF * 3;
            break;
        }
               
        if(i > 1) x -= PHD_OFFSET;

        if(pPlaying[i]){
            GRRLIB_Printf(x, y, tex_BMfont5, GRRLIB_LIME, 1, "G%d: %d", i+1, pScores[i]);
        }else{
            GRRLIB_Printf(x, y, tex_BMfont5, GRRLIB_LIME, 1, "G%d:N.P.", i);
        }

        if(nPlayersPlaying() < 4){
            GRRLIB_Printf(MWIDTH / 2, 20, tex_BMfont5, GRRLIB_AQUA, 1, "Press + enter the game");
        }
        GRRLIB_Printf(M_XOFF / 2, SHEIGHT-23, tex_BMfont5, GRRLIB_AQUA, 1, "In-Game Players: Press + to exit the game |-| Home button to homebrew");

    }
}

// Return true if the player has decided to leave
bool showLeaveMenu(int player){
    u32 wdown = 0;
    GRRLIB_Printf(MWIDTH / 2, 20, tex_BMfont5, GRRLIB_AQUA, 1, "Press + enter the game");
    while(1){
        wdown = WPAD_ButtonsDown(player);
        //leaving
        if(wdown & WPAD_BUTTON_PLUS) return false;
        //not leaving
        if(wdown & WPAD_BUTTON_MINUS) return true;
    }
}

void showPlayerNames(){
}