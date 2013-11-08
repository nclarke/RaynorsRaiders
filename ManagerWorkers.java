package javabot.RaynorsRaiders;

import java.awt.Point;
import java.util.LinkedList;

import javabot.JNIBWAPI;
import javabot.model.BaseLocation;
import javabot.model.Unit;
import javabot.types.TechType;
import javabot.types.TechType.TechTypes;
import javabot.types.UnitType;
import javabot.types.UnitType.UnitTypes;
import javabot.types.UpgradeType;
import javabot.types.UpgradeType.UpgradeTypes;
import javabot.util.BWColor;
import javabot.RaynorsRaiders.CoreReactive.*; // Why do we need this line? -Matt


/*
 * Need to get the home base locations from the builder
 * Need to get number of minerals at each base location
 * 
 */

public class ManagerWorkers extends RRAITemplate 
{
	private static final double SCVS_PER_MIN_PATCH = 2.5;
	LinkedList<UnitTypes> orders;
	LinkedList<Worker> allWorkers;
	LinkedList<LinkedList<Worker>> baseWorkers;
	UnitTypes tempType;
	Unit tempUnit;
	

	
	public ManagerWorkers() 
	{
		//SET UP ALL INTERNAL VARIABLES HERE
		allWorkers = new LinkedList<Worker>();
		allWorkers.clear();
		baseWorkers = new LinkedList<LinkedList<Worker>>();
		baseWorkers.add(new LinkedList<Worker>());
	}
	
	@Override
	public void setup() 
	{
		
	}
	
	public enum workerOrders
	{
		MINE, GAS, ATTACK, DEFEND, BUILD, SCOUT
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
			this.asgnedBaseX = builder.ourBases.get(asgnedBase).getX();
			this.asgnedBaseY = builder.ourBases.get(asgnedBase).getY();
			
		}
		
	}
	
	public void addWorker(int unitID)
	{
		int baseNdx = getBaseToAddWorkers();
		System.out.println("In new worker, baseNdx is " + baseNdx);
		//default is base zero which is main base
		//by default have it set to mine
		Worker newWorker = new Worker(unitID, baseNdx, workerOrders.MINE);
		addWorkerToBase(newWorker, baseNdx);
		allWorkers.push(newWorker);
	}
	
	private int getBaseToAddWorkers()
	{
		int rtnNdx = -1, ndx = 0;
		int baseWorkers, baseMins, gasWorkers;
		BaseLocation curBase;
		for (ndx = 0; ndx < builder.ourBases.size(); ndx++) 
		{
			curBase = builder.ourBases.get(ndx);
			baseWorkers = getBaseWorkers(ndx);
			baseMins = getNumMinerals(curBase.getX(), curBase.getY());
			if (!curBase.isMineralOnly())
				gasWorkers = 3;
			else
				gasWorkers = 0;
			if (baseWorkers < ( (SCVS_PER_MIN_PATCH * baseMins) + gasWorkers) )
				return ndx;
		}
		System.out.println("MANAGERWORKER: All bases are full, can't assign new workers");
		return rtnNdx;
	}
	
	public void removeWorker(int unitID)
	{
		System.out.println("In lost worker");
		int ndx = 0;
		Worker toRemove = new Worker();
		toRemove.unitID = unitID;
		
		
		for (ndx = 0; ndx < allWorkers.size(); ndx++)
		{
			if (allWorkers.get(ndx).unitID == unitID)
			{
				System.out.println("Found remove");
				removeWorkerFromBase(allWorkers.get(ndx), allWorkers.get(ndx).asgnedBase);
				allWorkers.remove(ndx);
			}
		}
	}
	
	public void assignWorkersToGas(int baseNdx, int howMany)
	{
		int ndx = 0, workerNdx = 0, curCC;
		BaseLocation curBase = builder.ourBases.get(baseNdx);
		curCC = getNearestUnit(UnitTypes.Terran_Command_Center.ordinal(), curBase.getX(), curBase.getY());
		Worker tempWorker;
		for (ndx = 0; ndx < howMany; workerNdx++)
		{
			tempWorker = baseWorkers.get(0).get(workerNdx);
			if (tempWorker.curOrder != workerOrders.GAS)
			{
				tempWorker.curOrder = workerOrders.GAS;
				bwapi.rightClick(tempWorker.unitID, curCC);
				ndx++;
			}	
		}
		
			
	}
	
	/*
	 * Adds a new LinkedList for a new base
	 * allows new workers to be assigned to a linked list 
	 * corresponding to that base.
	 * Returns the LinkedList that was just created
	 */
	
	public int addBaseToBaseWorkers() 
	{
		baseWorkers.add(new LinkedList<Worker>());
		return baseWorkers.size();
	}
	
	public void addWorkerToBase(Worker toAdd, int baseNdx)
	{
		LinkedList<Worker> curBaseWorkers = baseWorkers.get(baseNdx);
		curBaseWorkers.add(toAdd);
	}
	
	public void removeWorkerFromBase(Worker toRem, int baseNdx)
	{
		LinkedList<Worker> curBaseWorkers = baseWorkers.get(baseNdx);
		curBaseWorkers.remove(toRem);
	}
	
	public int getTotalNumWorkers()
	{
		return allWorkers.size();
	}
	
	public int getBaseWorkers(int baseNum)
	{
		return baseWorkers.get(baseNum).size();
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
				if (distance < (10 * 32))
				{
					numMins++;

				}
			}	
		}

		return numMins;
	}
	
	public void handleIdle()
	{
		Worker curWorker = null;
		int closestId = -1;
		double closestDist = 99999999;
		for (Unit unit : bwapi.getMyUnits())
		{
			if (unit.getTypeID() == UnitTypes.Terran_SCV.ordinal() & unit.isIdle()) // if this unit is Terran_SCV (worker) and if it is idle (not doing anything),
			{
				curWorker = getWorkerByID(unit.getID());
				switch (curWorker.curOrder)
				{
				case MINE :
					closestId = getNearestMin(curWorker.asgnedBaseX, curWorker.asgnedBaseY); 
					if (closestId != -1) 
						bwapi.rightClick(unit.getID(), closestId);
					break;
				case GAS :
					closestId = getNearestUnit(UnitTypes.Terran_Refinery.ordinal(), (int)curWorker.asgnedBaseX, (int)curWorker.asgnedBaseY); 
					//closestId = getNearestGas(curWorker.asgnedBaseX, curWorker.asgnedBaseY);
					System.out.println("---------------------Idle Gas, Closest id is " + closestId);
					if (closestId != -1) 
						bwapi.rightClick(unit.getID(), closestId);
					break;
				case ATTACK :
					break;
				default :
					break;
				}
			}
		}
	}
	
	private int getNearestMin(double workerX, double workerY) 
	{
		int rtnID = -1;
		double closestDist = 99999999;
		for (Unit neu : bwapi.getNeutralUnits()) 
		{
			if (neu.getTypeID() == UnitTypes.Resource_Mineral_Field.ordinal()) 
			{
				double distance = Math.sqrt(Math.pow(neu.getX() - workerX, 2)
				+ Math.pow(neu.getY() - workerY, 2));
				if ((rtnID == -1) || (distance < closestDist)) 
				{
					closestDist = distance;
					//bwapi.printText("Closet dist is" + String.valueOf(closestDist));
					rtnID = neu.getID();
				}
			}
		}
		return rtnID;
	}
	
	private int getNearestGas(double workerX, double workerY)
	{
		int rtnID = -1;
		double closestDist = 99999999;
		for (Unit neu : bwapi.getNeutralUnits()) 
		{
			if (neu.getTypeID() == UnitTypes.Resource_Vespene_Geyser.ordinal()) 
			{
				double distance = Math.sqrt(Math.pow(neu.getX() - workerX, 2)
						+ Math.pow(neu.getY() - workerY, 2));
				if ((rtnID == -1) || (distance < closestDist)) 
				{
					closestDist = distance;
					//bwapi.printText("Closet dist is" + String.valueOf(closestDist));
					rtnID = neu.getID();
				}
			}
		}
		return rtnID;
	}
	
	private Worker getWorkerByID(int unitID)
	{
		for (Worker w : allWorkers)
			if (w.unitID == unitID)
				return w;
		return null;
	}

	//Returns the id of a unit of a given type, that is closest to a pixel position (x,y), or -1 if we
	//don't have a unit of this type
	
	//Want to remove this and put this in another file? I am using this in this file
	public int getNearestUnit(int unitTypeID, int x, int y)
	{
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


	
	@Override
	public void debug() 
	{
		for (BaseLocation b : bwapi.getMap().getBaseLocations())//builder.ourBases 
		{
			int tempMins = getNumMinerals(b.getX(), b.getY());
			int tempWorkers = getBaseWorkers(0);
			if (tempMins != 0)
			{
				bwapi.drawText(b.getX()-64, b.getY()-(32*2)-10, "Num mins is " + tempMins, false);
				bwapi.drawText(b.getX()-64, b.getY()-(32*2), "Num workers is " + tempWorkers, false);
			}
		}
		
		
		bwapi.drawText(0, 10, "Workers are", true);
		int ndx;
		for (ndx = 0; ndx < allWorkers.size(); ndx++)
		{
			bwapi.drawText(0, 10 + (10 * (ndx + 1)), ndx + " " + allWorkers.get(ndx).unitID, true);	
		}
	}

}

