package javabot.RaynorsRaiders;

import java.util.LinkedList;

import javabot.JNIBWAPI;
import javabot.types.*;
import javabot.types.UnitType.UnitTypes;
import javabot.util.*;
import javabot.model.*;

public class CoreReactive extends RRAITemplate 
{
	//Data structs
	LinkedList<UnitTypes> core_econ_buildingStack;
	LinkedList<UnitTypes> core_econ_unitsStack;
	LinkedList<BuildAlert> core_econ_buildAlerts;

	BuildMode core_econ_buildingMode;
	BuildMode core_econ_unitsMode;
	
	
	public enum BuildMode 
	{
		FIRST_POSSIBLE, BLOCKING_STACK, HOLD_ALL, RESET_BLOCKING_STACK
	};
	
	public enum BuildAlert 
	{
		NO_MINERALS, NO_GAS, NO_ROOM, NO_WORKERS
	};
	
	/* Here is our constructor */
	public CoreReactive() 
	{
		core_econ_buildingStack = new LinkedList<UnitTypes>();
		core_econ_unitsStack = new LinkedList<UnitTypes>();
		core_econ_buildAlerts = new LinkedList<BuildAlert>();
		
		// Set default build mode to process everything in the stack in-order and normally
		core_econ_buildingMode = BuildMode.BLOCKING_STACK;
		core_econ_unitsMode = BuildMode.FIRST_POSSIBLE;
	}
	
	/* This is to be run during setup, currently its a basic loadout of units to create */
	public void setup() 
	{
		System.out.println("CoreReactive Online");
	}
	
	
	/* This is to be run frequently, and is the quick-decider for things such as resources */
	public void checkUp() 
	{
		//System.out.println("CoreReactive checkup phase");
		//Commented out to keep checkUp silent
		/*
		 BuildAlert currentAlert;
		
		if ((currentAlert = core_econ_buildAlerts.pop()) != null) {
			if (currentAlert == BuildAlert.NO_ROOM) {
				//Save previous modes...
				core_econ_buildingStack.push(UnitTypes.Terran_Supply_Depot);
				core_econ_buildingMode = BuildMode.BLOCKING_STACK;
				core_econ_unitsMode = BuildMode.HOLD_ALL;
			}
			if (currentAlert == BuildAlert.NO_GAS && false) {
				//Add on make a vespene geyeser facility
			}
		}
		*/
		
		
	}
	
	public void debug() 
	{
		// Need to put core Reactive debug here
	}
	
	public void econ_sendBuildAlert(BuildAlert alert) 
	{
		core_econ_buildAlerts.add(alert);
	}
	
	public LinkedList<UnitTypes> econ_getBuildingStack()
	{
		return this.core_econ_buildingStack;
	}
	
	public LinkedList<UnitTypes> econ_getUnitsStack()
	{
		return this.core_econ_unitsStack;
	}
	
	public BuildMode econ_getBuildingMode()
	{
		return this.core_econ_buildingMode;
	}
	
	public BuildMode econ_getUnitsMode()
	{
		return this.core_econ_unitsMode;
	}
	
	public LinkedList<Unit> gen_findUnits(UnitTypes input) 
	{
	LinkedList<Unit> listToBuild = new LinkedList<Unit>();
	UnitType searchFor = bwapi.getUnitType(input.ordinal());
		for(Unit unit : bwapi.getMyUnits()) {
			if(unit.getTypeID() == searchFor.getWhatBuildID()) {
				listToBuild.add(unit);
			}
		}
		return listToBuild;
	}
	

	
}
