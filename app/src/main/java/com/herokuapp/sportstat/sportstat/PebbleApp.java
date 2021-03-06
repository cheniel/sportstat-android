package com.herokuapp.sportstat.sportstat;

import java.util.UUID;

public class PebbleApp {
    public final static UUID APP_UUID = UUID.fromString("fe649006-bd16-4f05-a0c3-060750535107");
    public final static int MSG_GENERIC_STRING = 0;
    public final static int MSG_ASSIST_COUNT = 1;
    public final static int MSG_TWO_POINT_COUNT = 2;
    public final static int MSG_THREE_POINT_COUNT = 3;
    public final static int MSG_END_GAME = 4;
    public final static int MSG_GAME_CANCELLED = 5;
    public final static int MSG_REQUEST_RESPONSE = 6;
    public final static int MSG_INITIAL_POINT_LOAD = 7;
    public final static int MSG_ATTEMPTED_SHOTS = 8;
}
