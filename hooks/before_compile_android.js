const path = require("path"),
    fs = require("fs"),
    xmlDom = require("xmldom"),
    cordovaRoot = path.resolve(),
    DELIVERY_SERVICE_PATH = "./platforms/android/app/src/main/java/com/android/plugins/DeliveryService.java";

module.exports = function () {
    let DOMParser = xmlDom.DOMParser,
        cordovaPushDeliveryConfig = path.resolve(cordovaRoot, 'plugins/cordova-push-delivery/plugin.xml'),
        xmlContent = fs.readFileSync(cordovaPushDeliveryConfig, 'utf8'),
        document = new DOMParser().parseFromString(xmlContent, 'text/xml'),
        preferences = document.getElementsByTagName('preference'),
        deliveryUrlKey = null;

    for (let i = 0; i < preferences.length; i++) {
        let nameAttribute = preferences[i].getAttribute("name");
        if (nameAttribute === "DELIVERY_URL_KEY") {
            deliveryUrlKey = preferences[i].getAttribute("value");
        }
    }

    let deliveryService = fs.readFileSync(DELIVERY_SERVICE_PATH).toString();
    deliveryService = deliveryService
        .replace(new RegExp('\key = "(.+?)\"'), 'key = "' + deliveryUrlKey + '"')
        .replace(new RegExp('\has `(.+?)\`'), 'has `' + deliveryUrlKey + '`');
    fs.writeFileSync(DELIVERY_SERVICE_PATH, deliveryService, 'utf8');
};
