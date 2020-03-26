import java.io.File;

public class Rater {

    public static void main(String [] args){
        if (args.length < 3)
            System.err.println("Número de argumentos erroneo: " + args.length);
        else{
            File referenceDir = new File(args[1]);
            File projectDir = new File(args[2]);
            String arg = args[0];
            switch (arg){
                case "-d":
                    System.out.println("-d: Directorio de proyectos");
                    Script.rateDirectory(referenceDir, projectDir);
                    break;

                case "-p":
                    System.out.println("-p: Un solo proyecto");
                    Script.rateProject(referenceDir, projectDir);
                    break;

                case "-a":
                    System.out.println("-a: Anticopia de un directorio de proyectos");
                    Script.antiplag(new File(args[2]), new File(args[3]));
                    break;
            }
        }
    }
}
