import java.io.File;

public class Rater {

    public static void main(String [] args){
        if (args.length < 4)
            System.err.println("Número de argumentos erroneo: " + args.length);
        else{
            String arg = args[0];
            File referenceDir = new File(args[1]);
            File projectDir = new File(args[2]);
            String practiceName = args[3];
//            String practiceName = "Practica_1";
//            File referenceDir = new File("/home/alex/Desktop/projects/references/1/Practica2_2019");
//            File projectDir = new File("/home/alex/Desktop/projects/references/1/Practica2_2019");
//            String arg = "-p";
            String pathToJplag = "/home/alex/Desktop/projects/jplag.jar";
            if(args.length == 5){
               pathToJplag = args[4];
            }
            switch (arg){
                case "-d":
                    System.out.println("Rating directory of projects: " + projectDir.getPath() + "\n");
                    Script.rateDirectory(referenceDir, projectDir, practiceName, pathToJplag);
                    break;

                case "-p":
                    System.out.println("Rating project " + projectDir.getPath() + "\n");
                    Script.rateProject(referenceDir, projectDir, false, practiceName, null);
                    break;

                case "-a":
                    System.out.println("-a: Anticopia de un directorio de proyectos");
                    Script.antiplag(projectDir, referenceDir, pathToJplag);
                    break;
            }
        }
    }
}
