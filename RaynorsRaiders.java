package javabot.RaynorsRaiders;

/**
 * Example of a Java AI Client that does nothing.
 */
import java.awt.Point;

import javabot.model.*;
import javabot.types.*;
import javabot.*;
import javabot.types.OrderType.OrderTypeTypes;
import javabot.types.UnitType.UnitTypes;
import javabot.util.BWColor;

public class RaynorsRaiders implements BWAPIEventListener {

	int homePositionX, homePositionY, mainMins;
	boolean debgFlag = false;
	
	/* BroodWar API Harness*/
	JNIBWAPI bwapi;
	
	/* Name AIs here */
	CoreReactive ai_core;     //Active
	ManagerBuild ai_builder;  //Active
	ManagerWorkers ai_worker; //Passive
	
	public static void main(String[] args) {
		new RaynorsRaiders();
	}
	
	public RaynorsRaiders() {
		bwapi = new JNIBWAPI(this);
		bwapi.start();
		
		/* Construct builders */
		ai_core = new CoreReactive();
		ai_builder = new ManagerBuild();
		ai_worker = new ManagerWorkers();
		
		/* Send AI Pointers to all the AIs (this is the "second" constructor */
		ai_builder.AILinkManagerBuild(bwapi, ai_core);
		ai_core.AILinkCoreReactive(bwapi, ai_builder);
		ai_core.AILinkCoreReactive(bwapi, ai_builder);
	} 
	
	
	public void connected() {
		bwapi.loadTypeData();
	}
	
	public void gameStarted() {
		System.out.println("In RaynorsRaiders");
		System.out.println("Game Started");

		// allow me to manually control units during the game
		bwapi.enableUserInput();
		
		// set game speed to 30 (0 is the fastest. Tournament speed is 20)
		// You can also change the game speed from within the game by "/speed X" command.
		bwapi.setGameSpeed(30);
		
		// analyze the map
		bwapi.loadMapData(true);
	}
	public void gameUpdate() 
	{
		// This sets up the base location
		if (bwapi.getFrameCount() == 1) {
			ai_builder.captureBaseLocation();
		}
		
		/*
		for(Unit u : bwapi.getAllUnits())
		{
			bwapi.drawCircle(u.getX(), u.getY(), 5, BWColor.RED, true, false);
		}*/
		
		// Call actions every 30 frames
		if (bwapi.getFrameCount() % 30 == 0) 
		{
			ai_core.checkUp();
			ai_builder.construct();
		}
		// Draw debug information on screen
		if (debgFlag)
			drawDebugInfo();	
	}
	
	public void drawDebugInfo() {
		for (Unit u : bwapi.getMyUnits())  
		{
			if (u.isUnderAttack()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.RED, false, false);
			else if (u.isGatheringMinerals()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.BLUE, false, false);
			else if (u.isGatheringGas()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.GREEN, false, false);
			else if (u.isAttacking()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.ORANGE, false, false);
			
			
			bwapi.drawLine(u.getX(), u.getY(), u.getTargetX(), u.getTargetY(), BWColor.WHITE, false);
		}
	}
	
	public void gameEnded() { }
	public void keyPressed(int keyCode) 
	{
		if (keyCode == 66 ) //if equals b toggle debgFlag
			debgFlag = !debgFlag;
	}
	public void matchEnded(boolean winner) { }
	public void nukeDetect(int x, int y) { }
	public void nukeDetect() { }
	public void playerLeft(int id) { }
	public void unitCreate(int unitID) { }
	public void unitDestroy(int unitID) { }
	public void unitDiscover(int unitID) { }
	public void unitEvade(int unitID) { }
	public void unitHide(int unitID) { }
	public void unitMorph(int unitID) { }
	public void unitShow(int unitID) { }
}
