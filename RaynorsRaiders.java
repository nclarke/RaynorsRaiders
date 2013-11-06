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

public class RaynorsRaiders implements BWAPIEventListener {

	boolean debgFlag = false;
	boolean healthFlag = false;
	
	/* Master unit list */
	LinkedList<Unit> masterUnitList = new LinkedList<Unit>();;
	
	
	/* BroodWar API Harness*/
	JNIBWAPI bwapi;
	
	/* Name AIs here */
//<<<<<<< HEAD
	CoreReactive 		coreReactive;
	ManagerBuild 		managerBuild;
	ManagerWorkers		managerWorkers;
//=======
	CoreBaby         ai_baby;     //Active
	CoreReactive     ai_react;     //Active
	ManagerBuild     ai_builder;  //Active
	ManagerMilitary  ai_military; //Active
	ManagerWorkers   ai_worker;   //Passive
//>>>>>>> a88d9c7ed35e8ffcd4f6aadc350ec3ef27dad21e
	
	public static void main(String[] args) {
		new RaynorsRaiders();
	}
	
	public RaynorsRaiders() {
		bwapi = new JNIBWAPI(this);
		
		
		/* Construct builders */
//<<<<<<< HEAD
		coreReactive = new CoreReactive();
		managerBuild = new ManagerBuild();
		managerWorkers = new ManagerWorkers();
		
		/* Send AI Pointers to all the AIs (this is the "second" constructor */
		//coreReactive.AILinkCoreReactive(bwapi, managerBuild);
		managerBuild.AILinkManagerBuild(bwapi, coreReactive);
		managerWorkers.AILinkManagerWorkers(bwapi, coreReactive);
		
		bwapi.start();
//=======
		ai_react = new CoreReactive();
		ai_baby = new CoreBaby();
		ai_builder = new ManagerBuild();
		ai_military = new ManagerMilitary(null); //FIXME NEEDS TO NOT HAVE PASSED VALUES
		ai_worker = new ManagerWorkers();
		
		/* Send AI Pointers to all the AIs (this is the "second" constructor */
		ai_react.AILink(bwapi, ai_react, ai_baby, ai_builder, ai_military, ai_worker);
		ai_baby.AILink(bwapi, ai_react, ai_baby, ai_builder, ai_military, ai_worker);
		ai_builder.AILinkManagerBuild(bwapi, ai_react);
		
//>>>>>>> a88d9c7ed35e8ffcd4f6aadc350ec3ef27dad21e
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
		System.out.println("Analyzing map... Please wait");
		bwapi.loadMapData(true);
		System.out.println("Map Analyzed");
		
		// Setup master unit list
		masterUnitList.clear();
		for (Unit u : bwapi.getMyUnits())
		{
			masterUnitList.add(u);
		}
		System.out.println("Game setup complete");
		System.out.println("Size of master list is " + masterUnitList.size());
		
		ai_baby.startUp();
		ai_react.startUp();
	}
	public void gameUpdate() 
	{
		 //Draw debug information on screen
		if (debgFlag)
			drawDebugInfo();
		// This sets up the base location
		if (bwapi.getFrameCount() == 1) {
			managerBuild.captureBaseLocation();
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
		if (bwapi.getFrameCount() % 30 == 0) 
		{
			managerWorkers.handleIdle();
			coreReactive.checkUp();
			managerBuild.construct();
			ai_react.checkUp();
			ai_baby.checkUp();
			
			ai_builder.construct();
		}

		
	}
	
	public void drawDebugInfo() {
		bwapi.drawText(0, 0, "Home base location is ( " + managerBuild.homePositionX + ", "
				+ managerBuild.homePositionY + ")", true);
		bwapi.drawText(200, 0, "Home base location is ( " + managerBuild.getStartLocation().getX() + ", "
				+ managerBuild.getStartLocation().getY() + ")", true);
		
		for (Unit u : bwapi.getMyUnits())  
		{
			if (u.isUnderAttack()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.RED, false, false);
			else if (u.isGatheringMinerals()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.BLUE, false, false);
			else if (u.isGatheringGas()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.GREEN, false, false);
			else if (u.isAttacking()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.ORANGE, false, false);
			
			
			bwapi.drawLine(u.getX(), u.getY(), u.getTargetX(), u.getTargetY(), BWColor.WHITE, false);
		}
		int tempMins = managerWorkers.getNumMinerals(managerBuild.homePositionX, managerBuild.homePositionY);
		bwapi.drawText(500, 500, "On the map?", false);
		bwapi.drawText(managerBuild.homePositionX-32, managerBuild.homePositionY-(32*2), "Num mins is " + tempMins, false);
		bwapi.drawHealth(healthFlag);
		managerWorkers.workerDebg();
	//	System.out.print("debing");
	}
	
	
	public void gameEnded() { }
	public void keyPressed(int keyCode) 
	{
		if (keyCode == 66 ) //if equals b toggle debgFlag
			debgFlag = !debgFlag;
		else if (keyCode == 72) // press h to toggle health
			healthFlag = !healthFlag;
	}
	public void matchEnded(boolean winner) { }
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
