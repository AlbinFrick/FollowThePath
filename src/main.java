import java.io.IOException;

import static java.lang.System.exit;

public class main {

    public static void main(String[] args)  {
        try{
            ReadPath readPath = new ReadPath(args[0]);

            int pathsize = readPath.pathSize();
            Position[] path = readPath.getPath();
            System.out.println("position " + path[0].getX() + "," + path[0].getY());
            System.out.println("Creating Robot");
            FollowPathRobot3 robot = new FollowPathRobot3("http://127.0.0.1", 50000);
            robot.run(path, pathsize);


        }catch (IOException e){
            System.out.println("Control: Path-file");
            System.out.println("Simulator");
            System.out.println("Program arguments");
            exit(-1);
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Control the argument of the program");
            exit(-1);
        }catch (Exception e){
            e.printStackTrace();
            exit(-1);
        }

    }
}
