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
	

	
	@Override
	public void checkUp() 
	{
//		System.out.println("In workers checkup");
		Unit curWorker;
		for (Worker w : allWorkers)
		{
			
			curWorker = bwapi.getUnit(w.unitID);
			if (curWorker.isUnderAttack())
			{
				workerUnderAttack(curWorker);
			}
		}
		handleIdle();
	}
	
	/*
	 * Nick call this function like
	 * ManagerWorkers.checkOutWorker(ManagerWorkers.workerOrders.SCOUT, 0)
	 * will grab you a worker from base 0 to scout
	 * 
	 * Ben call this function like
	 * ManagerWorkers.checkOutWorker(ManagerWorkers.workerOrders.BUILD, 0)
	 */
	
	public int checkOutWorker(workerOrders todo, int baseNum)
	{
		int ndx;
		Worker toRtn = null, curWorker;
		for (ndx = 0; ndx < baseWorkers.get(baseNum).size(); ndx++)
		{
			curWorker = baseWorkers.get(baseNum).get(ndx);
			if (curWorker.curOrder == workerOrders.MINE)
			{
				curWorker.lastOrder = curWorker.curOrder; 
				curWorker.curOrder = todo;
				toRtn = curWorker;
				return toRtn.unitID;				
			}
		}
		System.out.println("Unable to checkout worker for: " + todo);
		return -1;
	}
	public void checkInWorker(int unitID)
	{
		Worker curWorker;
		workerOrders temp;
		
		curWorker = getWorkerByID(unitID);
		if (curWorker != null)
		{
			temp = curWorker.curOrder;
			curWorker.curOrder = curWorker.lastOrder;
			curWorker.lastOrder = temp;
			bwapi.stop(unitID);
		}
		else
			System.out.println("checkInWorker failed, could not find worker by unitID");
	}
	/*
	 * Pass in the unit that is under attack
	 */
	public void workerUnderAttack(Unit underAttack)
	{
		Unit helperSCV;
		Worker underAttackWorker, helperWorker;
		int nearestSCV, x, y;
		underAttackWorker = getWorkerByID(underAttack.getID());
		x = underAttack.getX();
		y = underAttack.getY();
		//if (attacker.getTypeID() == UnitTypes.Protoss_Probe.ordinal())
		if (underAttackWorker.curOrder != workerOrders.UNDERATTACK)
		{
			nearestSCV = getNearestUnit(UnitTypes.Terran_SCV.ordinal(), x, y);
			System.out.println("Nearest SCV is " + nearestSCV);
			helperSCV = bwapi.getUnit(nearestSCV);
			helperWorker = getWorkerByID(helperSCV.getID());
			underAttackWorker.lastOrder = underAttackWorker.curOrder;
			
			helperWorker.lastOrder = helperWorker.curOrder;
			underAttackWorker.curOrder = workerOrders.UNDERATTACK;
			helperWorker.curOrder = workerOrders.UNDERATTACK;
			bwapi.attack(helperSCV.getID(), x, y);
			bwapi.attack(underAttack.getID(), x, y);
		}
	}
	
	public void trainWorker()
	{
		
		// how many workers does each base need for max saturation?
		int baseNdx = 0;
		for (BaseLocation bl : builder.ourBases)
		{
			int ccInt = getNearestUnit(UnitTypes.Terran_Command_Center.ordinal(), bl.getX(), bl.getY());
			Unit cc = bwapi.getUnit(ccInt);
			if ( !cc.isTraining() & baseNeedsWorkers(bl))
			{
				//train worker for this base
				//train( buildingID, unitTypeID ): Trains a unit of a specified type in our building.
				bwapi.train(cc.getID(), UnitTypes.Terran_SCV.ordinal());
				//cc.
			}
			//!cc.isTraining()
			else if ( (cc.getTrainingQueueSize() == 0) & getBaseToAddWorkers() != -1)
			{
				//if not training and another base needs workers train for that base
			}
			else 
			{
				//Don't train
			}
			baseNdx++;
		}
	}
	
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
		MINE, GAS, ATTACK, SCOUT, UNDERATTACK, BUILD
	}
	
	private class Worker
	{
		private int unitID;
		private int asgnedBase; //what base it should be mining from 0 = Main, 1 = Natural...
		private workerOrders curOrder; //mine, attack, defend etc... thinking of doing an enum
		private workerOrders lastOrder;
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
	
	/*
	 * Should only be used for the start SCVS
	 */
	
	public void startWorkers(int unitID)
	{
		Worker newWorker = new Worker(unitID, 0, workerOrders.MINE);
		addWorkerToBase(newWorker, 0);
		allWorkers.push(newWorker);
	}
	
	/*
	 * Goals:
	 * Assign the worker a base and put it in the right data structure
	 */
	
	public void addWorker(int unitID, Unit createdFrom)
	{
		//System.out.println("In add worker");
		BaseLocation createdBase = builder.getBaseFromUnit(createdFrom);
		int baseToAdd;
		int createdBaseNdx = builder.ourBases.indexOf(createdBase);
		//System.out.println("In new worker, baseNdx is " + createdBaseNdx);
		//default is base zero which is main base
		//by default have it set to mine
		
		Worker newWorker;
		if (baseNeedsWorkers(createdBase))
		{
			 newWorker = new Worker(unitID, createdBaseNdx, workerOrders.MINE);
			 addWorkerToBase(newWorker, createdBaseNdx);
		}
		else
		{
			baseToAdd = getBaseToAddWorkers();
			newWorker = new Worker(unitID, baseToAdd, workerOrders.MINE);
			addWorkerToBase(newWorker, baseToAdd);
		}
		//System.out.println("Pushed new worker");
		allWorkers.push(newWorker);
	}
	
	/*
	 * Returns true if this base needs more workers, false otherwise
	 * Can change this to an int if we care how many more workers we need 
	 */
	
	private boolean baseNeedsWorkers(BaseLocation baseCreatedFrom)
	{
		//int rtnNdx = -1, ndx = 0;
		int baseNdx, baseWorkers, baseMins, gasWorkers;
		baseNdx = builder.ourBases.indexOf(baseCreatedFrom);
		//curBase = builder.ourBases.get(baseNdx);
		baseWorkers = getBaseWorkers(baseNdx);
		baseMins = getNumMinerals(baseCreatedFrom.getX(), baseCreatedFrom.getY());
		if (!baseCreatedFrom.isMineralOnly())
			gasWorkers = 3;
		else
			gasWorkers = 0;
		//System.out.println("base needs workers? " + (baseWorkers < ( (SCVS_PER_MIN_PATCH * baseMins) + gasWorkers)) );
		return (baseWorkers < ( (SCVS_PER_MIN_PATCH * baseMins) + gasWorkers) );
	}
	
	/*
	 * Passing in an int that corresponds to what base you are creating from
	 */
	
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
		//System.out.println("MANAGERWORKER: All bases are full, can't assign new workers");
		return rtnNdx;
	}
	
	public void removeWorker(int unitID)
	{
		//System.out.println("In lost worker");
		int ndx = 0;
		Worker toRemove = new Worker();
		toRemove.unitID = unitID;
		
		
		for (ndx = 0; ndx < allWorkers.size(); ndx++)
		{
			if (allWorkers.get(ndx).unitID == unitID)
			{
				//System.out.println("Found remove");
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
		//System.out.println("Handling Idle");
		Unit curUnit;
		int closestId = -1;
		
		for (Worker w : allWorkers)
		{
			curUnit = bwapi.getUnit(w.unitID);
			if (curUnit.isIdle())
			{
				//System.out.println("Idle worker with order " + w.curOrder);
				switch (w.curOrder)
				{
				case MINE :
					closestId = getNearestMin(w.asgnedBaseX, w.asgnedBaseY); 
					if (closestId != -1) 
						bwapi.rightClick(curUnit.getID(), closestId);
					break;
				case GAS :
					closestId = getNearestUnit(UnitTypes.Terran_Refinery.ordinal(), (int)w.asgnedBaseX, (int)w.asgnedBaseY); 
					//closestId = getNearestGas(curWorker.asgnedBaseX, curWorker.asgnedBaseY);
					//System.out.println("---------------------Idle Gas, Closest id is " + closestId);
					if (closestId != -1) 
						bwapi.rightClick(curUnit.getID(), closestId);
					break;
				case UNDERATTACK :
					w.curOrder = w.lastOrder;
					break;
				default :
					break;
				}
			}
		}
		/*
		for (Unit unit : bwapi.getMyUnits())
		{
			if (unit.getTypeID() == UnitTypes.Terran_SCV.ordinal() && unit.isIdle()) // if this unit is Terran_SCV (worker) and if it is idle (not doing anything),
			{
				curWorker = getWorkerByID(unit.getID());
				//System.out.println("     Worker Idle with order " + curWorker.curOrder);
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
					//System.out.println("---------------------Idle Gas, Closest id is " + closestId);
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
		//System.out.println("End Handle Idle");
		*/
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
		for (Unit unit : bwapi.getMyUnits())
		{
			if ((unit.getTypeID() != unitTypeID) || (!unit.isCompleted())) continue;
			double dist = Math.sqrt(Math.pow(unit.getX() - x, 2) + Math.pow(unit.getY() - y, 2));
			if (nearestID == -1 || dist < nearestDist) 
			{
//				System.out.println("Found nearest unit");
				nearestID = unit.getID();
				nearestDist = dist;
 			}
 		}
 		return nearestID;
	}	


	
	@Override
	public void debug() 
	{
		int baseNdx = 0;
		String workerString = "";
		Unit curWorker;
		
		bwapi.drawText(200, 200, "Number workers is " + allWorkers.size(), true);
		
		
		for (Worker w : allWorkers)
		{
			workerString = "";
			curWorker = bwapi.getUnit(w.unitID);
			if (curWorker.isGatheringMinerals()) 
				bwapi.drawCircle(curWorker.getX(), curWorker.getY(), 12, BWColor.BLUE, false, false);
			else if (curWorker.isGatheringGas()) 
				bwapi.drawCircle(curWorker.getX(), curWorker.getY(), 12, BWColor.GREEN, false, false);
			else
				bwapi.drawCircle(curWorker.getX(), curWorker.getY(), 12, BWColor.ORANGE, false, false);
			
			
			//MINE, GAS, ATTACK, SCOUT, UNDERATTACK, BUILD
			switch (w.curOrder) 
			{
			case MINE:  
				workerString += "M";
				break;
			case GAS: 
				workerString += "G";
				break;
			case ATTACK:  
				workerString += "A";
				break;
			case SCOUT:  
				workerString += "S";
				break;
			case UNDERATTACK:
				workerString += "U";
				break;
			case BUILD:  
				workerString += "B";
				break;
            default:
				workerString += "UNK";
				break;
			}
			
			workerString += " " + w.asgnedBase;
			
			bwapi.drawText(curWorker.getX(), curWorker.getY(), workerString, false);
			
			
			if (w.curOrder == workerOrders.UNDERATTACK) 
			{
				//bwapi.drawCircle(curWorker.getX(), curWorker.getY(), 12, BWColor.PURPLE, false, false);

				curWorker = bwapi.getUnit(w.unitID);
				//bwapi.drawCircle(curWorker.getX(), curWorker.getY(), 12, BWColor.PURPLE, false, false); - FIXME - crashed
			}
		}
		
		
		int ndx = 0;
		for (BaseLocation b : builder.ourBases)
		{
			int baseWorkers, baseMins;
			
			baseWorkers = getBaseWorkers(ndx++);
			baseMins = b.getMinerals();
			
			bwapi.drawText(b.getX()-64, b.getY()-(32*2)-10, "Num mins is " + baseMins, false);
			bwapi.drawText(b.getX()-64, b.getY()-(32*2), "Num workers is " + baseWorkers, false);
		}
		
		/*
		for (BaseLocation b : bwapi.getMap().getBaseLocations())//builder.ourBases 
		{
			int tempMins = getNumMinerals(b.getX(), b.getY());
			int tempWorkers = getBaseWorkers(baseNdx++);
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
		*/
	}

}

