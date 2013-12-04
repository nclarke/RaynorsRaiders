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
import javabot.RaynorsRaiders.CoreReactive.*;

public class ManagerMilitary extends RRAITemplate
{
	int homePositionX, homePositionY;
	private BaseLocation homeBase;
	
	// Unit pool: military units gets added to this map as they are created 
	EnumMap<UnitTypes, LinkedList<Unit>> unitPool;
	
	// Military Teams: they are created in unitOperation each time an order is given from CoreBaby
	private LinkedList<MilitaryTeam> militaryTeams;
	
	private enum RallyStatus
	{
		NOT_RALLIED, RALLIED
	};
	
	private enum DispatchStatus
	{
		NOT_DISPATCHED, DISPATCHED
	};
	
	private enum TeamStatus
	{
		ATTACK, RETREAT, IDLE
	};
	
	private class MilitaryTeam
	{
		LinkedList<Unit> militaryTeam;
		int attackLocationX, attackLocationY;
		int currentTeamSize;
		RallyStatus rallyStatus;
		DispatchStatus dispatchStatus;
		TeamStatus teamStatus;
		
		public MilitaryTeam(LinkedList<Unit> militaryTeam, int attackLocationX,  int attackLocationY)
		{
			this.militaryTeam = militaryTeam;
			this.attackLocationX = attackLocationX;
			this.attackLocationY = attackLocationY;
			this.currentTeamSize = militaryTeam.size();
			this.rallyStatus = RallyStatus.NOT_RALLIED;
			this.dispatchStatus = DispatchStatus.NOT_DISPATCHED;
			this.teamStatus = TeamStatus.IDLE;
		}
		
		public LinkedList<Unit> getMilitaryTeam()
		{
			return militaryTeam;
		}
		
		public int getX()
		{
			return attackLocationX;
		}
		
		public int getY()
		{
			return attackLocationY;
		}
		
		public void setLocation(int x, int y)
		{
			this.attackLocationX = x;
			this.attackLocationY = y;
		}
		
		public int getTeamSize()
		{
			return currentTeamSize;
		}
		
		public void setTeamSize(int newSize)
		{
			currentTeamSize = newSize;
		}
		
		public RallyStatus getRallyStatus()
		{
			return rallyStatus;
		}
		
		public void setRallyStatus(RallyStatus rs)
		{
			rallyStatus = rs;
		}
		
		public DispatchStatus getDispatchStatus()
		{
			return dispatchStatus;
		}
		
		public void setDispatchStatus(DispatchStatus ds)
		{
			dispatchStatus = ds;
		}
		
		public TeamStatus getTeamStatus()
		{
			return teamStatus;
		}
		
		public void setTeamStatus(TeamStatus ts)
		{
			teamStatus = ts;
		}
	}
	
	public class UnitTypesRequest
	{
		UnitTypes unitTy;
		int numOfUnits;
		
		public UnitTypesRequest(UnitTypes unitTypesNeeded, int numOfNeeded)
		{
			this.unitTy = unitTypesNeeded;
			this.numOfUnits = numOfNeeded;
		}
		
		public UnitTypes getUnitTypes()
		{
			return this.unitTy;
		}
		
		public int getNumOfUnits()
		{
			return this.numOfUnits;
		}
	}
	
	private int getTotalNumOfUnits(LinkedList<UnitTypesRequest> utr)
	{
		int total = 0;
		for(int index = 0; index < utr.size(); index++)
		{
			total += utr.get(index).getNumOfUnits();
		}
		return total;
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
		System.out.println("START OF MM CHECKUP");
		attackLocationsTest();
		
		rallyTeamToAttack();
		handleUnitsAttacking();
		handleUnitPoolAttacks();
		maintainAttackLocations();
		//handleTeamStatus();
		removeEmptyMilitaryTeam();
		System.out.println("END OF MM CHECKUP");
	}
	
	/*
	 * Meant for testings in MM ONLY. 
	 */
	private void attackLocationsTest()
	{
		for (BaseLocation b : bwapi.getMap().getBaseLocations()) 
		{
			if (b.isStartLocation() )
			{
				if((b.getX() != homePositionX) && (b.getY() != homePositionY))
				{
					unitOperation(b.getX(), b.getY()); 
				}
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
    
    public void testSiege()
    {
		Unit toAttack;
    	Unit tank = unitPool.get(UnitTypes.Terran_Siege_Tank_Tank_Mode).get(0);
    	toAttack = getNearestEnemyUnit(tank.getX(), tank.getY());
		if (getEnemiesWithinUnitSightRange(tank) > 0)
		{
			//bwapi.siege(tank.getID());
		}
		else
		{
			bwapi.attack(tank.getID(), toAttack.getX(), toAttack.getY());
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
	    	double dist = Math.pow(unit.getX() - x, 2) + Math.pow(unit.getY() - y, 2);
	    	if (nearestID == -1 || dist < nearestDist) 
	    	{
	    		toRtn = unit;
	    		nearestDist = dist;
	    	}
	    }
	    return toRtn;
    }
    
    public int getEnemiesWithinUnitSightRange(Unit ourUnit)
    {
    	int count = 0, radius;
    	double distance;
    	UnitType ut;
    	int unitID;
    	unitID = ourUnit.getID();
    	ut = bwapi.getUnitType(unitID);
    	ourUnit = bwapi.getUnit(unitID);
    	radius = ut.getSightRange();
    	for (Unit u : bwapi.getEnemyUnits())
    	{
    		distance = Math.pow(u.getX() - ourUnit.getX(),2) + Math.pow(u.getY() - ourUnit.getY(), 2);
    		if (distance < (radius * radius))
    		{
    			count++;
    		}
    	}
    	return count;
    }
    
    public void testStutter()
    {
    	Player p;
    
    	Unit toAttack;
    	Unit vult = null;
    	//Unit vult = militaryTeams.get(UnitTypes.Terran_Vulture).get(0);
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
    
    public void wraithAttack()
    {
    	
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
    
	private void setHomePosition()
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

				//System.out.println("Military Manager: Removed. New size is " + unitPool.get(unitType).size());
			}
		}
	}
	
	/*
	 * Removes a destroyed units in MilitaryTeams.
	 */
	private void removeUnitInMilitaryTeams(int unitObj)
	{
		int newSize;
		
		for(int index = 0; index < militaryTeams.size(); index++)
		{
			for(int index2 = 0; index2 < militaryTeams.get(index).getMilitaryTeam().size(); index2++)
			{
				Unit tmp = militaryTeams.get(index).getMilitaryTeam().get(index2);
				if(unitObj == (tmp.getID()))
				{
					//System.out.println("MILITARYTEAMS" );
					militaryTeams.get(index).getMilitaryTeam().remove(index2);
					newSize = militaryTeams.get(index).getMilitaryTeam().size();
					militaryTeams.get(index).setTeamSize(newSize);
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
		
		if(unitTypeID == UnitTypes.Terran_Science_Vessel.ordinal())
			addCreatedMilitaryUnitsHelper(createdUnit, UnitTypes.Terran_Science_Vessel);
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
		
		if(unitTypeID == UnitTypes.Terran_Science_Vessel.ordinal())
		{
			removeDestroyedMilitaryUnitsHelper(destroyedUnit, UnitTypes.Terran_Science_Vessel);
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
				System.out.println("MM: OLD MILITARYTEAMS SIZE: " + militaryTeams.size() );
				militaryTeams.remove(index);
				System.out.println("MM: NEW MILITARYTEAMS SIZE: " +  militaryTeams.size() );
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
	 * locationY:   Pixel Y coordinate on the map
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
							unitPool.get(unitTy).get(index).isCompleted() && 
							unitPool.get(unitTy).get(index).isExists())
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
			if(!containsUnitTypeInList(tmp, UnitTypes.Terran_Medic))
			{
				for(int index = 0; index < (unitPool.get(UnitTypes.Terran_Medic).size()/2); index++)
				{
					if(unitPool.get(UnitTypes.Terran_Medic).get(index).isIdle() 
							&& unitPool.get(UnitTypes.Terran_Medic).get(index).isCompleted())
						tmp.add(unitPool.get(UnitTypes.Terran_Medic).get(index));
				}
			}
			
			if(!containsUnitTypeInList(tmp, UnitTypes.Terran_Science_Vessel))
			{
				for(int index = 0; index < (unitPool.get(UnitTypes.Terran_Science_Vessel).size()/3); index++)
				{
					if(unitPool.get(UnitTypes.Terran_Science_Vessel).get(index).isIdle() 
							&& unitPool.get(UnitTypes.Terran_Science_Vessel).get(index).isCompleted())
						tmp.add(unitPool.get(UnitTypes.Terran_Science_Vessel).get(index));
				}
			}
			
			militaryTeams.add(new MilitaryTeam(tmp, locationX, locationY));
			removeUsedUnits(tmp);
		}
	}
	
	public void unitOperation(LinkedList<UnitTypesRequest> unitTypes, int locationX, int locationY)
	{
		int numOfUnits = getTotalNumOfUnits(unitTypes);
		
		LinkedList<Unit> tmp = new LinkedList<Unit>();
		
		for(int index = 0; index < unitTypes.size(); index++)
		{
			for(int index2 = 0; index2 < unitTypes.get(index).getNumOfUnits(); index2++)
			{
				if(unitPool.get(unitTypes.get(index).getUnitTypes()).get(index).isIdle() && 
						unitPool.get(unitTypes.get(index).getUnitTypes()).get(index).isCompleted() && 
						unitPool.get(unitTypes.get(index).getUnitTypes()).get(index).isExists())
				tmp.add(unitPool.get(unitTypes.get(index).getUnitTypes()).get(index2));
			}
		}
		
		if(tmp.size() == numOfUnits)
		{
			if(!containsUnitTypeInList(tmp, UnitTypes.Terran_Medic))
			{
				for(int index = 0; index < (unitPool.get(UnitTypes.Terran_Medic).size()/2); index++)
				{
					if(unitPool.get(UnitTypes.Terran_Medic).get(index).isIdle() 
							&& unitPool.get(UnitTypes.Terran_Medic).get(index).isCompleted())
						tmp.add(unitPool.get(UnitTypes.Terran_Medic).get(index));
				}
			}
			
			if(!containsUnitTypeInList(tmp, UnitTypes.Terran_Science_Vessel))
			{
				for(int index = 0; index < (unitPool.get(UnitTypes.Terran_Science_Vessel).size()/3); index++)
				{
					if(unitPool.get(UnitTypes.Terran_Science_Vessel).get(index).isIdle() 
							&& unitPool.get(UnitTypes.Terran_Science_Vessel).get(index).isCompleted())
						tmp.add(unitPool.get(UnitTypes.Terran_Science_Vessel).get(index));
				}
			}
			
			militaryTeams.add(new MilitaryTeam(tmp, locationX, locationY));
			removeUsedUnits(tmp);
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
						unitPool.get(UnitTypes.Terran_Marine).get(index).isCompleted() &&
						unitPool.get(UnitTypes.Terran_Marine).get(index).isExists())
				{
					tmp.add(unitPool.get(UnitTypes.Terran_Marine).get(index));
				}
			}
		}
		
		if(tmp.size() == numOfUnits)
		{
			militaryTeams.add(new MilitaryTeam(tmp, locationX, locationY));
			removeUsedUnits(tmp);
		}
	}
	
	private boolean containsUnitTypeInList(LinkedList<Unit> units, UnitTypes ut)
	{
		for(int index = 0; index < units.size(); index++)
		{
			if(units.get(index).getTypeID() == ut.ordinal())
				return true;
		}
		return false;
	}
	
	/*
	 * Similar to the above, except team sizes are 5. Put Marines in a team to attack by default.
	 * This method is mainly used for TESTING.
	 */
	private void unitOperation(int locationX, int locationY)
	{
		LinkedList<Unit> tmp = new LinkedList<Unit>();
		
		for(int index = 0; index < unitPool.get(UnitTypes.Terran_Marine).size(); index++)
		{			
			if(tmp.size() < 5)
			{
				if(unitPool.get(UnitTypes.Terran_Marine).get(index).isIdle() && 
						unitPool.get(UnitTypes.Terran_Marine).get(index).isCompleted() && 
						unitPool.get(UnitTypes.Terran_Marine).get(index).isExists())
				{
					tmp.add(unitPool.get(UnitTypes.Terran_Marine).get(index));
				}
			}
		}
		
		if(tmp.size() == 5)
		{
			if(!containsUnitTypeInList(tmp, UnitTypes.Terran_Medic))
			{
				for(int index = 0; index < (unitPool.get(UnitTypes.Terran_Medic).size()/2); index++)
				{
					if(unitPool.get(UnitTypes.Terran_Medic).get(index).isIdle() 
							&& unitPool.get(UnitTypes.Terran_Medic).get(index).isCompleted())
						tmp.add(unitPool.get(UnitTypes.Terran_Medic).get(index));
				}
			}
			
			if(!containsUnitTypeInList(tmp, UnitTypes.Terran_Science_Vessel))
			{
				for(int index = 0; index < (unitPool.get(UnitTypes.Terran_Science_Vessel).size()/3); index++)
				{
					if(unitPool.get(UnitTypes.Terran_Science_Vessel).get(index).isIdle() 
							&& unitPool.get(UnitTypes.Terran_Science_Vessel).get(index).isCompleted())
						tmp.add(unitPool.get(UnitTypes.Terran_Science_Vessel).get(index));
				}
			}
			militaryTeams.add(new MilitaryTeam(tmp, locationX, locationY));
			removeUsedUnits(tmp);
		}
	}
	
	/*
	 * Returns a UnitTypes enum
	 * 
	 * unitTypeID:   a unit type ID 
	 */
	private UnitTypes getUnitType(int unitTypeID)
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
					}
				}
			}
		}
	}
	
	/*
	 * Rallies a newly created MilitaryTeam at the Command Center and then orders them to carry out the attack.
	 */
	private void rallyTeamToAttack()
	{
		for(int index = 0; index < militaryTeams.size(); index++)
		{
			if(militaryTeams.get(index).getRallyStatus().equals(RallyStatus.NOT_RALLIED) && 
					(militaryTeams.get(index).getDispatchStatus().equals(DispatchStatus.NOT_DISPATCHED)))
			{
				for(int index2 = 0; index2 < militaryTeams.get(index).getMilitaryTeam().size(); index2++)
				{
					bwapi.move(militaryTeams.get(index).getMilitaryTeam().get(index2).getID(), homePositionX, homePositionY);
				}
			}
			
			if(rallyReadyCheck(militaryTeams.get(index).getMilitaryTeam(), homePositionX, homePositionY))
			{
				militaryTeams.get(index).setRallyStatus(RallyStatus.RALLIED);
				attackEnemyLocation();
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
			for(int index = 0; index < unitGroup.size(); index++)
			{	
				if(unitGroup.get(index).isExists())
				{
					double dist = Math.sqrt(Math.pow(unitGroup.get(index).getX() - pixelPositionX, 2) + Math.pow(unitGroup.get(index).getY() - pixelPositionY, 2));
					
					if(dist <= 140)
					{
						checkReadyFlag = true;
					}
					else
					{
						return false;
					}
				}
			}
		}
		
		return checkReadyFlag;
	}
	
	/*
	 * Once a team is rallied and not attacking yet, ask them to go attack.
	 */
	private void attackEnemyLocation()
	{
		for(int index = 0; index < militaryTeams.size(); index++)
		{
			if(militaryTeams.get(index).getDispatchStatus().equals(DispatchStatus.NOT_DISPATCHED) && 
					(militaryTeams.get(index).getRallyStatus().equals(RallyStatus.RALLIED)))
			{
				for(int index2 = 0; index2 < militaryTeams.get(index).getMilitaryTeam().size(); index2++)
				{
					bwapi.attack(militaryTeams.get(index).getMilitaryTeam().get(index2).getID(), militaryTeams.get(index).getX(), militaryTeams.get(index).getY());
				}
			}
			militaryTeams.get(index).setDispatchStatus(DispatchStatus.DISPATCHED);
			militaryTeams.get(index).setTeamStatus(TeamStatus.ATTACK);
		}
	}
	
	private void maintainAttackLocations()
	{
		for(int index = 0; index < militaryTeams.size(); index++)
		{
			if(militaryTeams.get(index).getDispatchStatus().equals(DispatchStatus.DISPATCHED) && 
					(militaryTeams.get(index).getRallyStatus().equals(RallyStatus.RALLIED) &&
							(militaryTeams.get(index).getTeamStatus().equals(TeamStatus.ATTACK))))
			{
				for(int index2 = 0; index2 < militaryTeams.get(index).getMilitaryTeam().size(); index2++)
				{
					bwapi.attack(militaryTeams.get(index).getMilitaryTeam().get(index2).getID(), militaryTeams.get(index).getX(), militaryTeams.get(index).getY());
				}
			}
		}
	}
	
	public void handleTeamStatus()
	{
		int initialTeamSize = 0;
		
		for(int index = 0; index < militaryTeams.size(); index++)
		{
			if(militaryTeams.get(index).getDispatchStatus().equals(DispatchStatus.DISPATCHED) && 
					(militaryTeams.get(index).getTeamStatus().equals(TeamStatus.ATTACK)))
			{
				initialTeamSize = militaryTeams.get(index).getTeamSize();
				
				for(int index2 = 0; index2 < militaryTeams.get(index).getMilitaryTeam().size(); index2++)
				{
					if(militaryTeams.get(index).getMilitaryTeam().get(index2).isStartingAttack())
					{
						//System.out.println("TeamSize: " + militaryTeams.get(index).getTeamSize());
						if(militaryTeams.get(index).getTeamSize() < (initialTeamSize))
						{
							militaryTeams.get(index).setTeamStatus(TeamStatus.RETREAT);
							bwapi.move(militaryTeams.get(index).getMilitaryTeam().get(index2).getID(), homePositionX, homePositionY);
						}
					}
				}
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
	
	/*
	 * Order the unit pool (unused units in the base) to attack a location.
	 */
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
	private void handleUnitsAttacking()
	{
		for(int index = 0; index < militaryTeams.size(); index++)
		{
			for(int index2 = 0; index2 < militaryTeams.get(index).getMilitaryTeam().size(); index2++)
			{
				Unit tmp = militaryTeams.get(index).getMilitaryTeam().get(index2);
				
				if(tmp.getTypeID() == UnitTypes.Terran_Marine.ordinal())
				{
					if((tmp.isStartingAttack()) && (!tmp.isStimmed()))
					{
						bwapi.useTech(tmp.getID(), TechTypes.Stim_Packs.ordinal());
					}
					
					//commented out: causing crashes 
					/*for(int groupUnits = 0; groupUnits < militaryTeams.get(index).getMilitaryTeam().size(); groupUnits++)
					{
						int targetID = militaryTeams.get(index).getMilitaryTeam().get(groupUnits).getTargetUnitID();
						Unit targetUnit = bwapi.getUnit(targetID);
						
						if(targetUnit.getHitPoints() <= targetUnit.getInitialHitPoints()/2)
						{
							bwapi.attack(tmp.getID(), targetID);
						}
					}*/
				}
				
				if(tmp.getTypeID() == UnitTypes.Terran_Medic.ordinal())
				{
					for(int groupUnits = 0; groupUnits < militaryTeams.get(index).getMilitaryTeam().size(); groupUnits++)
					{
						if((militaryTeams.get(index).getMilitaryTeam().get(groupUnits).getTypeID() != UnitTypes.Terran_Medic.ordinal()) &&
								(militaryTeams.get(index).getMilitaryTeam().get(groupUnits).getTypeID() != UnitTypes.Terran_Science_Vessel.ordinal()))
						{
							bwapi.follow(tmp.getID(), militaryTeams.get(index).getMilitaryTeam().get(groupUnits).getID());
						}
						
						if(militaryTeams.get(index).getMilitaryTeam().get(groupUnits).getHitPoints() < 
								militaryTeams.get(index).getMilitaryTeam().get(groupUnits).getInitialHitPoints())
						{
							bwapi.useTech(tmp.getID(), TechTypes.Healing.ordinal(), 
									militaryTeams.get(index).getMilitaryTeam().get(groupUnits).getX(),
									militaryTeams.get(index).getMilitaryTeam().get(groupUnits).getY());
						}
					}
				}
				
				if(tmp.getTypeID() == UnitTypes.Terran_Science_Vessel.ordinal())
				{
					for(int groupUnits = 0; groupUnits < militaryTeams.get(index).getMilitaryTeam().size(); groupUnits++)
					{
						if((militaryTeams.get(index).getMilitaryTeam().get(groupUnits).getTypeID() != UnitTypes.Terran_Medic.ordinal()) &&
								(militaryTeams.get(index).getMilitaryTeam().get(groupUnits).getTypeID() != UnitTypes.Terran_Science_Vessel.ordinal()))
						{
							bwapi.follow(tmp.getID(), militaryTeams.get(index).getMilitaryTeam().get(groupUnits).getID());
						}
					}
				}
				
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
	
	public void handleUnitMicros()
	{
		for(int index = 0; index < militaryTeams.size(); index++)
		{
			for(int index2 = 0; index2 < militaryTeams.get(index).getMilitaryTeam().size(); index2++)
			{
				Unit tmp = militaryTeams.get(index).getMilitaryTeam().get(index2);
				Unit toAttack = getNearestEnemyUnit(tmp.getX(), tmp.getY());
				
				if(tmp.getTypeID() == UnitTypes.Terran_Vulture.ordinal())
				{			        
			    	if (tmp.getGroundWeaponCooldown() == 0)
			    	{
			    		bwapi.attack(tmp.getID(), toAttack.getID());
			    	}
			    	else
			    	{
			    		bwapi.move(tmp.getID(), -(toAttack.getX()), -(toAttack.getY()));
			    	}
				}
				
				/*if(tmp.getTypeID() == UnitTypes.Terran_Marine.ordinal())
				{			        
			    	if (tmp.getGroundWeaponCooldown() == 0)
			    	{
			    		bwapi.attack(tmp.getID(), toAttack.getID());
			    	}
			    	else
			    	{
			    		bwapi.move(tmp.getID(), -(toAttack.getX()), -(toAttack.getY()));
			    	}
				}*/
			}
		}
	}
	
	private void handleUnitPoolAttacks()
	{
		for(UnitTypes ut: UnitTypes.values())
		{
			for(int index = 0; index < unitPool.get(ut).size(); index++)
			{
				Unit poolUnit = unitPool.get(ut).get(index);
				
				if(poolUnit.getTypeID() == UnitTypes.Terran_Marine.ordinal())
				{
					if(poolUnit.isStartingAttack() && (!poolUnit.isStimmed()))
					{
						bwapi.useTech(poolUnit.getID(), TechTypes.Stim_Packs.ordinal());
					}
				}
				
				if(poolUnit.getTypeID() == UnitTypes.Terran_Vulture.ordinal())
				{
					if(poolUnit.isStartingAttack())
					{
						bwapi.useTech(poolUnit.getID(), TechTypes.Spider_Mines.ordinal(), poolUnit.getX(), poolUnit.getY());
					}
				}
				
				if(poolUnit.getTypeID() == UnitTypes.Terran_Ghost.ordinal())
				{
					if(poolUnit.isUnderAttack())
					{	
						bwapi.cloak(poolUnit.getID());
					}
				}
				
				if(poolUnit.getTypeID() == UnitTypes.Terran_Siege_Tank_Tank_Mode.ordinal())
				{
					int xtile, ytile;
					Region r = react.gen_findClosestRegion(homePositionX, homePositionY);
					ChokePoint cp = null;

					if (r != null) 
					{
						if (r.getChokePoints().isEmpty())
						{
							//System.out.println("No chokepoint?");
						}
						else 
						{
							cp = r.getChokePoints().get(0);
						}
					}
					
					xtile = cp.getFirstSideX();
					ytile = cp.getFirstSideY();
					
					bwapi.move(poolUnit.getID(), xtile, ytile);
					
					if(poolUnit.isStartingAttack())
					{
						bwapi.siege(poolUnit.getID());
					}
				}
				
				if(poolUnit.getTypeID() == UnitTypes.Terran_Siege_Tank_Siege_Mode.ordinal())
				{
					if((poolUnit.isIdle()))
					{
						bwapi.unsiege(poolUnit.getID());
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
	
	/* Returns the id of a unit of a given type, that is closest to a pixel position (x,y), or -1 if we
     * don't have a unit of this type
     */
    private int getNearestUnit(int unitTypeID, int x, int y) 
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
