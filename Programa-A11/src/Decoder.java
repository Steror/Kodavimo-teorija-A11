//import java.util.;

import java.util.*;

public class Decoder {
    private final int[][] matrix_G; //Generuojanti matrica
    private final int[][] matrix_H; //Kontroline matrica
    private final HashMap<String, Integer> syndromeWeightMap;

    public Decoder(final int[][] matrix_G)
    {
        this.matrix_G = matrix_G;
        int rows = matrix_G.length;
        int columns = matrix_G[0].length;
        //Sudaro kontroline matrica
        this.matrix_H = new int[columns - rows][columns];
        //Perrasoma desine matricos puse i kaire kontrolines puse, is eilutes i stulpeli
        for (int i=0; i<rows; i++)
        {
            for (int j=0; j<columns - rows; j++)
            {
                matrix_H[j][i] = matrix_G[i][j + rows];
            }
        }
        //Sukuriama vienetine matrica kaireje puseje
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
        //Suskaiciuoja visas galimas lyderiu reiksmes 2^rows
        int leaderCount = (int) Math.pow(2, rows);
        Encoder encoder = new Encoder(matrix_G);
        int[] array = new int[rows];
        for (int i=0; i<leaderCount; i++)
        {
            //Pavercia skaicius i binarini pavidala
            String bits = Integer.toBinaryString(i);
            String missingZeroes = String.join("", Collections.nCopies(rows - bits.length(), "0"));
            String fullString = missingZeroes + bits;
            for (int j=0; j<fullString.length(); j++)
            {
                array[j] = Character.getNumericValue(fullString.charAt(j));
            }
            codeWords.add(encoder.Encode(array));
        }

        //Randa visas galimas skaiciu kombinacijas
        List<int[]> codeVariations = new ArrayList<>();
        int bound = (int) Math.pow(2, columns);
        for (int i = 0; i < bound; i++) {
            //Pavercia skaicius i binarini pavidala
            int[] array2 = new int[columns];
            String bits = Integer.toBinaryString(i);
            String missingZeroes = String.join("", Collections.nCopies(columns - bits.length(), "0"));
            String fullString = missingZeroes + bits;
            for (int j=0; j<fullString.length(); j++)
            {
                array2[j] = Character.getNumericValue(fullString.charAt(j));
            }
            //Ismeta codeWords panaudotus zodzius
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
        //Prie klases lyderiu prideda pirma vektoriu
        cosetLeaders.add(codeWords.get(0));
        for (int i=0; i<Math.pow(2, columns - rows) - 1; i++)
        {
            //Gauna standartines lenteles eilute
            List<int[]> cosetRow = new ArrayList<>();
            //Suranda klases lyderi su maziausiu svoriu
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

            //Prideda klases lyderi prie kiekvieno kodo zodzio
            for (int j=0; j<codeWords.size(); j++)
            {
                int[] codeWord;
                codeWord = codeWords.get(j).clone();
                for (int k=0; k<codeWord.length; k++)
                {
                    codeWord[k] = (codeWord[k] + cosetLeader[k]) % 2;
                }
                cosetRow.add(codeWord); //Gauta  standartines lenteles eilute
            }

            //Is visu galimu kombinaciju isima panaudotas standartines lenteles eiluteje
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
            //Prideda klases lyderi prie lyderiu saraso
            cosetLeaders.add(cosetRow.get(0));
        }

        //Sudaro sindromu ir svorio Map
        //Klasiu lyderiu transponuotus vektorius sudaugina su kontroline matrica, sandaugos rezultata ir klases lyderio svori sudeda i Map
        this.syndromeWeightMap = new HashMap<>();
        for (int i=0; i<cosetLeaders.size(); i++)
        {
            int weight = 0;
            for (int j=0; j<cosetLeaders.get(0).length; j++)
            {
                if (cosetLeaders.get(i)[j] == 1)
                {
                    weight++;
                }
            }
            this.syndromeWeightMap.put(Arrays.toString(MultiplyByVectorT(cosetLeaders.get(i))), weight);
        }
    }

    //Sudaugina zinute su H matrica ir grazina uzkoduota zinute
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

    //Dekoduoja uzkoduotą vektoriu
    public int[] Decode(int[] message)
    {
        //Daugina transponuota vektoriu is H matricos
        int[] syndromeV = MultiplyByVectorT(message);

        //Randa atitinkamo sindromo svori
        int weight = this.syndromeWeightMap.get(Arrays.toString(syndromeV));

        //Pradine pozicija - pirmas bitas
        int position = 0;
        int lastWeight;

        //Dekoduoja, kol svoris lygus nuliui
        while (weight != 0)
        {
            lastWeight = weight;
            //Pakeicia vektoriaus bita
            message[position] = (message[position] + 1) % 2;
            int[] syndrome = MultiplyByVectorT(message);
            weight = this.syndromeWeightMap.get(Arrays.toString(syndrome));

            //Jei svoris nesumazejo, grazina pakeista bita atgal
            if (weight >= lastWeight)
            {
                message[position] = (message[position] + 1) % 2;
                weight = lastWeight;
            }
            position++;
        }
        //Grazina pirmus k bitus
        int[] decodedMessage = new int[matrix_G.length];
        for (int i=0; i<matrix_G.length; i++)
        {
            decodedMessage[i] = message[i];
        }
        return decodedMessage;
    }
}
