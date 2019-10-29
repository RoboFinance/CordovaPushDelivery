#### Description

A plugin that overrides the ```onMessageReceived()``` method of the `phonegap-plugin-push` package.
It introduces a possibility to detect if push is received in foreground / background and sends a fallback GET request.

**Only Android support right now.**
#### How to install
```
cordova plugin add "@robo-finance/cordova-push-delivery"
```

### How to use
Kindly append `delivery_url` parameter to firebase `data`
 
#### Example of data structure

```json
{
	"data" : {
    	"delivery_url": "http://localhost/set-read/886d27ee-f6fd-11e9-832d-362b9e155667"
    }
}
```

After push received and processed by `phonegap-plugin-push` in foreground/background, plugin will make GET request to `http://localhost/set-read/886d27ee-f6fd-11e9-832d-362b9e155667` automatically.
So, it doesn't need to write any Javascript code.

Is is possible to detect if plugin is installed. No available methods in the object right now.
```js
if (typeof window.CordovaPushDelivery !== 'undefined') {
    
}
```