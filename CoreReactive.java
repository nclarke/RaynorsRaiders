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
	LinkedList<BuildAlert> core_econ_buildAlerts;
	
	public enum BuildAlert 
	{
		NO_MINERALS, NO_GAS, NO_ROOM, NO_WORKERS
	};
	
	/* Here is our constructor */
	public CoreReactive() 
	{
		core_econ_buildAlerts = new LinkedList<BuildAlert>();
	}
	
	/* This is to be run during setup, currently its a basic loadout of units to create */
	public void setup() 
	{
		//System.out.println("CoreReactive Online");
	}
	
	
	/* This is to be run frequently, and is the quick-decider for things such as resources */
	public void checkUp() 
	{
		if (baby.genomeSetting.defensiveness > (int) (Math.random() * 100)) {
			Unit targetUnit = bwapi.getUnit(info.scouter.getNearestUnit(UnitTypes.Protoss_Zealot.ordinal(), builder.homePositionX, builder.homePositionY));
			military.unitOperation(20, targetUnit.getX(), targetUnit.getY());
		}
	}
	
	public void debug() 
	{
		// Need to put core Reactive debug here
	}
	
	public void econ_sendBuildAlert(BuildAlert alert) 
	{
		core_econ_buildAlerts.add(alert);
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
	
	public Region gen_findClosestRegion(Integer d_x, Integer d_y) 
	{
		Region winningRegion = null;
		Double distanceFromWinning;
		Double distanceFromWorking;
		for (Region workingRegion : bwapi.getMap().getRegions()) 
		{
			if (winningRegion != null) 
			{
				distanceFromWinning = 
				 Math.sqrt(
				  Math.pow(Math.abs(d_x - winningRegion.getCenterX()), 2) +
				  Math.pow(Math.abs(d_y - winningRegion.getCenterY()), 2)
				 );
				distanceFromWorking = 
				 Math.sqrt(
				  Math.pow(Math.abs(d_x - workingRegion.getCenterX()), 2) +
				  Math.pow(Math.abs(d_y - workingRegion.getCenterY()), 2)
				 );
				if (distanceFromWorking < distanceFromWinning) 
				{
					winningRegion = workingRegion;
				}
			}
			else
			{
				winningRegion = workingRegion;
			}
		}
		
		return winningRegion;
	}
	
}