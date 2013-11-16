package javabot.RaynorsRaiders;

import java.awt.Point;
import java.util.*;

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



public class ManagerInfo extends RRAITemplate
{

	MiltScouter scouter;
	List<Unit> neutralUnits;
	List<Unit> enemyUnits;
	int selfID;
	
	public ManagerInfo() {
		//SET UP ALL INTERNAL VARIABLES HERE
		super();
		scouter = new MiltScouter(this);
		neutralUnits = new ArrayList<Unit>();
		enemyUnits = new ArrayList<Unit>();
	}
	
	
	public void setup() {
		selfID = bwapi.getSelf().getID();
		this.scouter.startUp();

	}
	

	public void checkUp() 
	{
		this.scouter.scout();
	
	}
	
	public void unitSeen(int unitID)
	{		
		Unit unit = bwapi.getUnit(unitID);
		if(bwapi.getUnit(unitID).getPlayerID() == 0) //neutral Unit
		{
			if(!this.neutralUnits.contains(unit))
			{
				neutralUnits.add(unit);
			}
		}
		else if(bwapi.getUnit(unitID).getPlayerID() != selfID) //enemy Unit
		{
			if(!this.enemyUnits.contains(unit))
			{
				enemyUnits.add(unit);
				System.out.println("New Enemy unit discovered of type: "+bwapi.getUnitType(unitID));
			}
		
		}
		else
		{
			//our unit
		}
/*		
		System.out.println("this was the unit discovered: "+unitID);
		System.out.println("of type: "+bwapi.getUnitType(unitID));
		System.out.println("of player ID: "+bwapi.getUnit(unitID).getPlayerID());
		System.out.println("of type ID: "+bwapi.getUnit(unitID).getTypeID());
		System.out.println("\n");
		*/
	}
	
	public void unitDestoryed(int unitID)
	{		
		Unit unit = bwapi.getUnit(unitID);
		if(bwapi.getUnit(unitID).getPlayerID() == 0) //neutral Unit
		{
			if(this.neutralUnits.contains(unit))
			{
				neutralUnits.remove(unit);
			}
		}
		else if(bwapi.getUnit(unitID).getPlayerID() != selfID) //enemy Unit
		{
			if(this.enemyUnits.contains(unit))
			{
				enemyUnits.remove(unit);
				System.out.println("Enemy unit distroeyed of type: "+bwapi.getUnitType(unitID));
			}
		
		}
		else
		{
			//our unit
		}
		
	}

	
	public void debug() {
	}
	
}

