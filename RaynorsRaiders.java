package javabot.RaynorsRaiders;

/**
 * Example of a Java AI Client that does nothing.
 */
import java.awt.Point;
import java.util.LinkedList;

import javabot.model.*;
import javabot.types.*;
import javabot.*;
import javabot.types.OrderType.OrderTypeTypes;
import javabot.types.UnitType.UnitTypes;
import javabot.util.BWColor;

public class RaynorsRaiders implements BWAPIEventListener 
{

	boolean debgFlag = false;
	boolean healthFlag = false;
	
	/* Master unit list */
	LinkedList<Unit> masterUnitList = new LinkedList<Unit>();
	
	
	/* BroodWar API Harness*/
	JNIBWAPI bwapi;

	CoreReactive 		coreReactive;
	CoreBaby			coreBaby;
	ManagerBuild 		managerBuild;
	ManagerWorkers		managerWorkers;
	ManagerMilitary     managerMilitary;

	public static void main(String[] args) 
	{
		new RaynorsRaiders();
	}
	
	public RaynorsRaiders() 
	{
		System.out.println("TOP of RR");
		bwapi = new JNIBWAPI(this);
		
		/* Construct builders */
		coreReactive = new CoreReactive();
		managerBuild = new ManagerBuild();
		managerWorkers = new ManagerWorkers();
		managerMilitary = new ManagerMilitary();
		coreBaby = new CoreBaby();
		
		/* Send AI Pointers to all the AIs (this is the "second" constructor */
		coreReactive.AILink(bwapi, coreReactive, coreBaby, managerBuild, managerMilitary, managerWorkers);
		coreBaby.AILink(bwapi, coreReactive, coreBaby, managerBuild, managerMilitary, managerWorkers);
		managerBuild.AILink(bwapi, coreReactive, coreBaby, managerBuild, managerMilitary, managerWorkers);
		managerMilitary.AILink(bwapi, coreReactive, coreBaby, managerBuild, managerMilitary, managerWorkers);
		managerWorkers.AILink(bwapi, coreReactive, coreBaby, managerBuild, managerMilitary, managerWorkers);
		
		bwapi.start();
	} 
	
	
	public void connected()
	{
		bwapi.loadTypeData();
	}
	
	public void gameStarted()
	{
		System.out.println("In RaynorsRaiders");
		System.out.println("Game Started");
		

		// allow me to manually control units during the game
		bwapi.enableUserInput();
		
		// set game speed to 30 (0 is the fastest. Tournament speed is 20)
		// You can also change the game speed from within the game by "/speed X" command.
		bwapi.setGameSpeed(30);
		
		// analyze the map
		System.out.println("Analyzing map... Please wait");
		bwapi.loadMapData(true);
		System.out.println("Map Analyzed");
		
		
		
		// Setup master unit list
		masterUnitList.clear();
		for (Unit u : bwapi.getMyUnits())
		{
			masterUnitList.add(u);
		}
		System.out.println("Size of master list is " + masterUnitList.size());
		
		coreBaby.setup();
		coreReactive.setup();
		managerMilitary.setup();
		managerBuild.setup();
		managerWorkers.setup();
		System.out.println("Game setup complete");
		
	}
	
	
	public void gameUpdate() 
	{
		Integer frameCount = bwapi.getFrameCount();
		
		 //Draw debug information on screen
		if (debgFlag)
			drawDebugInfo();
		
		// This sets up the base location
		if (frameCount == 1)
		{		
			managerBuild.captureBaseLocation();
			managerMilitary.setHomePosition();
			managerMilitary.scoutOperation();
			if( managerBuild.baseSetup() != 1)
			{
				//Throw error here
				System.out.println("ERROR: BaseSetup does not equal 1");
			}
			for (Unit unit : bwapi.getMyUnits()) 
			{
				if (unit.getTypeID() == UnitTypes.Terran_SCV.ordinal()) 
				{
					managerWorkers.addWorker(unit.getID());
				}
			}
		}
		
		// Call actions every 30 frames
		if (frameCount % 30 == 0) 
		{
			managerWorkers.handleIdle();
			coreBaby.checkUp();
			coreReactive.checkUp();
			managerBuild.checkUp();
		}	
	}
	
	public void drawDebugInfo() 
	{
		System.out.println("Start debug");
		bwapi.drawText(0, 0, "Home base location is ( " + managerBuild.homePositionX + ", "
				+ managerBuild.homePositionY + ")", true);
		bwapi.drawText(200, 0, "Home base location is ( " + managerBuild.getStartLocation().getX() + ", "
				+ managerBuild.getStartLocation().getY() + ")", true);

		bwapi.drawHealth(healthFlag);
		for (Unit u : bwapi.getMyUnits())  
		{
			if (u.isUnderAttack()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.RED, false, false);
			else if (u.isGatheringMinerals()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.BLUE, false, false);
			else if (u.isGatheringGas()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.GREEN, false, false);
			else if (u.isAttacking()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.ORANGE, false, false);
			
			
			bwapi.drawLine(u.getX(), u.getY(), u.getTargetX(), u.getTargetY(), BWColor.WHITE, false);
		}
		System.out.println("Here");
		managerWorkers.debug();

		System.out.println("End debug");
	}
	
	
	public void gameEnded() 
	{
		System.out.println("RaynorsRaiders: Game Ended");
	}
	public void keyPressed(int keyCode) 
	{
		if (keyCode == 66 ) //if equals b toggle debgFlag
			debgFlag = !debgFlag;
		else if (keyCode == 72) // press h to toggle health
			healthFlag = !healthFlag;
	}
	public void matchEnded(boolean winner) 
	{
		System.out.println("RaynorsRaiders: Match Ended");
		System.out.println("RaynorsRaiders: Did we win? " + winner);
	}
	public void nukeDetect(int x, int y) { }
	public void nukeDetect() { }
	public void playerLeft(int id) { }
	public void unitCreate(int unitID) 
	{
		bwapi.printText("Unit Created " + String.valueOf(unitID));
		masterUnitList.add(bwapi.getUnit(unitID));
		System.out.println("creating type id is " + bwapi.getUnit(unitID).getTypeID());
		if (bwapi.getUnit(unitID).getTypeID() == UnitTypes.Terran_SCV.ordinal()) 
			managerWorkers.addWorker(unitID);
	}
	public void unitDestroy(int unitID)
	{
		System.out.println("In unit destroyed");
		bwapi.printText("Unit Destroyed " + String.valueOf(unitID));
		
		for (Unit u : masterUnitList)
		{
			if (u.getID() == unitID)
			{
				if (u.getTypeID() == UnitTypes.Terran_SCV.ordinal())
					managerWorkers.removeWorker(unitID);
				masterUnitList.remove(u);
				System.out.println("Remove succesfull");
			}
		}
	}
	public void unitDiscover(int unitID) { }
	public void unitEvade(int unitID) { }
	public void unitHide(int unitID) { }
	public void unitMorph(int unitID) { }
	public void unitShow(int unitID) { }
}
