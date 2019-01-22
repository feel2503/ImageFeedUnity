using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class UnityPluginc : MonoBehaviour
{
    private AndroidJavaObject activityContext = null;
    private AndroidJavaClass javaClass = null;
    private AndroidJavaObject javaClassInstance = null;

    private void Awake()
    {
        using (AndroidJavaClass activityClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer"))
        {
            activityContext = activityClass.GetStatic<AndroidJavaObject>("currentActivity");
        }

        using (javaClass = new AndroidJavaClass("com.feed.plugin.BridgeCls"))
        {
            if(javaClass != null)
            {
                javaClassInstance = javaClass.CallStatic<AndroidJavaObject>("instance");
                javaClassInstance.Call("setContext", activityContext);
            }
        }

    }

    public void CallShowToast()
    {
        using (javaClass = new AndroidJavaClass("com.feed.plugin.BridgeCls"))
        {
            if (javaClass != null)
            {
                javaClassInstance = javaClass.CallStatic<AndroidJavaObject>("instance");
                javaClassInstance.Call("startGalleryActivity", activityContext);
            }
        }

        /* -- *
        activityContext.Call("runOnUiThread", new AndroidJavaRunnable(() =>
        {
            //javaClassInstance.Call("showToast", "Hello World");
            javaClassInstance.Call("startGalleryActivity");
        }));
        /* -- */
    }

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
