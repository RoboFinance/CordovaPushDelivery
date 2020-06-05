#### Description

A plugin that overrides the ```onMessageReceived()``` method of the `phonegap-plugin-push` package.
It introduces a possibility to detect if notifications are enabled and push is received in foreground / background and sends a fallback **POST** request.

**Only Android support right now.**
#### How to install
```
cordova plugin add "phonegap-plugin-push"
cordova plugin add "@robo-finance/cordova-push-delivery"
```

### How to use

1. In the global configuration file `config.xml`, and add the <variable /> tag inside plugin directive with the attribute name = DELIVERY_URL_KEY and the value attribute with its own value:
##### Example: DELIVERY_URL_KEY = delivery_url
```xml
<variable name="DELIVERY_URL_KEY" value="delivery_url"/>
```
2. Kindly append value DELIVERY_URL_KEY (e.g. `delivery_url`) to firebase `data` on backend:
 
##### Example of data structure

```json
{
	"data" : {
		"delivery_url": "https://localhost/set-read/886d27ee-f6fd-11e9-832d-362b9e155667"
	}
}
```

After push received and processed by `phonegap-plugin-push` in foreground/background, plugin will make **POST** request to `http://localhost/set-read/886d27ee-f6fd-11e9-832d-362b9e155667` automatically.
So, it doesn't need to write any Javascript code.

It is possible to detect if plugin is installed. No available methods in the `CordovaPushDelivery` object right now.
```js
if (typeof window.CordovaPushDelivery !== 'undefined') {
    
}
```

### How it works
Because on Android must be only one `intent-filter` per action the plugin use  [intent-filter priority](https://developer.android.com/guide/topics/manifest/intent-filter-element#priority) to override `phonegap-plugin`'s FCMService.
After onMessageReceived received it call parent `super.onMessageReceived(message)`, `phonegap-plugin` processes the notification and our plugin do the following:
1. Checks that `DELIVERY_URL_KEY` (e.g. `delivery_url`) is present in firebase `data`.
2. Checks if notifications are not blocked by the user, because they cannot be read.
3. If debug mode it disables chrome's trust certificate feature (for example, to use in staging environment), affected only to current request.
4. Sends POST callback to `DELIVERY_URL_KEY` (e.g. `delivery_url`) with empty body. You should set any `notification_id` or something like that to `DELIVERY_URL_KEY` (e.g. `delivery_url`), for example, `https://localhost/set-read/886d27ee-f6fd-11e9-832d-362b9e155667`