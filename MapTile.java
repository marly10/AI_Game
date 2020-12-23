
import javafx.event.*;
import javafx.stage.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.animation.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.geometry.*;
import javafx.application.Application;
import java.io.*;
import java.util.*;
import java.text.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class MapTile
{
   private ArrayList<MapTile> links = new ArrayList<MapTile>(); //links to other tiles
   private ArrayList<Unit> units = new ArrayList<Unit>(); //the units (technically a max of 1 unit) currently on the tile
   private int px,py; //location of the tile
   
   private float goodScore=-9000; //the good score (for the AIs)
   private float badScore=-9000;  //the bad score
   
   private int currentIt=-1; //last iteration number performed.
   
   public MapTile(int x, int y)
   {
      px = x;
      py = y;
   }

   public int getX()
   {
      return px;
   }
   public int getY()
   {
      return py;
   }
   
   public void setColor(Color in)
   {
      tileColor = in;
   }
   public Color getColor()
   {
      return tileColor;
   }
   
   Color tileColor = Color.GREEN;
   

   
   public void setGoodScore(float score)
   {
      goodScore = score;
   }
   
   public void setBadScore(float score)
   {
      badScore = score;
   }
   
   public float getGoodScore()
   {
      return goodScore;
   }
   public float getBadScore()
   {
      return badScore;
   }
   
   public void setLink(MapTile other)
   {
         links.add(other);
   }
   

   public void addUnit(Unit u)
   {
      if(units.size() > 0)
      {
         System.out.println("ERROR Units overlapping; see maptile.java");
      }
   
      units.add(u);
   }
   public void removeUnit(Unit u)
   {
      units.remove(u);
   }
   
   //current iterator value. to be used in traversals of the graph. Don't intermix two graph traversals unless you add a way to keep track of the second one.
   public int getIt()
   {
      return currentIt;
   }
   
   public void setIt(int newit)
   {
      currentIt =  newit;
   }
   
   int moveRange=0;
   public int getMoveRange()
   {
      return moveRange;
   }
   public void setMoveRange(int x)
   {
      moveRange = x;
   }
   
   public Unit getUnit()
   {
      if(units.size() <=0)
      {
         return null;
      } 
      return units.get(0);
   }
   
   public String toString()
   {
      return px+" "+py;
   }
   
   public void clearScore()
   {
      goodScore = -9000;
      badScore = -9000;
      tileColor = Color.GREEN;
   }
   
   
   public Iterator createIterator()
   {
      return new Iterator(this);
   }
   
   public class Iterator
   {
      MapTile mt;
      int current;
   
      public Iterator(MapTile mt_in)
      {
         mt = mt_in;
      }
      public MapTile next()
      {
         int temp = current++;
         return mt.links.get(temp);
      }
      public boolean hasNext()
      {
         if(mt.links.size() > current)
         {
            return true;
         }
         return false;
      }
   }
   
}