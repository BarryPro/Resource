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


});

function setting_fun() {
    $("#setting-area").hide();
    $("#setting-btn").click(function () {
        // 显示div
        $("#setting-area").show(1000);
    });
    $("#setting-area").mouseleave(function(){
        $("#setting-area").hide(1000);
    })

}
