import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main (String[] args) {
        int columns, rows;   //Stulpeliai, eilutės
        Random r = new Random();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Kodo ilgis (n): ");
        columns = scanner.nextInt();
        System.out.println("Dimensija (k): ");
        rows = scanner.nextInt();
        while (rows >= columns)
        {
            System.out.println("Dimensija turi būti mažesnė už ilgį: ");
            rows = scanner.nextInt();
        }
        int[][] matrix_G = new int[rows][columns];   //Generuojanti matrica
        System.out.println("Ar norite patys suvesti generuojančią matricą (t/n)?");
        String answer = scanner.next();
        while(!answer.equals("t") && !answer.equals("n"))
        {
            System.out.println("Ar norite patys suvesti generuojančią matricą (t/n)?");
            answer = scanner.next();
        }
        if (answer.equals("t")) //Suvedam matrica
        {
            System.out.println("Įveskite " + columns*rows + " matricos elementus: ");
            for (int i = 0; i < rows; i++){
                for (int j = 0; j < columns; j++)
                {
                    matrix_G[i][j] = scanner.nextInt();
                }
            }
        }
        else    //Sukuriam generuojancia matrica G
        {
            for (int i = 0; i < rows; i++){
                for (int j = 0; j < columns; j++)
                {
                    if(rows-j > 0) //Kuriama vienetine matrica
                    {
                        if(i == j)
                            matrix_G[i][j] = 1;
                        else
                            matrix_G[i][j] = 0;
                    }
                    else
                    matrix_G[i][j] = r.nextInt(2);
                }
            }
        }
        //Atspausdina generuojančią matricą
        for (int i = 0; i < rows; i++){
            System.out.print("(");
            for (int j = 0; j < columns; j++)
            {
                System.out.print(matrix_G[i][j] + " ");
            }
            System.out.println(")");
        }

        //Vektoriaus įvedimas
        String messageString;
        int[] messageArray = new int [rows];   //Žinutė skaitmenimis
        do {
            System.out.println("Įveskite vektorių (" + rows + " ilgio): ");
            messageString = scanner.next();
        }while(messageString.length() != rows);
        //Tikimybės įvedimas
        double probability;
        do {
            System.out.println("Įveskite klaidos tikimybę(0-1): ");
            String temp = scanner.next();
            temp = temp.replaceAll(",", ".");
            probability = Double.parseDouble(temp);
        }while(!((probability >= 0) && (probability <= 1)));

        //Skaitmenis suveda į integer masyvą
        for(int i=0; i<messageString.length(); i++)
        {
            char temp = messageString.charAt(i);
            messageArray[i] = Integer.parseInt(String.valueOf(temp));
        }

        //Išveda vektorių ir tikimybę
        System.out.print("           Vektorius: ");
        for (int i=0; i<messageArray.length; i++)
        {
            System.out.print(messageArray[i]);
        }
        System.out.println("   Tikimybė: " + probability);

        //Išveda užkoduotą vektorių
        Encoder encoder = new Encoder(matrix_G);
        int[] encodedArray = encoder.Encode(messageArray);
        System.out.print("Užkoduotas vektorius: ");
        for (int i=0; i<encodedArray.length; i++)
        {
            System.out.print(encodedArray[i]);
        }

        //Siučiam žinutę per kanalą
        Channel channel = new Channel(probability);
        int[] errorArray = channel.sendMessage(encodedArray);

        //Išveda klaidų vektorių
        System.out.print("\n    Klaidų vektorius: ");
        for (int i=0; i<errorArray.length; i++)
        {
            System.out.print(errorArray[i]);
        }
        //Išveda vektorių išėjusį iš kanalo
        System.out.print("\n    Gautas vektorius: ");
        for (int i=0; i<encodedArray.length; i++)
        {
            System.out.print(encodedArray[i]);
        }

        //Dekoduojam vektorių
        Decoder decoder = new Decoder(matrix_G);
        int[] decodedMessage = decoder.Decode(encodedArray);
        
        //Išveda dekoduotą vektorių
        System.out.print("\nDekoduotas vektorius: ");
        for (int i=0; i<decodedMessage.length; i++)
        {
            System.out.print(decodedMessage[i]);
        }

        //scanner.nextLine(); //Reikalauja dar vieno paspaudimo pries baigiant darba
    }
}
