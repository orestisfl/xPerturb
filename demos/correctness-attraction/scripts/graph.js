function graph() {

        var probabilityOverTime = [];
        var nbPerturbationPerExecOverTime = [];
        var successRateOverTime = [];

        var chart = new CanvasJS.Chart("chartContainer",{
            title :{
                text: "",
            },
            toolTip: {
                shared: true
            },
            legend: {
            	verticalAlign: "top",
                horizontalAlign: "center",
                    fontSize: 14,
            	fontWeight: "bold",
                fontFamily: "calibri",
                fontColor: "dimGrey"
            },
            axisX: {
                title: ""
            },
            axisY:{
            	title: '#Perturbation/Exec',
                includeZero: true
            },
            axisY2:{
            	title: 'Probability (%)',
                includeZero: true
            },
            data: [
                {
                    type: "line",
                    dataPoints: nbPerturbationPerExecOverTime,
                    xValueType: "dateTime",
                    showInLegend: true,
                    name: "Nb Perturbation / Exec" ,
                },
                {
                    type: "line",
                    axisYType: "secondary",
                    dataPoints: probabilityOverTime,
                    xValueType: "dateTime",
                    showInLegend: true,
                    name: "Probability" ,
                }
            ],
        });

        var xVal = 0;
        var updateInterval = 100;
        var dataLength = 200; // number of dataPoints visible at any point

        var updateChart = function (count) {
            count = count || 1;
            // count is number of times loop runs to generate random dataPoints.
            for (var j = 0; j < count; j++) {
                nbPerturbationPerExecOverTime.push({x:xVal , y: perturbationPerExecution})
                probabilityOverTime.push({x:xVal , y: probabilityToGraph})
                xVal++;
            };
            if (nbPerturbationPerExecOverTime.length > dataLength) {
                nbPerturbationPerExecOverTime.shift();
                probabilityOverTime.shift();
            }
            chart.render();
        };

        // generates first set of dataPoints
        updateChart(dataLength);

        // update chart after specified time.
        setInterval(function(){updateChart()}, updateInterval);
}