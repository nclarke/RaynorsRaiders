package javabot.AIStarCraft;

import java.util.LinkedList;

import javabot.JNIBWAPI;
import javabot.types.*;
import javabot.types.UnitType.UnitTypes;
import javabot.util.*;
import javabot.model.*;

public class core_reactive {

	//AIs
	JNIBWAPI bwapi;
	build_manager builder;
	
	//Data structs
	LinkedList<UnitTypes> core_econ_buildingStack;
	LinkedList<UnitTypes> core_econ_unitsStack;
	LinkedList<BuildAlert> core_econ_buildAlerts;

	BuildMode core_econ_buildingMode;
	BuildMode core_econ_unitsMode;
	
	
	public enum BuildMode {
		FIRST_POSSIBLE, BLOCKING_STACK, HOLD_ALL, RESET_BLOCKING_STACK
	};
	
	public enum BuildAlert {
		NO_MINERALS, NO_GAS, NO_ROOM, NO_WORKERS
	};
	
	/* Here is our constructor */
	public core_reactive() {
		core_econ_buildingStack = new LinkedList<UnitTypes>();
		core_econ_unitsStack = new LinkedList<UnitTypes>();
		
		// Set default build mode to process everything in the stack in-order and normally
		core_econ_buildingMode = BuildMode.BLOCKING_STACK;
		core_econ_unitsMode = BuildMode.FIRST_POSSIBLE;
	}
	
	public void AI_link_core_reactive(JNIBWAPI d_bwapi, build_manager d_builder) {
		bwapi = d_bwapi;
		builder = d_builder;
	}
	
	/* This is to be run during startup, currently its a basic loadout of units to create */
	public void startUpSequence() {
		// Now populate the buildingStack
		core_econ_buildingStack.push(UnitTypes.Terran_Supply_Depot);
		core_econ_buildingStack.push(UnitTypes.Terran_Supply_Depot);
		core_econ_buildingStack.push(UnitTypes.Terran_Barracks);
		core_econ_buildingStack.push(UnitTypes.Terran_Refinery);
		core_econ_buildingStack.push(UnitTypes.Terran_Supply_Depot);
		core_econ_buildingStack.push(UnitTypes.Terran_Bunker);
		core_econ_buildingStack.push(UnitTypes.Terran_Bunker);
		
		core_econ_unitsStack.push(UnitTypes.Terran_SCV);
		core_econ_unitsStack.push(UnitTypes.Terran_SCV);
		core_econ_unitsStack.push(UnitTypes.Terran_SCV);
		core_econ_unitsStack.push(UnitTypes.Terran_SCV);
		core_econ_unitsStack.push(UnitTypes.Terran_Marine);
		core_econ_unitsStack.push(UnitTypes.Terran_Marine);
		core_econ_unitsStack.push(UnitTypes.Terran_Marine);
		core_econ_unitsStack.push(UnitTypes.Terran_Marine);
	}
	
	
	/* This is to be run frequently, and is the quick-decider for things such as resources */
	public void checkUp() {
		BuildAlert currentAlert;
		
		/* Check on build alert */
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
		
	}
	
	public void econ_sendBuildAlert(BuildAlert alert) {
		core_econ_buildAlerts.add(alert);
	}
	
	public LinkedList<UnitTypes> econ_getBuildingStack(){
		return this.core_econ_buildingStack;
	}
	
	public LinkedList<UnitTypes> econ_getUnitsStack() {
		return this.core_econ_unitsStack;
	}
	
	public BuildMode econ_getBuildingMode() {
		return this.core_econ_buildingMode;
	}
	
	public LinkedList<Unit> gen_findUnits(UnitTypes input) {
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
