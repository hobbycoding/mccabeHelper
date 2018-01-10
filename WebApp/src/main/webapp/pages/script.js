function summary_refresh() {
    var category = ["시스템", "업무분류", "전체Program", "테스트된 Program", "80% 미만 Program",
        "전체 Funtion", "테스트된 Function", "총라인수", "테스트라인수", "COVERAGE", "미테스트 Program"];
    var data = {"method" : "getOverView", "where" : document.getElementById("search").value};
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
    sendServer(data, callback);
}

function getCategoryList(date) {
    var data = {"method" : "getCategoryList", "where" : date};
    var callback = function () {
        var jsonArray, content = "";
        if (this.readyState == 4 && this.status == 200) {
            jsonArray = JSON.parse(this.responseText);
            for (var index in jsonArray) {
                var v = jsonArray[index]["업무분류"];
                content+= "<option value=\"" + jsonArray[index]["업무분류"] + "\">" + v + "</option>";
            }
            document.getElementById("categoryList").innerHTML = content;
        }
    };
    sendServer(data, callback);
    //    document.getElementById("browsers").innerHTML = "<option value=\"Chrome\"><option value=\"Opera\"><option value=\"Safari\">";
// <option value="1">hi</option>
//         <option value="2">there</option>
//         <option value="3">three</option>
//         <option value="4">four</option>
}

function getSubDetailView(category) {
    var data = {"method" : "getSubDetailView", "where" : document.getElementById('search').value, "category" : category};
    var category = ["프로그램 영문명", "프로그램 한글명", "Function 영문명", "Function 한글명", "서비스 ID",
        "업무명", "담당자", "유형", "전체라인수", "Covered 라인수", "Coverage(%)"];
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
    sendServer(data, callback);
}

function sendServer(data, callback) {
    var dbParam = JSON.stringify(data);
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = callback;
    xmlhttp.open("POST", "../rest/mccabe/process", true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlhttp.send(dbParam);
}