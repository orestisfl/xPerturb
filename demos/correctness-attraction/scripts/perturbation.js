var probability = 0.005;
var perturbationCount = 0;
var activePerturbationPoints = [];
var typesToPerturb = [];

function p(location, value) {
    var rand = Math.random();
    if (rand < probability
        && activePerturbationPoints.includes(location)
        && typesToPerturb.includes(typeof value)) {
        perturbationCount++;
        if (typeof value == "number") {
            return Math.floor(value + 1);
        } else {
            return !value;
        }
    } else {
        return value;
    }
}