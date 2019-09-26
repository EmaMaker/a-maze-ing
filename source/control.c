#include "control.h"
#include "player.h"
#include "vars.h"
#include "hud.h"

#include <grrlib.h>
#include <wiiuse/wpad.h>

// Needed for gettime and ticks_to_millisecs
#include <ogc/lwp_watchdog.h>

void updateControls(){

    /*WIIMOTE INTENDED TO BE HELD HORIZONTALLY, WITH NUNCHUK PORT IN THE RIGHT HAND*/
    updatePlayingPlayers();

    for(int i = 0; i < 4; i++){
        if(pPlaying[i]){
            wpadHD[i][0] = WPAD_ButtonsHeld(i);
            wpadHD[i][1] = WPAD_ButtonsHeld(i);

            if((wpadHD[i][0] & WPAD_BUTTON_RIGHT) &&  getCurrentTime() - MOVE_TOFFSET >= pMoveCounter[i]) {
                //Go Up
                if(pPos[i][1] - 1 > 0 && todraw[ pPos[i][0] ][ pPos[i][1]-1 ] != 1){
                    pPos[i][1]--;
                    pMoveCounter[i] = getCurrentTime();
                } 
            }

            if(wpadHD[i][0] & WPAD_BUTTON_DOWN &&  getCurrentTime() - MOVE_TOFFSET >= pMoveCounter[i]) {
                //Go Right
                if(pPos[i][0] + 1 < w_GRID && todraw[ pPos[i][0]+1 ][ pPos[i][1] ] != 1) {
                    pPos[i][0]++;
                    pMoveCounter[i] = getCurrentTime();
                }
            }

            if(wpadHD[i][0] & WPAD_BUTTON_LEFT &&  getCurrentTime() - MOVE_TOFFSET >= pMoveCounter[i]) {
                //Go Down
                if(pPos[i][1] + 1 < h_GRID && todraw[ pPos[i][0] ][ pPos[i][1]+1 ] != 1) {
                    pPos[i][1]++;
                    pMoveCounter[i] = getCurrentTime();
                }
            }
            
            if(wpadHD[i][0] & WPAD_BUTTON_UP &&  getCurrentTime() - MOVE_TOFFSET >= pMoveCounter[i]) {
                //Go Left
                if(pPos[i][0] - 1 > 0 && todraw[ pPos[i][0]-1 ][ pPos[i][1] ] != 1) {
                    pPos[i][0]--;
                    pMoveCounter[i] = getCurrentTime();
                }
            }
        }
    }
}

void updatePlayingPlayers(){
    for(int i = 0; i < 4; i++){
        wpadHD[i][0] = WPAD_ButtonsHeld(i);
        wpadHD[i][1] = WPAD_ButtonsHeld(i);

        if(!pPlaying[i] && wpadHD[i][0] & WPAD_BUTTON_PLUS) {
            if(pPlaying[i]) pPlaying[i] = showLeaveMenu(i);
            else pPlaying[i] = true;
        }
    }
}

u32 getCurrentTime(){
    return ticks_to_millisecs(gettime());
}

void quitGame(){

}