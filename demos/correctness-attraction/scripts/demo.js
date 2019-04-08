
function runCustom() {
     if (document.getElementById("run").value == "Stop") {
            document.getElementById("run").value = "Run";
            run();
        } else {
            stopOthers();
            updateValueProba();
            updateValueSize();
            updateValueNumber();
            document.getElementById("run").value = "Stop";
            run();
        }
}

function demo3() {
    if (document.getElementById("demo3").value == "Stop") {
        document.getElementById("demo3").value = "Demo3";
        run();
    } else {
        stopOthers();
        document.getElementById("number").value = 40;
        document.getElementById("size").value = 100;
        document.getElementById("probability").value = 500;
        document.getElementById("Antifragile").checked = true;
        document.getElementById("Integer").checked = true;
        document.getElementById("Boolean").checked = false;
        updateValueProba();
        updateValueSize();
        updateValueNumber();
        document.getElementById("demo3").value = "Stop";
        run();
    }
}

function demo2() {
    if (document.getElementById("demo2").value == "Stop") {
        document.getElementById("demo2").value = "Demo2";
        run();
    } else {
        stopOthers();
        document.getElementById("number").value = 40;
        document.getElementById("size").value = 200;
        document.getElementById("probability").value = 50;
        document.getElementById("Antifragile").checked = true;
        document.getElementById("Integer").checked = true;
        document.getElementById("Boolean").checked = true;
        updateValueProba();
        updateValueSize();
        updateValueNumber();
        document.getElementById("demo2").value = "Stop";
        run();
    }
}

function demo1() {
    if (document.getElementById("demo1").value == "Stop") {
        document.getElementById("demo1").value = "Demo1";
        run();
    } else {
        stopOthers();
        document.getElementById("number").value = 40;
        document.getElementById("size").value = 100;
        document.getElementById("probability").value = 50;
        document.getElementById("Antifragile").checked = true;
        document.getElementById("Integer").checked = true;
        document.getElementById("Boolean").checked = false;
        updateValueProba();
        updateValueSize();
        updateValueNumber();
        document.getElementById("demo1").value = "Stop";
        run();
    }
}

function stopOthers() {
    if (document.getElementById("demo1").value == "Stop") {
        demo1();
    } else if (document.getElementById("demo2").value == "Stop") {
        demo2();
    } else if (document.getElementById("demo3").value == "Stop") {
        demo3();
    } else if (document.getElementById("run").value == "Stop") {
        runCustom();
    }
    perturbationPerExecution = 0;
    successRate = 0;
}