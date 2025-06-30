import java.util.*;

 public class basic
{
public static void main(String args[])
{
    Scanner s=new Scanner(System.in);
    int num=s.nextInt();

int count=0,i;
for(i=1;i<num;i++)
{
if(i%2==0)
{
    System.out.println("EVEN NUMBER:"+i);
}
else{
    count=count+1;
    System.out.println(" ODD  NUMBER:");
    System.out.println("counting numbers:"+ count);

}
}

}


}


