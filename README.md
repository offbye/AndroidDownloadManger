#What is it？

This is the source code of  android download manager. I devleoped this code when I develop a online video app, this lib is mainly designed for large video files download.It 's performace was tested fully.Now I decided to opensource under Apache License in 2014.

<img src="http://img.my.csdn.net/uploads/201211/21/1353505828_7068.jpg" alt="" width="320" style="border: none;"/>
<img src="http://img.my.csdn.net/uploads/201211/21/1353505882_8971.jpg" alt="" width="320" style="border: none;"/>


I think it will help you when you want develop a app such as app market which need download manager feature. It support below features:

##1.User friendly API provided
Download task management api and UI Support add, start, pause,cancel or delete a download task by API or use download task manager UI. The UI can display each tasks progress and status.

##2.Notification bar information supported
Notification bar download progress and speed display, download finished alarm. When click a downloading Notification will go to the downloading task list. When a download task finished, it will pop in Notification bar with a ring alarm.

##3.Large file download support. 
This download manager is designed for downloading flv video files, it can download files more than 200M and is full tested. it also support continue transferring from breakpoint.

##4.Support download item icon/image store in asset,sdcard,http url.
You can find examples in DownloadTestActivity.java

##5.Easy intergration
It can be published as a zip file.
You only need to copy resource files, jar file to you project,then add sevaral line code to the Androidmanifest.xml, then you can have a full feature download manager. Document and demo also provide to help you.

##6.Customize friendly
You can change the UI by edit the layouts and icons as you wish. You can change the download task click behavior by extend our DownloadListActivity.

You can clone the download manager from github. It will be always updated here.

##Donate
If you find this code is helpful,please donate me!

<form action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_top">
<input type="hidden" name="cmd" value="_s-xclick">
<input type="hidden" name="hosted_button_id" value="RRK4E6Y2RHCNJ">
<input type="image" src="https://www.paypalobjects.com/zh_XC/i/btn/btn_paynowCC_LG.gif" border="0" name="submit" alt="PayPal——最安全便捷的在线支付方式！">
<img alt="" border="0" src="https://www.paypalobjects.com/zh_XC/i/scr/pixel.gif" width="1" height="1">
</form>

##Contact

Any questions by contact email offbye@gmail.com.



