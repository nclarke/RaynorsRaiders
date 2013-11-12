package javabot.RaynorsRaiders;

import java.awt.Point;
import java.util.*;

import javabot.JNIBWAPI;
import javabot.RaynorsRaiders.Level;
import javabot.model.*;
import javabot.types.UnitType.UnitTypes;
import javabot.util.*;

public class ManagerMilitary extends RRAITemplate
{

	/* EnumMap for us to know what UnitTypes to train per Level - Level is determined by how well AI is doing in the game 
	 * (AI is very rich in the beginning, gets its level changed from ZERO to TWO) */
	EnumMap<Level, ArrayList<UnitTypes>> unitTypesPerLevel;
	int homePositionX, homePositionY;
	MiltScouter scouter;
	BaseLocation homeBase;
	
	EnumMap<UnitTypes, LinkedList<Unit>> militaryUnits;
	
	public class Tile {
		int x;
		int y;
		public Tile(int x,int y){
			this.x=x;
			this.y=y;
		}
		public int getX(){
			return this.x;
		}
		public int getY(){
			return this.y;
		}
	}
	
	public ManagerMilitary()
	{
		unitTypesPerLevel = new EnumMap<Level, ArrayList<UnitTypes>>(Level.class);
		initMap(unitTypesPerLevel);
		
		militaryUnits = new EnumMap<UnitTypes, LinkedList<Unit>>(UnitTypes.class);
		scouter = new MiltScouter(this);
	}
	
	public void AILinkData() {
		//None here
	}
	
	public void setup() {
		//System.out.println("ManageMilitary online");
	}
	
	public void startUp()
	{
		setHomePosition();
	}
	
	public void checkUp() {
		//Check up - FIXME - code needs to go here.
		this.scouter.scout();
	}
	
    public void debug()
    {
    	int spacing = 10;
		
		// Draw our home position.
		bwapi.drawText(new Point(5,0), "Our home position: " + String.valueOf(homePositionX) + ", " + String.valueOf(homePositionY), true);
		for (BaseLocation b : bwapi.getMap().getBaseLocations()) {
			if (b.isStartLocation()) {
				int index  = spacing/10;
				bwapi.drawText(new Point(5,spacing), "Base position " + index + ": " + String.valueOf(b.getX()) + ", " + String.valueOf(b.getY()), true);
			}
			spacing =+ 10;
		}
		
		bwapi.drawText(new Point(5,120), "Total Marines trained: " + String.valueOf(getCurrentUnitCount(UnitTypes.Terran_Marine)), true);
    }
    
	public void setHomePosition()
	{
		int cc = getNearestUnit(UnitTypes.Terran_Command_Center.ordinal(), 0, 0);
		if (cc == -1) cc = getNearestUnit(UnitTypes.Zerg_Hatchery.ordinal(), 0, 0);
		if (cc == -1) cc = getNearestUnit(UnitTypes.Protoss_Nexus.ordinal(), 0, 0);
			homePositionX = bwapi.getUnit(cc).getX();
			homePositionY = bwapi.getUnit(cc).getY();
			
		int index;
		for(index=0;index<bwapi.getMap().getBaseLocations().size();index++){
			BaseLocation tmpBase = bwapi.getMap().getBaseLocations().get(index);
			if(tmpBase.getX()==homePositionX && tmpBase.getY()==homePositionY){
				this.homeBase=tmpBase;
			}				
		}
	}
	
	public void addMilitaryUnit(Unit unitObj, UnitTypes unitType)
	{
		//FIXME - causing crashes
		//militaryUnits.get(unitType).add(unitObj);
	}
	
	public void removeMilitaryUnit(Unit unitObj, UnitTypes unitType)
	{
		//FIXME - causing crashes
		//militaryUnits.get(unitType).remove(unitObj);
	}
	
	/* Initializes a EnumMap of UnitTypes we want per Level */
	private void initMap(EnumMap<Level, ArrayList<UnitTypes>> lvlMap)
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
	private ArrayList<Unit> unitFormationHelper(Level levelEnum, EnumMap<Level, ArrayList<UnitTypes>> unitTypesPerLvl)
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
	private ArrayList<Unit> unitFormation(Level levelEnum, EnumMap<Level, ArrayList<UnitTypes>> unitTypesPerLvl)
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
	private void rallyUnits(ArrayList<Unit> unitFormation, int pixelPositionX, int pixelPositionY )
	{
		if(unitFormation != null)
		{
			for(Unit unit: unitFormation)
			{
				bwapi.move(unit.getID(), pixelPositionX, pixelPositionY);
			}
			//System.out.println("RALLY TEST");
		}
	}
	
	private boolean rallyReadyCheck(ArrayList<Unit> unitFormation, int pixelPositionX, int pixelPositionY)
	{
		boolean checkFlag = false;
		
		if(unitFormation != null)
		{
			for(Unit unit: unitFormation)
			{
				if((unit.getX() == pixelPositionX) && (unit.getY() == pixelPositionY))
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
	private void attackEnemyLocation(ArrayList<Unit> unitFormation, int pixelPositionX, int pixelPositionY)
	{
		if(unitFormation != null)
		{
			for(Unit unit: unitFormation)
			{
				bwapi.attack(unit.getID(), pixelPositionX, pixelPositionY);
			}
			//System.out.println("ATTACK TEST");
		}
	}
	
	/*
	 * Returns the current number of units of a UnitType
	 */
	public int getCurrentUnitCount(UnitTypes unitType)
	{
		return militaryUnits.get(unitType).size();
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
	 * Give this function a base location to have a group of units (marines atm) to attack
	 * 
	 */
	public void attackOperation(int pixelPositionX, int pixelPositionY)
	{
		if(((getCurrentUnitCount(UnitTypes.Terran_Marine) % 12) == 0))
		{
			ArrayList<Unit> unitFormed = unitFormation(Level.ZERO, unitTypesPerLevel);

			rallyUnits(unitFormed, homePositionX, homePositionY);
			attackEnemyLocation(unitFormed, pixelPositionX, pixelPositionY);
		}
		//System.out.println("Attacked");
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
    

}
