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

public class ManagerWorkers {
	
	JNIBWAPI bwapi;
	CoreReactive core;
	CoreReactive.BuildMode mode;
	LinkedList<UnitTypes> orders;
	// Want to store an array of workers at each current base location
	//LinkedList<UnitTypes> Buildings;
	UnitTypes tempType;
	Unit tempUnit;
	

	
	public ManagerWorkers() 
	{
		//SET UP ALL INTERNAL VARIABLES HERE
	}
	
	/*
	 * 	if ((unit.getTypeID() == buildingTypeID) && (!unit.isCompleted())) return true;
	 * 
	 * 
	 */
	
	public void AILinkManagerBuild(JNIBWAPI d_bwapi, CoreReactive d_core) {
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
	public boolean weAreBuilding(int buildingTypeID) {
		for (Unit unit : bwapi.getMyUnits()) {
			if ((unit.getTypeID() == buildingTypeID) && (!unit.isCompleted())) return true;
			if (bwapi.getUnitType(unit.getTypeID()).isWorker() && unit.getConstructingTypeID() == buildingTypeID) return true;
		}
		return false;
	}

}

