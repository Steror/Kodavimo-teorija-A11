public class Channel {
    private final double probability;

    public Channel(final double probability)
    {
        this.probability = probability;
    }

    public int[] sendMessage(int[] message)
    {
        int[] errors = new int[message.length];
        for (int i=0; i<message.length; i++)
        {
            if (Math.random() < probability)
            {
                message[i] = (message[i] + 1) % 2;
                errors[i] = 1;
            }
        }
        return errors;
    }
}
