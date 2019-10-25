#### Description

A plugin that overrides the ```onMessageReceived()``` method of the `phonegap-plugin-push` package
and sends a post request for url (delivery_url) that comes from the server to send a reading notice.
#### Exmaple of data sctructure

```json
{
	...
	"data" : {
    	...
    	"delivery_url": "http://localhost/set-read/886d27ee-f6fd-11e9-832d-362b9e155667"
    }
}
```

