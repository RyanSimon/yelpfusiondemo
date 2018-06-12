# Yelp Fusion API Demo

This sample app for Yelp Fusion allows the user to search for businesses.

The results are displayed on a grid showing an image of the business, and its name.

## Running the Demo

You'll need a device that runs Android Lollipop (API 21) and above.

In addition, you'll need to add the following entry to your `local.properties` gradle file.

```
YELP_API_KEY="YOUR KEY HERE"
```

## Known Issues

Due to time constraints, a few things were left out of this demo.
* Loading progress states when waiting for the server to respond
* Error messages in the UI (errors are sent to Logcat)
* Units tests for the ViewModel
* Empty state
* Support for user's location