package javabot.AIStarCraft;

public enum Level {
	
	ZERO(0), ONE(1), TWO(2);
	
	private int levelNum;
	
	private Level(int lvl)
	{
		levelNum = lvl;
	}
	
	private int getLevelNum()
	{
		return levelNum;
	}

}
