import java.util.Random;
import java.util.Scanner;

public class MainIO {

    public static void main (String[] args) {
        int n, k;
        Random r = new Random();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Stulpeliu skaicius (n): ");
        n = scanner.nextInt();
        System.out.println("Eiluciu skaicius (k): ");
        k = scanner.nextInt();
        int[][] matrix = new int[k][n];
        System.out.println("Ar norite patys suvesti matrica (t/n)?");
        String answer = scanner.next();
        while(!answer.equals("t") && !answer.equals("n"))
        {
            System.out.println("Ar norite patys suvesti matrica (t/n)?");
            answer = scanner.next();
        }
        if (answer.equals("t")) //suvedam matrica
        {
            System.out.println("Iveskite " + n*k + " matricos elementus: ");
            for (int i = 0; i < k; i++){
                for (int j = 0; j < n; j++)
                {
                    matrix[i][j] = scanner.nextInt();
                }
            }
        }
        else    //generuojam matrica G
        {
            for (int i = 0; i < k; i++){
                for (int j = 0; j < n; j++)
                {
                    if(k-j > 0) //Kuriama vienetine matrica
                    {
                        if(i == j)
                            matrix[i][j] = 1;
                        else
                            matrix[i][j] = 0;
                    }
                    else
                    matrix[i][j] = r.nextInt(2);
                }
            }
        }

        for (int i = 0; i < k; i++){
            System.out.print("(");
            for (int j = 0; j < n; j++)
            {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println(")");
        }
        scanner.next(); //Reikalauja dar vieno paspaudimo pries baigiant darba
    }
}
