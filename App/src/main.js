var templates = []

$(document).ready(function () {
    /*$(".btn-solution").click(() => {
        $(event.target).next().toggle();
    });*/

    $("code[data-src]").each((i, obj) => {
        $.get("./code/" + obj.dataset.src, function(data){
            $(obj).text(data)
            hljs.highlightBlock(obj);
        });
    });

    $.get("./templates.kml", function(data){
        let codeMode = false;
        let code = "";
        let fragmentName = "";

        let lines = data.split("\n");
        lines.push("=#=");

        for(let line of lines){
            if(codeMode){
                if(!line.startsWith('+')&&!line.startsWith("=#=")){
                    code += line + "\n";
                    continue
                }else{
                    var arr = fragmentName.split("==");

                    templates.push({code:code, fileName: arr[0], name: arr[1]});

                    code = "";
                    codeMode = false;
                }
            }
            if(line.startsWith("=#=")){
                continue;
            }
            if(line.startsWith('+')){
                fragmentName = line.substring(1, line.length);
                codeMode = true;
                continue;
            }
        }

        loadTasks();
    }, "text");
});

let tasks = [];

function loadTasks() {
    let codeLinesGlobal = 0;
    let taskNumber = 1;
    $(".task[data-src]").each((iter, obj) => {
        let task = {
            obj: obj,
            num: taskNumber++
        };

        tasks.push(task);
    });

    for(let task of tasks){
        $.get("./tasks/" + task.obj.dataset.src, function(data){

            let html = "";
            let codeLines = 0;
            let taskName;

            let level = 0;
            let codeMode = false;
            let tableMode = false;
            let tableRow = 0;
            let solutionStarted = false;
            let code = "";
            let fragmentName = "";

            let lines = data.split("\n");
            lines.push("=#=");

            for(let line of lines){
                if(codeMode){
                    if(!line.startsWith('=')&&!line.startsWith('==')
                        &&!line.startsWith('+')&&!line.startsWith("=#=")){
                        code += line + "\n";
                        codeLines++;
                        continue
                    }else{
                        html += `<div class="fragment">`;
                        html += `<div class="caption">${fragmentName}</div>`;
                        html += `<code class="${guessLang(fragmentName)}">${code.escape()}</code>`;
                        html += `</div>`;

                        if(!line.startsWith('+')){
                            solutionStarted = false;
                            html += `</div>`;
                        }

                        code = "";
                        codeMode = false;
                    }
                }
                if(tableMode){
                    if(!line.startsWith('=')&&!line.startsWith('==')
                        &&!line.startsWith('+')&&!line.startsWith("=#=")){

                        let rows = line.split("|");
                        html += "<tr>";

                        for(let row of rows){
                            if(tableRow === 1){
                                html += `<th>${row}</th>`
                            }else {
                                html += `<td>${row}</td>`
                            }
                        }
                        html += "</tr>";
                        tableRow++;

                        continue;
                    }else{
                        tableMode = false;

                        html += "</table>";
                    }
                }
                if(line.startsWith("=#=")){
                    continue;
                }

                if(line.startsWith('@@')){
                    tableMode = true;
                    tableRow = 1;
                    let content = line.substring(2, line.length);
                    html += `<table class="table table-sm" style="width: inherit">`;
                }

                if(line.startsWith('###')){
                    if(line.includes("=")) {
                        let meta = line.substring(3, line.length).split("=");
                        let key = meta[0].trim();
                        let value = meta[1].trim();

                        if (key === "name") {
                            taskName = `<h1 id="task${task.num}">Task ${task.num} - ${value}</h1>`
                            html += taskName;
                            task.name = value;
                        }
                        if(key === "time"){
                            task.time = value;
                        }
                        if(key === "full"){
                            task.full = value;
                        }
                    }
                }
                if(line.startsWith('==')){
                    if(level === 2){
                        html += `</li>`;
                    }
                    if(level === 1){
                        level = 2;
                        html += `<ul>`;
                    }
                    let content = line.substring(2, line.length);
                    content = postProcessHeader(content);

                    html += `<li>${content}`;
                    continue;
                }
                if(line.startsWith('=')){//level 1
                    if(level === 1){
                        //html += `</ol><ol>`;
                        html += `</li>`;
                    }
                    if(level === 0){
                        level = 1;
                        html += `<ol>`;
                    }
                    if(level === 2){
                        level = 1;
                        html += `</ul>`;
                    }
                    let content = line.substring(1, line.length);
                    content = postProcessHeader(content);
                    html += `<li>${content}`;
                    continue;
                }
                if(line.startsWith('+')){
                    fragmentName = line.substring(1, line.length);
                    if(fragmentName.startsWith("<<")){
                        let targetTemplate;
                        for(let template of templates){
                            if(fragmentName.substring(2, fragmentName.length) === template.name){
                                targetTemplate = template;
                                fragmentName = template.fileName;
                            }
                        }
                        code = targetTemplate.code;
                        codeMode = true;
                        if(!solutionStarted) {
                            solutionStarted = true;
                            html += `<button class="btn-solution">Open</button>`;
                            html += `<div class="solution">`;
                        }
                        continue;
                    }
                    codeMode = true;
                    if(!solutionStarted){
                        solutionStarted = true;
                        html += `<button class="btn-solution">Open</button>`;
                        html += `<div class="solution">`;
                    }

                    continue;
                }
            }

            task.lines = codeLines;

            console.log("---" + taskName + " --> " + codeLines);
            codeLinesGlobal += codeLines;

            html += `<div><a href="./ref/${task.full}">Download the project (${task.full})</a></div>`;

            console.log(html)

            $(task.obj).html(html);

            $("code").each((i, obj) => {
                hljs.highlightBlock(obj);
            });
            $(task.obj).find(".btn-solution").click(() => {
                $(event.target).next().toggle();
            });
        }, "text");
    }

    $(document).ajaxStop(function () {
        "use strict";

        let tocHtml = "<div>Table of content<div>";

        for(let task of tasks){
            let timeHtml = "";
            if(task.time !== undefined){
                timeHtml = " (" + task.time + " min)";
            }
            tocHtml += `<div><a href="#task${task.num}">${task.num}) ${task.name}</a> ${timeHtml} </div>`;
        }

        $(".tableOfContent").html(tocHtml);
    });

    setTimeout(function(){
        "use strict";
        console.log("All lines = " + codeLinesGlobal)
    }, 1000);
}

let allCodeIsVisible = false;
function showAllCode() {
    if(!allCodeIsVisible){
        $(".btn-solution").next().show();
    }else{
        $(".btn-solution").next().hide();
    }

    allCodeIsVisible = !allCodeIsVisible;
}

function postProcessHeader(content) {
    content = content.escape();

    let start = true;
    for(let i = 0; i< 10; i++) {
        if(start) {
            content = content.replace("##", "<span class='term'>");
        }else{
            content = content.replace("##", "</span>");
        }
        start = !start;
    }

    start = true;
    for(let i = 0; i< 10; i++) {
        if(start) {
            content = content.replace("@@", "<span class='cls'>");
        }else{
            content = content.replace("@@", "</span>");
        }
        start = !start;
    }

    start = true;
    for(let i = 0; i< 10; i++) {
        if(start) {
            content = content.replace("~~", "<span class='file'>");
        }else{
            content = content.replace("~~", "</span>");
        }
        start = !start;
    }

    start = true;
    for(let i = 0; i< 10; i++) {
        if(start) {
            content = content.replace("!!", "<span class='method'>");
        }else{
            content = content.replace("!!", "</span>");
        }
        start = !start;
    }

    return content;
}

function guessLang(fileName) {
    fileName = fileName.trim();

    if(fileName.endsWith(".java")){
        return "java";
    }
    if(fileName.endsWith(".xml")){
        return "xml";
    }
    if(fileName.endsWith(".sql")){
        return "sql";
    }

    return "xml";
}

String.prototype.escape = function() {
    var tagsToReplace = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;'
    };
    return this.replace(/[&<>]/g, function(tag) {
        return tagsToReplace[tag] || tag;
    });
};
if (!String.prototype.endsWith) {
    String.prototype.endsWith = function(search, this_len) {
        if (this_len === undefined || this_len > this.length) {
            this_len = this.length;
        }
        return this.substring(this_len - search.length, this_len) === search;
    };
}