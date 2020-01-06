public class Encoder {
    private final int[][] matrix_G;

    public Encoder (int[][] matrix_G)
    {
        this.matrix_G = matrix_G;
    }

    //Sudaugina žinutę su G matrica ir gražina užkoduotą žinutę
    public int[] Encode (int[] message)
    {
        int[] result = new int[matrix_G[0].length];
        for (int i=0; i<matrix_G.length; i++)
        {
            for (int j=0; j<matrix_G[0].length; j++){
                result[j] += matrix_G[i][j] * message[i];
                result[j] = result[j] % 2;
            }
        }
        return result;
    }
}
