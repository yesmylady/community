// 用jquery中的方法以post的方式请求/comment并附带json信息，在标签中使用onclick调用即可

/**
 * 提交问题评论，即一级评论
 */
function levelOneReviewPost() {
    let questionId = $("#question_id").val();
    let content = $("#comment_content").val();
    commentPost(questionId, content, 1);
}

/**
 * 二级评论
 */
function levelTwoReviewPost(element) {
    var commentId = element.getAttribute("data-id");
    var content = $("#input-"+commentId).val();
    commentPost(commentId, content, 2);
}


function commentPost(parentId, content, type) {
    if (!content) {
        alert("内容为空！")
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId": parentId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {  // 提交成功，隐藏编辑框，这里的#comment_section是html标签中的id属性
                // $("#comment_section").hide();
                window.location.reload();
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
    console.log(parentId);
    console.log(content);
}



function levelTwoCommentGet(element, parentId) {
    var id = element.getAttribute("data-id");
    var comments = $("#comment-"+id);
    comments.toggleClass("in");  // collapse加上in属性就会把折叠的内容展开，再次点击去掉in

    $.ajax({
        type: "GET",
        url: "/comment/"+parentId,
        contentType: "application/json",

        success: function (response) {
            if (response.code == 200) {  // 提交成功，获取到此评论的所有二级评论，用js把这些二级评论展示
                console.log(response)
                
                var div = document.getElementById("target-"+parentId);
                // 设置元素的内容
                for (var i=0; i < response.data.length; i++) {
                    var listItem = document.createElement("li");
                    var text = document.createTextNode(response.data.at(i).user.name + " - " + response.data.at(i).content);
                    listItem.appendChild(text);
                    console.log("hhhh" + listItem.innerHTML);
                    div.appendChild(listItem);
                    console.log(div.innerHTML);
                }
            }
        },
        dataType: "json"
    })
    console.log(parentId);
}


/**
 * 展开某个一级评论的二级评论
 */
function collapseComments(element) {
    var id = element.getAttribute("data-id");
    var comments = $("#comment-"+id);
    comments.toggleClass("in");  // collapse加上in属性就会把折叠的内容展开，再次点击去掉in

}


function selectTag(value) {
    var previous = $("#tag").val();

    if (previous.indexOf(value) == -1) {  // value不存在于tag框中的时候才添加！
        if (previous) {
            $("#tag").val(previous + ',' + value);
        } else {
            $("#tag").val(value);
        }
    }

}