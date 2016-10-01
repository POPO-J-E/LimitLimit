package com.server;

import java.util.HashMap;
import java.util.Map;

public class Config 
{
	//when the websocket http handshake complete 
	public static final int  HANDSHAKE_COMPLETE_SUCCESS = 1;
	public static final int  LOGIN = 2;
	public static final int  LOGIN_DONE = 3;
	public static final int  LOGIN_FAIL = 4;
	public static final int  NEW_USER_LOGIN_DONE = 5;
	public static final int  DISCONNECT = 6;
	public static final int  USER_DISCONNECTED = 7;
	public static final int  PLAY = 8;
	public static final int  PLAY_DONE = 9;
	public static final int  USER_PLAY_DONE = 10;
	public static final int  ALL_PLAYS_DONE = 11;
	public static final int  CHOOSE_WINNER = 12;
	public static final int  CHOOSE_WINNER_DONE = 13;
	public static final int  WINNER_CHOOSED = 14;
	public static final int  NEW_TURN = 15;
	public static final int  NEW_TURN_WINNER = 16;
	public static final int  ERROR = 17;
}
