package javabot.RaynorsRaiders;

import java.awt.Point;
import java.util.ArrayList;
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

// Cannot import core reactive, primary and secondary constructors will init the AI core communication

public class ManagerBuild extends RRAITemplate
{
	
	BuildMode bldgMode;
	BuildMode unitsMode;
	LinkedList<UnitTypes> orders;
	LinkedList<UnitTypes> roster;
	LinkedList<Unit> builtBuildings;
	LinkedList<Unit> buildingBuildings;
	LinkedList<BaseLocation> ourBases;
	
	UnitTypes tempType;
	Unit tempUnit;
	
	ArrayList<BuildingRR> buildingsStack;
	int builtIndex;
	int priorityIndex;
	int readyIndex;
	
	
	int homePositionX;
	int homePositionY;
	
	public ManagerBuild() {
		//SET UP ALL INTERNAL VARIABLES HERE
		super();
		builtBuildings = new LinkedList<Unit>();
		buildingBuildings = new LinkedList<Unit>();
		ourBases = new LinkedList<BaseLocation>();
		bldgMode = BuildMode.BLOCKING_STACK;
		unitsMode = BuildMode.FIRST_POSSIBLE;
		orders = new LinkedList<UnitTypes>();
		roster = new LinkedList<UnitTypes>();	
	}
	
	public enum baseStatus 
	{
		UNOCCUPIED, BUILT, BUILDING, 
		DESTROYED, ENEMIES, ENEMIES_DESTROYED
	}
	
	public enum BuildMode 
	{
		FIRST_POSSIBLE, BLOCKING_STACK, HOLD_ALL, RESET_BLOCKING_STACK
	};

	public class BuildingRR {
		int requiredSupply;
		int requiredSCVs;
		int baseAssignment;
		UnitTypes blueprint;
		Unit unit;
		boolean attemptBuild;
	}
	
	private class BaseRR
	{
		private BaseLocation baseLoc;
		private int baseNumber;
		private baseStatus status;
		
		private BaseRR() 
		{	
		}
		
		private BaseRR(BaseLocation bl, int number, baseStatus status)
		{
			this.baseLoc = bl;
			this.baseNumber = number;
			this.status = status;
		}
	
	}

	public void AILinkData() 
	{
	
	}
	
	public void setup() {
		//System.out.println("ManagerBuild Online");
		for(Unit unit : bwapi.getMyUnits())
		{
			if(unit.getTypeID() == UnitTypes.Terran_Command_Center.ordinal())
			{
				builtBuildings.add(unit);
			}
		}
	}
	
	private void constructionStatus()
	{
		for(Unit bldg : buildingBuildings)
		{
//System.out.println(bwapi.getUnitType(bldg.getTypeID()).getName() + " ready in " + bldg.getRemainingBuildTimer());
			if(bldg.isCompleted())
			{
				builtBuildings.add(bldg);
				buildingBuildings.remove(bldg);
			}
		}
	}
	// looks for a building to construct according to the build mode
	// calls build() method if it finds something to construct
	public void checkUp() 
	{
/*
System.out.println("orders: " + orders.toString());
System.out.print("under construction: ");
for(Unit bldg : buildingBuildings)
{
	UnitType btype = bwapi.getUnitType(bldg.getTypeID());
	System.out.print(btype.getName() + " ");
}
System.out.println();

System.out.println("completed buildings:");
for(Unit bldg : builtBuildings)
{
	UnitType btype = bwapi.getUnitType(bldg.getTypeID());
	System.out.print(btype.getName() + " ");
}
System.out.println();
*/
        // building construction
		switch(bldgMode) {
			case FIRST_POSSIBLE:
				int i = 0;
				boolean canBuild = false;
				UnitTypes b = null;
				UnitType bldg = null;
				
				// go through list until we find a buildable structure
				while(!canBuild && orders.size() > 0 && i < orders.size())
				{
					b = orders.get(i);
					bldg = bwapi.getUnitType(b.ordinal());
		
					if(weAreBuilding(b.ordinal()) && bwapi.getSelf().getMinerals() - underConstructionM() < bldg.getMineralPrice() && 
							bwapi.getSelf().getGas() - underConstructionG() < bldg.getGasPrice()) 
					{
						i++;
					}
					else 
					{
						canBuild = true;
					}
				}
				
				if(canBuild)
				{
				   build(b);
				   
// temp solution				
					if(b.ordinal() == UnitTypes.Terran_Refinery.ordinal())
					{
						orders.remove(b);
					}
				}
				else 
				{
					//System.out.println("could not build anything in stack");
				}
					
				break;
			case BLOCKING_STACK:
				i = 0;

				if(orders.size() > 0)
				{
					//find first building not being constructed **** MAY NEED ADJUSTMENT
					while(i < orders.size() && (weAreBuilding(orders.get(i).ordinal())))
					{
						i++;
					}
					
					if(i < orders.size())
					{
						b = orders.get(i);
						bldg = bwapi.getUnitType(b.ordinal());
				
						if(bwapi.getSelf().getMinerals() - underConstructionM() >= bldg.getMineralPrice() && 
						bwapi.getSelf().getGas() - underConstructionG() >= bldg.getGasPrice()) 
						{
							build(b);
// temp solution				
							if(b.ordinal() == UnitTypes.Terran_Refinery.ordinal())
							{
								orders.remove(b);
							}
						}
					}
					else
					{
						//System.out.println("everything under construction");
					}
				}
				else
				{
					//System.out.println("nothing to build");
				}

				break;
			case HOLD_ALL:
				//System.out.println("halting construction...");
				break;
			default:
				break;
		}

		constructionStatus();
		
//System.out.println("roster: " + roster.toString());
		// unit training
		switch(unitsMode) 
		{
		case FIRST_POSSIBLE:
			int i = 0;
			boolean canTrain = false;
			boolean trainingGroundsPresent = false;
			UnitTypes c = null;
			UnitType soldier = null;
			
			// Go through list until we find a trainable unit
			while(!canTrain && i < roster.size())
			{
				c = roster.get(i);
				soldier = bwapi.getUnitType(c.ordinal());

				for(Unit bldg : builtBuildings)
				{
					if(bldg.getTypeID() == soldier.getWhatBuildID())
					{
						trainingGroundsPresent = true;
						break;
					}
				}
				
				if(!trainingGroundsPresent || bwapi.getSelf().getMinerals() - underConstructionM() < soldier.getMineralPrice() && 
						bwapi.getSelf().getGas() - underConstructionG() < soldier.getGasPrice()) 
				{
					i++;
				}
				else 
				{
					canTrain = true;
				}
			}
			
			if(canTrain)
			{
			   train(c);
			   roster.remove(i);
			   // add unit to military list
			}
			else 
			{
				//System.out.println("could not build anything in roster");
			}
				
			break;
		case BLOCKING_STACK:
			c = null;
			soldier = null;
			
			c = roster.peek();
			soldier = bwapi.getUnitType(c.ordinal());
			
			if(bwapi.getSelf().getMinerals() - underConstructionM() >= soldier.getMineralPrice() && 
					bwapi.getSelf().getGas() - underConstructionG() >= soldier.getGasPrice()) 
			{
				train(c);
				roster.pop();
				// add units to military list
			}
			break;
		case HOLD_ALL:
			//System.out.println("halting construction...");
			break;
		default:
			break;
			
		}
				
	}
	
	public void debug() {
		System.out.println(orders.size() + " orders: " + orders.toString());
		System.out.println("Checking if null: " + orders.toString());
		System.out.println("built: " +builtBuildings.toString());
	}
	
	public void captureBaseLocation() {
		// Remember our homeTilePosition at the first frame
//		if (bwapi.getFrameCount() == 1) {
			int cc = getNearestUnit(UnitTypes.Terran_Command_Center.ordinal(), 0, 0);
			homePositionX = bwapi.getUnit(cc).getX();
			homePositionY = bwapi.getUnit(cc).getY();
//		}
	}
	
	
	/*
	 * Assumes that this function will be called on the first frame before
	 * we have other bases (ie only hace one command center)
	 */
	
	public int baseSetup()
	{
		ourBases.clear();
		int unitPostX = 0, unitPostY = 0;
		int cc = getNearestUnit(UnitTypes.Terran_Command_Center.ordinal(), 0, 0);
		unitPostX = bwapi.getUnit(cc).getX();
		unitPostY = bwapi.getUnit(cc).getY();
		//System.out.println("CC is located at: " + bwapi.getUnit(cc).getTileX() +", " + bwapi.getUnit(cc).getTileY());
		for (BaseLocation bl : bwapi.getMap().getBaseLocations())
		{
			//System.out.println("Base is: " + bl.getTx() + ", " + bl.getTy());
			if (bl.getX() == unitPostX && bl.getY() == unitPostY)
			{
				ourBases.add(bl);
				//System.out.println("ManagerBuild: Found our home base! Size is " + ourBases.size());
			}
		}
		return ourBases.size();
	}
	
	/*
	 * Gets called whenever a new base is created. Called from RR.unitCreated
	 */
	
	public void newBaseLocation(Unit newCC)
	{
		for (BaseLocation bl : bwapi.getMap().getBaseLocations())
		{
			if (bl.getX() == newCC.getX() && bl.getY() == newCC.getY())
			{
				ourBases.add(bl);
				workers.addBaseToBaseWorkers();
				//System.out.println("ManagerBuild: Added new base succesfully");
			}
		}		
	}
	
	/*
	 * Returns the location of our very first base
	 */
	
	public BaseLocation getStartLocation() 
	{
		return ourBases.get(0);
	}

	public BaseLocation getBaseFromUnit(Unit curCC)
	{
		//System.out.println("ManagerBuild: In base from unit");
		//System.out.println("Unit is at: " + curCC.getX() + ", " + curCC.getY());
		for (BaseLocation bl : ourBases)
		{
			//System.out.println("Loop one");
			//System.out.println("bl is at: " + bl.getX() + ", " + bl.getY());
			if (curCC.getX() == bl.getX() && curCC.getY() == bl.getY())
			{
				//System.out.println("MangerBuild: Got base from Unit");
				return bl;
			}
		}
		//System.out.println("WARNING--------------------- getBaseFromUnit eturning null");
		return null;
		
	}
	
	/*
	 * 	if ((unit.getTypeID() == buildingTypeID) && (!unit.isCompleted())) return true;
	 * 
	 * 
	 */
	
	
	private int underConstructionM() 
	{
		int cost = 0;
		
		for(Unit unit : bwapi.getMyUnits()) 
		{
			if(unit.isConstructing()) 
			{
				UnitType bldg = bwapi.getUnitType(unit.getBuildTypeID());
				
				cost += bldg.getMineralPrice();
			}
		}
		
		return cost;
	}
	
	private int underConstructionG() 
	{
		int cost = 0;
		
		for(Unit unit : bwapi.getMyUnits()) 
		{
			if(unit.isConstructing()) 
			{
				UnitType bldg = bwapi.getUnitType(unit.getBuildTypeID());
				
				cost += bldg.getGasPrice();
			}
		}
		
		return cost;
	}
	
        // input: build(UnitTypes.xxxx) 
        // will build near similar building types
        // will also build add-ons (not sure if structure will attempt relocating if there's not enough room)
	public boolean build(UnitTypes structure) {
		//System.out.println("Building " + structure.toString());
		UnitType bldg;
		
		if (structure == null)
		{
			return false;
		}
		if (structure.ordinal() == UnitTypes.Terran_Command_Center.ordinal())
		{
			buildExpand();
			return false;
		}
		
		bldg = bwapi.getUnitType(structure.ordinal());
		
		if (bwapi.getSelf().getMinerals() >= bldg.getMineralPrice()) 
		{
			if(bwapi.getSelf().getGas() >= bldg.getGasPrice()) 
			{
				if(bldg.isAddon()) 
				{
					//find parent structure
					for(Unit unit : bwapi.getMyUnits()) 
					{
						if(unit.getTypeID() == bldg.getWhatBuildID()) 
						{
							bwapi.buildAddon(unit.getID(), bldg.getID());
							break;
						}
					}
				
				} 
				else 
				{
					// try to find the worker near our home position
					int worker = getNearestUnit(UnitTypes.Terran_SCV.ordinal(), homePositionX, homePositionY);
					if (worker != -1) 
					{
						// if we found him, try to select appropriate build tile position for bldg (near our home base)
						int xtile = homePositionX, ytile = homePositionY;
				
						for(Unit unit : bwapi.getMyUnits()) 
						{
							if(unit.getTypeID() == bldg.getID()) 
							{
								xtile = unit.getX();
								ytile = unit.getY();
								break;
							}
						}
						Point buildTile = getBuildTile(worker, bldg.getID(), xtile, ytile);
						// if we found a good build position, and we aren't already constructing a bldg 
						// order our worker to build it
						if ((buildTile.x != -1) && (!weAreBuilding(bldg.getID()))) 
						{
							bwapi.build(worker, buildTile.x, buildTile.y, bldg.getID());
							return true;
							//buildingBuildings.push(bldg);
						}
					}
					else 
					{
						react.econ_sendBuildAlert(BuildAlert.NO_WORKERS);
					}
				}
			}
			else 
			{
				  react.econ_sendBuildAlert(BuildAlert.NO_GAS);
			}
		}
		else 
		{
			react.econ_sendBuildAlert(BuildAlert.NO_MINERALS);
		}
		return false;
	}
	
	public void buildExpand()
	{
		
	}

    // input: upgrade(UpgradeTypes.xxxx)
    // method will do resource check before attempting upgrade
    // so far, will only upgrade at base price
	public void upgrade(UpgradeTypes lvlup) 
	{
		UpgradeType gear = bwapi.getUpgradeType(lvlup.ordinal());
	
		if(bwapi.getSelf().getMinerals() >= gear.getMineralPriceBase()) 
		{
			if(bwapi.getSelf().getGas() >= gear.getGasPriceBase()) 
			{
				for(Unit unit : bwapi.getMyUnits()) 
				{
					if(unit.getTypeID() == gear.getWhatUpgradesTypeID()) 
					{
						bwapi.upgrade(unit.getID(), gear.getID());
					}
				}
			}
			else 
			{
				react.econ_sendBuildAlert(BuildAlert.NO_GAS);
			}
		}
		else 
		{
			react.econ_sendBuildAlert(BuildAlert.NO_MINERALS);
		}
	}

   // input: research(TechTypes.xxxx)
    // method will do resource check before attempting research
	public void research(TechTypes item) 
	{
		TechType tech = bwapi.getTechType(item.ordinal());
	
		if(bwapi.getSelf().getMinerals() >= tech.getMineralPrice()) 
		{
		  if(bwapi.getSelf().getGas() >= tech.getGasPrice()) 
		  {
		
			  for(Unit unit : bwapi.getMyUnits()) 
			  {
				  if(unit.getTypeID() == tech.getWhatResearchesTypeID()) 
				  {
					  bwapi.research(unit.getID(), tech.getID());
				  }
			  }
		  }
		  else 
		  {
				react.econ_sendBuildAlert(BuildAlert.NO_GAS);
		  }
	    }
		else 
		{
			react.econ_sendBuildAlert(BuildAlert.NO_MINERALS);
		}
	}

	//input: train(UnitTypes.xxxx)
	// method will do research check before attempting to train
	public void train(UnitTypes cadet) 
	{
		UnitType soldier = bwapi.getUnitType(cadet.ordinal());
		
		if(bwapi.getSelf().getMinerals() - underConstructionM() >= soldier.getMineralPrice()) 
		{
			if(bwapi.getSelf().getGas() - underConstructionG() >= soldier.getGasPrice()) 
			{
				for(Unit unit : builtBuildings) 
				{
					if(unit.getTypeID() == soldier.getWhatBuildID()) 
					{
						if(unit.getTrainingQueueSize() < 5) 
						{
							bwapi.train(unit.getID(), soldier.getID());
						}
					}
				}
			}
			else 
			{
				react.econ_sendBuildAlert(BuildAlert.NO_GAS);
			}
		}
		else 
		{
			react.econ_sendBuildAlert(BuildAlert.NO_MINERALS);
		}
	}
	
	
	//Returns the id of a unit of a given type, that is closest to a pixel position (x,y), or -1 if we
	//don't have a unit of this type
	private int getNearestUnit(int unitTypeID, int x, int y) {
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
	private Point getBuildTile(int builderID, int buildingTypeID, int x, int y) {
		Point ret = new Point(-1, -1);
		int maxDist = 3;
		int stopDist = 40;
		int tileX = x/32; int tileY = y/32;
		int tilesize = 4;
	
		// Refinery, Assimilator, Extractor
		if (bwapi.getUnitType(buildingTypeID).isRefinery()) {
			for (Unit n : bwapi.getNeutralUnits()) {
				if ((n.getTypeID() == UnitTypes.Resource_Vespene_Geyser.ordinal()) && 
						( Math.abs(n.getTileX()-tileX) < stopDist ) &&
						( Math.abs(n.getTileY()-tileY) < stopDist )
						) return new Point(n.getTileX(),n.getTileY());
			}
		}
	
		while ((maxDist < stopDist) && (ret.x == -1)) {
			for (int i=tileX-maxDist; i<=tileX+maxDist; i++) {
				for (int j=tileY-maxDist; j<=tileY+maxDist; j++) {
					if (bwapi.canBuildHere(builderID, i, j, buildingTypeID, false)) {
						// units that are blocking the tile
						boolean unitsInWay = false;
						for (Unit u : bwapi.getAllUnits()) {
							if (u.getID() == builderID) continue;
							
							if(buildingTypeID == UnitTypes.Terran_Command_Center.ordinal() || u.getTypeID() == UnitTypes.Terran_Command_Center.ordinal() || 
									buildingTypeID == UnitTypes.Terran_Factory.ordinal() || u.getTypeID() == UnitTypes.Terran_Factory.ordinal() ||
									buildingTypeID == UnitTypes.Terran_Factory.ordinal() || u.getTypeID() == UnitTypes.Terran_Science_Facility.ordinal() ||
									buildingTypeID == UnitTypes.Terran_Factory.ordinal() || u.getTypeID() == UnitTypes.Terran_Starport.ordinal())
							{
								tilesize = 6;
							}
							else
							{
								tilesize = 4;
							}
							
							if ((Math.abs(u.getTileX()-i) < tilesize) && (Math.abs(u.getTileY()-j) < tilesize)) unitsInWay = true;
						}
						if (!unitsInWay) {
							ret.x = i; ret.y = j;
							return ret;
						}
						// creep for Zerg (this may not be needed - not tested yet)
						if (bwapi.getUnitType(buildingTypeID).isRequiresCreep()) {
							boolean creepMissing = false;
							for (int k=i; k<=i+bwapi.getUnitType(buildingTypeID).getTileWidth(); k++) {
								for (int l=j; l<=j+bwapi.getUnitType(buildingTypeID).getTileHeight(); l++) {
									if (!bwapi.hasCreep(k, l)) creepMissing = true;
									break;
								}
							}
							if (creepMissing) continue; 
						}
						// psi power for Protoss (this seems to work out of the box)
						if (bwapi.getUnitType(buildingTypeID).isRequiresPsi()) {}
					}
				}
			}
			maxDist += 2;
		}
	
		if (ret.x == -1) bwapi.printText("Unable to find suitable build position for "+bwapi.getUnitType(buildingTypeID).getName());
		return ret;
	}

	//Returns true if we are currently constructing the building of a given type.
	private boolean weAreBuilding(int buildingTypeID) {
		for (Unit unit : bwapi.getMyUnits()) {
			if ((unit.getTypeID() == buildingTypeID) && (!unit.isCompleted())) return true;
			if (bwapi.getUnitType(unit.getTypeID()).isWorker() && unit.getConstructingTypeID() == buildingTypeID) return true;
		}
		return false;
	}
}

