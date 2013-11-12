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
	boolean militaryDebugFlag = false;
	
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
		//System.out.println("TOP of RR");
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
		//System.out.println("In RaynorsRaiders");
		//System.out.println("Game Started");
		

		// allow me to manually control units during the game
		bwapi.enableUserInput();
		
		// set game speed to 30 (0 is the fastest. Tournament speed is 20)
		// You can also change the game speed from within the game by "/speed X" command.
		bwapi.setGameSpeed(30);
		
		// analyze the map
		//System.out.println("Analyzing map... Please wait");
		bwapi.loadMapData(true);
		//System.out.println("Map Analyzed");
		
		
		
		// Setup master unit list
		masterUnitList.clear();
		for (Unit u : bwapi.getMyUnits())
		{
			masterUnitList.add(u);
		}
		//System.out.println("Size of master list is " + masterUnitList.size());
		
		coreBaby.setup();
		coreReactive.setup();
		managerMilitary.setup();
		managerBuild.setup();
		managerWorkers.setup();
		//System.out.println("Game setup complete");
		
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
			managerMilitary.startUp();
			if( managerBuild.baseSetup() != 1)
			{
				//Throw error here
				//System.out.println("ERROR: BaseSetup does not equal 1");
			}
			for (Unit unit : bwapi.getMyUnits()) 
			{
				if (unit.getTypeID() == UnitTypes.Terran_SCV.ordinal()) 
				{
					managerWorkers.startWorkers(unit.getID());
				}
			}
		}
		
		// Call actions every 30 frames
		if (frameCount % 30 == 0) 
		{
			managerWorkers.handleIdle();
			coreBaby.checkUp();
			//coreReactive.checkUp();
			managerBuild.checkUp();
			managerMilitary.checkUp();
		}

	}
	
	public void drawDebugInfo() 
	{
		////System.out.println("Start debug");
		bwapi.drawText(0, 0, "Home base location is ( " + managerBuild.homePositionX + ", "
				+ managerBuild.homePositionY + ")", true);
		bwapi.drawText(200, 0, "Home base location is ( " + managerBuild.getStartLocation().getX() + ", "
				+ managerBuild.getStartLocation().getY() + ")", true);

		//bwapi.drawHealth(healthFlag);
		for (Unit u : bwapi.getMyUnits())  
		{
			if (u.isUnderAttack()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.RED, false, false);
			else if (u.isGatheringMinerals()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.BLUE, false, false);
			else if (u.isGatheringGas()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.GREEN, false, false);
			else if (u.isAttacking()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.ORANGE, false, false);
			
			
			bwapi.drawLine(u.getX(), u.getY(), u.getTargetX(), u.getTargetY(), BWColor.WHITE, false);
		}
		//managerWorkers.debug();
		
		if(militaryDebugFlag)
		{
			//managerMilitary.debug();
		}
		
		////System.out.println("End debug");
	}
	
	
	public void gameEnded() 
	{
		//System.out.println("RaynorsRaiders: Game Ended");
	}
	/*
	 *	Letters are in ascii so just look up ascii code for the letter you want 
	 *	UpperCase ascii
	 */
	public void keyPressed(int keyCode) 
	{
		if (keyCode == 66 ) //if equals b toggle debgFlag
			debgFlag = !debgFlag;
		else if (keyCode == 72) // press h to toggle health
			healthFlag = !healthFlag;
		else if (keyCode == 77) // press m to toggle military debugs
			militaryDebugFlag = !militaryDebugFlag;
		//else if (keyCode == 73) // press g to send workers to mine (DEBUG)
			//managerWorkers.assignWorkersToGas(0, 2);
	}
	public void matchEnded(boolean winner) 
	{
		//System.out.println("RaynorsRaiders: Match Ended");
		//System.out.println("RaynorsRaiders: Did we win? " + winner);
	}
	public void nukeDetect(int x, int y) { }
	public void nukeDetect() { }
	public void playerLeft(int id) { }
	public void unitCreate(int unitID) 
	{
		Unit createdUnit = bwapi.getUnit(unitID), createdFrom = null;
		int createdUnitType = createdUnit.getTypeID();
		bwapi.printText("Unit Created " + String.valueOf(unitID));
		masterUnitList.add(bwapi.getUnit(unitID));
		//System.out.println("creating type id is " + bwapi.getUnit(unitID).getTypeID());
		
		for (Unit u : bwapi.getMyUnits())
		{
			if (u.getX() == createdUnit.getX() & u.getY() == createdUnit.getY())
			{
				createdFrom = u;				
			}
		}
		
		if (createdUnitType == UnitTypes.Terran_SCV.ordinal())
		{
			//System.out.println("Created unit location " + createdUnit.getX() + ", " + createdUnit.getY());
			managerWorkers.addWorker(unitID, createdFrom);
		}
		if (createdUnitType == UnitTypes.Terran_Command_Center.ordinal())
		{
			//System.out.println("Created CC");
			managerBuild.newBaseLocation(createdUnit);
		}
		
		if (createdUnitType == UnitTypes.Terran_Marine.ordinal())
		{
			managerMilitary.addMilitaryUnit(createdUnit, UnitTypes.Terran_Marine);
		}
		
		// needs to be extended for all buildings
		// is there a function that can return UnitTypes?
		if(bwapi.getUnitType(createdUnitType).isBuilding())
		{
			if(createdUnitType == UnitTypes.Terran_Academy.ordinal())
			{
				managerBuild.builtBuildings.add(UnitTypes.Terran_Academy);
			}
			else if(createdUnitType == UnitTypes.Terran_Supply_Depot.ordinal())
			{
				managerBuild.builtBuildings.add(UnitTypes.Terran_Supply_Depot);
			}
		}
	}
	public void unitDestroy(int unitID)
	{
		
		//System.out.println("In unit destroyed");
		bwapi.printText("Unit Destroyed " + String.valueOf(unitID));
		
		for (Unit u : masterUnitList)
		{
			if (u.getID() == unitID)
			{
				if (u.getTypeID() == UnitTypes.Terran_SCV.ordinal())
					managerWorkers.removeWorker(unitID);
				
				if (u.getTypeID() == UnitTypes.Terran_Marine.ordinal())
				{
					managerMilitary.removeMilitaryUnit(bwapi.getUnit(unitID), UnitTypes.Terran_Marine);
				}
				
				masterUnitList.remove(u);
				//System.out.println("Remove succesfull");
			}
		}
	}
	public void unitDiscover(int unitID) { }
	public void unitEvade(int unitID) { }
	public void unitHide(int unitID) { }
	public void unitMorph(int unitID) { }
	public void unitShow(int unitID) { }
}
