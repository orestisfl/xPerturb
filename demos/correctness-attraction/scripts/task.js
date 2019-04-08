
var perturbationPerExecution = 0;
var successRate = 0;
var running = false;
var probabilityToGraph = 0.005;

async function run() {
    running = !running;
    if (running) {
        do {
                // initialize
                perturbationCount = 0;
                var number = document.getElementById("number").value;
                var nbSuccess = 0;
                var bound = Number.MAX_SAFE_INTEGER;
                var size = document.getElementById("size").value;
                probability = document.getElementById("probability").value / 1000;
                initPerturbationPoint();

                //run
                for (var i = 0 ; i < number; i++) {
                    nbSuccess += psort(size, bound) ? 1 : 0;
                }
                //display
                successRate = ((nbSuccess / number) * 100).toFixed(2);
                probabilityToGraph = probability;
                perturbationPerExecution = ((perturbationCount / number)).toFixed(2);
                document.getElementById("success").innerHTML = successRate;
                document.getElementById("nbPerturbationPerExecution").innerHTML = perturbationPerExecution;
                perturbationPerExecution = Math.floor(perturbationPerExecution);
                successRate = Math.floor(successRate);
                drawWithInputValue(nbSuccess, number);
                await sleep(100);
            } while (running);
    } else {
        document.getElementById("success").innerHTML = "0.0";
        document.getElementById("nbPerturbationPerExecution").innerHTML = "0.0"
        drawWithInputValue(0, number);
        perturbationPerExecution = 0;
        successRate = 0;
    }
}

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

function updateValueProba() {
     probability = document.getElementById("probability").value;
     document.getElementById("labelProbability").innerHTML = (probability / 1000);
     probabilityToGraph = probability / 1000;
}

function updateValueSize() {
     var size = document.getElementById("size").value;
     document.getElementById("labelSize").innerHTML = size;
}

function updateValueNumber() {
     var number = document.getElementById("number").value;
     document.getElementById("labelNumber").innerHTML = number;
}

function init() {
    graph();
    draw(0);
    drawWithInputValue(0, number);
    updateValueProba();
    updateValueNumber();
    updateValueSize();
}