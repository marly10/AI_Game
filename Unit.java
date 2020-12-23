//class that encapsalates a unit.

import java.util.*;
import javafx.scene.paint.*;

public class Unit
{
   private int x,y; //tile position
   private MapTile currentTile; //reference to current tile
   
   private int owner; //the owner of the unit -> either 0 or 1. Your AI may be 0 or it may be 1 depending upon the game / ordering
   private AI theAI; // the AI for the unit
   private int range; //attack range
   private int speed; //move range
   private float hp; //health
   private float damage; //damage
   private Color c; //color for the circle
    
   public enum Type {RK, ST, MT}; //enum type 
   
   private Type unitType; //what type (of the enums) is this unit
    
   public Unit(int owner, AI theAI, int range, int speed, int x, int y, float damage, float hp, Color c, Type unitType)
   {
      this.owner = owner;
      this.theAI = theAI;
      this.range = range;
      this.speed = speed;
      this.x = x;
      this.y = y;
      this.damage = damage;
      this.hp = hp;
      this.c = c;
      this.unitType = unitType;
   }
   
   //deals damage to the other unit
   public void attack(Unit other)
   {
      other.hp-=damage;
   }
   
   //remove the unit from the game (in this case, remove it from the current tile)
   public void remove()
   {
      if(currentTile != null)
      {
         currentTile.removeUnit(this);
      }
   }
   
   //sets the current tile the unit is on, removes the unit from the previous, and updates all variables accordingly
   public void setTile(MapTile other)
   {
      if(currentTile != null)
      {
         currentTile.removeUnit(this);
      }
      currentTile = other;
      currentTile.addUnit(this);
      x = currentTile.getX();
      y = currentTile.getY();
   }
   
   //calls the AI's run method on this units AI
   public Move runAI(ArrayList<Unit> unitsLeftToRun, boolean first)
   {
      return theAI.runAI(unitsLeftToRun,first);
   }
   
   
   //accessors / mutators
   
   public MapTile getTile()
   {
      return currentTile;
   }
   
   public AI getAI()
   {
      return theAI;
   }
   
   public int getX()
   {
      return x;
   }
   public int getY()
   {
      return y;
   }
   
   public int getRange()
   {
      return range;
   }
   
   public int getSpeed()
   {
      return speed;
   }

   
   public int getOwner()
   {
      return owner;
   }
   
   public Color getColor()
   {
      return c;
   }
   
   public float getHP()
   {
      return hp;
   }
   
   public float getDamage()
   {
      return damage;
   }
   
   public Unit.Type getType()
   {
      return unitType;
   }
   
   public String toString()
   {
      return ""+ x+" "+y+" "+range;
   }
   
}