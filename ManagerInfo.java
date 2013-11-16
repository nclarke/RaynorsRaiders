package javabot.RaynorsRaiders;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Comparator;

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
import javabot.RaynorsRaiders.CoreReactive.*; // Why do we need this line? -Matt



public class ManagerInfo extends RRAITemplate
{

	MiltScouter scouter;
	
	public ManagerInfo() {
		//SET UP ALL INTERNAL VARIABLES HERE
		super();
		scouter = new MiltScouter(this);
	}
	
	
	public void setup() {

	}
	

	public void checkUp() 
	{
		this.scouter.scout();
	
	}
	
	public void debug() {
	}
	
}

