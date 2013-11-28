package javabot.RaynorsRaiders;

import java.awt.Point;
import java.util.*;

import javabot.JNIBWAPI;
import javabot.RaynorsRaiders.Level;
import javabot.model.*;
import javabot.types.TechType.TechTypes;
import javabot.types.UnitType;
import javabot.types.UnitType.UnitTypes;
import javabot.util.*;

public class ManagerMilitary extends RRAITemplate
{
	int homePositionX, homePositionY;
	BaseLocation homeBase;
	
	// Unit pool: military units gets added to this map as they are created 
	EnumMap<UnitTypes, LinkedList<Unit>> unitPool;
	
	// Military Teams: they are created in unitOperation each time an order is given from CoreBaby
	LinkedList<MilitaryTeam> militaryTeams;
	
	private class MilitaryTeam
	{
		LinkedList<Unit> militaryTeam;
		int locX, locY;
		int teamSize;
		
		public MilitaryTeam(LinkedList<Unit> militaryTeam, int locX,  int locY)
		{
			this.militaryTeam = militaryTeam;
			this.locX = locX;
			this.locY = locY;
			this.teamSize = militaryTeam.size();
		}
		
		public LinkedList<Unit> getMilitaryTeam()
		{
			return militaryTeam;
		}
		
		public int getX()
		{
			return locX;
		}
		
		public int getY()
		{
			return locY;
		}
		
		public void setLocation(int x, int y)
		{
			this.locX = x;
			this.locY = y;
		}
		
		public int getTeamSize()
		{
			return teamSize;
		}
		
		public void setTeamSize(int newSize)
		{
			teamSize = newSize;
		}
	}
	
	public ManagerMilitary()
	{		
		unitPool = new EnumMap<UnitTypes, LinkedList<Unit>>(UnitTypes.class);
		militaryTeams = new LinkedList<MilitaryTeam>();
		initMilitaryUnit();
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
		
		//for testing purposes - sends units to attack and tries to handle attack logistics for different units
		//attackLocationsTest();
		
		removeEmptyMilitaryTeam();
		handleUnitsAttacking();
	}
	
	public void attackLocationsTest()
	{
		for (BaseLocation b : bwapi.getMap().getBaseLocations()) 
		{
			if (b.isStartLocation() )
			{
				unitOperation(b.getX(), b.getY()); 
			}
		}
	}
	
    public void debug()
    {
    	int spacing = 10;
		
		bwapi.drawText(new Point(5,0), "Our home position: " + String.valueOf(homePositionX) + ", " + String.valueOf(homePositionY), true);
		for (BaseLocation b : bwapi.getMap().getBaseLocations()) {
			if (b.isStartLocation()) {
				int index  = spacing/10;
				bwapi.drawText(new Point(5,spacing), "Base position " + index + ": " + String.valueOf(b.getX()) + ", " + String.valueOf(b.getY()), true);
			}
			spacing =+ 10;
		}
    }
    
    public void testVult() 
    {
    	Unit vult = unitPool.get(UnitTypes.Terran_Vulture).get(0);
    	if (vult.isIdle())
    	{
    		javabot.types.TechType techType = bwapi.getTechType(3);
    		bwapi.useTech(vult.getID(), 3, 100, 100);
    		//bwapi.getTechType(`)
    	}
    }
    
    /*
     * Gets and returns the enemy unit (not building) that currently visible
     * and closet the the passed in location
     * public int getNearestUnit(int unitTypeID, int x, int y) 
     */
    
    
    public Unit getNearestEnemyUnit(int x, int y)
    {
    	Unit toRtn = null;
    	int nearestID = -1;
	    double nearestDist = 9999999;
	    for (Unit unit : bwapi.getEnemyUnits()) 
	    {
	    	
	    	if ((!unit.isCompleted()) || bwapi.getUnitType(unit.getTypeID()).isBuilding()) continue;
	    	double dist = Math.sqrt(Math.pow(unit.getX() - x, 2) + Math.pow(unit.getY() - y, 2));
	    	if (nearestID == -1 || dist < nearestDist) 
	    	{
	    		toRtn = unit;
	    		nearestDist = dist;
	    	}
	    }
	    return toRtn;
    }
    
    public void testStutter()
    {
    	Player p;
    
    	Unit toAttack;
    	Unit vult = militaryUnits.get(UnitTypes.Terran_Vulture).get(0);
    	//Unit marine = militaryUnits.get(UnitTypes.Terran_Marine).get(0);
    	if (vult.getGroundWeaponCooldown() == 0)
    	//if (marine.getGroundWeaponCooldown() == 0)
    	{
    		toAttack = getNearestEnemyUnit(vult.getX(), vult.getY());
    		bwapi.attack(vult.getID(), toAttack.getID());
    		//bwapi.attack(vult.getID(), 2000, 1000);
    	}
    	else
    	{
    		bwapi.move(vult.getID(), 0, 0);
    	}
    }
    
    public void scanLocation(int x, int y)
    {
    	double framesTillScan = 0; // returns a non-zero number if we didn't scan
    	int energyPoints = 0;
    	System.out.println("Scanning");
    	for (Unit u : bwapi.getMyUnits())
    	{
    		if (u.getTypeID() == UnitTypes.Terran_Comsat_Station.ordinal())
    		{
    			if (u.getEnergy() >= 50)
    			{
    				bwapi.useTech(u.getID(), 4, x, y);
    			}
    			else
    			{
    				//0.03125 points per frame
    				energyPoints = 50 - u.getEnergy();
    				framesTillScan = ((double) energyPoints)/ .03125;
    			}
    			//return //succesfull
    		}
    	}
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
	
	/*
	 * Initializes the Emun Map for store different Units
	 */
	private void initMilitaryUnit()
	{
		for(UnitTypes ut: UnitTypes.values())
		{
			LinkedList<Unit> tmp = new LinkedList<Unit>();
			unitPool.put(ut, tmp);
		}
	}
	
	/*
	 * Adds a unit to the Enum Map by its specified UnitTypes. Helper method for addCreatedMilitaryUnits.
	 */
	private void addCreatedMilitaryUnitsHelper(Unit unitObj, UnitTypes unitType)
	{
		unitPool.get(unitType).add(unitObj);
		//System.out.println("Military Manager: Added. New size is " + militaryUnits.get(UnitTypes.Terran_Marine).size());
	}
	
	/*
	 * Removes a unit from the Enum Map by its specified UnitTypes. Helper method for removeDestroyedMilitaryUnits.
	 * 
	 * unitID:   unitID of the unit to remove
	 * unitType: unit's UnitTypes for the Enum map
	 */
	private void removeDestroyedMilitaryUnitsHelper(int unitID, UnitTypes unitType)
	{
		for(int index = 0; index < unitPool.get(unitType).size(); index++)
		{
			if(unitID == (unitPool.get(unitType).get(index).getID()))
			{
				unitPool.get(unitType).remove(index);
				//System.out.println("Military Manager: Removed. New size is " + militaryUnits.get(UnitTypes.Terran_Marine).size());
			}
		}
	}
	
	/*
	 * Removes a destroyed units in MilitaryTeams.
	 */
	private void removeUnitInMilitaryTeams(int unitObj)
	{
		for(int index = 0; index < militaryTeams.size(); index++)
		{
			for(int index2 = 0; index2 < militaryTeams.get(index).getMilitaryTeam().size(); index2++)
			{
				Unit tmp = militaryTeams.get(index).getMilitaryTeam().get(index2);
				if(unitObj == (tmp.getID()))
				{
					militaryTeams.get(index).getMilitaryTeam().remove(index2);
					militaryTeams.get(index).setTeamSize(militaryTeams.get(index).getMilitaryTeam().size());
					//System.out.println("Military Manager: removeUnitInMilitaryTeams " + index +". New size is " + militaryTeams.get(index).getMilitaryTeam().size());
				}
			}
		}
	}
	
	/*
	 * Adds a created military unit to the militaryUnits pool. Created for using in RR.
	 * 
	 * createdUnit:   the unit created 
	 * unitTypeID:    it's type for using as the key for militaryUnits to store the unit
	 */
	public void addCreatedMilitaryUnits(Unit createdUnit, int unitTypeID)
	{
		if(unitTypeID == UnitTypes.Terran_Marine.ordinal())
			addCreatedMilitaryUnitsHelper(createdUnit, UnitTypes.Terran_Marine);
		
		if(unitTypeID == UnitTypes.Terran_Firebat.ordinal())
			addCreatedMilitaryUnitsHelper(createdUnit, UnitTypes.Terran_Firebat);
		
		if(unitTypeID == UnitTypes.Terran_Ghost.ordinal())
			addCreatedMilitaryUnitsHelper(createdUnit, UnitTypes.Terran_Ghost);
			
		if(unitTypeID == UnitTypes.Terran_Goliath.ordinal())
			addCreatedMilitaryUnitsHelper(createdUnit, UnitTypes.Terran_Goliath);
			
		if(unitTypeID == UnitTypes.Terran_Medic.ordinal())
			addCreatedMilitaryUnitsHelper(createdUnit, UnitTypes.Terran_Medic);
			
		if(unitTypeID == UnitTypes.Terran_Valkyrie.ordinal())
			addCreatedMilitaryUnitsHelper(createdUnit, UnitTypes.Terran_Valkyrie);
			
		if(unitTypeID == UnitTypes.Terran_Vulture.ordinal())
			addCreatedMilitaryUnitsHelper(createdUnit, UnitTypes.Terran_Vulture);
			
		if(unitTypeID == UnitTypes.Terran_Vulture_Spider_Mine.ordinal())
			addCreatedMilitaryUnitsHelper(createdUnit, UnitTypes.Terran_Vulture_Spider_Mine);
			
		if(unitTypeID == UnitTypes.Terran_Dropship.ordinal())
			addCreatedMilitaryUnitsHelper(createdUnit, UnitTypes.Terran_Dropship);
			
		if(unitTypeID == UnitTypes.Terran_Siege_Tank_Tank_Mode.ordinal())
			addCreatedMilitaryUnitsHelper(createdUnit, UnitTypes.Terran_Siege_Tank_Tank_Mode);
			
		if(unitTypeID == UnitTypes.Terran_Siege_Tank_Siege_Mode.ordinal())
			addCreatedMilitaryUnitsHelper(createdUnit, UnitTypes.Terran_Siege_Tank_Siege_Mode);
			
		if(unitTypeID == UnitTypes.Terran_Wraith.ordinal())
			addCreatedMilitaryUnitsHelper(createdUnit, UnitTypes.Terran_Wraith);
	}
	
	/*
	 * If a unit gets destroyed in the militaryUnits pool or in currUnitGroups, remove it.
	 */
	public void removeDestroyedMilitaryUnits(int destroyedUnit, int unitTypeID)
	{
		if(unitTypeID == UnitTypes.Terran_Marine.ordinal())
		{
			removeDestroyedMilitaryUnitsHelper(destroyedUnit, UnitTypes.Terran_Marine);
			removeUnitInMilitaryTeams(destroyedUnit);
		}
		
		if(unitTypeID == UnitTypes.Terran_Firebat.ordinal())
		{
			removeDestroyedMilitaryUnitsHelper(destroyedUnit, UnitTypes.Terran_Firebat);
			removeUnitInMilitaryTeams(destroyedUnit);
		}
		
		if(unitTypeID == UnitTypes.Terran_Ghost.ordinal())
		{
			removeDestroyedMilitaryUnitsHelper(destroyedUnit, UnitTypes.Terran_Ghost);
			removeUnitInMilitaryTeams(destroyedUnit);
		}
			
		if(unitTypeID == UnitTypes.Terran_Goliath.ordinal())
		{
			removeDestroyedMilitaryUnitsHelper(destroyedUnit, UnitTypes.Terran_Goliath);
			removeUnitInMilitaryTeams(destroyedUnit);
		}
			
		if(unitTypeID == UnitTypes.Terran_Medic.ordinal())
		{
			removeDestroyedMilitaryUnitsHelper(destroyedUnit, UnitTypes.Terran_Medic);
			removeUnitInMilitaryTeams(destroyedUnit);
		}
			
		if(unitTypeID == UnitTypes.Terran_Valkyrie.ordinal())
		{
			removeDestroyedMilitaryUnitsHelper(destroyedUnit, UnitTypes.Terran_Valkyrie);
			removeUnitInMilitaryTeams(destroyedUnit);
		}
			
		if(unitTypeID == UnitTypes.Terran_Vulture.ordinal())
		{
			removeDestroyedMilitaryUnitsHelper(destroyedUnit, UnitTypes.Terran_Vulture);
			removeUnitInMilitaryTeams(destroyedUnit);
		}
			
		if(unitTypeID == UnitTypes.Terran_Vulture_Spider_Mine.ordinal())
		{
			removeDestroyedMilitaryUnitsHelper(destroyedUnit, UnitTypes.Terran_Vulture_Spider_Mine);
			removeUnitInMilitaryTeams(destroyedUnit);
		}
			
		if(unitTypeID == UnitTypes.Terran_Dropship.ordinal())
		{
			removeDestroyedMilitaryUnitsHelper(destroyedUnit, UnitTypes.Terran_Dropship);
			removeUnitInMilitaryTeams(destroyedUnit);
		}
			
		if(unitTypeID == UnitTypes.Terran_Siege_Tank_Tank_Mode.ordinal())
		{
			removeDestroyedMilitaryUnitsHelper(destroyedUnit, UnitTypes.Terran_Siege_Tank_Tank_Mode);
			removeUnitInMilitaryTeams(destroyedUnit);
		}
			
		if(unitTypeID == UnitTypes.Terran_Siege_Tank_Siege_Mode.ordinal())
		{
			removeDestroyedMilitaryUnitsHelper(destroyedUnit, UnitTypes.Terran_Siege_Tank_Siege_Mode);
			removeUnitInMilitaryTeams(destroyedUnit);
		}
			
		if(unitTypeID == UnitTypes.Terran_Wraith.ordinal())
		{
			removeDestroyedMilitaryUnitsHelper(destroyedUnit, UnitTypes.Terran_Wraith);
			removeUnitInMilitaryTeams(destroyedUnit);
		}
	}
	
	/*
	 * Removes destroyed MilitaryTeams (size of its team becomes 0)
	 */
	private void removeEmptyMilitaryTeam()
	{
		for(int index = 0; index < militaryTeams.size(); index++)
		{
			if(militaryTeams.get(index).getMilitaryTeam().size() == 0)
			{
				System.out.println("OLD SIZE: " + militaryTeams.size());
				militaryTeams.remove(index);
				System.out.println("NEW SIZE: " + militaryTeams.size());
			}
		}
	}
	
	/*
	 * Method used for gathering the specified UnitTypes and # of units to attack a location. After units are grouped,
	 * they are sorted in currUnitGroups as a LinkedList for unit group bookkeeping.
	 * 
	 * unitTypes:	UnitTypes of units requested
	 * numOfUnits:  number of units needed (don't know how many units you want each). 
	 *              It will take as much there is on the first UnitTypes before the next.
	 * locationX:   Pixel X coordinate on the map
	 * locationY:   Pixel X coordinate on the map
	 */
	public void unitOperation(LinkedList<UnitTypes> unitTypes, int numOfUnits,  int locationX, int locationY)
	{
		LinkedList<Unit> tmp = new LinkedList<Unit>();
		
		for(UnitTypes unitTy: unitTypes)
		{
			for(int index = 0; index < unitPool.get(unitTy).size(); index++)
			{
				if(tmp.size() < numOfUnits)
				{
					if(unitPool.get(unitTy).get(index).isIdle() && 
							unitPool.get(unitTy).get(index).isCompleted())
					{
						//System.out.println("Military Manager: Adding unit " + ut.getID() + " to group");
						tmp.add(unitPool.get(unitTy).get(index));
						//System.out.println("Military Manager: Added unit " + ut.getID() + " to group");
					}
				}
			}
		}
		
		if(tmp.size() == numOfUnits)
		{
			for(int index = 0; index < (unitPool.get(UnitTypes.Terran_Medic).size()/2); index++)
			{
				if(unitPool.get(UnitTypes.Terran_Medic).get(index).isIdle() 
						&& unitPool.get(UnitTypes.Terran_Medic).get(index).isCompleted())
					tmp.add(unitPool.get(UnitTypes.Terran_Medic).get(index));
			}
			
			militaryTeams.add(new MilitaryTeam(tmp, locationX, locationY));
			removeUsedUnits(tmp);
			unitOperationHelper(tmp, locationX, locationY);
		}
	}
	
	/*
	 * Similar to the above unitOperation, except it only requires a team size and a location. 
	 * Put Marines in a team to attack by default.
	 */
	public void unitOperation(int numOfUnits, int locationX, int locationY)
	{
		LinkedList<Unit> tmp = new LinkedList<Unit>();
		
		for(int index = 0; index < unitPool.get(UnitTypes.Terran_Marine).size(); index++)
		{
			if(tmp.size() < numOfUnits)
			{
				if(unitPool.get(UnitTypes.Terran_Marine).get(index).isIdle() && 
						unitPool.get(UnitTypes.Terran_Marine).get(index).isCompleted())
				{
					tmp.add(unitPool.get(UnitTypes.Terran_Marine).get(index));
				}
			}
		}
		
		if(tmp.size() == numOfUnits)
		{
			militaryTeams.add(new MilitaryTeam(tmp, locationX, locationY));
			removeUsedUnits(tmp);
			unitOperationHelper(tmp, locationX, locationY);
		}
	}
	
	/*
	 * Similar to the above, except team sizes are 5. Put Marines in a team to attack by default.
	 */
	public void unitOperation(int locationX, int locationY)
	{
		LinkedList<Unit> tmp = new LinkedList<Unit>();
		
		for(int index = 0; index < unitPool.get(UnitTypes.Terran_Vulture).size(); index++)
		{
			if(tmp.size() < 5)
			{
				if(unitPool.get(UnitTypes.Terran_Vulture).get(index).isIdle() && 
						unitPool.get(UnitTypes.Terran_Vulture).get(index).isCompleted())
				{
					tmp.add(unitPool.get(UnitTypes.Terran_Vulture).get(index));
				}
			}
		}
		
		if(tmp.size() == 5)
		{
			for(int index = 0; index < (unitPool.get(UnitTypes.Terran_Medic).size()/2); index++)
			{
				if(unitPool.get(UnitTypes.Terran_Medic).get(index).isIdle() 
						&& unitPool.get(UnitTypes.Terran_Medic).get(index).isCompleted())
					tmp.add(unitPool.get(UnitTypes.Terran_Medic).get(index));
			}
			militaryTeams.add(new MilitaryTeam(tmp, locationX, locationY));
			removeUsedUnits(tmp);
			unitOperationHelper(tmp, locationX, locationY);
		}
	}
	
	/*
	 * Returns a UnitTypes enum
	 * 
	 * unitTypeID:   a unit type ID 
	 */
	public UnitTypes getUnitType(int unitTypeID)
	{
		if(unitTypeID == UnitTypes.Terran_Marine.ordinal())
			return UnitTypes.Terran_Marine;
		
		if(unitTypeID == UnitTypes.Terran_Firebat.ordinal())
			return UnitTypes.Terran_Firebat;
		
		if(unitTypeID == UnitTypes.Terran_Ghost.ordinal())
			return UnitTypes.Terran_Ghost;
			
		if(unitTypeID == UnitTypes.Terran_Goliath.ordinal())
			return UnitTypes.Terran_Goliath;
			
		if(unitTypeID == UnitTypes.Terran_Medic.ordinal())
			return UnitTypes.Terran_Medic;
			
		if(unitTypeID == UnitTypes.Terran_Valkyrie.ordinal())
			return UnitTypes.Terran_Valkyrie;
			
		if(unitTypeID == UnitTypes.Terran_Vulture.ordinal())
			return UnitTypes.Terran_Vulture;
			
		if(unitTypeID == UnitTypes.Terran_Vulture_Spider_Mine.ordinal())
			return UnitTypes.Terran_Vulture_Spider_Mine;
			
		if(unitTypeID == UnitTypes.Terran_Dropship.ordinal())
			return UnitTypes.Terran_Dropship;
			
		if(unitTypeID == UnitTypes.Terran_Siege_Tank_Tank_Mode.ordinal())
			return UnitTypes.Terran_Siege_Tank_Tank_Mode;
			
		if(unitTypeID == UnitTypes.Terran_Siege_Tank_Siege_Mode.ordinal())
			return UnitTypes.Terran_Siege_Tank_Siege_Mode;
			
		if(unitTypeID == UnitTypes.Terran_Wraith.ordinal())
			return UnitTypes.Terran_Wraith;
		
		return null;
	}
	
	/*
	 * After storing a group of units that were sent out on an order in currUnitGroups, remove them from militaryUnits
	 * so that units will not be double counted. 
	 */
	private void removeUsedUnits(LinkedList<Unit> usedUnits)
	{	
		if(usedUnits != null)
		{
			for(int index = 0; index < usedUnits.size(); index++)
			{
				UnitTypes tmp = getUnitType(usedUnits.get(index).getTypeID());
				
				for(int index2 = 0; index2 < unitPool.get(tmp).size(); index2++)
				{
					if(usedUnits.get(index).getID() == (unitPool.get(tmp).get(index2).getID()))
					{
						unitPool.get(tmp).remove(index2);
						System.out.println("Military Manager: removeUsedUnits. New size is " + unitPool.get(tmp).size());
					}
				}
			}
		}
	}
	
	/*
	 * Helper function to execute unit attacks to a location 
	 */
	private void unitOperationHelper(LinkedList<Unit> unitGroup, int locationX, int locationY)
	{
		//rallyUnits(unitGroup, homePositionX, homePositionY);
		//System.out.println("Rallied");
		//boolean test = rallyReadyCheck(unitGroup, homePositionX, homePositionY);
		//System.out.println(test);
		//if(rallyReadyCheck(unitGroup, homePositionX, homePositionY))
		//{
			//System.out.println("Military Manager: In unitOperationHelper");
			attackEnemyLocation(unitGroup, locationX, locationY);
			System.out.println("Attacking");
			//System.out.println("Military Manager: Exited unitOperationHelper");
		//}
	}
	
	/*
	 * Commands the Units stored in an LinkedList to rally at a location on the map
	 * 
	 * unitGroup:   LinkedList of Units we wanted to rally
	 * pixelPositionX:  Rally position's X
	 * pixelPositionY:  Rally position's Y
	 * 
	 */
	private void rallyUnits(LinkedList<Unit> unitGroup, int pixelPositionX, int pixelPositionY )
	{
		if(unitGroup != null)
		{
			for(int index = 0; index < unitGroup.size(); index++)
			{
				bwapi.move(unitGroup.get(index).getID(), pixelPositionX, pixelPositionY);
			}
		}
	}
	
	/*
	 * Checks to see if all the units in a group are gathered at Command Center 
	 * before going out to attack together
	 */
	private boolean rallyReadyCheck(LinkedList<Unit> unitGroup, int pixelPositionX, int pixelPositionY)
	{
		boolean checkReadyFlag = false;
		
		if(unitGroup != null)
		{
			while(!checkReadyFlag)
			{
				for(int index = 0; index < unitGroup.size(); index++)
				{
					if(!unitGroup.get(index).isAccelerating())
					{
						checkReadyFlag = true;
					}
				}
			}
		}
		return checkReadyFlag;
	}
	
	/*
	 * Order the Units stored in an LinkedList to attack a location on the map
	 * 
	 * unitGroup:   LinkedList of Units we wanted to rally
	 * pixelPositionX:  Attack position's X
	 * pixelPositionY:  Attack position's Y
	 * 
	 */
	private void attackEnemyLocation(LinkedList<Unit> unitGroup, int pixelPositionX, int pixelPositionY)
	{
		if(unitGroup != null)
		{
			for(int index = 0; index < unitGroup.size(); index++)
			{					
				bwapi.attack(unitGroup.get(index).getID(), pixelPositionX, pixelPositionY);
			}
		}
	}
	
	/*
	 * Orders all unit groups to attack a location on the map
	 */
	public void orderAllMilitaryTeamsToAtk(int pixelPositionX, int pixelPositionY)
	{
		for(int index = 0; index < militaryTeams.size(); index++)
		{
			for(int index2 = 0; index2 < militaryTeams.get(index).getMilitaryTeam().size(); index2++)
			{
				bwapi.attack(militaryTeams.get(index).getMilitaryTeam().get(index2).getID(), pixelPositionX, pixelPositionY);
			}
		}
	}
	
	public void orderUnitPoolToAtk(int pixelPositionX, int pixelPositionY)
	{
		for(UnitTypes unitTy: UnitTypes.values())
		{
			for(int index = 0; index < unitPool.get(unitTy).size(); index++)
			{
				bwapi.attack(unitPool.get(unitTy).get(index).getID(), pixelPositionX, pixelPositionY);
			}
		}
	}
	
	/*
	 * Handles different unit mechanics. This needs to be in checkup to be checkedup every second.
	 */
	public void handleUnitsAttacking()
	{
		for(int index = 0; index < militaryTeams.size(); index++)
		{
			for(int index2 = 0; index2 < militaryTeams.get(index).getMilitaryTeam().size(); index2++)
			{
				Unit tmp = militaryTeams.get(index).getMilitaryTeam().get(index2);
				
				if(tmp.getTypeID() == UnitTypes.Terran_Vulture.ordinal())
				{
					if(tmp.isStartingAttack())
					{
						bwapi.useTech(tmp.getID(), TechTypes.Spider_Mines.ordinal(), tmp.getX(), tmp.getY());
					}
				}
				
				if(tmp.getTypeID() == UnitTypes.Terran_Ghost.ordinal())
				{
					bwapi.cloak(tmp.getID());
					if(tmp.isCloaked())
					{	
						bwapi.useTech(tmp.getID(), TechTypes.Nuclear_Strike.ordinal(), militaryTeams.get(index).getX(), militaryTeams.get(index).getY());
					}
				}
				
				if(tmp.getTypeID() == UnitTypes.Terran_Siege_Tank_Tank_Mode.ordinal())
				{					
					if(tmp.isAttacking())
					{
						bwapi.siege(tmp.getID());
					}
					else if(tmp.isStartingAttack())
					{
						bwapi.siege(tmp.getID());
					}
					else if(tmp.isIdle())
					{
						bwapi.attack(tmp.getID(), militaryTeams.get(index).getX(), militaryTeams.get(index).getY());
					}
				}
				
				if(tmp.getTypeID() == UnitTypes.Terran_Siege_Tank_Siege_Mode.ordinal())
				{
					if((tmp.isIdle()))
					{
						bwapi.unsiege(tmp.getID());
					}
				}
			}
		}
	}
	
	/*
	 * Returns the current number of units of a UnitType
	 */
	private int getCurrentUnitCount(UnitTypes unitType)
	{
		return unitPool.get(unitType).size();
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
