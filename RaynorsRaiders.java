package javabot.RaynorsRaiders;

/**
 * Example of a Java AI Client that does nothing.
 */
import java.awt.Point;
import java.util.LinkedList;

import javabot.RaynorsRaiders.ManagerBuild.BuildStatus;
import javabot.RaynorsRaiders.ManagerBuild.BuildingRR;
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
	ManagerInfo         managerInfo; 

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
		managerInfo = new ManagerInfo();
		coreBaby = new CoreBaby();
		
		/* Send AI Pointers to all the AIs (this is the "second" constructor */
		coreReactive.AILink(bwapi, coreReactive, coreBaby, managerBuild, managerMilitary, managerWorkers, managerInfo);
		coreBaby.AILink(bwapi, coreReactive, coreBaby, managerBuild, managerMilitary, managerWorkers, managerInfo);
		managerBuild.AILink(bwapi, coreReactive, coreBaby, managerBuild, managerMilitary, managerWorkers, managerInfo);
		managerMilitary.AILink(bwapi, coreReactive, coreBaby, managerBuild, managerMilitary, managerWorkers, managerInfo);
		managerWorkers.AILink(bwapi, coreReactive, coreBaby, managerBuild, managerMilitary, managerWorkers, managerInfo);
		managerInfo.AILink(bwapi, coreReactive, coreBaby, managerBuild, managerMilitary, managerWorkers, managerInfo);
		
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
			
			//testing on Matt's test map - not important
			managerMilitary.addCreatedMilitaryUnits(u, u.getTypeID());
		}
		//System.out.println("Size of master list is " + masterUnitList.size());
		
		coreBaby.setup();
		coreReactive.setup();
		managerMilitary.setup();
		managerBuild.setup();
		managerWorkers.setup();
		managerInfo.setup();
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
			coreBaby.startUp();
		}
		
		// Call actions every 30 frames
		if (frameCount % 15 == 0) 
		{
			//managerWorkers.handleIdle();
			managerWorkers.checkUp();
			coreBaby.checkUp();
			//coreReactive.checkUp();
			managerBuild.checkUp();
			managerInfo.checkUp();
		}
		
		if (frameCount % 30 == 0)
		{
			managerMilitary.checkUp();
			//managerMilitary.testVult();
		}

	}
	
	public void drawDebugInfo() 
	{
		////System.out.println("Start debug");
		bwapi.drawText(0, 0, "Home base location is ( " + managerBuild.homePositionX + ", "
				+ managerBuild.homePositionY + ")", true);
		//bwapi.drawText(200, 0, "Home base location is ( " + managerBuild.getStartLocation().getX() + ", "
				//+ managerBuild.getStartLocation().getY() + ")", true);

		//bwapi.drawHealth(healthFlag);
		for (Unit u : bwapi.getMyUnits())  
		{
			if (u.isUnderAttack()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.RED, false, false);
			else if (u.isGatheringMinerals()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.BLUE, false, false);
			else if (u.isGatheringGas()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.GREEN, false, false);
			else if (u.isAttacking()) bwapi.drawCircle(u.getX(), u.getY(), 12, BWColor.ORANGE, false, false);
			
			
			bwapi.drawLine(u.getX(), u.getY(), u.getTargetX(), u.getTargetY(), BWColor.WHITE, false);
		}
		managerWorkers.debug();
		
		int buildOrderNdx = 100;
		for (ManagerBuild.BuildingRR bo : coreBaby.buildingGoals)
		{
			bwapi.drawText(0, buildOrderNdx, bo.blueprint.toString() + " " + bo.status.toString(), true);
			buildOrderNdx += 10;
		}
		buildOrderNdx = 100;
		//System.out.println("Ordersssss to build is: " + managerBuild.orders.size());
		if (managerBuild.orders.size() != 0)
		{
			for (UnitTypes ut : managerBuild.orders)
			{
				bwapi.drawText(200, buildOrderNdx,ut.toString(), true);
				buildOrderNdx += 10;
			}
		}
		else
		{
			bwapi.drawText(200, buildOrderNdx, "No orders", true);
			buildOrderNdx += 10;
		}
		buildOrderNdx = buildOrderNdx + 30;
		
		int unitNdx = 0;
		String msg;
		msg = "Number of vultures " + managerMilitary.militaryUnits.get(UnitTypes.Terran_Vulture).size(); 
		bwapi.drawText(300, unitNdx, msg, true);
		
		msg = "Genome Status: Blood " + coreBaby.genomeSetting.bloodFrequency + " Def " + coreBaby.genomeSetting.defensiveness + " Spread " + coreBaby.genomeSetting.spread + " Count " + coreBaby.countdown;
		bwapi.drawText(300, 20, msg, true);
		
		
		//if (managerBuild.buildingBuildings.size() != 0) 
		//{
			//for (UnitTypes ut2 : managerBuild.buildingBuildings)
			//{
				//bwapi.drawText(200, buildOrderNdx,ut2.toString(), true);
				//buildOrderNdx += 10;
			//}
		//}
		//else
		//{
			//bwapi.drawText(200, buildOrderNdx, "Nothing building", true);
			//buildOrderNdx += 10;
		//}
		//System.out.println("Here");
		
		
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
		
		managerMilitary.addCreatedMilitaryUnits(createdUnit, createdUnitType);
		
		// needs to be extended for all buildings
		// is there a function that can return UnitTypes?
		if(bwapi.getUnitType(createdUnitType).isBuilding())
		{
			if(createdUnitType == managerBuild.buildingsStack.get(managerBuild.nextToBuildIndex).blueprint.ordinal())
			{
				managerBuild.buildingsStack.get(managerBuild.nextToBuildIndex).unit = createdUnit;
				managerBuild.buildingsStack.get(managerBuild.nextToBuildIndex).status = BuildStatus.ACCEPTTED;

				managerBuild.nextToBuildIndex++;
				
				// sort under construction according to build time
				managerBuild.scheduleBuildTime();

			}
		}
	}
	public void unitDestroy(int unitID)
	{		
		//System.out.println("In unit destroyed");
		bwapi.printText("Unit Destroyed " + String.valueOf(unitID));
		Unit taggedForDeath = null;

		this.managerInfo.unitDestoryed(unitID);
		
		for (Unit u : masterUnitList)
		{
			if (u.getID() == unitID)
			{
				if (u.getTypeID() == UnitTypes.Terran_SCV.ordinal())
				{
					managerWorkers.removeWorker(unitID);
					
					for(BuildingRR bldg : managerBuild.buildingsStack)
					{
						if(bldg.worker.getID() == unitID)
						{
							bldg.worker = null;
							break;
						}
					}
				}
				managerMilitary.removeDestroyedMilitaryUnits(unitID, u.getTypeID());
				
				taggedForDeath = u;
			}
		}
		masterUnitList.remove(taggedForDeath);

		/*
		for(Unit bldg : managerBuild.builtBuildings)
		{
			if(bldg.getID() == unitID)
			{
				managerBuild.builtBuildings.remove(bldg);
				break;
			}
		}
		*/
	}
	public void unitDiscover(int unitID) 
	{ 
		this.managerInfo.unitSeen(unitID);
	}
	public void unitEvade(int unitID) { }
	public void unitHide(int unitID) { }
	public void unitMorph(int unitID) { }
	public void unitShow(int unitID) { }
}
