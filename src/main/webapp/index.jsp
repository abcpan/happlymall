<%@page language="java" contentType="text/html; charset=utf-8"%>
<html>
<head>
    <title>SpringMVC 文件上传</title>
</head>
<body>
<form action="/manage/product/img_upload" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_img">
    <input type="submit" value="上传文件">
</form>
<form action="/manage/product/richtext_upload" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_richtext">
    <input type="submit" value="上传富文本">
</form>
</body>
</html>