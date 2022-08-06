$(function () {

    //动态设置高度
    let wH = $(window).height() - 80;
    $(".phoneApp").css("height", wH - 40);
    $(".phoneScript").css("height", wH);
    $(".phoneState").css("height", wH - 60);


    //遍历手机应用
    eachPhoneApp();
    var dataArr;
    var mPackName;

    function eachPhoneApp() {
        layer.load();
        $.ajax({
            type: "get",
            dataType: 'json', //服务器返回json格式数据
            timeout: 30000, //超时时间设置为30秒
            contentType: "application/json;charset=UTF-8",
            url: "/app/appList",
            success: function (data) {
                dataArr = data.data;
                result("");
                layer.closeAll();
            },
            //请求失败，包含具体的错误信息
            error: function (e) {
                layer.closeAll();
            }
        });
    }

    function result(appName) {
        $(".phoneApp").empty();
        dataArr.forEach(function (bean, index) {
            if (appName !== "") {
                if (bean.label.indexOf(appName) !== -1) {
                    let nodeDiv = "<div class='appLabel' data-pack='" + bean.package_name + "' data-uid='" + bean.uid + "'>" +
                        bean.label +
                        "</div>";
                    $(".phoneApp").append(nodeDiv);
                }
            } else {
                let nodeDiv = "<div class='appLabel' data-pack='" + bean.package_name + "' data-uid='" + bean.uid + "'>" +
                    bean.label +
                    "</div>";
                $(".phoneApp").append(nodeDiv);
            }

        });

        //遍历左侧的按钮，并追加点击事件
        $('.appLabel').each(function (position) {
            $(this).on('click', function () {
                $(this).css({"color": "#222222", "background-color": "#e1e1e1"});
                $(this).siblings().css({"color": "#e1e1e1", "background-color": "#222222"});
                //执行点击事件
                mPackName = $(this).attr("data-pack");
                clickScript($(this).attr("data-uid"));
            })
        });
    }

    $(".appSearch").click(function () {
        //搜索
        searchContent();

    });

    function searchContent() {
        let appName = $(".appName").val();
        if (appName == null || appName === "") {
            layer.msg('请输入要搜索得内容');
            return;
        }
        result(appName);
    }

    $(".appName").bind("input propertychange ", function () {
        if ($(this).val() === "") {
            result("");
        }
    });

    //点击左侧的应用
    var scriptUid;

    function clickScript(uid) {
        appJson = [];
        $(".scriptList").empty();
        $(".scriptText").val("");
        $(".scriptName").val("");
        $(".saveApp").text("创建");
        $(".deleteApp").css("display", "none");
        $(".startApp").css("display", "none");

        scriptUid = uid;
        //根据uid查找对应的脚本信息
        $.ajax({
            type: "get",
            dataType: 'json', //服务器返回json格式数据
            timeout: 30000, //超时时间设置为30秒
            contentType: "application/json;charset=UTF-8",
            url: "/app/appScript?appKey=" + scriptUid,
            success: function (data) {
                let json = JSON.parse(data.data);
                if (json.length > 0) {
                    json.forEach(function (bean, index) {
                        let appInfo = {};
                        //进行创建
                        appInfo.scriptName = bean.scriptName;
                        appInfo.scriptText = bean.scriptText;
                        appJson.push(appInfo);
                    });
                    eachScriptList();
                }

            },
            //请求失败，包含具体的错误信息
            error: function (e) {

            }
        });

    }

    var appJson = [];
    var appPosition;
    $(".saveApp").click(function () {
        //保存脚本
        let scriptText = $(".scriptText").val();
        var scriptName = $(".scriptName").val();

        if ($(this).text() === "创建") {
            if (scriptText === "" || scriptText == null) {
                layer.msg("请输入执行程序");
                return;
            }
            if (scriptName === "" || scriptName == null) {
                layer.msg("请输入程序名字");
                return;
            }

            if (scriptName.length > 20) {
                scriptName = scriptName.substring(0, 20);
            }

            let appInfo = {};
            //进行创建
            appInfo.scriptName = scriptName;
            appInfo.scriptText = scriptText;
            appJson.push(appInfo);

            saveHttp(-1);

        } else {
            //保存
            if (scriptText === "" || scriptText == null) {
                layer.msg("请输入执行程序");
                return;
            }
            if (scriptName === "" || scriptName == null) {
                layer.msg("请输入程序名字");
                return;
            }
            if (scriptName.length > 20) {
                scriptName = scriptName.substring(0, 20);
            }
            let bean = appJson[appJson.length - 1 - appPosition];
            bean.scriptName = scriptName;
            bean.scriptText = scriptText;

            scriptInfoNode.text(scriptName);


            saveHttp(0);

        }


    });

    function saveHttp(type) {

        if (scriptUid == null || scriptUid === "") {
            layer.msg("请选择应用后再创建程序");
            return;
        }
        let info = JSON.stringify(appJson);
        //保存的信息
        $.post("/app/save", {
            appScript: info,
            appKey: scriptUid
        }, function (data, status) {
            if (type === -1) {
                eachScriptList();
                $(".scriptText").val("");
                $(".scriptName").val("");
            } else if (type === 0) {
                layer.msg("更改成功");
            }
            if (type === 1) {
                //删除之后，还原状态，也就是，隐藏删除和运行按钮，让保存改为创建
                $(".saveApp").text("创建");
                $(".deleteApp").css("display", "none");
                $(".startApp").css("display", "none");
            }
        });
    }


    //遍历脚本信息
    var scriptInfoNode;

    function eachScriptList() {
        $(".scriptList").empty();
        appJson.forEach(function (bean, position) {
            let nodeItem = "<div class='scriptItem'>" +
                bean.scriptName +
                "</div>";
            $(".scriptList").prepend(nodeItem);
        });

        $('.scriptItem').each(function (position) {
            $(this).on('click', function () {
                scriptInfoNode = $(this);
                $(this).css({"color": "#222222", "background-color": "#e1e1e1"});
                $(this).siblings().css({"color": "#e1e1e1", "background-color": "#222222"});
                //点击上边的脚本信息
                appPosition = position;
                let bean = appJson[appJson.length - 1 - position];
                $(".scriptText").val(bean.scriptText);
                $(".scriptName").val(bean.scriptName);

                //改变为保存
                $(".saveApp").text("更改");
                $(".deleteApp").css("display", "inline");
                $(".startApp").css("display", "inline");
            })
        });
    }

    //监听脚本内容变化
    $(".scriptText").bind("input propertychange ", function () {
        let scriptName = $(".scriptName").val();
        if ($(this).val() === "" && scriptName === "") {
            $(".scriptList").empty();
            $(".saveApp").text("创建");
            $(".deleteApp").css("display", "none");
            $(".startApp").css("display", "none");
        }
    });

    //监听脚本名字变化
    $(".scriptName").bind("input propertychange ", function () {
        let scriptText = $(".scriptText").val();
        if ($(this).val() === "" && scriptText === "") {
            $(".saveApp").text("创建");
            $(".deleteApp").css("display", "none");
            $(".startApp").css("display", "none");
        }
    });

    $(".deleteApp").click(function () {
        //删除脚本信息
        let endPosition = appJson.length - 1 - appPosition;
        let app = appJson[endPosition];
        appJson.remove(app);
        //重新保存
        eachScriptList();
        $(".scriptText").val("");
        $(".scriptName").val("");
        saveHttp(1);

    });

    //获取元素在数组的下标
    Array.prototype.indexOf = function (val) {
        for (var i = 0; i < this.length; i++) {
            if (this[i] == val) {
                return i;
            }
        }
        return -1;
    };

//根据数组的下标，删除该下标的元素
    Array.prototype.remove = function (val) {
        var index = this.indexOf(val);
        if (index > -1) {
            this.splice(index, 1);
        }
    };

    var isStartScript = false;
    var doubleClick = true;//防止多次点击
    //点击脚本运行
    $(".startApp").click(function () {
        intervalString = "";
        toastMessage=""
        if (doubleClick) {
            doubleClick = false

            if (!isStartScript) {
                $(".phoneState").empty();
                let scriptText = $(".scriptText").val();
                if (scriptText === "" || scriptText == null) {
                    layer.msg("请输入执行程序");
                    return;
                }
                $(".phoneState").append("<div class='messageItem' style='color: #ffffff'>程序开始执行…</div>");
                //运行脚本信息
                $.post("/app/startApp", {
                    startApp: "true",
                    appScript: scriptText,
                    appPack: mPackName
                }, function (data, status) {
                    doubleClick = true;
                    isStartScript = true;
                    $(".startApp").text("终止");
                });
            } else {
                //终止程序
                $(".startApp").text("运行");
                isStartScript = true;
                doubleClick = true;

            }
        }


    });

    $('.appName').on('keypress', function (e) {
        if (e.keyCode === 13) {
            searchContent();
        }
    })

    //定时请求
    intervalMessage();
    var intervalString = "";

    function intervalMessage() {
        setInterval(function () {
            if (isStartScript) {
                $.get("/app/getScriptMessage", function (data, status) {
                    let d = data.data;
                    var color = "#e1e1e1";
                    if (d.indexOf("开始") !== -1 || d.indexOf("完毕") !== -1|| d.indexOf("结束") !== -1) {
                        color = "#e1e1e1";
                    } else if (d.indexOf("未找到") !== -1) {
                        color = "#d43c3c";
                    } else {
                        color = "#1cdb15";
                    }

                    if(d.indexOf("错误") !== -1){
                        color = "#d43c3c";
                    }

                    if(d.indexOf("循环") !== -1){
                        color="#d4da17";
                    }
                    let msg = d.split("==");
                    let msg1 = msg[0];
                    let msg2 = msg[1];

                    console.log(intervalString + "=========");
                    if (intervalString.indexOf(msg1) === -1) {
                       console.log(msg1 + "=========");
                        //不包含
                        intervalString = intervalString + msg1;

                        if (msg2 !== "" && msg2 != null) {
                            let dNode = "<div class='messageItem' style='color: " + color + "'>" + msg2 + "</div>";
                            $(".phoneState").append(dNode);
                        }
                    }
                    //执行完毕
                    if (d.indexOf("错误") !== -1||d.indexOf("完毕") !== -1 || d.indexOf("未找到") !== -1) {

                         if(toastSign!=null&&toastSign!=""&&toastMessage.indexOf(toastSign)==-1){
                            toastMessage=toastMessage+toastSign;
                            let dNode = "<div class='messageItem' style='color: #1c8cdc'>" + toastSign + "</div>";
                            $(".phoneState").append(dNode);
                         }

                        $(".startApp").text("运行");
                        isStartScript = false;
                    }
                });
            }
        }, 300);
    }

    //保存文档
    $(".scriptResultSave").click(function () {

    });

    $(".docModule").click(function () {
        //保存文档
        layer.open({
            type: 2,
            title: '程序编辑快速入门',
            shadeClose: true,
            shade: [0.5, '#000000'],
            maxmin: false,
            area: ['500px', '530px'],
            content: 'doc.html'
        });
    });


    intervalToast();
    var toastMessage="";
    var toastSign="";
    function intervalToast() {
            setInterval(function () {
                $.get("/app/getToastMessage", function (data, status) {
                                      let d = data.data;
                                      toastSign=d;
                                    });
            }, 200);
        }
})