/**
 * Created by belong on 2017/4/16.
 */
$(function () {
    $("#class_btn").click(function () {
        console.log("开始获取资源")
        $.ajax({
            url: "type",
            type: "post",
            dataType: "text",
            success:function(data){
                $("#msg").empty();
                $("#msg").append(data);
                console.log("结束获取资源");
            }
        })
    })
    $("#subUrl_btn").click(function () {
        console.log("开始获取urls")
        $.ajax({
            url: "subUrls",
            type: "post",
            dataType: "text",
            success:function(data){
                $("#msg").empty();
                console.log("结束获取urls");
            }
        })
    })
})
