$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	//获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	//发送异步请求(POST)
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title,"content":content},
		//加上回调函数处理返回的结果
		function (data) {
			//返回的是字符串，要将其转换成对象
			data = $.parseJSON(data);
			//在提示框中显示返回的消息
			$("#hintBody").text(data.msg);
			//显示提示框
			$("#hintModal").modal("show");
			//两秒后自动隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//刷新页面
				if(data.code == 0){//为0则成功，因为之前设置了成功为0，失败为403,在DiscussPostController中
					window.location.reload();//重新加载
				}
			}, 2000);
		}
	);


}