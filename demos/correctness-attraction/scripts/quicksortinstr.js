/*
    perturbed quicksort function
*/

var antiFragile = [2, 3, 4, 5, 6, 7, 8, 10, 11, 14, 17, 20, 22, 23, 28, 29, 31, 34, 35, 12];
var robust = [9, 15, 19];
var fragile = [0, 1, 13, 18, 24, 26, 27, 30, 33, 36, 38, 39, 40, 41, 42, 43, 44, 45, 46, 16, 21, 25, 32, 37];

function quicksort_instr(array, beg, end) {
    var left = p(0, beg);
    var right = p(1, end);
    var pivot = p(9,
        array[p(8,
         Math.floor(p(2, beg) + p(7, (p(5, (p(3, end) - p(4, beg))) / p(6, 2))))
    )]);

    /*Example of perturbation point:
        Here in the condition of the while loop, there is 3 perturbation point:
            2 integers perturbation point: values of left and right
            1 boolean perturbation point: the result of the comparison of the both value
    */

    while (p(12, p(10, left) <= p(11, right))) {

        while (p (16, p(14, array[p(13, left)]) < p(15, pivot))) {
            p(17, left++);
        }

        while ( p(21, p(19, array[p(18, right)]) > p(20, pivot))) {
            p(22, right--);
        }

        if ( p(25, p(23, left) <= p(24, right))) {
            swap(array, p(26, left), p(27, right));
            p(28, left++);
            p(29, right--);
        }
    }
    if (p (32, p(30, beg) < p(31, right) )) {
        quicksort_instr(array, p(33, beg), p(34, right));
    }
    if (p(37, p(35, end) > p(36, left))) {
        quicksort_instr(array, p(38, left), p(39, end));
    }
}

function swap(array, i, j) {
    var x = p(41, array[p(40, i)]);
    array[p(42,i)] = p(44, array[p(43, j)]);
    array[p(45,j)] = p(46, x);
}
