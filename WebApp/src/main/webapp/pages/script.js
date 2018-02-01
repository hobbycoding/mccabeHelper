var jsonArray;
var summaryCategory = ["시스템", "업무분류", "전체Program", "테스트된 Program","80% 이상 Program", "80% 미만 Program",
    "전체 Funtion", "테스트된 Function", "총라인수", "테스트라인수", "COVERAGE", "미테스트 Program"];
var subDetailCategory = ["프로그램 영문명", "프로그램 한글명", "Function 영문명", "Function 한글명", "서비스 ID",
    "업무명", "담당자", "유형", "전체라인수", "Covered 라인수", "Coverage(%)"];
var tableCategory = ["프로그램 영문명", "프로그램 한글명", "Function 영문명", "Function 한글명", "서비스 ID",
    "업무명", "담당자", "유형", "전체라인수", "Covered 라인수", "Coverage(%)"];
var table1Category = ["패키지명", "Coverage(%)"];
var table2Category = ["프로그램 영문명", "프로그램 한글명", "Coverage(%)"];
var table3Category = ["Function 영문명", "Function 한글명", "Coverage(%)"];

function summary_refresh(date) {
    var data = {"method": "getOverView", "where": date};
    var callback = function () {
        if (this.readyState == 4 && this.status == 200) {
            var header = "", content = "";
            jsonArray = JSON.parse(this.responseText);
            header += "<table style=\"width:100%; border-spacing:0;\"><tr>";
            if (jsonArray.length == 0 ) {
                summary_refresh(null);
                return;
            }
            for (var index in jsonArray) {
                content += "<tr>";
                for (var entry in summaryCategory) {
                    if (index == undefined || index == 0)
                        header += "<th>" + summaryCategory[entry] + "</th>";
                    content += "<td>" + jsonArray[index][summaryCategory[entry]] + "</td>";
                }
            }
            header += "</tr>";
            document.getElementById("table").innerHTML = header + content + "</table>";
        }
    };
    if (date != null) {
        sendServer(data, callback);
    } else {
        var header = "<table style=\"width:100%; border-spacing:0;\"><tr>";
        for (var entry in summaryCategory) {
            header += "<th>" + summaryCategory[entry] + "</th>";
        }
        document.getElementById("table").innerHTML = header + "</table>";
    }
}

function getCategoryList(date) {
    if (document.getElementById("categoryList").childElementCount > 0) {
        getSubDetailView(document.getElementById("categoryList").options[document.getElementById("categoryList").selectedIndex].value)
        return;
    }
    var data = {"method": "getCategoryList", "where": date};
    var callback = function () {
        var content = "";
        if (this.readyState == 4 && this.status == 200) {
            jsonArray = JSON.parse(this.responseText);
            for (var index in jsonArray) {
                var v = jsonArray[index]["업무분류"];
                content += "<option value=\"" + jsonArray[index]["업무분류"] + "\">" + v + "</option>";
            }
            document.getElementById("categoryList").innerHTML = content;
        }
    };
    sendServer(data, callback);
}

function getSubDetailView(item) {
    var data = {
        "method": "getSubDetailView", "category": item,
        "where": document.getElementById('search').value, "JOB_NAME": document.getElementById('JOB_NAME').value,
        "MANAGER": document.getElementById('MANAGER').value, "FILE_TYPE": document.getElementById('FILE_TYPE').value
    };
    var callback = function () {
        if (this.readyState == 4 && this.status == 200) {
            var header = "", content = "";
            jsonArray = JSON.parse(this.responseText);
            header += "<table style=\"width:100%; border-spacing:0;\"><tr>";
            for (var index in jsonArray) {
                content += "<tr>";
                for (var entry in subDetailCategory) {
                    if (index == 0)
                        header += "<th>" + subDetailCategory[entry] + "</th>";
                    content += "<td>" + jsonArray[index][subDetailCategory[entry]] + "</td>";
                }
            }
            header += "</tr>";
            document.getElementById("table").innerHTML = header + content + "</table>";
        }
    };
    if (item != null) {
        sendServer(data, callback);
    } else {
        var header = "<table style=\"width:100%; border-spacing:0;\"><tr>";
        for (var entry in subDetailCategory) {
            header += "<th>" + subDetailCategory[entry] + "</th>";
        }
        document.getElementById("table").innerHTML = header + "</table>";
    }
}

function sendServer(data, callback) {
    var dbParam = JSON.stringify(data);
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = callback;
    xmlHttp.open("POST", "../rest/mccabe/process", true);
    xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlHttp.send(dbParam);
}

function getJobNameList(date) {
    var data = {"method": "getJobList", "where": date};
    if (document.title == "chartView") {
        data.from = document.getElementById('from').value;
    }
    var callback = function () {
        var content = "";
        if (this.readyState == 4 && this.status == 200) {
            jsonArray = JSON.parse(this.responseText);
            for (var index in jsonArray) {
                var v = jsonArray[index]["업무명"];
                content += "<option value=\"" + jsonArray[index]["업무명"] + "\">" + v + "</option>";
            }
            document.getElementById("jobNameList").innerHTML = content;
            clearTables();
        }
    };
    sendServer(data, callback);
}

var job_name;

function getFirstTable(item) {
    var data = {
        "method": "getJobListTable", "order": "1", "where": document.getElementById('search').value,
        "job_name": item
    };
    job_name = item;
    if (document.title == "chartView") {
        data.from = document.getElementById('from').value;
        createChart("",[],[]);
    }
    var callback = function () {
        if (this.readyState == 4 && this.status == 200) {
            var content = "";
            jsonArray = JSON.parse(this.responseText);
            for (var index in jsonArray) {
                content += "<tr>";
                for (var entry in table1Category) {
                    content += "<td>" + jsonArray[index][table1Category[entry]] + "</td>";
                }
            }
            document.getElementById("cTable1_tbody").innerHTML = content;
            document.getElementById("cTable2_tbody").innerHTML = "";
            document.getElementById("cTable3_tbody").innerHTML = "";
            addRowHandlers("cTable1_tbody");
        }
    };
    if (item != null) {
        sendServer(data, callback);
    }
}
var file_package;

function getSecondTable(package) {
    var data = {
        "method": "getJobListTable", "order": "2", "where": document.getElementById('search').value,
        "file_package": package, "job_name": job_name
    };
    file_package = package;
    if (document.title == "chartView") {
        data.from = document.getElementById('from').value;
    }
    var callback = function () {
        if (this.readyState == 4 && this.status == 200) {
            var content = "";
            jsonArray = JSON.parse(this.responseText);
            for (var index in jsonArray) {
                content += "<tr>";
                for (var entry in table2Category) {
                    content += "<td>" + jsonArray[index][table2Category[entry]] + "</td>";
                }
            }
            document.getElementById("cTable2_tbody").innerHTML = content;
            document.getElementById("cTable3_tbody").innerHTML = "";
            addRowHandlers("cTable2_tbody");
        }
    };
    if (package != null) {
        sendServer(data, callback);
    }
}
var file_name;

function getThirdTable(name) {
    var data = {
        "method": "getJobListTable", "order": "3", "where": document.getElementById('search').value,
        "file_package": file_package, "job_name": job_name, "file_name": name
    };
    file_name = name;
    if (document.title == "chartView") {
        data.from = document.getElementById('from').value;
    }
    var callback = function () {
        if (this.readyState == 4 && this.status == 200) {
            var content = "";
            jsonArray = JSON.parse(this.responseText);
            for (var index in jsonArray) {
                content += "<tr>";
                for (var entry in table3Category) {
                    content += "<td>" + jsonArray[index][table3Category[entry]] + "</td>";
                }
            }
            document.getElementById("cTable3_tbody").innerHTML = content;
            addRowHandlers("cTable3_tbody");
        }
    };
    if (name != null) {
        sendServer(data, callback);
    }
}

function clearTables() {
    document.getElementById("cTable1_tbody").innerHTML = "";
    document.getElementById("cTable2_tbody").innerHTML = "";
    document.getElementById("cTable3_tbody").innerHTML = "";
    if (document.title == "chartView") {
        createChart("",[],[]);
    }
}

var function_name;
function getSourceView(name) {
    var data = {
        "method": "getCodes", "order": "4", "where": document.getElementById('search').value,
        "file_package": file_package, "job_name": job_name, "file_name": file_name, "function_name": name
    };
    function_name = name;
    createCodeMirror();
    var callback = function () {
        if (this.readyState == 4 && this.status == 200) {
            var jsonArray = JSON.parse(this.responseText);
            mirror.setValue(jsonArray[0]["CODES"]);
        }
    };
    if (name != null) {
        sendServer(data, callback);
    }
}

function getChartView(name, data, tab) {
    var callback = function () {
        if (this.readyState == 4 && this.status == 200) {
            jsonArray = JSON.parse(this.responseText);
            createChart(name, jsonArray.data, jsonArray.label);
            openTab(null, tab);
        }
    };
    sendServer(data, callback);
}

function addRowHandlers(tableId) {
    var table = document.getElementById(tableId);
    var rows = table.getElementsByTagName("tr");
    for (i = 0; i < rows.length; i++) {
        var currentRow = table.rows[i];
        var createClickHandler;
        if (tableId == "cTable1_tbody") {
            createClickHandler = function (row) {
                return function () {
                    var cell = row.getElementsByTagName("td")[0];
                    var id = cell.innerHTML;
                    if (document.title == "chartView") {
                        var data = {
                            "method": "getChartView", "order":"1", "from": document.getElementById('from').value,
                            "to": document.getElementById('search').value,
                            "job_name": job_name, "file_package":id
                        };
                        getChartView(id, data, "Package");
                    }
                    getSecondTable(id);
                };
            };
        } else if (tableId == 'cTable2_tbody') {
            createClickHandler = function (row) {
                return function () {
                    var cell = row.getElementsByTagName("td")[0];
                    var id = cell.innerHTML;
                    if (document.title == "chartView") {
                        var data = {
                            "method": "getChartView", "order":"2", "from": document.getElementById('from').value,
                            "to": document.getElementById('search').value,
                            "job_name": job_name, "file_package":file_package, "file_name":id
                        };
                        getChartView(id, data, "Class");
                    }
                    getThirdTable(id);
                };
            };
        } else if (tableId = 'cTable3_tbody') {
            createClickHandler = function (row) {
                return function () {
                    var cell = row.getElementsByTagName("td")[0];
                    var id = cell.innerHTML;
                    if (document.title == "chartView") {
                        var data = {
                            "method": "getChartView", "order":"3", "from": document.getElementById('from').value,
                            "to": document.getElementById('search').value,
                            "job_name": job_name, "file_package":file_package, "file_name":file_name, "function_name":id
                        };
                        getChartView(id, data, "Function");
                    } else {
                        getSourceView(id);
                        openTab(null, 'source');
                    }
                };
            };
        }
        currentRow.onclick = createClickHandler(currentRow);
    }
}

var chart;
function createChart(name, data, label) {
    var chartData = {
        labels:label,
        datasets: [
            {
                label: name,
                fill:false,
                backgroundColor:"rgb(75, 192, 192)",
                borderColor: "rgb(75, 192, 192)",
                data: data
            }
        ]
    };
    var options = {
        maintainAspectRatio: false,
        title: {
            display: true,
            text: 'Coverage(%)'
        },
        tooltips: {
            mode: 'index',
            intersect: false,
        },
        hover: {
            mode: 'nearest',
            intersect: true
        },
        scales: {
            xAxes: [{
                display: true,
                scaleLabel: {
                    display: true,
                }
            }],
            yAxes: [{
                display: true,
                ticks: {
                    beginAtZero: true,
                    stepSize: 20,
                    max: 100
                }
            }]
        }
    }
    if (chart == null) {
        chart = new Chart(document.getElementById("chartArea"), {
            type: 'line',
            data: chartData,
            options: options
        });
    } else {
        chart.config.data = chartData;
        chart.update();
    }
}

function openTab(evt, id) {
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    if (id == 'source') {
        document.getElementById(id).style.display = "block";
        if (evt != null)
            evt.currentTarget.className += " active";
    } else {
        document.getElementById('chartContainer').style.display = "block";
        if (evt != null) {
            evt.currentTarget.className += " active";
        } else {
            document.getElementById(id).className += " active";
        }
        // createChart();
    }
}

var mirror = null;

function createCodeMirror() {
    if (mirror == null) {
        mirror = CodeMirror.fromTextArea(document.getElementById("sourceArea"), {
            lineNumbers: false,
            mode: "text/x-java",
            matchBrackets: true,
            readOnly: true
        });
    }
    mirror.refresh();
}

function exportToCsv(filename) {
    var str = ConvertToCSV(jsonArray, arguments);
    var blob = new Blob(["\ufeff" + str], {type: 'text/csv;charset=utf-8;'});
    var link = document.createElement("a");
    if (link.download !== undefined) { // feature detection
        // Browsers that support HTML5 download attribute
        var url = URL.createObjectURL(blob);
        link.setAttribute("href", url);
        link.setAttribute("download", filename);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
}

function exportToCsvForStatus(filename, rawString) {
    var str = ConvertToStatusCSV(JSON.parse(rawString));
    var blob = new Blob([str], {type: 'text/csv;charset=utf-8;'});
    var link = document.createElement("a");
    if (link.download !== undefined) { // feature detection
        // Browsers that support HTML5 download attribute
        var url = URL.createObjectURL(blob);
        link.setAttribute("href", url);
        link.setAttribute("download", filename);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
}

function ConvertToStatusCSV(objArray) {
    var array = typeof objArray != 'object' ? JSON.parse(objArray) : objArray;
    var str = '';
    for (var i = 0; i < array.length; i++) {
        var line = '';
        if (typeOf(array[i]) == 'Object') {
            for (var index in array[i]) {
                if (line != '')
                    line += ',';
                var v = array[i][index];
                if (typeOf(v) != 'String')
                    line += ConvertToStatusCSV(v);
                else line += v;
            }
        } else if (typeOf(array[i]) == 'Array') {
            line = ConvertToStatusCSV(array[i]);
        } else if (typeOf(array[i]) == 'String') {
            if (str != '')
                line += ',';
            str += line + array[i];
            continue;
        }
        str += line + '\r\n';
    }
    return str;
}

function ConvertToCSV(objArray) {
    var array = typeof objArray != 'object' ? JSON.parse(objArray) : objArray;
    var str = '';
    for (var i = 0; i < array.length; i++) {
        var line = '';
        for (var add = 2; add < arguments[1].length; add++) {
            if (line != '')
                line += ','
            line += arguments[1][add];
        }
        for (var index in array[i]) {
            if (line != '')
                line += ','
            line += array[i][index];
        }
        str += line + '\r\n';
    }
    return str;
}

function typeOf(object) {
    if (object === null) {
        return "null";
    }
    else if (object === undefined) {
        return "undefined";
    }
    else if (object.constructor === "s".constructor) {
        return "String";
    }
    else if (object.constructor === [].constructor) {
        return "Array";
    }
    else if (object.constructor === {}.constructor) {
        return "Object";
    }
    else {
        return "don't know";
    }
}