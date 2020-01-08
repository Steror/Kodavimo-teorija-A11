import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main (String[] args) {
        int columns, rows;   //Stulpeliai, eilutes
        Random r = new Random();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Kodo ilgis (n): ");
        columns = scanner.nextInt();
        System.out.println("Dimensija (k): ");
        rows = scanner.nextInt();
        while (rows >= columns)
        {
            System.out.println("Dimensija turi buti mazesne uz ilgi: ");
            rows = scanner.nextInt();
        }
        int[][] matrix_G = new int[rows][columns];   //Generuojanti matrica
        String answer;
        do
        {
            System.out.println("Ar norite patys suvesti generuojancia matrica (t/n)?");
            answer = scanner.next();
        }while(!answer.equals("t") && !answer.equals("n"));
        if (answer.equals("t")) //Suvedam matrica
        {
            for (int i = 0; i < rows; i++){
                String row;
                do {
                    System.out.println("Iveskite " + columns + " simbolius " + (i + 1) + " matricos eilutei: ");
                    row = scanner.next();
                }while(row.length() != columns);
                for (int j = 0; j < columns; j++)
                {
                    matrix_G[i][j] = Character.getNumericValue(row.charAt(j));
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
        //Atspausdina generuojancia matrica
        for (int i = 0; i < rows; i++){
            System.out.print("(");
            for (int j = 0; j < columns; j++)
            {
                System.out.print(matrix_G[i][j] + " ");
            }
            System.out.println(")");
        }

        //Vektoriaus ivedimas
        String messageString;
        int[] messageArray = new int [rows];   //Zinute skaitmenimis
        do {
            System.out.println("Iveskite vektoriu (" + rows + " ilgio): ");
            messageString = scanner.next();
        }while(messageString.length() != rows);
        //Tikimybes ivedimas
        double probability;
        do {
            System.out.println("Iveskite klaidos tikimybe(0-1): ");
            String temp = scanner.next();
            temp = temp.replaceAll(",", ".");
            probability = Double.parseDouble(temp);
        }while(!((probability >= 0) && (probability <= 1)));

        //Skaitmenis suveda i integer masyva
        for(int i=0; i<messageString.length(); i++)
        {
            char temp = messageString.charAt(i);
            messageArray[i] = Integer.parseInt(String.valueOf(temp));
        }

        //Isveda vektoriu ir tikimybe
        System.out.print("           Vektorius: ");
        for (int i=0; i<messageArray.length; i++)
        {
            System.out.print(messageArray[i]);
        }
        System.out.println("   Tikimybe: " + probability);

        //Isveda uzkoduota vektoriu
        Encoder encoder = new Encoder(matrix_G);
        int[] encodedArray = encoder.Encode(messageArray);
        System.out.print("Uzkoduotas vektorius: ");
        for (int i=0; i<encodedArray.length; i++)
        {
            System.out.print(encodedArray[i]);
        }

        //Siunciam zinute per kanala
        Channel channel = new Channel(probability);
        int[] errorArray = channel.sendMessage(encodedArray);

        //Isveda klaidu vektoriu
        System.out.print("\n    Klaidu vektorius: ");
        for (int i=0; i<errorArray.length; i++)
        {
            System.out.print(errorArray[i]);
        }
        //Isveda vektoriu isejusi is kanalo
        System.out.print("\n    Gautas vektorius: ");
        for (int i=0; i<encodedArray.length; i++)
        {
            System.out.print(encodedArray[i]);
        }

        //Leidzia pakeisti is kanalo atejusi vektoriu
        do {
            System.out.println("\nAr norite pakeisti vektoriu (t/n)?");
            answer = scanner.next();
        }while(!answer.equals("t") && !answer.equals("n"));
        if (answer.equals("t")) //Suvedam vektoriu
        {
            do {
                System.out.println("Iveskite " + encodedArray.length + " ilgio vektoriu: ");
                messageString = scanner.next();
            }while(messageString.length() != encodedArray.length);
            //Skaitmenis suveda i vektoriu
            for(int i=0; i<encodedArray.length; i++)
            {
                char temp = messageString.charAt(i);
                encodedArray[i] = Integer.parseInt(String.valueOf(temp));
            }
        }

        //Dekoduojam vektoriu
        Decoder decoder = new Decoder(matrix_G);
        int[] decodedMessage = decoder.Decode(encodedArray);
        
        //Isveda dekoduota vektoriu
        System.out.print("Dekoduotas vektorius: ");
        for (int i=0; i<decodedMessage.length; i++)
        {
            System.out.print(decodedMessage[i]);
        }
        System.out.println();
    }
}
