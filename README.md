### Oriient SDK Test App

This app demonstrates that the Oriient SDK removes all
files in the app package's files directory when
positioning is started.

## To reproduce

1. Set the proper credentials in the credentials.properties file
2. Update the `BUILDING_ID` in `MainActivity` to a valid id.
2. Run the app.
3. Upload any file via the Android Studio Device Explorer to
   /data/data/com.example.oriientsdktestapp/files
4. Tap "Login", then "Start positioning"
5. Refresh the Device explorer and navigate to the above directory
6. Observe that the file that was uploaded is now gone.

This is an issue for apps that rely on files in this directory,
specifically this was found because it was removing files
that Firebase Remote Config added. This caused subsequent
queries of the Remote Config cache on the device to return
empty values.

