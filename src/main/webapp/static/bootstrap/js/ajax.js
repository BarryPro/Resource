/**
 * Created by belong on 2017/4/16.
 */
$(function () {
    $("#class_btn").click(function () {
        console.log("开始获取资源");
        $.ajax({
            url: "type",
            type: "post",
            dataType: "text",
            success: function (data) {
                $("#msg").empty();
                console.log("结束获取资源");
            }
        })
    });
    $("#subUrl_btn").click(function () {
        console.log("开始获取urls");
        $.ajax({
            url: "subUrls",
            type: "post",
            dataType: "text",
            success: function (data) {
                $("#msg").empty();
                console.log("结束获取urls");
            }
        })
    });

    // 设置区域
    setting_fun();
    for(var i = 0;i<400;i++){
        $("#txt").append("world!hello world!hello world!hello world!hello world!hello world!\n");
    }




});

function setting_fun() {
    $("#video-window").hide();
    $("#info-area").hide();
    $("#setting-area").hide();
    $("#setting-btn").click(function () {
        // 显示div
        $("#setting-area").show(1000);
    });
    $("#setting-area").mouseleave(function(){
        $("#setting-area").hide(1000);
    });

    $("#message-area").click(function(){
        $("#video-window").slideDown(400)
    });

    $("#email-area").click(function(){
        $("#info-area").slideDown(600)
    });

    $("#info-area").mouseleave(function(){
        $("#info-area").hide(1000);
    });
    $("#close-video").click(function(){
        $("#video-window").slideUp(400);
        $("#video-player").trigger("pause")
    })

}
