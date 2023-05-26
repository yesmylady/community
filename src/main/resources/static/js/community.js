// 用jquery中的方法以post的方式请求/comment并附带json信息，在标签中使用onclick调用即可
function post() {
    let questionId = $("#question_id").val();
    let content = $("#comment_content").val();
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId": questionId,
            "content": content,
            "type": 1
        }),
        success: function (response) {
            if (response.code == 200) {
                $("#comment_section").hide();
            } else {
                if (response.code == 2003) {  // 2003表示未登录，若点击confirm中的确定，则跳转到登录url并设本地closable为true供旧页面检查是否关闭自己
                    const isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=35bed685bab2aec9f622&redirect_uri=http://localhost:8080/callback&scope=user&state=1")
                        window.localStorage.setItem("closable", true);
                    }
                } else {
                    alert(response.message);
                }
            }
            console.log(response)
        },
        dataType: "json"
    })
    console.log(questionId);
    console.log(content);
}