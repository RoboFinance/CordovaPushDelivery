#### Description

A plugin that overrides the ```onMessageReceived()``` method of the `phonegap-plugin-push` package.
It introduces a possibility to detect if push is received in foreground / background and sends a fallback GET request (`delivery_url` firebase message's param).

You can detect if plugin installed:
```js
if (typeof window.CordovaPushDelivery !== 'undefined') {
    
}
```
#### Example of data structure

```json
{
	"data" : {
    	"delivery_url": "http://localhost/set-read/886d27ee-f6fd-11e9-832d-362b9e155667"
    }
}
```

