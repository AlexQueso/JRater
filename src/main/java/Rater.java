import java.io.File;

public class Rater {

    public static void main(String [] args){
        if (args.length < 4)
            System.err.println("Número de argumentos erroneo: " + args.length);
        else{
            File referenceDir = new File(args[1]);
            File projectDir = new File(args[2]);
            String practiceName = args[3];
            String arg = args[0];
            switch (arg){
                case "-d":
                    System.out.println("Rating directory of projects: " + projectDir.getPath() + "\n");
                    Script.rateDirectory(referenceDir, projectDir, practiceName);
                    break;

                case "-p":
                    System.out.println("Rating project " + projectDir.getPath() + "\n");
                    Script.rateProject(referenceDir, projectDir, false, practiceName, null);
                    break;

                case "-a":
                    System.out.println("-a: Anticopia de un directorio de proyectos");
                    Script.antiplag(projectDir, referenceDir);
                    break;
            }
        }
    }
}
