package javabot.RaynorsRaiders;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import javabot.JNIBWAPI;
import javabot.RaynorsRaiders.Level;
import javabot.model.*;
import javabot.types.*;
import javabot.types.OrderType.OrderTypeTypes;
import javabot.types.UnitType.UnitTypes;
import javabot.util.BWColor;

public class Military 
{
	JNIBWAPI bwapi;
	/* EnumMap for us to know what UnitTypes to train per Level - Level is determined by how well AI is doing in the game 
	 * (AI is very rich in the beginning, gets its level changed from ZERO to TWO) */
	EnumMap<Level, ArrayList<UnitTypes>> unitTypesPerLevel;
	int homePositionX, homePositionY;
	
	public Military(JNIBWAPI rr_bwapi)
	{
		this.bwapi = rr_bwapi;
		unitTypesPerLevel = new EnumMap<Level, ArrayList<UnitTypes>>(Level.class);
		initMap(unitTypesPerLevel);
		setHomePosition(homePositionX, homePositionY);
	}
	
	void setHomePosition(int homePositionX, int homePositionY)
	{
		if (bwapi.getFrameCount() == 1) 
		{
			int cc = getNearestUnit(UnitTypes.Terran_Command_Center.ordinal(), 0, 0);
			if (cc == -1) cc = getNearestUnit(UnitTypes.Zerg_Hatchery.ordinal(), 0, 0);
			if (cc == -1) cc = getNearestUnit(UnitTypes.Protoss_Nexus.ordinal(), 0, 0);
			homePositionX = bwapi.getUnit(cc).getX();
			homePositionY = bwapi.getUnit(cc).getY();
		}
	}
	
	/* Initializes a EnumMap of UnitTypes we want per Level */
	public void initMap(EnumMap<Level, ArrayList<UnitTypes>> lvlMap)
	{
		if(lvlMap != null)
		{
			ArrayList<UnitTypes> levelZero = new ArrayList<UnitTypes>(Arrays.asList(UnitTypes.Terran_Marine));
			lvlMap.put(Level.ZERO, levelZero);
			
			ArrayList<UnitTypes> levelOne = new ArrayList<UnitTypes>(
					Arrays.asList(UnitTypes.Terran_Marine, UnitTypes.Terran_Medic));
			lvlMap.put(Level.ONE, levelOne);
			
			ArrayList<UnitTypes> levelTwo = new ArrayList<UnitTypes>(
					Arrays.asList(UnitTypes.Terran_Marine, UnitTypes.Terran_Medic, UnitTypes.Terran_Vulture));
			lvlMap.put(Level.TWO, levelTwo);
		}
	}
	
	/* 
	 * Helper function for unitFormation
	 * 
	 * levelEnum:         takes in a Level Enum type (i.e. Level.ZERO)
	 * unitTypesPerLevel: for reading in the different UnitTypes we want per level
	 * 
	 * Returns an ArrayList of Units (max = 12) of the UnitTypes wanted in levelEnum
	 */
	public ArrayList<Unit> unitFormationHelper(Level levelEnum, EnumMap<Level, ArrayList<UnitTypes>> unitTypesPerLvl)
	{
		final int maxUnits = 12;
		ArrayList<Unit> unitRally = new ArrayList<Unit>();
		
		if(unitTypesPerLvl != null)
		{
			for (Unit unit : bwapi.getMyUnits())
			{
				for(int index = 0; index < unitTypesPerLvl.get(levelEnum).size(); index++)
				{
					if(unit.getTypeID() == unitTypesPerLvl.get(levelEnum).get(index).ordinal())
					{
						if(unitRally.size() < maxUnits)
						{
							unitRally.add(unit);
						}
					}
				}
			}
		}
		return unitRally;
	}
	
	/*
	 * Handles the cases of different levels of the AI during the Game
	 * 
	 * levelEnum:         takes in a Level Enum type (i.e. Level.ZERO)
	 * unitTypesPerLevel: for reading in the different UnitTypes we want per level
	 * 
	 * Returns the ArrayList of Units passed in from unitFormationHelper
	 */
	public ArrayList<Unit> unitFormation(Level levelEnum, EnumMap<Level, ArrayList<UnitTypes>> unitTypesPerLvl)
	{	
		if(levelEnum.equals(Level.ZERO))
		{
			return unitFormationHelper(Level.ZERO, unitTypesPerLvl);
		}
		else if(levelEnum.equals(Level.ONE))
		{
			return unitFormationHelper(Level.ONE, unitTypesPerLvl);
		}
		else if(levelEnum.equals(Level.TWO))
		{
			return unitFormationHelper(Level.TWO, unitTypesPerLvl);
		}
		else
		{
			return unitFormationHelper(Level.ZERO, unitTypesPerLvl);
		}
	}
	
	/*
	 * Commands the Units stored in an ArrayList to attack a location on the map
	 * 
	 * unitFormation:   ArrayList of Units we wanted to rally
	 * pixelPositionX:  Rally position's X
	 * pixelPositionY:  Rally position's Y
	 * 
	 */
	public void rallyUnits(ArrayList<Unit> unitFormation, int pixelPositionX, int pixelPositionY )
	{
		if(unitFormation != null)
		{
			for(int index = 0; index < unitFormation.size(); index++)
			{
				bwapi.move(unitFormation.get(index).getID(), pixelPositionX, pixelPositionY);
			}
			System.out.println("RALLY TEST");
		}
	}
	
	public boolean rallyReadyCheck(ArrayList<Unit> unitFormation, int pixelPositionX, int pixelPositionY)
	{
		boolean checkFlag = false;
		
		if(unitFormation != null)
		{
			for(int index = 0; index < unitFormation.size(); index++)
			{
				if((unitFormation.get(index).getX() == pixelPositionX) && (unitFormation.get(index).getY() == pixelPositionY))
				{
					checkFlag = true;
				}
			}
		}	
		return checkFlag;
	}
	
	/*
	 * Rally the Units stored in an ArrayList to a location on the map
	 * 
	 * unitFormation:   ArrayList of Units we wanted to rally
	 * pixelPositionX:  Attack position's X
	 * pixelPositionY:  Attack position's Y
	 * 
	 */
	public void attackEnemyLocation(ArrayList<Unit> unitFormation, int pixelPositionX, int pixelPositionY, int homePosX, int homePosY)
	{
		if(unitFormation != null)
		{
			for(int index = 0; index < unitFormation.size(); index++)
			{
				bwapi.attack(unitFormation.get(index).getID(), pixelPositionX, pixelPositionY);
			}
			System.out.println("ATTACK TEST");
		}
	}
	
	/*
	 * Returns the current marines on the map (testing out Level.ZERO atm)
	 */
	public int getCurrentUnitCount()
	{
		int unitsTotal = 0;
		
		for (Unit unit : bwapi.getMyUnits())
		{
			if(unit.getTypeID() == UnitTypes.Terran_Marine.ordinal())
			{
				unitsTotal++;
			}
		}
		return unitsTotal;
	}
	
	public int getWorkersCount()
	{
		int unitsTotal = 0;
		
		for (Unit unit : bwapi.getMyUnits())
		{
			if(unit.getTypeID() == UnitTypes.Terran_SCV.ordinal())
			{
				unitsTotal++;
			}
		}
		return unitsTotal;
	}
	
	/*
	 * Gets the scout unit at the very start of the game
	 * 
	 * Returns the scout Unit, a Unit with ID of -1 if not
	 */
	public Unit getScoutUnit()
	{		
		for (Unit unit : bwapi.getMyUnits())
		{
			// if this unit is a command center (Terran_SCV)
			if (unit.getTypeID() == UnitTypes.Terran_SCV.ordinal())
			{
				return new Unit(unit.getID());
			}
		}
		
		return new Unit(-1);
	}
	
	/*
	 * Gets all the possible the base locations on the map
	 * 
	 * Returns an ArrayList of those base locations other than our home base location
	 */
	public ArrayList<BaseLocation> getEnemyBases()
	{
		ArrayList<BaseLocation> enemyBaseLocsArr = new ArrayList<BaseLocation>();
		
		for (BaseLocation b : bwapi.getMap().getBaseLocations()) 
		{
			if (b.isStartLocation() && (b.getX() != homePositionX) && (b.getY() != homePositionY)) 
			{
				enemyBaseLocsArr.add(b);
			}
		}
		return enemyBaseLocsArr;
	}
	
	/*
	 * Action performed to tell the Scout to scout the base locations
	 * 
	 * scoutUnitID:   a unit's ID
	 * enemyBaseLocs:  ArrayList of enemy's base locations
	 * 
	 */
	public void scoutEnemyBases(Unit scoutUnitID, ArrayList<BaseLocation> enemyBaseLocs)
	{		
		for (int index = 0; index < enemyBaseLocs.size(); index++) 
		{
			bwapi.move(scoutUnitID.getID(), enemyBaseLocs.get(index).getX(), enemyBaseLocs.get(index).getY());
			
			 /* tried to get the scout to not do anything once it reaches the enemy base - not working atm for some reason */
			 if( ((scoutUnitID.getX() == enemyBaseLocs.get(index).getX()) 
					&& (scoutUnitID.getY() == enemyBaseLocs.get(index).getY())) 
					|| scoutUnitID.isAttacking() || scoutUnitID.isGatheringGas() 
					|| scoutUnitID.isGatheringMinerals() )
			{
				/* if it did do something, tell it to go back to home base */
				bwapi.move(scoutUnitID.getID(), homePositionX, homePositionY);
			} 
		}
	}
	
	/*
	 * The high-level function called to do the scouting
	 */
	public void scoutOperation()
	{
		Unit scout = getScoutUnit();
		scoutEnemyBases(scout, getEnemyBases());
		System.out.println("Scouted");
	}
	
	public void attackOperation()
	{
		if(((getCurrentUnitCount() % 12) == 0))
		{
			ArrayList<Unit> unitFormed = unitFormation(Level.ZERO, unitTypesPerLevel);
			ArrayList<BaseLocation> enemyBases = getEnemyBases();

			rallyUnits(unitFormed, homePositionX, homePositionY);
			
			attackEnemyLocation(unitFormed, enemyBases.get(0).getX(), enemyBases.get(0).getY(), homePositionX, homePositionY);
		}
		System.out.println("Attacked");
	}
	
	   // Returns the id of a unit of a given type, that is closest to a pixel position (x,y), or -1 if we
    // don't have a unit of this type
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
	    		nearestID = unit.getID();
	    		nearestDist = dist;
	    	}
	    }
	    return nearestID;
    }
    
    public void drawMilitaryDebugInfo()
    {
    	int spacing = 10;
		
		// Draw our home position.
		bwapi.drawText(new Point(5,0), "Our home position: "+String.valueOf(homePositionX)+","+String.valueOf(homePositionY), true);
		for (BaseLocation b : bwapi.getMap().getBaseLocations()) {
			if (b.isStartLocation()) {
				int index  = spacing/10;
				bwapi.drawText(new Point(5,spacing), "Base position " + index + ": " +String.valueOf(b.getX())+","+String.valueOf(b.getY()), true);
			}
			spacing= spacing + 10;
		}
		
		bwapi.drawText(new Point(5,110), "Total SCVs trained: " + String.valueOf(getWorkersCount()), true);
		bwapi.drawText(new Point(5,120), "Total Marines trained: " + String.valueOf(getCurrentUnitCount()), true);
    }
}
