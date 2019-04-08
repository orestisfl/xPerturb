function oracle(input, output) {
    if (input.length != output.length) {
        return false;
    }
    var referenceOutput = input.slice().sort(function (a,b){return a-b;});
    for (var i = 0 ; i < input.length ; i++) {
        if (referenceOutput[i] != output[i]) {
            return false;
        }
    }
    return true;
}