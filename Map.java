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

public class Map
{
   private MapTile[][] tiles;
   public static int SIZEX=10;
   public static int SIZEY=10;

   private static Map myMap;
   public static Map getMap()
   {
      if(myMap == null)
      {
         System.out.println("resetMap must be called first");
      }
      return myMap;
   }
   
   //create the new map
   public static void resetMap(int[][] map)
   {
      myMap = new Map(map);
   }

   private Map(int[][] map)
   {
      //creates a grid of size by size map tiles and "links" them together. I.e., they can reference each other. Kind of linked list style.
      
      SIZEY = map.length;
      SIZEX = map[0].length;
      
      tiles = new MapTile[SIZEY][SIZEX];
      
      for(int i=0;i<SIZEY ;i++)
      {
         for(int j=0;j<SIZEX;j++)
         {
            if(map[i][j] != -1)
               tiles[i][j] = new MapTile(j,i);
         }
      }
      for(int i=0;i<SIZEY;i++)
      {
         for(int j=0;j<SIZEX;j++)
         {
            if(i >= 1 && tiles[i][j] != null && tiles[i-1][j] != null)
            {
               tiles[i][j].setLink(tiles[i-1][j]);
            }
            if(j >= 1 && tiles[i][j] != null && tiles[i][j-1] != null)
            {
               tiles[i][j].setLink(tiles[i][j-1]);
            }
            if(i < SIZEY-1 && tiles[i][j] != null && tiles[i+1][j] != null)
            {
               tiles[i][j].setLink(tiles[i+1][j]);
            }
            if(j < SIZEX-1 && tiles[i][j] != null && tiles[i][j+1] != null)
            {
               tiles[i][j].setLink(tiles[i][j+1]);
            }
         }
      }
   }
   
   //get a tile
   public MapTile getTile(int x, int y)
   {
      return tiles[y][x];
   }
   
   //clear the good and bad scores from the tiles
   public void clearScores()
   {
      for(int i=0;i<SIZEY;i++)
      {
         for(int j=0;j<SIZEX;j++)
         {
            if(tiles[i][j] != null)
               tiles[i][j].clearScore();
         }
      }
   }
   
   
   //getting all units in attack range if a unit was at a particular location.
   public ArrayList<Unit> getEnemyUnitsInRangeIfUnitUWasAtLocationXY(int x, int y, Unit u)
   {
      ArrayList<Unit> units = new ArrayList<Unit>();
      
      Map.Iterator it = generateIteratorWithinAttackRange(x,y,u.getRange(),u);
      
      while(it.hasNext())
      {
         MapTile m = it.next();
         if(m.getUnit() != null)
         {
            if(m.getUnit().getOwner() != u.getOwner())
            {
               units.add(m.getUnit());
            }
         }
      }
      
      return units;
   }
   
   
   //create a new iterator that iterates over the map
   public Map.Iterator generateIteratorAllTiles()
   {
      return new Iterator();
   }
   
   //create a new iterator for the movement of a particular unit at a particular position with a particular range.
   public Map.Iterator generateIteratorWithinMoveRange(int x, int y, int range, Unit caller)
   {
      return new Iterator(x,y,range,true,caller);
   }
   
   //create a new iterator for the attack range of a particular unit at a particular spot
   public Map.Iterator generateIteratorWithinAttackRange(int x, int y, int range, Unit caller)
   {
      return new Iterator(x,y,range,false,caller);
   }  
   
   //which iteration are we looping over; used by tiles. Do not do two custom iterations at the same time. Or do not do a custom iterator and a getMoveRange files.
   public static int initCounter()
   {
      return ++currentIt;
   }
   
   static int currentIt=0;
   
   //iterator class
   public class Iterator
   {
      ArrayList<MapTile> theQueue = new ArrayList<MapTile>();
      int current=0;
      
      
   
      public Iterator()
      {
         theQueue.clear();
      
         for(int i=0;i<tiles.length;i++)
         {
            for(int j=0;j<tiles[0].length;j++)
            {
               if(tiles[i][j] != null)
                  theQueue.add(tiles[i][j]);
            }
         }
      }
   
      public Iterator(int x, int y, int range, boolean useModifiedDijkstra, Unit caller)
      {
         theQueue.clear();
      

         if(!useModifiedDijkstra)
         {
            //probably about half as efficient as it could be, should be same O though.
            for(int i=y-range-1;i<y+range+1;i++)
            {
               for(int j=x-range-1;j<x+range+1;j++)
               {
                  int distance = Math.abs(y-i) + Math.abs(x-j);
                  if(i >= 0 && j >= 0 && i <SIZEY && j < SIZEX)
                     if(distance <= range)
                     {
                        if(tiles[i][j] != null)
                           theQueue.add(tiles[i][j]);
                     }
               }
            }      
         }
         else
         {
            //modified version of dijkstra assuming each link is weighted 1
            ArrayList<MapTile> queues = new ArrayList<MapTile>();
            queues.add(tiles[y][x]);
            tiles[y][x].setIt(++currentIt);
            tiles[y][x].setMoveRange(0);
            
            while(queues.size()>0)
            {
               MapTile mt  = queues.get(0);
               queues.remove(0);
               theQueue.add(mt);
               mt.setIt(currentIt);
               MapTile.Iterator it = mt.createIterator();
               
               while(it.hasNext() && mt.getMoveRange() < caller.getSpeed())
               {
                  MapTile next = it.next();
                  
                  if(next.getIt() != currentIt)
                  {
                     if(next.getUnit()==null || next.getUnit().getOwner() == caller.getOwner())
                     {
                        next.setIt(currentIt);
                        queues.add(next);
                        next.setMoveRange(mt.getMoveRange()+1);
                     }
                  }
               }
            }
         }
      }
      
      
      public MapTile next()
      {
         return theQueue.get(current++);
      }
      public boolean hasNext()
      {
         return current < theQueue.size();
      }
   }
}