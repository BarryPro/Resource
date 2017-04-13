<html>
<#include "common/head.ftl" encoding="utf-8" parse="true"/>
<#include "common/particle.ftl" encoding="utf-8" parse="true"/>
<script>
    $(function () {
        $("#My_btn").click(function () {
            alert("pl");
        })
    })

</script>
<body>
<button class="btn btn-primary" id="My_btn">按钮</button>
<div class="alert alert-success">成功！很好地完成了提交。</div>
<div class="alert alert-info">信息！请注意这个信息。</div>
<div class="alert alert-warning">警告！请不要提交。</div>
<div class="alert alert-danger">错误！请进行一些更改。</div>
</body>
</html>
