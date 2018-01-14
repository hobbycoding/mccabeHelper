function summary_refresh(date) {
    var category = ["시스템", "업무분류", "전체Program", "테스트된 Program", "80% 미만 Program",
        "전체 Funtion", "테스트된 Function", "총라인수", "테스트라인수", "COVERAGE", "미테스트 Program"];
    var data = {"method" : "getOverView", "where" : date};
    var callback = function () {
        if (this.readyState == 4 && this.status == 200) {
            var header = "", content = "", jsonArray = JSON.parse(this.responseText);
            header += "<table style=\"width:100%; border-spacing:0;\"><tr>";
            for (var index in jsonArray) {
                content += "<tr>";
                for (var entry in category) {
                    if (index == 0)
                        header += "<th>" + category[entry] + "</th>";
                    content += "<td>" + jsonArray[index][category[entry]] + "</td>";
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
        for (var entry in category) {
            header += "<th>" + category[entry] + "</th>";
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
        var jsonArray, content = "";
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
    var category = ["프로그램 영문명", "프로그램 한글명", "Function 영문명", "Function 한글명", "서비스 ID",
        "업무명", "담당자", "유형", "전체라인수", "Covered 라인수", "Coverage(%)"];
    var data = {"method" : "getSubDetailView", "category" : item,
        "where" : document.getElementById('search').value,  "JOB_NAME" : document.getElementById('JOB_NAME').value,
        "MANAGER" : document.getElementById('MANAGER').value,"FILE_TYPE" : document.getElementById('FILE_TYPE').value
    };
    var callback = function () {
        if (this.readyState == 4 && this.status == 200) {
            var header = "", content = "", jsonArray = JSON.parse(this.responseText);
            header += "<table style=\"width:100%; border-spacing:0;\"><tr>";
            for (var index in jsonArray) {
                content += "<tr>";
                for (var entry in category) {
                    if (index == 0)
                        header += "<th>" + category[entry] + "</th>";
                    content += "<td>" + jsonArray[index][category[entry]] + "</td>";
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
        for (var entry in category) {
            header += "<th>" + category[entry] + "</th>";
        }
        document.getElementById("table").innerHTML = header + "</table>";
    }
}

function sendServer(data, callback) {
    var dbParam = JSON.stringify(data);
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = callback;
    xmlhttp.open("POST", "../rest/mccabe/process", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlhttp.send(dbParam);
}

function getJobNameList(date) {
    if (document.getElementById("jobNameList").childElementCount > 0) {
        getSubDetailView(document.getElementById("jobNameList").options[document.getElementById("jobNameList").selectedIndex].value)
        return;
    }
    var data = {"method": "getJobList", "where": date};
    var callback = function () {
        var jsonArray, content = "";
        if (this.readyState == 4 && this.status == 200) {
            jsonArray = JSON.parse(this.responseText);
            for (var index in jsonArray) {
                var v = jsonArray[index]["업무명"];
                content += "<option value=\"" + jsonArray[index]["업무명"] + "\">" + v + "</option>";
            }
            document.getElementById("jobNameList").innerHTML = content;
        }
    };
    sendServer(data, callback);
}

function gettables(item) {
    var category = ["프로그램 영문명", "프로그램 한글명", "Function 영문명", "Function 한글명", "서비스 ID",
        "업무명", "담당자", "유형", "전체라인수", "Covered 라인수", "Coverage(%)"];
    var data = {"method" : "getSubDetailView", "category" : item,
        "where" : document.getElementById('search').value,  "JOB_NAME" : document.getElementById('JOB_NAME').value,
        "MANAGER" : document.getElementById('MANAGER').value,"FILE_TYPE" : document.getElementById('FILE_TYPE').value
    };
    var callback = function () {
        if (this.readyState == 4 && this.status == 200) {
            var header = "", content = "", jsonArray = JSON.parse(this.responseText);
            header += "<table style=\"width:100%; border-spacing:0;\"><tr>";
            for (var index in jsonArray) {
                content += "<tr>";
                for (var entry in category) {
                    if (index == 0)
                        header += "<th>" + category[entry] + "</th>";
                    content += "<td>" + jsonArray[index][category[entry]] + "</td>";
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
        for (var entry in category) {
            header += "<th>" + category[entry] + "</th>";
        }
        document.getElementById("table").innerHTML = header + "</table>";
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
    document.getElementById(id).style.display = "block";
    evt.currentTarget.className += " active";
    if (id == 'source')
        createCodeMirror()
}
var mirror = null;
function createCodeMirror() {
    if (mirror == null) {
        mirror = CodeMirror.fromTextArea(document.getElementById("sourceArea"), {
            lineNumbers: true,
            mode: "text/x-java",
            matchBrackets: true,
            readOnly: true
        });
    }
    mirror.refresh();
}