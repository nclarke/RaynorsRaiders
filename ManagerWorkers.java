package javabot.RaynorsRaiders;

import java.awt.Point;
import java.util.LinkedList;

import javabot.JNIBWAPI;
import javabot.model.Unit;
import javabot.types.TechType;
import javabot.types.TechType.TechTypes;
import javabot.types.UnitType;
import javabot.types.UnitType.UnitTypes;
import javabot.types.UpgradeType;
import javabot.types.UpgradeType.UpgradeTypes;
import javabot.util.BWColor;
import javabot.RaynorsRaiders.CoreReactive.*; // Why do we need this line? -Matt

// Cannot import core reactive, primary and secondary constructors will init the AI core communication

/*
 * Need to get the home base locations from the builder
 * Need to get number of minerals at each base location
 * 
 */

public class ManagerWorkers extends RRAITemplate 
{
	private static final double SCVS_PER_MIN_PATCH = 2.5;
	JNIBWAPI bwapi;
	CoreReactive core;
	CoreReactive.BuildMode mode;
	LinkedList<UnitTypes> orders;
	LinkedList<Worker> workers;
	// Want to store an array of workers at each current base location
	//LinkedList<UnitTypes> Buildings;
	UnitTypes tempType;
	Unit tempUnit;
	

	
	public ManagerWorkers() 
	{
		//SET UP ALL INTERNAL VARIABLES HERE
		workers = new LinkedList<Worker>();
	}
	
	private class Worker
	{
		private int unitID;
		private int asgnedBase; //what base it should be mining from 0 = Main, 1 = Natural...
		private workerOrders curOrder; //mine, attack, defend etc... thinking of doing an enum
		private double asgnedBaseX, asgnedBaseY; // X and Y coords of the current base location
		
		private Worker() 
		{
			unitID = asgnedBase = 0;
			curOrder = workerOrders.MINE;
		}
		
		private Worker(int unitID, int asgnedBase, workerOrders workOrd)
		{
			this.unitID = unitID;
			this.asgnedBase = asgnedBase;
			this.curOrder = workOrd;
			//this.asgnedBaseX = 
			
		}
		
	}
	
	public int getNumWorkers()
	{
		return workers.size();
	}
	
	/*
	 * Pass in a location and it will return the number of minerals at that base
	 * Right now gets all minerals within a 20 tile area
	 * Possible move this function to info manager?
	 */
	
	public int getNumMinerals(int xLoc, int yLoc)
	{
		int numMins = 0;
		for (Unit neu : bwapi.getNeutralUnits()) 
		{
			if (neu.getTypeID() == UnitTypes.Resource_Mineral_Field.ordinal())
			{
				double distance = Math.sqrt(Math.pow(neu.getX() - xLoc, 2) + Math.pow(neu.getY() - yLoc, 2));
				if (distance < (20 * 32))
				{
					numMins++;

				}
			}	
		}

		return numMins;
	}
	
	public void addWorker(int unitID)
	{
		System.out.println("In new worker");
		Worker newWorker = new Worker();
		newWorker.unitID = unitID;
		newWorker.curOrder = workerOrders.MINE;  //by default have it set to mine
		newWorker.asgnedBase = 0; //default is base zero which is main base
		workers.push(newWorker);
	}
	
	public void removeWorker(int unitID)
	{
		System.out.println("In lost worker");
		int ndx = 0;
		Worker toRemove = new Worker();
		toRemove.unitID = unitID;
		
		for (ndx = 0; ndx < workers.size(); ndx++)
		{
			if (workers.get(ndx).unitID == unitID)
			{
				System.out.println("Found remove");
				workers.remove(ndx);
			}
		}
	}
	
	public enum workerOrders
	{
		MINE, GAS, ATTACK, DEFEND, BUILD
	}
	
	public void handleIdle()
	{
		Worker curWorker = null;
		int workersHomeX, workersHomeY;
		int closestId = -1;
		for (Unit unit : bwapi.getMyUnits()) {
			// if this unit is Terran_SCV (worker),
			// and if it is idle (not doing anything),
			if (unit.getTypeID() == UnitTypes.Terran_SCV.ordinal() & unit.isIdle()) {
				curWorker = getWorkerByID(unit.getID());
				switch (curWorker.curOrder)
				{
				case MINE :
					double closestDist = 99999999;
					for (Unit neu : bwapi.getNeutralUnits()) 
					{
						if (neu.getTypeID() == UnitTypes.Resource_Mineral_Field.ordinal()) 
						{
							double distance = Math.sqrt(Math.pow(neu.getX() - curWorker.asgnedBaseX, 2)
							+ Math.pow(neu.getY() - curWorker.asgnedBaseY, 2));
							if ((closestId == -1) || (distance < closestDist)) 
							{
								closestDist = distance;
								//bwapi.printText("Closet dist is" + String.valueOf(closestDist));
								closestId = neu.getID();
							}
						}
					} 
					// and (if we found it) send this worker to gather it.
					if (closestId != -1) bwapi.rightClick(unit.getID(), closestId);
					break;
				case GAS :
					break;
				case ATTACK :
					break;
				default :
					break;
				}
			}
		}
	}
	
	public Worker getWorkerByID(int unitID)
	{
		for (Worker w : workers)
			if (w.unitID == unitID)
				return w;
		return null;
	}
	
	/*
	 * 	if ((unit.getTypeID() == buildingTypeID) && (!unit.isCompleted())) return true;
	 * 
	 * 
	 */
	
	public void AILinkManagerWorkers(JNIBWAPI d_bwapi, CoreReactive d_core) {
		//Here you get your pointers to the other AI cores (JINBWAPI, core, ect ect ect)
		//The Raynors Raiders code should call this "constructor" after all the other AI parts have
		// been created.
		bwapi = d_bwapi;
		core = d_core;
		mode = core.econ_getBuildingMode();
		orders = core.econ_getBuildingStack();

	}
	

	

	//Returns the id of a unit of a given type, that is closest to a pixel position (x,y), or -1 if we
	//don't have a unit of this type
	public int getNearestUnit(int unitTypeID, int x, int y) {
		int nearestID = -1;
		double nearestDist = 9999999;
		for (Unit unit : bwapi.getMyUnits()) {
			if ((unit.getTypeID() != unitTypeID) || (!unit.isCompleted())) continue;
			double dist = Math.sqrt(Math.pow(unit.getX() - x, 2) + Math.pow(unit.getY() - y, 2));
			if (nearestID == -1 || dist < nearestDist) {
				nearestID = unit.getID();
				nearestDist = dist;
 			}
 		}
 		return nearestID;
	}	

	//Returns the Point object representing the suitable build tile position
	//for a given building type near specified pixel position (or Point(-1,-1) if not found)
	//(builderID should be our worker)

	//Returns true if we are currently constructing the building of a given type.
	public boolean weAreBuilding(int buildingTypeID)
	{
		for (Unit unit : bwapi.getMyUnits()) {
			if ((unit.getTypeID() == buildingTypeID) && (!unit.isCompleted())) return true;
			if (bwapi.getUnitType(unit.getTypeID()).isWorker() && unit.getConstructingTypeID() == buildingTypeID) return true;
		}
		return false;
	}
	
	public int workerDebg()
	{
		//System.out.println("In worker debg");
		bwapi.drawText(0, 10, "Workers are", true);
		int ndx;
		for (ndx = 0; ndx < workers.size(); ndx++)
		{
			bwapi.drawText(0, 10 + (10 * (ndx + 1)), ndx + " " + workers.get(ndx).unitID, true);	
		}
		return 0;
	}

}

