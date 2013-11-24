package javabot.RaynorsRaiders;

import java.awt.Point;
import java.util.*;

import javabot.JNIBWAPI;
import javabot.RaynorsRaiders.Level;
import javabot.model.*;
import javabot.types.UnitType;
import javabot.types.UnitType.UnitTypes;
import javabot.util.*;

public class ManagerMilitary extends RRAITemplate
{
	int homePositionX, homePositionY;
	BaseLocation homeBase;
	
	EnumMap<UnitTypes, LinkedList<Unit>> militaryUnits;
	LinkedList<LinkedList<Unit>> currUnitGroups;
	
	public ManagerMilitary()
	{		
		militaryUnits = new EnumMap<UnitTypes, LinkedList<Unit>>(UnitTypes.class);
		currUnitGroups = new LinkedList<LinkedList<Unit>>();
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
		/*for (BaseLocation b : bwapi.getMap().getBaseLocations()) 
		{
			if (b.isStartLocation() )
			{
				unitOperation(b.getX(), b.getY()); 
				
				for(int index = 0; index < currUnitGroups.size(); index++)
				{
					handleUnitsAttacking(currUnitGroups.get(index), b.getX(), b.getY());
				}
			}
		}*/
	}
	
    public void debug()
    {
    	/*int spacing = 10;
		
		bwapi.drawText(new Point(5,0), "Our home position: " + String.valueOf(homePositionX) + ", " + String.valueOf(homePositionY), true);
		for (BaseLocation b : bwapi.getMap().getBaseLocations()) {
			if (b.isStartLocation()) {
				int index  = spacing/10;
				bwapi.drawText(new Point(5,spacing), "Base position " + index + ": " + String.valueOf(b.getX()) + ", " + String.valueOf(b.getY()), true);
			}
			spacing =+ 10;
		}*/
		
		//bwapi.drawText(new Point(5,120), "Total Marines trained: " + String.valueOf(getCurrentUnitCount(UnitTypes.Terran_Marine)), true);
    }
    
    public void testVult() 
    {
    	Unit vult = militaryUnits.get(UnitTypes.Terran_Vulture).get(0);
    	if (vult.isIdle())
    	{
    		javabot.types.TechType techType = bwapi.getTechType(3);
    		bwapi.useTech(vult.getID(), 3, 100, 100);
    		//bwapi.getTechType(`)
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
	
	
	private void initMilitaryUnit()
	{
		for(UnitTypes ut: UnitTypes.values())
		{
			LinkedList<Unit> tmp = new LinkedList<Unit>();
			militaryUnits.put(ut, tmp);
		}
	}
	
	public void addMilitaryUnit(Unit unitObj, UnitTypes unitType)
	{
		//System.out.println("Military Manager: Adding unit " + unitObj.getID() + " to militaryUnit");
		militaryUnits.get(unitType).add(unitObj);
		System.out.println("Military Manager: Added. New size is " + militaryUnits.get(UnitTypes.Terran_Marine).size());
		//System.out.println("Military Manager: Added unit " + unitObj.getID() + " to militaryUnit");
	}
	
	public void removeMilitaryUnit(int unitObj, UnitTypes unitType)
	{
		//System.out.println("Military Manager: Removing unit " + unitObj.getID() + " in militaryUnit");
		for(int index = 0; index < militaryUnits.get(unitType).size(); index++)
		{
			if(unitObj == (militaryUnits.get(unitType).get(index).getID()))
			{
				militaryUnits.get(unitType).remove(index);
				System.out.println("Military Manager: Removed. New size is " + militaryUnits.get(UnitTypes.Terran_Marine).size());
			}
		}
		//System.out.println("Military Manager: Removd unit " + unitObj.getID() + " in militaryUnit");
	}
	
	public void removeUnitInUnitGroup(int unitObj, UnitTypes unitType)
	{
		//System.out.println("Military Manager: Removing unit " + unitObj.getID() + " in militaryUnit");
		for(int index = 0; index < currUnitGroups.size(); index++)
		{
			for(int index2 = 0; index2 < currUnitGroups.get(index).size(); index2++)
			{
				if(unitObj == (currUnitGroups.get(index).get(index2).getID()))
				{
					currUnitGroups.get(index).remove(index2);
					System.out.println("Military Manager: removeUnitInUnitGroup " + index +". New size is " + currUnitGroups.get(index).size());
				}
			}
		}
		//System.out.println("Military Manager: Removd unit " + unitObj.getID() + " in militaryUnit");
	}
	
	public void addCreatedMilitaryUnits(Unit createdUnit, int unitTypeID)
	{
		if(unitTypeID == UnitTypes.Terran_Marine.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Marine);
		
		if(unitTypeID == UnitTypes.Terran_Firebat.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Firebat);
		
		if(unitTypeID == UnitTypes.Terran_Ghost.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Ghost);
			
		if(unitTypeID == UnitTypes.Terran_Goliath.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Goliath);
			
		if(unitTypeID == UnitTypes.Terran_Medic.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Medic);
			
		if(unitTypeID == UnitTypes.Terran_Valkyrie.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Valkyrie);
			
		if(unitTypeID == UnitTypes.Terran_Vulture.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Vulture);
			
		if(unitTypeID == UnitTypes.Terran_Vulture_Spider_Mine.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Vulture_Spider_Mine);
			
		if(unitTypeID == UnitTypes.Terran_Dropship.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Dropship);
			
		if(unitTypeID == UnitTypes.Terran_Siege_Tank_Tank_Mode.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Siege_Tank_Tank_Mode);
			
		if(unitTypeID == UnitTypes.Terran_Siege_Tank_Siege_Mode.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Siege_Tank_Siege_Mode);
			
		if(unitTypeID == UnitTypes.Terran_Wraith.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Wraith);
	}
	
	public void removeDestroyedMilitaryUnits(int destroyedUnit, int unitTypeID)
	{
		if(unitTypeID == UnitTypes.Terran_Marine.ordinal())
		{
			removeMilitaryUnit(destroyedUnit, UnitTypes.Terran_Marine);
			removeUnitInUnitGroup(destroyedUnit, UnitTypes.Terran_Marine);
		}
		
		if(unitTypeID == UnitTypes.Terran_Firebat.ordinal())
		{
			removeMilitaryUnit(destroyedUnit, UnitTypes.Terran_Firebat);
			removeUnitInUnitGroup(destroyedUnit, UnitTypes.Terran_Marine);
		}
		
		if(unitTypeID == UnitTypes.Terran_Ghost.ordinal())
		{
			removeMilitaryUnit(destroyedUnit, UnitTypes.Terran_Ghost);
			removeUnitInUnitGroup(destroyedUnit, UnitTypes.Terran_Ghost);
		}
			
		if(unitTypeID == UnitTypes.Terran_Goliath.ordinal())
		{
			removeMilitaryUnit(destroyedUnit, UnitTypes.Terran_Goliath);
			removeUnitInUnitGroup(destroyedUnit, UnitTypes.Terran_Goliath);
		}
			
		if(unitTypeID == UnitTypes.Terran_Medic.ordinal())
		{
			removeMilitaryUnit(destroyedUnit, UnitTypes.Terran_Medic);
			removeUnitInUnitGroup(destroyedUnit, UnitTypes.Terran_Medic);
		}
			
		if(unitTypeID == UnitTypes.Terran_Valkyrie.ordinal())
		{
			removeMilitaryUnit(destroyedUnit, UnitTypes.Terran_Valkyrie);
			removeUnitInUnitGroup(destroyedUnit, UnitTypes.Terran_Valkyrie);
		}
			
		if(unitTypeID == UnitTypes.Terran_Vulture.ordinal())
		{
			removeMilitaryUnit(destroyedUnit, UnitTypes.Terran_Vulture);
			removeUnitInUnitGroup(destroyedUnit, UnitTypes.Terran_Vulture);
		}
			
		if(unitTypeID == UnitTypes.Terran_Vulture_Spider_Mine.ordinal())
		{
			removeMilitaryUnit(destroyedUnit, UnitTypes.Terran_Vulture_Spider_Mine);
			removeUnitInUnitGroup(destroyedUnit, UnitTypes.Terran_Vulture_Spider_Mine);
		}
			
		if(unitTypeID == UnitTypes.Terran_Dropship.ordinal())
		{
			removeMilitaryUnit(destroyedUnit, UnitTypes.Terran_Dropship);
			removeUnitInUnitGroup(destroyedUnit, UnitTypes.Terran_Dropship);
		}
			
		if(unitTypeID == UnitTypes.Terran_Siege_Tank_Tank_Mode.ordinal())
		{
			removeMilitaryUnit(destroyedUnit, UnitTypes.Terran_Siege_Tank_Tank_Mode);
			removeUnitInUnitGroup(destroyedUnit, UnitTypes.Terran_Siege_Tank_Tank_Mode);
		}
			
		if(unitTypeID == UnitTypes.Terran_Siege_Tank_Siege_Mode.ordinal())
		{
			removeMilitaryUnit(destroyedUnit, UnitTypes.Terran_Siege_Tank_Siege_Mode);
			removeUnitInUnitGroup(destroyedUnit, UnitTypes.Terran_Siege_Tank_Siege_Mode);
		}
			
		if(unitTypeID == UnitTypes.Terran_Wraith.ordinal())
		{
			removeMilitaryUnit(destroyedUnit, UnitTypes.Terran_Wraith);
			removeUnitInUnitGroup(destroyedUnit, UnitTypes.Terran_Wraith);
		}
	}
	
	/*
	 * Method used for gathering the specified UnitTypes and # of units to attack a location
	 * 
	 * unitTypes:	UnitTypes of units requested
	 * numOfUnits:  number of units needed (don't know how many units you want each)
	 * locationX:   Pixel X coordinate on the map
	 * locationY:   Pixel X coordinate on the map
	 */
	public void unitOperation(LinkedList<UnitTypes> unitTypes, int numOfUnits,  int locationX, int locationY)
	{
		LinkedList<Unit> tmp = new LinkedList<Unit>();
		
		for(UnitTypes unitTy: unitTypes)
		{
			for(int index = 0; index < militaryUnits.get(unitTy).size(); index++)
			{
				if(tmp.size() < numOfUnits)
				{
					if(militaryUnits.get(unitTy).get(index).isIdle() && 
							militaryUnits.get(unitTy).get(index).isCompleted())
					{
						//System.out.println("Military Manager: Adding unit " + ut.getID() + " to group");
						tmp.add(militaryUnits.get(unitTy).get(index));
						//System.out.println("Military Manager: Added unit " + ut.getID() + " to group");
					}
				}
			}
		}
		
		if(tmp.size() == numOfUnits)
		{
			for(int index = 0; index < (militaryUnits.get(UnitTypes.Terran_Medic).size()/2); index++)
			{
				if(militaryUnits.get(UnitTypes.Terran_Medic).get(index).isIdle() 
						&& militaryUnits.get(UnitTypes.Terran_Medic).get(index).isCompleted())
					tmp.add(militaryUnits.get(UnitTypes.Terran_Medic).get(index));
			}
			
			currUnitGroups.add(tmp);
			removeUsedUnits(tmp);
			unitOperationHelper(tmp, locationX, locationY);
		}
	}
	
	public void unitOperation(int numOfUnits, int locationX, int locationY)
	{
		LinkedList<Unit> tmp = new LinkedList<Unit>();
		
		for(int index = 0; index < militaryUnits.get(UnitTypes.Terran_Marine).size(); index++)
		{
			if(tmp.size() < numOfUnits)
			{
				if(militaryUnits.get(UnitTypes.Terran_Marine).get(index).isIdle() && 
						militaryUnits.get(UnitTypes.Terran_Marine).get(index).isCompleted())
				{
					//System.out.println("Military Manager: Adding unit " + ut.getID() + " to group");
					tmp.add(militaryUnits.get(UnitTypes.Terran_Marine).get(index));
					//System.out.println("Military Manager: Added unit " + ut.getID() + " to group");
				}
			}
		}
		
		if(tmp.size() == numOfUnits)
		{
			currUnitGroups.add(tmp);
			removeUsedUnits(tmp);
			unitOperationHelper(tmp, locationX, locationY);
		}
	}
	
	public void unitOperation(int locationX, int locationY)
	{
		LinkedList<Unit> tmp = new LinkedList<Unit>();
		
		for(int index = 0; index < militaryUnits.get(UnitTypes.Terran_Ghost).size(); index++)
		{
			//System.out.println("Military Manager: INDEX " + index + " Size of group is " + tmp.size());
			if(tmp.size() < 5)
			{
				if(militaryUnits.get(UnitTypes.Terran_Ghost).get(index).isIdle() && 
						militaryUnits.get(UnitTypes.Terran_Ghost).get(index).isCompleted())
				{
					//System.out.println("Military Manager: Adding unit " + ut.getID() + " to group");
					tmp.add(militaryUnits.get(UnitTypes.Terran_Ghost).get(index));
					//System.out.println("Military Manager: Added unit " + ut.getID() + " to group");
				}
			}
		}
		
		if(tmp.size() == 5)
		{
			for(int index = 0; index < (militaryUnits.get(UnitTypes.Terran_Medic).size()/2); index++)
			{
				if(militaryUnits.get(UnitTypes.Terran_Medic).get(index).isIdle() 
						&& militaryUnits.get(UnitTypes.Terran_Medic).get(index).isCompleted())
					tmp.add(militaryUnits.get(UnitTypes.Terran_Medic).get(index));
			}
			currUnitGroups.add(tmp);
			removeUsedUnits(tmp);
			unitOperationHelper(tmp, locationX, locationY);
		}
	}
	
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
	
	private void removeUsedUnits(LinkedList<Unit> usedUnits)
	{	
		if(usedUnits != null)
		{
			for(int index = 0; index < usedUnits.size(); index++)
			{
				UnitTypes tmp = getUnitType(usedUnits.get(index).getTypeID());
				
				for(int index2 = 0; index2 < militaryUnits.get(tmp).size(); index2++)
				{
					if(usedUnits.get(index).getID() == (militaryUnits.get(tmp).get(index2).getID()))
					{
						militaryUnits.get(tmp).remove(index2);
						System.out.println("Military Manager: removeUsedUnits. New size is " + militaryUnits.get(tmp).size());
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
	 * Commands the Units stored in an LinkedList to attack a location on the map
	 * 
	 * unitFormation:   LinkedList of Units we wanted to rally
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
		//System.out.println("RALLY TEST");
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
						//System.out.println(checkReadyFlag);
					}
				}
			}
		}
		return checkReadyFlag;
	}
	
	/*
	 * Rally the Units stored in an LinkedList to a location on the map
	 * 
	 * unitFormation:   LinkedList of Units we wanted to rally
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
				//bwapi.useTech(unitGroup.get(index).getID(), UnitTypes.Terran_Vulture_Spider_Mine.ordinal());
			}
		}
	}
	
	public void handleUnitsAttacking(LinkedList<Unit> unitGroup, int pixelPositionX, int pixelPositionY)
	{
		for(int index = 0; index < unitGroup.size(); index++)
		{			
			if(unitGroup.get(index).getTypeID() == UnitTypes.Terran_Vulture.ordinal())
			{
				if(unitGroup.get(index).isStartingAttack())
				{
					bwapi.useTech(unitGroup.get(index).getID(), UnitTypes.Terran_Vulture_Spider_Mine.ordinal());
				}
			}
			
			if(unitGroup.get(index).getTypeID() == UnitTypes.Terran_Ghost.ordinal())
			{
				bwapi.cloak(unitGroup.get(index).getID());
				if(unitGroup.get(index).isStartingAttack())
				{
					for(Unit un: bwapi.getMyUnits())
					{
						if(un.getTypeID() == UnitTypes.Terran_Command_Center.ordinal())
						{
							if(un.isNukeReady())
							{
								bwapi.useTech(unitGroup.get(index).getID(), UnitTypes.Terran_Nuclear_Missile.ordinal(), pixelPositionX, pixelPositionY);
							}
						}
					}
				}
			}
			
			if(unitGroup.get(index).getTypeID() == UnitTypes.Terran_Siege_Tank_Tank_Mode.ordinal())
			{
				System.out.println(unitGroup.get(index).isIdle());
				
				if(unitGroup.get(index).isAttacking())
				{
					bwapi.siege(unitGroup.get(index).getID());
				}
				else if(unitGroup.get(index).isStartingAttack())
				{
					bwapi.siege(unitGroup.get(index).getID());
				}
				else if(unitGroup.get(index).isIdle())
				{
					bwapi.attack(unitGroup.get(index).getID(), pixelPositionX, pixelPositionY);
				}
			}
			
			if(unitGroup.get(index).getTypeID() == UnitTypes.Terran_Siege_Tank_Siege_Mode.ordinal())
			{
				if((unitGroup.get(index).isIdle()))
				{
					bwapi.unsiege(unitGroup.get(index).getID());
				}
			}
		}
	}
	
	/*
	 * Returns the current number of units of a UnitType
	 */
	private int getCurrentUnitCount(UnitTypes unitType)
	{
		return militaryUnits.get(unitType).size();
	}
	
	private int getWorkersCount()
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
