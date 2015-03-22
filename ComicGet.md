A program to download comics.

# Introduction #

In some websites that offer comics, users need to click "Next" to view the next page. This is kinda messy consider that people, like me, don't like to be interrupted by network latency and stuff. So I wrote a program that targets these types of comic websites in which the comic are displayed in a sequential order. My program can download the whole chapter of comic. User using this program can just set up the program to start the download, go brew a cup of coffee, come back and relax to find that the whole new chapter of comic is already set for you to enjoy.

# Details #

The program is written in java. Currently I have uploaded a compiled jar file so users doesn't need to compile from source. The instruction assumes users of this program knows a bit about running java, be able to find out some detail about how the website has organized its comic pages, and bit of HTML and URL knowledges.

1) User is expected to write out an "input.txt" file to configure the program. Example of the file:

---

htmlUrl=http://comic2.kukudm.com/comiclist/277/15353/1.htm

jpgUrl=![http://210.51.23.71/kuku6comic6/200911/%E5%8D%81%E4%B8%80%E6%9C%88%E4%BA%8C%E5%8D%81%E4%B8%83%E6%97%A5/299/comic.kukudm.com_00103D.jpg](http://210.51.23.71/kuku6comic6/200911/%E5%8D%81%E4%B8%80%E6%9C%88%E4%BA%8C%E5%8D%81%E4%B8%83%E6%97%A5/299/comic.kukudm.com_00103D.jpg)

pages=22

dir=out

urlJpgKey=`_`


---

htmlUrl specifies the first page of the comic of your choice

jpgUrl specifies the first page of the comic (embedded in the HTML content in htmlURL). This is sometimes tricky to get, because of 2 reasons:

a) In the comic website that I uses, they uses javascript to obscure the full URL of the comic jpg file.

b) Part of the URL path uses Chinese encoding.

The solution to the above two problems is to find out the full, encoded URL of the jpg file. In Firefox, I can drag the jpg into the browser's web address box, it will open up the jpg file. Then, right click on the jpg file and choose "Properties". In there you will find the full, encoded URL of the jpg file. Use this path for jpgUrl.

pages specifies the total number of pages for this chapter of comic

dir specifies the directory (relative to your running directory) where you want the comic to be stored

urlJpgKey specifies the keyword separator where the jpg filename changes on the right. For the jpgUrl example above, it is the "`_`" that separates the jpg name on the right that gets changed for the next jpg file.

2) So given the above instruction, my layout of the directory before I start the downloading will be as follows:

pckutil-2.0.jar

input.txt

out/

3) Invoke the program like this:

> java -cp pckutil-2.0.jar com.pck.util.ComicGet

4) Watch the comic being downloaded to the destination directory.

That's pretty much it. Enjoy!