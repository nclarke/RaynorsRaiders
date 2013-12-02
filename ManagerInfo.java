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
	int hostileX;
	int hostileY;
	boolean isScouting;
	List<Base> enemyBases;
	Unit closestUnit; /*the closest enemy unit to our base*/
	Tile closestTile; /*tile of the closest enemy unit to our base*/
	
	
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
	
	public class Base {
		BaseLocation baseLoc;
		Tile tile;
		boolean hasSeen;
		boolean hasEnemy;
	}
	
	public ManagerInfo() {
		//SET UP ALL INTERNAL VARIABLES HERE
		super();
		hostileX = 0;
		hostileY = 0;
		scouter = new MiltScouter(this);
		isScouting = false;
		neutralUnits = new ArrayList<Unit>();
		enemyUnits = new ArrayList<Unit>();
		enemyBases = new LinkedList<Base>();
		closestTile = new Tile(9999, 9999);
		closestUnit = null;
	}
	
	
	public void setup() {
		selfID = bwapi.getSelf().getID();
		this.scouter.startUp();

	}
	

	public void checkUp() 
	{
//		if (this.bwapi.getFrameCount() == 1)
			this.scouter.scout();
			for(Base base : scouter.bases)
			{
				if(base.hasEnemy && !enemyBases.contains(base))
				{
					enemyBases.add(base);
				}				
			}
	
	}
	
	public void unitSeen(int unitID)
	{		
		Unit unit = bwapi.getUnit(unitID);
		if(bwapi.getUnit(unitID).getPlayerID() == 0) //neutral Unit
		{
			if(!this.neutralUnits.contains(unit))
			{
				neutralUnits.add(unit);
				System.out.println("New Neautral unit discovered of type: "+bwapi.getUnitType(unitID));
			}
		}
		else if(bwapi.getUnit(unitID).getPlayerID() != selfID) //enemy Unit
		{
			if(!this.enemyUnits.contains(unit))
			{
				enemyUnits.add(unit);
				System.out.println("New Enemy unit discovered of type: "+bwapi.getUnitType(unitID));
				if (Math.abs(unit.getTileX() - military.homePositionX) < Math.abs( closestTile.getX() - military.homePositionX) && 
				 Math.abs(unit.getTileY() - military.homePositionY) < Math.abs(closestTile.getY() - military.homePositionY))
				{
					System.out.println("found new closestunit at ("+unit.getTileX()+","+unit.getTileY()+")");
					closestTile = new Tile(unit.getTileX(), unit.getTileY());
					closestUnit = unit;
				}
			}
		
		}
		else
		{
			//our unit
		}
	}
	
	public void unitDestoryed(int unitID)
	{		
		Unit unit = bwapi.getUnit(unitID);
		System.out.println("destroyed unitID: "+unitID);


		if (unitID == this.scouter.scout.getID())
		{
			System.out.println("scout destroyed of type: "+scouter.scout.getTypeID());
			if (this.scouter.scout.getTypeID() == UnitTypes.Terran_SCV.ordinal())
				workers.checkInWorker(scouter.scout.getID());
			this.scouter.scout = null;
			this.scouter.bases.get(scouter.currIndex).hasEnemy = true;
			System.out.println("scout checked back in");

			
		}
//		System.out.println("here now with : "+unitID);
		for(Unit u : this.enemyUnits)
		{
//			System.out.println("unit of : " +u);
//			System.out.println("middle (closest: "+closestUnit+", "+closestUnit.getID());
//			System.out.println("unit ID of : " +u.getID());
			if(u.getID() == unitID)
			{
				this.enemyUnits.remove(u);
			}
//			System.out.println("middle (closest: "+closestUnit+", "+closestUnit.getID());
			if(closestUnit.getID() == unitID)
			{
				closestTile = new Tile(9999, 9999);
				closestUnit = null;
			}
		}
		for(Unit u : this.neutralUnits)
		{
			if(u.getID() == unitID)
				this.neutralUnits.remove(u);
		}

//		System.out.println("ending now");
	}

	
	public void debug() {
	}
	
}

