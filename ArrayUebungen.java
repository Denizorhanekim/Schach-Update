public class ArrayUebungen {


    public static int getSum(int[] elements){
        int sum = 0;
        for(int i = 0; i < elements.length; i++){
            sum += elements[i];
        }
        return sum;
    }


    public static int getSum(int[][] elements){
        int sum = 0;
        for(int i = 0; i < elements.length; i++){
            sum += getSum(elements[i]);
        }
        return sum;
    }


    public static int[] concatenate(int[] arr1, int[] arr2) {
        int[] result = new int[arr1.length + arr2.length];
        for (int i = 0; i < arr1.length; i++) {
            result[i] = arr1[i];
        }
        for (int i = 0; i < arr2.length; i++) {
            result[arr1.length + i] = arr2[i];
        }
        return result;
    }


    public static int[] filter(int[] arr, int min, int max) {
        int count = 0;
        for (int value : arr) {
            if (value >= min && value <= max) {
                count++;
            }
        }

        int[] result = new int[count];
        int index = 0;
        for (int value : arr) {
            if (value >= min && value <= max) {
                result[index++] = value;
            }
        }

        return result;
    }
}
