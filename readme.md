

Vidada
=======

Vidada is a Tag based movie and clip manager which supports nearly all video formats and codecs. There are several 3th party libraries and tools in use, such as jVLC (for playback) and ffmpeg (for gathering thumbnails).

Vidada is a video clip manager which supports custom Tags and supports nearly every video format. You can have thousands of movies indexed, and all can be in the thumbnail view - Vidada has an custom written Thumbnail viewer (JThumbViewer) which has an intelligent rendering to support unlimited items.

Also, Vidada does not dictate you any folder structure. Instead, the file contents are indexed (file hashes), which means that you can even move your video clips around in your media library, or you can rename them. Hell, you can keep working with your files they way you like.

Vidada also supports a feature called DirectPlay - if you select a video clip in the Thumb viewer, it will start playing. Move your mouse over the movie, and it will automatically seek to the relative position. Preview a video was never easier. :)


Source Code is freely avaiable at http://github.com/IsNull/Vidada

Copyright P.Buettiker  2012-2013







### MAC OS X Hack for Retina Support

If you are using the original Apple JRE 1.6, you can activate HDPI rendering of Java Applications by adding some keys to the applications plist. (Oracle didnt manage to release a Retina-Display ready rendering pipeline until now)

In the OS X App plist, add following keys:

<key>NSHighResolutionCapable</key> 
<true/>
<key>NSPrincipalClass</key>
<string>NSApplication</string>