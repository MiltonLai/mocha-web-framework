<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
  <title>测试页面<#if title??> | ${title}</#if></title>
</head>
<body>
  <h1>${title!""}</h1>
  <p>Date: <#if date??>${date?string('yyyy.MM.dd HH:mm:ss')}</#if> Time: <#if time??>${time?string('yyyy.MM.dd HH:mm:ss')}</#if></p>
  <p>${content!""}</p>
  <p>${description!""}</p>
</body>