public class Vector2
{
   private int x;
   private int y;
   public Vector2(int x, int y)
   {
      this.x = x;
      this.y = y;
   }
   public int getX()
   {
      return x;
   }
   public int getY()
   {
      return y;
   }
   
   public String toString()
   {
      return x + " "+y;
   }
}