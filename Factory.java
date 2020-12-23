//simple class to create Units based on the parameters passed in.
import javafx.scene.paint.*;

public class Factory
{
   int range;
   int speed;
   float damage;
   float hp;
   Color c;
   

   public Factory(int range, int speed, float damage, float hp, Color c)
   {
      this.range = range;
      this.speed = speed;
      this.damage = damage;
      this.hp = hp;
      this.c = c;
   }
   
   public Unit createUnit(int x, int y, int owner, AI theAI, Unit.Type type)
   {
      return new Unit(owner,theAI, range, speed,x,y,damage,hp,c, type);
   }
}