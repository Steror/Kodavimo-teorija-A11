import java.util.*;

public class Decoder {
    private final int[][] matrix_G; //Generuojanti matrica
    private final int[][] matrix_H; //Kontrolinė matrica
    private final HashMap<String, Integer> syndromeWeightMap;

    public Decoder(final int[][] matrix_G)
    {
        this.matrix_G = matrix_G;
        int rows = matrix_G.length;
        int columns = matrix_G[0].length;
        //Sudaro kontrolinę matricą
        this.matrix_H = new int[columns - rows][columns];
        //Perrašoma dešinė matricos pusė į kairę kontrolinės pusę, iš eilutės į stulpelį
        for (int i=0; i<rows; i++)
        {
            for (int j=0; j<columns - rows; j++)
            {
                matrix_H[j][i] = matrix_G[i][j + rows];
            }
        }
        //Sukuriama vienetinė matrica kairėje pusėje
        for (int i=rows; i<columns; i++)
        {
            for (int j=0; j<columns - rows; j++)
            {
                if (i - rows == j)
                {
                    matrix_H[j][i] = 1;
                }
                else
                {
                    matrix_H[j][i] = 0;
                }
            }
        }

        List<int[]> codeWords = new ArrayList<>();
        //Suskaičiuoja visas galimas lyderių reikšmes 2^rows
        int leaderCount = (int) Math.pow(2, rows);
        Encoder encoder = new Encoder(matrix_G);
        int[] array = new int[rows];
        for (int i=0; i<leaderCount; i++)
        {
            //Paverčia skaičius į binarinį pavidalą
            String bits = Integer.toBinaryString(i);
            String missingZeroes = String.join("", Collections.nCopies(rows - bits.length(), "0"));
            String fullString = missingZeroes + bits;
            for (int j=0; j<fullString.length(); j++)
            {
                array[j] = Character.getNumericValue(fullString.charAt(j));
            }
            codeWords.add(encoder.Encode(array));
        }

        //Randa visas galimas skaičių kombinacijas
        List<int[]> codeVariations = new ArrayList<>();
        int bound = (int) Math.pow(2, columns);
        for (int i = 0; i < bound; i++) {
            //Paverčia skaičius į binarinį pavidalą
            int[] array2 = new int[columns];
            String bits = Integer.toBinaryString(i);
            String missingZeroes = String.join("", Collections.nCopies(columns - bits.length(), "0"));
            String fullString = missingZeroes + bits;
            for (int j=0; j<fullString.length(); j++)
            {
                array2[j] = Character.getNumericValue(fullString.charAt(j));
            }
            //Išmeta codeWords panaudotus žodžius
            int mismatches = 0;
            for (int j=0; j<codeWords.size(); j++)
            {
                if (!Arrays.equals(codeWords.get(j), array2))
                {
                    mismatches++;
                    if (mismatches == codeWords.size())
                        codeVariations.add(array2);
                }
            }
        }

        List<int[]> cosetLeaders = new ArrayList<>();
        //Prie klasės lyderių pridedam pirmą vektorių
        cosetLeaders.add(codeWords.get(0));
        for (int i=0; i<Math.pow(2, columns - rows) - 1; i++)
        {
            //Gauna standartinės lentelės eilutę
            List<int[]> cosetRow = new ArrayList<>();
            //Suranda klasės lyderį su mažiausiu svoriu
            int minWeight = -1;
            int minWeightIndex = 0;
            for (int j=0; j<codeVariations.size(); j++)
            {
                int count = 0;
                int[] tempVector = codeVariations.get(j);
                for (int k=0; k<tempVector.length; k++)
                {
                    if (tempVector[k] == 1)
                        count++;
                }
                if ((minWeight > count) || (minWeight == -1))
                {
                    minWeight = count;
                    minWeightIndex = j;
                }
            }
            int[] cosetLeader = codeVariations.get(minWeightIndex); //Lyderis

            //Prideda klasės lyderį prie kiekvieno kodo žodžio
            for (int j=0; j<codeWords.size(); j++)
            {
                int[] codeWord = new int[columns];
                codeWord = codeWords.get(j).clone();
                for (int k=0; k<codeWord.length; k++)
                {
                    codeWord[k] = (codeWord[k] + cosetLeader[k]) % 2;
                }
                cosetRow.add(codeWord); //standartinės lentelės eilutė
            }

            //cosetRow = codeWords;   //Gauta standartinė eilutė
            //Iš visų galimų kombinacijų išima panaudotas standartinės lentelės eilutėje
            ////////////////////////////////////////////////////////////////////////////////////////////////////////
            for (int j = 0; j<cosetRow.size(); j++)
            {
                for (int k=0; k<codeVariations.size(); k++)
                {
                    if (Arrays.equals(cosetRow.get(j), codeVariations.get(k)))
                    {
                        codeVariations.remove(k);
                        k--;
                    }
                }
            }
            ////////////////////////////////////////////////////////////////////////////////////////////////////////
            //Prideda klasės lyderį prie lyderių sąrašo
            cosetLeaders.add(cosetRow.get(0));
        }

        //Sudaro sindromų ir svorio Map
        //Klasių lyderių transponuotus vektorius sudaugina su kontroline matrica, sandaugos rezultatą ir klasės lyderio svorį sudeda į Map
        this.syndromeWeightMap = new HashMap<>();
        System.out.print("\n");
        for (int i=0; i<cosetLeaders.size(); i++)
        {
            int weight = 0;
            System.out.print(Arrays.toString(cosetLeaders.get(i)));
            for (int j=0; j<cosetLeaders.get(0).length; j++)
            {
                if (cosetLeaders.get(i)[j] == 1)
                {
                    weight++;
                }
            }
            System.out.println(" "+weight);
            this.syndromeWeightMap.put(Arrays.toString(MultiplyByVectorT(cosetLeaders.get(i))), weight);
        }
    }

    //Sudaugina žinutę su H matrica ir gražina užkoduotą žinutę
    public int[] MultiplyByVectorT (int[] message)
    {
        int[] result = new int[matrix_H.length];
        for (int i=0; i<matrix_H.length; i++)
        {
            for (int j=0; j<matrix_H[0].length; j++){
                result[i] += matrix_H[i][j] * message[j];
                result[i] = result[i] % 2;
            }
        }
        return result;
    }

    //Dekoduoja užkoduotą vektorių
    public int[] Decode(int[] message)
    {
        //Daugina transponuotą vektorių iš H matricos
        int[] syndromeV = MultiplyByVectorT(message);

        //Randa atitinkamo sindromo svorį
        //System.out.println(syndromeWeightMap);
        //System.out.println(Arrays.toString(syndromeV));
        int weight = this.syndromeWeightMap.get(Arrays.toString(syndromeV));

        //Pradinė pozicija - pirmas bitas
        int position = 0;
        int lastWeight;

        //Dekoduoja, kol svoris lygus nuliui
        while (weight != 0)
        {
            lastWeight = weight;
            //Pakeičiam vektoriaus bitą
            message[position] = (message[position] + 1) % 2;
            int[] syndrome = MultiplyByVectorT(message);
            weight = this.syndromeWeightMap.get(Arrays.toString(syndrome));

            //Jei svoris nesumažėjo, gražina pakeistą bitą atgal
            if (weight >= lastWeight)
            {
                message[position] = (message[position] + 1) % 2;
                weight = lastWeight;
            }
            position++;
        }
        //Gražina pirmus k bitus
        int[] decodedMessage = new int[matrix_G.length];
        for (int i=0; i<matrix_G.length; i++)
        {
            decodedMessage[i] = message[i];
        }
        return decodedMessage;
    }
}
