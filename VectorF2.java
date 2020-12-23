public class VectorF2
{
   private float x;
   private float y;
   public VectorF2(float x, float y)
   {
      this.x = x;
      this.y = y;
   }
   public float getX()
   {
      return x;
   }
   public float getY()
   {
      return y;
   }
   
   public String toString()
   {
      return x + " "+y;
   }
}