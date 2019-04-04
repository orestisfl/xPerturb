/*
    original quicksort function
*/
function quicksort(array, beg, end) {

        var left = beg;
        var right = end;
        var pivot = array[beg + ((end - beg) / 2)];

        while (left <= right) {

            while (array[left] < pivot) {
                left++;
            }

            while (array[right] > pivot) {
                right--;
            }

            if (left <= right) {
                swap(array, left, right);
                left++;
                right--;
            }

        }
        if (beg < right)
            sort(array, beg, right);
        if (end > left)
            sort(array, left, end);
}

function swap( array, i, j) {
    var x = array[i];
    array[i] = array[j];
    array[j] = x;
}