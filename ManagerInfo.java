package javabot.RaynorsRaiders;

import java.awt.Point;
import java.util.*;

import javabot.JNIBWAPI;
import javabot.model.BaseLocation;
import javabot.model.Player;
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
	List<Unit> enemyUnits;/*refresh after sometime*/
	int selfID;
	int enemyID;
	int hostileX;
	int hostileY;
	boolean isScouting;
	boolean timeToDwell;
	List<Base> enemyBases;/*make better*/
	javabot.model.ChokePoint enemyChoke;
	Tile chokeTile;
//	Unit closestUnit; /*the closest enemy unit to our base*/
//	Tile closestTile; /*tile of the closest enemy unit to our base*/
	
	
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
		timeToDwell = false;
		enemyChoke = null;
//		closestTile = new Tile(9999, 9999);
//		closestUnit = null;
	}
	
	
	public void setup()
	{
		selfID = bwapi.getSelf().getID();
		for(Player p : bwapi.getEnemies())
		{
			enemyID = p.getID();
//			System.out.println("ID of: "+p.getID());
		}
	}

	public void startUp()
	{
		System.out.println("starting up scouter...");
		this.scouter.startUp();
		System.out.println("start Location ("+scouter.homeBase.baseLoc.getX()+","+scouter.homeBase.baseLoc.getY()+")");
		System.out.println("... done starting scouter");
		
	}

	public void checkUp() 
	{
		if(bwapi.getFrameCount()%2500 == 0)
		{
			for(Unit u : this.enemyUnits)
			{
				System.out.println("enemy unit is: "+u.toString());
			}
			this.enemyUnits.clear();
			
		}
		this.isScouting = false;
		if(this.scouter.scout != null)
			this.isScouting = true;

		System.out.println("scout is: "+scouter.scout+", and isScouting: "+isScouting);
		System.out.println("Total supply used: "+bwapi.getSelf().getSupplyUsed());
		System.out.println("Total supply overall: "+bwapi.getSelf().getSupplyTotal());
		if(timeToDwell && isScouting)
		{
			System.out.println("scout: Time to dwell at");
			System.out.println("	here ("+scouter.scout.getX()+","+scouter.scout.getY()+")");
			dwell();
		}
		else if (this.isScouting || (bwapi.getSelf().getSupplyUsed()/2) == 8 || 
		 (scouter.scout == null  && (bwapi.getSelf().getSupplyUsed()/2)%40 == 0))
		{
//			if((scouter.scout == null  && (bwapi.getSelf().getSupplyUsed()/2)%40 == 0))
//				System.out.println("\n\n\n\n\nNEWS SCOUT\n\n\n\n");
			this.scouter.scout();
			for(Base base : scouter.bases)
			{
				if(base.hasEnemy && !enemyBases.contains(base))
				{
					enemyBases.add(base);
					System.out.println("\nADDED AN ENEMY BASE LOCATION!\n");
				}				
			}
		}
	
	}
	
	public void unitSeen(int unitID)
	{		
		Unit unit = bwapi.getUnit(unitID);
		System.out.println("	seen of type ID: "+unit.getTypeID());
		if(unit.getPlayerID() == enemyID) //enemy Unit
		{
			if(!this.enemyUnits.contains(unit))
			{
				enemyUnits.add(unit);
				System.out.println("New Enemy unit discovered of type: "+bwapi.getUnitType(unitID));
				System.out.println("looking for type of Nexus: "+UnitTypes.Protoss_Nexus.ordinal());
				if(bwapi.getUnit(unitID).getTypeID() == UnitTypes.Protoss_Nexus.ordinal())
				{
					Base tempBase  = new Base();
					for(Base tbase : scouter.bases)
					{
						if(tbase.baseLoc.getX() == unit.getX() && tbase.baseLoc.getY() == unit.getY())
						{
							tbase.hasEnemy = true;
							tbase.hasSeen = true;
							enemyBases.remove(tbase);
							System.out.println("\nADDED AN ENEMY BASE LOCATION!\n");
							enemyBases.add(tbase);
						}
					}
					for(Base tbase : scouter.bases)
					{
						System.out.println("baseLoc: "+tbase.baseLoc);
						System.out.println("hasEnemy: "+tbase.hasEnemy);
						System.out.println("hasSeen: "+tbase.hasSeen);
					}
					for(Unit u : this.enemyUnits)
					{
						System.out.println("tostring: "+u.toString());
						System.out.println("typeID: "+u.getTypeID());
						System.out.println("ID: "+u.getID());
						System.out.println("");
					}

				}
				System.out.println("finish enemy ID");
			}

		}
		else if(bwapi.getUnit(unitID).getPlayerID() != enemyID && bwapi.getUnit(unitID).getPlayerID() != selfID) //neutral Unit
		{
			if(!this.neutralUnits.contains(unit))
			{
				neutralUnits.add(unit);
				System.out.println("New Neautral unit discovered of type: "+bwapi.getUnitType(unitID));
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


		if (this.scouter.scout != null && unitID == this.scouter.scout.getID())
		{
			System.out.println("scout destroyed of type: "+scouter.scout.getTypeID());
			// 			if (this.scouter.scout.getTypeID() == UnitTypes.Terran_SCV.ordinal())
				//				workers.checkInWorker(scouter.scout.getID());
			this.scouter.scout = null;
//			this.scouter.bases.get(scouter.currIndex).hasEnemy = true;
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
/*			if(closestUnit.getID() == unitID)
			{
				closestTile = new Tile(9999, 9999);
				closestUnit = null;
			}
*/		}
		for(Unit u : this.neutralUnits)
		{
			if(u.getID() == unitID)
				this.neutralUnits.remove(u);
		}

		System.out.println("ending now");
	}

	private void dwell()
	{
		int enemyRegion;
		int chokeRegion;
		System.out.println("dwelling, right? "+scouter.scout.isMoving());
		if (enemyChoke == null)
		{
			System.out.println("right! ");
			for(Base b : this.enemyBases)
			{
				enemyRegion = b.baseLoc.getRegionID();
				System.out.println("ID("+enemyRegion+") checking base ("+b.baseLoc.getX()+"," +b.baseLoc.getY()+")");
				for(javabot.model.ChokePoint cp : bwapi.getMap().getChokePoints())
				{
					chokeRegion = cp.getFirstRegionID();
					System.out.println("region ("+chokeRegion+") is same? "+(chokeRegion == enemyRegion));
					System.out.println("choke region first("+cp.getFirstSideX()+"," +cp.getFirstSideY()+")");
					System.out.println("choke region second("+cp.getSecondSideX()+"," +cp.getSecondSideY()+")");
					System.out.println("choke region center("+cp.getCenterX()+"," +cp.getCenterY()+")");
					if(chokeRegion == enemyRegion)
					{
						System.out.println("they are equal");
						enemyChoke = cp;
						boolean greaterX = scouter.homeBase.baseLoc.getX() < cp.getCenterX();
						boolean greaterY = scouter.homeBase.baseLoc.getY() < cp.getCenterY();
						System.out.println("greaters: ("+greaterX+","+greaterY+")");
						if(greaterX && greaterY)
						{
							bwapi.move(scouter.scout.getID(), cp.getSecondSideX(), cp.getSecondSideY());
							this.chokeTile = new Tile(cp.getSecondSideX(), cp.getSecondSideY());
						}
						else
						{
							bwapi.move(scouter.scout.getID(), cp.getFirstSideX(), cp.getFirstSideY());
							this.chokeTile = new Tile(cp.getFirstSideX(), cp.getFirstSideY());
						}
						System.out.println("moving to center("+cp.getCenterX()+"," +cp.getCenterY()+")");
						System.out.println("moving to ("+scouter.scout.getTargetX()+"," +scouter.scout.getTargetY()+")");
					}
				}
			}
			System.out.println("now done dwelling ");
		}
		else
		{
			System.out.println("now acclimate");
			if(Math.abs(this.chokeTile.x - scouter.scout.getX()) > 100 || Math.abs(this.chokeTile.y - scouter.scout.getY()) > 100)
			{
				System.out.println("moving to ("+this.chokeTile.x+"," +this.chokeTile.y+")");
				bwapi.move(scouter.scout.getID(), this.chokeTile.x, this.chokeTile.y);
			}
			else
				acclimate();
			System.out.println("now done acclimating");
		}
	}
	
	private void acclimate()
	{
		if(scouter.scout == null)
			return;
		int count = 1;
		boolean greaterX = scouter.homeBase.baseLoc.getX() < scouter.scout.getX();
		boolean greaterY = scouter.homeBase.baseLoc.getY() < scouter.scout.getY();
		System.out.println("scout loc: ("+scouter.scout.getX()+","+scouter.scout.getY()+")");
		System.out.println("greaters: ("+greaterX+","+greaterY+")");
		System.out.println("count: " +count);
		System.out.println("is scout moving? "+scouter.scout.isMoving());
		while (scouter.scout == null && count > 0 && !scouter.scout.isMoving())
		{
			if (greaterX && greaterY)
				bwapi.move(scouter.scout.getID(), scouter.scout.getX() - 1, scouter.scout.getY() - 1);
			else if (greaterX && !greaterY)
				bwapi.move(scouter.scout.getID(), scouter.scout.getX() - 1, scouter.scout.getY() + 1);
			else if (!greaterX && greaterY)
				bwapi.move(scouter.scout.getID(), scouter.scout.getX() + 1, scouter.scout.getY() - 1);
			else if (!greaterX && !greaterY)
				bwapi.move(scouter.scout.getID(), scouter.scout.getX() + 1, scouter.scout.getY() + 1);

			System.out.println("scout loc: ("+scouter.scout.getX()+","+scouter.scout.getY()+")");
			count = military.getEnemiesWithinUnitSightRange(scouter.scout);
			System.out.println("count: " +count);
		}
	}
	
	
	public void debug() {
	}
	
}

