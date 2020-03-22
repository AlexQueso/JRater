import java.io.File;

public class Rater {

    public static void main(String [] args){
        String[] args1 = {"rater.java", "-p",
                "/home/alex/Desktop/projects/reference",
                "/home/alex/Desktop/projects/students"};
        args = args1;
        if (args.length < 4)
            System.err.println("Número de argumentos erroneo: " + args.length);
        else{ //todo organizar acciones y procesado de las mismas
            File referenceDir = new File(args[2]);
            File projectDir = new File(args[3]);
            String arg = args[1];
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
