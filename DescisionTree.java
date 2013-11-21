package javabot.RaynorsRaiders;

import java.awt.Point;
import java.util.*;

import javabot.JNIBWAPI;
import javabot.RaynorsRaiders.Level;
import javabot.model.*;
import javabot.types.UnitType.UnitTypes;
import javabot.util.*;
import javabot.RaynorsRaiders.ManagerBuild.BuildingRR;

public class DescisionTree {

	ManagerBuild mBld;
	
	
	public DescisionTree(ManagerBuild mBld)
	{
		this.mBld = mBld;

	}

	
	public UnitTypes canIBuildThis(Unit unit)
	{
		return canIBuildThis(unit.getTypeID());
	}
	
	//return null if dont need to build anything else
	public UnitTypes canIBuildThis(int unitType)
	{
		Unit building, building1, building2, building3;
		
		if (unitType == UnitTypes.Terran_Barracks.ordinal() || unitType == UnitTypes.Terran_Engineering_Bay.ordinal())
		{
			for (int i = 0; i < mBld.completedBuildingsIndex; i++)
			{
				building = mBld.buildingsStack.get(i).unit;
				
				if (building != null && building.getID() == UnitTypes.Terran_Command_Center.ordinal())
					return null;
			}
			return UnitTypes.Terran_Command_Center;
		}
		else if (unitType == UnitTypes.Terran_Bunker.ordinal() || unitType == UnitTypes.Terran_Academy.ordinal() || unitType == UnitTypes.Terran_Factory.ordinal())
		{
			for (int i = 0; i < mBld.completedBuildingsIndex; i++)
			{
				building = mBld.buildingsStack.get(i).unit;
				
				if (building.getID() == UnitTypes.Terran_Barracks.ordinal())
					return null;
			}
			return UnitTypes.Terran_Barracks;
		}
		else if (unitType == UnitTypes.Terran_Starport.ordinal() || unitType == UnitTypes.Terran_Armory.ordinal())
		{
			for (int i = 0; i < mBld.completedBuildingsIndex; i++)
			{
				building = mBld.buildingsStack.get(i).unit;
				
				if (building.getID() == UnitTypes.Terran_Factory.ordinal())
					return null;
			}
			return UnitTypes.Terran_Factory;
		}
		else if (unitType == UnitTypes.Terran_Science_Facility.ordinal())
		{
			for (int i = 0; i < mBld.completedBuildingsIndex; i++)
			{
				building = mBld.buildingsStack.get(i).unit;
				
				if (building.getID() == UnitTypes.Terran_Starport.ordinal())
					return null;
			}
			return UnitTypes.Terran_Starport;
		}

		else if (unitType == UnitTypes.Terran_Marine.ordinal())
		{
			for (int i = 0; i < mBld.completedBuildingsIndex; i++)
			{
				building = mBld.buildingsStack.get(i).unit;
				
				if (building.getID() == UnitTypes.Terran_Barracks.ordinal())
					return null;
			}
			return UnitTypes.Terran_Barracks;
		}
		else if (unitType == UnitTypes.Terran_Firebat.ordinal() || unitType == UnitTypes.Terran_Medic.ordinal())
		{
			for (int i = 0; i < mBld.completedBuildingsIndex; i++)
			{
				building1 = mBld.buildingsStack.get(i).unit;
				
				if (building1.getID() == UnitTypes.Terran_Barracks.ordinal())
				{
					for (int j = 0; j < mBld.completedBuildingsIndex; j++)
					{
						building2 = mBld.buildingsStack.get(j).unit;
						
						if (building2.getID() == UnitTypes.Terran_Academy.ordinal())						
							return null;						
					}
					return UnitTypes.Terran_Academy;				
				}
			}
			return UnitTypes.Terran_Barracks;
		}
		else if (unitType == UnitTypes.Terran_Ghost.ordinal())
		{
			for (int i = 0; i < mBld.completedBuildingsIndex; i++)
			{
				building1 = mBld.buildingsStack.get(i).unit;
				
				if (building1.getID() == UnitTypes.Terran_Barracks.ordinal())
				{
					for (int j = 0; j < mBld.completedBuildingsIndex; j++)
					{
						building2 = mBld.buildingsStack.get(j).unit;
						
						if (building2.getID() == UnitTypes.Terran_Academy.ordinal())	
						{
							for (int k = 0; k < mBld.completedBuildingsIndex; k++)
							{
								building3 = mBld.buildingsStack.get(k).unit;
								
								if (building3.getID() == UnitTypes.Terran_Science_Facility.ordinal())	
								{
									return null;
								}								
							}						
							return UnitTypes.Terran_Science_Facility;
						}
					}
					return UnitTypes.Terran_Academy;				
				}
			}
			return UnitTypes.Terran_Barracks;
		}
		else if (unitType == UnitTypes.Terran_Vulture.ordinal())
		{
			for (int i = 0; i < mBld.completedBuildingsIndex; i++)
			{
				building = mBld.buildingsStack.get(i).unit;		
				if (building.getID() == UnitTypes.Terran_Factory.ordinal())
					return null;
			}
			return UnitTypes.Terran_Factory;
		}

		else if (unitType == UnitTypes.Terran_Wraith.ordinal())
		{
			for (int i = 0; i < mBld.completedBuildingsIndex; i++)
			{
				building = mBld.buildingsStack.get(i).unit;		
				if (building.getID() == UnitTypes.Terran_Starport.ordinal())
					return null;
			}
			return UnitTypes.Terran_Starport;
		}

		else if (unitType == UnitTypes.Terran_Siege_Tank_Tank_Mode.ordinal() || unitType == UnitTypes.Terran_Siege_Tank_Siege_Mode.ordinal())
		{
			for (int i = 0; i < mBld.completedBuildingsIndex; i++)
			{
				building1 = mBld.buildingsStack.get(i).unit;
				
				if (building1.getID() == UnitTypes.Terran_Factory.ordinal())
				{
					for (int j = 0; j < mBld.completedBuildingsIndex; j++)
					{
						building2 = mBld.buildingsStack.get(j).unit;
						
						if (building2.getID() == UnitTypes.Terran_Machine_Shop.ordinal())						
							return null;						
					}
					return UnitTypes.Terran_Machine_Shop;				
				}
			}
			return UnitTypes.Terran_Factory;
		}
		else if (unitType == UnitTypes.Terran_Goliath.ordinal())
		{
			for (int i = 0; i < mBld.completedBuildingsIndex; i++)
			{
				building1 = mBld.buildingsStack.get(i).unit;
				
				if (building1.getID() == UnitTypes.Terran_Factory.ordinal())
				{
					for (int j = 0; j < mBld.completedBuildingsIndex; j++)
					{
						building2 = mBld.buildingsStack.get(j).unit;
						
						if (building2.getID() == UnitTypes.Terran_Armory.ordinal())						
							return null;						
					}
					return UnitTypes.Terran_Armory;				
				}
			}
			return UnitTypes.Terran_Factory;
		}

		else if (unitType == UnitTypes.Terran_Science_Vessel.ordinal())
		{
			for (int i = 0; i < mBld.completedBuildingsIndex; i++)
			{
				building1 = mBld.buildingsStack.get(i).unit;
				
				if (building1.getID() == UnitTypes.Terran_Control_Tower.ordinal())
				{
					for (int j = 0; j < mBld.completedBuildingsIndex; j++)
					{
						building2 = mBld.buildingsStack.get(j).unit;
						
						if (building2.getID() == UnitTypes.Terran_Academy.ordinal())	
						{
							for (int k = 0; k < mBld.completedBuildingsIndex; k++)
							{
								building3 = mBld.buildingsStack.get(k).unit;
								
								if (building3.getID() == UnitTypes.Terran_Science_Facility.ordinal())	
								{
									return null;
								}								
							}						
							return UnitTypes.Terran_Science_Facility;
						}
					}
					return UnitTypes.Terran_Control_Tower;				
				}
			}
			return UnitTypes.Terran_Starport;
		}

		else if (unitType == UnitTypes.Terran_Goliath.ordinal())
		{
			for (int i = 0; i < mBld.completedBuildingsIndex; i++)
			{
				building1 = mBld.buildingsStack.get(i).unit;
				
				if (building1.getID() == UnitTypes.Terran_Starport.ordinal())
				{
					for (int j = 0; j < mBld.completedBuildingsIndex; j++)
					{
						building2 = mBld.buildingsStack.get(j).unit;
						
						if (building2.getID() == UnitTypes.Terran_Control_Tower.ordinal())						
							return null;						
					}
					return UnitTypes.Terran_Control_Tower;				
				}
			}
			return UnitTypes.Terran_Starport;
		}

		
/*
		else if (unitType == UnitTypes.Terran_Ghost.ordinal())
		{
			for (Unit building : this.mBld.builtBuildings)		
				if (building.getID() == UnitTypes.Terran_Factory.ordinal())
					return null;
			return UnitTypes.Terran_Factory;
		}
		*/

		return null;
	}

}
